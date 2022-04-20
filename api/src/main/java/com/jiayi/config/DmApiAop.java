package com.jiayi.config;

import com.jiayi.common.components.RedisLock;
import com.jiayi.common.exception.BusinessException;
import com.jiayi.common.model.CallResult;
import com.jiayi.model.annotations.BatchExecuteAnnotation;
import com.jiayi.model.annotations.RedisLockAnnotation;
import com.jiayi.model.annotations.RedisLockInterface;
import com.jiayi.model.enums.BatchExecuteTypeEnum;
import com.jiayi.model.enums.CommonResultEnum;
import com.jiayi.model.support.ResponseUtil;
import com.jiayi.model.support.request.BaseSignRequest;
import com.jiayi.model.support.response.SysResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author cjw
 * @date 2020-10-07
 */
@Aspect
@Component
@Order(2)
@Slf4j
public class DmApiAop {
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RedisLock redisLock;

    /**
     * 无效返回类型定义
     */
    private final String voidType = "void";

    /**
     * 对切面方法增加时间统计
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.jiayi.model.annotations.TimeCountAnnotation)")
    public Object addTimeLog(ProceedingJoinPoint point) throws Throwable {
        boolean bInsertMDC = StringUtils.isEmpty(MDC.get(LogbackFilter.UNIQUE_ID)) && LogbackFilter.insertMDC();
        Object result = null;
        String errorMsg = null;
        LocalDateTime startTime = LocalDateTime.now();
        log.info("timelog start {} class:{} method:{} param:{}", startTime,
                point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), point.getArgs());
        try {
            result = point.proceed();
            return result;
        } catch (Exception ex) {
            errorMsg = ex.getMessage();
            throw ex;
        } finally {
            log.info("timelog end useTime:{} millisecond class:{} method:{} result:{} exceptionMessage:{}",
                    Duration.between(startTime, LocalDateTime.now()).toMillis(),
                    point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), result, errorMsg);
            if (bInsertMDC) {
                MDC.remove(LogbackFilter.UNIQUE_ID);
            }
        }
    }

    /**
     * 对切面方法的参数对象进行分批处理,
     * 使用条件：
     * <p>
     * 被切面方法第一个参数为被分批处理数据， 目前只支持List
     * 被切面方法返回值支持：void  CallResult CallResult<List>
     * <p>
     * 场景：1，按大量snId查询sn数据
     * 2，批量保存sn数据
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.jiayi.model.annotations.BatchExecuteAnnotation)")
    public Object batchExecute(ProceedingJoinPoint point) throws Throwable {
        boolean bInsertMDC = StringUtils.isEmpty(MDC.get(LogbackFilter.UNIQUE_ID)) && LogbackFilter.insertMDC();
        Object result = null;
        CallResult callResult = null;
        try {
            // 获得注解
            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            BatchExecuteAnnotation batchExecuteAnnotation = methodSignature.getMethod().getAnnotation(BatchExecuteAnnotation.class);
            Object[] args = point.getArgs();
            Class resultType = methodSignature.getReturnType();
            // 判断返回值类型，验证当前切面是否支持: 只支持返回类型为void / CallResult / Collection<>
            if (!voidType.equals(resultType.getName())
                    && !Void.class.isAssignableFrom(resultType) && !SysResponse.class.isAssignableFrom(resultType)
                    && !List.class.isAssignableFrom(resultType)
            ) {
                throw new BusinessException("分批处理切面 batchExecute 不支持当前方法：返回值类型");
            }
            // 分批处理数量
            final int batchNum = batchExecuteAnnotation.batchNum();
            // 按参数分批执行、分批查询处理分支，BatchExecuteTypeEnum.EXECUTE BatchExecuteTypeEnum.QUERY
            if (BatchExecuteTypeEnum.EXECUTE.equals(batchExecuteAnnotation.value())
                    || BatchExecuteTypeEnum.QUERY.equals(batchExecuteAnnotation.value())) {
                // 判断第一个参数类型，必须为集合类型
                Object batchArg = args[0];
                if (!(batchArg instanceof List)) {
                    throw new BusinessException("分批处理切面 batchExecute(EXECUTE,QUERY) 不支持当前方法：第一个传参类型必须为List");
                }
                // 定义新的参数对象组
                Object[] newArgs = new Object[args.length];
                for (int i = 0; i < args.length; i++) {
                    newArgs[i] = i > 0 ? args[i] : null;
                }

                // 分批处理第一个参数集合
                List batchArgList = (List) batchArg;
                // 分批处理sn入库
                int total = batchArgList.size();
                log.info("分批处理切面: 对象：{} 方法：{} 总数 {}", point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), total);
                // 定义需要返回的数据对象，如果没有则直接返回CallResult
                List<Object> resultList = Lists.newArrayList();
                for (int batch = 0; batch < Math.ceil((double) total / batchNum); batch++) {
                    int toIndex = (batch + 1) * batchNum;
                    if (toIndex > total) {
                        toIndex = total;
                    }
                    log.info("分批处理切面，批次:{}, 截止索引：{}", batch, toIndex);
                    List batchList = batchArgList.subList(batch * batchNum, toIndex);
                    newArgs[0] = batchList;
                    result = point.proceed(newArgs);
                    log.info("批次：{}, 返回值：{}", batch, result);
                    // void 返回值，直接执行下一批次
                    if (Objects.isNull(result)) {
                        continue;
                    }
                    // 转换CallResult
                    if (result instanceof List) {
                        resultList.addAll((List) result);
                    } else {
                        callResult = (CallResult) result;
                        if (BatchExecuteTypeEnum.EXECUTE.equals(batchExecuteAnnotation.value()) && !callResult.isSuccess()) {
                            return result;
                        }
                        if (BatchExecuteTypeEnum.QUERY.equals(batchExecuteAnnotation.value()) &&
                                !Objects.isNull(callResult.getResultObject()) && callResult.getResultObject() instanceof List) {
                            resultList.addAll((List) callResult.getResultObject());
                        }
                    }
                }
                if (CollectionUtils.isEmpty(resultList)) {
                    return result;
                }
                if (List.class.isAssignableFrom(resultType)) {
                    return resultList;
                } else {
                    return ResponseUtil.success(resultList);
                }
                // 按PageHelper 分页查询(生效在切面方法中的第一个sql查询)
            } else if (BatchExecuteTypeEnum.RESULT_QUERY.equals(batchExecuteAnnotation.value())) {
                // 接收返回值 返回数据应该为List
                List<Object> resultList = Lists.newArrayList();
                int batchNo = 1;
                while (true) {
                    List<Object> oneResultList = null;
                    PageHelper.startPage(batchNo, batchNum, false);
                    result = point.proceed(args);
                    if (result instanceof List) {
                        oneResultList = (List) result;
                        resultList.addAll(oneResultList);
                        log.info("分批查询处理：批次号：{}, 返回数据: {}", batchNo, Objects.isNull(oneResultList) ? 0 : oneResultList.size());
                    } else if (result instanceof SysResponse) {
                        callResult = (CallResult) result;
                        if (!callResult.isSuccess()) {
                            break;
                        }
                        if (Objects.isNull(callResult.getResultObject()) || !(callResult.getResultObject() instanceof List)) {
                            throw new BusinessException("分批处理切面 batchExecute(RESULT_QUERY) 不支持此方法: 返回值类型");
                        }
                        oneResultList = (List) callResult.getResultObject();
                        log.info("分批查询处理：批次号：{}, 返回数据: {}", batchNo, Objects.isNull(oneResultList) ? 0 : oneResultList.size());
                        resultList.addAll(oneResultList);
                    } else {
                        throw new BusinessException("分批处理切面 batchExecute(RESULT_QUERY) 不支持此方法: 返回值类型");
                    }
                    if (CollectionUtils.isEmpty(oneResultList) || batchNum != oneResultList.size()) {
                        break;
                    }
                    batchNo++;
                }
                if (List.class.isAssignableFrom(resultType)) {
                    return resultList;
                } else if (!CollectionUtils.isEmpty(resultList)) {
                    return CallResult.success(resultList);
                } else {
                    return result;
                }
            } else {
                throw new BusinessException("分批处理切面 batchExecute 不支持当前方法：注释value无效");
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (bInsertMDC) {
                MDC.remove(LogbackFilter.UNIQUE_ID);
            }
        }
    }


    /**
     * redis锁注解处理
     * @return
     */
    @Around("@annotation(com.jiayi.model.annotations.RedisLockAnnotation)")
    public Object redisLockHandle(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        RedisLockAnnotation redisLockAnnotation = methodSignature.getMethod().getAnnotation(RedisLockAnnotation.class);

        String lockKey = methodSignature.getMethod().getName();
        Object[] args = point.getArgs();
        if (!Objects.isNull(args) && args.length > 0) {
            Object firstArg = point.getArgs()[0];
            if (firstArg instanceof RedisLockInterface) {
                lockKey += "_"+((RedisLockInterface) point.getArgs()[0]).redisLockKey();
            } else if(firstArg instanceof Integer || firstArg instanceof Long || firstArg instanceof String || firstArg instanceof Byte){
                lockKey += "_" + firstArg.toString();
            }
        }
        int timeout = redisLockAnnotation.timeout();
        log.info("操作redis加锁, lockKey:{}", lockKey);
        if (!redisLock.lock(lockKey, timeout)) {
            return ResponseUtil.failure("操作冲突，请"+timeout+"秒后再进行操作!");
        }
        Object result = point.proceed();
        log.info("操作redis解锁, lockKey:{}", lockKey);
        redisLock.unlock(lockKey);
        return result;
    }

}

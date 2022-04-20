package com.jiayi.common.util;

import com.jiayi.common.enums.GenerateCodeEnum;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 生成编码, 总共23位
 * 2 位字符前缀，大写字母，区分编码类型，参考GenerateCodeEnum枚举值
 * 2 位年
 * 2 位月
 * 2 位日
 * 2 位时
 * 2 位分
 * 3 位毫秒
 * 8 位组合字符串（包含ID和补充随机数）
 * Created by katrina on 2019/04/28.
 *
 * @author Katrina
 * @date 2020-10-28
 */
@Slf4j
public class GenerateCodeUtil {

    private static final int KEEP_ID_LENGTH = 4;
    private static final String DEFAULT_CURRENT_DATE_FORMAT = "yyMMddHHmmSSS";
    private static final String DEFAULT_SHORT_DATE_FORMAT = "yyMM";

    public static String generateCode(GenerateCodeEnum codeEnum, Long id) {
        return generateCode(codeEnum, id, true);
    }

    public static String generateCode(GenerateCodeEnum codeEnum, Integer id) {
        return generateCode(codeEnum, id.longValue(), true);
    }

    public static String generateCode(GenerateCodeEnum codeEnum, Integer id, Boolean isShort) {
        return generateCode(codeEnum, id.longValue(), isShort);
    }

    /**
     * 生成编码code
     *
     * @param codeEnum
     * @param id
     * @return
     * @author Katrina
     * @date 2020-10-28
     */
    public static String generateCode(GenerateCodeEnum codeEnum, Long id, boolean isShort) {
        // id转为字符串
        String idStr = id.toString();
        if (idStr.isEmpty()) {
            return "";
        }
        try {
            StringBuilder strBuilder = new StringBuilder();
            // 2位字母前缀
            strBuilder.append(codeEnum.getPrefix());
            LocalDateTime currentDateTime = LocalDateTime.now();
            if (isShort) {
                // 4位年月
                strBuilder.append(currentDateTime.format(DateTimeFormatter.ofPattern(DEFAULT_SHORT_DATE_FORMAT)));
            } else {
                // 10位年月日时分 + 3位毫秒
                strBuilder.append(currentDateTime.format(DateTimeFormatter.ofPattern(DEFAULT_CURRENT_DATE_FORMAT)));
            }

            int keepIdLength = KEEP_ID_LENGTH;
            if (KEEP_ID_LENGTH < idStr.length()) {
                strBuilder.append(idStr.substring(0, KEEP_ID_LENGTH));
            } else {
                // id转为字符串
                strBuilder.append(idStr);
                keepIdLength = KEEP_ID_LENGTH * 2 - idStr.length();
            }
            // 8位减去id值所占位数，生成随机数字
            String randomStr = RandomUtil.randomNumber(keepIdLength - idStr.length());
            strBuilder.append(randomStr);
            log.info("GenerateCodeUtil->generateCode()生成CODE成功：GenerateCodeEnum={}，id={}，code={}", codeEnum.toString(), id, strBuilder);
            return strBuilder.toString();

        } catch (Exception e) {
            log.error("GenerateCodeUtil->generateCode()生成CODE异常：GenerateCodeEnum={}，id={}", codeEnum.toString(), id);
            return "";
        }
    }
}

package com.jiayi.common.util;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 数据脱敏工具类
 *
 * @author laiyilong
 * @date 2021-01-16
 */
@Slf4j
public class MaskDataUtil {
    
    public static String MaskData(String unMaskData){
        return MaskData(unMaskData,0,0);
    }

    public static String MaskData(String unMaskData,MaskTypeEnum maskTypeEnum){
        return MaskData(unMaskData,maskTypeEnum.prefixNum,maskTypeEnum.suffixNum);
    }

    public static String MaskDataWithPrefix(String unMaskData,Integer prefixNum){
        return MaskData(unMaskData,prefixNum,0);
    }

    public static String MaskDataWithSuffix(String unMaskData,Integer suffixNum){
        return MaskData(unMaskData,0,suffixNum);
    }


    /**
     * 数据脱敏
     *
     * @return
     * @author laiyilong
     * @date 2021-01-16
     */
    public static String MaskData(String unMaskData,Integer prefixNum,Integer suffixNum) {
        try {
            if(Strings.isNullOrEmpty(unMaskData)){
                return "";
            }
            if(Objects.isNull(prefixNum)){
                prefixNum = 0;
            }else if(prefixNum<0){
                prefixNum = 0;
            }
            if(Objects.isNull(suffixNum)){
                suffixNum = 0;
            }else if(suffixNum<0){
                prefixNum = 0;
            }

            if(prefixNum+suffixNum>=unMaskData.length()){
                if(prefixNum>=unMaskData.length()){
                    return unMaskData;
                }
                //当前缀加后缀数量大于或等于数据长度时，优先保障前缀长度足够的前提下缩减后缀长度，并保证最小mark长度为1
                suffixNum = unMaskData.length()-prefixNum-1;
            }

            String prefix = unMaskData.substring(0,prefixNum);
            String suffix = unMaskData.substring(unMaskData.length()-suffixNum);
            StringBuilder stringBuilder = new StringBuilder(prefix);
            for(int i = 0;i<unMaskData.length()-prefixNum-suffixNum;i++){
                stringBuilder.append("*");
            }
            stringBuilder.append(suffix);
            return stringBuilder.toString();
        } catch (Exception e) {
            log.error("数据脱敏出错");
            return "";
        }
    }

    public static enum MaskTypeEnum {
        /**
         * 脱敏数据类型
         */
        MOBILE("手机号", 3,4),
        NAME("姓名", 1,1),
        ;
        private String desc;
        private Integer prefixNum;
        private Integer suffixNum;

        MaskTypeEnum(String desc, Integer prefixNum,Integer suffixNum) {
            this.desc = desc;
            this.prefixNum = prefixNum;
            this.suffixNum = suffixNum;
        }
    }

    public static void main(String[] args) {
        System.out.println(MaskData("小明", MaskTypeEnum.NAME));
    }
}

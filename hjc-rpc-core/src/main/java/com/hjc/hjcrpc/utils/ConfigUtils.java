package com.hjc.hjcrpc.utils;

/**
 * 配置工具类
 * File Description: ConfigUtils
 * Author: hou-jch
 * Date: 2024/5/15
 */

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 加载配置对象
 */
public class ConfigUtils {
    public static <T> T loadConfig(Class<T> clazz,String prefix) {
        return loadConfig(clazz,prefix,"");
    }

    /**
     * 加载配置对象，支持区分环境
     */
    public static <T> T loadConfig(Class<T> clazz,String prefix,String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if(StrUtil.isNotBlank(environment)){
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(clazz,prefix);

    }
}

package com.hjc.hjcrpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * File Description: ServiceRegisterInfo
 * Author: hou-jch
 * Date: 2024/7/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRegisterInfo <T>{

    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 实现类
     */
    private Class<? extends T> implClass;
}

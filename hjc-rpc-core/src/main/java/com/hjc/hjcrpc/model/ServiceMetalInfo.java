package com.hjc.hjcrpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * 注册信息
 * File Description: ServiceSetalInfo
 * Author: hou-jch
 * Date: 2024/5/26
 */
@Data
public class ServiceMetalInfo {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */

    private String serviceVersion = "1.0";

    /**
     * 服务地址
     */

    private String serviceAddress;

    /**
     * 服务分组
     */

    private String serviceGroup = "default";

    /**
     * 服务host
     */
    private String serviceHost;

    /**
     * 服务post
     */
    private Integer servicePort;
    /**
     * 获取服务键名  文件夹节点
     */

    public String getServiceKey(){
        //后续可扩展服务分组
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取服务注册节点键名    把服务地址作为节点getServiceKey()
     */

    public String getServiceNodeKey(){
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    /**
     * 获取完整服务地址
     */

    public String getServiceAddress(){
        if(!StrUtil.contains(serviceHost,"http")){
            return String.format("http://%s:%s",serviceHost,servicePort);
        }
        return String.format("%s:%s",serviceHost,servicePort);
    }
}

package com.hjc.hjcrpc.config;

import lombok.Data;

/**
 * Rpc框架配置
 * File Description: RpcConfig
 * Author: hou-jch
 * Date: 2024/5/15
 */
@Data
public  class RpcConfig {
    /**
     * 名称
     */
    private String name = "hou-rpc";
    /**
     * 版本号
     */

    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */

    private Integer serverPort = 8080;


    /**
     * 模拟调用
     */

    private Boolean mock = false;

}

package com.hjc.hjcrpc;

/**
 * File Description: RpcApplication
 * Author: hou-jch
 * Date: 2024/5/15
 */

import com.hjc.hjcrpc.config.RpcConfig;
import com.hjc.hjcrpc.constant.RpcConstant;
import com.hjc.hjcrpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架应用
 * 相当于holder 存放了项目中全局用到的变量，双检锁单例模式实现
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;
    /**
     * 框架初始化，支持传入自定义配置
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init,config = {}", newRpcConfig.toString());
    }

    /**
     * 初始化
     */
    public static void init(){
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e){
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置
     * 双重检查锁定机制只在第一次需要初始化单例对象时执行锁定操作，后续的访问均无需锁定，直接返回已初始化的单例对象，从而大幅减少了同步的开销。
     */
    public static RpcConfig getRpcConfig(){
        //大多数情况下，instance 已经被初始化，直接返回 instance，避免了进入同步块的开销。这是为什么第一次检查非常重要 - 它是在大多数情况下避免同步。
        if(rpcConfig == null){
            //如果第一次检查发现 rpcConfig 是 null，只有此时才会进入同步块。
            //进入同步块意味着当前线程将获取这个类的类级别锁，其他任何试图进入此方法的线程将被阻塞，直到线程从同步块中退出。
            synchronized (RpcApplication.class){
                //在进入同步块后，第二次检查 rpcConfig 是否依然为 null。
                //这个重复检查是必要的，因为在第一次检查与进入同步块的这段时间内，可能有其他线程已经初始化了 rpcConfig。第二次检查确保在同步块内真正安全地创建对象。
                if(rpcConfig == null){
                    init();
                }
            }
        }
        return rpcConfig;
    }
}

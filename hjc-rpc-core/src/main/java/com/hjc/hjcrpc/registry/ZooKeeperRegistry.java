package com.hjc.hjcrpc.registry;

import com.hjc.hjcrpc.config.RegistryConfig;
import com.hjc.hjcrpc.model.ServiceMetalInfo;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

/**
 * zookeeper注册中心
 * File Description: ZooKeeperRegistry
 * Author: hou-jch
 * Date: 2024/6/4
 */
@Slf4j
public class ZooKeeperRegistry implements Registry{

    private CuratorFramework client;

    private ServiceDiscovery<ServiceMetalInfo> serviceDiscovery;
    /**
     * 本机注册的key节点集合（维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();
    /**
     * 正在监听key的集合
     */
    private final Set<String> watchingKsySet =  new ConcurrentHashSet<>();

    /**
     * 根节点
     */
    private static final String ZK_ROOT_PATH = "/rpc/zk";

    @Override
    public void  init(RegistryConfig registryConfig) {
        //构建client实例
        client = CuratorFrameworkFactory.builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();
        //构建serviceDiscovery实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetalInfo.class).client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetalInfo.class))
                .build();
        try {
            //启动
            client.start();
            serviceDiscovery.start();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public void register(ServiceMetalInfo serviceMetalInfo) throws Exception {

        //注册到zk
        serviceDiscovery.registerService(buildServiceInstance(serviceMetalInfo));
        //注册到本地缓存
        String registerKey = ZK_ROOT_PATH + "/" + serviceMetalInfo.getServiceNodeKey();
        //注册到本地注册节点key集合
        localRegisterNodeKeySet.add(registerKey);
    }

    private ServiceInstance<ServiceMetalInfo> buildServiceInstance(ServiceMetalInfo serviceMetalInfo) {
      String serviceAddress = serviceMetalInfo.getServiceHost() + ":" + serviceMetalInfo.getServicePort();
      try {
         return ServiceInstance.<ServiceMetalInfo>builder().id(serviceAddress)
                  .name(serviceMetalInfo.getServiceKey())
                  .address(serviceAddress)
                  .payload(serviceMetalInfo)
                  .build();
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
    }

    @Override
    public void unRegister(ServiceMetalInfo serviceMetalInfo) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetalInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String registerKey = ZK_ROOT_PATH + "/" + serviceMetalInfo.getServiceNodeKey();
        localRegisterNodeKeySet.remove(registerKey);

    }

    @Override
    public List<ServiceMetalInfo> serviceDiscovery(String serviceKey) {
        List<ServiceMetalInfo> cacheServiceMetalInfoList = registryServiceCache.readCache(serviceKey);
        if(cacheServiceMetalInfoList != null && cacheServiceMetalInfoList.size() > 0){
            return cacheServiceMetalInfoList;
        }
        try {
            //查询服务信息
            Collection<ServiceInstance<ServiceMetalInfo>> serviceInstancesList = serviceDiscovery.queryForInstances(serviceKey);
            //解析服务信息
            List<ServiceMetalInfo> serviceMetalInfoList = serviceInstancesList.stream().map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());
            //写入缓存
            registryServiceCache.writeCache(serviceKey,serviceMetalInfoList);
            return serviceMetalInfoList;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void destroy() {
        log.info("当前节点下线");
        for (String key  : localRegisterNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(key);
            }catch (Exception e){
                throw new RuntimeException("节点下线失败");
            }
            }
        if(client != null){
            client.close();
        }


    }

    @Override
    public void heartBeat() {
        //不需要心跳检测，建立临时节点，如果服务器故障，临时节点直接丢失
    }

    @Override
    public void watch(String serviceNodeKey) {
        String watchKey = ZK_ROOT_PATH + "/" + serviceNodeKey;
        boolean newWatch = watchingKsySet.add(watchKey);
        if(newWatch){
            CuratorCache curatorCache = CuratorCache.build(client, watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(
                    CuratorCacheListener.builder()
                            .forDeletes(childData -> registryServiceCache.clearCache(serviceNodeKey))
                            .forChanges((oldNode,node)->registryServiceCache.clearCache(serviceNodeKey))
                            .build()
            );

        }


    }
}


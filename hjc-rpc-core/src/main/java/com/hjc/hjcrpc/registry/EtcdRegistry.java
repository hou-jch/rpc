package com.hjc.hjcrpc.registry;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.hjc.hjcrpc.config.RegistryConfig;
import com.hjc.hjcrpc.model.ServiceMetalInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.vertx.core.impl.ConcurrentHashSet;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;



/**
 * File Description: EtcdRegistry
 * Author: hou-jch
 * Date: 2024/5/26
 */
public class EtcdRegistry implements Registry  {

    private  Client client;
    private  KV kvClient;
    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    /**
     * 正在监听key的集合
     */
    private final Set<String> watchingKsySet =  new ConcurrentHashSet<>();
    /**
     * 本机注册的节点key集合（用于维护续期）
     * @param registryConfig
     */
    private final  Set<String> localRegistryNodeKeySet = new HashSet<>();
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void register(ServiceMetalInfo serviceMetalInfo) throws ExecutionException, InterruptedException {
        /**
         * 创建Lease客户端
         */
        Lease leaseClient = client.getLeaseClient();
        //创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();
        //设置要存储的键值对
            String registerKey = ETCD_ROOT_PATH + serviceMetalInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetalInfo), StandardCharsets.UTF_8);
        //关联键值对和租约，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key,value,putOption).get();
        //将注册的节点key添加到本机维护的节点key集合中(为了续约)
        localRegistryNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetalInfo serviceMetalInfo) {
        String registry = ETCD_ROOT_PATH + serviceMetalInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetalInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
        //服务注销是移除缓存
        localRegistryNodeKeySet.remove(registry);
    }

    @Override
    public List<ServiceMetalInfo> serviceDiscovery(String serviceKey) {
        List<ServiceMetalInfo> serviceMetalInfoList = registryServiceCache.readCache(serviceKey);
        if(serviceMetalInfoList != null && serviceMetalInfoList.size() > 0){
            return serviceMetalInfoList;
        }
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            //前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> kvs = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption).get().getKvs();

            //解析服务信息
            Map<String, List<ServiceMetalInfo>> serviceMetalInfoMap = kvs.stream().collect(Collectors.toMap(kv -> {
              String key = kv.getKey().toString(StandardCharsets.UTF_8);
              //监听key的变化
              watch(key);
              return  key;
            }, kv -> Arrays.asList(JSONUtil.toBean(kv.getValue().toString(StandardCharsets.UTF_8), ServiceMetalInfo.class))));

//            List<ServiceMetalInfo> serviceMetalInfos = kvs.stream().map(kv -> {
//                String key = kv.getKey().toString(StandardCharsets.UTF_8);
//                //监听key的变化
//                watch(key);
//                String value = kv.getValue().toString(StandardCharsets.UTF_8);
//                return JSONUtil.toBean(value, ServiceMetalInfo.class);
//            }).collect(Collectors.toList());
            if(serviceMetalInfoMap == null || serviceMetalInfoMap.size() > 1){
                throw new RuntimeException("获取的服务不存在或者服务出错，请检查注册中心");
            }
            List<ServiceMetalInfo> serviceMetalInfoLists = null;
            for (String serviceKeys :serviceMetalInfoMap.keySet()) {
                serviceMetalInfoLists = serviceMetalInfoMap.get(serviceKeys);
                registryServiceCache.writeCache(serviceKeys,serviceMetalInfoLists);
            }

//            registryServiceCache.writeCache(serviceMetalInfoMap);
            return serviceMetalInfoLists;


        }catch (Exception e){
            throw new RuntimeException("获取服务列表失败",e);
        }

    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        for (String key :localRegistryNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key,StandardCharsets.UTF_8)).get();
            }catch (Exception e){
                throw new RuntimeException(key + "节点下线失败");
            }
        }
        if(kvClient != null){
            kvClient.close();
        }
        if(client != null){
            client.close();;
        }
    }

    @Override
    public void heartBeat() {
        //10秒续签一次
        System.out.println("开始续约");
        CronUtil.schedule("*/5 * * * * *", new Task() {
            @Override
            public void execute() {
                //遍历所有节点
                for (String key :localRegistryNodeKeySet) {
                    try {
                      List<KeyValue> keyValues =  kvClient.get(ByteSequence.from(key,StandardCharsets.UTF_8)).get().getKvs();

                        //keyValues为空说明节点已经过期（需要重启才能重新注册）
                        if(CollUtil.isEmpty(keyValues)){
                            continue;
                        }
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetalInfo serviceMetalInfo = JSONUtil.toBean(value, ServiceMetalInfo.class);
                        register(serviceMetalInfo);

                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败" ,e);
                    }
                }

            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();

    }

    @Override
    /**
     * 监听（消费端）
     */
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean newWatch = watchingKsySet.add(serviceNodeKey);
        if(newWatch)
            watchClient.watch(ByteSequence.from(serviceNodeKey,StandardCharsets.UTF_8), response->{
                    for (WatchEvent watchEvent:response.getEvents()){
                        switch (watchEvent.getEventType()){
                            case DELETE:
                            //清理注册服务缓存
                                registryServiceCache.clearCache(serviceNodeKey);
                                break;

                            case PUT:
                            default:
                                break;
                        }
                    }
            });
        }


//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        Client client = Client.builder().endpoints("http://192.168.10.107:2379").build();
//        KV kvClient = client.getKVClient();
//        ByteSequence key = ByteSequence.from("test-key".getBytes());
//        ByteSequence value = ByteSequence.from("test-value".getBytes());
//        kvClient.put(key,value).get();
//
////        CompletableFuture<GetResponse> getResponseCompletableFuture = kvClient.get(key);
////        GetResponse getResponse = getResponseCompletableFuture.get();
////        kvClient.delete(key);
//
//
//    }
}

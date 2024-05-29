package com.hjc.hjcrpc.registry;


import cn.hutool.json.JSONUtil;
import com.hjc.hjcrpc.config.RegistryConfig;
import com.hjc.hjcrpc.model.ServiceMetalInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * File Description: EtcdRegistry
 * Author: hou-jch
 * Date: 2024/5/26
 */
public class EtcdRegistry implements Registry  {

    private static Client client;
    private static KV kvClient;
    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
    }

    @Override
    public void register(ServiceMetalInfo serviceMetalInfo) throws ExecutionException, InterruptedException {
        /**
         * 创建Lease客户端
         */
        Lease leaseClient = client.getLeaseClient();
        //创建一个30秒的租约
        long leaseId = leaseClient.grant(300000000).get().getID();
        //设置要存储的键值对
            String registerKey = ETCD_ROOT_PATH + serviceMetalInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetalInfo), StandardCharsets.UTF_8);
        //关联键值对和租约，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key,value,putOption).get();

    }

    @Override
    public void unRegister(ServiceMetalInfo serviceMetalInfo) {
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetalInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
    }

    @Override
    public List<ServiceMetalInfo> serviceDiscovery(String serviceKey) {
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            //前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> kvs = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption).get().getKvs();
            //解析服务信息
            return kvs.stream().map(kv->{
                String value = kv.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value,ServiceMetalInfo.class);
            }).collect(Collectors.toList());


        }catch (Exception e){
            throw new RuntimeException("获取服务列表失败",e);
        }

    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        if(kvClient != null){
            kvClient.close();
        }
        if(client != null){
            client.close();;
        }
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

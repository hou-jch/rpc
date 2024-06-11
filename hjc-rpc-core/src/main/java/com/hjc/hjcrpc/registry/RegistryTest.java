package com.hjc.hjcrpc.registry;

import com.hjc.hjcrpc.config.RegistryConfig;
import com.hjc.hjcrpc.model.ServiceMetalInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * File Description: RegistryTest
 * Author: hou-jch
 * Date: 2024/5/28
 */

public class RegistryTest {

    final Registry registry = new EtcdRegistry();



    @Before
    public void init(){
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://192.168.10.107:2379");
        registry.init(registryConfig);
    }


    @Test
    public void register() throws Exception {
        ServiceMetalInfo serviceMetalInfo = new ServiceMetalInfo();

        ServiceMetalInfo serviceMetaInfo = new ServiceMetalInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetalInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1235);
        registry.register(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetalInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);


    }

    @Test
    public  void sreviceDiscovery(){
        ServiceMetalInfo serviceMetalInfo = new ServiceMetalInfo();
        serviceMetalInfo.setServiceName("myService");
        serviceMetalInfo.setServiceVersion("1.0");
        String serviceNodeKey = serviceMetalInfo.getServiceKey();
        List<ServiceMetalInfo> serviceMetalInfoList = registry.serviceDiscovery(serviceNodeKey);
        System.err.println(serviceMetalInfoList.size());
        Assert.assertNotNull(serviceMetalInfoList);




    }

    @Test
    public void heartBeat() throws Exception {
        register();
        Thread.sleep(60*1000L);
    }

}

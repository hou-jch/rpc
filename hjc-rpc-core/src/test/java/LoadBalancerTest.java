import com.hjc.hjcrpc.loadbalancer.LoadBalancer;
import com.hjc.hjcrpc.loadbalancer.RoundRobinLoadBalancer;
import com.hjc.hjcrpc.model.ServiceMetalInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File Description: test
 * Author: hou-jch
 * Date: 2024/7/10
 */
public class LoadBalancerTest {
    final LoadBalancer loadBalancer = new RoundRobinLoadBalancer();

    @Test
    public void select() {
// 请求参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", "apple");
// 服务列表
        ServiceMetalInfo serviceMetaInfol = new ServiceMetalInfo();
        serviceMetaInfol.setServiceName("myService");
        serviceMetaInfol.setServiceVersion("1.0");
        serviceMetaInfol.setServiceHost("localhost");
        serviceMetaInfol.setServicePort(1234);
        ServiceMetalInfo serviceMetaInfo2 = new ServiceMetalInfo();
        serviceMetaInfo2.setServiceName("myService");
        serviceMetaInfo2.setServiceVersion("1.0");
        serviceMetaInfo2.setServiceHost("yupi.icu");
        serviceMetaInfo2.setServicePort(80);
        List<ServiceMetalInfo> serviceMetaInfolist = Arrays.asList(serviceMetaInfol, serviceMetaInfo2);
//连续调用3次
        ServiceMetalInfo seryiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfolist);
        System.out.println(seryiceMetaInfo);
        Assert.assertNotNull(seryiceMetaInfo);
        seryiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfolist);
        System.out.println(seryiceMetaInfo);
        Assert.assertNotNull(seryiceMetaInfo);
        seryiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfolist);
        System.out.println(seryiceMetaInfo);
        Assert.assertNotNull(seryiceMetaInfo);
    }
}

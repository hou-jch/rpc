package com.hjc.examplespringbootconsumer;

import com.hjc.example.common.service.UserService;
import com.hjc.hjcrpc.springboot.starter.annotation.RpcReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 测试类不可以有 public  class前面和方法前面都不能有
 */
@SpringBootTest
class ExampleSpringbootConsumerApplicationTests {
    @Resource
    private ExampleServiceImpl  exampleService;

    @Test
    void contextLoads() {
        exampleService.test();
    }

}

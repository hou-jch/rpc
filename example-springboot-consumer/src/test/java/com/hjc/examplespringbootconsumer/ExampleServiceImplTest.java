package com.hjc.examplespringbootconsumer;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
public class ExampleServiceImplTest {

    @Resource
    private ExampleServiceImpl  exampleService;


  @Test
    public void test1() {
//        System.out.println(userService.getNumber());
        exampleService.test();

    }
}

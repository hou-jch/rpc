package com.hjc.examplespringbootconsumer;

import com.hjc.example.common.model.User;
import com.hjc.example.common.service.UserService;
import com.hjc.hjcrpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;


/**
 * File Description: ExampleServiceImpl
 * Author: hou-jch
 * Date: 2024/7/15
 */

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("yupi");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}

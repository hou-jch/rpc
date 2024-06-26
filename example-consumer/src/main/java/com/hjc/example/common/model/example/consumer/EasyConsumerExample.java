package com.hjc.example.common.model.example.consumer;

import com.hjc.example.common.model.User;
import com.hjc.example.common.service.UserService;
import com.hjc.hjcrpc.proxy.ServiceProxyFactory;
import lombok.val;

import static com.hjc.hjcrpc.proxy.ServiceProxyFactory.getProxy;

/**
 * 建议服务消费者示例
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        UserService userService = getProxy(UserService.class);
        User user = new User();
        user.setName("hjc");
        User newUser = userService.getUser(user);
        if(newUser != null){
            System.out.println(newUser.getName());
        }else{
            System.out.println("newUser is null");
        }

    }
}

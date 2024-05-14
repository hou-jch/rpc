package com.hjc.hjcrpc.server;

import com.hjc.example.common.model.User;
import com.hjc.example.common.service.UserService;

/**
 * 用户服务实现类
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名已经被我修改：" + user.getName() + "获取到");
        user.setName(user.getName() + "获取到了");
        return user;
    }
}

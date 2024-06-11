package com.hjc.example.common.model.example.consumer;

import com.hjc.example.common.model.Color;
import com.hjc.example.common.model.User;
import com.hjc.example.common.service.ColorService;
import com.hjc.example.common.service.UserService;
import com.hjc.hjcrpc.proxy.ServiceProxyFactory;
import com.hjc.hjcrpc.serializer.KryoSerializer;
import com.hjc.hjcrpc.spi.SpiLoader;
import lombok.val;

import java.awt.image.ColorModel;

import static com.hjc.hjcrpc.proxy.ServiceProxyFactory.getProxy;

/**
 * 建议服务消费者示例
 */
public class EasyConsumerExample {
    public static void main(String[] args) throws InterruptedException {

//        try {
//
////            KryoSerializer kryoSerializer = new KryoSerializer();
//            Class.forName("com.hjc.hjcrpc.serializer.KryoSerializer");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

      UserService userService = getProxy(UserService.class);
        UserService userService2 = getProxy(UserService.class);
      ColorService colorService = getProxy(ColorService.class);
        User user = new User();
        user.setName("hjc");
        System.out.println(userService.getNumber());
        User newUser = userService.getUser(user);
        Color color = new Color();
        color.setColor("红色");
        Color colorServiceColor = colorService.getColor(color);
        Thread.sleep(10000);
        if(colorServiceColor != null){
            System.out.println(colorServiceColor.getName());
        }
        if(newUser != null){
            System.out.println(newUser.getName());
        }else{
            System.out.println("newUser is null");
        }
//        short number = userService.getNumber();
//        System.out.println(number);


    }
}

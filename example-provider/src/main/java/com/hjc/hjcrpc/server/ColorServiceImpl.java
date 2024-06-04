package com.hjc.hjcrpc.server;

import com.hjc.example.common.model.Color;
import com.hjc.example.common.service.ColorService;

/**
 * File Description: ColorServiceImpl
 * Author: hou-jch
 * Date: 2024/6/3
 */
public class ColorServiceImpl implements ColorService {
    @Override
    public Color getColor(Color color) {
        System.out.println("这里是Color类哦");
        color.setName("张三阿萨");
        return color;
    }
}

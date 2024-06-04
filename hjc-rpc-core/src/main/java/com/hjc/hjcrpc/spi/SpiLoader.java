package com.hjc.hjcrpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.esotericsoftware.kryo.Kryo;
import com.hjc.hjcrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI 加载器（支持键值对映射）
 * File Description: SpiLoader
 * Author: hou-jch
 * Date: 2024/5/16
 */
@Slf4j
public class SpiLoader {
    /**
     * 储存已加载的类 接口名(类名带包名)-> 实现类
     * loaderMap中存的每一个key其实都是一个接口的类名带包名，value存的是这个接口所有的实现类,实现类对应的key和value是配置文件中的key和value
     */
    private static Map<String, Map<String,Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存（避免重复new） ，类路径-》对象实例
     */
    private static Map<String,Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统SPI目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义SPImulu
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final  String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};


    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);


    /**
     * 加载所有类型
     */
    public static void loadAll(){
        log.info("加载所有SPI");
        for (Class<?> aClass  :LOAD_CLASS_LIST) {
            load(aClass);
        }


    }

    /**
     * 获取某个接口的实例
     * @param clazz
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<?> clazz , String key){
        String clazzName = clazz.getName();
        Map<String, Class<?>> classMap = loaderMap.get(clazzName);
        if(classMap == null){
            throw new RuntimeException(String.format("spiLoader 未加载 %s", clazzName));
        }
        if(!classMap.containsKey(key)){
            throw new RuntimeException(String.format("spiLoader 的 %s 不存在key =  %s 的类型", clazzName, key));

        }
        //获取到要加载的类型
        Class<?> implClass = classMap.get(key);
        String implClassName = implClass.getName();
        if(!instanceCache.containsKey(implClassName)){

            try {
                //Class<?>不是一个真正的实例对象，而是描述类本身的元数据。
                //newInstance():实际触发调用 implClass 的无参数构造函数，返回的是 implClass 这个类的一个新实例。
                System.out.println(implClass.newInstance());
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
              String errorMsg = String.format("%s 类实例化失败",implClassName);
               throw new RuntimeException(errorMsg,e);
            }
        }

        return (T) instanceCache.get(implClassName);
    }

    /**
     * 这段代码的目的是加载指定类型的 SPI（Service Provider Interface）实现类，并将它们存储在一个 Map 中，需要在序列化工厂中初始化一次即可
     * 通俗描述：传入一个序列化接口.class  然后把这个接口的包名加类名作为key,value:通过读取指定的目录下的文件，文件名和key相同，文件内容是实现类全类名，然后把实现类存入map中
     * @param loadClass
     * @return
     */
    public static Map <String,Class<?>> load(Class<?> loadClass){


        log.info("加载类型为{} 的SPI",loadClass.getName());

        //扫描路径，用自定义的SPI优先级高于系统
        // 创建一个用于存放 SPI 实现类的 Map，键为 SPI 名称，值为对应的 Class<?> 对象    key和value是配置文件中的key和value
        Map<String,Class<?>> keyClassMap = new HashMap<>();
        // 遍历指定的扫描目录（SCAN_DIRS 是一个目录列表，通常用于指定要搜索 SPI 配置文件的路径）
        for (String scanDir : SCAN_DIRS) {
            //这里后面拼接了 loadClass.getName() 这就要求系统或自定义目录下建的文件名字必须和其对应类名相同才能找到文件
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            //读取每个资源文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null){
                        // 按等号分割每行内容，格式通常为 "key=className"
                        String[] split = line.split("=");
                        if(split.length > 1){
                            String key = split[0];
                            String className = split[1];

                            // 将从文件中读取的 key 和 className 对应的 Class 对象存放到 Map 中
                            keyClassMap.put(key,Class.forName(className));

                        }

                    }
                }catch (Exception e){
                    log.error("spi resource load error ",e);
                }
            }

        }
    loaderMap.put(loadClass.getName(),keyClassMap);
        return keyClassMap;

    }

    public static void main(String[] args) throws ClassNotFoundException {
        loadAll();


        Serializer serializer = getInstance(Serializer.class, "Jdk");
        System.out.println(serializer);
    }
}

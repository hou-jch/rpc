package com.hjc.hjcrpc.proxy;

import sun.reflect.Reflection;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;

/**
 * 服务代理工厂（用于创建代理对象）
 * File Description: ServiceProxyFactory
 * Author: hou-jch
 * Date: 2024/5/14
 */
public class ServiceProxyFactory {

    /**
     * static <T>中的<T>： 告诉编译器这是一个泛型方法,如果不加 <T>，编译器就不知道 T 是什么类型参数，会导致编译错误。
     * T getProxy(Class<T> serviceClass)：返回一个类型为 T 的对象，并接收一个 Class<T> 类型的参数，这个参数表示希望代理的接口类型。
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static  <T> T getProxy(Class<T> serviceClass){
        //Proxy.newProxyInstance：这是静态方法，用于创建代理实例。

        //serviceClass.getClassLoader()：第一个参数是类加载器，用于加载代理类。 serviceClass.getClassLoader() 获取了被代理接口的类加载器。

        //new Class<?>[]{serviceClass}：代理类要实现的接口列表。代理对象会实现这些接口中的方法，并将调用委托给 InvocationHandler。
        //这里指定了被代理类实现的接口。需要确保 serviceClass 是一个接口，而不是一个具体的类，因为 Proxy 仅能为接口创建代理。

        //new ServiceProxy()：调用处理器，必须实现 InvocationHandler 接口。InvocationHandler 接口定义了 invoke 方法，所有代理对象的方法调用都会转发到这个 invoke 方法。

        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{serviceClass}, new ServiceProxy());
    }
}


/**
 * 这里serviceClass  称为代理类
 * newProxyInstance 源码解释：
 *
public static Object newProxyInstance(ClassLoader loader,
                                      Class<?>[] interfaces,
                                      InvocationHandler h)
        throws IllegalArgumentException
                                                                                loader: 类加载器，用于定义代理类。
                                                                                interfaces: 代理对象实现的接口列表。
                                                                                h: 调用处理程序 (InvocationHandler) 实例，处理代理对象上的方法调用。
{
    Objects.requireNonNull(h);                                                  确保 InvocationHandler 实例不为空。

    final Class<?>[] intfs = interfaces.clone();                                创建接口数组的副本，避免外部修改数组内容
    final SecurityManager sm = System.getSecurityManager();                     安全管理器检查：
    if (sm != null) {
        checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
    }


    Class<?> cl = getProxyClass0(loader, intfs);通过 getProxyClass0      方法传入类加载器和接口数组  重新生成新的代理类，这是一个运行时生成的类，通常名字是系统生成的，例如 $Proxy0。这个新的代理类实现了原来的代理类的接口，并且还有一个构造方法：
                                                                                                                                                   public $Proxy0(InvocationHandler h) {
                                                                                                                                                   this.h = h;
                                                                                                                                                   }


    try {                           构造代理对象
        if (sm != null) {
            checkNewProxyPermission(Reflection.getCallerClass(), cl);
        }


 cons 表示代理类的构造函数：
 final Constructor<?> cons = cl.getConstructor(constructorParams);     按照本代码逻辑可以理解成 Constructor<?> cons = UserService.getConstructor(InvocationHandler.class);

final InvocationHandler ih = h;
        if (!Modifier.isPublic(cl.getModifiers())) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    cons.setAccessible(true);
                    return null;
                }
            });
        }

 return cons.newInstance(new Object[]{h}); 这个方法通过反射调用构造函数，创建一个新的代理类的实例，并将 InvocationHandler 实例 h 传递给它。
 实际上是在通过反射调用代理类的构造函数，将 InvocationHandler 实例 h 传递给它。代理类的构造函数会将这个 InvocationHandler 保存在一个字段中，以便在每次代理方法被调用时，能够通过该字段访问 InvocationHandler，并调用其 invoke 方法来处理方法调用。


    } catch (IllegalAccessException|InstantiationException e) {
        throw new InternalError(e.toString(), e);
    } catch (InvocationTargetException e) {
        Throwable t = e.getCause();
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else {
            throw new InternalError(t.toString(), t);
        }
    } catch (NoSuchMethodException e) {
        throw new InternalError(e.toString(), e);
    }
}
 原理和背景
 Java 动态代理主要依赖于两个核心部分：
 Proxy 类：Proxy 类提供了创建动态代理类和实例的基本方法。
 InvocationHandler 接口：所有的代理方法调用都会被委托给实现了这个接口的对象的 invoke 方法。
 代理类的生成
 当我们使用 Proxy.newProxyInstance 方法来创建代理对象时，Java 在运行时生成一个新的代理类。这些代理类具有以下特性：
 实现了指定的接口：代理对象会实现我们传入的所有接口。
 包含一个 InvocationHandler 字段：这个字段用于存储传入的 InvocationHandler 实例。
 代理类的构造函数：生成的代理类包含一个构造函数，这个构造函数接受一个 InvocationHandler 参数。这个参数会被用来初始化代理类内部的 InvocationHandler 字段。

 当 Proxy.newProxyInstance 方法被调用时，它会执行以下步骤：

 生成代理类：JVM 会动态生成一个实现了 MyInterface 的代理类。这是一个运行时生成的类，通常名字是系统生成的，例如 $Proxy0。

 生成的代理类包含构造函数：这个类包含一个构造函数，该构造函数接受一个参数，类型是 InvocationHandler。

 使用反射创建代理实例：

 构造函数初始化 InvocationHandler 字段：

 return cons.newInstance(new Object[]{h});
 实际上是在通过反射调用代理类的构造函数，将 InvocationHandler 实例 h 传递给它。代理类的构造函数会将这个 InvocationHandler 保存在一个字段中，以便在每次代理方法被调用时，能够通过该字段访问 InvocationHandler，并调用其 invoke 方法来处理方法调用。

 这样设计的目的是为了让代理对象能够将所有接口方法的调用通过 InvocationHandler.invoke 方法进行处理，这就是动态代理的核心逻辑。
 */
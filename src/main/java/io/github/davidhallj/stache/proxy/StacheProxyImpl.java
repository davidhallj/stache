package io.github.davidhallj.stache.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class StacheProxyImpl<T> implements InvocationHandler {

    private final InvocationHandler invocationHandler;
    private final Class<T> clazz;

    public StacheProxyImpl(Class<T> clazz, InvocationHandler invocationHandler) {
        this.clazz = clazz;
        this.invocationHandler = invocationHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invocationHandler.invoke(proxy, method, args);
    }

    public T proxy() {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { clazz }, invocationHandler);
    }

}

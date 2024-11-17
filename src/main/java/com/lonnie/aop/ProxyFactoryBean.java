package com.lonnie.aop;

import com.lonnie.beans.BeansException;
import com.lonnie.beans.factory.BeanFactory;
import com.lonnie.beans.factory.BeanFactoryAware;
import com.lonnie.beans.factory.FactoryBean;
import com.lonnie.util.ClassUtils;
import lombok.Getter;
import lombok.Setter;

public class ProxyFactoryBean implements FactoryBean<Object>, BeanFactoryAware {
    @Getter
    @Setter
    private AopProxyFactory aopProxyFactory;
    @Setter
    private String targetName;
    @Setter
    @Getter
    private Object target;
    private ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
    private Object singletonInstance;
    @Setter
    private BeanFactory beanFactory;
    @Setter
    private String interceptorName;
    private PointcutAdvisor advisor;

    private synchronized void initializeAdvisor() {
        Object advice;
        MethodInterceptor mi;
        try {
            advice = this.beanFactory.getBean(this.interceptorName);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
        this.advisor = (PointcutAdvisor) advice;
    }

    public ProxyFactoryBean() {
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }

    protected AopProxy createAopProxy() {
        return getAopProxyFactory().createAopProxy(target, this.advisor);
    }

    @Override
    public Object getObject() throws Exception {//获取内部对象
        return getSingletonInstance();
    }
    private synchronized Object getSingletonInstance() {//获取代理
        if (this.singletonInstance == null) {
            this.singletonInstance = getProxy(createAopProxy());
        }
        return this.singletonInstance;
    }
    protected Object getProxy(AopProxy aopProxy) {//生成代理对象
        return aopProxy.getProxy();
    }
    @Override
    public Class<?> getObjectType() {
        return null;
    }
}

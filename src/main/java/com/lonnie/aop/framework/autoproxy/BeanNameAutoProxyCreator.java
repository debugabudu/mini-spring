package com.lonnie.aop.framework.autoproxy;

import com.lonnie.aop.AopProxyFactory;
import com.lonnie.aop.DefaultAopProxyFactory;
import com.lonnie.aop.PointcutAdvisor;
import com.lonnie.aop.ProxyFactoryBean;
import com.lonnie.beans.BeansException;
import com.lonnie.beans.factory.BeanFactory;
import com.lonnie.beans.factory.config.BeanPostProcessor;
import com.lonnie.util.PatternMatchUtils;

public class BeanNameAutoProxyCreator implements BeanPostProcessor {
    String pattern; //代理对象名称模式，如action*
    private BeanFactory beanFactory;
    private AopProxyFactory aopProxyFactory;
    private String interceptorName;
    private PointcutAdvisor advisor;
    public BeanNameAutoProxyCreator() {
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }
    //核心方法。在bean实例化之后，init-method调用之前执行这个步骤。
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (isMatch(beanName, this.pattern)) {
            ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean(); //创建以恶ProxyFactoryBean
            proxyFactoryBean.setTarget(bean);
            proxyFactoryBean.setBeanFactory(beanFactory);
            proxyFactoryBean.setAopProxyFactory(aopProxyFactory);
            proxyFactoryBean.setInterceptorName(interceptorName);
            return proxyFactoryBean;
        }
        else {
            return bean;
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    protected boolean isMatch(String beanName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, beanName);
    }
}

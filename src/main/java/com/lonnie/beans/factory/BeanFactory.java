package com.lonnie.beans.factory;

import com.lonnie.beans.BeansException;

/**
 * Bean工厂，IoC容器
 * 获取/注册bean
 */
public interface BeanFactory {
    Object getBean(String beanName) throws BeansException;
    boolean containsBean(String name);
    boolean isSingleton(String name);
    boolean isPrototype(String name);
    Class<?> getType(String name);
    void registerBean(String beanName, Object obj);

    //最原始版本-未增加单例，仅有getBean和此方法
    //void registerBeanDefinition(BeanDefinition beanDefinition);
}

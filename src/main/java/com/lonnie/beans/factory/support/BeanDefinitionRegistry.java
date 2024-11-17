package com.lonnie.beans.factory.support;

import com.lonnie.beans.factory.config.BeanDefinition;

/**
 * 存放BeanDefinition的仓库
 */
public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String name, BeanDefinition bd);
    void removeBeanDefinition(String name);
    BeanDefinition getBeanDefinition(String name);
    boolean containsBeanDefinition(String name);
}

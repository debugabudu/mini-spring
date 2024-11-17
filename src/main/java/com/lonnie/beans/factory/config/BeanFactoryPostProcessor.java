package com.lonnie.beans.factory.config;

import com.lonnie.beans.BeansException;
import com.lonnie.beans.factory.BeanFactory;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(BeanFactory beanFactory) throws BeansException;
}

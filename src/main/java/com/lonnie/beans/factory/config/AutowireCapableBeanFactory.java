package com.lonnie.beans.factory.config;

import com.lonnie.beans.BeansException;
import com.lonnie.beans.factory.BeanFactory;

//增加注解特性
public interface AutowireCapableBeanFactory extends BeanFactory {
    int AUTOWIRE_NO = 0;
    int AUTOWIRE_BY_NAME = 1;
    int AUTOWIRE_BY_TYPE = 2;
    Object applyBeanPostProcessorBeforeInitialization(Object existingBean,
                                                       String beanName) throws BeansException;
    Object applyBeanPostProcessorAfterInitialization(Object existingBean,
                                                      String beanName) throws BeansException;
}

package com.lonnie.context;

import com.lonnie.beans.BeansException;
import com.lonnie.beans.factory.ListableBeanFactory;
import com.lonnie.beans.factory.config.BeanFactoryPostProcessor;
import com.lonnie.beans.factory.config.ConfigurableBeanFactory;
import com.lonnie.beans.factory.config.ConfigurableListableBeanFactory;
import com.lonnie.core.env.Environment;
import com.lonnie.core.env.EnvironmentCapable;

public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory,
        ConfigurableBeanFactory, ApplicationEventPublisher{
    String getApplicationName();
    long getStartupDate();
    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
    void setEnvironment(Environment environment);
    Environment getEnvironment();
    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);
    void refresh() throws BeansException, IllegalStateException;
    void close();
    boolean isActive();
}

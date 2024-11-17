package com.lonnie.context;

import com.lonnie.beans.BeansException;
import com.lonnie.beans.factory.config.BeanFactoryPostProcessor;
import com.lonnie.beans.factory.config.ConfigurableListableBeanFactory;
import com.lonnie.core.env.Environment;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public abstract class AbstractApplicationContext implements ApplicationContext{
    private Environment environment;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    private long startupDate;
    private final AtomicBoolean active = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public Object getBean(String beanName) throws BeansException {
        return getBeanFactory().getBean(beanName);
    }

    public void refresh() throws BeansException, IllegalStateException {
        postProcessBeanFactory(getBeanFactory());
        registerBeanPostProcessors(getBeanFactory());
        initApplicationEventPublisher();
        onRefresh();
        registerListeners();
        finishRefresh();
    }
    public abstract void registerListeners();
    public abstract void initApplicationEventPublisher();
    public abstract void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
    public abstract void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory);
    public abstract void onRefresh();
    public abstract void finishRefresh();
    @Override
    public String getApplicationName() {
        return "";
    }
    @Override
    public long getStartupDate() {
        return this.startupDate;
    }
    @Override
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws
            IllegalStateException;
    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor
                                                    postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }
    @Override
    public void close() {
    }
    @Override
    public boolean isActive(){
        return true;
    }
    //省略包装beanfactory的方法
}

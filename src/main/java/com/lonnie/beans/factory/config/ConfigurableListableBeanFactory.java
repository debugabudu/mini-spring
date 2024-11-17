package com.lonnie.beans.factory.config;

import com.lonnie.beans.factory.ListableBeanFactory;

//每个interface代表一种特性或能力
public interface ConfigurableListableBeanFactory extends ListableBeanFactory,
        AutowireCapableBeanFactory, ConfigurableBeanFactory {
}

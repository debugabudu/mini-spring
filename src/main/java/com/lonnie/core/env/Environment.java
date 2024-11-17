package com.lonnie.core.env;

//用于获取属性，所有ApplicationContext都实现了该接口
public interface Environment extends PropertyResolver {
    String[] getActiveProfiles();
    String[] getDefaultProfiles();
    boolean acceptsProfiles(String... profiles);
}

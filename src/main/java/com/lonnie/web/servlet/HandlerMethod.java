package com.lonnie.web.servlet;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

public class HandlerMethod {
    @Setter
    @Getter
    private  Object bean;
    private  Class<?> beanType;
    @Setter
    @Getter
    private  Method method;
    //private  MethodParameter[] parameters;
    private  Class<?> returnType;
    private  String description;
    private  String className;
    private  String methodName;

    public HandlerMethod(Method method, Object obj) {
        this.setMethod(method);
        this.setBean(obj);
    }
}

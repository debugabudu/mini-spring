package com.lonnie.aop;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DefaultAdvisor implements Advisor{
    private MethodInterceptor methodInterceptor;
    public DefaultAdvisor() {
    }
}

package com.lonnie.aop;

public interface PointcutAdvisor extends Advisor {
    Pointcut getPointcut();
}

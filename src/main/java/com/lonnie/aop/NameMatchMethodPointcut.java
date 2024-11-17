package com.lonnie.aop;

import com.lonnie.util.PatternMatchUtils;
import lombok.Setter;

import java.lang.reflect.Method;

@Setter
public class NameMatchMethodPointcut implements MethodMatcher, Pointcut{
    private String mappedName = "";

    @Override
    public boolean matches(Method method, Class<?> targetCLass) {
        if (mappedName.equals(method.getName()) || isMatch(method.getName(), mappedName)) {
            return true;
        }
        return false;
    }
    //核心方法，判断方法名是否匹配给定的模式
    protected boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }
    @Override
    public MethodMatcher getMethodMatcher() {
        return null;
    }
}

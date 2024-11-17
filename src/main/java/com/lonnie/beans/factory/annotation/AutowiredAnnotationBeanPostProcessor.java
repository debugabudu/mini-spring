package com.lonnie.beans.factory.annotation;

import com.lonnie.beans.BeansException;
import com.lonnie.beans.factory.config.AutowireCapableBeanFactory;
import com.lonnie.beans.factory.config.BeanPostProcessor;
import lombok.Data;

import java.lang.reflect.Field;

@Data
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {
    private AutowireCapableBeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        //对每一个属性进行判断，如果带有@Autowired注解则进行处理
        for(Field field : fields){
            boolean isAutowired = field.isAnnotationPresent(Autowired.class);
            if(isAutowired){
                //根据属性名查找同名的bean
                String fieldName = field.getName();
                Object autowiredObj = this.getBeanFactory().getBean(fieldName);
                //设置属性值，完成注入
                try {
                    field.setAccessible(true);
                    field.set(bean, autowiredObj);
                    System.out.println("autowire " + fieldName + " for bean beanName");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return null;
    }
}


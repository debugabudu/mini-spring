package com.lonnie.beans.factory.support;

import com.lonnie.beans.BeansException;
import com.lonnie.beans.PropertyValue;
import com.lonnie.beans.PropertyValues;
import com.lonnie.beans.factory.BeanFactoryAware;
import com.lonnie.beans.factory.FactoryBean;
import com.lonnie.beans.factory.config.BeanDefinition;
import com.lonnie.beans.factory.config.ConfigurableBeanFactory;
import com.lonnie.beans.factory.config.ConstructorArgumentValue;
import com.lonnie.beans.factory.config.ConstructorArgumentValues;
import lombok.NoArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport
        implements ConfigurableBeanFactory, BeanDefinitionRegistry {
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    protected List<String> beanDefinitionNames = new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    public void refresh() {
        for (String beanName : beanDefinitionNames) {
            try {
                getBean(beanName);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public Object getBean(String beanName) throws BeansException {
        //先尝试直接从容器中获取bean实例
        Object singleton = this.getSingleton(beanName);
        if (singleton == null) {
            //如果没有实例，则尝试从毛胚实例中获取
            singleton = this.earlySingletonObjects.get(beanName);
            if (singleton == null) {
                //如果连毛胚都没有，则创建bean实例并注册
                System.out.println("get bean null -------------- " + beanName);
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                if (beanDefinition != null) {
                    singleton = createBean(beanDefinition);
                    this.registerBean(beanName, singleton);
                    if (singleton instanceof BeanFactoryAware) {
                        ((BeanFactoryAware) singleton).setBeanFactory(this);
                    }
                    // 进行beanpostprocessor处理
                    // step 1: postProcessBeforeInitialization
                    singleton = applyBeanPostProcessorBeforeInitialization(singleton, beanName);
                    // step 2: init-method
                    if (beanDefinition.getInitMethodName() != null
                            && !beanDefinition.getInitMethodName().isEmpty()) {
                        invokeInitMethod(beanDefinition, singleton);
                    }
                    // step 3: postProcessAfterInitialization
                    applyBeanPostProcessorAfterInitialization(singleton, beanName);
                    this.removeSingleton(beanName);
                    this.registerBean(beanName, singleton);
                }else {
                    return null;
                }
            }
        }
        if (singleton instanceof FactoryBean) {
            return this.getObjectForBeanInstance(singleton, beanName);
        }
        return singleton;
    }
    private void invokeInitMethod(BeanDefinition beanDefinition, Object obj) {
        Class<?> clz = beanDefinition.getClass();
        Method method;
        try {
            method = clz.getMethod(beanDefinition.getInitMethodName());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            method.invoke(obj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean containsBean(String name) {
        return containsSingleton(name);
    }
    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);
    }
    @Override
    public void registerBeanDefinition(String name, BeanDefinition
            beanDefinition) {
        this.beanDefinitionMap.put(name, beanDefinition);
        this.beanDefinitionNames.add(name);
        if (!beanDefinition.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);
    }
    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }
    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }
    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }
    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }
    @Override
    public Class<?> getType(String name) {
        return this.beanDefinitionMap.get(name).getClass();
    }
    private Object createBean(BeanDefinition beanDefinition) {
        Class<?> clz;
        //创建毛胚bean实例
        Object obj = doCreateBean(beanDefinition);
        //存放到毛胚实例缓存中
        this.earlySingletonObjects.put(beanDefinition.getId(), obj);
        try {
            clz = Class.forName(beanDefinition.getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //完善bean，主要是处理属性
        populateBean(beanDefinition, clz, obj);
        return obj;
    }
    //doCreateBean创建毛胚实例，仅仅调用构造方法，没有进行属性处理
    private Object doCreateBean(BeanDefinition beanDefinition) {
        Class<?> clz;
        Object obj = null;
        Constructor<?> con;
        try {
            clz = Class.forName(beanDefinition.getClassName());
            // handle constructor
            ConstructorArgumentValues constructorArgumentValues =
                    beanDefinition.getConstructorArgumentValues();
            if (!constructorArgumentValues.isEmpty()) {
                Class<?>[] paramTypes = new Class<?>
                        [constructorArgumentValues.getArgumentCount()];
                Object[] paramValues = new
                        Object[constructorArgumentValues.getArgumentCount()];
                for (int i = 0; i <
                        constructorArgumentValues.getArgumentCount(); i++) {
                    ConstructorArgumentValue constructorArgumentValue =
                            constructorArgumentValues.getIndexedArgumentValue(i);
                    if ("String".equals(constructorArgumentValue.getType()) ||
                            "java.lang.String".equals(constructorArgumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = constructorArgumentValue.getValue();
                    } else if
                    ("Integer".equals(constructorArgumentValue.getType()) ||
                                    "java.lang.Integer".equals(constructorArgumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.valueOf((String)
                                constructorArgumentValue.getValue());
                    } else if ("int".equals(constructorArgumentValue.getType()))
                    {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.valueOf((String)
                                constructorArgumentValue.getValue());
                    } else {
                        paramTypes[i] = String.class;
                        paramValues[i] = constructorArgumentValue.getValue();
                    }
                }
                con = clz.getConstructor(paramTypes);
                obj = con.newInstance(paramValues);
            }
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        System.out.println(beanDefinition.getId() + " bean created. " +
                beanDefinition.getClassName() + " : " + obj.toString());
        return obj;
    }
    private void populateBean(BeanDefinition beanDefinition, Class<?> clz, Object obj) {
        handleProperties(beanDefinition, clz, obj);
    }
    private void handleProperties(BeanDefinition beanDefinition, Class<?> clz, Object obj) {
        // handle properties
        System.out.println("handle properties for bean : " +
                beanDefinition.getId());
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        //如果有属性
        if (!propertyValues.isEmpty()) {
            for (int i = 0; i < propertyValues.size(); i++) {
                PropertyValue propertyValue =
                        propertyValues.getPropertyValueList().get(i);
                String pType = propertyValue.getType();
                String pName = propertyValue.getName();
                Object pValue = propertyValue.getValue();
                boolean isRef = propertyValue.isRef();
                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues = new Object[1];
                if (!isRef) { //如果不是ref，只是普通属性
                    //对每一个属性，分数据类型分别处理
                    if ("String".equals(pType) ||
                            "java.lang.String".equals(pType)) {
                        paramTypes[0] = String.class;
                    } else if ("Integer".equals(pType) ||
                            "java.lang.Integer".equals(pType)) {
                        paramTypes[i] = Integer.class;
                    } else if ("int".equals(pType)) {
                        paramTypes[i] = int.class;
                    } else {
                        paramTypes[i] = String.class;
                    }
                    paramValues[0] = pValue;
                } else {//is ref, create the dependent beans
                    try {
                        paramTypes[0] = Class.forName(pType);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    try {//再次调用getBean创建ref的bean实例
                        paramValues[0] = getBean((String) pValue);
                    } catch (BeansException e) {
                        throw new RuntimeException(e);
                    }
                }
                //按照setXxxx规范查找setter方法，调用setter方法设置属性
                String methodName = "set" + pName.substring(0, 1).toUpperCase()
                        + pName.substring(1);
                Method method;
                try {
                    method = clz.getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                try {
                    method.invoke(obj, paramValues);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected Object getObjectForBeanInstance(Object beanInstance, String beanName) {
        // Now we have the bean instance, which may be a normal bean or a FactoryBean.
        if (!(beanInstance instanceof FactoryBean)) {
            return beanInstance;
        }
        Object object;
        FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
        object = getObjectFromFactoryBean(factory, beanName);
        return object;
    }

    //下面两个方法教给具体的继承类去实现
    abstract public Object applyBeanPostProcessorBeforeInitialization(Object existingBean, String beanName)
            throws BeansException;
    abstract public Object applyBeanPostProcessorAfterInitialization(Object existingBean, String beanName)
            throws BeansException;
}

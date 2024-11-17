//package com.lonnie.beans.factory.supprot;
//
//import com.lonnie.beans.*;
//import com.lonnie.beans.factory.BeanFactory;
//import com.lonnie.beans.factory.config.BeanDefinition;
//import com.lonnie.beans.factory.config.ConstructorArgumentValue;
//import com.lonnie.beans.factory.config.ConstructorArgumentValues;
//import lombok.NoArgsConstructor;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;

/**
 * 既是仓库也是工厂
 * 增加注解后被AutowireCapableBeanFactory替换
 */
//@NoArgsConstructor
//public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {
//    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
//    private List<String> beanDefinitionNames = new ArrayList<>();
//    private Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(256);
//
//    //getBean，容器的核心方法
//    public Object getBean(String beanName) throws BeansException {
//        //先尝试直接拿bean实例
//        Object singleton = this.getSingleton(beanName);
//        //如果此时还没有这个bean的实例，则获取它的定义来创建实例
//        if (singleton == null) {
//            //先尝试获取毛坯示例
//            singleton = this.earlySingletonObjects.get(beanName);
//            if (singleton == null) {
//                //获取bean的定义
//                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
//                singleton = createBean(beanDefinition);
//                //新注册这个bean实例
//                this.registerSingleton(beanName, singleton);
//            }
//        }
//        return singleton;
//    }
//
//    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
//        this.beanDefinitionMap.put(name, beanDefinition);
//        this.beanDefinitionNames.add(name);
//        if (!beanDefinition.isLazyInit()){
//            try {
//                getBean(name);
//            } catch (BeansException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    public void removeBeanDefinition(String name) {
//        this.beanDefinitionNames.remove(name);
//        this.beanDefinitionMap.remove(name);
//        this.removeSingleton(name);
//    }
//
//    public BeanDefinition getBeanDefinition(String name) {
//        return this.beanDefinitionMap.get(name);
//    }
//
//    public boolean containsBeanDefinition(String name) {
//        return this.beanDefinitionMap.containsKey(name);
//    }
//
//    public boolean isSingleton(String name) {
//        return this.beanDefinitionMap.get(name).isSingleton();
//    }
//
//    public boolean isPrototype(String name) {
//        return this.beanDefinitionMap.get(name).isPrototype();
//    }
//
//    public Class<?> getType(String name) {
//        return this.beanDefinitionMap.get(name).getClass();
//    }
//
//    public boolean containsBean(String name) {
//        return containsSingleton(name);
//    }
//
//    public void registerBean(String beanName, Object obj) {
//        this.registerSingleton(beanName, obj);
//    }
//
//    /**
//     * 增加构造器和setter注入参数处理后新增
//     * 依赖注入-依靠反射
//     * 第一版：所有的注入放在一个方法中
//     * 第二版：处理依赖注入的情况，把property单独拿出来处理
//     * 第三版：解决循环依赖，把构造器注入单独拿出来处理，先构造毛坯实例
//     */
//    private Object createBean(BeanDefinition beanDefinition) {
//        Class<?> clz;
//        //创建毛坯实例
//        Object obj = doCreateBean(beanDefinition);
//        this.earlySingletonObjects.put(beanDefinition.getId(), obj);
//        try {
//            clz = Class.forName(beanDefinition.getClassName());
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        // 处理属性
//        handleProperties(beanDefinition, clz, obj);
//        return obj;
//    }
//
//    private Object doCreateBean(BeanDefinition beanDefinition) {
//        Class<?> clz;
//        Object obj = null;
//        Constructor<?> con;
//        try {
//            clz = Class.forName(beanDefinition.getClassName());
//            // 处理构造器参数
//            ConstructorArgumentValues argumentValues =
//                    beanDefinition.getConstructorArgumentValues();
//            //如果有参数
//            if (!argumentValues.isEmpty()) {
//                Class<?>[] paramTypes = new Class<?>
//                        [argumentValues.getArgumentCount()];
//                Object[] paramValues = new
//                        Object[argumentValues.getArgumentCount()];
//                //对每一个参数，分数据类型分别处理（暂时只处理string int integer三种类型）
//                for (int i = 0; i < argumentValues.getArgumentCount(); i++) {
//                    ConstructorArgumentValue argumentValue =
//                            argumentValues.getIndexedArgumentValue(i);
//                    if ("String".equals(argumentValue.getType()) ||
//                            "java.lang.String".equals(argumentValue.getType())) {
//                        paramTypes[i] = String.class;
//                        paramValues[i] = argumentValue.getValue();
//                    } else if ("Integer".equals(argumentValue.getType()) ||
//                            "java.lang.Integer".equals(argumentValue.getType())) {
//                        paramTypes[i] = Integer.class;
//                        paramValues[i] =
//                                Integer.valueOf((String)argumentValue.getValue());
//                    } else if ("int".equals(argumentValue.getType())) {
//                        paramTypes[i] = int.class;
//                        paramValues[i] = Integer.valueOf((String)
//                                argumentValue.getValue());
//                    } else { //默认为string
//                        paramTypes[i] = String.class;
//                        paramValues[i] = argumentValue.getValue();
//                    }
//                }
//                try {
//                    //按照特定构造器创建实例
//                    con = clz.getConstructor(paramTypes);
//                    obj = con.newInstance(paramValues);
//                } catch (NoSuchMethodException | SecurityException | InstantiationException |
//                         IllegalAccessException | IllegalArgumentException |
//                         InvocationTargetException e) {
//                    throw new RuntimeException(e);
//                }
//            } else { //如果没有参数，直接创建实例
//                obj = clz.newInstance();
//            }
//        } catch (Exception ignored) {
//        }
//        return obj;
//    }
//
//    private void handleProperties(BeanDefinition bd, Class<?> clz, Object obj) {
//        // 处理属性
//        System.out.println("handle properties for bean : " + bd.getId());
//        PropertyValues propertyValues = bd.getPropertyValues();
//        //如果有属性
//        if (!propertyValues.isEmpty()) {
//            for (int i=0; i<propertyValues.size(); i++) {
//                PropertyValue propertyValue = propertyValues.getPropertyValueList().get(i);
//                String pName = propertyValue.getName();
//                String pType = propertyValue.getType();
//                Object pValue = propertyValue.getValue();
//                boolean isRef = propertyValue.isRef();
//                Class<?>[] paramTypes = new Class<?>[1];
//                Object[] paramValues =   new Object[1];
//                if (!isRef) { //如果不是ref，只是普通属性
//                    //对每一个属性，分数据类型分别处理
//                    if ("String".equals(pType) || "java.lang.String".equals(pType)) {
//                        paramTypes[0] = String.class;
//                    }
//                    else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
//                        paramTypes[0] = Integer.class;
//                    }
//                    else if ("int".equals(pType)) {
//                        paramTypes[0] = int.class;
//                    }
//                    else {
//                        paramTypes[0] = String.class;
//                    }
//
//                    paramValues[0] = pValue;
//                }
//                else { //is ref, create the dependent beans
//                    try {
//                        paramTypes[0] = Class.forName(pType);
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        //再次调用getBean创建ref的bean实例
//                        paramValues[0] = getBean((String)pValue);
//                    } catch (BeansException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//
//                //按照setXxxx规范查找setter方法，调用setter方法设置属性
//                String methodName = "set" + pName.substring(0,1).toUpperCase() + pName.substring(1);
//                Method method;
//                try {
//                    method = clz.getMethod(methodName, paramTypes);
//                } catch (NoSuchMethodException e) {
//                    throw new RuntimeException(e);
//                }
//                try {
//                    method.invoke(obj, paramValues);
//                } catch (InvocationTargetException | IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }
//
//    //一键加载，从bean工厂创建到bean对象实例化初始化，再到spring容器加载
//    public void refresh() {
//        for (String beanName : beanDefinitionNames) {
//            try {
//                getBean(beanName);
//            } catch (BeansException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//}

/**
 * 最原始版本（未增加单例）
 */
//public class SimpleBeanFactory implements BeanFactory{
//    private List<BeanDefinition> beanDefinitions = new ArrayList<>();
//    private List<String> beanNames = new ArrayList<>();
//    private Map<String, Object> singletons = new HashMap<>();
//    public SimpleBeanFactory() {
//    }
//
//    //getBean，容器的核心方法
//    public Object getBean(String beanName) throws BeansException{
//        //先尝试直接拿Bean实例
//        Object singleton = singletons.get(beanName);
//        //如果此时还没有这个Bean的实例，则获取它的定义来创建实例
//        if (singleton == null) {
//            int i = beanNames.indexOf(beanName);
//            if (i == -1) {
//                throw new BeansException("");
//            }
//            else {
//                //获取Bean的定义
//                BeanDefinition beanDefinition = beanDefinitions.get(i);
//                try {
//                    singleton = Class.forName(beanDefinition.getClassName()).newInstance();
//                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//                //注册Bean实例
//                singletons.put(beanDefinition.getId(), singleton);
//            }
//        }
//        return singleton;
//    }
//
//    public void registerBeanDefinition(BeanDefinition beanDefinition) {
//        this.beanDefinitions.add(beanDefinition);
//        this.beanNames.add(beanDefinition.getId());
//    }
//}

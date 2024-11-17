package com.lonnie.context;

import com.lonnie.beans.BeansException;
import com.lonnie.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.lonnie.beans.factory.config.*;
import com.lonnie.beans.factory.support.DefaultListableBeanFactory;
import com.lonnie.beans.factory.xml.XmlBeanDefinitionReader;
import com.lonnie.core.ClassPathXmlResource;
import com.lonnie.core.Resource;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext{
    DefaultListableBeanFactory beanFactory;
    @Getter
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }
    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        Resource resource = new ClassPathXmlResource(fileName);
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);
        this.beanFactory = beanFactory;
        if (isRefresh) {
            try {
                refresh();
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public  void registerListeners() {
        ApplicationListener listener = new ApplicationListener();
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }
    @Override
    public  void initApplicationEventPublisher() {
        ApplicationEventPublisher aep = new SimpleApplicationEventPublisher();
        this.setApplicationEventPublisher(aep);
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        this.getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }

    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor
                                                    postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    @Override
    public void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory)
    {
        this.beanFactory.addBeanPostProcessor(new
                AutowiredAnnotationBeanPostProcessor());
    }

    @Override
    public void onRefresh() {
        this.beanFactory.refresh();
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws
            IllegalStateException {
        return this.beanFactory;
    }

    @Override
    public void finishRefresh() {
        publishEvent(new ContextRefreshEvent("Context Refreshed..."));
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return false;
    }

    @Override
    public int getBeanDefinitionCount() {
        return 0;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return new String[0];
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return new String[0];
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return Map.of();
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {

    }

    @Override
    public int getBeanPostProcessorCount() {
        return 0;
    }

    @Override
    public void registerDependentBean(String beanName, String dependentBeanName) {

    }

    @Override
    public String[] getDependentBeans(String beanName) {
        return new String[0];
    }

    @Override
    public String[] getDependenciesForBean(String beanName) {
        return new String[0];
    }

    @Override
    public boolean containsBean(String name) {
        return false;
    }

    @Override
    public boolean isSingleton(String name) {
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        return false;
    }

    @Override
    public Class<?> getType(String name) {
        return null;
    }

    @Override
    public void registerBean(String beanName, Object obj) {

    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {

    }

    @Override
    public Object getSingleton(String beanName) {
        return null;
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return false;
    }

    @Override
    public String[] getSingletonNames() {
        return new String[0];
    }
}

/**
 * 解析某个路径下的xml文件来构建应用上下文
 * 最初版-未加单例时，使用BeanFactory
 * 加完单例全部换成SimpleBeanFactory
 * 增加注解相关，SimpleBeanFactory全部替换为AutowireCapableBeanFactory
 * 新流程：
 * 1.启动 ClassPathXmlApplicationContext 容器，执行 refresh()。
 * 2.在 refresh 执行过程中，调用 registerBeanPostProcessors()，往 BeanFactory 里注册 Bean 处理器，如 AutowiredAnnotationBeanPostProcessor。
 * 3.执行 onRefresh()， 执行 AbstractBeanFactory 的 refresh() 方法。
 * 4.AbstractBeanFactory 的 refresh() 获取所有 Bean 的定义，执行 getBean() 创建 Bean 实例。
 * 5.getBean() 创建完 Bean 实例后，调用 Bean 处理器并初始化。
 * 6.applyBeanPostProcessorBeforeInitialization 由具体的 BeanFactory，如 AutowireCapableBeanFactory，来实现
 * 7.事先准备好的 AutowiredAnnotationBeanPostProcessor 方法里面会解释 Bean 中的 Autowired 注解。
 * 完整版之前的版本-未抽象出ApplicationContext和AbstractApplicationContext
 */
//public class ClassPathXmlApplicationContext implements BeanFactory, ApplicationEventPublisher{
//    AbstractAutowireCapableBeanFactory beanFactory;
//    @Getter
//    List<AutowiredAnnotationBeanPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
//
//    public ClassPathXmlApplicationContext(String fileName) {
//        this(fileName,true);
//    }
//
//    //context负责整合容器的启动过程，读外部配置，解析Bean定义，创建BeanFactory
//    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
//        Resource resource = new ClassPathXmlResource(fileName);
//        AbstractAutowireCapableBeanFactory autowireCapableBeanFactory = new AbstractAutowireCapableBeanFactory();
//        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(autowireCapableBeanFactory);
//        reader.loadBeanDefinitions(resource);
//        this.beanFactory = autowireCapableBeanFactory;
//        if (isRefresh) {
//            try {
//                refresh();
//            } catch (BeansException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    public void addBeanFactoryPostProcessor(AutowiredAnnotationBeanPostProcessor postProcessor) {
//        this.beanFactoryPostProcessors.add(postProcessor);
//    }
//    public void refresh() throws BeansException, IllegalStateException {
//        // Register bean processors that intercept bean creation.
//        registerBeanPostProcessors(this.beanFactory);
//        // Initialize other special beans in specific context subclasses.
//        onRefresh();
//    }
//    private void registerBeanPostProcessors(AbstractAutowireCapableBeanFactory beanFactory) {
//        beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
//    }
//    private void onRefresh() {
//        this.beanFactory.refresh();
//    }
//
//    //context再对外提供一个getBean，底下就是调用的BeanFactory对应的方法
//    public Object getBean(String beanName) throws BeansException {
//        return this.beanFactory.getBean(beanName);
//    }
//
//    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
//        this.beanFactory.registerBeanDefinition(name, beanDefinition);
//    }
//
//    /**
//     * 跟随BeanFactory的改动，增加containsBean和registerBean方法
//     */
//    public boolean containsBean(String name) {
//        return this.beanFactory.containsBean(name);
//    }
//
//    public void registerBean(String beanName, Object obj) {
//        this.beanFactory.registerBean(beanName, obj);
//    }
//
//    public void publishEvent(ApplicationEvent event) { }
//
//    @Override
//    public void addApplicationListener(ApplicationListener listener) {
//
//    }
//
//    public boolean isSingleton(String name) { return false; }
//
//    public boolean isPrototype(String name) { return false; }
//
//    public Class<?> getType(String name) { return null; }

    /**
     * 第一节最初版本，Resource、Reader、BeanFactory早期版本的融合
     */
//    private List<BeanDefinition> beanDefinitions = new ArrayList<>();
//    private Map<String, Object> singletons = new HashMap<>();
//    //构造器获取外部配置，解析出Bean的定义，形成内存映像
//    public ClassPathXmlApplicationContext(String fileName) {
//        this.readXml(fileName);
//        this.instanceBeans();
//    }
//    private void readXml(String fileName) {
//        SAXReader saxReader = new SAXReader();
//        try {
//            URL xmlPath =
//                    this.getClass().getClassLoader().getResource(fileName);
//            Document document = saxReader.read(xmlPath);
//            Element rootElement = document.getRootElement();
//            //对配置文件中的每一个<bean>，进行处理
//            for (Element element : rootElement.elements()) {
//                //获取Bean的基本信息
//                String beanID = element.attributeValue("id");
//                String beanClassName = element.attributeValue("class");
//                BeanDefinition beanDefinition = new BeanDefinition(beanID,
//                        beanClassName);
//                //将Bean的定义存放到beanDefinitions
//                beanDefinitions.add(beanDefinition);
//            }
//        } catch (DocumentException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    //利用反射创建Bean实例，并存储在singletons中
//    private void instanceBeans() {
//        for (BeanDefinition beanDefinition : beanDefinitions) {
//            try {
//                singletons.put(beanDefinition.getId(),
//                        Class.forName(beanDefinition.getClassName()).newInstance());
//            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//    //这是对外的一个方法，让外部程序从容器中获取Bean实例，会逐步演化成核心方法
//    public Object getBean(String beanName) {
//        return singletons.get(beanName);
//    }
//}

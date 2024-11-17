package com.lonnie.beans.factory.xml;

import com.lonnie.beans.factory.config.AbstractAutowireCapableBeanFactory;
import com.lonnie.beans.factory.config.AutowireCapableBeanFactory;
import com.lonnie.beans.factory.config.ConstructorArgumentValues;
import com.lonnie.beans.PropertyValue;
import com.lonnie.beans.PropertyValues;
import com.lonnie.beans.factory.config.BeanDefinition;
import com.lonnie.core.Resource;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 从xml resource中读取bean
 * 最初版-未加单例时，使用BeanFactory
 * 加完单例全部换成SimpleBeanFactory
 * 增加注解相关，SimpleBeanFactory全部替换为AutowireCapableBeanFactory
 */
public class XmlBeanDefinitionReader {
    AbstractAutowireCapableBeanFactory simpleBeanFactory;
    public XmlBeanDefinitionReader(AbstractAutowireCapableBeanFactory simpleBeanFactory) {
        this.simpleBeanFactory = simpleBeanFactory;
    }

    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanID = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanID, beanClassName);
            /**
             * 增加setter和构造器注入值的处理
             */
            //处理构造器参数
            List<Element> constructorElements = element.elements("constructor-arg");
            ConstructorArgumentValues AVS = new ConstructorArgumentValues();
            for (Element e : constructorElements) {
                String aType = e.attributeValue("type");
                String aName = e.attributeValue("name");
                String aValue = e.attributeValue("value");
                AVS.addGenericArgumentValue(aType, aValue, aName);
            }
            beanDefinition.setConstructorArgumentValues(AVS);

            //处理属性
            List<Element> propertyElements = element.elements("property");
            PropertyValues PVS = new PropertyValues();
            List<String> refs = new ArrayList<>();
            for (Element e : propertyElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                String pRef = e.attributeValue("ref");
                String pV = "";
                boolean isRef = false;
                if (pValue != null && !pValue.isEmpty()) {
                    pV = pValue;
                }else if (pRef != null && !pRef.isEmpty()) {
                    isRef = true;
                    pV = pRef;
                    refs.add(pRef);
                }
                PVS.addPropertyValue(new PropertyValue(pType, pName, pV, isRef));
            }
            beanDefinition.setPropertyValues(PVS);

            String[] refArray = refs.toArray(new String[0]);
            beanDefinition.setDependsOn(refArray);
            this.simpleBeanFactory.registerBeanDefinition(beanID, beanDefinition);
        }
    }
}

package com.lonnie.web;

import javax.servlet.ServletContext;
import com.lonnie.context.ClassPathXmlApplicationContext;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class XmlWebApplicationContext
        extends ClassPathXmlApplicationContext implements WebApplicationContext{
    private ServletContext servletContext;

    public XmlWebApplicationContext(String fileName) {
        super(fileName);
    }

}

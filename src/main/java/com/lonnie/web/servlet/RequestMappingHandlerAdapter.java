package com.lonnie.web.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lonnie.beans.BeansException;
import com.lonnie.web.*;

public class RequestMappingHandlerAdapter implements HandlerAdapter {
    WebApplicationContext wac;
    private WebBindingInitializer webBindingInitializer;
    private HttpMessageConverter messageConverter = null;

    public RequestMappingHandlerAdapter(WebApplicationContext wac) {
        try {
            this.webBindingInitializer = (WebBindingInitializer) this.wac.
                    getBean("webBindingInitializer");
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        handleInternal(request, response, (HandlerMethod) handler);
    }
    private void handleInternal(HttpServletRequest request, HttpServletResponse response,
                                HandlerMethod handler) {
        Method method = handler.getMethod();
        Object obj = handler.getBean();
        Object objResult = null;
        try {
            objResult = method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            response.getWriter().append(objResult.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected ModelAndView invokeHandlerMethod(HttpServletRequest request,
                                       HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        ModelAndView mav = null;
        WebDataBinderFactory binderFactory = new WebDataBinderFactory();
        Parameter[] methodParameters =
                handlerMethod.getMethod().getParameters();
        Object[] methodParamObjs = new Object[methodParameters.length];
        int i = 0;
        //对调用方法里的每一个参数，处理绑定
        for (Parameter methodParameter : methodParameters) {
            Object methodParamObj = methodParameter.getType().newInstance();
            //给这个参数创建WebDataBinder
            WebDataBinder wdb = binderFactory.createBinder(request,
                    methodParamObj, methodParameter.getName());
            wdb.bind(request);
            methodParamObjs[i] = methodParamObj;
            i++;
        }
        Method invocableMethod = handlerMethod.getMethod();
        Object returnObj = invocableMethod.invoke(handlerMethod.getBean(), methodParamObjs);
        response.getWriter().append(returnObj.toString());
        if (invocableMethod.isAnnotationPresent(ResponseBody.class)){ //ResponseBody
             this.messageConverter.write(returnObj, response);
        }else {
            if (returnObj instanceof ModelAndView) {
                mav = (ModelAndView)returnObj;
            } else if (returnObj instanceof String) { //字符串也认为是前端页面
                String sTarget = (String)returnObj;
                mav = new ModelAndView();
                mav.setViewName(sTarget);
            }
        }
        return mav;
    }
}

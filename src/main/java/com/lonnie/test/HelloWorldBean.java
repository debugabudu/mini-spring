package com.lonnie.test;

import com.lonnie.web.RequestMapping;

public class HelloWorldBean {
    @RequestMapping("/test")
    public String doTest(){
        return "hello world for doTest!";
    }
//    public String doGet() {
//        return "hello world!";
//    }
//    public String doPost() {
//        return "hello world!";
//    }
}

package com.lonnie.web;


import lombok.Getter;
import lombok.Setter;

/**
 * uri clz method 分别对应servlet.xml的 id class value
 */
@Setter
@Getter
public class MappingValue {
    String uri;
    String clz;
    String method;

    public MappingValue(String uri, String clz, String method) {
        this.uri = uri;
        this.clz = clz;
        this.method = method;
    }
}

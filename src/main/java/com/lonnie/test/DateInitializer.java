package com.lonnie.test;

import java.util.Date;
import com.lonnie.web.WebBindingInitializer;
import com.lonnie.web.WebDataBinder;

public class DateInitializer implements WebBindingInitializer{
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(Date.class,"yyyy-MM-dd", false));
    }
}


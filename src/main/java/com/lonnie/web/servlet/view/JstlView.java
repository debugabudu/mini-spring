package com.lonnie.web.servlet.view;

import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.lonnie.web.servlet.View;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JstlView implements View{
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";
    private String contentType = DEFAULT_CONTENT_TYPE;
    private String requestContextAttribute;
    private String beanName;
    private String url;

    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        for (Entry<String, ?> e : model.entrySet()) {
            request.setAttribute(e.getKey(),e.getValue());
        }
        request.getRequestDispatcher(getUrl()).forward(request, response);
    }
}

package net.snnmo.interceptors;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by TTong on 16-1-12.
 */
public class RoleInterceptor  extends HandlerInterceptorAdapter {
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        System.out.println("RoleInterceptor: REQUEST Intercepted for URI: "
                + request.getRequestURI());

        return true;
    }
}
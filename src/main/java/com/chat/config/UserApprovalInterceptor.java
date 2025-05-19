package com.chat.config;

import com.chat.model.User;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserApprovalInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user != null && !user.isApproved()) {
            response.sendRedirect("/login?error=not_approved");
            return false;
        }
        return true;
    }
}

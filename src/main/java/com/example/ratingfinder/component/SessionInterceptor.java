package com.example.ratingfinder.component;

import com.example.ratingfinder.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;




@Component
public class SessionInterceptor implements HandlerInterceptor {

    //interceptor check user state,return true if user is signed and stored in session, false if the user has not signed in or is not stored in session.
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User u =(User) session.getAttribute("currentUser");
        if(u!=null && u.getUser_id()>0)
        {
            return true;
        }else {
            return false;
        }
    }

}

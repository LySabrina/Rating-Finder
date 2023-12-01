package com.example.ratingfinder.component;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
   //this interceptor check valid the current user by session, if user have not signed in, it will intercept the api access.
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/api/getAllUsers");
        registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/product/allProduct");
//        registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/delete");
//        registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/update");
    }
}

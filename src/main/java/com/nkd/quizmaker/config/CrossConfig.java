package com.nkd.quizmaker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//@Configuration
public class CrossConfig {

//    @Bean
//    public WebMvcConfigurer getCrossConfig() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
////                registry.addMapping("/admin/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE");
////                registry.addMapping("/**")
////                        .allowedOrigins("http://127.0.0.1:3000")
////                        .allowedMethods(GET.name(), POST.name(), DELETE.name(), PUT.name())
////                        .allowedHeaders("*");
////                registry.addMapping("/**")
////                        .allowedMethods("GET","POST","PUT","DELETE")
////                        .allowedOrigins("http://localhost:3000");
//
//                registry
//                        .addMapping("/**")
//                        .allowedMethods("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
//            }
//        };
//    }
}

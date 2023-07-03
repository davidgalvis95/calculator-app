//package com.calculator.calculatorapi.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//
//import java.io.IOException;
//
//@Slf4j
//@Component
//public class GlobalCalculatorFilter extends OncePerRequestFilter {
//
//    @Qualifier("handlerExceptionResolver")
//    private final HandlerExceptionResolver resolver;
//
//    @Autowired
//    public GlobalCalculatorFilter(HandlerExceptionResolver resolver) {
//        this.resolver = resolver;
//    }
//
//    @Override
//    protected void doFilterInternal(final HttpServletRequest request,
//                                    final HttpServletResponse response,
//                                    final FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            filterChain.doFilter(request, response);
//        } catch (Exception e) {
//            log.error("Spring Security Filter Chain Exception:", e);
//            resolver.resolveException(request, response, null, e);
//        }
//    }
//}

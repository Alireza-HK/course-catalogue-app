package com.example.catalogue.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.StringJoiner;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.example.catalogue.backend.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Object[] args = joinPoint.getArgs();
        StringJoiner params = new StringJoiner(", ", "(", ")");
        Arrays.stream(args).map(Object::toString).forEach(params::add);

        log.info("Before {}.{}() - Parameters: {}", className, methodName, params);
    }

    @AfterReturning(pointcut = "execution(* com.example.catalogue.backend.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("After {} is called. Result: {}", className + "." + methodName, result);
    }
}

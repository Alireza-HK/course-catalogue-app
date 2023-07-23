package com.example.catalogue.backend.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.StringJoiner;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.catalogue.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Object[] args = joinPoint.getArgs();
        StringJoiner params = new StringJoiner(", ", "(", ")");
        Arrays.stream(args).map(Object::toString).forEach(params::add);

        logger.info("Before {}.{}() - Parameters: {}", className, methodName, params);
    }

    @AfterReturning(pointcut = "execution(* com.example.catalogue.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        logger.info("After {} is called. Result: {}", className + "." + methodName, result);
    }
}

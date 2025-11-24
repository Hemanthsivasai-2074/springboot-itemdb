package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.demo.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("🔍 [AOP] Calling: " + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* com.example.demo.service.*.*(..))", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        System.out.println("✅ [AOP] Returned from: " + joinPoint.getSignature().getName() + " with result: " + result);
    }

    @AfterThrowing(pointcut = "execution(* com.example.demo.service.*.*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        System.out.println("❌ [AOP] Exception in: " + joinPoint.getSignature().getName() + " → " + ex.getMessage());
    }
}
package org.diarymoodanalyzer.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AOP based Logging class
 * <br/>
 * Watch method in `.service`, `.controller`
 * and log method name, execution time, exception.
 * <br/>
 * If you want to ignore AOP logging on specific class,
 * using {@link org.diarymoodanalyzer.annotation.SkipLogging @SkipLogging} annotation on it.
 */
@Aspect
@Component
public class LoggingAspect {

    // Get Logger
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Watch Service methods and Controller methods
    // Exclude Class with annotation `SkipLogging`
    @Around("( execution(* org.diarymoodanalyzer.service..*(..)) || " +
            "execution(* org.diarymoodanalyzer.controller..*(..)) ) " +
            "&& !@within(org.diarymoodanalyzer.annotation.SkipLogging)")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get target method signature from `ProceedingJoinPoint`
        String methodName = joinPoint.getSignature().toShortString();

        // Log method entering
        logger.info("Enter {}", methodName);

        // To measure execution time
        long start = System.currentTimeMillis();

        try {
            // Proceed target method (invoke target method)
            Object result = joinPoint.proceed();

            long end = System.currentTimeMillis();

            // Log method complete and execution time
            logger.info("Complete {} in {} ms", methodName, (end - start));

            return result;
        } catch (Exception e) {
            // Log Exception and propagation it
            logger.error("Exception in {}", methodName, e);
            throw e;
        }
    }
}

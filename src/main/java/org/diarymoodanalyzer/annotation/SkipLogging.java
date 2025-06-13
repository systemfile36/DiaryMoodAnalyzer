package org.diarymoodanalyzer.annotation;

import org.diarymoodanalyzer.aop.LoggingAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for skip AOP logging.
 * <br/>
 * {@link LoggingAspect LoggingAspect} will ignore class with this annotation
 * <br/>
 * Use this on the class include asynchronous method like <code>@Async</code>, <code>@Scheduled</code>.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipLogging {}


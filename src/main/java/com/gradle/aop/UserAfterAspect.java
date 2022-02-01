/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.aop;


import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.Arrays;



@Aspect
public class UserAfterAspect {
    public static final Logger logger = Logger.getLogger(UserAfterAspect.class);

    /**
     * @param joinPoint A joinpoint is a candidate point in the Program Execution of the application where an
     *                  aspect can be plugged in. This point could be a method being called, an exception being thrown,
     *                  or even a field being modified.
     */
    // This method will get called everytime before callin any method from class Alerts
    @Before("execution(* com.gradle.util.Alerts.*(..))")
    public void getNameAdvice(JoinPoint joinPoint) {
        logger.debug("Method Name :" + joinPoint.getSignature().toShortString() + "| Args => " + Arrays.asList(joinPoint.getArgs()));
    }



    /**
     * @param joinPoint A joinpoint is a candidate point in the Program Execution of the application where an
     *                  aspect can be plugged in. This point could be a method being called, an exception being thrown,
     *                  or even a field being modified.
     */
    // This method will get called everytime before callin saveOrUpdate method in UserService
    @Before("execution(* com.gradle.services.iface.user.UserService.saveOrUpdate(..))")
    public void setPassword(JoinPoint joinPoint) {
        logger.debug("Method Name :" + joinPoint.getSignature().toShortString() + "| Args => " + Arrays.asList(joinPoint.getArgs()));
    }




    /**
     * @param joinPoint A joinpoint is a candidate point in the Program Execution of the application where an
     *                  aspect can be plugged in. This point could be a method being called, an exception being thrown,
     *                  or even a field being modified.(Here joinpoint will be an exception
     */
    //Any kind of exception in User model will be logged here
    @AfterThrowing("within(com.gradle.entity.user.User)")
    public void logExceptions(JoinPoint joinPoint) {
        logger.error(joinPoint.toString());
    }


    /**
     * @param joinPoint A joinpoint is a candidate point in the Program Execution of the application where an
     *                  aspect can be plugged in. This point could be a method being called, an exception being thrown,
     *                  or even a field being modified.(Here joinpoint will be an exception
     */
    //Any kind of exception in User model will be logged here
    /*@Before("execution(* com.gradle.util.ServiceUtil.*(..))")
    public void serviceUtilCalled(JoinPoint joinPoint) {
        logger.error(joinPoint.toString());
    }*/


    /**
     * @param joinPoint A joinpoint is a candidate point in the Program Execution of the application where an
     *                  aspect can be plugged in. This point could be a method being called, an exception being thrown,
     *                  or even a field being modified.(Here joinpoint will be an exception
     */
    //Any kind of exception in User model will be logged here
    @AfterThrowing("execution(* com.gradle.util.ServiceUtil.*(..))")
    public void serviceUtilException(JoinPoint joinPoint) {
        logger.error("Method Name :" + joinPoint.getSignature().toShortString() + "| Args => " + Arrays.asList(joinPoint.getArgs()));
    }


    /**
     * @param returnString will return username
     */
    // Logs whenever getUserName method called
    @AfterReturning(pointcut = "execution(* getUserName())", returning = "returnString")
    public void getNameReturningAdvice(String returnString) {
        logger.debug("getUserName method executed and return value is : " + returnString);
    }
}

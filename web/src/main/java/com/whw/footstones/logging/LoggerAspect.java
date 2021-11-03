package com.whw.footstones.logging;import com.alibaba.fastjson.JSON;import lombok.extern.slf4j.Slf4j;import org.apache.commons.lang3.StringUtils;import org.aspectj.lang.ProceedingJoinPoint;import org.aspectj.lang.annotation.AfterThrowing;import org.aspectj.lang.annotation.Around;import org.aspectj.lang.annotation.Aspect;import org.aspectj.lang.annotation.Pointcut;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.stereotype.Component;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;/** * @author * @version 1.0 * @description: TODO * @date 2021/9/29 7:20 PM */@Aspect@Slf4j@Componentpublic class LoggerAspect {    private static Logger logger = LoggerFactory.getLogger(LoggerAspect.class);    @Pointcut("execution(* com.whw.footstones.controller.*.*(..))")    private void controllerPointCut() {    }    @Around("controllerPointCut()||thirdApiClientPointCut()")    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {        String method = null;        try {            method = pjp.getSignature().toString();            String parameter = null;            Object[] args = pjp.getArgs();            for (int i = 0; i < args.length; i++) {                Object arg = args[i];                if (!isServletRequestOrResponse(arg.getClass())) {                    parameter = JSON.toJSONString(arg);                    break;                }            }            logger.info("start method:{},parameter:{}", method, parameter);        } catch (Exception ex) {            //ignore ex        }        Object retVal = pjp.proceed();        try {            String result = StringUtils.substring(JSON.toJSONString(retVal), 0, 100);            logger.info("end method:{},result:{}", method, result);        } catch (Exception ex) {            //ignore ex        }        return retVal;    }    private boolean isServletRequestOrResponse(Class<?> clazz) {        return HttpServletRequest.class.isAssignableFrom(clazz) || HttpServletResponse.class.isAssignableFrom(clazz);    }    @AfterThrowing(            pointcut = "controllerPointCut()",            throwing = "ex")    public void doAfterThrowing(Exception ex) {        logger.error("error.", ex);    }    @Pointcut("execution(* com.whw.footstones..dao.*Dao.*(..))")    private void daoPointCut() {    }    @Pointcut("execution(* com.whw.footstones..service.*ServiceImpl.*(..))")    private void servicePointCut() {    }    @Pointcut("execution(* com.whw.footstones..service.third..*Client.*(..))")    private void thirdApiClientPointCut() {    }    @Around("controllerPointCut() || servicePointCut() || thirdApiClientPointCut() || daoPointCut() ")    public Object doAround2(ProceedingJoinPoint pjp) throws Throwable {        long start = System.currentTimeMillis();        Object retValue = pjp.proceed();        long end = System.currentTimeMillis();        logger.info("cost:{} ms", end - start);        return retValue;    }}
package com.whw.footstones.config.exception;

import com.whw.footstones.common.exception.ExceptionEnums;
import com.whw.footstones.core.message.Result;
import jodd.util.ArraysUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * 全局统一异常处理类
 *
 * @author zhanghao13
 * @since 2020/12/28 21:10
 */
@Slf4j
@RestControllerAdvice
public class ExceptionConfiguration {

    /**
     * 业务异常处理
     * 如支付失败
     *
     * @param e 异常对象
     * @return Ret
     */
//    @ExceptionHandler(ServiceException.class)
//    @ResponseBody
//    public Result handler(ServiceException e) {
//        log.error(e.getMessage(), e);
//        return Result.fail(e.getCode(), e.getMessage());
//    }

    /**
     * 业务异常处理
     * 如支付失败
     *
     * @param e 异常对象
     * @return Ret
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result handler(ServiceException e) {
        ExceptionEnums enums = e.getExceptionEnums();
        return Result.fail(enums.getCode(), enums.getMessage());
    }

    /**
     * 校验异常处理
     *
     * @param e 异常对象
     * @return Ret
     */
    @ExceptionHandler(VerifyException.class)
    @ResponseBody
    public Result handler(VerifyException e) {
        log.warn(e.getMessage(), e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 请求参数缺失异常处理
     * 如要 name 字段, 却传递 eg: Required String parameter 'name' is not present
     *
     * @param e 异常对象
     * @return Ret
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Result handler(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);

        return Result.fail("请求参数缺失: " + StringUtils.capitalize(e.getParameterName()));
    }

    /**
     * 请求参数类型不匹配异常处理
     * 如要 Integer 参数, 却传了字符串, 且无法转换为 Integer eg: Failed to convert value of type 'java.lang.String'
     * to
     * required type 'java.lang.Integer'; nested exception is java.lang.NumberFormatException: For
     * input string: "hello"
     *
     * @param e 异常对象
     * @return Result
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Result handler(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        Class<?> type = e.getRequiredType();
        if (type == null) {
            return Result.fail("请求参数类型不匹配");
        }
        String simpleName = type.getSimpleName();
        return Result.fail("请求参数类型不匹配, 需要 " + simpleName + ", 得到 " + e.getValue());
    }

    /**
     * 请求参数校验失败异常处理
     * 即 @Validated 校验失败
     *
     * @param e 异常对象
     * @return Result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result handler(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        if (fieldError == null) {
            return Result.fail("请求参数错误");
        }
        return Result.fail(fieldError.getField() + " " + fieldError.getDefaultMessage());
    }

    /**
     * 请求参数校验失败异常处理
     * 即 @Valid 校验失败
     *
     * @param e 异常对象
     * @return Ret
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Result handler(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        Iterator<ConstraintViolation<?>> iterator = violations.iterator();
        if (iterator.hasNext()) {
            ConstraintViolation<?> violation = iterator.next();
            return Result.fail(violation.getMessage());
        }
        return Result.fail(e.getMessage());
    }

    /**
     * 请求方法错误异常处理
     * 即 POST 接口, 请求时用了 GET 方法
     *
     * @param e 异常对象
     * @return Ret
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Result handler(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        String method = e.getMethod();
        String[] supportedMethods = e.getSupportedMethods();
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("此接口不支持 ")
                .append(method)
                .append(" 请求");
        if (supportedMethods.length != 0) {
            msgBuilder.append(", 支持的请求类型为 [")
                    .append(StringUtils.join(supportedMethods, ", "))
                    .append("]");
            return Result.fail(msgBuilder.toString());
        }
        return Result.fail(msgBuilder.toString());
    }

    /**
     * 请求体不可读错误异常处理
     * 如 要求接收一个 json 请求体, 但是未读取到
     *
     * @param e 异常对象
     * @return Ret
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Result handler(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return Result.fail("请求参数错误, 请检查请求参数");
    }

    /**
     * 默认异常处理
     *
     * @param e 异常对象
     * @return Ret
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handler(Exception e) {
        log.error(e.getMessage(), e);
        return Result.fail(e.getMessage());
    }

}

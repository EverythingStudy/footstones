package com.whw.footstones.config.exception;

import lombok.Getter;

/**
 * 校验异常类
 * 此异常阻止了异常栈追踪信息, 提高了性能, 避免抛出不必要的异常栈
 *
 * @author chenlinya
 */
@Getter
public class VerifyException extends RuntimeException {

    private static final long serialVersionUID = -3418676949916369533L;

    private final int code;

    private final String msg;

    /**
     * 仅记录异常信息, 不记录 cause by 和 stack trace, 提高性能
     *
     * @param msg 错误信息
     */
    public VerifyException(String msg) {
        this(1, msg);
    }


    /**
     * 仅记录异常信息, 不记录 cause by 和 stack trace, 提高性能
     *
     * @param code 错误码
     * @param msg  错误信息
     */
    public VerifyException(int code, String msg) {
        /*
          message  异常的描述信息，也就是在打印栈追踪信息时异常类名后面紧跟着的描述字符串
          cause    导致此异常发生的父异常，即追踪信息里的caused by
          enableSuppress   关于异常挂起的参数，这里我们永远设为false即可
          writableStackTrace   表示是否生成栈追踪信息，只要将此参数设为false,
                                则在构造异常对象时就不会调用fillInStackTrace()
         */
        super(msg, null, false, false);
        this.code = code;
        this.msg = msg;
    }

}

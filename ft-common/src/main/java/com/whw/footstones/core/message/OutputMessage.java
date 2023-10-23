package com.whw.footstones.core.message;

import com.whw.footstones.exception.ExceptionEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputMessage<T> implements OutputMessageType{

    public static final OutputMessage<Object> DEFAULT = OutputMessage.success(null);
    private Integer code;
    private String message;
    private T data;

    public static <T> OutputMessage<T> success(T data) {
        return new OutputMessage<T>(0, "成功！", data);
    }

    public static <T> OutputMessage<T> error(ExceptionEnums exceptionEnums) {
        return new OutputMessage<T>(exceptionEnums.getCode(), exceptionEnums.getMessage(), null);
    }

    public static <T> OutputMessage<T> error(Integer errorCode, String errorMessage) {
        return new OutputMessage<T>(errorCode, errorMessage, null);
    }


    public static OutputMessage error(ExceptionEnums error, Object data) {
        return new OutputMessage(error.getCode(), error.getMessage(), data);
    }

    @Override
    public Integer errorCode() {
        return null;
    }

    @Override
    public String errorMessage() {
        return null;
    }

    @Override
    public Object data() {
        return null;
    }
}

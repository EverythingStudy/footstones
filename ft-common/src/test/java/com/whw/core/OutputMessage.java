package com.whw.core;import com.whw.footstones.core.message.OutputMessageType;/** * @version 1.0 * @description: TODO * @date 2021/9/13 5:22 PM */public class OutputMessage<T> implements OutputMessageType {    public static final com.whw.footstones.core.message.OutputMessage DEFAULT = new com.whw.footstones.core.message.OutputMessage();    /**     * 错误码，为null或者0时表示返回成功     */    private Integer code;    /**     * 消息，错误提示/成功提示/警告等     */    private String message;    /**     * 数据     */    private T data;    public OutputMessage() {    }    public OutputMessage(Integer code, String message, T data) {        this.code = code;        this.message = message;        this.data = data;    }    public Integer getCode() {        return code;    }    public void setCode(Integer code) {        this.code = code;    }    public String getMessage() {        return message;    }    public void setMessage(String message) {        this.message = message;    }    public T getData() {        return data;    }    public void setData(T data) {        this.data = data;    }    @Override    public Object data() {        return this.data;    }    @Override    public Integer errorCode() {        return this.code;    }    @Override    public String errorMessage() {        return this.message;    }    public static com.whw.footstones.core.message.OutputMessage success(Object data) {        return new com.whw.footstones.core.message.OutputMessage(0, "成功！", data);    }    public static com.whw.footstones.core.message.OutputMessage error(Integer errorCode, String errorMessage) {        return new com.whw.footstones.core.message.OutputMessage(errorCode, errorMessage, null);    }//    public static OutputMessage error(ExceptionEnums error) {//        return new OutputMessage(error.getCode(), error.getMessage(), null);//    }////    public static OutputMessage error(ExceptionEnums error, Object data) {//        return new OutputMessage(error.getCode(), error.getMessage(), data);//    }}
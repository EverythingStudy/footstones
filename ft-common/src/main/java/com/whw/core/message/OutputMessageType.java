package com.whw.core.message;public interface OutputMessageType <T> extends ErrorMessageType{    /**     * 获取返回数据     * @return     */    T data();}
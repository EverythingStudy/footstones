package com.whw.footstones.core.message;public interface OutputMessageType <T> extends ErrorMessageType{    /**     * 获取返回数据     * @return     */    T data();}
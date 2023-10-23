package com.whw.footstones.core.message;

/**
 * @author hobo
 * @description 返回消息类型
 * @creatTime 2018/12/24 上午11:07
 * @since 1.0.0
 */
public interface OutputMessageType<T> extends ErrorMessageType{
    /**
     * 获取返回数据
     * @return
     */
    T data();
}

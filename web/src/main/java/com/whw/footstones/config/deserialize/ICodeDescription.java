/*
 * Copyright (c) 2018-2023.
 * All rights reserved.
 */

package com.whw.footstones.config.deserialize;

/**
 * @author
 * @description 编码和描述接口
 * @creatTime
 * @since 1.0.0
 */
public interface ICodeDescription {
    /**
     * 获取对应的编码
     *
     * @return
     */
    int getCode();

    /**
     * 获取对于键值的描述
     *
     * @return
     */
    String getDescription();

    /**
     * 通过code 查询
     *
     * @param enumClass
     * @param code
     * @param <T>
     * @return
     */
    static <T extends ICodeDescription> T getByCode(Class<T> enumClass, Integer code) {
        if (code == null) {
            return null;
        }
        return null;
    }
}

package com.whw.footstones.config.deserialize;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;

import java.io.IOException;
import java.util.Date;

/**
 * @author
 * @description
 * @creatTime 2018/9/13 下午3:34
 * @since 1.0.0
 */
public class CustomDateDeserializer extends DateDeserializers.DateDeserializer{
    /**
     * 1973/3/3 17:46:39
     */
    private static final long minSeconds = 99999999L;
    /**
     * 2286/11/21 1:46:39
     */
    private static final long maxSeconds = 9999999999L;
    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Date result = null;

        if (p.getCurrentTokenId() != JsonTokenId.ID_NUMBER_INT ) {
            return super.deserialize(p, ctxt);
        }
        //如果是数字
        {
            long ts;
            try {
                ts = p.getLongValue();
                //默认是毫秒，如果是秒 转化为毫秒
                if (minSeconds < ts && ts < maxSeconds) {
                    ts = ts * 1000;
                }

            } catch (JsonParseException e) {
                Number v = (Number) ctxt.handleWeirdNumberValue(_valueClass, p.getNumberValue(),
                        "not a valid 64-bit long for creating `java.util.Date`");
                ts = v.longValue();
            }
            result = new Date(ts);
        }
        return result;

    }
}

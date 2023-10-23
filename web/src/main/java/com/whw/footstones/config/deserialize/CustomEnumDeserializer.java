/*
 * Copyright (c) 2018-2023.
 * All rights reserved.
 */

package com.whw.footstones.config.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.util.CompactStringObjectMap;
import com.fasterxml.jackson.databind.util.EnumResolver;

import java.io.IOException;
import java.util.List;

/**
 * @author
 * @description 解析JsonDeserializer对应的枚举
 * @creatTime 2018/4/2 下午12:43
 * @since 1.0.0
 */
public class CustomEnumDeserializer extends EnumDeserializer {
    private Class<?> rawType;

    public CustomEnumDeserializer(EnumResolver byNameResolver, Boolean caseInsensitive, Class<?> rawType) {
        super(byNameResolver, caseInsensitive);
        this.rawType = rawType;
    }


    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken curr = p.getCurrentToken();
        Integer index = null;
        // Usually should just get string value:
        if (curr == JsonToken.VALUE_STRING || curr == JsonToken.FIELD_NAME) {
            CompactStringObjectMap lookup = ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
                    ? _getToStringLookup(ctxt) : _lookupByName;
            final String name = p.getText();
            try {
                index = Integer.parseInt(name);
            } catch (Exception e) {
            }
            Object result = lookup.find(name);
            if (result != null) {
                return result;
            }
        }
//        // But let's consider int acceptable as well (if within ordinal range)
        if (index != null || curr == JsonToken.VALUE_NUMBER_INT) {
            // ... unless told not to do that
            if (index == null) {
                index = p.getIntValue();
            }
            if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
                return ctxt.handleWeirdNumberValue(_enumClass(), index,
                        "not allowed to deserialize Enum value out of number: disable DeserializationConfig.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow"
                );
            }
            CompactStringObjectMap lookup = ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
                    ? _getToStringLookup(ctxt) : _lookupByName;
            List<String> keys = lookup.keys();
            for (String name : keys) {
                Object result = lookup.find(name);
                if (result instanceof ICodeDescription) {
                    if (((ICodeDescription) result).getCode() == index) {
                        return result;
                    }
                }
            }
            if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                return ctxt.handleWeirdNumberValue(_enumClass(), index,
                        "code value outside legal code range");
            }
            return null;
        }

        return null;
    }
}

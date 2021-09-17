package com.whw.footstones.core.util;import com.fasterxml.classmate.MemberResolver;import com.fasterxml.classmate.ResolvedType;import com.fasterxml.classmate.ResolvedTypeWithMembers;import com.fasterxml.classmate.TypeResolver;import com.fasterxml.classmate.members.ResolvedField;import org.apache.commons.lang3.ArrayUtils;import org.apache.commons.lang3.reflect.FieldUtils;import org.springframework.beans.BeanUtils;import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;import org.apache.commons.lang3.StringUtils;import java.beans.PropertyDescriptor;import java.lang.reflect.*;import java.util.*;/** * @version 1.0 * @description: TODO * @date 2021/9/13 7:54 PM */public class MockUtils {    private static Map<Class, Object> defaultValues = new HashMap<Class, Object>();    static  final TypeResolver TYPE_RESOLVER = new TypeResolver();    static {        defaultValues.put(byte.class, 1);        defaultValues.put(Byte.class, 1);        defaultValues.put(short.class, 1);        defaultValues.put(Short.class, 1);        defaultValues.put(int.class, 1);        defaultValues.put(Integer.class, 1);        defaultValues.put(long.class, 1L);        defaultValues.put(Long.class, 1L);        defaultValues.put(float.class, 1.5f);        defaultValues.put(Float.class, 1.5f);        defaultValues.put(double.class, 1.5d);        defaultValues.put(Double.class, 1.5d);        defaultValues.put(char.class, 'A');        defaultValues.put(Character.class, 'A');        defaultValues.put(boolean.class, false);        defaultValues.put(Boolean.class, false);        defaultValues.put(Object.class, new Object());        defaultValues.put(String.class, "string");        defaultValues.put(Date.class, new Date());    }    /**     * 任何指定type的bean自动mock     * @param type     * @return     */    public static Object mock(Type type){        Map<Class, Object> beanExists = new HashMap<>(4);        ResolvedType resolvedType = TYPE_RESOLVER.resolve(type);        return mock(resolvedType, beanExists);    }    private static Object mock(ResolvedType resolvedType, Map<Class, Object> beanExists) {        //普通类型/List/Map        Class clazz = resolvedType.getErasedType();        Object result = getDefaultValue(clazz);        if (result != null) {            return result;        }        if (List.class == clazz) {            result = new ArrayList<>(4);            ((List) result).add(mock(resolvedType.getTypeBindings().getTypeParameters().get(0), beanExists));            return result;        }        if (Map.class == clazz) {            result = new HashMap<>(4);            ResolvedType keyType = resolvedType.getTypeBindings().getTypeParameters().get(0);            ResolvedType valueType = resolvedType.getTypeBindings().getTypeParameters().get(1);            ((Map) result).put(mock(keyType, beanExists), mock(valueType, beanExists));            return result;        }        //数组        if (resolvedType.isArray()) {            Class elementType = resolvedType.getArrayElementType().getErasedType();            result =  Array.newInstance(elementType, 1);            Array.set(result, 0, mock(resolvedType.getArrayElementType(), beanExists));            return result;        }        if (!resolvedType.isConcrete() || resolvedType.isInterface()) {            return null;        }        result = initPojoDefaultValue(resolvedType, beanExists);        return result;    }    private static Object initPojoDefaultValue(ResolvedType resolvedType, Map<Class, Object> beanExists) {        Class clazz = resolvedType.getErasedType();        //避免jackson序列化时，自引用产生的循环依赖        if (beanExists.containsKey(clazz)) {            try {                Object value = clazz.newInstance();                BeanUtils.copyProperties(beanExists.get(clazz), value);                return value;            } catch (Exception ex) {                throw new RuntimeException(ex.getMessage(), ex);            }        }        final Object result;        try {            result = clazz.newInstance();            beanExists.put(clazz, result);        } catch (Exception ex) {            throw new RuntimeException(ex.getMessage(), ex);        }        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(clazz);        if (ArrayUtils.isEmpty(propertyDescriptors)) {            return result;        }        Arrays.stream(propertyDescriptors).forEach(e -> {            Method method = e.getWriteMethod();            if (method == null) {                return;            }            try {                Field field = FieldUtils.getField(clazz, e.getName(),true);                MemberResolver resolver = new MemberResolver(TYPE_RESOLVER);                resolver.setIncludeLangObject(false);                ResolvedTypeWithMembers typeWithMembers = resolver.resolve(resolvedType, null, null);                ResolvedField[] resolvedFields = typeWithMembers.getMemberFields();                ResolvedType filedType  = getFiledType(resolvedFields, e.getName());                Object obj = getDefaultValueWithExample(filedType, field, beanExists);                method.invoke(result, obj);            } catch (Exception ex) {                throw new RuntimeException(ex.getMessage(), ex);            }        });        return result;    }    private static ResolvedType getFiledType(ResolvedField[] resolvedFields, String fileName){        Optional<ResolvedField> optional = Arrays.stream(resolvedFields).filter(e ->{            //简单判断            return e.getName().equals(fileName);        }).findFirst();        return optional.get().getType();    }    /**     * 优先取example，再取给定的默认值     *     * @param field     * @return     */    private static Object getDefaultValueWithExample(ResolvedType filedType, Field field, Map<Class, Object> beanExists) {        Object value = getDefaultValueByExample(field);        if (value != null) {            return value;        }        return mock(filedType, beanExists);    }    private static Type getType(Type dcClassType, Type type) {        HashMap<String,Type> typeParams = new HashMap<>(4);        if (dcClassType instanceof ParameterizedType) {            ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl)dcClassType;            TypeVariable<? extends Class<?>>[] typeVariables =parameterizedType.getRawType().getTypeParameters();            Type[] types = parameterizedType.getActualTypeArguments();            if (types != null) {                for (int i = 0; i < types.length; i++) {                    typeParams.put(typeVariables[i].getName(),types[i]);                }            }        }else if(dcClassType instanceof Class) {            Class clazz = (Class)dcClassType;        }        return typeParams.get(type.getTypeName());    }    /**     * 取得默认值     *     * @param clazz     * @param <T>     * @return     */    private static <T> T getDefaultValue(Class<T> clazz) {        T defaultValue = (T) defaultValues.get(clazz);        return defaultValue;    }    /**     * 从 ApiModelProperty 中取得默认值     *     * @param field     * @return     */    private static Object getDefaultValueByExample(Field field) {        Object defaultValue = null;//        ApiModelProperty apiModelProperty = null;//        apiModelProperty = field.getAnnotation(ApiModelProperty.class);//        if (apiModelProperty == null) {//            return null;//        }//        if (StringUtils.isBlank(apiModelProperty.example())) {//            return null;//        }//        Class type = field.getType();//        try {//            Constructor constructor = type.getConstructor(String.class);//            defaultValue = constructor.newInstance(apiModelProperty.example());//        } catch (Exception e) {//            //不做任何处理//        }        return defaultValue;    }}
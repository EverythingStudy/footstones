/*
 * Copyright (c) 2018-2023.
 * All rights reserved.
 */

package com.whw.footstones.config;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.whw.footstones.config.deserialize.CustomEnumDeserializer;
import com.whw.footstones.core.message.converter.FrameJackson2HttpMessageConverter;
import com.whw.footstones.exception.ExceptionEnums;
import com.whw.footstones.log.LogFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置请求参数，响应体，日志等
 *
 * @author
 * @email
 * @creatTime 2018/3/27 上午10:03
 * @since 1.0.0
 */
@Configuration
public class FrameConfig {
    public static final DeserializerFactory DESERIALIZERF_ACTORY = BeanDeserializerFactory.instance.withAdditionalDeserializers(JacksonDes.JACKSON_DES.setupEnumDeserializer());
    public static final DefaultDeserializationContext.Impl DESERIALIZATION_CONTEXT = new DefaultDeserializationContext.Impl(DESERIALIZERF_ACTORY);
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(null, null, DESERIALIZATION_CONTEXT);

    static {
        OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OBJECT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        OBJECT_MAPPER.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        //TODO
//      OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule module = new SimpleModule();
        //module.addSerializer(ICodeDescription.class, new CodeDescriptionSerializer());
        //module.addDeserializer(Date.class,new CustomDateDeserializer());
        OBJECT_MAPPER.registerModule(module);
    }

    @Value("${frame.message.input.enabled:true}")
    private boolean isInputMessage;

    /**
     * 注册message-converter
     * 1.自动转换驼峰为snake形式
     * 2.枚举自动适配name+code
     * <p>
     * 注意：HttpMessageConverters 会被feign、restTemplate 依赖
     *
     * @return
     * @see
     * @see
     */
    @Bean
    public HttpMessageConverters jackson() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new FrameJackson2HttpMessageConverter(isInputMessage, OBJECT_MAPPER);
        return new HttpMessageConverters(mappingJackson2HttpMessageConverter);
    }

    /**
     * 分布式全局ID生成
     *
     * @return
     */
//    //@Bean
//    // public IdGenerateService idGenerateService() {
//        return new
//
//    IdGenerateService();
//
//}

    /**
     * 返回体封装
     *
     * @return
     */
//    @Bean
//    @ConditionalOnProperty(name = "frame.message.output.type", havingValue = "v3", matchIfMissing = true)
//    public ResponseAdvisor responseAdvisor() {
//        return new ResponseAdvisor();
//    }

    /**
     * 异常处理
     *
     * @return
     */
    //@Bean
//   // @ConditionalOnProperty(name = "frame.message.output.type",havingValue = "v3", matchIfMissing = true)
//    //public ExceptionAdvisor exceptionAdvisor() {
//        return new ExceptionAdvisor();
//    }
//
//
//    @ConditionalOnProperty(name = "earth.frame.access.log.enable", havingValue = "true", matchIfMissing = true)
//    @Bean
//    public FilterRegistrationBean<Filter> createAccessLogFilter(@Autowired AccessLogFilter accessLogFilter) {
//        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
//        registrationBean.setFilter(accessLogFilter);
//        registrationBean.addUrlPatterns("/*");
//        registrationBean.setName(AccessLogFilter.class.getSimpleName());
//        // 比trace的filter优先级低
//        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
//        registrationBean.setEnabled(true);
//        // 传入初始化时的参数
//        // registrationBean.addInitParameter("", "");
//        return registrationBean;
//    }
//
//    @Lazy
//    @Bean
//    public AccessLogFilter crearteAccessLogFilter() {
//        return new AccessLogFilter();
//    }
//
//    @Bean
//    @ConditionalOnProperty(name = "management.health.defaults.enable", havingValue = "true", matchIfMissing = true)
//    public ActuatorHealthCloseAspect createActuatorHealthCloseAspect() {
//        return new ActuatorHealthCloseAspect();
//    }
//
//    @Bean
//    public TimelinePointAspect createTimelinePointAspect() {
//        return new TimelinePointAspect();
//    }
//
//
    public static class JacksonDes extends BeanDeserializerFactory {
        public final static JacksonDes JACKSON_DES = new JacksonDes(new DeserializerFactoryConfig());

        public JacksonDes(DeserializerFactoryConfig config) {
            super(config);
        }

        public Deserializers setupEnumDeserializer() {
            Deserializers d = new Deserializers.Base() {


                @Override
                public JsonDeserializer<?> findEnumDeserializer(Class<?> type,
                                                                DeserializationConfig config, BeanDescription beanDesc)
                        throws JsonMappingException {

                    boolean isContains = isEnable(type);
                    if (!isContains) {
                        return null;
                    }
                    Class<?> rawClass = beanDesc.getType().getRawClass();
                    if (beanDesc.getType().getContentType() != null) {
                        rawClass = beanDesc.getType().getContentType().getRawClass();
                    }
                    return new CustomEnumDeserializer(constructEnumResolver(type,
                            config, beanDesc.findJsonValueAccessor()),
                            config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS), rawClass);
                }
            };
            return d;
        }


    }

    //
//    /**
//     * 是否试用
//     *
//     * @param type
//     * @return
//     */
    private static boolean isEnable(Class<?> type) {
        AnnotatedType[] annotatedTypes = type.getAnnotatedInterfaces();
        if (annotatedTypes == null || annotatedTypes.length == 0) {
            return false;
        }
        for (AnnotatedType annotatedType : annotatedTypes) {
            if (annotatedType.getType() == ExceptionEnums.class
            ) {
                return true;
            }
        }
        return false;
    }

//    @Autowired
//    private List<String> ignoresUri;

//    @Bean
//    public FilterRegistrationBean logFilterRegistrationBean() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new LogFilter(new ArrayList<>()));
//        registration.addUrlPatterns(new String[]{"/*"});
//        registration.setName("logFilter");
//        registration.setOrder(1);
//        return registration;
//    }
}

package com.whw.footstones.core.annotation;import java.lang.annotation.*;/** * @author * @description 标识是否需要mock返回值, 作用在controller层 * @creatTime * @since */@Target({ElementType.TYPE, ElementType.METHOD})@Retention(RetentionPolicy.RUNTIME)@Documentedpublic @interface MockReturn {}
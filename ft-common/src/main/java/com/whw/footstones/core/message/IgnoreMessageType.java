package com.whw.footstones.core.message;import java.lang.annotation.*;@Target(ElementType.TYPE)@Retention(RetentionPolicy.RUNTIME)@Documented@IgnoreInputMessageType@IgnoreOutputMessageTypepublic @interface IgnoreMessageType {}
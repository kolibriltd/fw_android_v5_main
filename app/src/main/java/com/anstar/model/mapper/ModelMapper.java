package com.anstar.model.mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModelMapper {

	String JsonKey() default "";

	boolean IsUnique() default false;

	boolean IsArray() default false;
}

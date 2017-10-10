/*
 * Copyright (c) 2011 Carman Consulting, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package name.berries.wicket.reflection.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.wicket.markup.html.form.FormComponent;

import name.berries.wicket.reflection.JavaTypeEnum;
import name.berries.wicket.reflection.UnknownComponent;
import name.berries.wicket.reflection.WicketTypeEnum;

/**
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SuppressWarnings("javadoc")
public @interface FieldType {

	JavaTypeEnum value() default JavaTypeEnum.Unknown;

	@SuppressWarnings("rawtypes")
	Class<? extends FormComponent> fieldFormComponentClass() default UnknownComponent.class;

	WicketTypeEnum htmlType() default WicketTypeEnum.Unknown;

	String propertyExpression() default "";
}

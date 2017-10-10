/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.berries.wicket.reflection;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.string.Strings;

import name.berries.wicket.reflection.annotations.ClassFieldsPropertyMapping;
import name.berries.wicket.reflection.annotations.FieldType;
import name.berries.wicket.reflection.annotations.GenericsClass;


/**
 * @author rozkovec
 */
public class FieldWrapper implements Serializable
{
	private static final long serialVersionUID = -2244305853993851880L;

	private Field field;
	private JavaTypeEnum javaType;
	private WicketTypeEnum htmlType;
	private String resourceNamespace;

	/**
	 * Construct.
	 *
	 * @param field
	 * @param javaType
	 * @param htmlType
	 */
	public FieldWrapper(Field field, JavaTypeEnum javaType, WicketTypeEnum htmlType)
	{
		super();
		this.field = field;
		this.javaType = javaType;
		this.htmlType = htmlType;
		resourceNamespace = ReflectionUtil.getClassNamespace(field.getDeclaringClass());
	}

	/**
	 * Gets field.
	 *
	 * @return field
	 */
	public Field getField()
	{
		return field;
	}

	/**
	 * Gets generics.
	 *
	 * @return javaType
	 */
	public String getGenerics()
	{
		String generics = javaType.name();
		if (JavaTypeEnum.Unknown.equals(javaType))
			generics = StringUtils.capitalize(field.getType().getSimpleName());
		if (JavaTypeEnum.Object.equals(javaType))
			generics = StringUtils.capitalize(field.getType().getSimpleName());

		GenericsClass annotation = field.getAnnotation(GenericsClass.class);
		if (annotation != null)
		{
			Class<?> value = annotation.value();

			// No generics for void class
			if (Void.class.equals(value))
				return "";

			generics = value.getSimpleName();
		}
		else
		{
			switch (htmlType)
			{
				case DateTextField :
				case PasswordTextField :
				case CheckBox :
				case FileUploadField :
					return "";
			}
			switch (javaType)
			{
				case Text :
					generics = JavaTypeEnum.String.name();
					break;
				case Enum :
					generics = getEnumerationClass();
					break;

				default :
					break;
			}
		}
		return String.format("<%s>", generics);
	}

	/**
	 * Gets javaType.
	 *
	 * @return javaType
	 */
	public String getParentClassName()
	{
		return field.getDeclaringClass().getSimpleName();
	}

	/**
	 * Gets javaType as declared in class.
	 *
	 * @return javaType
	 */
	public JavaTypeEnum getJavaType()
	{
		return javaType;
	}

	/**
	 * Gets htmlType enum - the Wicket component to use.
	 *
	 * @return htmlType
	 */
	public WicketTypeEnum getHtmlType()
	{
		return htmlType;
	}

	/**
	 * Returns component string to use in HTML file
	 *
	 * @return html string
	 */
	public String getHtmlString()
	{
		return String.format(getHtmlType().getHtmlComponent(), getPropertyExpression());
	}

	/**
	 * @return html string
	 */
	public String getWicketComponent()
	{
		FieldType annotation = field.getAnnotation(FieldType.class);
		if (annotation != null)
		{
			Class<? extends FormComponent> formComponentClass = annotation
				.fieldFormComponentClass();

			if (UnknownComponent.class.equals(formComponentClass) == false)
			{
				return formComponentClass.getSimpleName();
			}
		}
		return getHtmlType().getWicketComponent();
	}

	/**
	 * @return field name
	 */
	public String getName()
	{
		return field.getName();
	}

	/**
	 * @return field name
	 */
	public String getPropertyExpression()
	{
		FieldType annotation = field.getAnnotation(FieldType.class);
		if (annotation != null && Strings.isEmpty(annotation.propertyExpression()) == false)
		{
			return annotation.propertyExpression();
		}
		else
		{
			ClassFieldsPropertyMapping propertyAnnotation = field.getDeclaringClass()
				.getAnnotation(ClassFieldsPropertyMapping.class);
			if (propertyAnnotation != null)
			{
				return String.format("%s.%s", propertyAnnotation.value(), field.getName());
			}
		}
		return getName();
	}

	/**
	 * @return field name with first letter capitalized
	 */
	public String getNameCapitalized()
	{
		return StringUtils.capitalize(getName());
	}

	/**
	 * @return resource namespace
	 */
	public String getResourceNamespace()
	{
		return resourceNamespace;
	}

	/**
	 * @return resource key for this field
	 */
	public String getResourceKey()
	{
		return resourceNamespace + "." + getName();
	}

	/**
	 * @return resource help key for this field
	 */
	public String getResourceHelpKey()
	{
		return resourceNamespace + "." + getName() + ".help";
	}

	/**
	 * @return true if this field is enumeration
	 */
	public boolean isOfUnknownType()
	{
		return JavaTypeEnum.Unknown.equals(javaType);
	}

	/**
	 * @return true if this field is enumeration
	 */
	public boolean isEnumeration()
	{
		return JavaTypeEnum.Enum.equals(javaType) || field.getType().isEnum();
	}

	/**
	 * @return true if this field is unique
	 */
	public boolean isUnique()
	{
		Column annotation = field.getAnnotation(Column.class);
		if (annotation != null && annotation.unique())
		{
			return true;
		}
		return false;
	}

	/**
	 * @return true if this field is enumeration
	 */
	public boolean isRequired()
	{
		Column annotation = field.getAnnotation(Column.class);
		if (annotation != null && annotation.nullable() == false)
		{
			return true;
		}
		return false;
	}

	/**
	 * @return true if this field is enumeration
	 */
	public String getEnumerationClass()
	{
		return field.getType().getSimpleName();
	}

}
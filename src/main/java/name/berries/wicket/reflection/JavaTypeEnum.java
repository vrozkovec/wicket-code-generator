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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.berries.wicket.reflection.annotations.FieldType;

/**
 * @author rozkovec
 */
public enum JavaTypeEnum {
	/** */
	Unknown,
	/** */
	Object,
	/** */
	String,
	/** Special type for TextArea */
	Text,
	/** */
	Date,
	/** */
	Integer,
	/** */
	BigDecimal,
	/** */
	Long,
	/** */
	Enum,
	/** */
	Set,
	/** */
	List,
	/** */
	File,
	/** */
	Boolean,
	/** */
	Double;

	private static final Logger logger = LoggerFactory.getLogger(JavaTypeEnum.class);

	/**
	 * @param field
	 * @return this
	 * @throws IllegalAccessException
	 */
	public static FieldWrapper getFieldWrapper(Field field)
	{
		java.lang.String fieldName = field.getType().getSimpleName();

		JavaTypeEnum javaType = null;
		WicketTypeEnum htmlType = null;

		FieldType typeAnnotation;
		if ((typeAnnotation = field.getAnnotation(FieldType.class)) != null)
		{
			javaType = typeAnnotation.value();
			htmlType = typeAnnotation.htmlType();
		}

		try
		{
			if (javaType == null || Unknown.equals(javaType))
			{
				javaType = JavaTypeEnum.valueOf(fieldName);

				// override to text
				Column columnAnno;
				if (String.equals(javaType) &&
					(columnAnno = field.getAnnotation(Column.class)) != null)
				{
					if (columnAnno.length() > 255)
					{
						javaType = Text;
					}
				}
			}
			if (htmlType == null || WicketTypeEnum.Unknown.equals(htmlType))
			{
				htmlType = WicketTypeEnum.get(javaType);
			}
			return new FieldWrapper(field, javaType, htmlType);

		}
		catch (IllegalArgumentException e)
		{
			// last resort - if all previous checks failed

			javaType = getElementsMap().get(fieldName);

			if (javaType == null)
			{
				// if field has Enumerated annotation, we use this information
				Enumerated enumAnno;
				if ((enumAnno = field.getAnnotation(Enumerated.class)) != null)
				{
					javaType = Enum;
				}
				else
				{
					ManyToOne manyToOneAnnotation;
					if ((manyToOneAnnotation = field.getAnnotation(ManyToOne.class)) != null)
					{
						javaType = Object;
					}
					else
					{
						javaType = Unknown;
						logger.warn("Unable to locate java type for field " + field);
					}
				}
			}


			if (htmlType == null)
			{
				htmlType = WicketTypeEnum.get(javaType);
				if (htmlType == null)
				{
					htmlType = WicketTypeEnum.Unknown;
				}
				logger.warn("Unable to create HTML component for java type " + javaType);
			}
			return new FieldWrapper(field, javaType, htmlType);
		}
	}

	/**
	 * last resort attempt
	 *
	 * @return field
	 */
	private static Map<String, JavaTypeEnum> getElementsMap()
	{
		HashMap<String, JavaTypeEnum> map = new HashMap<String, JavaTypeEnum>();
		map.put("double", Double);
		map.put("boolean", Boolean);
		map.put("int", Integer);
		map.put("long", Long);
		return map;
	}
}
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


/**
 * @author rozkovec
 */
public enum WicketTypeEnum {
	/** */
	Unknown,
	/** */
	WebMarkupContainer,
	/** */
	TextField,
	/** */
	PasswordTextField,
	/** */
	DateTextField,
	/** */
	TextArea,
	/** */
	DropDownChoice,
	/** */
	CheckBoxMultipleChoice,
	/** */
	ListMultipleChoice,
	/** */
	FileUploadField,
	/** */
	CheckBox;

	/**
	 * @param typeEnum
	 * @return this
	 */
	public static WicketTypeEnum get(JavaTypeEnum typeEnum)
	{
		switch (typeEnum)
		{
			case File :
				return FileUploadField;

			case Text :
				return TextArea;

			case String :
			case BigDecimal :
			case Double :
			case Integer :
			case Long :
				return TextField;

			case Date :
				return DateTextField;

			case List :
				return ListMultipleChoice;

			case Set :
			case Object :
			case Enum :
				return DropDownChoice;

			case Boolean :
				return CheckBox;

			case Unknown :
				return Unknown;
		}

		return null;
	}

	/**
	 * @return html component
	 */
	public String getHtmlComponent()
	{
		switch (this)
		{
			case WebMarkupContainer :
				return "<div wicket:id=\"%s\"></div>";

			case TextArea :
				return "<textarea wicket:id=\"%s\"></textarea>";

			case DropDownChoice :
			case ListMultipleChoice :
				return "<select wicket:id=\"%s\"></select>";

			case CheckBox :
				return "<input type=\"checkbox\" wicket:id=\"%s\" />";

			case FileUploadField :
				return "<input type=\"file\" wicket:id=\"%s\" />";

			case CheckBoxMultipleChoice :
				return "<span wicket:id=\"%s\"></span>";

			case DateTextField :
				return "<input type=\"text\" wicket:id=\"%s\"/>";

			case PasswordTextField :
				return "<input type=\"password\" wicket:id=\"%s\"/>";

			case Unknown :
				return "<div wicket:id=\"%s\"></div>";

			case TextField :
			default :
				return "<input type=\"text\" maxlength=\"250\" wicket:id=\"%s\"/>";
		}
	}

	/**
	 * @return html component
	 */
	public String getWicketComponent()
	{
		return name();
	}
}
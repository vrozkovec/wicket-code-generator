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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.berries.extensions.velocity.VelocityUtil;
import name.berries.wicket.reflection.annotations.ClassResourceNamespace;
import name.berries.wicket.reflection.annotations.IncludeField;
import name.berries.wicket.reflection.annotations.Order;
import name.berries.wicket.reflection.annotations.SkipField;


/**
 * @author rozkovec
 */
public class ReflectionUtil
{

	private static final Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);


	private static final String FORMAT_COMMON_PROPERTIES = "common.properties";
	private static final String FORMAT_MERGED_PROPERTIES = "merged.properties";

	private static final String FORMAT_EDIT_PANEL_HTML = "Edit%sPanel.html";
	private static final String FORMAT_EDIT_PANEL_JAVA = "Edit%sPanel.java";
	private static final String FORMAT_EDIT_PANEL_PROPERTIES = "Edit%sPanel.properties";

	private static final String FORMAT_LIST_PANEL_HTML = "List%sPanel.html";
	private static final String FORMAT_LIST_PANEL_JAVA = "List%sPanel.java";
	private static final String FORMAT_LIST_PANEL_ACTIONS_HTML = "List%sActionsPanel.html";
	private static final String FORMAT_LIST_PANEL_ACTIONS_JAVA = "List%sActionsPanel.java";

	private static final String FORMAT_VIEW_PANEL_HTML = "View%sPanel.html";
	private static final String FORMAT_VIEW_PANEL_JAVA = "View%sPanel.java";

	private static final String FORMAT_MODEL_JAVA = "%sModel.java";

	private static final String FORMAT_FACTORY_JAVA = "%sFactory.java";

	private String outputDirectoryPath;

	private Class<?> templateReferenceClass = ReflectionUtil.class;
	private String templatePath = "templates/bootstrap/horizontal";

	private ArrayList<FieldWrapper> filteredFields;

	private Class<?> domainClass;
	private String classNamespace;
	private String classSimpleName;

	private Class<?> outputReferenceClass;

	private String mergedProperties = "";


	private Class<?> factoryClazz;

	/**
	 * Construct.
	 *
	 * @param outputDirectoryPath
	 * @param outputReferenceClass
	 */
	public ReflectionUtil(String outputDirectoryPath, Class<?> outputReferenceClass)
	{
		this(outputDirectoryPath, outputReferenceClass, ReflectionTemplate.BOOTSTRAP_HORIZONTAL);
	}


	/**
	 * Construct.
	 *
	 * @param outputDirectoryPath
	 * @param outputReferenceClass
	 * @param template
	 */
	public ReflectionUtil(String outputDirectoryPath, Class<?> outputReferenceClass, ReflectionTemplate template)
	{
		super();
		this.outputDirectoryPath = outputDirectoryPath;
		this.outputReferenceClass = outputReferenceClass;
		templateReferenceClass = template.getReflectionClass();
		templatePath = template.getTemplatePath();
	}

	/**
	 * Construct.
	 *
	 * @param outputDirectoryPath
	 * @param outputReferenceClass
	 * @param templateReferenceClass
	 * @param templatePath
	 */
	public ReflectionUtil(String outputDirectoryPath, Class<?> outputReferenceClass, Class<?> templateReferenceClass,
		String templatePath)
	{
		super();
		this.outputDirectoryPath = outputDirectoryPath;
		this.outputReferenceClass = outputReferenceClass;
		this.templateReferenceClass = templateReferenceClass;
		this.templatePath = templatePath;
	}

	/**
	 * Sets where all generated files will be put.
	 *
	 * @param outputDirectoryPath
	 *            outputDirectoryPath
	 */
	public void setOutputDirectoryPath(String outputDirectoryPath)
	{
		this.outputDirectoryPath = outputDirectoryPath;
	}


	/**
	 * Sets where we should look for template files. Usage is the same as when using
	 * {@link PackageTextTemplate#PackageTextTemplate(Class, String)}, wth the difference that
	 * second param is only a path to directory.
	 *
	 * @param templateReferenceClass
	 *            templateReferenceClass
	 * @param templatePath
	 *            templatePath
	 */
	public void setTemplateReferenceClass(Class<?> templateReferenceClass, String templatePath)
	{
		this.templateReferenceClass = templateReferenceClass;
		this.templatePath = templatePath;
	}

	/**
	 * @param clazz
	 * @param outputDirectoryPath
	 */
	public void createComponents(Class<?> clazz)
	{
		createComponents(clazz, null);
	}

	/**
	 * @param clazz
	 * @param factoryClazz
	 * @param outputDirectoryPath
	 */
	public void createComponents(Class<?> clazz, Class<?> factoryClazz)
	{
		domainClass = clazz;
		this.factoryClazz = factoryClazz;
		classSimpleName = clazz.getSimpleName();
		classNamespace = getClassNamespace();


		// already was something generated
		if (filteredFields != null)
		{
			filteredFields.clear();
			filteredFields = null;
		}

		createWicketComponent(FORMAT_COMMON_PROPERTIES, false);
		File properties = getComponentFile(FORMAT_COMMON_PROPERTIES, false);
		try
		{
			properties.write(getCommonPropertiesString());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		createEditComponent();

		createListComponent();

		createViewComponent();

		createModelComponent();

		createFactoryComponent();
	}

	/*
	 * *********************************
	 *
	 * L I S T
	 */
	/**
	 * @param outputDirectoryPath
	 */
	private void createListComponent()
	{
		createWicketComponent(FORMAT_LIST_PANEL_JAVA);
		createWicketComponent(FORMAT_LIST_PANEL_HTML);
		createWicketComponent(FORMAT_LIST_PANEL_ACTIONS_JAVA);
		createWicketComponent(FORMAT_LIST_PANEL_ACTIONS_HTML);

		File java = getComponentFile(FORMAT_LIST_PANEL_JAVA);
		File html = getComponentFile(FORMAT_LIST_PANEL_HTML);
		File javaActions = getComponentFile(FORMAT_LIST_PANEL_ACTIONS_JAVA);
		File htmlActions = getComponentFile(FORMAT_LIST_PANEL_ACTIONS_HTML);

		try
		{
			java.write(getJavaListClass());
			html.write(getHtmlListFile());

			javaActions.write(getJavaListActionsClass());
			htmlActions.write(getHtmlListActionsFile());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return java form
	 */
	private String getJavaListClass()
	{
		Map<String, Object> map = newMap();
		map.put("actionKey", ResourceKey.GENERIC_ACTIONS_HEADER.key());
		map.put("actionValue", ResourceKey.GENERIC_ACTIONS_HEADER.val());

		map.put("javaComponent", getJavaComponent(FORMAT_LIST_PANEL_JAVA));
		map.put("javaComponentEdit", getJavaComponent(FORMAT_EDIT_PANEL_JAVA));

		map.put("actionsPanelComponent", getJavaComponent(FORMAT_LIST_PANEL_ACTIONS_JAVA));

		map.put("createRecordKey", ResourceKey.INSTANCE_CREATE_NEW.key(classSimpleName));
		map.put("createRecordDefaultValue", ResourceKey.INSTANCE_CREATE_NEW.val(classSimpleName));


		map.put("columns", getSortedAndFilteredFields(true));
		return VelocityUtil.evaluateVelocityTemplate(templatePath + "ListPanel.java.tmpl", map, templateReferenceClass);
	}

	/**
	 * @return java form
	 */
	private String getHtmlListFile()
	{
		Map<String, Object> map = newMap();
		return VelocityUtil.evaluateVelocityTemplate(templatePath + "ListPanel.html.tmpl", map, templateReferenceClass);
	}

	private String getJavaListActionsClass()
	{
		Map<String, Object> map = newMap();
		map.put("javaComponent", getJavaComponent(FORMAT_LIST_PANEL_ACTIONS_JAVA));
		map.put("javaComponentList", getJavaComponent(FORMAT_LIST_PANEL_JAVA));
		map.put("javaComponentEdit", getJavaComponent(FORMAT_EDIT_PANEL_JAVA));
		map.put("javaComponentView", getJavaComponent(FORMAT_VIEW_PANEL_JAVA));

		map.put("deletedRecordMessageKey", ResourceKey.MESSAGE_DELETED.key(classSimpleName));
		map.put("deletedRecordMessageDefaultValue", ResourceKey.MESSAGE_DELETED.val(classSimpleName));

		return VelocityUtil.evaluateVelocityTemplate(templatePath + "ListPanelActions.java.tmpl", map,
			templateReferenceClass);
	}

	private String getHtmlListActionsFile()
	{
		Map<String, Object> map = newMap();
		map.put("editKey", ResourceKey.GENERIC_EDIT.key());
		map.put("deleteKey", ResourceKey.GENERIC_DELETE.key());
		map.put("viewKey", ResourceKey.GENERIC_VIEW.key());

		return VelocityUtil.evaluateVelocityTemplate(templatePath + "ListPanelActions.html.tmpl", map,
			templateReferenceClass);
	}


	/*
	 * *********************************
	 *
	 * V I E W
	 */
	/**
	 * @param outputDirectoryPath
	 */
	private void createViewComponent()
	{
		createWicketComponent(FORMAT_VIEW_PANEL_HTML);
		createWicketComponent(FORMAT_VIEW_PANEL_JAVA);

		File java = getComponentFile(FORMAT_VIEW_PANEL_JAVA);
		File html = getComponentFile(FORMAT_VIEW_PANEL_HTML);

		try
		{
			java.write(getJavaViewClass());
			html.write(getHtmlViewFile());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return java form
	 */
	private String getJavaViewClass()
	{
		Map<String, Object> map = newMap();
		map.put("javaComponent", getJavaComponent(FORMAT_VIEW_PANEL_JAVA));
		map.put("javaComponentEdit", getJavaComponent(FORMAT_EDIT_PANEL_JAVA));
		map.put("javaComponentList", getJavaComponent(FORMAT_LIST_PANEL_JAVA));

		map.put("columns", getSortedAndFilteredFields(true));

		map.put("classHeaderKey", ResourceKey.CLASS_HEADER.key(classSimpleName));
		map.put("classHeaderDefaultValue", ResourceKey.CLASS_HEADER.val(classSimpleName));

		map.put("listRecordsKey", ResourceKey.INSTANCE_LIST.key(classSimpleName));
		map.put("listRecordsDefaultValue", ResourceKey.INSTANCE_LIST.val(classSimpleName));

		map.put("createRecordKey", ResourceKey.INSTANCE_CREATE_NEW.key(classSimpleName));
		map.put("createRecordDefaultValue", ResourceKey.INSTANCE_CREATE_NEW.val(classSimpleName));

		map.put("editRecordKey", ResourceKey.INSTANCE_EDIT.key(classSimpleName));
		map.put("editRecordDefaultValue", ResourceKey.INSTANCE_EDIT.val(classSimpleName));

		map.put("deleteRecordKey", ResourceKey.INSTANCE_DELETE.key(classSimpleName));
		map.put("deleteRecordDefaultValue", ResourceKey.INSTANCE_DELETE.val(classSimpleName));

		map.put("deletedRecordMessageKey", ResourceKey.MESSAGE_DELETED.key(classSimpleName));
		map.put("deletedRecordMessageDefaultValue", ResourceKey.MESSAGE_DELETED.val(classSimpleName));

		return VelocityUtil.evaluateVelocityTemplate(templatePath + "ViewPanel.java.tmpl", map, templateReferenceClass);
	}

	/**
	 * @return java form
	 */
	private String getHtmlViewFile()
	{
		Map<String, Object> map = newMap();
		map.put("columns", getSortedAndFilteredFields(true));
		return VelocityUtil.evaluateVelocityTemplate(templatePath + "ViewPanel.html.tmpl", map, templateReferenceClass);
	}

	/*
	 * *********************************
	 *
	 * M O D E L
	 */
	/**
	 * @param outputDirectoryPath
	 */
	private void createModelComponent()
	{
		createWicketComponent(FORMAT_MODEL_JAVA);

		File java = getComponentFile(FORMAT_MODEL_JAVA);

		try
		{
			java.write(getJavaModelClass());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return java form
	 */
	private String getJavaModelClass()
	{
		Map<String, Object> map = newMap();

		return VelocityUtil.evaluateVelocityTemplate(templatePath + "Model.java.tmpl", map, templateReferenceClass);
	}

	/*
	 * *********************************
	 *
	 * F A C T O R Y
	 */
	/**
	 * @param outputDirectoryPath
	 */
	private void createFactoryComponent()
	{
		createWicketComponent(FORMAT_FACTORY_JAVA);

		File java = getComponentFile(FORMAT_FACTORY_JAVA);

		try
		{
			java.write(getJavaFactoryClass());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return java form
	 */
	private String getJavaFactoryClass()
	{
		Map<String, Object> map = newMap();

		return VelocityUtil.evaluateVelocityTemplate(templatePath + "Factory.java.tmpl", map, templateReferenceClass);
	}

	/*
	 * *********************************
	 *
	 * E D I T
	 */

	/**
	 * @param outputDirectoryPath
	 */
	private void createEditComponent()
	{
		createWicketComponent(FORMAT_EDIT_PANEL_JAVA);
		createWicketComponent(FORMAT_EDIT_PANEL_HTML);
		createWicketComponent(FORMAT_EDIT_PANEL_PROPERTIES);

		File java = getComponentFile(FORMAT_EDIT_PANEL_JAVA);
		File html = getComponentFile(FORMAT_EDIT_PANEL_HTML);
		File properties = getComponentFile(FORMAT_EDIT_PANEL_PROPERTIES);
		try
		{

			java.write(getJavaEditClass());
			html.write(getHtmlEditFile());

			String props = getPropertiesString();
			properties.write(props);
			mergedProperties += props;
			mergedProperties += "\n\n";

		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

	}

	/**
	 * @param clazz
	 * @return java form
	 */
	private String getJavaEditClass()
	{
		Map<String, Object> map = newMap();


		map.put("javaComponent", getJavaComponent(FORMAT_EDIT_PANEL_JAVA));
		map.put("javaComponentList", getJavaComponent(FORMAT_LIST_PANEL_JAVA));

		map.put("classResourceKey", ResourceKey.CLASS_HEADER.key(classSimpleName));

		map.put("listRecordsKey", ResourceKey.INSTANCE_LIST.key(classSimpleName));
		map.put("listRecordsDefaultValue", ResourceKey.INSTANCE_LIST.val(classSimpleName));

		map.put("updateRecordKey", ResourceKey.INSTANCE_UPDATE.key(classSimpleName));
		map.put("updateRecordDefaultValue", ResourceKey.INSTANCE_UPDATE.val(classSimpleName));

		map.put("saveRecordKey", ResourceKey.INSTANCE_SAVE.key(classSimpleName));
		map.put("saveRecordDefaultValue", ResourceKey.INSTANCE_SAVE.val(classSimpleName));

		map.put("deleteRecordKey", ResourceKey.INSTANCE_DELETE.key(classSimpleName));
		map.put("deleteRecordDefaultValue", ResourceKey.INSTANCE_DELETE.val(classSimpleName));

		map.put("createdRecordMessageKey", ResourceKey.MESSAGE_SAVED.key(classSimpleName));
		map.put("createdRecordMessageDefaultValue", ResourceKey.MESSAGE_SAVED.val(classSimpleName));

		map.put("updatedRecordMessageKey", ResourceKey.MESSAGE_UPDATED.key(classSimpleName));
		map.put("updatedRecordMessageDefaultValue", ResourceKey.MESSAGE_UPDATED.val(classSimpleName));

		map.put("fields", getSortedAndFilteredFields(false));

		return VelocityUtil.evaluateVelocityTemplate(templatePath + "EditPanel.java.tmpl", map, templateReferenceClass);
	}

	/**
	 * @return java form
	 */
	private String getHtmlEditFile()
	{
		Map<String, Object> map = newMap();
		map.put("fields", getSortedAndFilteredFields(false));

		map.put("submitKey", ResourceKey.GENERIC_SUBMIT.key());
		map.put("submitValue", ResourceKey.GENERIC_SUBMIT.val());

		return VelocityUtil.evaluateVelocityTemplate(templatePath + "EditPanel.html.tmpl", map, templateReferenceClass);
	}

	/*
	 * *********************************************
	 *
	 * P R O P E R T I E S
	 */
	/**
	 * @param outputDirectoryPath
	 */
	public void createMergedProperties()
	{
		File properties = getComponentFile(Application.get().getClass().getSimpleName() + ".properties", false);
		try
		{
			properties.write(mergedProperties + "\n");
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

	}

	/**
	 * @param clazz
	 * @return string
	 */
	public String getPropertiesString()
	{
		String str = "#####################\n# ";
		str += classSimpleName;
		str += "\n#####################\n";

		str += "#labels used when creating / editing record\n";

		List<ResourceKey> instanceKeys = ResourceKey.getInstanceKeys();
		for (ResourceKey key : instanceKeys)
		{
			str += String.format("%s=%s\n", key.key(classSimpleName), key.val(classSimpleName));
		}

		str += "#---------------------\n";

		List<FieldWrapper> fields = getSortedAndFilteredFields(true, true);
		Collections.sort(fields, new Comparator<FieldWrapper>()
		{
			@Override
			public int compare(FieldWrapper o1, FieldWrapper o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});

		for (FieldWrapper wrapper : fields)
		{
			str += String.format("%s=%s\n", wrapper.getResourceKey(), wrapper.getNameCapitalized());
			str += String.format("%s=\n", wrapper.getResourceHelpKey());
		}

		return str;
	}

	/**
	 * @return string
	 */
	public String getCommonPropertiesString()
	{
		String str = "###########################\n# ";
		str += "\n# C O M M O N   P R O P S\n";
		str += "# --------------------------\n";
		str += "# place into aplication scope properties\n";
		str += "###########################\n";

		List<ResourceKey> genericKeys = ResourceKey.getGenericKeys();
		for (ResourceKey key : genericKeys)
		{
			str += String.format("%s=%s\n", key.key(), key.val());
		}
		str += "#---------------------\n";

		return str;
	}


	/**
	 * @return class resource namespace used in .properties, taken from
	 *         {@link ClassResourceNamespace} annotation
	 */
	public String getClassNamespace()
	{
		return getClassNamespace(domainClass);
	}

	/**
	 * @param domainClass
	 * @return class resource namespace used in .properties, taken from
	 *         {@link ClassResourceNamespace} annotation
	 */
	public static String getClassNamespace(Class<?> domainClass)
	{

		ClassResourceNamespace namespace = domainClass.getAnnotation(ClassResourceNamespace.class);
		if (namespace != null)
		{
			return namespace.value();
		}
		else
		{
			return domainClass.getName();
		}
	}


	/*
	 * *********************************************
	 *
	 * U T I L S
	 */
	/**
	 * @param fileFormatPattern
	 * @param outputDirectoryPath
	 */
	private void createWicketComponent(String fileFormatPattern)
	{
		createWicketComponent(fileFormatPattern, true);
	}

	/**
	 * @param fileFormatPattern
	 * @param inClassSubdirectory
	 * @param outputDirectoryPath
	 */
	private void createWicketComponent(String fileFormatPattern, boolean inClassSubdirectory)
	{
		File file;

		if (Strings.isEmpty(outputDirectoryPath) == false)
		{
			file = getComponentFile(fileFormatPattern, inClassSubdirectory);
			// file.mkdirs();
			if (file.exists())
			{
				// throw new IllegalStateException("Resource " + file.getAbsolutePath() +
				// " already exists!");
			}
		}
		else
		{
			throw new IllegalStateException("Path cannot be null.");
		}

		try
		{
			logger.info("Creating new component on path " + file.getAbsolutePath());
			file.getParentFolder().mkdirs();
			file.createNewFile();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private File getComponentFile(String formatPattern, boolean inClassSubdirectory)
	{
		if (inClassSubdirectory)
		{
			return new File(getFormattedPath(true, getFormattedPath(outputDirectoryPath, classSimpleName.toLowerCase()),
				String.format(formatPattern, domainClass.getSimpleName())));
		}
		return new File(
			getFormattedPath(true, outputDirectoryPath, String.format(formatPattern, domainClass.getSimpleName())));
	}

	private File getComponentFile(String formatPattern)
	{
		return getComponentFile(formatPattern, true);
	}

	private String getJavaComponent(String filenameFormatString)
	{
		return StringUtils.substringBeforeLast(String.format(filenameFormatString, domainClass.getSimpleName()), ".");
	}

	private String getEntityImport()
	{
		return String.format("import %s;", domainClass.getCanonicalName());
	}

	private Map<String, Object> newMap()
	{
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("package", outputReferenceClass.getPackage().getName() + "." + classSimpleName.toLowerCase());

		map.put("className", domainClass.getSimpleName());
		map.put("entityImport", getEntityImport());


		/***************************************************
		 * Factory
		 ****************************************************/
		if (factoryClazz != null)
		{
			map.put("factoryComponent", factoryClazz.getSimpleName());
			map.put("factoryImport", String.format("import %s;\n", factoryClazz.getCanonicalName()));
		}
		else
		{
			map.put("factoryComponent", getJavaComponent(FORMAT_FACTORY_JAVA));
			map.put("factoryImport", "");
		}


		/***************************************************
		 * Enums
		 ****************************************************/
		String imports = "";
		Set<Class<?>> importTypes = new HashSet<Class<?>>();
		List<FieldWrapper> fields = getSortedAndFilteredFields(false, false);
		for (FieldWrapper field : fields)
		{
			if (field.isEnumeration() || field.isOfUnknownType())
			{
				importTypes.add(field.getField().getType());
			}
		}
		for (Class<?> c : importTypes)
		{
			imports += String.format("import %s;\n", c.getCanonicalName());
		}

		map.put("requiredImports", imports);

		return map;
	}

	private List<FieldWrapper> getSortedAndFilteredFields(boolean viewMode)
	{
		return getSortedAndFilteredFields(viewMode, false);
	}

	private List<FieldWrapper> getSortedAndFilteredFields(boolean viewMode, boolean ignoreSkipViewAnnotation)
	{
		filteredFields = null;
		if (filteredFields == null)
		{
			filteredFields = new ArrayList<FieldWrapper>();

			Field[] declaredFields = domainClass.getDeclaredFields();
			List<Field> fields = Arrays.asList(declaredFields);

			boolean includeAnnotationPresent = false;
			for (Field field : fields)
			{
				if (field.getAnnotation(IncludeField.class) != null)
				{
					includeAnnotationPresent = true;
					break;
				}
			}


			for (Field field : fields)
			{

				SkipField skipAnnotation;
				if (ignoreSkipViewAnnotation == false && (skipAnnotation = field.getAnnotation(SkipField.class)) != null)
				{
					if (viewMode)
					{
						if (skipAnnotation.includeInView() == false)
						{
							continue;
						}
					}
					else
					{
						if (skipAnnotation.includeInEdit() == false)
						{
							continue;
						}
					}
				}
				if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()))
				{
					continue;
				}
				FieldWrapper wrapper = JavaTypeEnum.getFieldWrapper(field);

				// when annotation is present, only include fields with this annotation and no
				// others
				if (includeAnnotationPresent)
				{
					if (field.getAnnotation(IncludeField.class) != null)
					{
						filteredFields.add(wrapper);
					}
				}
				else
				{
					filteredFields.add(wrapper);
				}

			}

			Collections.sort(filteredFields, new Comparator<FieldWrapper>()
			{
				public int compare(FieldWrapper o1, FieldWrapper o2)
				{
					Order field1Order = o1.getField().getAnnotation(Order.class);
					Order field2Order = o2.getField().getAnnotation(Order.class);
					if (field1Order != null && field2Order != null)
					{
						if (field1Order.value() == field2Order.value())
							return 0;

						if (field1Order.value() < field2Order.value())
							return -1;

						return 1;

					}
					if (field1Order == null && field2Order == null)
					{
						return 0;
					}

					if (field1Order == null)
						return 1;

					return -1;
				}
			});
		}

		return filteredFields;
	}

	/**
	 * Naformatuje danou cestu tak, aby mela na konci lomitko.
	 *
	 * @param pathParts
	 * @return naformatovanou cestu
	 */
	public static String getFormattedPath(String... pathParts)
	{
		return getFormattedPath(false, pathParts);
	}

	/**
	 * Spoji jednotlive parametry do adresarove cesty.
	 *
	 * @param nolastSlash
	 *            pokud <code>true</code>, za posledni polozkou lomitko nebude
	 * @param pathParts
	 *            casti url adresy
	 *
	 * @return naformatovanou cestu
	 */
	public static String getFormattedPath(boolean nolastSlash, String... pathParts)
	{
		StringBuffer sb = new StringBuffer(50);

		for (int i = 0; i < pathParts.length; i++)
		{
			String path = pathParts[i];

			// start with the root
			if (i == 0)
			{
				sb.append("/");
			}

			// prevent multiple slashes
			path = StringUtils.stripStart(path, "/");
			path = StringUtils.stripEnd(path, "/");

			// kazda cast konci lomitkem, jen posledni cast nemusi
			if (!(i == pathParts.length - 1 && nolastSlash == true))
			{
				path = path.concat("/");
			}
			sb.append(path);
		}
		return sb.toString();
	}
}

// Map<String, Object> variables = new HashMap<String, Object>();
// variables.put("name", name);
//
// CharSequence relativeUrl = urlFor(new PackageResourceReference(MailTemplate.class,
// "resource.txt"), null);
// String href = getRequestCycle().getUrlRenderer().renderFullUrl(
// Url.parse(relativeUrl.toString()));
// variables.put("downloadLink", href);
//
// PackageTextTemplate template = new PackageTextTemplate(MailTemplate.class, "mail-template.tmpl");
// CharSequence templateHtml = template.asString(variables);
// updateResult(result, templateHtml, target);
// target.add(feedback);

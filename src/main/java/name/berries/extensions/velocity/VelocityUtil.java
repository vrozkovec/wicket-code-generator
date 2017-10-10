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
package name.berries.extensions.velocity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.wicket.core.util.resource.WebExternalResourceStream;
import org.apache.wicket.request.resource.ContextRelativeResourceReference;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.velocity.markup.html.VelocityPanel;


/**
 * Code borrowed from {@link VelocityPanel}.
 *
 * @author rozkovec
 */
public class VelocityUtil
{
	/**
	 * Evaluates the template and returns the result. Code borrowed from {@link VelocityPanel}
	 *
	 * @param templateFileName
	 * @param map
	 * @param templateReferenceClass
	 * @param templatePath
	 *
	 * @return the result of evaluating the velocity template
	 */
	public static String evaluateVelocityTemplate(String templatePath, Map<?, ?> map, Class<?> templateReferenceClass)
	{
		String evaluatedTemplate = null;

		PackageTextTemplate textTemplate = new PackageTextTemplate(templateReferenceClass, templatePath);

		String template = textTemplate.asString();
		StringReader reader = new StringReader(template);

		try
		{
			textTemplate.close();
			return evaluateVelocityTemplate(templatePath, map, reader);
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Evaluates the template and returns the result. Code borrowed from {@link VelocityPanel}
	 *
	 * @param templateFileName
	 * @param map
	 * @param templatePath
	 *
	 * @return the result of evaluating the velocity template
	 */
	public static String evaluateContextVelocityTemplate(String templateFileName, Map<?, ?> map, String templatePath)
	{
		ContextRelativeResourceReference resource = new ContextRelativeResourceReference(templatePath + templateFileName);
		WebExternalResourceStream res = (WebExternalResourceStream)resource.getResource().getResourceStream();

		try
		{
			return evaluateVelocityTemplate(templateFileName, map, new InputStreamReader(res.getInputStream()));
		}
		catch (ResourceStreamNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Evaluates the template and returns the result. Code borrowed from {@link VelocityPanel}
	 *
	 * @param map
	 * @param templateFile
	 *
	 * @return the result of evaluating the velocity template
	 */
	public static String evaluateFilesystemVelocityTemplate(Map<?, ?> map, File templateFile)
	{
		try
		{
			return evaluateVelocityTemplate(templateFile.getName(), map,
				new InputStreamReader(new FileInputStream(templateFile)));
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Evaluates the template and returns the result. Code borrowed from {@link VelocityPanel}
	 *
	 * @param logTag
	 * @param map
	 * @param reader
	 *
	 * @return the result of evaluating the velocity template
	 */
	public static String evaluateVelocityTemplate(String logTag, Map<?, ?> map, Reader reader)
	{
		String evaluatedTemplate = null;

		// create a Velocity context object using the model if set
		final VelocityContext ctx = new VelocityContext(map);

		// create a writer for capturing the Velocity output
		StringWriter writer = new StringWriter();

		// string to be used as the template name for log messages in case
		// of error
		try
		{
			// execute the velocity script and capture the output in writer
			Velocity.evaluate(ctx, writer, logTag, reader);

			// replace the tag's body the Velocity output
			evaluatedTemplate = writer.toString();

			return evaluatedTemplate;
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
		finally
		{
			IOUtils.closeQuietly(reader);
		}
	}
}

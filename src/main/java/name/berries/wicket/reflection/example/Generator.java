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
package name.berries.wicket.reflection.example;

import name.berries.wicket.reflection.ReflectionTemplate;
import name.berries.wicket.reflection.ReflectionUtil;
import name.berries.wicket.reflection.example.pojo.AnotherExamplePojo;
import name.berries.wicket.reflection.example.pojo.ExamplePojo;
import name.berries.wicket.reflection.example.pojo.gen.Hook;

/**
 * @author rozkovec
 */
public class Generator
{

	/**
	 * Construct.
	 */
	public Generator()
	{
		// set package where classes will be generated, for each passed entity, separate
		// subdirectory will be created
		String filesystemPathOfTheHookClass = "/speedy/dev/name.berries/code-generator/src/main/java/name/berries/wicket/reflection/example/pojo/gen";


		// select template
		ReflectionUtil ref = new ReflectionUtil(filesystemPathOfTheHookClass, Hook.class,
			ReflectionTemplate.BOOTSTRAP_HORIZONTAL);

		// add your classes
		ref.createComponents(ExamplePojo.class);
		ref.createComponents(AnotherExamplePojo.class);

		ref.createMergedProperties();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}
}

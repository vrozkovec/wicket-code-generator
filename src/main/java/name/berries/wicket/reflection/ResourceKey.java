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

import java.util.ArrayList;
import java.util.List;

/**
 * @author rozkovec
 */
public enum ResourceKey {

	/** */
	GENERIC_SUBMIT("form.submit", "Submit", true),
	/** */
	GENERIC_ACTIONS_HEADER("list.actions", "Actions", true),
	/** */
	GENERIC_EDIT("list.actions.edit", "Edit", true),
	/** */
	GENERIC_VIEW("list.actions.view", "View", true),
	/** */
	GENERIC_DELETE("list.actions.delete", "Delete", true),
	/** */
	GENERIC_LIST("list.actions.list", "List", true),
	/** */
	GENERIC_CREATE_NEW("list.actions.create", "Create", true),
	/** */
	CLASS_HEADER("%s.header", "%s"),
	/** */
	INSTANCE_LIST("%s.action.list", "List %s"),
	/** */
	INSTANCE_CREATE_NEW("%s.action.create", "Create new %s"),
	/** */
	INSTANCE_SAVE("%s.action.save", "Save %s"),
	/** */
	INSTANCE_EDIT("%s.action.edit", "Edit %s"),
	/** */
	INSTANCE_VIEW("%s.action.view", "View %s"),
	/** */
	INSTANCE_DELETE("%s.action.delete", "Delete %s"),
	/** */
	INSTANCE_UPDATE("%s.action.update", "Update %s"),
	/** */
	MESSAGE_UPDATED("%s.message.updated", "Updated %s"),
	/** */
	MESSAGE_SAVED("%s.message.saved", "Saved %s"),
	/** */
	MESSAGE_DELETED("%s.message.deleted", "Deleted %s");

	private String key;
	private String value;
	private boolean generic = false;


	private static String KEY_CLASS_INSTANCE_HEADER = "%s.%s.class";

	/**
	 * Construct.
	 * 
	 * @param key
	 * @param value
	 */
	private ResourceKey(String key, String value)
	{
		this.key = key;
		this.value = value;
	}


	/**
	 * Construct.
	 * 
	 * @param key
	 * @param value
	 * @param generic
	 */
	private ResourceKey(String key, String value, boolean generic)
	{
		this.key = key;
		this.value = value;
		this.generic = generic;
	}


	/**
	 * Gets key.
	 * 
	 * @return key
	 */
	public String key()
	{
		if (generic == false)
		{
			throw new IllegalArgumentException("You cannot use this method with instance keys");
		}

		return key;
	}

	/**
	 * Gets key.
	 * 
	 * @param classNamespace
	 * 
	 * @return key
	 */
	public String key(String classNamespace)
	{
		if (generic)
		{
			throw new IllegalArgumentException("You cannot use this method with generic keys");
		}

		return String.format(key, classNamespace);
	}

	/**
	 * Gets value.
	 * 
	 * @return value
	 */
	public String val()
	{
		if (generic == false)
		{
			throw new IllegalArgumentException("You cannot use this method with instance keys");
		}
		return value;
	}

	/**
	 * Gets value.
	 * 
	 * @param classNamespace
	 * @param instanceName
	 * 
	 * @return value
	 */
	public String val(String instanceName)
	{
		if (generic)
		{
			throw new IllegalArgumentException("You cannot use this method with generic keys");
		}
		return String.format(value, instanceName);
	}

	/**
	 * @return generic keys
	 */
	public static List<ResourceKey> getGenericKeys()
	{
		return getKeys(true);
	}

	/**
	 * @return instance keys
	 */
	public static List<ResourceKey> getInstanceKeys()
	{
		return getKeys(false);
	}

	private static List<ResourceKey> getKeys(boolean generic)
	{
		ArrayList<ResourceKey> list = new ArrayList<ResourceKey>();
		ResourceKey[] values = ResourceKey.values();
		for (ResourceKey key : values)
		{
			if (key.generic == generic)
			{
				list.add(key);
			}
		}
		return list;
	}


}

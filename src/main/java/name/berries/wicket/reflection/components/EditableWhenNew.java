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
package name.berries.wicket.reflection.components;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

/**
 * Traverses component tree up and checks if some parent implements {@link IModelUtils} interface.
 * If it does, enables the component that the behavior was added to only when creating new record.
 *
 * @author vit
 */
public class EditableWhenNew extends Behavior
{
	@Override
	public void onConfigure(Component component)
	{
		super.onConfigure(component);
		component.visitParents(MarkupContainer.class, new IVisitor<MarkupContainer, Void>()
		{
			@Override
			public void component(MarkupContainer c, IVisit<Void> r)
			{
				if (c instanceof IModelUtils<?>)
				{
					IModelUtils<?> util = (IModelUtils<?>)c;
					component.setEnabled(!util.isEditMode());
					r.stop();
				}
			}
		});
	}

	/**
	 * Simple factory method
	 * 
	 * @return instance
	 */
	public static EditableWhenNew get()
	{
		return new EditableWhenNew();
	}
}

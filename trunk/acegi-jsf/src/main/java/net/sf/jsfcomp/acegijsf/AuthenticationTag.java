/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jsfcomp.acegijsf;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

/**
 * @author Cagatay Civici
 * @author Jeroen Benckhuijsen
 * 
 * Tag class for the component
 */
public class AuthenticationTag extends UIComponentELTag {
	private ValueExpression operation = null;

	public void release() {
		super.release();
		operation = null;
	}

	protected void setProperties(UIComponent component) {
		super.setProperties(component);
		
		if(operation != null) {
			if (!this.operation.isLiteralText()) {
				component.setValueExpression("operation", this.operation);
			} else {
				component.getAttributes().put("operation",operation.getExpressionString());
			}
		}
	}

	public String getComponentType() {
		return Authentication.COMPONENT_TYPE;
	}

	public String getRendererType() {
		return null;
	}

	public ValueExpression getOperation() {
		return operation;
	}
	
	public void setOperation(ValueExpression operation) {
		this.operation = operation;
	}
}

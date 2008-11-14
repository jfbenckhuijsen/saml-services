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
 * @author Kenan Sevindik
 * @author Jeroen Benckhuijsen
 * 
 * Tag class for the component
 */
public class AuthorizeTag extends UIComponentELTag {
	private ValueExpression ifAllGranted = null;

	private ValueExpression ifAnyGranted = null;

	private ValueExpression ifNotGranted = null;

	public void release() {
		super.release();
		ifAllGranted = null;
		ifAnyGranted = null;
		ifNotGranted = null;
	}

	protected void setProperties(UIComponent component) {
		super.setProperties(component);

		if(ifAllGranted != null) {
			if (!this.ifAllGranted.isLiteralText()) {
				component.setValueExpression("ifAllGranted", this.ifAllGranted);
			} else {
				component.getAttributes().put("ifAllGranted",ifAllGranted.getExpressionString());
			}
		}
		
		if(ifAnyGranted != null) {
			if (!this.ifAnyGranted.isLiteralText()) {
				component.setValueExpression("ifAnyGranted", this.ifAnyGranted);
			} else {
				component.getAttributes().put("ifAnyGranted",ifAnyGranted.getExpressionString());
			}
		}
		
		if(ifNotGranted != null) {
			if (!this.ifNotGranted.isLiteralText()) {
				component.setValueExpression("ifNotGranted", this.ifNotGranted);
			} else {
				component.getAttributes().put("ifNotGranted",ifNotGranted.getExpressionString());
			}
		}
	}

	public String getComponentType() {
		return Authorize.COMPONENT_TYPE;
	}

	public String getRendererType() {
		return null;
	}

	public ValueExpression getIfAllGranted() {
		return ifAllGranted;
	}

	public void setIfAllGranted(ValueExpression ifAllGranted) {
		this.ifAllGranted = ifAllGranted;
	}

	public ValueExpression getIfAnyGranted() {
		return ifAnyGranted;
	}

	public void setIfAnyGranted(ValueExpression ifAnyGranted) {
		this.ifAnyGranted = ifAnyGranted;
	}

	public ValueExpression getIfNotGranted() {
		return ifNotGranted;
	}

	public void setIfNotGranted(ValueExpression ifNotGranted) {
		this.ifNotGranted = ifNotGranted;
	}
	/*
	 * public int doAfterBody() throws JspException { System.out.println("At
	 * After Body Value"); Authorize authorize = (Authorize)
	 * getComponentInstance(); UIComponent parent = authorize.getParent();
	 * String bodyContent = getBodyContent().getString().trim(); return
	 * getDoAfterBodyValue(); }
	 */
}
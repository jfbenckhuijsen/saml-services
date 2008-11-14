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

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * @author Cagatay Civici
 * @author Kenan Sevindik
 * @author Jeroen Benckhuijsen
 * 
 * Component that displayes user info
 */
public class Authentication extends UIComponentBase {

	public static final String COMPONENT_TYPE = "net.sf.jsfcomp.acegijsf.Authentication";

	public static final String COMPONENT_FAMILY = "net.sf.jsfcomp.acegijsf";

	private String operation;

	public Authentication() {
		super();
		setRendererType(null);
	}

	public void encodeBegin(FacesContext context) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String result = "Unsupported Authentication object operation...";
		if (getOperation().equals("username")) {
			result = AcegiJsfUtils.getRemoteUser();	
		} 
		
		writer.write(result);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getOperation() {
		if (this.operation != null) {
			return this.operation;
		}
		
		ValueExpression ve = getValueExpression("operation");
		if (ve != null) {
			String value = (String)ve.getValue(getFacesContext().getELContext());
			return value;
		} else {
			return this.operation;
		}
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Object saveState(FacesContext context) {
		Object values[] = new Object[2];
		values[0] = super.saveState(context);
		values[1] = operation;
		return values;
	}

	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.operation = (String) values[1];
	}
}
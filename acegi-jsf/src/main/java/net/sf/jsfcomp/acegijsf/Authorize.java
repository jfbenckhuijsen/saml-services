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
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

/**
 * @author Cagatay Civici
 * @author Kenan Sevindik
 * @author Jeroen Benckhuijsen
 * 
 * Component that controls the rendering of secured components
 */
public class Authorize extends UIComponentBase {
	public static final String COMPONENT_TYPE = "net.sf.jsfcomp.acegijsf.Authorize";

	public static final String COMPONENT_FAMILY = "net.sf.jsfcomp.acegijsf";

	private static final int GRANT_MODE_ALL = 1;

	private static final int GRANT_MODE_ANY = 2;

	private static final int GRANT_MODE_NOT = 3;

	private String ifAllGranted = null;

	private String ifAnyGranted = null;

	private String ifNotGranted = null;

	private IAuthenticationMode authenticationMode;

	public Authorize() {
		setRendererType(null);
	}

	public void encodeChildren(FacesContext context) throws IOException {
		if (isUserInRole(getRoles())) {
			for (UIComponent child : getChildren()) {
				encodeRecursive(context, child);
			}
		}
	}

	public boolean isUserInRole(String roles) {
		return getAuthenticationMode().isUserInRole(roles);
	}

	public IAuthenticationMode getAuthenticationMode() {
		if (authenticationMode == null) {
			if(getIfAllGranted() != null)
				authenticationMode = new AllAuthenticationMode();
			else if (getIfAnyGranted() != null)
				authenticationMode = new AnyAuthenticationMode();
			else if (getIfNotGranted() != null)
				authenticationMode = new NotAuthenticationMode();
			else //default
				authenticationMode = new AnyAuthenticationMode();
		}
		return authenticationMode;
	}

	public void setAuthenticationMode(IAuthenticationMode authenticationMode) {
		this.authenticationMode = authenticationMode;
	}

	public boolean getRendersChildren() {
		return true;
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	private String getRoles() {
		String roles = "";
		if (getIfAllGranted() != null)
			roles = getIfAllGranted();
		else if (getIfAnyGranted() != null)
			roles = getIfAnyGranted();
		else if (getIfNotGranted() != null)
			roles = getIfNotGranted();

		return roles;
	}

	protected void encodeRecursive(FacesContext context, UIComponent component)
			throws IOException {
		if (!component.isRendered())
			return;

		component.encodeBegin(context);
		if (component.getRendersChildren()) {
			component.encodeChildren(context);
		} else {
			for (UIComponent child : component.getChildren()) {
				encodeRecursive(context, child);
			}
		}
		component.encodeEnd(context);
	}

	public String getIfAllGranted() {
		if (this.ifAllGranted != null) {
			return this.ifAllGranted;
		}
		
		ValueExpression ve = getValueExpression("ifAllGranted");
		if (ve != null) {
			String value = (String)ve.getValue(getFacesContext().getELContext());
			return value;
		} else {
			return this.ifAllGranted;
		}
	}

	public void setIfAllGranted(String ifAllGranted) {
		this.ifAllGranted = ifAllGranted;
	}

	public String getIfAnyGranted() {
		if (this.ifAnyGranted != null) {
			return this.ifAnyGranted;
		}
		
		ValueExpression ve = getValueExpression("ifAnyGranted");
		if (ve != null) {
			String value = (String)ve.getValue(getFacesContext().getELContext());
			return value;
		} else {
			return this.ifAnyGranted;
		}
	}

	public void setIfAnyGranted(String ifAnyGranted) {
		this.ifAnyGranted = ifAnyGranted;
	}

	public String getIfNotGranted() {
		if (this.ifNotGranted != null) {
			return this.ifNotGranted;
		}
		
		ValueExpression ve = getValueExpression("ifNotGranted");
		if (ve != null) {
			String value = (String)ve.getValue(getFacesContext().getELContext());
			return value;
		} else {
			return this.ifNotGranted;
		}
	}

	public void setIfNotGranted(String ifNotGranted) {
		this.ifNotGranted = ifNotGranted;
	}

	public Object saveState(FacesContext context) {
		Object values[] = new Object[4];
		values[0] = super.saveState(context);
		values[1] = ifAllGranted;
		values[2] = ifAnyGranted;
		values[3] = ifNotGranted;
		return values;
	}

	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.ifAllGranted = (String) values[1];
		this.ifAnyGranted = (String) values[2];
		this.ifNotGranted = (String) values[3];
	}

}
/**
 * 
 */
package com.atosorigin.nl.saml.cas.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

/**
 * @author a108600
 *
 */
public class SamlProxyFailureResponseView  extends AbstractView {

	/**
	 * 
	 */
	public SamlProxyFailureResponseView() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(Map model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

}

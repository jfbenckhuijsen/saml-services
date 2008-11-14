/**
 * 
 */
package com.atosorigin.nl.saml.cas.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.inspektr.common.ioc.annotation.NotNull;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.principal.WebApplicationService;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.validation.Assertion;
import org.opensaml.SAMLAuthenticationQuery;
import org.opensaml.SAMLBinding;
import org.opensaml.SAMLRequest;
import org.opensaml.SAMLSOAPHTTPBinding;
import org.opensaml.SAMLSubject;
import org.opensaml.provider.SOAPHTTPBindingProvider;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * @author a108600
 *
 */
public class SamlController extends AbstractController {

    /** View if Service Ticket Validation Fails. */
    private static final String DEFAULT_SERVICE_FAILURE_VIEW_NAME = "casSamlProxyServiceFailureView";

    /** View if Service Ticket Validation Succeeds. */
    private static final String DEFAULT_SERVICE_SUCCESS_VIEW_NAME = "casSamlProxyServiceSuccessView";

	/** CORE to delegate all non-web tier functionality to. */
    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    /** The view to redirect to on a successful validation. */
    @NotNull
    private String successView = DEFAULT_SERVICE_SUCCESS_VIEW_NAME;

    /** The view to redirect to on a validation failure. */
    @NotNull
    private String failureView = DEFAULT_SERVICE_FAILURE_VIEW_NAME;

	/**
	 * 
	 */
	public SamlController() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		final SAMLSOAPHTTPBinding binding = new SOAPHTTPBindingProvider(SAMLBinding.SOAP, null);
		final SAMLRequest samlRequest = binding.receive(request, 0);
		
        ModelAndView mav;
		if (samlRequest.getQuery() instanceof SAMLAuthenticationQuery) {
			final SAMLAuthenticationQuery samlQuery = (SAMLAuthenticationQuery) samlRequest.getQuery();
			final SAMLSubject subject = samlQuery.getSubject();
			
			final String user = subject.getNameIdentifier().getName();
			final WebApplicationService targetService = SamlProxyWebApplicationService.createServiceFrom(subject);
			final String ticket = targetService.getArtifactId();
			
			try {
	        	if (StringUtils.hasText(ticket) && targetService != null) {
	        		Assertion assertion = this.centralAuthenticationService.validateServiceTicket(ticket, targetService);
	        		Map<String, Object> model = new HashMap<String, Object>();
	        		
	        		model.put("user", user);
	        		model.put("ticket", ticket);
	        		model.put("targetService", targetService.getId());
	        		model.put("assertion", assertion);
	        		model.put("requestId", samlRequest.getId());
	        		mav = new ModelAndView(successView, model);
	        	} else {
	        		mav = new ModelAndView(failureView);
	        	}
	        } catch (TicketException e) {
	        	mav = new ModelAndView(failureView);
	        }
		} else {
			mav = new ModelAndView(failureView);
		}
		        
		return mav;
	}

	/**
	 * @return the centralAuthenticationService
	 */
	public CentralAuthenticationService getCentralAuthenticationService() {
		return centralAuthenticationService;
	}

	/**
	 * @param centralAuthenticationService the centralAuthenticationService to set
	 */
	public void setCentralAuthenticationService(
			CentralAuthenticationService centralAuthenticationService) {
		this.centralAuthenticationService = centralAuthenticationService;
	}


}

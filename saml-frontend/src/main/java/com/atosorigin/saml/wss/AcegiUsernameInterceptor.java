/**
 * 
 */
package com.atosorigin.saml.wss;

import java.util.HashSet;
import java.util.Set;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

/**
 * @author a108600
 * 
 */
public class AcegiUsernameInterceptor implements PhaseInterceptor<Message> {

    private Set<String> before = new HashSet<String>();
    private Set<String> after = new HashSet<String>();
    private String id;
 
    /**
	 * 
	 */
	public AcegiUsernameInterceptor() {
		super();
		id = getClass().getName();
	}

	public void handleFault(Message msg) {
		// no op
	}

	public void handleMessage(Message msg) throws Fault {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		msg.put(WSHandlerConstants.USER, auth.getName());
	}

	public Set<String> getAfter() {
		return after;
	}

	public void setAfter(Set<String> after) {
		this.after = after;
	}

	public Set<String> getBefore() {
		return before;
	}

	public void setBefore(Set<String> before) {
		this.before = before;
	}

	public String getId() {
		return id;
	}

	public String getPhase() {
		return Phase.PRE_PROTOCOL;
	}

}

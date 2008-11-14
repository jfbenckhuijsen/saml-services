/**
 * 
 */
package com.atosorigin.saml.wss;

import java.util.List;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;

/**
 * @author a108600
 *
 */
public class InterceptorDecoratingFactoryBean {
	
	private ClientProxyFactoryBean proxyFactory;
	
	private List<Interceptor<Message>> inInterceptors;
	
	private List<Interceptor<Message>> outInterceptors;

	/**
	 * 
	 */
	public InterceptorDecoratingFactoryBean() {
		super();
	}
	
	/**
	 * @return
	 */
	public Object create() {
		Object proxy = proxyFactory.create();
		Client client = ClientProxy.getClient(proxy);
		Endpoint cxfEndpoint = client.getEndpoint(); 
		
		if (inInterceptors != null) {
			cxfEndpoint.getInInterceptors().addAll(this.inInterceptors);
		}
		
		if (outInterceptors != null) {
			cxfEndpoint.getOutInterceptors().addAll(this.outInterceptors);
		}
		
		return proxy;
	}

	/**
	 * @return the proxyFactory
	 */
	public ClientProxyFactoryBean getProxyFactory() {
		return proxyFactory;
	}

	/**
	 * @param proxyFactory the proxyFactory to set
	 */
	public void setProxyFactory(ClientProxyFactoryBean proxyFactory) {
		this.proxyFactory = proxyFactory;
	}

	/**
	 * @return the inInterceptors
	 */
	public List<Interceptor<Message>> getInInterceptors() {
		return inInterceptors;
	}

	/**
	 * @param inInterceptors the inInterceptors to set
	 */
	public void setInInterceptors(List<Interceptor<Message>> inInterceptors) {
		this.inInterceptors = inInterceptors;
	}

	/**
	 * @return the outInterceptors
	 */
	public List<Interceptor<Message>> getOutInterceptors() {
		return outInterceptors;
	}

	/**
	 * @param outInterceptors the outInterceptors to set
	 */
	public void setOutInterceptors(List<Interceptor<Message>> outInterceptors) {
		this.outInterceptors = outInterceptors;
	}

}

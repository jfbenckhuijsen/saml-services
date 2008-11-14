/**
 * 
 */
package com.atosorigin.saml.pagebeans;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.atosorigin.saml.businessbeans.HelloWorldBB;

/**
 * @author a108600
 *
 */
public class HelloWorldPB {
	HelloWorldBB helloWorldBB;
	
	/**
	 * 
	 */
	public HelloWorldPB() {
		super();
	}
	
	public String sayHello() {
		try {
			helloWorldBB.sayHello();
		} catch (RuntimeException e) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, e.getLocalizedMessage(), writer.toString()));
		}
		return null;
	}

	public HelloWorldBB getHelloWorldBB() {
		return helloWorldBB;
	}

	public void setHelloWorldBB(HelloWorldBB helloWorldBB) {
		this.helloWorldBB = helloWorldBB;
	}

	public String getHelloReply() {
		return helloWorldBB.getHelloReply();
	}

}

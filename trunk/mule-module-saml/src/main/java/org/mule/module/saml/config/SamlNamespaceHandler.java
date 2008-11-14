package org.mule.module.saml.config;

import org.apache.cxf.configuration.spring.SimpleBeanDefinitionParser;
import org.mule.api.config.MuleProperties;
import org.mule.config.spring.parsers.generic.ChildDefinitionParser;
import org.mule.config.spring.parsers.generic.DescendentDefinitionParser;
import org.mule.config.spring.parsers.generic.NamedDefinitionParser;
import org.mule.module.saml.SAMLSecurityProvider;
import org.mule.module.saml.SamlKeyInfo;
import org.mule.module.saml.cxf.CxfSAMLSecurityFilter;
import org.mule.module.saml.cxf.SAMLVerifyInterceptor;
import org.mule.module.saml.ejb.EjbSAMLSecurityFilter;
import org.mule.module.saml.jms.JmsSAMLSecurityFilter;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class SamlNamespaceHandler extends NamespaceHandlerSupport
{

	public void init() 
	{
        registerBeanDefinitionParser("security-manager", new NamedDefinitionParser(MuleProperties.OBJECT_SECURITY_MANAGER));
        registerBeanDefinitionParser("saml-security-provider", new ChildDefinitionParser("provider", SAMLSecurityProvider.class));
        registerBeanDefinitionParser("key-properties", new ChildDefinitionParser("keyInfo", SamlKeyInfo.class));
		registerBeanDefinitionParser("cxf-security-filter", new DescendentDefinitionParser("securityFilter", CxfSAMLSecurityFilter.class));
		registerBeanDefinitionParser("jms-security-filter", new DescendentDefinitionParser("securityFilter", CxfSAMLSecurityFilter.class));
		registerBeanDefinitionParser("cxf-security-interceptor", new SimpleBeanDefinitionParser(SAMLVerifyInterceptor.class));
		registerBeanDefinitionParser("jms-security-filter", new DescendentDefinitionParser("securityFilter", JmsSAMLSecurityFilter.class));
		registerBeanDefinitionParser("ejb-security-filter", new DescendentDefinitionParser("securityFilter", EjbSAMLSecurityFilter.class));
	}
	
}

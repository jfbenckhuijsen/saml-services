package com.atosorigin.saml.mule;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.MuleServer;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;

/**
 * Simple helper class that starts a mule server with the specified configuration.
 * 
 * @author NL24167
 */
public final class ESBStarter {

	/** Commons Logger. */
	private static transient Log logger = LogFactory.getLog(ESBStarter.class);

	/** */
    private static MuleServer server;

    /**
     * Default Constructor.
     */
    private ESBStarter() {
    	
    }
    
	/**
	 * @throws MuleException 
	 * 
	 */
	public static void main(String[] args) throws MuleException {
		DefaultMuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
		SpringXmlConfigurationBuilder configBuilder = new SpringXmlConfigurationBuilder(args[0]);
		
		MuleContext context = muleContextFactory.createMuleContext(configBuilder);
		
		context.start();
		
		Object lock = new Object();
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}
}

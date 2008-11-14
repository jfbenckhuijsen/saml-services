/**
 * 
 */
package org.mule.module.saml;

/**
 * @author a108600
 *
 */
public class SamlKeyInfo {
	private String keyStoreFile;
	private String keyStoreType;
	private String keyStorePassword;
	private String signKeyAlias;
	private String signKeyPassword;
	
	/**
	 * 
	 */
	public SamlKeyInfo() {
		super();
	}

	/**
	 * @return the keystoreFile
	 */
	public String getKeyStoreFile() {
		return keyStoreFile;
	}

	/**
	 * @param keystoreFile the keystoreFile to set
	 */
	public void setKeyStoreFile(String keyStoreFile) {
		this.keyStoreFile = keyStoreFile;
	}

	/**
	 * @return the keystoreType
	 */
	public String getKeyStoreType() {
		return keyStoreType;
	}

	/**
	 * @param keystoreType the keystoreType to set
	 */
	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
	}

	/**
	 * @return the keystorePassword
	 */
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	/**
	 * @param keystorePassword the keystorePassword to set
	 */
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	/**
	 * @return the signKeyAlias
	 */
	public String getSignKeyAlias() {
		return signKeyAlias;
	}

	/**
	 * @param signKeyAlias the signKeyAlias to set
	 */
	public void setSignKeyAlias(String signKeyAlias) {
		this.signKeyAlias = signKeyAlias;
	}

	/**
	 * @return the signKeyPassword
	 */
	public String getSignKeyPassword() {
		return signKeyPassword;
	}

	/**
	 * @param signKeyPassword the signKeyPassword to set
	 */
	public void setSignKeyPassword(String signKeyPassword) {
		this.signKeyPassword = signKeyPassword;
	}

}

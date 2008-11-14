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

import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.Authentication;

/**
 * @author Cagatay Civici
 * @author Kenan Sevindik
 * @author Jeroen Benckhuijsen
 */
public class AcegiJsfUtils {

	public static String[] parseRoleList(String roles) {
		String [] roleArray =  roles.split(",");
		for (int i = 0; i < roleArray.length; i++) {
			roleArray[i] = roleArray[i].trim();
		}
		return roleArray;
	}
	
	public static boolean isUserInRole(String role) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null){
			GrantedAuthority[] authorities = auth.getAuthorities();
			for (int i = 0; i < authorities.length; i++) {
				GrantedAuthority grantedAuthority = authorities[i];
				if(grantedAuthority.getAuthority().equalsIgnoreCase(role)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static String getRemoteUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null) {
			return auth.getName();
		}
		return null;
	}
}

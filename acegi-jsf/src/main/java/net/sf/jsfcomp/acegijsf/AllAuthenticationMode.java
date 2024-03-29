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

/**
 * @author Cagatay Civici
 * @author Kenan Sevindik
 * 
 * Checks if the user is in all of the needed roles
 */
public class AllAuthenticationMode implements IAuthenticationMode {

	public boolean isUserInRole(String roleList) {
		String [] roles = AcegiJsfUtils.parseRoleList(roleList);
		boolean isAuthorized = false;
		
		for (int i = 0; i < roles.length; i++) {
			if(AcegiJsfUtils.isUserInRole(roles[i])) {
				isAuthorized = true;
			} else {
				isAuthorized = false;
				break;
			}
		}
		return isAuthorized;
	}

}

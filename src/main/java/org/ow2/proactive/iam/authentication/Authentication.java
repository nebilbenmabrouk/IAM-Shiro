/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.iam.authentication;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Authentication {

    private static Logger logger = LoggerFactory.getLogger(Authentication.class);

    public static boolean isAuthenticated(String username, String password) {

        // Using the IniSecurityManagerFactory, which will use the an INI file
        // as the security file.
        Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");

        // Setting up the SecurityManager...
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        Subject currentUser = SecurityUtils.getSubject();

        return currentUser.isAuthenticated();
    }

    public static UsernamePasswordToken getToken(String username, String password) {

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(true);

        try {
            Subject user = SecurityUtils.getSubject();
            user.login(token);
        } catch (UnknownAccountException e) {
            logger.error("There is no user with username of " + token.getPrincipal());
        } catch (IncorrectCredentialsException e) {
            logger.error("Password for account " + token.getPrincipal() + " was incorrect!");
        } catch (LockedAccountException e) {
            logger.error("The account for username " + token.getPrincipal() + " is locked.  " +
                         "Please contact your administrator to unlock it.");
        } catch (AuthenticationException e) {
            logger.error(e.getMessage());
        }

        return token;
    }
}

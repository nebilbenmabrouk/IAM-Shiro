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
package org.ow2.proactive.iam.bootstrap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;
import org.ow2.proactive.iam.backend.embedded.ldap.EmbeddedLDAPServer;
import org.ow2.proactive.iam.identity.provisioning.LocalLDAPIdentityManagement;
import org.ow2.proactive.iam.util.PropertiesHelper;


/**
 * IAMContextListener implements ServletContextListener. It triggers the method startLDAPServer()
 * of EmbeddedLDAPServer when the IAM web application context is initialized, and triggers shutdownLDAPServer() when
 * the same context is destroyed.
 **/

@WebListener
public class IAMContextListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(IAMContextListener.class);

    private final String iamProperties = "iam.properties";

    private PropertiesHelper propHelper = new PropertiesHelper(iamProperties);

    private String backend = propHelper.getValueAsString("iam.backend", "embeddedLDAP");

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        try {

            if (backend.equals("embeddedLDAP")) {

                logger.debug("Starting embedded LDAP server");
                EmbeddedLDAPServer.INSTANCE.startLDAPServer();

                logger.debug("Loading identities");
                LocalLDAPIdentityManagement idm = new LocalLDAPIdentityManagement(iamProperties);
                idm.importLdif("identities.ldif");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

        try {
            if (backend.equals("embeddedLDAP")) {

                EmbeddedLDAPServer.INSTANCE.shutdownLDAPServer();
                logger.debug("LDAP Server is stopped!");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }
}

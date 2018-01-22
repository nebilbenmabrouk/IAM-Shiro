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
package org.ow2.proactive.iam.draft;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.log4j.Logger;
import org.ow2.proactive.iam.backend.embedded.ldap.EmbeddedLDAPServer;
import org.ow2.proactive.iam.identity.provisioning.Identity;
import org.ow2.proactive.iam.identity.provisioning.LocalLDAPIdentityManagement;


public class Test4 {

    private static final Logger logger = Logger.getLogger(Test4.class);

    public static void main(String[] args) {
        try {
            EmbeddedLDAPServer ldapServer = EmbeddedLDAPServer.INSTANCE;
            ldapServer.startLDAPServer();

            final Hashtable<String, String> lEnvironment = new Hashtable<String, String>();
            lEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            lEnvironment.put(Context.PROVIDER_URL, "ldap://localhost:11389/");
            lEnvironment.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
            lEnvironment.put(Context.SECURITY_CREDENTIALS, "secret");

            LocalLDAPIdentityManagement idm = new LocalLDAPIdentityManagement("iam.properties");
            idm.importLdif("identities.ldif");

            Identity id = new Identity("toto", "toto", "toto");
            idm.insert(id);

            id = new Identity("foo", "toto", "bar");
            idm.edit(id);

            idm.search(id);

            idm.delete(id);

            final LdapContext lLdapContext = new InitialLdapContext(lEnvironment, new Control[0]);
            final SearchControls lSearchControls = new SearchControls();
            lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            logger.info("Reading data");

            final NamingEnumeration<SearchResult> lResultats = lLdapContext.search("dc=activeeon,dc=com",
                                                                                   "(ObjectClass=*)",
                                                                                   lSearchControls);
            logger.info("Found result");
            while (lResultats.hasMore()) {
                logger.info("\t\t " + lResultats.next().getName());
            }

            ldapServer.shutdownLDAPServer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

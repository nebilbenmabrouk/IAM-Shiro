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

import java.io.IOException;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;


/**
 * Created by nebil on 12/01/18.
 */
public class IdentityProvisioning {

    final LdapConnection lLdapConnection = new LdapNetworkConnection("localhost", 11389);

    public void provisionIdentities() throws IOException, LdapException {

        lLdapConnection.bind("uid=admin,ou=system", "secret");

        /*
         * lLdapConnection.add(new DefaultEntry("dc=activeeon,dc=com",
         * "objectClass: dcObject",
         * "objectClass: organization",
         * "o: activeeon",
         * "dc: activeeon"));
         */

        lLdapConnection.add(new DefaultEntry("ou=users,dc=example,dc=com",
                                             //                                     "description: All users in Activeeon",
                                             "objectClass: organizationalunit",
                                             "ou: users"));

        final EntryCursor lEntryCursor = lLdapConnection.search("dc=example,dc=com",
                                                                "(objectclass=*)",
                                                                SearchScope.OBJECT,
                                                                "*");

        for (final Entry lEntry : lEntryCursor) {
            System.out.println(lEntry);
        }

        lLdapConnection.unBind();
        lLdapConnection.close();
    }
}

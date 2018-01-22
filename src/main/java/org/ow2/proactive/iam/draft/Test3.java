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

import java.util.*;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.factory.DefaultDirectoryServiceFactory;
import org.apache.directory.server.core.factory.DirectoryServiceFactory;
import org.apache.directory.server.core.partition.impl.avl.AvlPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.log4j.Logger;
import org.ow2.proactive.iam.backend.embedded.ldap.EmbeddedLDAPServer;


/**
 * Created by nebil on 16/01/18.
 */
public class Test3 {

    private static final Logger logger = Logger.getLogger(EmbeddedLDAPServer.class);

    private static final String ldapPropertiesFile = "iam.properties";

    private static final String ldifFile = "identities.ldif";

    private static DirectoryService lService;

    private static LdapServer lServer;

    private static String host = "localhost";

    private static int port = 11389;

    private static String identitiesFile = "identities.ldif";

    public static void startLDAPServer() throws Exception {

        final DirectoryServiceFactory lFactory = new DefaultDirectoryServiceFactory();
        lFactory.init("ProActiveLDAPFactory");
        logger.info("Factory initialized");

        lService = lFactory.getDirectoryService();
        lService.getChangeLog().

                setEnabled(false);
        lService.setShutdownHookEnabled(true);

        /*
         * final Partition partition1 = new AvlPartition(lService.getSchemaManager());
         * partition1.setId("dc=activeeon,dc=com");
         * //partition1.setPartitionPath(new
         * File(lService.getInstanceLayout().getPartitionsDirectory(),"activeeon").toURI());
         * partition1.setSuffixDn(new Dn("dc=activeeon,dc=com"));
         * /*
         * Set<Index<?,?, String>> indexedAttributes1 = new HashSet<Index<?,?, String>>();
         * indexedAttributes1.add(new JdbmIndex("objectClass", false));
         * indexedAttributes1.add(new JdbmIndex("dc", false));
         * indexedAttributes1.add(new JdbmIndex("organization", false));
         * partition1.setIndexedAttributes(indexedAttributes1);
         */
        /*
         * partition1.initialize();
         * lService.addPartition(partition1);
         */

        //initDirectoryService();

        lServer = new LdapServer();
        lServer.setTransports(new TcpTransport("localhost", port));
        lServer.setDirectoryService(lService);
        logger.info("LDAP Server initialized");

        lService.startup();
        lServer.start();

        logger.info("LDAP Server started");

        final Partition lPartition = new AvlPartition(lService.getSchemaManager());
        lPartition.setId("o=activeeon");
        lPartition.setSuffixDn(new Dn(lService.getSchemaManager(), "o=activeeon"));
        lService.addPartition(lPartition);
        logger.info("Partition added");

        Dn dn0 = new Dn("o=activeeon");
        Entry entry0 = lService.newEntry(dn0);
        entry0.add("objectClass", "domain");
        entry0.add("objectClass", "top");
        entry0.add("objectClass", "extensibleObject");
        entry0.add("dc", "activeeon");
        entry0.add("o", "activeeon");
        lService.getAdminSession().add(entry0);

        Dn dn1 = new Dn(" ou=users,o=activeeon");
        Entry entry1 = lService.newEntry(dn1);
        entry1.add("objectClass", "top");
        entry1.add("objectClass", "organizationalUnit");
        entry1.add("ou", "users");
        lService.getAdminSession().add(entry1);

        Dn dn2 = new Dn(" uid=nebil,ou=users,o=activeeon");
        Entry entry2 = lService.newEntry(dn2);
        entry2.add("objectClass", "top");
        entry2.add("objectClass", "inetOrgPerson");
        entry2.add("objectClass", "person");
        entry2.add("objectClass", "organizationalPerson");
        entry2.add("cn", "nebil ben");
        entry2.add("sn", "ben");
        entry2.add("uid", "benmabrouk");
        lService.getAdminSession().add(entry2);

        /*
         * String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() +
         * identitiesFile;
         * File identitiesFile = new File(path);
         * System.out.println(path);
         */

        /*
         * if (identitiesFile.exists()) {
         * //new LdifFileLoader(lService.getAdminSession(),path).execute();
         * 
         * 
         * LdifFileLoader loader = new LdifFileLoader(lService.getAdminSession(),
         * identitiesFile.getAbsolutePath());
         * loader.execute();
         * System.out.println("LDIF data loaded");
         * }else logger.warn("Identities file does not exist");
         */

        //System.out.println(Test3.class.getResourceAsStream("/" + identitiesFile));

        importLdif(new ArrayList<String>());

        // context entry, after ds.startup()
        /*
         * Dn dn3 = new Dn("dc=activeeon,dc=com");
         * Entry entry3 = lService.newEntry(dn3);
         * entry3.add("dc", "activeeon");
         * entry3.add("objectClass", "dcObject");
         * entry3.add("objectClass", "organization");
         * entry3.add("o", "activeeon");
         * lService.getAdminSession().add(entry3);
         * 
         * 
         * Dn dn4 = new Dn("ou=users,dc=activeeon,dc=com");
         * Entry entry4 = lService.newEntry(dn4);
         * entry4.add("objectClass", "organizationalunit");
         * entry4.add("ou", "users");
         * lService.getAdminSession().add(entry4);
         * 
         * Dn dn5 = new Dn(" uid=nebil,ou=users,dc=activeeon,dc=com");
         * Entry entry5 = lService.newEntry(dn5);
         * entry5.add("objectClass", "top");
         * entry5.add("objectClass", "inetOrgPerson");
         * entry5.add("objectClass", "person");
         * entry5.add("objectClass", "organizationalPerson");
         * entry5.add("cn", "nebil ben");
         * entry5.add("sn", "ben");
         * entry5.add("uid", "benmabrouk");
         * entry5.add("userpassword", "{SHA}0DPiKuNIrrVmD8IUCuw1hQxNqZc=");
         * lService.getAdminSession().add(entry5);
         */

        logger.info("Entries added");

        final Hashtable<String, String> lEnvironment = new Hashtable<String, String>();
        lEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        lEnvironment.put(Context.PROVIDER_URL, "ldap://localhost:11389/");
        lEnvironment.put(Context.SECURITY_AUTHENTICATION, "simple");
        lEnvironment.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
        lEnvironment.put(Context.SECURITY_CREDENTIALS, "secret");

        final LdapContext lLdapContext = new InitialLdapContext(lEnvironment, new Control[0]);
        final SearchControls lSearchControls = new SearchControls();
        lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        logger.info("Reading data");

        final NamingEnumeration<SearchResult> lResultats = lLdapContext.search("dc=activeeon,dc=com",
                                                                               "(ObjectClass=*)",
                                                                               lSearchControls);
        logger.info("Found result:");
        while (lResultats.hasMore()) {
            logger.info("\t\t " + lResultats.next().getName());
        }

    }

    public static void main(String[] args) {
        try {
            startLDAPServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Imports given LDIF file to the directory using given directory service and schema manager.
     *
     * @param ldifFiles
     * @throws Exception
     */
    private static void importLdif(List<String> ldifFiles) throws Exception {
        if (ldifFiles == null || ldifFiles.isEmpty()) {
            System.out.println("Importing default data\n");
            importLdif(new LdifReader(Test3.class.getResourceAsStream("/" + identitiesFile)));
        } else {
            for (String ldifFile : ldifFiles) {
                System.out.println("Importing " + ldifFile + "\n");
                importLdif(new LdifReader(ldifFile));
            }
        }
    }

    private static void importLdif(LdifReader ldifReader) throws Exception {
        try {
            for (LdifEntry ldifEntry : ldifReader) {
                checkPartition(ldifEntry);
                System.out.print(ldifEntry.toString());
                lService.getAdminSession().add(new DefaultEntry(lService.getSchemaManager(), ldifEntry.getEntry()));
            }
        } finally {
            ldifReader.close();
        }
    }

    private static void checkPartition(LdifEntry ldifEntry) throws Exception {
        Dn dn = ldifEntry.getDn();
        Dn parent = dn.getParent();
        try {
            lService.getAdminSession().exists(parent);
        } catch (Exception e) {
            System.out.println("Creating new partition for DN=" + dn + "\n");
            AvlPartition partition = new AvlPartition(lService.getSchemaManager());
            partition.setId(dn.getName());
            partition.setSuffixDn(dn);
            lService.addPartition(partition);
        }
    }

    public static DirectoryService getlService() {
        return lService;
    }

    public static void setlService(DirectoryService lService) {
        Test3.lService = lService;
    }

    public static LdapServer getlServer() {
        return lServer;
    }

    public static void setlServer(LdapServer lServer) {
        Test3.lServer = lServer;
    }
}

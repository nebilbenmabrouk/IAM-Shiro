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

import java.util.HashMap;
import java.util.Map;

import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.server.protocol.shared.transport.UdpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by nebil on 14/01/18.
 */
public class Test2 {

    private static final Logger LOG = LoggerFactory.getLogger(Test2.class);

    private static DirectoryService ds;

    private static LdapServer kdc;

    private static int port = 11389;

    // create one partition
    private static String orgName = "example";

    private static String orgDomain = "com";

    public static void main(String[] args) throws Exception {
        if (kdc != null) {
            throw new RuntimeException("Already started");
        }
        //initDirectoryService();
        initKDCServer();

        //IdentityProvisioning ip = new IdentityProvisioning();
        //ip.provisionIdentities();
    }

    /*
     * private static void initDirectoryService() throws Exception {
     * 
     * final DirectoryServiceFactory lFactory = new DefaultDirectoryServiceFactory();
     * lFactory.init("ProActiveLDAPFactory");
     * LOG.info("Factory initialized");
     * 
     * ds = lFactory.getDirectoryService();
     * 
     * CacheService cacheService = new CacheService();
     * ds.setCacheService(cacheService);
     * 
     * // first load the schema
     * InstanceLayout instanceLayout = ds.getInstanceLayout();
     * File schemaPartitionDirectory = new File(instanceLayout.getPartitionsDirectory(), "schema");
     * 
     * if (schemaPartitionDirectory.exists()) {
     * System.out.println("schema partition already exists, skipping schema extraction");
     * } else {
     * SchemaLdifExtractor extractor = new
     * DefaultSchemaLdifExtractor(instanceLayout.getPartitionsDirectory());
     * extractor.extractOrCopy();
     * }
     * 
     * //SchemaLdifExtractor extractor = new
     * DefaultSchemaLdifExtractor(instanceLayout.getPartitionsDirectory());
     * //extractor.extractOrCopy();
     * 
     * SchemaLoader loader = new LdifSchemaLoader(schemaPartitionDirectory);
     * SchemaManager schemaManager = new DefaultSchemaManager(loader);
     * schemaManager.loadAllEnabled();
     * ds.setSchemaManager(schemaManager);
     * 
     * // Init the LdifPartition with schema
     * LdifPartition schemaLdifPartition = new LdifPartition(schemaManager, ds.getDnFactory());
     * schemaLdifPartition.setPartitionPath(schemaPartitionDirectory.toURI());
     * 
     * // The schema partition
     * SchemaPartition schemaPartition = new SchemaPartition(schemaManager);
     * schemaPartition.setWrappedPartition(schemaLdifPartition);
     * ds.setSchemaPartition(schemaPartition);
     * 
     * JdbmPartition systemPartition = new JdbmPartition(ds.getSchemaManager(), ds.getDnFactory());
     * systemPartition.setId("system");
     * systemPartition.setPartitionPath(new File(ds.getInstanceLayout().getPartitionsDirectory(),
     * systemPartition.getId()).toURI());
     * systemPartition.setSuffixDn(new Dn(ServerDNConstants.SYSTEM_DN));
     * systemPartition.setSchemaManager(ds.getSchemaManager());
     * ds.setSystemPartition(systemPartition);
     * 
     * ds.getChangeLog().setEnabled(false);
     * ds.setDenormalizeOpAttrsEnabled(true);
     * ds.addLast(new KeyDerivationInterceptor());
     * 
     * String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() +
     * "config.ldif";
     * System.out.println(path);
     * 
     * SingleFileLdifPartition configPartition = new SingleFileLdifPartition(ds.getSchemaManager(),
     * ds.getDnFactory());
     * configPartition.setId("config");
     * configPartition.setPartitionPath(new File(path).toURI());
     * configPartition.setSuffixDn(new Dn(ds.getSchemaManager(), "ou=config"));
     * configPartition.setSchemaManager(ds.getSchemaManager());
     * configPartition.setCacheService(cacheService);
     * 
     * JdbmPartition partition1 = new JdbmPartition(ds.getSchemaManager(), ds.getDnFactory());
     * partition1.setId(orgName);
     * partition1.setPartitionPath(new File(ds.getInstanceLayout().getPartitionsDirectory(),
     * orgName).toURI());
     * partition1.setSuffixDn(new Dn("dc=" + orgName + ",dc=" + orgDomain));
     * Set<Index<?, String>> indexedAttributes1 = new HashSet<Index<?, String>>();
     * indexedAttributes1.add(new JdbmIndex("objectClass", false));
     * indexedAttributes1.add(new JdbmIndex("dc", false));
     * //indexedAttributes1.add(new JdbmIndex("organization", false));
     * partition1.setIndexedAttributes(indexedAttributes1);
     * partition1.initialize();
     * ds.addPartition(partition1);
     * 
     * // And start the ds
     * ds.setInstanceId("11");
     * ds.startup();
     * 
     * // context entry, after ds.startup()
     * Dn dn0 = new Dn("dc=" + orgName + ",dc=" + orgDomain);
     * Entry entry0 = ds.newEntry(dn0);
     * entry0.add("objectClass", "dcObject", "organization");
     * entry0.add("dc", orgName);
     * entry0.add("o", orgName);
     * ds.getAdminSession().add(entry0);
     * 
     * Dn dn1 = new Dn("ou=users,dc=" + orgName + ",dc=" + orgDomain);
     * Entry entry1 = ds.newEntry(dn1);
     * entry1.add("objectClass", "organizationalunit");
     * entry1.add("ou", "users");
     * ds.getAdminSession().add(entry1);
     * 
     * System.out.println("entry added");
     * }
     */

    private static void initKDCServer() throws Exception {
        String orgName = "example";
        String orgDomain = "com";
        String bindAddress = "localhost";
        final Map<String, String> map = new HashMap<String, String>();
        map.put("0", orgName.toLowerCase());
        map.put("1", orgDomain.toLowerCase());
        map.put("2", orgName.toUpperCase());
        map.put("3", orgDomain.toUpperCase());
        map.put("4", bindAddress);

        /*
         * ClassLoader cl = Thread.currentThread().getContextClassLoader();
         * InputStream is1 = cl.getResourceAsStream("config.ldif");
         * 
         * SchemaManager schemaManager = ds.getSchemaManager();
         * LdifReader reader = null;
         * 
         * try {
         * final String content = StrSubstitutor.replace(IOUtils.toString(is1,
         * Charset.defaultCharset()), map);
         * reader = new LdifReader(new StringReader(content));
         * 
         * for (LdifEntry ldifEntry : reader) {
         * ds.getAdminSession().add(new DefaultEntry(schemaManager, ldifEntry.getEntry()));
         * }
         * } finally {
         * IOUtils.closeQuietly(reader);
         * IOUtils.closeQuietly(is1);
         * }
         */

        kdc = new LdapServer();
        kdc.setDirectoryService(ds);

        // transport
        String transport = "TCP";
        if (transport.trim().equals("TCP")) {
            kdc.addTransports(new TcpTransport(bindAddress, port, 3, 50));
        } else if (transport.trim().equals("UDP")) {
            kdc.addTransports(new UdpTransport(port));
        } else {
            throw new IllegalArgumentException("Invalid transport: " + transport);
        }

        kdc.setServiceName("embeddedLDAPServer");
        //kdc.getConfig().setMaximumRenewableLifetime(Long.parseLong(conf.getProperty(MAX_RENEWABLE_LIFETIME)));
        //kdc.getConfig().setMaximumTicketLifetime(Long.parseLong(conf.getProperty(MAX_TICKET_LIFETIME)));

        kdc.start();

        LOG.info("MiniKdc listening at port: {}", port);

    }
}

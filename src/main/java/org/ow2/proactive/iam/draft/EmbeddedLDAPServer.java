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

import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.factory.DefaultDirectoryServiceFactory;
import org.apache.directory.server.core.factory.DirectoryServiceFactory;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.log4j.Logger;
import org.ow2.proactive.iam.util.PropertiesHelper;
import org.ow2.proactive.iam.util.PropertyType;


public enum EmbeddedLDAPServer {

    //singleton instance of EmbeddedLDAPServer
    INSTANCE;

    private static final Logger logger = Logger.getLogger(EmbeddedLDAPServer.class);

    private static final String ldapPropertiesFile = "iam.properties";

    private static final String ldifFile = "identities.ldif";

    private static DirectoryService lService;

    private static LdapServer lServer;

    private static String host = "localhost";

    private static int port = 11389;

    private void configureServer() {
        PropertiesHelper propHelper = new PropertiesHelper(ldapPropertiesFile);
        port = propHelper.getValueAsInt("ldap.port", PropertyType.INTEGER, port);
    }

    public void startLDAPServer() throws Exception {
        configureServer();

        final DirectoryServiceFactory lFactory = new DefaultDirectoryServiceFactory();
        lFactory.init("ProActiveLDAPFactory");
        logger.debug("Factory initialized");

        lService = lFactory.getDirectoryService();
        lService.getChangeLog().setEnabled(false);
        lService.setShutdownHookEnabled(true);

        lServer = new LdapServer();
        lServer.setTransports(new TcpTransport("localhost", port));
        lServer.setDirectoryService(lService);
        logger.debug("LDAP Server initialized");

        lService.startup();
        lServer.start();
        logger.info("LDAP Server started");

        /*
         * final Partition lPartition = new JdbmPartition(lService.getSchemaManager(),
         * lService.getDnFactory());
         * lPartition.setId("activeeon");
         * lPartition.setSuffixDn(new Dn(lService.getSchemaManager(), "dc=activeeon,dc=com"));
         * lPartition.initialize();
         * lService.addPartition(lPartition);
         * logger.info("Partition 'dc=activeeon,dc=com' added");
         */

        /*
         * InputStream ldifIn =
         * Thread.currentThread().getContextClassLoader().getResourceAsStream(ldifFile);
         * CoreSession cs = lService.getAdminSession();
         * for (LdifEntry ldifEntry : new LdifReader(ldifIn)) {
         * DefaultEntry entry = new DefaultEntry(cs.getDirectoryService().getSchemaManager(),
         * ldifEntry.getEntry());
         *
         * if (!cs.exists(entry.getDn())) {
         * cs.add(entry);
         * }
         * }
         */

        //new LdifFileLoader(lService.getAdminSession(), new File(getLdifFile()), null).execute();
        //logger.info("LDIF file loaded");

        // IdentityProvisioning ip = new IdentityProvisioning();
        // ip.provisionIdentities();

    }

    public void shutdownLDAPServer() throws Exception {
        lServer.stop();
        lService.shutdown();
        logger.info("LDAP Server stopped");
    }

    /**
     * initialize the schema manager and add the schema partition to diectory service
     *
     * @throws Exception if the schema LDIF files are not found on the classpath
     */
    /*
     * private void initSchemaPartition() throws Exception {
     * InstanceLayout instanceLayout = lService.getInstanceLayout();
     *
     * File schemaPartitionDirectory = new File(instanceLayout.getPartitionsDirectory(), "schema");
     *
     * // Extract the schema on disk (a brand new one) and load the registries
     * if (schemaPartitionDirectory.exists()) {
     * System.out.println("schema partition already exists, skipping schema extraction");
     * } else {
     * SchemaLdifExtractor extractor = new
     * DefaultSchemaLdifExtractor(instanceLayout.getPartitionsDirectory());
     * extractor.extractOrCopy();
     * }
     *
     * SchemaLoader loader = new LdifSchemaLoader(schemaPartitionDirectory);
     * SchemaManager schemaManager = new DefaultSchemaManager(loader);
     *
     * // We have to load the schema now, otherwise we won't be able
     * // to initialize the Partitions, as we won't be able to parse
     * // and normalize their suffix Dn
     * schemaManager.loadAllEnabled();
     *
     * List<Throwable> errors = schemaManager.getErrors();
     *
     * if (errors.size() != 0) {
     * throw new Exception(I18n.err(I18n.ERR_517, Exceptions.printErrors(errors)));
     * }
     *
     * lService.setSchemaManager(schemaManager);
     *
     * // Init the LdifPartition with schema
     * LdifPartition schemaLdifPartition = new LdifPartition(schemaManager,
     * lService.getDnFactory());
     * schemaLdifPartition.setPartitionPath(schemaPartitionDirectory.toURI());
     *
     * // The schema partition
     * SchemaPartition schemaPartition = new SchemaPartition(schemaManager);
     * schemaPartition.setWrappedPartition(schemaLdifPartition);
     * lService.setSchemaPartition(schemaPartition);
     * }
     */

    /**
     * Initialize the server. It creates the partition, adds the index, and
     * injects the context entries for the created partitions.
     *
     * @throws Exception if there were some problems while initializing the system
     */
    /*
     * private void initDirectoryService() throws Exception {
     * final DirectoryServiceFactory lFactory = new DefaultDirectoryServiceFactory();
     * lFactory.init("ProActiveLDAPFactory");
     * logger.info("Factory initialized");
     *
     * // Initialize the LDAP service
     * lService = lFactory.getDirectoryService();
     *
     * //lService = new ApacheDsService();
     * //lService.start(new InstanceLayout( workDir ));
     * //lService.setInstanceLayout( new InstanceLayout( workDir ) );
     *
     * CacheService cacheService = new CacheService();
     * cacheService.initialize(lService.getInstanceLayout());
     *
     * lService.setCacheService(cacheService);
     *
     * // first load the schema
     * initSchemaPartition();
     *
     * // then the system partition
     * // this is a MANDATORY partition
     * // DO NOT add this via addPartition() method, trunk code complains about duplicate partition
     * // while initializing
     * JdbmPartition systemPartition = new JdbmPartition(lService.getSchemaManager(),
     * lService.getDnFactory());
     * systemPartition.setId("system");
     * systemPartition.setPartitionPath(new
     * File(lService.getInstanceLayout().getPartitionsDirectory(),
     * systemPartition.getId()).toURI());
     * systemPartition.setSuffixDn(new Dn(ServerDNConstants.SYSTEM_DN));
     * systemPartition.setSchemaManager(lService.getSchemaManager());
     *
     * // mandatory to call this method to set the system partition
     * // Note: this system partition might be removed from trunk
     * lService.setSystemPartition(systemPartition);
     *
     * lService.getChangeLog().setEnabled(false);
     * lService.setDenormalizeOpAttrsEnabled(true);
     *
     * SingleFileLdifPartition configPartition = new
     * SingleFileLdifPartition(lService.getSchemaManager(),
     * lService.getDnFactory());
     * configPartition.setId("config");
     * configPartition.setPartitionPath(new File(lService.getInstanceLayout().getConfDirectory(),
     * "config.ldif").toURI());
     * configPartition.setSuffixDn(new Dn(lService.getSchemaManager(), "ou=config"));
     * configPartition.setSchemaManager(lService.getSchemaManager());
     * configPartition.setCacheService(cacheService);
     *
     * configPartition.initialize();
     * lService.addPartition(configPartition);
     *
     * lService.startup();
     *
     * /*
     * Partition iamPartition = utils.addPartition("iam", "dc=activeeon,dc=com",
     * lService.getDnFactory());
     *
     * // Index some attributes on the apache partition
     * utils.addIndex(iamPartition, "objectClass", "ou", "uid", "gidNumber", "uidNumber", "cn");
     *
     * // And start the service
     *
     *
     *
     *
     * Entry entryIAM = lService.newEntry( lService.getDnFactory().create(
     * "cn=config,ads-authenticatorid=awsiamauthenticator,ou=authenticators,ads-interceptorId=authenticationInterceptor,ou=interceptors,ads-directoryServiceId=default,ou=config")
     * );
     * entryIAM.put("objectClass", "iamauthenticatorconfig", "top");
     * entryIAM.put(SchemaConstants.ENTRY_CSN_AT, lService.getCSN().toString());
     * entryIAM.put(SchemaConstants.ENTRY_UUID_AT, UUID.randomUUID().toString());
     * entryIAM.put("cn", "config");
     * entryIAM.put("idGenerator", "1000");
     * lService.getAdminSession().add(entryIAM);
     *
     * Dn dnIAM = lService.getDnFactory().create("");
     * if (!lService.getAdminSession().exists(dnIAM)) {
     * Entry entryIAM = new DefaultEntry(lService.getSchemaManager(), dnIAM, "objectClass: top",
     * "objectClass: domain", "dc: iam",
     * "entryCsn: " + lService.getCSN(), SchemaConstants.ENTRY_UUID_AT + ": " +
     * UUID.randomUUID().toString());
     * iamPartition.add(new AddOperationContext(null, entryIAM));
     * }
     *
     * }
     */

    public DirectoryService getlService() {
        return lService;
    }

    public void setlService(DirectoryService lService) {
        EmbeddedLDAPServer.lService = lService;
    }

    public LdapServer getlServer() {
        return lServer;
    }

    public void setlServer(LdapServer lServer) {
        EmbeddedLDAPServer.lServer = lServer;
    }
}

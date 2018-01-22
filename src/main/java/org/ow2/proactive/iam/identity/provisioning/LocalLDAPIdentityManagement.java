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
package org.ow2.proactive.iam.identity.provisioning;

import java.security.MessageDigest;
import java.util.*;
import java.util.Base64;

import org.apache.directory.api.ldap.model.cursor.Cursor;
import org.apache.directory.api.ldap.model.entry.*;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.partition.impl.avl.AvlPartition;
import org.apache.log4j.Logger;
import org.ow2.proactive.iam.backend.embedded.ldap.EmbeddedLDAPServer;
import org.ow2.proactive.iam.util.PropertiesHelper;


public class LocalLDAPIdentityManagement implements IdentityManagement {

    private Logger logger = Logger.getLogger(Identity.class);

    private DirectoryService ds = EmbeddedLDAPServer.INSTANCE.getlService();

    private String usersBase = "ou=users,dc=activeeon,dc=com";

    private String encryptionAlgorithm = "SHA";

    public LocalLDAPIdentityManagement(String ldapPropertiesFile) {
        try {
            //load properties
            PropertiesHelper propHelper = new PropertiesHelper(ldapPropertiesFile);

            usersBase = propHelper.getValueAsString("users.base", usersBase);
            encryptionAlgorithm = propHelper.getValueAsString("password.encryption.algorithm", encryptionAlgorithm);

        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    public boolean insert(Identity id) {
        try {

            Dn dn = new Dn("uid=" + id.getLogin() + "," + usersBase);
            Entry entry = ds.newEntry(dn);
            entry.add("uid", id.getLogin());
            entry.add("cn", id.getName());
            entry.add("sn", id.getName());
            entry.add("userpassword", encryptPassword(encryptionAlgorithm, id.getPassword()));
            entry.add("objectClass", "top");
            entry.add("objectClass", "person");
            entry.add("objectClass", "organizationalPerson");
            entry.add("objectClass", "inetOrgPerson");
            ds.getAdminSession().add(entry);

            logger.info("Entry successfully inserted: " + dn);
            return true;
        } catch (Exception e) {
            logger.error(e, e);
            return false;
        }
    }

    public boolean edit(Identity id) {
        try {
            Dn dn = new Dn("uid=" + id.getLogin() + "," + usersBase);
            List<Modification> modifs = new ArrayList<Modification>();
            //add modifications
            ds.getAdminSession().modify(dn, modifs);

            logger.info("Entry successfully edited: " + dn);
            return true;
        } catch (Exception e) {
            logger.error(e, e);
            return false;
        }
    }

    public boolean delete(Identity id) {
        try {
            Dn dn = new Dn("uid=" + id.getLogin() + "," + usersBase);
            ds.getAdminSession().delete(dn);

            logger.info("Entry successfully deleted: " + dn);
            return true;
        } catch (Exception e) {
            logger.error(e, e);
            return false;
        }
    }

    public boolean search(Identity id) {
        try {
            Dn dn = new Dn("uid=" + id.getLogin() + "," + usersBase);
            Cursor<Entry> cursor = ds.getAdminSession().search(dn, "(objectclass=person)");
            Iterator<Entry> iterator = cursor.iterator();

            while (iterator.hasNext()) {
                logger.info("Entry found: " + iterator.next().getDn().toString());
            }
            return true;

        } catch (Exception e) {
            logger.error(e, e);
            return false;
        }
    }

    public String encryptPassword(String algorithm, String _password) {
        String sEncrypted = _password;
        if ((_password != null) && (_password.length() > 0)) {
            boolean bMD5 = algorithm.equalsIgnoreCase("MD5");
            boolean bSHA = algorithm.equalsIgnoreCase("SHA") || algorithm.equalsIgnoreCase("SHA1") ||
                           algorithm.equalsIgnoreCase("SHA-1");
            if (bSHA || bMD5) {
                String sAlgorithm = "MD5";
                if (bSHA) {
                    sAlgorithm = "SHA";
                }
                try {
                    MessageDigest md = MessageDigest.getInstance(sAlgorithm);
                    md.update(_password.getBytes("UTF-8"));
                    sEncrypted = "{" + sAlgorithm + "}" + (Base64.getEncoder().encode(md.digest()));
                } catch (Exception e) {
                    sEncrypted = null;
                    logger.error(e, e);
                }
            }
        }
        return sEncrypted;
    }

    /**
     * Imports given LDIF file to the directory using given directory service and schema manager.
     *
     * @param ldifFile
     * @throws Exception
     */
    public void importLdif(String ldifFile) throws Exception {
        LdifReader ldifReader = new LdifReader(Thread.currentThread()
                                                     .getContextClassLoader()
                                                     .getResourceAsStream(ldifFile));
        try {

            for (LdifEntry ldifEntry : ldifReader) {
                checkPartition(ldifEntry);
                System.out.print(ldifEntry.toString());
                ds.getAdminSession().add(new DefaultEntry(ds.getSchemaManager(), ldifEntry.getEntry()));
            }
        } finally {
            ldifReader.close();
        }
    }

    private void checkPartition(LdifEntry ldifEntry) throws Exception {
        Dn dn = ldifEntry.getDn();
        Dn parent = dn.getParent();
        try {
            ds.getAdminSession().exists(parent);
        } catch (Exception e) {
            System.out.println("Creating new partition for DN=" + dn + "\n");
            AvlPartition partition = new AvlPartition(ds.getSchemaManager());
            partition.setId(dn.getName());
            partition.setSuffixDn(dn);
            ds.addPartition(partition);
        }
    }
}

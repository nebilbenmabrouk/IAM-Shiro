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
import java.util.Base64;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
import org.ow2.proactive.iam.util.PropertiesHelper;
import org.ow2.proactive.iam.util.PropertyType;


public class RemoteLDAPIdentityManagement implements IdentityManagement {

    private Logger logger = Logger.getLogger(Identity.class);

    private Hashtable<String, String> env = new Hashtable<String, String>();

    private String ldapHost = "localhost";

    private int ldapPort = 11389;

    private String securityPrincipal = "uid=admin,ou=system";

    private String securityCredentials = "secret";

    private String rootDn = "dc=activeeon,dc=com";

    private String usersBase = "ou=users,dc=activeeon,dc=com";

    private String rolesBase = "ou=roles,dc=activeeon,dc=com";

    private String encryptionAlgorithm = "SHA";

    public RemoteLDAPIdentityManagement(String ldapPropertiesFile) {
        try {
            //load properties
            PropertiesHelper propHelper = new PropertiesHelper(ldapPropertiesFile);

            ldapHost = propHelper.getValueAsString("ldap.host", ldapHost);
            ldapPort = propHelper.getValueAsInt("ldap.port", PropertyType.INTEGER, ldapPort);
            securityPrincipal = propHelper.getValueAsString("admin.dn", securityPrincipal);
            securityCredentials = propHelper.getValueAsString("admin.password", securityCredentials);
            rootDn = propHelper.getValueAsString("dn.base", rootDn);
            usersBase = propHelper.getValueAsString("users.base", usersBase);
            rolesBase = propHelper.getValueAsString("roles.base", rolesBase);
            encryptionAlgorithm = propHelper.getValueAsString("password.encryption.algorithm", encryptionAlgorithm);

            // configure ldap context
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldap://" + ldapHost + ":" + ldapPort);
            env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
            env.put(Context.SECURITY_CREDENTIALS, securityCredentials);

        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    public boolean insert(Identity id) {
        try {

            DirContext dctx = new InitialDirContext(env);
            Attributes matchAttrs = new BasicAttributes(true);
            matchAttrs.put(new BasicAttribute("uid", id.getLogin()));
            matchAttrs.put(new BasicAttribute("cn", id.getName()));
            matchAttrs.put(new BasicAttribute("sn", id.getName()));
            matchAttrs.put(new BasicAttribute("userpassword", encryptPassword(encryptionAlgorithm, id.getPassword())));
            matchAttrs.put(new BasicAttribute("objectclass", "top"));
            matchAttrs.put(new BasicAttribute("objectclass", "person"));
            matchAttrs.put(new BasicAttribute("objectclass", "organizationalPerson"));
            matchAttrs.put(new BasicAttribute("objectclass", "inetorgperson"));

            String entry = "uid=" + id.getLogin() + "," + usersBase;
            InitialDirContext iniDirContext = (InitialDirContext) dctx;
            iniDirContext.bind(entry, dctx, matchAttrs);
            logger.info("Entry successfully inserted: " + entry);
            logger.debug("success inserting " + entry);
            return true;
        } catch (Exception e) {
            logger.error(e, e);
            return false;
        }
    }

    public boolean edit(Identity id) {
        try {

            DirContext ctx = new InitialDirContext(env);
            ModificationItem[] mods = new ModificationItem[2];
            Attribute mod0 = new BasicAttribute("cn", id.getName());
            Attribute mod1 = new BasicAttribute("sn", id.getName());
            Attribute mod2 = new BasicAttribute("userpassword", encryptPassword("SHA", id.getPassword()));
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
            mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod1);
            mods[2] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod2);

            String entry = "uid=" + id.getLogin() + "," + usersBase;
            ctx.modifyAttributes(entry, mods);
            logger.info("Entry successfully edited: " + entry);
            return true;
        } catch (Exception e) {
            logger.error(e, e);
            return false;
        }
    }

    public boolean delete(Identity id) {
        try {

            DirContext ctx = new InitialDirContext(env);
            String entry = "uid=" + id.getLogin() + "," + usersBase;

            ctx.destroySubcontext(entry);
            logger.info("Entry successfully deleted: " + entry);
            return true;
        } catch (Exception e) {
            logger.error(e, e);
            return false;
        }
    }

    public boolean search(Identity id) {
        try {

            DirContext ctx = new InitialDirContext(env);

            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String filter = "(&(objectclass=person)(uid=" + id.getLogin() + "))";

            NamingEnumeration results = ctx.search(usersBase, filter, sc);

            while (results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();

                Attribute attr = attrs.get("uid");
                if (attr != null)
                    logger.info("Entry found: " + attr.get());
            }
            ctx.close();

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
}

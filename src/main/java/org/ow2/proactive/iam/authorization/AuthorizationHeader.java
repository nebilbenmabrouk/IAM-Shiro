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
package org.ow2.proactive.iam.authorization;

import java.io.UnsupportedEncodingException;

import org.apache.shiro.codec.Base64;


/**
 * Created by nebil on 23/01/18.
 */
public class AuthorizationHeader {

    private static String getBasicAuthorizationHeaderValue(String username, String password)
            throws UnsupportedEncodingException {
        String authorizationHeader = username + ":" + password;
        byte[] valueBytes;
        valueBytes = authorizationHeader.getBytes("UTF-8");
        authorizationHeader = new String(Base64.encode(valueBytes));
        return "Basic " + authorizationHeader;
    }

    public static void main(String[] args) {
        try {
            System.out.println(getBasicAuthorizationHeaderValue("admin", "admin"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

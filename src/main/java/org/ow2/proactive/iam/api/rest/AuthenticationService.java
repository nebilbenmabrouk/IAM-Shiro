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
package org.ow2.proactive.iam.api.rest;

import javax.security.auth.login.LoginException;
import javax.ws.rs.*;
import javax.ws.rs.HeaderParam;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.ow2.proactive.iam.authentication.Authentication;


@Path("/authentication")
@Produces({ "application/json" })
public class AuthenticationService {

    private Logger logger = Logger.getLogger(AuthenticationService.class);

    /**
     * Login to ProActive using 2 params (username and password).
     *
     * @param username  username
     * @param password  password
     * @return true or false, whether the given user credentials are correct.
     */
    @GET
    @Produces("application/json")
    public String isAuthenticated(@HeaderParam("username") String username, @HeaderParam("password") String password) {

        if (username != null && password != null) {
            return String.valueOf(Authentication.isAuthenticated(username, password));
        } else {
            return String.valueOf(false);
        }
    }

    /**
     * Login to ProActive using 2 params (username and password).
     *
     * @param username  username
     * @param password  password
     * @return true or false, whether the given user credentials are correct.
     */
    @RequiresAuthentication
    @POST
    @Produces("application/json")
    public String login(@FormParam("username") String username, @FormParam("password") String password)
            throws LoginException {

        if (username != null && password != null) {
            return String.valueOf(Authentication.isAuthenticated(username, password));
        } else {
            return String.valueOf(false);
        }
    }

}

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

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.ow2.proactive.iam.authentication.Authentication;


@Path("authentication")
@Produces({ "application/json", "plain/text" })
public class AuthenticationService {

    private Logger logger = Logger.getLogger(AuthenticationService.class);

    @Resource
    WebServiceContext wsctx;

    /**
     * Gets the Message.
     *
     * @return the message
     */
    //   @RequiresAuthentication
    // @Path("isAuthenticated")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public Boolean isAuthenticated() {

        MessageContext mctx = wsctx.getMessageContext();
        Map http_headers = (Map) mctx.get(MessageContext.HTTP_REQUEST_HEADERS);

        List<String> userList = (List) http_headers.get("Username");
        List<String> passList = (List) http_headers.get("Password");

        String username = null;
        String password = null;
        if (userList != null) {
            username = userList.get(0);
        }
        if (passList != null) {
            password = passList.get(0);
        }

        logger.info(username);

        if (username != null && password != null) {
            return Authentication.isAuthenticated(username, password);
        } else
            return false;

    }

    /**
     * Login to ProActive using 2 params (username and password).
     *
     * @param username  username
     * @param password  password
     * @return the session id associated to the login.
     * @throws LoginException
     */
    //   @RequiresAuthentication
    // @Path("access")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces("application/json")
    public String login(@FormParam("username") String username, @FormParam("password") String password)
            throws LoginException {

        logger.info(username + "," + password);

        String token = null;
        try {
            if ((username == null) || (password == null)) {
                throw new LoginException("Empty login/password");
            } else {
                boolean b = Authentication.isAuthenticated(username, password);
                logger.info(b);
                token = "token";
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return token;

    }

}

/*
 *  Copyright (C) 2011 Alvaro Monge (amonge at csulb dot edu)
 *  California State University Long Beach (CSULB) ALL RIGHTS RESERVED
 *
 *  Use of this software is authorized for CSULB students in Dr. Monge's classes, so long
 *  as this copyright notice remains intact. Students must request permission from Dr. Monge
 *  if the code is to be used in other venues outside of Dr. Monge's classes.
 *
 *  This program is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */

package recipemix.controllers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import recipemix.beans.UsersEJB;
import recipemix.models.Users;

/**
 * SecurityFilter filters all requests so that the session is updated (if needed) with the
 * User object representing the authenticated user that's making the request.
 * 
 * This file has been modified for use in RecipeMix
 *
 * @author Alvaro Monge (amonge at csulb dot edu)
 */
public class SecurityFilter implements Filter {
    private static final Logger logger = Logger.getLogger("RequestWatchingFilter");

    private FilterConfig filterConfig = null;

    @EJB
    private UsersEJB userEJB;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest webRequest = (HttpServletRequest) request;
        HttpServletResponse webResponse = (HttpServletResponse) response;

        HttpSession session = webRequest.getSession(false);
        String resource = webRequest.getPathInfo();

        if (session != null) { // Do this only when a session is active
            UserIdentity userIdentity = (UserIdentity) session.getAttribute("userIdentity");
            String authenticatedUserName = webRequest.getRemoteUser();
            if (userIdentity != null && authenticatedUserName != null) {
                Users userInSession = userIdentity.getUser();
                if (userInSession == null || (! authenticatedUserName.equals(userInSession.getUserName())) ) {
                    // if the authenticated user is different from the user in the session...
                    //   then find the User in DB and put into session
                    Users u = userEJB.findUser(authenticatedUserName);
                    userIdentity.setUser(u); // EVEN if it's null... in order to invalidate user object in session
                    logger.log(Level.SEVERE, "Changed session, so now userIdentiy object has user=authenticated user");
                }
            } else if (userIdentity != null) {
                 // TODO: no user is authenticated, so make sure session has no object... even invalidate session?
                userIdentity.setUser(null);
            }
        }

        chain.doFilter(webRequest, webResponse);

        return;
    }

    public void logSessionContents(HttpServletRequest webReq) {
        HttpSession session = (HttpSession) webReq.getSession(false);

        if (session != null) {
            logger.log(Level.INFO, "FILTER: session id={0}", session.getId());
            Enumeration e = session.getAttributeNames();
            logger.log(Level.INFO, "Session Attributes: ");
            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                if (! name.contains("DebugOutput")) {
                    String value = session.getAttribute(name).toString();
                    logger.log(Level.INFO, "Session Attribute: {0}={1}", new Object[]{name, value});
                }
            }
        }
        logger.log(Level.INFO, "Session Cookies: ");
        for ( Cookie c : webReq.getCookies()) {
            logger.log(Level.INFO, "Cookie: {0}={1}", new Object[]{c.getName(), c.getValue()});
        }

        logger.log(Level.INFO, "Attributes: ");
        for (Enumeration<String> attrNames = webReq.getAttributeNames(); attrNames.hasMoreElements(); ) {
            String attrName = attrNames.nextElement();
            logger.log(Level.INFO, "Attribute: {0}={1}", new Object[]{attrName, webReq.getAttribute(attrName)});
        }

        logger.log(Level.INFO, "Parameters: ");
        Map<String,String[]> paramMap = webReq.getParameterMap();
        for (String key : paramMap.keySet()) {
            logger.log(Level.INFO, "Parameter: {0}={1}", new Object[]{key, paramMap.get(key)});
        }
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
        System.err.println("FILTER: destroy");
    }

    @Override
    public void init(FilterConfig filterConfig) {
        logger.log(Level.INFO, "FILTER: init");
        this.filterConfig = filterConfig;
    }
}
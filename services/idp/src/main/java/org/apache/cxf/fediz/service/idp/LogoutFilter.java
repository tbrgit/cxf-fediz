/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.fediz.service.idp;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogoutFilter implements Filter {
    
    public static final String IDP_USER = "idp-user";

    public static final String LOGOUT_URI = "logout.uri";

    private static final Logger LOG = LoggerFactory.getLogger(LogoutFilter.class);
    
    private String logoutUri = "logout";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (filterConfig.getInitParameter(LOGOUT_URI) != null) {
            logoutUri = filterConfig.getInitParameter(LOGOUT_URI);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest hrequest = (HttpServletRequest)request;
            if (hrequest.getParameter(this.logoutUri) != null) {
                HttpSession session = hrequest.getSession(false);
                if (session == null) {
                    LOG.info("Logout ignored. No session available.");
                    return;
                }
                
                LOG.info("Logout session for '" + session.getAttribute(IDP_USER) + "'");
                session.invalidate();
                return;
            }
        }
        
        chain.doFilter(request, response);
        
    }

    @Override
    public void destroy() {

    }

}
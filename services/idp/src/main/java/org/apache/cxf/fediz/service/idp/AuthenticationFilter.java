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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationFilter extends AbstractAuthFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);
//    static {
//        LOG = LoggerFactory.getLogger(AuthenticationFilter.class);
//    }
    
    @Override
    public void process(HttpServletRequest request,
                        HttpServletResponse response, AuthContext context)
        throws IOException, ServletException, ProcessingException {

        //Only Username/password authentication supported
        //otherwise parse wauth parameter
        if (context.get(FederationFilter.PARAM_WAUTH) != null) {
            LOG.warn("Parameter 'wauth' ignored");
        }
        this.setNextState(States.USERNAME_PASSWORD_REQUIRED.toString(), context);
    }

}

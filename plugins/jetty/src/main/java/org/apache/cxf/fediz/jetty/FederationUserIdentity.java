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

package org.apache.cxf.fediz.jetty;


import java.security.Principal;
import java.util.List;

import javax.security.auth.Subject;

import org.eclipse.jetty.server.UserIdentity;

public class FederationUserIdentity implements UserIdentity {
    
    private Subject subject;
    private Principal principal;
    private List<String> roles;

    public FederationUserIdentity(Subject subject, Principal principal, List<String> roles) {
        this.subject = subject;
        this.principal = principal;
        this.roles = roles;
    }


    public Subject getSubject() {
        return subject;
    }

    public Principal getUserPrincipal() {
        return principal;
    }

    public boolean isUserInRole(String role, Scope scope) {
        return roles.contains(role);
    }

}

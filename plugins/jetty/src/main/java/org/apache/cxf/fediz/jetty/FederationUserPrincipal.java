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

import org.apache.cxf.fediz.core.ClaimCollection;
import org.apache.cxf.fediz.core.FederationPrincipal;
import org.apache.cxf.fediz.core.FederationResponse;

public class FederationUserPrincipal implements FederationPrincipal {
    private String name;
    private ClaimCollection claims;
    private FederationResponse response;

    public FederationUserPrincipal(String name, FederationResponse response) {
        this.name = name;
        this.response = response;
        this.claims = new ClaimCollection(response.getClaims());
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public ClaimCollection getClaims() {
        return claims;
    }
    
    // not public available
    //[TODO] maybe find better approach, custom UserIdentity
    FederationResponse getFederationResponse() {
        return response;
    }
    

}

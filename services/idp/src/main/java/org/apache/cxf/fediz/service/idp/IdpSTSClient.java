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

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdpSTSClient extends STSClient {

    private static final Logger LOG = LoggerFactory.getLogger(IdpSTSClient.class);

    public IdpSTSClient(Bus b) {
        super(b);
    }

    public String requestSecurityTokenResponse() throws Exception {
        return requestSecurityTokenResponse(null);
    }

    public String requestSecurityTokenResponse(String appliesTo) throws Exception {
        String action = null;
        if (isSecureConv) {
            action = namespace + "/RST/SCT";
        }
        return requestSecurityTokenResponse(appliesTo, action, "/Issue", null);
    }

    public String requestSecurityTokenResponse(String appliesTo, String action,
            String requestType, SecurityToken target) throws Exception {
        STSResponse response = issue(appliesTo, null, "/Issue", null);

        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(response.getResponse(), new StreamResult(sw));
        } catch (TransformerException te) {
            LOG.warn("nodeToString Transformer Exception");
        }
        return sw.toString();

    }

}

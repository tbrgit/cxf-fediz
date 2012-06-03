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

package org.apache.cxf.fediz.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class STSUtil {
    
    public static final String SAMPLE_RSTR_MSG = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
        + "<SOAP-ENV:Envelope "
        +   "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "
        +   "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
        +   "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" 
        +   "<SOAP-ENV:Body>" 
        +       "<add xmlns=\"http://ws.apache.org/counter/counter_port_type\">" 
        +           "<value xmlns=\"\">15</value>" 
        +       "</add>" 
        +   "</SOAP-ENV:Body>" 
        + "</SOAP-ENV:Envelope>";

    
    public static final String SAMPLE_RSTR_COLL_MSG = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<RequestSecurityTokenResponseCollection "
        +   "xmlns=\"http://docs.oasis-open.org/ws-sx/ws-trust/200512\"> "
        +   "<RequestSecurityTokenResponse>"
        +     "<RequestedSecurityToken>"
        +     "</RequestedSecurityToken>"
        +   "</RequestSecurityTokenResponse>"
        + "</RequestSecurityTokenResponseCollection>";
    
    
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
    static {
        factory.setNamespaceAware(true);
    }
    
    protected STSUtil() {
        
    }
    
    /**
     * Convert an XML document as a String to a org.w3c.dom.Document.
     */
    public static org.w3c.dom.Document toSOAPPart(String xml) throws Exception {
        InputStream in = new ByteArrayInputStream(xml.getBytes());
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(in);
    }
    
}

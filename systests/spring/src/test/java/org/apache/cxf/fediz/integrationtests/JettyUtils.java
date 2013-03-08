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

package org.apache.cxf.fediz.integrationtests;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;

public final class JettyUtils {

    private static Server idpServer;
    private static Server rpServer;
    
    private JettyUtils() {
    }
        
    public static void initIdpServer() {
        if (idpServer == null) {
            try {
                Resource testServerConfig = Resource.newSystemResource("idp-server.xml");
                XmlConfiguration configuration = new XmlConfiguration(testServerConfig.getInputStream());
                idpServer = (Server)configuration.configure();   
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void startIdpServer() {
        if (idpServer != null && !idpServer.isStarted()) {
            try {
                idpServer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void stopIdpServer() {
        if (idpServer != null && idpServer.isStarted()) {
            try {
                idpServer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void initRpServer() {
        if (rpServer == null) {
            try {
                Resource testServerConfig = Resource.newSystemResource("rp-server.xml");
                XmlConfiguration configuration = new XmlConfiguration(testServerConfig.getInputStream());
                rpServer = (Server)configuration.configure();   
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void startRpServer() {
        if (rpServer != null && !rpServer.isStarted()) {
            try {
                rpServer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void stopRpServer() {
        if (rpServer != null && rpServer.isStarted()) {
            try {
                rpServer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}

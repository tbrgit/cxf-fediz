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

public final class JettyTest {
    
    private JettyTest() {
        super();
    }

    public static Server initIdp(String idpHttpsPort) {
        Server idpServer = null;
        try {
            Resource testServerConfig = Resource.newSystemResource("jetty/idp-server.xml");
            XmlConfiguration configuration = new XmlConfiguration(testServerConfig.getInputStream());
            idpServer = (Server)configuration.configure();   
            idpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idpServer;
    }

    public static Server initRp(String rpHttpsPort) {
        Server rpServer = null;
        try {
            Resource testServerConfig = Resource.newSystemResource("jetty/rp-server.xml");
            XmlConfiguration configuration = new XmlConfiguration(testServerConfig.getInputStream());
            rpServer = (Server)configuration.configure();
            rpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rpServer;
    }

    public static void cleanup(Server idpServer, Server rpServer) {
        if (idpServer != null && idpServer.isStarted()) {
            try {
//                idpServer.stop();
                idpServer.setGracefulShutdown(2000);
                idpServer.setStopAtShutdown(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (rpServer != null && rpServer.isStarted()) {
            try {
//                rpServer.stop();
                rpServer.setGracefulShutdown(2000);
                rpServer.setStopAtShutdown(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

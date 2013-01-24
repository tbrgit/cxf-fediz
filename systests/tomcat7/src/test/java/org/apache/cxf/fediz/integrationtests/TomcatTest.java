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

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.cxf.fediz.tomcat.FederationAuthenticator;

public final class TomcatTest {
    
    private TomcatTest() {
        super();
    }

    public static Tomcat initIdp(String idpHttpsPort) {
        Tomcat idpServer = new Tomcat();
        try {
            idpServer.setPort(0);
            String currentDir = new File(".").getCanonicalPath();
            idpServer.setBaseDir(currentDir + File.separator + "target");

            idpServer.getHost().setAppBase("tomcat/idp/webapps");
            idpServer.getHost().setAutoDeploy(true);
            idpServer.getHost().setDeployOnStartup(true);

            Connector httpsConnector = new Connector();
            httpsConnector.setPort(Integer.parseInt(idpHttpsPort));
            httpsConnector.setSecure(true);
            httpsConnector.setScheme("https");
            // httpsConnector.setAttribute("keyAlias", keyAlias);
            httpsConnector.setAttribute("keystorePass", "tompass");
            httpsConnector.setAttribute("keystoreFile",
                    "test-classes/server.jks");
            httpsConnector.setAttribute("clientAuth", "false");
            httpsConnector.setAttribute("sslProtocol", "TLS");
            httpsConnector.setAttribute("SSLEnabled", true);

            idpServer.getService().addConnector(httpsConnector);

            idpServer.addWebapp("/fediz-idp-sts", "fediz-idp-sts");
            idpServer.addWebapp("/fediz-idp", "fediz-idp");

            idpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idpServer;
    }

    public static Tomcat initRp(String rpHttpsPort) {
        Tomcat rpServer = new Tomcat();
        try {
            rpServer.setPort(0);
            String currentDir = new File(".").getCanonicalPath();
            rpServer.setBaseDir(currentDir + File.separator + "target");

            rpServer.getHost().setAppBase("tomcat/rp/webapps");
            rpServer.getHost().setAutoDeploy(true);
            rpServer.getHost().setDeployOnStartup(true);

            Connector httpsConnector = new Connector();
            httpsConnector.setPort(Integer.parseInt(rpHttpsPort));
            httpsConnector.setSecure(true);
            httpsConnector.setScheme("https");
            // httpsConnector.setAttribute("keyAlias", keyAlias);
            httpsConnector.setAttribute("keystorePass", "tompass");
            httpsConnector.setAttribute("keystoreFile",
                    "test-classes/server.jks");
            httpsConnector.setAttribute("clientAuth", "false");
            httpsConnector.setAttribute("sslProtocol", "TLS");
            httpsConnector.setAttribute("SSLEnabled", true);

            rpServer.getService().addConnector(httpsConnector);

            Context cxt = rpServer
                    .addWebapp("/fedizhelloworld", "simpleWebapp");
            FederationAuthenticator fa = new FederationAuthenticator();
            fa.setConfigFile(currentDir + File.separator + "target"
                    + File.separator + "test-classes" + File.separator
                    + "fediz_config.xml");
            cxt.getPipeline().addValve(fa);

            rpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rpServer;
    }

    public static void cleanup(Tomcat idpServer, Tomcat rpServer) {
        try {
            if (idpServer.getServer() != null
                    && idpServer.getServer().getState() != LifecycleState.DESTROYED) {
                if (idpServer.getServer().getState() != LifecycleState.STOPPED) {
                    idpServer.stop();
                }
                idpServer.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (rpServer.getServer() != null
                    && rpServer.getServer().getState() != LifecycleState.DESTROYED) {
                if (rpServer.getServer().getState() != LifecycleState.STOPPED) {
                    rpServer.stop();
                }
                rpServer.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

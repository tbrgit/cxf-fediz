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
import java.io.FileInputStream;
import java.security.KeyStore;

import org.apache.cxf.fediz.core.ClaimTypes;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;

public abstract class AbstractTests {

    static String idpHttpsPort;
    static String rpHttpsPort;
    
    public AbstractTests() {
        super();
    }

    static String getIdpHttpsPort() {
        return idpHttpsPort;
    }

    static String getRpHttpsPort() {
        return rpHttpsPort;
    }
    static void init() {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");

        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");

        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");

        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");

        System.setProperty("org.apache.commons.logging.simplelog.log.org.springframework.webflow", "debug");
        
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.cxf.fediz", "debug");
        
       // System.setProperty("idp.https.port", "23617");
       // System.setProperty("rp.https.port", "24617");
        
        idpHttpsPort = System.getProperty("idp.https.port");
        Assert.assertNotNull("Property 'idp.https.port' null", idpHttpsPort);
        rpHttpsPort = System.getProperty("rp.https.port");
        Assert.assertNotNull("Property 'rp.https.port' null", rpHttpsPort);

    }
    
    @org.junit.Test
    public void testUserAlice() throws Exception {
        String url = "https://localhost:" + getRpHttpsPort() + "/fedizhelloworld/secure/fedservlet";
        String user = "alice";
        String password = "ecila";
        String response = sendHttpGet(url, user, password);

        Assert.assertTrue("Principal not " + user, response.indexOf("userPrincipal=" + user) > 0);
        Assert.assertTrue("User " + user + " does not have role Admin", response.indexOf("role:Admin=false") > 0);
        Assert.assertTrue("User " + user + " does not have role Manager", response.indexOf("role:Manager=false") > 0);
        Assert.assertTrue("User " + user + " must have role User", response.indexOf("role:User=true") > 0);

        String claim = ClaimTypes.FIRSTNAME.toString();
        Assert.assertTrue("User " + user + " claim " + claim + " is not 'Alice'",
                          response.indexOf(claim + "=Alice") > 0);
        claim = ClaimTypes.LASTNAME.toString();
        Assert.assertTrue("User " + user + " claim " + claim + " is not 'Smith'",
                          response.indexOf(claim + "=Smith") > 0);
        claim = ClaimTypes.EMAILADDRESS.toString();
        Assert.assertTrue("User " + user + " claim " + claim + " is not 'alice@mycompany.org'",
                          response.indexOf(claim + "=alice@mycompany.org") > 0);

    }

    @org.junit.Test
    public void testUserBob() throws Exception {
        String url = "https://localhost:" + getRpHttpsPort() + "/fedizhelloworld/secure/fedservlet";
        String user = "bob";
        String password = "bob";
        String response = sendHttpGet(url, user, password);

        Assert.assertTrue("Principal not " + user, response.indexOf("userPrincipal=" + user) > 0);
        Assert.assertTrue("User " + user + " does not have role Admin", response.indexOf("role:Admin=true") > 0);
        Assert.assertTrue("User " + user + " does not have role Manager", response.indexOf("role:Manager=true") > 0);
        Assert.assertTrue("User " + user + " must have role User", response.indexOf("role:User=true") > 0);

        String claim = ClaimTypes.FIRSTNAME.toString();
        Assert.assertTrue("User " + user + " claim " + claim + " is not 'Bob'",
                          response.indexOf(claim + "=Bob") > 0);
        claim = ClaimTypes.LASTNAME.toString();
        Assert.assertTrue("User " + user + " claim " + claim + " is not 'Windsor'",
                          response.indexOf(claim + "=Windsor") > 0);
        claim = ClaimTypes.EMAILADDRESS.toString();
        Assert.assertTrue("User " + user + " claim " + claim + " is not 'bobwindsor@idp.org'",
                          response.indexOf(claim + "=bobwindsor@idp.org") > 0);
    }

    @org.junit.Test
    public void testUserTed() throws Exception {
        String url = "https://localhost:" + getRpHttpsPort() + "/fedizhelloworld/secure/fedservlet";
        String user = "ted";
        String password = "det";
        String response = sendHttpGet(url, user, password);

        Assert.assertTrue("Principal not " + user, response.indexOf("userPrincipal=" + user) > 0);
        Assert.assertTrue("User " + user + " does not have role Admin", response.indexOf("role:Admin=false") > 0);
        Assert.assertTrue("User " + user + " does not have role Manager", response.indexOf("role:Manager=false") > 0);
        Assert.assertTrue("User " + user + " must have role User", response.indexOf("role:User=false") > 0);

        String claim = ClaimTypes.FIRSTNAME.toString();
        Assert.assertTrue("User " + user + " claim " + claim + " is not 'Ted'",
                          response.indexOf(claim + "=Ted") > 0);
        claim = ClaimTypes.LASTNAME.toString();
        Assert.assertTrue("User " + user + " claim " + claim + " is not 'Cooper'",
                          response.indexOf(claim + "=Cooper") > 0);
        claim = ClaimTypes.EMAILADDRESS.toString();
        Assert.assertTrue("User " + user + " claim " + claim + " is not 'tcooper@hereiam.org'",
                          response.indexOf(claim + "=tcooper@hereiam.org") > 0);
    }

    @org.junit.Test
    public void testUserAliceNoAccess() throws Exception {
        String url = "https://localhost:" + getRpHttpsPort() + "/fedizhelloworld/secure/admin/fedservlet";
        String user = "alice";
        String password = "ecila";
        sendHttpGet(url, user, password, 200, 403);        
    }

    @org.junit.Test
    public void testUserAliceWrongPassword() throws Exception {
        String url = "https://localhost:" + getRpHttpsPort() + "/fedizhelloworld/secure/fedservlet";
        String user = "alice";
        String password = "alice";
        sendHttpGet(url, user, password, 401, 0);        
    }

    @org.junit.Test
    public void testUserTedNoAccess() throws Exception {
        String url = "https://localhost:" + getRpHttpsPort() + "/fedizhelloworld/secure/admin/fedservlet";
        String user = "ted";
        String password = "det";
        sendHttpGet(url, user, password, 200, 403);        
    }

    private String sendHttpGet(String url, String user, String password) throws Exception {
        return sendHttpGet(url, user, password, 200, 200);
    }

    abstract String sendHttpGet(String url, String user, String password, int returnCodeIDP, int returnCodeRP)
        throws Exception;
    
    void configureSSL(DefaultHttpClient httpclient)
        throws Exception {
        KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream instream = new FileInputStream(new File("./target/test-classes/server.jks"));
        try {
            trustStore.load(instream, "tompass".toCharArray());
        } finally {
            try {
                instream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
        Scheme schIdp = new Scheme("https", Integer.parseInt(getIdpHttpsPort()), socketFactory);
        httpclient.getConnectionManager().getSchemeRegistry().register(schIdp);
        Scheme schRp = new Scheme("https", Integer.parseInt(getRpHttpsPort()), socketFactory);
        httpclient.getConnectionManager().getSchemeRegistry().register(schRp);
    }
}

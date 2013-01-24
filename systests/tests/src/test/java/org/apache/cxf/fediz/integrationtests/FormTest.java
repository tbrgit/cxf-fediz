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

import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.FormField;
import net.htmlparser.jericho.FormFields;
import net.htmlparser.jericho.Source;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;

public abstract class FormTest extends AbstractTests {

    static void init() {
        System.setProperty("idp.authSupportType", "FORM");
        AbstractTests.init();
    }

    @Override
    String sendHttpGet(String url, String user, String password,
            int pseudoReturnCodeIDP, int returnCodeRP) throws Exception {
        Assert.assertTrue("IDP HTTP pseudo response code not supported : [" + pseudoReturnCodeIDP + "]",
                pseudoReturnCodeIDP == 401 || pseudoReturnCodeIDP == 200);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpEntity entity;
            HttpResponse response;
            FormFields formFields;
            Source source;
            Element form;
            List <NameValuePair> nvps; 
            HttpPost httpPost;
            
            configureSSL(httpclient);
            
            HttpGet httpget = new HttpGet(url);
            response = httpclient.execute(httpget);

            entity = getIDPEntity(response);
            source = new Source(EntityUtils.toString(entity));

            form = assertForm(source, "signinform");

            formFields = form.getFormFields();
            url = form.getAttributeValue("action");

            signinFormAsserts(url, formFields);

            httpclient.setRedirectStrategy(new LaxRedirectStrategy());

            nvps = new ArrayList <NameValuePair>();
            fillCredentials(user, password, formFields);
            setUrlParametersList(formFields, nvps);
            HttpHost httpHost = new HttpHost("localhost", Integer
                    .parseInt(getIdpHttpsPort()), "https");
//            HttpPost httpPost = new HttpPost(url 
//                    + "&username=" + user
//                    + "&password=" + password 
//                    + "&execution=" + formFields.get("execution").getValues().get(0)
//                    + "&_eventId_authenticate=authenticate");
            httpPost = new HttpPost(url); 
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            response = httpclient.execute(httpHost, httpPost);

            entity = getIDPEntity(response);
            source = new Source(EntityUtils.toString(entity));

            if (pseudoReturnCodeIDP == 401) {
                form = assertForm(source, "signinform");
                return null;
            }
            if (pseudoReturnCodeIDP == 200) {
                form = assertForm(source, "signinresponseform");
            }

            formFields = form.getFormFields();
            url = form.getAttributeValue("action");

            signinResponseFormAsserts(url, formFields);

            nvps = new ArrayList <NameValuePair>();
            setUrlParametersList(formFields, nvps);
            httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            response = httpclient.execute(httpPost);

            entity = getRPEntity(returnCodeRP, response);

            return EntityUtils.toString(entity);
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

    private void fillCredentials(String user, String password,
            FormFields formFields) {
        formFields.get("username").setValue(user);
        formFields.get("password").setValue(password);
    }

    private HttpEntity getRPEntity(int returnCodeRP, HttpResponse response) {
        HttpEntity entity;
        System.out.println(response.getStatusLine());
        entity = response.getEntity();
        if (entity != null) {
            System.out.println("Response content length: " + entity.getContentLength());
        }
        Assert.assertTrue("RP HTTP Response code: " + response.getStatusLine().getStatusCode()
                          + " [Expected: " + returnCodeRP + "]",
                          returnCodeRP == response.getStatusLine().getStatusCode());
        return entity;
    }

    private HttpEntity getIDPEntity(HttpResponse response) {
        HttpEntity entity;
        System.out.println(response.getStatusLine());
        entity = response.getEntity();
        if (entity != null) {
            System.out.println("Response content length: " + entity.getContentLength());
        }
        Assert.assertTrue("IDP HTTP Response code: "
                + response.getStatusLine().getStatusCode() + " [Expected: 200]", 200 == response
                .getStatusLine().getStatusCode());
        return entity;
    }

    private Element assertForm(Source source, String formName) {
        Element form = source.getElementById(formName);
        Assert.assertNotNull("Form " + formName + " not reached", form);
        return form;
    }

    private void signinFormAsserts(String url, FormFields formFields) {
        Assert.assertNotNull("Form field 'username' not found", formFields.get("username"));
        Assert.assertNotNull("Form field 'password' not found", formFields.get("password"));
        Assert.assertNotNull("Form field 'execution' not found", formFields.get("execution"));
        Assert.assertNotNull("Form field '_eventId_authenticate' not found",
                formFields.get("_eventId_authenticate"));

        Assert.assertNotNull("Query parameter 'action' not found", url);
        Assert.assertTrue("Parameter 'wa' not found", url.contains("wa="));
        Assert.assertTrue("Parameter 'wtrealm' not found", url.contains("wtrealm="));
    }

    private void signinResponseFormAsserts(String url, FormFields formFields) {
        Assert.assertNotNull("Form field 'wa' not found", formFields.get("wa"));
        Assert.assertNotNull("Form field 'wresult' not found", formFields.get("wresult"));
        Assert.assertNotNull("Form field '_eventId_submit' not found",
                formFields.get("_eventId_submit"));

        Assert.assertNotNull("Query parameter 'action' not found", url);
    }

    private void setUrlParametersList(FormFields formFields,
            List<NameValuePair> nvps) {
        for (FormField formField : formFields) {
            if (formField.getUserValueCount() != 0) {
                nvps.add(new BasicNameValuePair(formField.getName(),
                        formField.getValues().get(0)));
            } else {
                if (formField.getName().toLowerCase().startsWith("_eventid_")) {
                    nvps.add(new BasicNameValuePair(formField.getName().replace("_eventid_", "_eventId_"), 
                            formField.getName().toLowerCase().substring("_eventid_".length())));
                }
            }
        }
    }

}

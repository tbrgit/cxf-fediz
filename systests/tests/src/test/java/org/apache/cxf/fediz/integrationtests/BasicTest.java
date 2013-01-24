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

import net.htmlparser.jericho.FormField;
import net.htmlparser.jericho.FormFields;
import net.htmlparser.jericho.Source;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;

public abstract class BasicTest extends AbstractTests {

    static void init() {
        System.setProperty("idp.authSupportType", "BASIC");
        AbstractTests.init();
    }

    @Override
    String sendHttpGet(String url, String user, String password,
            int returnCodeIDP, int returnCodeRP) throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope("localhost",
                            Integer.parseInt(getIdpHttpsPort())),
                    new UsernamePasswordCredentials(user, password));

            configureSSL(httpclient);
            
            HttpGet httpget = new HttpGet(url);

            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("Response content length: "
                        + entity.getContentLength());
            }
            Assert.assertTrue("IDP HTTP Response code: "
                    + response.getStatusLine().getStatusCode() + " [Expected: "
                    + returnCodeIDP + "]", returnCodeIDP == response
                    .getStatusLine().getStatusCode());

            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }

            // Redirect to a POST is not supported without user interaction
            // http://www.ietf.org/rfc/rfc2616.txt
            // If the 301 status code is received in response to a request other
            // than GET or HEAD, the user agent MUST NOT automatically redirect
            // the
            // request unless it can be confirmed by the user, since this might
            // change the conditions under which the request was issued.

            httpclient.setRedirectStrategy(new LaxRedirectStrategy());
            HttpPost httppost = new HttpPost(url);

            Source source = new Source(EntityUtils.toString(entity));
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            FormFields formFields = source.getFormFields();
            Assert.assertNotNull("Form field 'wa' not found",
                    formFields.get("wa"));
            Assert.assertNotNull("Form field 'wresult' not found",
                    formFields.get("wresult"));
            for (FormField formField : formFields) {
                if (formField.getUserValueCount() != 0) {
                    nvps.add(new BasicNameValuePair(formField.getName(),
                            formField.getValues().get(0)));
                }
            }
            httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

            response = httpclient.execute(httppost);

            entity = response.getEntity();
            System.out.println(response.getStatusLine());
            Assert.assertTrue("RP HTTP Response code: "
                    + response.getStatusLine().getStatusCode() + " [Expected: "
                    + returnCodeRP + "]", returnCodeRP == response
                    .getStatusLine().getStatusCode());

            if (entity != null) {
                System.out.println("Response content length: "
                        + entity.getContentLength());
            }

            return EntityUtils.toString(entity);
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

    }
}

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
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class JettyFormTest extends FormTest {

    private static Server idpServer;
    private static Server rpServer;

    @BeforeClass
    public static void init() {
        FormTest.init();
        initIdp();
        initRp();
    }

    public static void initIdp() {
        idpServer = JettyTest.initIdp(AbstractTests.getIdpHttpsPort());
    }

    public static void initRp() {
        rpServer = JettyTest.initRp(AbstractTests.getRpHttpsPort());
    }

    @AfterClass
    public static void cleanup() {
        JettyTest.cleanup(idpServer, rpServer);
    }
}

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

package org.apache.cxf.fediz.core;

import org.w3c.dom.Element;
import org.apache.cxf.fediz.core.config.FederationContext;
import org.apache.cxf.fediz.core.exception.ProcessingException;

public interface TokenValidator {

    /**
     * Return true if this TokenValidator implementation is capable of validating the
     * TokenType argument.
     */
    boolean canHandleTokenType(String tokenType);


    /**
     * Return true if this TokenValidator implementation is capable of validating the
     * Token argument.
     */
    boolean canHandleToken(Element token);


    /**
     * Validate a Token using the given Element and Configuration.
     * @throws ProcessingException 
     */
    TokenValidatorResponse validateAndProcessToken(Element token, FederationContext config) throws ProcessingException;
}

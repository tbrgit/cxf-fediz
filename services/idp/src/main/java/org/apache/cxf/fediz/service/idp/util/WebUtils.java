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
package org.apache.cxf.fediz.service.idp.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.Assert;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.RequestContext;

/**
 * Utility class to bind with webflow artifacts
 * @author T.Beucher 
 */
public final class WebUtils {
    
    private WebUtils() {
        super();
    }

    public static HttpServletRequest getHttpServletRequest(
            final RequestContext context) {
        Assert.isInstanceOf(ServletExternalContext.class,
                context.getExternalContext(),
                "Cannot obtain HttpServletRequest from event of type: "
                        + context.getExternalContext().getClass().getName());
        return (HttpServletRequest) context.getExternalContext()
                .getNativeRequest();
    }

    public static HttpSession getHttpSession(final RequestContext context) {
        HttpServletRequest httpServletRequest = getHttpServletRequest(context);
        return httpServletRequest.getSession();
    }

    public static HttpServletResponse getHttpServletResponse(
            final RequestContext context) {
        Assert.isInstanceOf(ServletExternalContext.class,
                context.getExternalContext(),
                "Cannot obtain HttpServletResponse from event of type: "
                        + context.getExternalContext().getClass().getName());
        return (HttpServletResponse) context.getExternalContext()
                .getNativeResponse();
    }

    public static void putAttributeInRequestScope(final RequestContext context,
            final String attributeKey, final Object attributeValue) {
        context.getRequestScope().put(attributeKey, attributeValue);
    }

    public static void putAttributeInExternalContext(
            final RequestContext context, final String attributeKey,
            final Object attributeValue) {
        context.getExternalContext().getSessionMap()
                .put(attributeKey, attributeValue);
    }

    /**
     * put attribute in request or in session depending on storeInSession.
     * 
     * @param context
     * @param attributeKey
     * @return
     */
    public static void putAttribute(final RequestContext context,
            final String attributeKey, final Object attributeValue,
            boolean storeInSession) {
        // Object oldValue = null;
        if (storeInSession) {
            // oldValue = getAttributeFromExternalContext(context,
            // attributeKey);
            putAttributeInExternalContext(context, attributeKey, attributeValue);
        } else {
            // oldValue = getAttributeFromRequestScope(context, attributeKey);
            putAttributeInRequestScope(context, attributeKey, attributeValue);
        }
        // return oldValue;
    }

    public static Object getAttributeFromRequestScope(
            final RequestContext context, final String attributeKey) {
        return context.getRequestScope().getString(attributeKey);
    }

    public static Object getAttributeFromExternalContext(
            final RequestContext context, final String attributeKey) {
        return context.getExternalContext().getSessionMap()
                .getString(attributeKey);
    }

    /**
     * get attribute from request; if not found get it from session.
     * 
     * @param context
     * @param attributeKey
     * @return
     */
    public static Object getAttribute(final RequestContext context,
            final String attributeKey) {
        Object value = getAttributeFromRequestScope(context, attributeKey);
        if (value != null) {
            return value;
        }
        return getAttributeFromExternalContext(context, attributeKey);
    }

    public static Object removeAttributeFromRequestScope(
            final RequestContext context, final String attributeKey) {
        return context.getRequestScope().remove(attributeKey);
    }

    public static Object removeAttributeFromExternalContext(
            final RequestContext context, final String attributeKey) {
        return context.getExternalContext().getSessionMap()
                .remove(attributeKey);
    }

    /**
     * remove attribute from request and session.
     * 
     * @param context
     * @param attributeKey
     * @return
     */
    public static Object removeAttribute(final RequestContext context,
            final String attributeKey) {
        Object valueReq = removeAttributeFromRequestScope(context, attributeKey);
        Object valueSes = removeAttributeFromExternalContext(context,
                attributeKey);
        if (valueSes != null) {
            return valueSes; // not clean if request has different value !
        }
        if (valueReq != null) {
            return valueReq;
        }
        return null;
    }

    public static void putAttributeInFlowScope(final RequestContext context,
            final String attributeKey, final String attributeValue) {
        context.getFlowScope().put(attributeKey, attributeValue);
    }

    public static String getAttributeFromFlowScope(
            final RequestContext context, final String attributeKey) {
        return context.getFlowScope().getString(attributeKey);
    }

    public static Object removeAttributeFromFlowScope(
            final RequestContext context, final String attributeKey) {
        return context.getFlowScope().remove(attributeKey);
    }

    public static String getParamFromRequestParameters(
            final RequestContext context, final String attributeKey) {
        return context.getRequestParameters().get(attributeKey);
    }

}

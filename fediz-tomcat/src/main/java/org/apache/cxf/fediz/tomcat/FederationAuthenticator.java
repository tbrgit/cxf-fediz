package org.apache.cxf.fediz.tomcat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Session;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.authenticator.FormAuthenticator;
import org.apache.catalina.authenticator.SavedRequest;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.cxf.fediz.core.FederationConfiguration;
import org.apache.cxf.fediz.core.FederationConstants;
import org.apache.cxf.fediz.core.FederationProcessor;
import org.apache.cxf.fediz.core.FederationProcessorImpl;
import org.apache.cxf.fediz.core.FederationRequest;
import org.apache.cxf.fediz.core.FederationResponse;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class FederationAuthenticator extends FormAuthenticator {
	
	//[TODO] Expired token
	
    private static final Log log = LogFactory.getLog(FormAuthenticator.class);
    
    /**
     * Descriptive information about this implementation.
     */
    protected static final String info =
        "org.apache.cxf.fediz.tomcat.WsFedAuthenticator/1.0";
    
    public static final String FEDERATION_NOTE =
        "org.apache.cxf.fediz.tomcat.FEDERATION";
    
    public static final String SECURITY_TOKEN =
        "org.apache.fediz.SECURITY_TOKEN";
    
    /**
     * IssuerURL
     */
    protected String issuerURL = null;
    
    /**
     * Requested Authentication type.
     * See org.apache.cxf.fediz.tomcat.WsFedConstants.AUTH_TYPE_*
     */   
    protected URI authenticationType = null;
    
    /**
     * Trusted Issuer Name
     */
    protected String trustedIssuer = null;
    
    
    /**
     * Truststore file
     */
    protected String truststoreFile = null;
    

	/**
     * Truststore password
     */
    protected String truststorePassword = null;
   


	/**
     * Role URI in Claim
     */
    protected String roleClaimURI = null;
    
    /**
     * Role delimiter in claim value
     */
    protected String roleDelimiter = ",";
	
    
	public FederationAuthenticator() {
		log.debug("WsFedAuthenticator()");
	}
	
    /**
     * Return descriptive information about this Valve implementation.
     */
    @Override
    public String getInfo() {
        return (info);
    }
    
    
    /**
     * Return the character encoding to use to read the username and password.
     */
    public String getIssuerURL() {
        return issuerURL;
    }
    

    /**
     * Set the character encoding to be used to read the username and password. 
     */
    public void setIssuerURL(String issuerURL) {
    	this.issuerURL = issuerURL;
    }
    
    
    /**
     * Return the requested authentication type.
     */
    public URI getAuthenticationType() {
        return authenticationType;
    }

    /**
     * Set the requested authentication type.
     */
    public void setAuthenticationType(String authenticationType) {
    	FederationConstants.AUTH_TYPE_MAP.containsKey(authenticationType);
    	this.authenticationType = FederationConstants.AUTH_TYPE_MAP.get(authenticationType);
    }
    
    public String getTruststorePassword() {
		return truststorePassword;
	}

	public void setTruststorePassword(String truststorePassword) {
		this.truststorePassword = truststorePassword;
	}
    
	
    public String getTruststoreFile() {
		return truststoreFile;
	}

	public void setTruststoreFile(String truststoreFile) {
		this.truststoreFile = truststoreFile;
	}
	
    /**
     * 
     */
    public String getRoleClaimURI() {
        return this.roleClaimURI;
    }

    /**
     * 
     */
    public void setRoleClaimURI(String roleClaimURI) {
    	this.roleClaimURI = roleClaimURI;
    }
    
    /**
     * 
     */
    public String getRoleDelimiter() {
        return this.roleDelimiter;
    }

    /**
     * 
     */
    public void setRoleDelimiter(String roleDelimiter) {
    	this.roleDelimiter = roleDelimiter;
    }
    
    
    /**
     * 
     */
    public String getTrustedIssuer() {
        return this.trustedIssuer;
    }

    /**
     * 
     */
    public void setTrustedIssuer(String trustedIssuer) {
    	this.trustedIssuer = trustedIssuer;
    }
    
    
    
    @Override
    public void invoke(Request request, Response response)
        throws IOException, ServletException {
    	
    	log.debug("WsFedAuthenticator:invoke()");
    	super.invoke(request, response);
    	
    }
	
	@Override
	public boolean authenticate(Request request, HttpServletResponse response,
			LoginConfig config) throws IOException {

		log.debug("authenticate invoked");
        // References to objects we will need later
        Session session = null;

        // Have we already authenticated someone?
        Principal principal = request.getUserPrincipal();
        //String ssoId = (String) request.getNote(Constants.REQ_SSOID_NOTE);
        if (principal != null) {
            if (log.isDebugEnabled())
                log.debug("Already authenticated '" +
                    principal.getName() + "'");
            // Associate the session with any existing SSO session
            /*
               if (ssoId != null)
                associate(ssoId, request.getSessionInternal(true));
             */
            
            // Check whether security token still valid
            session = request.getSessionInternal();
            if (session == null) {
            	log.debug("Session should not be null after authentication");
            } else {
            	FederationResponse wfRes = (FederationResponse)session.getNote(FEDERATION_NOTE);
            	
            	Date tokenExpires = wfRes.getTokenExpires();
            	if (tokenExpires == null) {
            		log.debug("Token doesn't expire");
            		return (true);
            	}
        	    Calendar cal = Calendar.getInstance();
        	    if ( cal.getTime().after(wfRes.getTokenExpires()) ) {
        	    	log.debug("Token already expired. Clean up and redirect");
        	    	
        	    	session.removeNote(FEDERATION_NOTE);
        	    	session.removeNote(Constants.FORM_PRINCIPAL_NOTE);
        	    	session.setPrincipal(null);
        	    	request.getSession().removeAttribute(SECURITY_TOKEN);
        	    	
                    if (log.isDebugEnabled())
                        log.debug("Save request in session '" + session.getIdInternal() + "'");
                    try {
                        saveRequest(request, session);
                    } catch (IOException ioe) {
                        log.debug("Request body too big to save during authentication");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN,
                                sm.getString("authenticator.requestBodyTooBig"));
                        return (false);
                    }
                    redirectToLoginPage(request, response, config);
        	    	
        	    	return (false);
        	    }
            }
            
            return (true);
        }

        // Is this the re-submit of the original request URI after successful
        // authentication?  If so, forward the *original* request instead.
        if (matchRequest(request)) {
            session = request.getSessionInternal(true);
            if (log.isDebugEnabled())
                log.debug("Restore request from session '"
                          + session.getIdInternal() 
                          + "'");
            principal = (Principal)session.getNote(Constants.FORM_PRINCIPAL_NOTE);
            register(request, response, principal, FederationConstants.WSFED_METHOD,
                    null,
                    null);
            
            if (restoreRequest(request, session)) {
                if (log.isDebugEnabled())
                    log.debug("Proceed to restored request");
                return (true);
            } else {
                if (log.isDebugEnabled())
                    log.debug("Restore of original request failed");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return (false);
            }
        }

        // Acquire references to objects we will need to evaluate
/*        
        MessageBytes uriMB = MessageBytes.newInstance();
        CharChunk uriCC = uriMB.getCharChunk();
        uriCC.setLimit(-1);
*/        
        //String contextPath = request.getContextPath();
        String requestURI = request.getDecodedRequestURI();

        
        String wa = request.getParameter("wa");
		// Unauthenticated -> redirect
        if (wa == null) {
            session = request.getSessionInternal(true);
            if (log.isDebugEnabled())
                log.debug("Save request in session '" + session.getIdInternal() + "'");
            try {
                saveRequest(request, session);
            } catch (IOException ioe) {
                log.debug("Request body too big to save during authentication");
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        sm.getString("authenticator.requestBodyTooBig"));
                return (false);
            }
            redirectToLoginPage(request, response, config);
            return (false);
        }
        
        // Check whether it is the signin request, validate the token.
        // If failed, redirect to the error page if they are not correct
        String wresult = request.getParameter("wresult");
        FederationResponse wfRes = null;
		if ( wa.equals(FederationConstants.ACTION_SIGNIN) ) {
			if (log.isDebugEnabled())
                log.debug("SignIn request found");
			log.debug("SignIn action...");
			
			if (wresult == null) {
				if (log.isDebugEnabled())
					log.debug("SignIn request must contain wresult");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return (false);
			}
			else {
				request.getResponse().sendAcknowledgement();
				//processSignInRequest
				if (log.isDebugEnabled()){
	                log.debug("Process SignIn request");
	                log.debug("wresult=\n" + wresult);
				}
				
				FederationRequest wfReq = new FederationRequest();
				wfReq.setWa(wa);
				wfReq.setWresult(wresult);
				//wfReq.setWtrealm(wtrealm);
				
				FederationConfiguration fedConfig = new FederationConfiguration();
				fedConfig.setTrustedIssuer(this.getTrustedIssuer());
				fedConfig.setRoleDelimiter(this.getRoleDelimiter());
				if (this.getRoleClaimURI() == null || this.getRoleClaimURI().length() == 0) {
					fedConfig.setRoleURI(FederationConstants.DEFAULT_ROLE_URI);
				}
				else {
					fedConfig.setRoleURI(URI.create(this.getRoleClaimURI()));
				}
				
				
				if (this.getTruststoreFile() == null || this.getTruststoreFile().length() == 0) {
					log.error("Truststore file configuration must be checked before redirect to IDP");
					//TODO would an exception not be the better solution here ?
					return false;
				}
				if (this.getTruststorePassword() == null || this.getTruststorePassword().length() == 0) {
					log.error("Truststore password configuration must be checked before redirect to IDP");
					//TODO would an exception not be the better solution here ?
					return false;
				}
				else {
					if ( (new File(getTruststoreFile())).exists() ) {
						fedConfig.setTrustStoreFile(this.getTruststoreFile());
					} else {
						String catalinaHome = System.getProperty("catalina.home");
						if (catalinaHome != null && catalinaHome.length() > 0) {
							
							String fqTruststoreFile = catalinaHome.concat(File.separator + getTruststoreFile());
							this.setTruststoreFile(fqTruststoreFile);
							fedConfig.setTrustStoreFile(this.getTruststoreFile());
						}
						else {
							log.error("Truststore file configuration not valid");
							return false;
						}
					}
										
					fedConfig.setTrustStoreFile(this.getTruststoreFile());
					fedConfig.setTrustStorePassword(this.getTruststorePassword());
					if (log.isDebugEnabled()) {
						log.debug("Truststore file: " + fedConfig.getTrustStoreFile());
						log.debug("Truststore password: " + fedConfig.getTrustStorePassword());
					}
				}
				
				
				FederationProcessor wfProc = new FederationProcessorImpl();
				wfRes = wfProc.processRequest(wfReq, fedConfig);
				
				if ( wfRes.getAudience() != null && request.getRequestURL().indexOf(wfRes.getAudience()) == -1 ) {
					log.debug("Audience doesn't match with request URL [" + wfRes.getAudience() + "]  [" + request.getRequestURL() + "]");
				}
				
				List<String> roles = wfRes.getRoles();
				if (roles == null || roles.size() == 0) {
					roles = new ArrayList<String>();
					roles.add(new String("Authenticated"));
				}
				
				principal = new FederationPrincipal(wfRes.getUsername(), roles, wfRes.getClaims());
				
				//[TODO] Cache lifetime (in session), token (in session/TLS), ?audience?
				//[TODO] clocksqew
			}
		}
		else {
			log.error("Not supported action found in parameter wa: " + wa);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return (false);
		}

        
        
        /*
        Realm realm = context.getRealm();
        if (characterEncoding != null) {
            request.setCharacterEncoding(characterEncoding);
        
        String username = request.getParameter(Constants.FORM_USERNAME);
        String password = request.getParameter(Constants.FORM_PASSWORD);
        if (log.isDebugEnabled())
            log.debug("Authenticating username '" + username + "'");
        principal = realm.authenticate(username, password);
        */
        if (principal == null) {
            forwardToErrorPage(request, response, config);
            return (false);
        }

        if (log.isDebugEnabled()) {
            log.debug("Authentication of '" + principal + "' was successful");
        }
        //context.addServletContainerInitializer(sci, classes)
        //session.addSessionListener(listener)
        //HttpSessionAttributeListener
        
        if (session == null)
            session = request.getSessionInternal(false);
        if (session == null) {
            if (containerLog.isDebugEnabled())
                containerLog.debug
                    ("User took so long to log on the session expired");
            if (landingPage == null) {
                response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT,
                        sm.getString("authenticator.sessionExpired"));
            } else {
                // Make the authenticator think the user originally requested
                // the landing page
                String uri = request.getContextPath() + landingPage;
                SavedRequest saved = new SavedRequest();
                saved.setMethod("GET");
                saved.setRequestURI(uri);
                request.getSessionInternal(true).setNote(
                        Constants.FORM_REQUEST_NOTE, saved);
                response.sendRedirect(response.encodeRedirectURL(uri));
            }
            return (false);
        }

        // Save the authenticated Principal in our session
        session.setNote(Constants.FORM_PRINCIPAL_NOTE, principal);
        
        // Save Federation response in our session
        session.setNote(FEDERATION_NOTE, wfRes);
        
        // Save Federation response in public session
        request.getSession(true).setAttribute(SECURITY_TOKEN, wfRes.getToken());

/*
        // Save the username and password as well
        session.setNote(Constants.SESS_USERNAME_NOTE, username);
        session.setNote(Constants.SESS_PASSWORD_NOTE, password);
*/
        // Redirect the user to the original request URI (which will cause
        // the original request to be restored)
        requestURI = savedRequestURL(session);
        if (log.isDebugEnabled())
            log.debug("Redirecting to original '" + requestURI + "'");
        if (requestURI == null)
            if (landingPage == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        sm.getString("authenticator.formlogin"));
            } else {
                // Make the authenticator think the user originally requested
                // the landing page
                String uri = request.getContextPath() + landingPage;
                SavedRequest saved = new SavedRequest();
                saved.setMethod("GET");
                saved.setRequestURI(uri);
                session.setNote(Constants.FORM_REQUEST_NOTE, saved);
                
                response.sendRedirect(response.encodeRedirectURL(uri));
            }
        else
            response.sendRedirect(response.encodeRedirectURL(requestURI));
        return (false);
	}

	@Override
	protected String getAuthMethod() {
		return FederationConstants.WSFED_METHOD;
	}

    /**
     * Called to redirect to the login page
     * 
     * @param request Request we are processing
     * @param response Response we are populating
     * @param config    Login configuration describing how authentication
     *              should be performed
     * @throws IOException  If the forward to the login page fails and the call
     *                      to {@link HttpServletResponse#sendError(int, String)}
     *                      throws an {@link IOException}
     */
    protected void redirectToLoginPage(Request request,
            HttpServletResponse response, LoginConfig config)
            throws IOException {
        
    	String redirectURL = null;
    	String issuerURL = getIssuerURL();
    	if (issuerURL != null && issuerURL.length() > 0) {
    		redirectURL = issuerURL;
    	}
    	String loginPage = config.getLoginPage();
    	if (redirectURL == null) {
    		if (loginPage != null &&  loginPage.length() > 0) {
    			redirectURL = loginPage;
    		} else {
    			String msg = sm.getString("formAuthenticator.noLoginPage",
    					context.getName());
    			log.warn(msg);
    			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
    					msg);
    			return;
    		}
    	}
        StringBuilder sb = new StringBuilder();
    	
    	//StringBuilder sb = new StringBuilder(redirectURL);
        //sb.append('?');
    	
        sb.append(FederationConstants.PARAM_ACTION).append('=').append(FederationConstants.ACTION_SIGNIN);

    	sb.append('&').append(FederationConstants.PARAM_REPLY).append('=');
    	sb.append(URLEncoder.encode(request.getRequestURL().toString(), "UTF-8"));       	
        	
        	
    	/*
    	 * http://hostname.com:80/mywebapp/servlet/MyServlet/a/b;c=123?d=789
    	public static String getUrl3(HttpServletRequest req) {
    	    String scheme = req.getScheme();             // http
    	    String serverName = req.getServerName();     // hostname.com
    	    int serverPort = req.getServerPort();        // 80
    	    String contextPath = req.getContextPath();   // /mywebapp
    	*/
    	String contextPath = request.getContextPath();
    	String requestUrl = request.getRequestURL().toString();
    	int ctxIn = requestUrl.indexOf(contextPath);
    	//String realm = request.getRequestURL().toString();
    	String realm = requestUrl.substring(0, ctxIn + contextPath.length() + 1);
    	
    	StringBuffer realmSb = new StringBuffer(request.getScheme());
    	realmSb.append("://").append(request.getServerName()).
    	        append(":").append(request.getServerPort()).
    	        append(request.getContextPath());
//    	sb.append('&').append(WsFedConstants.PARAM_TREALM).append('=').append(realmSb.toString());
    	sb.append('&').append(FederationConstants.PARAM_TREALM).append('=').append(URLEncoder.encode(realm, "UTF-8"));
    	
    	
    	//[TODO] Current time, wct
    	
//        if (false) {
//        	sb.append("&");
//        	sb.append("wfresh=jjjj"); 
//        }
//        if (false) {
//        	sb.append("&");
//        	sb.append("wauth=jjjj"); 
//        }
//        if (false) {
//        	sb.append("&");wct
//        	sb.append("wreq=jjjj"); 
//        }
//        if (false) {
//    	    sb.append("&");
//    	    sb.append("wct=").append("jjjj");
//        }

    	        
        //WORKS, why didn't it when sb.toSring(contained redirectURL)
        //response.sendRedirect(response.encodeRedirectURL(redirectURL + "?" + response.encodeURL(sb.toString())));
    	//response.sendRedirect(redirectURL + "?" + response.encodeURL(sb.toString()));
    	response.sendRedirect(redirectURL + "?" + sb.toString());
        
        //WORKS NOW TOO, no, maybe already signed in, session with idp
        //response.sendRedirect(response.encodeRedirectURL(sb.toString()));
        
    }
	
	
}

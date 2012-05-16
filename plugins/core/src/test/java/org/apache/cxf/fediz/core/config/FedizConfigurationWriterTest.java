package org.apache.cxf.fediz.core.config;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.cxf.fediz.core.config.jaxb.ArgumentType;
import org.apache.cxf.fediz.core.config.jaxb.AudienceUris;
import org.apache.cxf.fediz.core.config.jaxb.AuthenticationType;
import org.apache.cxf.fediz.core.config.jaxb.CertificateStores;
import org.apache.cxf.fediz.core.config.jaxb.ClaimType;
import org.apache.cxf.fediz.core.config.jaxb.ClaimTypesRequested;
import org.apache.cxf.fediz.core.config.jaxb.ContextConfig;
import org.apache.cxf.fediz.core.config.jaxb.FederationProtocolType;
import org.apache.cxf.fediz.core.config.jaxb.FedizConfig;
import org.apache.cxf.fediz.core.config.jaxb.HomeRealm;
import org.apache.cxf.fediz.core.config.jaxb.KeyStoreType;
import org.apache.cxf.fediz.core.config.jaxb.TrustManagersType;
import org.apache.cxf.fediz.core.config.jaxb.TrustedIssuerType;
import org.apache.cxf.fediz.core.config.jaxb.TrustedIssuers;
import org.apache.cxf.fediz.core.config.jaxb.ValidationType;
import org.junit.Assert;

public class FedizConfigurationWriterTest {

    private static final String TRUST_ISSUER_CERT_CONSTRAINT = ".*CN=www.sts.com.*";
    private static final String TRUST_ISSUER_NAME = "Apache FEDIZ IDP";
    private static final String ROLE_URI = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role";
    private static final String ROLE_DELIMITER = ";";

    private static final String ISSUER = "http://url_to_the_issuer";
    private static final String PROTOCOL_VERSION = "1.0.0";
    private static final String REPLY = "reply value";
    private static final String TARGET_REALM = "target realm";
    private static final String HOME_REALM_CLASS = "org.apache.fediz.realm.MyHomeRealm.class";
    private static final String FRESHNESS_VALUE = "10000";

    private static final String CONFIG_NAME = "ROOT";
    private static final String CLOCK_SKEW = "1000";
    private static final String KEYSTORE_FILE = "stsstore.jks";

    private static final String JKS_TYPE = "JKS";

    private static final String KEYSTORE_PASSWORD = "stsspass";
    private static final String AUDIENCE_URI_1 = "http://host_one:port/url";

    private static final String AUTH_TYPE_VALUE = "some auth type";

    private static final String CLAIM_TYPE_1 = "a particular claim type";

    private static final String CONFIG_FILE = "./target/fediz_test_config.xml";

    private FedizConfig createConfiguration() throws JAXBException {

        FedizConfig rootConfig = new FedizConfig();
        ContextConfig config = new ContextConfig();
        rootConfig.getContextConfig().add(config);

        config.setName(CONFIG_NAME);
        config.setMaximumClockSkew(new BigInteger(CLOCK_SKEW));
        //config.setCertificateValidation(ValidationType.CHAIN_TRUST);

        // TrustManagersType tm0 = new TrustManagersType();
        //
        // KeyStoreType ks0 = new KeyStoreType();
        // ks0.setType(FILE_TYPE);
        // ks0.setPassword(KEYSTORE_PASSWORD);
        // ks0.setFile(KEYSTORE_FILE);
        //
        // tm0.setKeyStore(ks0);
        //
        // config.setServiceCertificate(tm0);

        FederationProtocolType protocol = new FederationProtocolType();
        config.setProtocol(protocol);

        TrustedIssuers trustedIssuers = new TrustedIssuers();
             
        TrustedIssuerType trustedIssuer = new TrustedIssuerType();
        trustedIssuer.setCertificateValidation(ValidationType.CHAIN_TRUST);
        trustedIssuer.setName(TRUST_ISSUER_NAME);
        trustedIssuer.setSubject(TRUST_ISSUER_CERT_CONSTRAINT);
        trustedIssuers.getIssuer().add(trustedIssuer);
        config.setTrustedIssuers(trustedIssuers);

        CertificateStores certStores = new CertificateStores();
        TrustManagersType truststore = new TrustManagersType();
        
        KeyStoreType ks1 = new KeyStoreType();
        ks1.setType(JKS_TYPE);
        ks1.setPassword(KEYSTORE_PASSWORD);
        ks1.setFile(KEYSTORE_FILE);
        truststore.setKeyStore(ks1);
        certStores.getTrustManager().add(truststore);
        config.setCertificateStores(certStores);

        AuthenticationType authType = new AuthenticationType();
        authType.setType(ArgumentType.STRING);
        authType.setValue(AUTH_TYPE_VALUE);

        AudienceUris audienceUris = new AudienceUris();
        audienceUris.getAudienceItem().add(AUDIENCE_URI_1);
        config.setAudienceUris(audienceUris);

        protocol.setAuthenticationType(authType);
        protocol.setRoleDelimiter(ROLE_DELIMITER);
        protocol.setRoleURI(ROLE_URI);

        ClaimTypesRequested claimTypeReq = new ClaimTypesRequested();
        ClaimType claimType = new ClaimType();
        claimType.setOptional(true);
        claimType.setType(CLAIM_TYPE_1);
        claimTypeReq.getClaimType().add(claimType);

        protocol.setClaimTypesRequested(claimTypeReq);

        protocol.setFreshness(FRESHNESS_VALUE);

        HomeRealm homeRealm = new HomeRealm();
        homeRealm.setType(ArgumentType.CLASS);
        homeRealm.setValue(HOME_REALM_CLASS);

        protocol.setHomeRealm(homeRealm);
        protocol.setRealm(TARGET_REALM);
        protocol.setReply(REPLY);
        protocol.setRequest("REQUEST");
        protocol.setVersion(PROTOCOL_VERSION);
        protocol.setIssuer(ISSUER);

        return rootConfig;

    }

    @org.junit.Test
    public void readWriteConfig() throws JAXBException {

        final JAXBContext jaxbContext = JAXBContext
                .newInstance(FedizConfig.class);
        FedizConfig configOut = createConfiguration();

        StringWriter writer = new StringWriter();
        jaxbContext.createMarshaller().marshal(configOut, writer);

        StringReader reader = new StringReader(writer.toString());
        jaxbContext.createUnmarshaller().unmarshal(reader);
    }

    @org.junit.Test
    public void testSaveConfig() throws JAXBException, IOException {
        final JAXBContext jaxbContext = JAXBContext
                .newInstance(FedizConfig.class);

        FederationConfigurator configurator = new FederationConfigurator();
        FedizConfig configOut = createConfiguration();
        StringWriter writer = new StringWriter();
        jaxbContext.createMarshaller().marshal(configOut, writer);
        StringReader reader = new StringReader(writer.toString());
        configurator.loadConfig(reader);

        File f = new File(CONFIG_FILE);
        f.createNewFile();

        configurator.saveConfiguration(f);
    }

    @org.junit.Test
    public void testLoadConfig() throws JAXBException {
        FederationConfigurator configurator = new FederationConfigurator();
        File f = new File(CONFIG_FILE);
        configurator.loadConfig(f);
    }

    @org.junit.Test
    public void verifyConfig() throws JAXBException {

        final JAXBContext jaxbContext = JAXBContext
                .newInstance(FedizConfig.class);
        
        /**
         * Test JAXB part
         */

        FederationConfigurator configurator = new FederationConfigurator();
        FedizConfig configOut = createConfiguration();
        StringWriter writer = new StringWriter();
        jaxbContext.createMarshaller().marshal(configOut, writer);
        StringReader reader = new StringReader(writer.toString());
        configurator.loadConfig(reader);

        ContextConfig config = configurator.getContextConfig(CONFIG_NAME);
        Assert.assertNotNull(config);
        AudienceUris audience = config.getAudienceUris();
        Assert.assertEquals(1, audience.getAudienceItem().size());
        Assert.assertTrue(config.getProtocol() instanceof FederationProtocolType);
        FederationProtocolType fp = (FederationProtocolType) config
                .getProtocol();

        Assert.assertEquals(HOME_REALM_CLASS, fp.getHomeRealm().getValue());
        //Assert.assertEquals(config.getCertificateValidation(),ValidationType.CHAIN_TRUST);
        
        /**
         * Check Runtime configuration
         */
        FederationContext fedContext = configurator.getFederationContext(CONFIG_NAME);
        Protocol protocol = fedContext.getProtocol();
        Assert.assertTrue(protocol instanceof FederationProtocol);
        FederationProtocol fedProtocol = (FederationProtocol) protocol;
        Assert.assertEquals(TARGET_REALM,fedProtocol.getRealm());
        
        Authentication auth = fedProtocol.getAuthenticationType();
        Assert.assertEquals(auth.getType(),PropertyType.STRING);
        Assert.assertEquals(auth.getValue(),AUTH_TYPE_VALUE);
        
        //Assert.assertEquals(ValidationMethod.CHAIN_TRUST, fedContext.getCertificateValidation());
        List<String> audienceUris = fedContext.getAudienceUris();
        Assert.assertEquals(1,audienceUris.size());
        List<TrustedIssuer> trustedIssuers = fedContext.getTrustedIssuers();
        Assert.assertEquals(1,trustedIssuers.size());
        TrustedIssuer issuer = trustedIssuers.get(0);
        Assert.assertEquals(TRUST_ISSUER_NAME, issuer.getName());
        Assert.assertEquals(CertificateValidationMethod.CHAIN_TRUST, issuer.getCertificateValidationMethod());
        Assert.assertEquals(TRUST_ISSUER_CERT_CONSTRAINT, issuer.getSubject());
        
        List<TrustManager> trustManagers = fedContext.getCertificateStores();
        Assert.assertEquals(1,trustManagers.size());
        TrustManager manager = trustManagers.get(0);
        KeyStore keyStore = manager.getKeyStore();
        Assert.assertEquals(JKS_TYPE, keyStore.getType());
        Assert.assertEquals(KEYSTORE_FILE, keyStore.getFile());
        Assert.assertEquals(KEYSTORE_PASSWORD, keyStore.getPassword());

    }

}
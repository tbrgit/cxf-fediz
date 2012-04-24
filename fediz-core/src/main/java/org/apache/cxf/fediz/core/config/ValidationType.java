//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.28 at 02:33:06 PM CEST 
//

package org.apache.cxf.fediz.core.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for validationType.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="validationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PeerTrust"/>
 *     &lt;enumeration value="ChainTrust"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "validationType")
@XmlEnum
public enum ValidationType {

    @XmlEnumValue("PeerTrust")
    PEER_TRUST("PeerTrust"), @XmlEnumValue("ChainTrust")
    CHAIN_TRUST("ChainTrust");
    private final String value;

    ValidationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ValidationType fromValue(String v) {
        for (ValidationType c : ValidationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

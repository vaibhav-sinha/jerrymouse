//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.10.13 at 09:47:35 PM IST 
//


package com.github.vaibhavsinha.jerrymouse.model.descriptor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * 	The ejb-ref-typeType contains the expected type of the
 * 	referenced enterprise bean.
 * 
 * 	The ejb-ref-type designates a value
 * 	that must be one of the following:
 * 
 * 	    Entity
 * 	    Session
 * 
 *       
 * 
 * <p>Java class for ejb-ref-typeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ejb-ref-typeType">
 *   &lt;simpleContent>
 *     &lt;restriction base="&lt;http://java.sun.com/xml/ns/j2ee>string">
 *     &lt;/restriction>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ejb-ref-typeType")
public class EjbRefTypeType
    extends String
{


}

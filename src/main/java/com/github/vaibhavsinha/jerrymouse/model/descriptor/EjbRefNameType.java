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
 * 
 * 	  The ejb-ref-name element contains the name of an EJB
 * 	  reference. The EJB reference is an entry in the
 * 	  Deployment Component's environment and is relative to the
 * 	  java:comp/env context.  The name must be unique within the
 * 	  Deployment Component.
 * 
 * 	  It is recommended that name is prefixed with "ejb/".
 * 
 * 	  Example:
 * 
 * 	  <ejb-ref-name>ejb/Payroll</ejb-ref-name>
 * 
 * 	  
 *       
 * 
 * <p>Java class for ejb-ref-nameType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ejb-ref-nameType">
 *   &lt;simpleContent>
 *     &lt;restriction base="&lt;http://java.sun.com/xml/ns/j2ee>jndi-nameType">
 *     &lt;/restriction>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ejb-ref-nameType")
public class EjbRefNameType
    extends JndiNameType
{


}

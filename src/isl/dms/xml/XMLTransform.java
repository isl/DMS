/*
 * Copyright 2006-2015 Institute of Computer Science,
 * Foundation for Research and Technology - Hellas
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 *
 * Contact:  POBox 1385, Heraklio Crete, GR-700 13 GREECE
 * Tel:+30-2810-391632
 * Fax: +30-2810-391638
 * E-mail: isl@ics.forth.gr
 * http://www.ics.forth.gr/isl
 *
 * Authors : Nikos Papadopoulos, Georgios Samaritakis, Konstantina Konsolaki.
 *
 * This file is part of the DMS project.
 */
package isl.dms.xml;

import isl.dbms.DBMSException;
import isl.dms.DMSException;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Element;

public class XMLTransform {
	private Element XMLElem;
	private String transformBase;
	
	/**
	 * Creates a new <code>XMLTransform</code>. The new
	 * <code>XMLTransform</code> represents an XML formatted
	 * <code>String</code>..
	 * 
	 * @param XML
	 *            the XML <code>String</code> to be represented.
	 * @throws DMSException
	 *             with expected error codes.
	 */
	public XMLTransform(String XML) throws DMSException {
		try {
			DocumentBuilderFactory docBuilderF = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderF.newDocumentBuilder();
			this.setElement(docBuilder.parse(
					new org.xml.sax.InputSource(new java.io.StringReader(XML)))
					.getDocumentElement());
		} catch (ParserConfigurationException PCEx) {
			throw new DMSException(PCEx.getMessage());
		} catch (java.io.IOException IOEx) {
			throw new DMSException(IOEx.getMessage());
		} catch (org.xml.sax.SAXException SAXEx) {
			throw new DMSException(SAXEx.getMessage());
		}
	}

    /**
     * Getter method for XMLElem.
     * @return Returns XMLElem as an <CODE>Element</CODE>.
     */
	protected Element getElement() {
        return XMLElem;
    }

    /**
     * Setter method for XMLElem.
     * @param XMLElem XMLElem to set.
     */
    protected void setElement(Element XMLElem) {
    	this.XMLElem = XMLElem;
    }

	/**
	 * Sets the 'base' for the transformation.
	 * 
	 * @param base the 'base' wich the transformation will be based upon.
	 * @throws DMSException with expected error codes.
	 */
	public void setTransformBase(String base) throws DMSException {
		try{
			this.transformBase = base;
		} catch (Exception Ex) {
			throw new DMSException(Ex.getMessage());
		}
	}

	/**
	 * Returns the content of the <code>XMLElement</code>.
	 * 
	 * @return a <code>String</code> representing the XML content
	 *             of this <code>XMLElement</code>.
	 */
    public String toString() {
    	try{
    		StringWriter output = new StringWriter();
    		StreamResult outResult = new StreamResult(output);
    		DOMSource domSource = new DOMSource(this.getElement());
    		TransformerFactory.newInstance().newTransformer().transform(domSource, outResult);
    		return output.toString();
    	} catch (Exception Ex) {
    		Ex.printStackTrace();
    		return null;
    	}
    }

	/**
	 * Tranforms this <code>XMLDocument</code> based upon another
	 * <code>XMLDocument</code>.
	 * 
	 * @param xsl
	 *            the <code>XMLDocument</code> upon which the transformation
	 *            is basen on.
	 * @return the result of the transformation as a <code>String</code>.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public String transform(XMLDocument xsl) throws DMSException, DBMSException {
		try {
			String xml = this.toString();
			StreamSource xmlSource = new StreamSource(new StringReader(xml));
			StreamSource xsltSource = new StreamSource(new StringReader(xsl.getContent()));
			if (this.transformBase != null)
				xsltSource.setSystemId(this.transformBase+"/"+xsl.getName());

			// get the factory
			TransformerFactory transFact = TransformerFactory.newInstance();

			// get a transformer for this particular stylesheet
			Transformer trans = transFact.newTransformer(xsltSource);

			StringWriter out = new StringWriter();
			// do the transformation
			StreamResult output = new StreamResult(out);

			trans.transform(xmlSource, output);
			return out.toString();
		} catch (TransformerException ex) {
			throw new DMSException(ex.getMessage());
		}
	}

	/**
	 * Tranforms this <code>XMLDocument</code> based upon another
	 * <code>XMLDocument</code> and the results of the transformation are
	 * redirected to the specified <code>PrintWriter</code>.
	 * 
	 * @param out
	 *            the <code>PrintWriter</code> to which the results
	 *            of the transformation are redirected.
	 * @param xsl
	 *            the <code>XMLDocument</code> upon which the transformation
	 *            is basen on.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void transform(PrintWriter out, XMLDocument xsl) throws DMSException, DBMSException {
		try {
			String xml = this.toString();
			Source xmlSource = new StreamSource(new StringReader(xml));
			Source xsltSource = new StreamSource(new StringReader(xsl.getContent()));
			if (this.transformBase != null)
				xsltSource.setSystemId(this.transformBase+"/"+xsl.getName());

			// get the factory
			TransformerFactory transFact = TransformerFactory.newInstance();

			// get a transformer for this particular stylesheet
			Transformer trans = transFact.newTransformer(xsltSource);

			// do the transformation
			StreamResult output = new StreamResult(out);
			trans.transform(xmlSource, output);
			out.flush();
		} catch (TransformerException ex) {
			throw new DMSException(ex.getMessage());
		}
	}

	/**
	 * Tranforms this <code>XMLDocument</code> based upon a system
	 * identifier (URL).
	 * 
	 * @param systemId
	 *            the system identifier upon which the transformation
	 *            is basen on.
	 * @return the result of the transformation as a <code>String</code>.
	 * @throws DMSException with expected error codes.
	 */
	public String transform(String systemId) throws DMSException {
		try {
			String xml = this.toString();
			StreamSource xmlSource = new StreamSource(new StringReader(xml));
			StreamSource xsltSource = new StreamSource(systemId);

			// get the factory
			TransformerFactory transFact = TransformerFactory.newInstance();

			// get a transformer for this particular stylesheet
			Transformer trans = transFact.newTransformer(xsltSource);

			StringWriter out = new StringWriter();
			// do the transformation
			StreamResult output = new StreamResult(out);

			trans.transform(xmlSource, output);
			return out.toString();
		} catch (TransformerException ex) {
			throw new DMSException(ex.getMessage());
		}
	}

	/**
	 * Tranforms this <code>XMLDocument</code> based upon a system
	 * identifier (URL) and the results of the transformation are
	 * redirected to the specified <code>PrintWriter</code>.
	 * 
	 * @param out
	 *            the <code>PrintWriter</code> to which the results
	 *            of the transformation are redirected.
	 * @param systemId
	 *            the system identifier upon which the transformation
	 *            is basen on.
	 * @throws DMSException with expected error codes.
	 */
	public void transform(PrintWriter out, String systemId) throws DMSException {
		try {
			String xml = this.toString();
			StreamSource xmlSource = new StreamSource(new StringReader(xml));
			StreamSource xsltSource = new StreamSource(systemId);

			// get the factory
			TransformerFactory transFact = TransformerFactory.newInstance();

			// get a transformer for this particular stylesheet
			Transformer trans = transFact.newTransformer(xsltSource);

			// do the transformation
			StreamResult output = new StreamResult(out);

			trans.transform(xmlSource, output);
		} catch (TransformerException ex) {
			throw new DMSException(ex.getMessage());
		}
	}
}

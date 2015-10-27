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

import isl.dms.DMSException;

import java.io.StringWriter;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

/**
 * This class handles XML files.
 * @author samarita
 */
public class XMLElement {
    /**
     * XML Element.
     */
    private Element XMLElem;
    
    /**
     * XML Document.
     */
    private Document XMLDoc;
    /**
     * Type of XMLElement: Element or Text
     */
    private String XMLType;
    /**
     * XML Text
     */
    private Text XMLText;
    /**
     * Integer Constant = 0
     */
    public static final int ELEMENT = 0;
    /**
     * Integer Constant = 1
     */
    public static final int TEXT = 1;
    /**
     * Integer Constant = 10
     */
    public static final int ALL = 10;

    /**
     * Constructs a new XMLElement from a Node.
     * @param node DOM Tree of the new <CODE>XMLElement</CODE>
     */
    public XMLElement(Node node) {
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            XMLElem=(Element)node;
            XMLType="Element";
            XMLDoc=XMLElem.getOwnerDocument();
        } else if (node.getNodeType() == Node.TEXT_NODE){
            XMLText=(Text)node;
            XMLType="Text";
            XMLDoc=XMLText.getOwnerDocument();
        }
    }
    
    /**
     * Constructs a new XMLElement inside a Document of a chosen type (Element or Text).
     * If chosen type is Text then string is the text content. If type is Element then string is the element name.
     * @param type TEXT for objects of type "Text" or ELEMENT for objects of type "Element"
     * @param string Element name for objects of type "Element" or text for objects of type "Text"
     * @throws DMSException with expected error codes.
     */
    public XMLElement(int type, String string) throws DMSException {
        if (type==ALL) {
            throw new DMSException("ALL is not a valid type!");
        } else {
            Document doc = createNewDocument();
            XMLDoc = doc;
            if (type==ELEMENT){
                XMLType="Element";
                XMLElem = doc.createElement(string);
            } else if (type==TEXT) {
                XMLType="Text";
                XMLText=doc.createTextNode(string);
            }
        }
    }
    
    /**
     * Constructs a new XMLElement of type "Element" with
     * the given name and string context. That new object
     * is created inside a new Document.
     * 
     * @param text Text content of new object.
     * @param name Name of the new object
     * @throws DMSException with expected error codes.
     */
    public XMLElement(String name, String text) throws DMSException {
        Document doc = createNewDocument();
        XMLDoc = doc;
        XMLType="Element";
        XMLElem=XMLDoc.createElement(name);
        XMLElem.setTextContent(text);
    }
    
	/**
	 * Creates a new <code>XMLElement</code>. The new
	 * <code>XMLElement</code> represents an XML formatted
	 * <code>String</code>.
	 * 
	 * @param XML
	 *            the XML <code>String</code> to be represented.
	 * @throws DMSException
	 *             with expected error codes.
	 */
	public XMLElement(String XML) throws DMSException {
		try {
			DocumentBuilderFactory docBuilderF = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderF.newDocumentBuilder();
			this.setElement(docBuilder.parse(
					new org.xml.sax.InputSource(new java.io.StringReader(XML))).getDocumentElement());
		} catch (ParserConfigurationException PCEx) {
			throw new DMSException(PCEx.getMessage());
		} catch (java.io.IOException IOEx) {
			throw new DMSException(IOEx.getMessage());
		} catch (org.xml.sax.SAXException SAXEx) {
			throw new DMSException(SAXEx.getMessage());
		}
	}

    /**
     * Constructs a new empty XMLElement.
     */
    protected XMLElement() {
        XMLElem = null;
        XMLText = null;
        XMLDoc = null;
        XMLType = null;
    }
    
    /**
     * Creates an empty Document so that new XMLElemets can be created in there.
     * @throws isl.dms.DMSException with expected error codes.
     * @return New empty <CODE>Document</CODE>.
     */
    private Document createNewDocument() throws DMSException {
        try {
            DocumentBuilderFactory factory;
            DocumentBuilder builder;
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            builder = factory.newDocumentBuilder();
            
            return builder.newDocument();
        } catch (ParserConfigurationException e) {
            throw new DMSException("Error creating new DOM Document");
        }
    }
    
	/**
	 * Returns the XML representation of the <code>XMLElement</code>
	 *  as a <code>String</code>.
	 * 
	 * @return an XML <code>String</code> representing this <code>XMLElement</code>.
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
     * The parent of this object. All nodes, except Attr, Document, DocumentFragment, Entity, and Notation may have a parent. However, if a node has just been created and not yet added to the tree, or if it has been removed from the tree, this is null.
     * @throws isl.dms.DMSException with expected error codes
     * @return Returns the parent of this object as a <CODE>XMLElement</CODE>
     */
    public XMLElement getParent() throws DMSException {
        try{
            XMLElement parent = null;
            if (this.XMLType.equals("Element")) {
                parent = new XMLElement(this.XMLElem.getParentNode());
            } else if (this.XMLType.equals("Text")) {
                parent = new XMLElement(this.XMLText.getParentNode());
            }
            return parent;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * The first child of this object. Only objects of type Element have children. If there are no children returns null.
     * @throws isl.dms.DMSException with expected error codes
     * @return Returns the first child of this object as a <CODE>XMLElement</CODE>
     */
    public XMLElement getFirstChild() throws DMSException {
        try{
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            XMLElement firstChild = null;
            firstChild = new XMLElement(this.XMLElem.getFirstChild());
            return firstChild;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * The last child of this object. Only objects of type Element have children. If there are no children returns null.
     * @throws isl.dms.DMSException with expected error codes
     * @return Returns the last child of this object as a <CODE>XMLElement</CODE>
     */
    public XMLElement getLastChild() throws DMSException {
        try{
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            XMLElement lastChild = null;
            lastChild = new XMLElement(this.XMLElem.getLastChild());
            return lastChild;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * The XMLElement immediately following this XMLElement. If there is no such object, this returns null.
     * @throws isl.dms.DMSException with expected error codes
     * @return Returns the next sibling as a <CODE>XMLElement</CODE>
     */
    
    public XMLElement getNextSibling() throws DMSException {
        try{
            XMLElement sibling = null;
            if (this.XMLType.equals("Element")) {
                sibling = new XMLElement(this.XMLElem.getNextSibling());
            } else if (this.XMLType.equals("Text")) {
                sibling = new XMLElement(this.XMLText.getNextSibling());
            }
            return sibling;
            
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * The XMLElement of a specific type immediately following this XMLElement. If there is no such object, this returns null.
     * @return Returns the next sibling as a <CODE>XMLElement</CODE>
     * @param type Type of sibling (ALL, ELEMENT, TEXT)
     * @throws isl.dms.DMSException with expected error codes
     */
    
    public XMLElement getNextSibling(int type) throws DMSException {
        try{
            XMLElement sibling = null;
            
            if (type==ALL) {
                sibling = this.getNextSibling();
            } else {
                int index = this.getIndex();
                
                XMLElement typeSiblings [] = this.getParent().getChildren(type);
                
                for (int i=0;i<typeSiblings.length;i++) {
                    if (typeSiblings[i].getIndex()>index) {
                        sibling = typeSiblings[i];
                        break;
                    }
                }
                
            }
            return sibling;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * The XMLElement of a specific type immediately before this XMLElement. If there is no such object, this returns null.
     * @return Returns the next sibling as a <CODE>XMLElement</CODE>
     * @param type Type of sibling (ALL, ELEMENT, TEXT)
     * @throws isl.dms.DMSException with expected error codes
     */
    
    public XMLElement getPreviousSibling(int type) throws DMSException {
        try{
            XMLElement sibling = null;
            
            if (type==ALL) {
                sibling = this.getPreviousSibling();
            } else {
                int index = this.getIndex();
                
                XMLElement typeSiblings [] = this.getParent().getChildren(type);
                
                int i=0;
                while (typeSiblings[i].getIndex()<index) {
                    sibling = typeSiblings[i];
                    
                    i++;
                }
                
            }
            return sibling;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * The XMLElement immediately before this XMLElement. If there is no such object, this returns null.
     * @throws isl.dms.DMSException with expected error codes
     * @return Returns the previous sibling as a <CODE>XMLElement</CODE>
     */
    
    public XMLElement getPreviousSibling() throws DMSException {
        try{
            XMLElement sibling = null;
            if (this.XMLType.equals("Element")) {
                sibling = new XMLElement(this.XMLElem.getPreviousSibling());
            } else if (this.XMLType.equals("Text")) {
                sibling = new XMLElement(this.XMLText.getPreviousSibling());
            }
            return sibling;
            
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Appends a <CODE>XMLElement</CODE> (Type element or Text).If XMLElement is already inside the tree, it is removed first.
     * @param object Object to append.
     * @throws isl.dms.DMSException with expected error codes
     */
    public void appendChild(XMLElement object) throws DMSException {
        try{
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            if (object.XMLType.equals("Element")) {
                
                this.XMLDoc.adoptNode(object.XMLElem);
                this.XMLElem.appendChild(object.XMLElem);
                
            } else if (object.XMLType.equals("Text")) {
                
                this.XMLDoc.adoptNode(object.XMLText);
                this.XMLElem.appendChild(object.XMLText);
            }
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Inserts the newChild before the existing child refChild. If refChild is null, inserts newChild at the end of the list of children.
     * If newChild is a DocumentFragment object, all of its children are inserted, in the same order, before refChild. If the newChild is already in the tree, it is first removed.
     * @param newChild The child to insert as a <CODE>XMLElement</CODE>.
     * @param refChild Reference child as a <CODE>XMLElement</CODE>
     * @throws isl.dms.DMSException with expected error codes
     */
    public void insertChildBefore(XMLElement newChild, XMLElement refChild) throws DMSException {
        try{
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            
            if (newChild.XMLType.equals("Element")) {
                this.XMLDoc.adoptNode(newChild.XMLElem);
                if (refChild.XMLType.equals("Element")) {
                    this.XMLElem.insertBefore(newChild.XMLElem,refChild.XMLElem);
                } else if (refChild.XMLType.equals("Text")) {
                    this.XMLElem.insertBefore(newChild.XMLElem, refChild.XMLText);
                }
            } else if (newChild.XMLType.equals("Text")) {
                this.XMLDoc.adoptNode(newChild.XMLText);
                if (refChild.XMLType.equals("Element")) {
                    this.XMLElem.insertBefore(newChild.XMLText,refChild.XMLElem);
                } else if (refChild.XMLType.equals("Text")) {
                    
                    this.XMLElem.insertBefore(newChild.XMLText, refChild.XMLText);
                }
            }

        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Inserts the newChild after the existing child refChild. If refChild is null, inserts newChild at the end of the list of children.
     * If newChild is a DocumentFragment object, all of its children are inserted, in the same order, before refChild. If the newChild is already in the tree, it is first removed.
     * @param newChild The child to insert as a <CODE>XMLElement</CODE>.
     * @param refChild Reference child as a <CODE>XMLElement</CODE>
     * @throws isl.dms.DMSException with expected error codes
     */
    public void insertChildAfter(XMLElement newChild, XMLElement refChild) throws DMSException {
        try {
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            XMLElement refNextChild = refChild.getNextSibling();
            this.insertChildBefore(newChild, refNextChild);
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Replaces a child with a new <CODE>XMLElement</CODE>.
     * @param newChild New child as a <CODE>XMLElement</CODE>.
     * @param oldChild Old child as a <CODE>XMLElement</CODE>
     * @throws isl.dms.DMSException with expected error codes
     */
    public void replaceChild(XMLElement newChild, XMLElement oldChild) throws DMSException {
        try{
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            } else {
                if (newChild.XMLType.equals("Element")) {
                    
                    this.XMLDoc.adoptNode(newChild.XMLElem);
                    if (oldChild.XMLType.equals("Element")) {
                        
                        this.XMLElem.replaceChild(newChild.XMLElem,oldChild.XMLElem);
                    } else if (oldChild.XMLType.equals("Text")) {
                        
                        this.XMLElem.replaceChild(newChild.XMLElem, oldChild.XMLText);
                    }
                } else if (newChild.XMLType.equals("Text")) {
                    
                    this.XMLDoc.adoptNode(newChild.XMLText);
                    
                    if (oldChild.XMLType.equals("Element")) {
                        
                        this.XMLElem.replaceChild(newChild.XMLText,oldChild.XMLElem);
                    } else if (oldChild.XMLType.equals("Text")) {
                        
                        this.XMLElem.replaceChild(newChild.XMLText, oldChild.XMLText);
                    }
                }
            }
            
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Removes child elements with a given name. If name is "" removes all child elements.
     * @param childName Name of child to remove.
     * @throws isl.dms.DMSException with expected error codes
     */
    public void removeChild(String childName) throws DMSException {
        try{
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            XMLElement [] toBeRemoved;
            if (childName.equals("")) {
                toBeRemoved = this.getChildren(ELEMENT);
            } else {
                toBeRemoved = this.getChildren(childName);
            }
            for (int i=0;i<toBeRemoved.length;i++) {
                this.XMLElem.removeChild(toBeRemoved[i].XMLElem);
            }
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }

    /**
     * Returns the content of this <code>XMLElement</code> as a
     * <code>String</code>. 
     * 
     * @return Returns a <CODE>String</CODE> representation of the
     * content of this <code>XMLElement</code>.
     * @throws isl.dms.DMSException with expected error codes
     */
    public String getContentAsString() throws DMSException {
        try{
            if (this.XMLType.equals("Text")) {
            	return this.getText();
            }

            String childrenStr = "";
            XMLElement [] children = this.getChildren(ALL);

            for (int i=0;i<children.length;i++){
                if (children[i].XMLType.equals("Text")) {
                	childrenStr += children[i].getText();
                } else if (children[i].XMLType.equals("Element")) {
                	childrenStr += "<"+children[i].getName()+">"+children[i].getContentAsString()+"</"+children[i].getName()+">";
                }
            }
            return childrenStr;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }

    /**
     * Returns children's markup.
     * @return Returns a <CODE>String</CODE> representation of all child objects.
     * @throws isl.dms.DMSException with expected error codes
     */
    
    public String getChildrenMarkup() throws DMSException {
        try{
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            String childrenMarkup = "";
            XMLElement [] children = this.getChildren(ALL);
            
            for (int i=0;i<children.length;i++){
                if (children[i].XMLType.equals("Text")) {
                    childrenMarkup += "No"+i+":("+children[i].getText()+")\n";
                } else if (children[i].XMLType.equals("Element")) {
                    childrenMarkup += "No"+i+":(<"+children[i].getName()+"/>)\n";
                }
            }
            return childrenMarkup;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    
    /**
     * Returns all child objects of a specific type.
     * @return Returns a <CODE>XMLElement[]</CODE> of all the Child Elements.
     * @param type Type of children to return (ELEMENT, TEXT or ALL).
     * @throws isl.dms.DMSException with expected error codes
     */
    
    public XMLElement[] getChildren(int type) throws DMSException {
        try{
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            NodeList children = XMLElem.getChildNodes();
            int childrenNum = children.getLength();
            Vector<XMLElement> vect = new Vector<XMLElement>();
            
            for(int i=0;i<childrenNum;i++){
                Node n = children.item(i);
                if(type==ELEMENT && n.getNodeType() == Node.ELEMENT_NODE){
                    XMLElement x = new XMLElement(n);
                    vect.add(x);
                } else if (type==TEXT && n.getNodeType() == Node.TEXT_NODE) {
                    XMLElement x = new XMLElement(n);
                    vect.add(x);
                } else if (type==ALL) {
                    XMLElement x = new XMLElement(n);
                    vect.add(x);
                }
            }
            XMLElement[] ret = vect.toArray(new XMLElement[0]);
            return ret;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Returns child elements with a given tag name.
     * @param childName The name of the tag to match on.
     * @throws isl.dms.DMSException with expected error codes
     * @return Returns a <CODE>XMLElement[]</CODE> of the Child Elements with a given tag name
     */
    public XMLElement[] getChildren(String childName) throws DMSException {
        try{
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            XMLElement[] children = this.getChildren(ELEMENT);
            Vector<XMLElement> vect = new Vector<XMLElement>();
            
            for(int i=0;i<children.length;i++){
                XMLElement n = children[i];
                
                if(n.XMLType.equals("Element") && n.getName().equals(childName)){
                    vect.add(n);
                }
            }
            
            XMLElement[] ret = vect.toArray(new XMLElement[0]);
            return ret;
            
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Returns child elements with a given tag name, attribute name and value.
     * @return Returns a <CODE>XMLElement[]</CODE> of the Child Elements with a given tag name, attribute name and attribute value.
     * @param childName The name of the tag to match on.
     * @param attributeName The name of the attribute to match on.
     * @param attributeValue The value of the attribute to match on.
     * @throws isl.dms.DMSException with expected error codes
     */
    public XMLElement[] getChildren(String childName, String attributeName, String attributeValue) throws DMSException {
        try{
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            XMLElement[] children = this.getChildren(childName);
            Vector<XMLElement> vect = new Vector<XMLElement>();
            
            for(int i=0;i<children.length;i++) {
                if (children[i].getAttributeValue(attributeName).equals(attributeValue)) {
                    vect.add(children[i]);
                }
            }
            XMLElement[] ret = vect.toArray(new XMLElement[0]);
            return ret;
            
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    /**
     * Returns name of element or "Text" if XMLElement was created based on a Text node.
     * @throws isl.dms.DMSException with expected error codes
     * @return Returns name of element as a <CODE>String</CODE>
     */
    public String getName() throws DMSException{
        try {
            String name = null;
            if (this.XMLType.equals("Element")) {
                name = this.XMLElem.getTagName();
            } else if (this.XMLType.equals("Text")) {
                
                name = "Text"+this.getIndex();
            }
            return name;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Returns index of an XMLElement.
     * @throws isl.dms.DMSException with expected error codes
     * @return Returns name of element as a <CODE>String</CODE>
     */
    public int getIndex() throws DMSException{
        try {
            int index=-1;
            Node node = null;
            if (this.XMLType.equals("Element")) {
                node = this.XMLElem;
            } else if (this.XMLType.equals("Text")) {
                node = this.XMLText;
            }
            XMLElement parent = this.getParent();
            NodeList children = parent.XMLElem.getChildNodes();
            
            for (int i=0;i<children.getLength();i++) {
                
                if (node.isSameNode(children.item(i))) {
                    
                    index = i;
                }
            }
            
            return index;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Returns an attribute value by name.
     * @param attributeName The name of the attribute to retrieve.
     * @throws isl.dms.DMSException with expected error codes
     * @return The Attribute Value as a <CODE>String</CODE>, or an empty <CODE>String</CODE> if that attribute does not have a default or specified value.
     */
    public String getAttributeValue(String attributeName) throws DMSException{
        try {
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have attributes!");
            }
            return XMLElem.getAttribute(attributeName);
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
//    /**
//     * Creates a new child element with the specified name inside a <CODE>Document</CODE>.
//     * @param name Name of the new element.
//     * @throws isl.dms.DMSException with expected error codes
//     *
//     */
//    public void createChild(String name) throws DMSException {
//        try {
//            //this.XMLDoc.createElement(name);
//            //this.createElement(name);
//            this.XMLElem.appendChild((Node)((Element)this.createElement(name)));
//        }catch(Exception Ex){
//            throw new DMSException(Ex.getMessage());
//        }
//    }
    
    /**
     * Creates a child object of type Element with a specified name and text value.
     * @param name The child�s name (tag)
     * @param textValue The text that goes in the child�s element
     * @return The newly created Element as a <CODE>XMLElement</CODE>
     * @throws DMSException with expected error codes
     */
    public XMLElement createChildWithText(String name, String textValue)
    throws DMSException {
        try {
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            
            XMLElement child = this.createElement(name, textValue);
            this.appendChild(child);
                        
            return child;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Creates a child object of type Text with text value.
     * @param textValue Text content of the child.
     * @return The newly created Text as a <CODE>XMLElement</CODE>
     * @throws DMSException with expected error codes
     */
    public XMLElement createChildText(String textValue)
    throws DMSException {
        try {
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            
            XMLElement child = this.createText(textValue);
            this.appendChild(child);
            
            return child;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
///**
//* Updates the text for the given node.
//* @param node the node to update
//* @param newValue the value to be place at that node
//* @exception XMLException if an error occurred updating the XML Node
//*/
//public void setText(String newValue) throws DMSException
//{
//   NodeList children;
//   Node childNode;
//   children = this.getChildNodes();
//   boolean success = false;
//   if (children != null)
//   {
//     for (int i = 0; i < children.getLength(); i++)
//     {
//       childNode = children.item(i);
//       if ((childNode.getNodeType() == org.w3c.dom.Node.TEXT_NODE) || (childNode.getNodeType() == Node.CDATA_SECTION_NODE))
//       {
//         childNode.setNodeValue(newValue);
//         success = true;
//       }
//     }
//   }
//   if (!success)
//   {
//     Text textNode = node.getOwnerDocument().createTextNode(newValue);
//     node.appendChild(textNode);
//   }
//}
    
    /**
     * Creates a new text node with the specified text content inside a <CODE>Document</CODE>.
     * @return Returns new text node as an <CODE>XMLElement</CODE>
     * @param text Text content.
     * @throws isl.dms.DMSException with expected error codes
     */
    public XMLElement createText(String text) throws DMSException {
        try {
            Text new_text = this.XMLDoc.createTextNode(text);
            XMLElement xnew_text = new XMLElement(new_text);
            return xnew_text;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Creates a new element with the specified name inside a <CODE>Document</CODE>.
     * @return Returns new element as an <CODE>XMLElement</CODE>
     * @param elementName Name of the new element.
     * @throws isl.dms.DMSException with expected error codes
     */
    public XMLElement createElement(String elementName) throws DMSException {
        try {
            Element new_elem = this.XMLDoc.createElement(elementName);
            XMLElement xnew_elem = new XMLElement(new_elem);
            return xnew_elem;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Creates a new element with the specified name inside a <CODE>Document</CODE>.
     * @return Returns new element as an <CODE>XMLElement</CODE>
     * @param elementName Name of the new element.
     * @param text Text to add.
     * @throws isl.dms.DMSException with expected error codes
     */
    public XMLElement createElement(String elementName, String text) throws DMSException {
        try {
            XMLElement xnew_elem = this.createElement(elementName);
            xnew_elem.setText(text);
            return xnew_elem;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    /**
     * Adds a new attribute.If an attribute with that name is already present in the element an Exception is thrown.
     * @param attributeValue Value to set in <CODE>String</CODE> form.
     * @param attributeName The name of the attribute to create.
     * @throws isl.dms.DMSException with expected error codes
     */
    public void addAttribute(String attributeName, String attributeValue) throws DMSException {
        try {
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have attributes!");
            }
            if (this.XMLElem.hasAttribute(attributeName)==true) {
                throw new DMSException("Attribute "+attributeName+" already exists!");
            }
            XMLElem.setAttribute(attributeName, attributeValue);
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Sets the value of an existing attribute.If an attribute with that name does not exist, an Exception is thrown.
     * @param attributeValue Value to set in <CODE>String</CODE> form.
     * @param attributeName The name of the attribute to set.
     * @throws isl.dms.DMSException with expected error codes
     */
    public void setAttribute(String attributeName, String attributeValue) throws DMSException {
        try {
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have attributes!");
            }
            if (this.XMLElem.hasAttribute(attributeName)==false) {
                throw new DMSException("Attribute "+attributeName+" does not exist!");
            }
            XMLElem.setAttribute(attributeName, attributeValue);
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Removes an attribute by name. If the removed attribute is known to have a default value, an attribute immediately appears containing the default value as well as the corresponding namespace URI, local name, and prefix when applicable.
     * To remove an attribute by local name and namespace URI, use the removeAttributeNS method.
     * @param attributeName The name of the attribute to remove.
     * @throws isl.dms.DMSException with expected error codes
     */
    public void removeAttribute(String attributeName) throws DMSException {
        try {
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have attributes!");
            }
            XMLElem.removeAttribute(attributeName);
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Returns the number of attributes an element has or null otherwise.
     * @throws isl.dms.DMSException with expected error codes
     * @return Number of attributes the element has as <CODE>int</CODE>.
     */
    public int getAttributeCount() throws DMSException {
        try {
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have attributes!");
            }
            return XMLElem.getAttributes().getLength();
            
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Sets the text of an XMLElement.If XMLElement is of type "Text" it simply replaces the text.
     * If XMLElement is of type "Element" it sets the text node as the first child of the element node and replaces any existing text nodes.
     * @param text Text of the Element as <CODE>String</CODE>
     * @throws isl.dms.DMSException with expected error codes
     */
    public void setText(String text) throws DMSException {
        try {
            
            if (this.XMLType.equals("Text")) {
                this.XMLText.replaceWholeText(text);
            }
            
            if (this.XMLType.equals("Element")) {
                Text content = this.XMLDoc.createTextNode(text);
                if (this.XMLElem.getChildNodes().getLength()>0){
                    
                    if (this.XMLElem.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                        this.XMLElem.replaceChild(content, this.XMLElem.getFirstChild());
                    } else {
                        this.XMLElem.insertBefore(content, this.XMLElem.getFirstChild());
                    }
                } else {
                    this.XMLElem.appendChild(content);
                }
            }
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Gets the text of an XMLElement.If XMLElement is of type Element it gets the text from all child text nodes the element might have and concats it.
     * If XMLElement is of type Text it simply gets the Text.
     * @throws isl.dms.DMSException with expected error codes
     * @return Returns the element text as a <CODE>String</CODE>
     */
    public String getText() throws DMSException {
        try {
            String string = "";
            if (this.XMLType.equals("Text")) {
                string = this.XMLText.getData();
            } else if (this.XMLType.equals("Element")){
                
                XMLElement [] children = this.getChildren(TEXT);
                
                if (children == null) return string;
                for (int i = 0; i < children.length; i++)   {
                	if (children[i].XMLType.equals("Text")) {
                		string += " "+children[i].XMLText.getData();
                	}
                }
            }
            return string;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    
    /**
     * Gets all the attribute names an element might have.
     * @throws isl.dms.DMSException with expected error codes
     * @return Attribute names of an element as a <CODE>String[]</CODE>
     */
    public String[] getAttributeNames() throws DMSException {
        try {
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have attributes!");
            }
            String[] names = new String [this.getAttributeCount()];
            for (int i=0; i<names.length; i++) {
                names[i] = XMLElem.getAttributes().item(i).getNodeName();
            }
            return names;
            
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Gets the first child element with the given name or returns null if one can�t be found.
     * @return the first child element with the given name or null if one can�t be found.
     * @param childName The name of the tag to match on.
     * @param deepSearch - if True then the search will be performed on all levels
     * If False then only Direct childs will be searched
     * @throws isl.dms.DMSException with expected error codes
     */
    public XMLElement getFirstElementNamed(String childName, boolean deepSearch) throws DMSException{
        try {
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't have children!");
            }
            NodeList children = this.XMLElem.getChildNodes();
            
            XMLElement xchild = null;
            for (int i = 0; i < children.getLength() && xchild == null; i++) {
                if (children.item(i).getNodeName().equals(childName)) {
                    
                    xchild = new XMLElement(children.item(i));
                    break;
                    
                } else if ((deepSearch) && (children.item(i).getNodeType() == Element.ELEMENT_NODE)) {
                    
                    XMLElement xobj = new XMLElement(children.item(i));
                    xchild = xobj.getFirstElementNamed(childName, deepSearch);
                }
            }
            return xchild;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Returns all XMLElements of type "Element" with the given name.
     * @param name Name of the objects.
     * @return Objects with a specific name as a <CODE>XMLElement[]</CODE>.
     * @throws isl.dms.DMSException with expected error codes
     */
    public XMLElement[] getElementsNamed(String name) throws DMSException{
        try {
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't include elements!");
            }
            //Vector<XMLElement> all = new Vector<XMLElement>();
            NodeList all = this.XMLElem.getElementsByTagName(name);
            XMLElement[] xobjs = new XMLElement[all.getLength()];
            for (int i=0;i<all.getLength();i++){
                XMLElement xobj = new XMLElement(all.item(i));
                xobjs[i]=xobj;
                
            }
            return xobjs;
            // return (XMLElement[])child.toArray(new XMLElement[0]);
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Returns all XMLElements of type "Element" with the given name, attributeName and attributeValue.
     * @return Objects with a specific name, attribute and attribute value as a <CODE>XMLElement[]</CODE>.
     * @param attributeName Name of the attribute.
     * @param attributeValue Value of the attribute.
     * @param name Name of the objects.
     * @throws isl.dms.DMSException with expected error codes
     */
    public XMLElement[] getElementsNamed(String name, String attributeName, String attributeValue) throws DMSException{
        try {
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't include elements!");
            }
            
            XMLElement[] pot_elems = this.getElementsNamed(name);
            Vector<XMLElement> vect = new Vector<XMLElement>();
            for (int i=0;i<pot_elems.length;i++){
                
                if (pot_elems[i].getAttributeValue(attributeName).equals(attributeValue)) {
                    vect.add(pot_elems[i]);
                }
                
            }
            XMLElement[] ret = vect.toArray(new XMLElement[0]);
            return ret;
            
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    
    /**
     * Returns the names of all XMLElements of type Element.
     * @throws isl.dms.DMSException with expected error codes
     * @return Names of all elements as a <CODE>String[]</CODE>
     */
    public String[] getTagNames() throws DMSException {
        try{
            if (this.XMLType.equals("Text")) {
                throw new DMSException("Text objects don't include elements!");
            }
            NodeList list = XMLElem.getElementsByTagName("*");
            String[] tags = new String[list.getLength()];
            for (int i=0; i<list.getLength(); i++) {
                // Get element
                Element element = (Element)list.item(i);
                tags[i] = element.getTagName();
            }
            return tags;
        }catch(Exception Ex){
            throw new DMSException(Ex.getMessage());
        }
    }
    
    /**
     * Getter method for XMLElem.
     * @return Returns XMLElem as an <CODE>Element</CODE>.
     */
    public Element getElement() {
        return XMLElem;
    }
    
    /**
     * Setter method for XMLElem.
     * @param XMLElem XMLElem to set.
     */
    protected void setElement(Element XMLElem) {
    	this.XMLElem = XMLElem;
    	this.XMLType = "Element";
    	if (XMLElem != null)
    		this.XMLDoc = XMLElem.getOwnerDocument();
    	else
    		this.XMLDoc = null;
    }
    
}

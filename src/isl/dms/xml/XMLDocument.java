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

import isl.dbms.DBFile;
import isl.dbms.DBMSException;
import isl.dms.DMSConfig;
import isl.dms.DMSException;
import isl.dms.file.DMSAdmin;

/**
 * The <code>XMLDocument</code> class represents an XML <code>DBFile</code>.
 * Its methods use XQuery to query the documents and XUpdate
 * to update them.
 * 
 * @author samarita
 * @author npap
 */
public class XMLDocument extends DBFile{
	/**
	 * Creates a new <code>XMLDocument</code>. The new
	 * <code>XMLDocument</code> represents an XML file stored
	 * in a collection in a database.
	 * 
	 * @param database
	 *            the database where the collection (next argument) is in.
	 * @param collection
	 *            the collection where the file (next argument) is in.
	 * @param document
	 *            the name of the document.
	 * @param user
	 *            the username to use for authentication to the database or
	 *            <code>null</code> if the database does not support
	 *            authentication.
	 * @param password
	 *            the password to use for authentication to the database or
	 *            <code>null</code> if the database does not support
	 *            authentication.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public XMLDocument(String database, String collection, String document, String user, String password)
	throws DMSException, DBMSException {
		super(database, collection, document, user, password);
	}

	/**
     * Creates a new <code>XMLDocument</code>. The new
     * <code>XMLDocument</code> represents an XML file stored
     * in a collection in a database.<br>
     * <br>
     * The specified document must exist in the database and in
     * the collection, that have been specified by the
     * <code>DMSConfig.init()</code> method.
     *
     * @param document the name of the document
     * @param conf a <code>DMSConfig</code> object
     * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public XMLDocument(String document, DMSConfig conf)	throws DMSException, DBMSException {
		super(conf.DB, conf.COLLECTION, document, conf.DB_USERNAME, conf.DB_PASSWORD);
	}

	/**
     * Returns a <code>String</code> representing the XML content
     * of the <code>XMLDocument</code>.
     * @return a <code>String</code> representing the XML content
     * of the <code>XMLDocument</code>
     */
	public String getContent(){
		return this.toString();
	}

	/**
	 * Sets the value of an admin property. Admin properties are
	 * in the 'admin part' of a file. This method is used when
	 * 'admin part' is kept inside the file represented by
	 * this <code>XMLDocument</code>.<br><br>
	 * When 'admin parts' of all files are kept in one file
	 * (DMSAdmins.xml), the <b>getAdmin</b> method should be used.
	 * @param property the admin property to set
	 * @param value the value to set
	 * @throws DBMSException with expected error codes.
	 */
	public void setAdminProperty(String property, String value) throws DBMSException{
		this.xUpdate("//admin/"+property, value);
	}

	/**
     * Returns the <code>DMSAdmin</code> representind the 'admin part'
     * of the <code>XMLDocument</code>. This method is used when the
     * 'admin parts' of all files are kept in one file (DMSAdmins.xml).
     * @return the the <code>DMSAdmin</code> of this <code>XMLDocument</code>.
     * @param conf a <code>DMSConfig</code> object
	 * @throws DMSException with expected error codes. 
	 * @throws DBMSException with expected error codes.
     */	
	public DMSAdmin getAdmin(DMSConfig conf) throws DMSException, DBMSException{
		return new DMSAdmin(this.getName(), this.getCollection().getName(), conf);		
	}
}

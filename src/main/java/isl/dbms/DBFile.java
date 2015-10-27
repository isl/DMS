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
package isl.dbms;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.BinaryResource;
import org.xmldb.api.modules.XPathQueryService;
import org.xmldb.api.modules.XUpdateQueryService;
import org.xmldb.api.DatabaseManager;
import javax.xml.transform.OutputKeys;

/**
 * <code>DBFile</code> is a container for data stored within the database. A
 * <code>DBFile</code> represents either an XML container or a binary one. The
 * kind of the container a <code>DBFile</code> represents is indicated by its
 * type.<br>
 * <br>
 * There are three defined types: <br>
 * <br>
 * <i>XMLDBFile </i>- all XML data stored in the database <br>
 * <i>BinaryDBFile </i>- Binary blob data stored in the database <br>
 * <i>QueryDBFile </i>- if the <code>DBFile</code> is produced as a result of
 * a query. <br>
 * <br>
 * A <code>DBFile</code> of type 'XMLDBFile' or 'QueryDBFile' can be accessed
 * either as text XML or via the DOM or SAX APIs. <br>
 * <bR>
 * An <code>DBFile</code> of type 'BinaryDBFile' can be accessed as an array
 * of bytes <code>byte[]</code>.
 */
public class DBFile extends DBXUpdate {
	private XMLResource XMLResource;
	private BinaryResource BinaryResource;

	private Collection Collection;

	private String Type;
	private String DB;
	private String User, Password;

	protected DBFile(){
		
	}

	/**
	 * Constructs a new empty <code>DBFile</code>. The type of the
	 * <code>DBFile</code> is determined by the type argument. The DBMS API
	 * currently defines "XMLDBFile", "BinaryDBFile" and "QueryDBFile" as valid
	 * types. <b>When creating a new <code>DBFile</code> only the first two
	 * types are valid</b>. <br>
	 * <br>
	 * The new <code>DBFile</code> does NOT have an owner collection.
	 * 
	 * @param type
	 *            the type of the file.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public DBFile(String type) throws DBMSException {
		this.XMLResource = null;
		this.BinaryResource = null;
		if (!type.equals("XMLDBFile") && !type.equals("BinaryDBFile"))
			throw new DBMSException("DBFile could not be instantiated. Wrong type: " + type);
		this.Type = type;
		this.User = null;
		this.Password = null;
		this.Collection = null;
		this.DB = null;
	}

	protected DBFile(String db, Collection col, Resource res, String type, String user, String password)
	throws DBMSException {
		try{
			this.Type = type;
			
			this.User = user;
			
			this.Password = password;
			
			this.XMLResource = null;
                        if (res == null)
				throw new DBMSException("DBFile could not be instantiated. File " 
						+ " not found in collection " + col);
//                        Object content = res.getContent();
                        
			this.BinaryResource = null;
			setResource(res);
			this.Collection = col;
			this.DB = db;
		}
		catch (Exception e) {
			e.printStackTrace();}
		finally{
			 if (this.Collection != null)
				try {
					this.Collection.close();} 
			    catch (XMLDBException e) {
					e.printStackTrace();}
		}
		
	}

	protected DBFile(String db, Collection col, Resource res, String user, String password)
	throws DBMSException {
		try{
			try{
				setType(res.getResourceType());
			} catch (XMLDBException XMLDBEx) {
				throw new DBMSException(XMLDBEx);
			}
			this.XMLResource = null;
//                        String content = (String)res.getContent();
			this.BinaryResource = null;
			setResource(res);
			this.DB = db;
			this.Collection = col;
			this.User = user;
			this.Password = password;}
		
		catch (Exception e) {
			e.printStackTrace();}
		
		finally{
			 if (this.Collection != null)
				try {
					this.Collection.close();} 
			    catch (XMLDBException e) {
					e.printStackTrace();}
		}
		
	}

	/**
	 * Constructs a new <code>DBFile</code> instance associated with the
	 * specified file, within the specified collection in the specified
	 * database.
	 * 
	 * @param database
	 *            the database where the collection (next argument) is in.
	 * @param collection
	 *            the collection where the file (next argument) is in.
	 * @param file
	 *            the name of the file.
	 * @param user
	 *            the username to use for authentication to the database or
	 *            <code>null</code> if the database does not support
	 *            authentication.
	 * @param password
	 *            the password to use for authentication to the database or
	 *            <code>null</code> if the database does not support
	 *            authentication.
	 * 
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public DBFile(String database, String collection, String file, String user, String password)
	throws DBMSException {
		try {
			registerDriver();
			this.DB = database;
			this.User = user;
			this.Password = password;
			// get the collection
			this.Collection = DatabaseManager.getCollection(database + collection, user, password);
			deregisterDriver();
			if (this.Collection == null)
				throw new DBMSException("DBFile could not be instantiated. Collection "
						+ collection + " not found");
			this.Collection.setProperty(OutputKeys.INDENT, "yes");
			
			Resource res = this.Collection.getResource(file);
			if (res == null)
				throw new DBMSException("DBFile could not be instantiated. File " + file
						+ " not found in collection " + collection);
			setType(res.getResourceType());
			setResource(res);
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
		finally{
			if (this.Collection != null)
				try {
					this.Collection.close();} 
			    catch (XMLDBException e) {
					e.printStackTrace();}
		}
	}

	/**
	 * Returns the unique name (id) for this <code>DBFile</code> or null if
	 * the <code>DBFile</code> is anonymous. That is if it is obtained as the
	 * result of a query.
	 * 
	 * @return the name (id) for the <code>DBFile</code> or null if no id
	 *         exists.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public String getName() throws DBMSException {
		try {
			return this.Type.equals("BinaryDBFile") ? BinaryResource.getId() : XMLResource.getId();
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
	}

	/**
	 * Returns the type for this <code>DBFile</code>.<br>
	 * <br>
	 * There are three defined types: <br>
	 * <br>
	 * <i>XMLDBFile </i>- all XML data stored in the database <br>
	 * <i>BinaryDBFile </i>- Binary blob data stored in the database <br>
	 * <i>QueryDBFile </i>- if the <code>DBFile</code> is produced as a result
	 * of a query.
	 * 
	 * @return the type for the <code>DBFile</code>.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public String getType() throws DBMSException {
		return this.Type;
	}

	/**
	 * Returns the full path in the database of the file
	 * represented by this <code>DBFile</code>.
	 * 
	 * @return the full path in the database of the file represented
	 * by this <code>DBFile</code>.
	 * @throws DBMSException with expected error codes.
	 */
	public String getPath() throws DBMSException {
		String ret = getName();
		DBCollection c = getCollection();
		while(c != null){
			ret = c.getName() + "/" + ret;
			c = c.getParentCollection();
		}
		return ret;
	}

	/**
	 * Returns a <code>String</code> representing the XML content
	 * of the <code>DBFile</code>.
	 */
	public String toString() {
		try{
			return this.getXMLAsString();
		}catch(DBMSException Ex){
			return null;
		}
	}

	/**
	 * Returns the XML content of this <code>DBFile</code> as a
	 * <code>String</code>.
	 * 
	 * @return the XML content of this <code>DBFile</code> as a
	 *         <code>String</code> object.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public String getXMLAsString() throws DBMSException {
		try {
			return (String) this.XMLResource.getContent();
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
	}

	/**
	 * Sets the XML content for this <code>DBFile</code> using a
	 * <code>String</code> as the source.
	 * 
	 * @param content
	 *            The new content value.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public void setXMLAsString(String content) throws DBMSException {
		try {
			this.XMLResource.setContent(content);
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
	}

	/**
	 * Returns the XML content of this <code>DBFile</code> as a
	 * <code>DOM Node (org.w3c.dom.Node)</code>.
	 * 
	 * @return the XML content of this <code>DBFile</code> as a
	 *         <code>DOM Node (org.w3c.dom.Node)</code> object.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public org.w3c.dom.Node getXMLAsDOM() throws DBMSException {
		try {
			return (org.w3c.dom.Node) this.XMLResource.getContentAsDOM();
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
	}

	/**
	 * Sets the XML content for this <code>DBFile</code> using a
	 * <code>DOM Node (org.w3c.dom.Node)</code> as the source.
	 * 
	 * @param content
	 *            The new content value.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public void setXMLAsDOM(org.w3c.dom.Node content) throws DBMSException {
		try {
			this.XMLResource.setContentAsDOM(content);
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
	}

	/**
	 * Allows the use of a
	 * <code>ContentHandler (org.xml.sax.ContentHandler)</code> to parse this
	 * XML <code>DBFile</code>.
	 * 
	 * @param handler
	 *            the
	 *            <code>SAX ContentHandler (org.xml.sax.ContentHandler)</code>
	 *            to use to handle the XML file content.
	 * 
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public void getXMLAsSAX(org.xml.sax.ContentHandler handler)
	throws DBMSException {
		try {
			this.XMLResource.getContentAsSAX(handler);
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
	}

	/**
	 * Sets the XML content for this <code>DBFile</code> using a
	 * <code>SAX ContentHandler (org.xml.sax.ContentHandler)</code>.
	 * 
	 * @return a <code>SAX ContentHandler (org.xml.sax.ContentHandler)</code>
	 *         that can be used to add content into the <code>DBFile</code>.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public org.xml.sax.ContentHandler setXMLAsSAX() throws DBMSException {
		try {
			return this.XMLResource.setContentAsSAX();
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
	}

	// }
	/**
	 * Returns the binary content of this <code>DBFile</code> as an array of
	 * bytes <code>(byte[])</code>.
	 * 
	 * @return the binary content of this <code>DBFile</code> as an array of
	 *         bytes <code>(byte[])</code>.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public byte[] getBinary() throws DBMSException {
		try {
			return (byte[]) this.BinaryResource.getContent();
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
	}

	/**
	 * Sets the binary content for this <code>DBFile</code> using an array of
	 * bytes <code>byte[]</code> as the source.
	 * 
	 * @param content
	 *            The new content value.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public void setBinary(byte[] content) throws DBMSException {
		try {
			this.BinaryResource.setContent(content);
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
	}

	/**
	 * Returns the <code>DBCollection</code> instance that this
	 * <code>DBFile</code> is associated with. If no owner collection exist it
	 * returns <code>null</code>. All <code>DBFile</code> objects must
	 * exist within the context of a <code>DBCollection</code>.
	 * 
	 * @return the <code>DBCollection</code> of this <code>DBFile</code> or
	 *         <code>null</code> if no owner collection exist.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public DBCollection getCollection() throws DBMSException {
		try {
			if (this.Collection == null)
				return null;
			else
				return new DBCollection(this.DB, this.Collection.getName(), this.User, this.Password);
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
		finally{
			if (this.Collection != null)
				try {
					this.Collection.close();} 
			    catch (XMLDBException e) {
					e.printStackTrace();}
		}
	}

	/**
	 * Stores this <code>DBFile</code> into the collection it belongs to. The
	 * file associated with this <code>DBFile</code> is stored into the
	 * database, into the collection associated with the
	 * <code>DBCollection</code> it belongs to. If the file does NOT already
	 * exist it will be created. If it does already exist it will be updated.
	 * 
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public void store() throws DBMSException {
		try {
			if (this.Collection == null)
				throw new DBMSException(
						"DBFile could not be stored. No owner collection found.");
			this.Collection.storeResource(getResource());
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
		
	}

	/**
	 * Stores this <code>DBFile</code> into the collection it belongs to, under
	 * a particular name. The file associated with this <code>DBFile</code> is
	 * stored into the database, into the collection associated with the
	 * <code>DBCollection</code> it belongs to, under the specified name. If
	 * the specified file does NOT already exist it will be created. If it does
	 * already exist it will be updated. It returns a <code>DBFile</code>
	 * representing the newly stored file.<br>
	 * <br>
	 * If the content of this <code>DBFile</code> was changed but NOT stored,
	 * the file under the original name in the database will not reflect the
	 * changes (until <code>store</code> is called). Only the file under the
	 * 'new' name will reflect the content of this <code>DBFile</code>.
	 * 
	 * @param fileName
	 *            the name under which the file will be stored.
	 * @return the <code>DBFile</code> representing the newly stored file.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public DBFile storeAs(String fileName) throws DBMSException {
		if (this.Collection == null)
			throw new DBMSException(
					"DBFile could not be stored. No owner collection found.");
		DBCollection col = new DBCollection(this.DB, this.Collection);
		return col.storeFileAs(this, fileName);
		
	}

	/**
	 * Stores this <code>DBFile</code> into a collection. The file associated
	 * with this <code>DBFile</code> is stored into the database, into the
	 * collection associated with the specified <code>DBCollection</code>. If
	 * the file does NOT already exist it will be created. If it does already
	 * exist it will be updated.<br>
	 * <br>
	 * <b>The owner collection of this <code>DBFile</code> is NOT changed.</b>
	 * Than means that if you want to get a <code>DBFile</code> associated
	 * with the 'new' file, a new <code>DBFile</code> must be constructed.
	 * 
	 * @param collection
	 *            the <code>DBCollection</code> representing the collection
	 *            into which the file will be stored.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public void storeInto(DBCollection collection) throws DBMSException {
		collection.storeFile(this);
	}

	/**
	 * Stores this <code>DBFile</code> into a collection. The file associated
	 * with this <code>DBFile</code> is stored into the specified database,
	 * into the specified collection. If the file does NOT already exist it will
	 * be created. If it does already exist it will be updated. <br>
	 * <br>
	 * <b>The owner collection of this <code>DBFile</code> is NOT changed.</b>
	 * Than means that if you want to get a <code>DBFile</code> associated
	 * with the 'new' file, a new <code>DBFile</code> must be constructed.<br>
	 * <br>
	 * If the collection (or the database) does not exist a DBMSException is
	 * thrown indicating the error.
	 * 
	 * @param database
	 *            the database where the collection (next argument) is in.
	 * @param collection
	 *            the name of the collection into which the file will be stored.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public void storeInto(String database, String collection)
	throws DBMSException {
		DBCollection col = new DBCollection(database, collection, this.User, this.Password);
		col.storeFile(this);
	}

	/**
	 * Stores this <code>DBFile</code> into a collection under a particular
	 * name. The file associated with this <code>DBFile</code> is stored into
	 * the database, into the collection associated with the specified
	 * <code>DBCollection</code> under the specified name. If the file does
	 * NOT already exist it will be created. If it does already exist it will be
	 * updated. It returns a <code>DBFile</code> representing the newly stored file<br>
	 * <br>
	 * <b>The owner collection of this <code>DBFile</code> is NOT changed.</b>
	 * Than means that if you want to get a <code>DBFile</code> associated
	 * with the 'new' file, a new <code>DBFile</code> must be constructed.<br>
	 * <br>
	 * If the content of this <code>DBFile</code> was changed but NOT stored,
	 * the file under the original name in the database will not reflect the
	 * changes (until <code>store</code> is called). Only the file under the
	 * 'new' name will reflect the content of this <code>DBFile</code>.
	 * 
	 * @param fileName
	 *            the name under which the file will be stored.
	 * @param collection
	 *            the <code>DBCollection</code> representing the collection
	 *            into which the file will be stored.
	 * @return the <code>DBFile</code> representing the newly stored file.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public DBFile storeIntoAs(DBCollection collection, String fileName)
	throws DBMSException {
		return collection.storeFileAs(this, fileName);
	}

	/**
	 * Stores this <code>DBFile</code> into a collection under a particular
	 * name. The file associated with this <code>DBFile</code> is stored into
	 * specified database, into the specified collection. If the file does NOT
	 * already exist it will be created. If it does already exist it will be
	 * updated. It returns a <code>DBFile</code> representing the newly stored file.<br>
	 * <br>
	 * <b>The owner collection of this <code>DBFile</code> is NOT changed.</b>
	 * Than means that if you want to get a <code>DBFile</code> associated
	 * with the 'new' file, a new <code>DBFile</code> must be constructed.<br>
	 * <br>
	 * If the collection (or the database) does not exist a DBMSException is
	 * thrown indicating the error.
	 * 
	 * @param database
	 *            the database where the collection (next argument) is in.
	 * @param collection
	 *            the name of the collection into which the file will be stored.
	 * @param fileName
	 *            the name under which the file will be stored.
	 * @return the <code>DBFile</code> representing the newly stored file.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public DBFile storeIntoAs(String database, String collection, String fileName)
	throws DBMSException {
		DBCollection col = new DBCollection(database, collection, this.User, this.Password);
		return col.storeFileAs(this, fileName);
	}

	/**
	 * Removes this <code>DBFile</code> from the collection it belongs to. The
	 * file associated with the specified <code>DBFile</code> is removed from
	 * the database.
	 * 
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public void remove() throws DBMSException {
		try {
			if (this.Collection == null)
				throw new DBMSException(
						"DBFile could not be stored. No owner collection found.");
			this.Collection.removeResource(getResource());
			this.Collection = null;
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
		finally{
			if (this.Collection != null)
				try {
					this.Collection.close();} 
			    catch (XMLDBException e) {
					e.printStackTrace();}
		}
	}

	/**
	 * Moves this <code>DBFile</code> from its owner collection into a
	 * collection. The file associated with this <code>DBFile</code> is stored
	 * into the database, into the collection associated with the specified
	 * <code>DBCollection</code>. If the file does NOT already exist it will
	 * be created. If it does already exist it will be updated.<br>
	 * <br>
	 * <b>The owner collection of this <code>DBFile</code> IS changed.</b>
	 * Than means that the owner collection of this <code>DBFile</code> is the
	 * collection it moved into.
	 * 
	 * @param collection
	 *            the <code>DBCollection</code> representing the collection
	 *            into which the file will be stored.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public void moveInto(DBCollection collection) throws DBMSException {
		try{
			collection.storeFile(this);
			this.remove();
			this.Collection = collection.getCollection();}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if (this.Collection != null)
				try {
					this.Collection.close();} 
			    catch (XMLDBException e) {
					e.printStackTrace();}
		}
		
	}

	/**
	 * Executes a query (either XPath or XQuery) against this
	 * <code>DBFile</code>. The result is an array containing the results of
	 * the query.
	 * <br><br>
	 * The idea is that the XQuery is a superset of the XPath.
	 * 
	 * @param query
	 *            The XPath or XQuery query string to use.
	 * @return an array of <code>DBFile (DBFile[])</code> containing the
	 *         results of the query.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public DBFile[] query(String query) throws DBMSException {
		try {
			XPathQueryService service = (XPathQueryService) this.Collection.getService("XQueryService", "1.0");
			ResourceSet rs = service.queryResource(getName(), query);
                        
			DBFile[] ret = new DBFile[(int) rs.getSize()];
			ResourceIterator ri = rs.getIterator();
			int index = 0;
                        
			while (ri.hasMoreResources()) {
				ret[index++] = new DBFile(null, null, ri.nextResource(), "QueryDBFile", User, Password);
			}
//                        rs.clear();
			return ret;
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
		finally{
			if (this.Collection != null)
				try {
					this.Collection.close();} 
			    catch (XMLDBException e) {
					e.printStackTrace();}
		}
	}

	/**
	 * Runs a set of XUpdate operations against this <code>DBFile</code>.
	 * The file will be updated in place in the collection it belongs to. 
	 *  
	 * @param updateQuery The XUpdate commands to use.
	 * @return the number of modified nodes in the file.
	 * @throws DBMSException with expected error codes.
	 */
	public long update(String updateQuery) throws DBMSException {
		try {
			XUpdateQueryService service = (XUpdateQueryService)this.Collection.getService("XUpdateQueryService", "1.0");
			return service.updateResource(getName(),updateQuery);
		}catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
		finally{
			if (this.Collection != null)
			try {
				this.Collection.close();} 
		    catch (XMLDBException e) {
				e.printStackTrace();}
		}
	}
	
	/**
	 * Checks whether a query inside an <code>DBFile</code> returns any
	 * results.
	 * 
	 * @param selectQuery
	 *            The query to perform.
	 * @return <code>true</code> if the query returns any results,
	 *         <code>false</code> otherwise.
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	public boolean exist(String selectQuery) throws DBMSException {
		DBFile[] files = this.query(selectQuery);
		return (files.length == 0) ? false : true;
	}

	/**
	 * Executes a query (either XPath or XQuery) against this
	 * <code>DBFile</code>. The result is an array (of
	 * <code>String</code>) containing the results of the query.
	 * 
	 * @return an array of <code>String (String[])</code> containing the
	 *         results of the query.
	 * @param query
	 *            The XPath or XQuery query string to use.
	 * @throws DBMSException
	 *             with expected error codes
	 */

        public String[] queryString(String query) throws DBMSException {
            String[] ret = null;
            try {
                XPathQueryService service = (XPathQueryService) this.Collection.getService("XQueryService", "1.0");
                ResourceSet rs = service.queryResource(getName(), query);
                XMLResource resource;
                ret = new String[(int) rs.getSize()];
                ResourceIterator ri = rs.getIterator();
                int index = 0;

                while (ri.hasMoreResources()) {
                    resource= (XMLResource) ri.nextResource();
                    ret[index++] = (String) resource.getContent();
                }
            } catch (XMLDBException XMLDBEx) {
                throw new DBMSException(XMLDBEx);
            }
            finally{
                if (this.Collection != null)
                    try {
                        this.Collection.close();}
                    catch (XMLDBException e) {
                        e.printStackTrace();
                    }
            }
            return ret;

        }

	public Resource getResource() {
		if (this.Type.equals("BinaryDBFile"))
			return this.BinaryResource;
		else
			return this.XMLResource;
	}

	protected void setResource(Resource res) throws DBMSException {
		try {
			if (res.getResourceType().equals("XMLResource"))
				this.XMLResource = (XMLResource) res;
			else
				this.BinaryResource = (BinaryResource) res;
		} catch (XMLDBException XMLDBEx) {
			throw new DBMSException(XMLDBEx);
		}
	}

	protected void setCollection(Collection col) {
		this.Collection = col;
	}
	
	protected void setType(String type) throws DBMSException {
		if (type.equals("XMLResource"))
			this.Type = "XMLDBFile";
		else if (type.equals("BinaryResource"))
			this.Type = "BinaryDBFile";
		else
			this.Type = type;
	}
}

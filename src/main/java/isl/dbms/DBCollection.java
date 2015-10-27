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

import javax.xml.transform.OutputKeys;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XUpdateQueryService;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.modules.XMLResource;

/**
 * A
 * <code>DBCollection</code> represents a collection of
 * <code>DBFile</code> objects (files/resources stored within an XML database).
 * An XML database MAY expose collections as a hierarchical set of parent and
 * child collections. <br> <br> A
 * <code>DBCollection</code> provides access to the
 * <code>DBFile</code> objects stored within the
 * <code>DBCollection</code> instances.
 */
public class DBCollection extends DBXUpdate {

    private Collection Collection, col;
    private String DB;
    private String User, Password;

    protected DBCollection(String database, Collection col) throws DBMSException {
        try {
            this.Collection = col;
            this.DB = database;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (this.Collection != null) {
                try {
                    this.Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Constructs a new
     * <code>DBCollection</code> instance associated with the specified
     * collection in the specified database.
     *
     * @param database the database where the collection (next argument) is in.
     * @param collection the name of the collection.
     * @param user the username to use for authentication to the database or
     * <code>null</code> if the database does not support authentication.
     * @param password the password to use for authentication to the database or
     * <code>null</code> if the database does not support authentication.
     *
     * @throws DBMSException with expected error codes.
     */
    public DBCollection(String database, String collection, String user, String password)
            throws DBMSException {
        try {
            registerDriver();
            this.DB = database;
            this.User = user;
            this.Password = password;
            // get the collection
            this.Collection = DatabaseManager.getCollection(database + collection,
                    user, password);
            deregisterDriver();
            if (this.Collection == null) {
                throw new DBMSException("DBCollection could not be instantiated. Collection "
                        + collection + " not found");
            }
            this.Collection.setProperty(OutputKeys.INDENT, "yes");
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (this.Collection != null) {
                try {
                    this.Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns the name associated with this
     * <code>DBCollection</code>.
     *
     * @return the name of this <code>DBCollection</code>.
     * @throws DBMSException with expected error codes.
     */
    public String getName() throws DBMSException {
        try {
            return this.Collection.getName();
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (this.Collection != null) {
                try {
                    this.Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns the full path in the database of the collection represented by
     * this
     * <code>DBCollection</code>.
     *
     * @return the full path in the database of the collection represented by
     * this <code>DBCollection</code>.
     * @throws DBMSException with expected error codes.
     */
    public String getPath() throws DBMSException {
        return this.getName();
    }

    /**
     * Returns the parent collection for this
     * <code>DBcollection</code> or
     * <code>null</code> if no parent collection exists.
     *
     * @return the parent <code>DBcollection</code> instance.
     * @throws DBMSException with expected error codes.
     */
    public DBCollection getParentCollection() throws DBMSException {
        try {
            Collection col = this.Collection.getParentCollection();
            if (col == null) {
                return null;
            } else {
                return new DBCollection(this.DB, col.getName(), this.User, this.Password);
            }
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns the number of child collections under this
     * <code>DBCollection</code> or 0 if no child collections exist.
     *
     * @return the number of child collections.
     * @throws DBMSException with expected error codes.
     */
    public int getChildCollectionCount() throws DBMSException {
        try {
            return this.Collection.getChildCollectionCount();
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (Collection != null) {
                try {
                    Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns an array containing the collection names naming all child
     * collections in this
     * <code>DBCollection</code>. If no child collections exist an empty array
     * is returned.
     *
     * @return an array of <code>String</code> containing the collection names
     * for all child collections.
     * @throws DBMSException with expected error codes.
     */
    public String[] listChildCollections() throws DBMSException {
        try {
            return this.Collection.listChildCollections();
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (Collection != null) {
                try {
                    Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns a
     * <code>DBCollection</code> instance for the requested child collection if
     * it exists.
     *
     * @param collection the name of the child collection to be retrieved.
     * @return a <code>DBCollection</code> representing the requested child
     * collection or <code>null</code> if no such child exist.
     * @throws DBMSException with expected error codes.
     */
    public DBCollection getChildCollection(String collection)
            throws DBMSException {
        try {
            Collection col = this.Collection.getChildCollection(collection);
            if (col == null) {
                return null;
            } else {
                return new DBCollection(this.DB, col.getName(), this.User, this.Password);
            }
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates a new collection in the database as a child of this
     * <code>DBCollection</code>. The new collection is associated with a new
     * <code>DBCollection</code>, which is returned. The new
     * <code>DBCollection</code> will be created relative to this
     * <code>DBCollection</code>.
     *
     * @param collection the name of the collection to be created.
     * @return an empty <code>DBCollection</code> instance associated with the
     * created collection.
     * @throws DBMSException with expected error codes.
     */
    public DBCollection createCollection(String collection)
            throws DBMSException {
        try {
            CollectionManagementService cms = (CollectionManagementService) this.Collection
                    .getService("CollectionManager", "1.0");
            Collection col = cms.createCollection(collection);
            return new DBCollection(this.DB, col.getName(), this.User, this.Password);
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }

    }

//	/**
//	 * Creates a new collection in the database as a child of another
//	 * collection. The new collection is associated with a new
//	 * <code>DBCollection</code>, which is returned. The new collection will
//	 * be created relative to the collection.
//	 * 
//	 * @param database
//	 *            the database where the collection (next argument) is in.
//	 * @param collection
//	 *            the collection where the new collection will be created into.
//	 * @param user
//	 *            the username to use for authentication to the database or
//	 *            <code>null</code> if the database does not support
//	 *            authentication.
//	 * @param password
//	 *            the password to use for authentication to the database or
//	 *            <code>null</code> if the database does not support
//	 *            authentication.
//	 * @param childCollection
//	 *            the name of the colelction to be created.
//	 * @return an empty <code>DBCollection</code> instance associated with the
//	 *         created collection.
//	 * @throws DBMSException
//	 *             with expected error codes.
//	 */
//	public static DBCollection createCollection(String database, String collection, String user, String password, String childCollection)
//	throws DBMSException {
//		try {
//			Collection col = obtainCollection(database, collection, user,
//					password);
//			CollectionManagementService cms = (CollectionManagementService) col
//					.getService("CollectionManager", "1.0");
//			Collection newCol = cms.createCollection(collection);
//			return new DBCollection(database, newCol.getName(), user, password);
//		} catch (XMLDBException XMLDBEx) {
//			throw new DBMSException(XMLDBEx);
//		}
//	}
    /**
     * Removes a collection from the database. The specified collection must be
     * a child collection of this
     * <code>DBCollection</code>. Thus it is treated relative to this
     * <code>DBCollection</code>. <br><br> Since the collection to be removed is
     * treated relative to this
     * <code>DBCollection</code>, <b>the name of the collection must NOT start
     * with a '/'</b>.
     *
     * @param collection the name of the collection to be removed.
     * @throws DBMSException with expected error codes.
     */
    public void removeCollection(String collection) throws DBMSException {
        try {
            DBCollection tmp = this.getChildCollection(collection);
            if (tmp == null) {
                throw new DBMSException("Collection " + collection + " not found");
            }
            CollectionManagementService cms = (CollectionManagementService) this.Collection
                    .getService("CollectionManager", "1.0");
            cms.removeCollection(collection);
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (this.Collection != null) {
                try {
                    this.Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns the number of files currently stored in this in this
     * <code>DBCollection</code> or 0 if the collection is empty.
     *
     * @return the number of files in the collection.
     * @throws DBMSException with expected error codes.
     */
    public int getFileCount() throws DBMSException {
        try {
            return this.Collection.getResourceCount();
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (this.Collection != null) {
                try {
                    this.Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns an array containing the names (ids) for all files stored in this
     * <code>DBCollection</code>. If no files exist an empty array is returned.
     *
     * @return an array of <code>String</code> containing the names for all XML
     * files in the collection.
     * @throws DBMSException with expected error codes.
     */
    public String[] listFiles() throws DBMSException {
        try {
            return this.Collection.listResources();
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (this.Collection != null) {
                try {
                    this.Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates a new empty
     * <code>DBFile</code> with the specified name. The type of the
     * <code>DBFile</code> returned is determined by the type argument. The DBMS
     * API currently defines "XMLDBFile", "BinaryDBFile" and "QueryDBFile" as
     * valid types. <b>When creating a new file only the first two types are
     * valid. </b> The name provided must be unique within the scope of the
     * collection. If name is
     * <code>null</code> or its value is empty then a name is automatically
     * generated. The
     * <code>DBFile</code> created is not stored to the database until
     * storeFile() (or other 'store' method) is called. <br> <br> The collection
     * specified by this
     * <code>DBCollecion</code> is the owner of the created
     * <code>DBFile</code>.
     *
     * @param file the name of the file to be created.
     * @param type the type of the file to be created.
     * @return an empty <code>DBFile</code> instance.
     * @throws DBMSException with expected error codes.
     */
    public DBFile createFile(String file, String type) throws DBMSException {
        try {
            DBFile f = this.getFile(file);
            if (f != null) {
                throw new DBMSException("File already exists: " + file);
            }
            String rType;
            if (type.equals("BinaryDBFile")) {
                rType = "BinaryResource";
            } else if (type.equals("XMLDBFile")) {
                rType = "XMLResource";
            } else {
                throw new DBMSException("Unknown DBFile type: " + type);
            }
            Resource res = Collection.createResource(file, rType);
            return new DBFile(this.DB, this.Collection, res, type, this.User, this.Password);
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (this.Collection != null) {
                try {
                    this.Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }
//
//	/**
//	 * Creates a new empty <code>DBFile</code> with the specified name. The
//	 * type of the <code>DBFile</code> returned is determined by the type
//	 * argument. The DBMS API currently defines "XMLDBFile", "BinaryDBFile" and
//	 * "QueryDBFile" as valid types. <b>When creating a new file only the first
//	 * two types are valid. </b> The name provided must be unique within the
//	 * scope of the collection. If name is <code>null</code> or its value is
//	 * empty then a name is automatically generated. The <code>DBFile</code>
//	 * created is not stored to the database until storeFile() is called. <br>
//	 * <br>
//	 * The collection specified is the owner of the created <code>DBFile</code>.
//	 * 
//	 * @param database
//	 *            the database where the collection (next argument) is in.
//	 * @param collection
//	 *            the collection where the file will be created into.
//	 * @param user
//	 *            the username to use for authentication to the database or
//	 *            <code>null</code> if the database does not support
//	 *            authentication.
//	 * @param password
//	 *            the password to use for authentication to the database or
//	 *            <code>null</code> if the database does not support
//	 *            authentication.
//	 * @param file
//	 *            the name of the file to be created.
//	 * @param type
//	 *            the type of the file to be created.
//	 * @return an empty <code>DBFile</code> instance.
//	 * @throws DBMSException
//	 *             with expected error codes.
//	 */
//	public static DBFile createFile(String database, String collection,
//			String user, String password, String file, String type)
//			throws DBMSException {
//		try {
//			Collection col = obtainCollection(database, collection, user,
//					password);
//			Resource r = col.getResource(file);
//			if (r != null)
//				throw new DBMSException("File already exists: " + file);
//			String rType;
//			if (type.equals("BinaryDBFile"))
//				rType = "BinaryResource";
//			else if (type.equals("XMLDBFile"))
//				rType = "XMLResource";
//			else
//				throw new DBMSException("Unknown DBFile type: " + type);
//			Resource res = col.createResource(file, rType);
//			return new DBFile(database, col, res, type, user, password);
//		} catch (XMLDBException XMLDBEx) {
//			throw new DBMSException(XMLDBEx);
//		}
//	}

    /**
     * Retrieves a file (stored in this
     * <code>DBCollection</code>) from the database. If the file could not be
     * located a
     * <code>null</code> value will be returned.
     *
     * @param file the filename of the file to be retrieved.
     * @return a <code>DBFile</code> representing the retrieved file * * *
     * or <code>null</code> if the file does not exist.
     * @throws DBMSException with expected error codes.
     */
    public DBFile getFile(String file) throws DBMSException {
        try {
            Resource res = this.Collection.getResource(file);
            if (res == null) {
                return null;
            } else {
                return new DBFile(this.DB, this.Collection, res, this.User, this.Password);
            }
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (this.Collection != null) {
                try {
                    this.Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Removes a file from this
     * <code>DBCollection</code>. The specified file is removed from the
     * database.
     *
     * @param file the name of the file to be removed.
     * @throws DBMSException with expected error codes.
     */
    public void removeFile(String file) throws DBMSException {
        removeFile(new DBFile(this.DB, this.getName(), file, this.User, this.Password));
    }

    /**
     * Removes a
     * <code>DBFile</code> from this
     * <code>DBCollection</code>. The file associated with the specified
     * <code>DBFile</code> is removed from the database, from the collection
     * associated with this
     * <code>DBCollection</code>.<br> <br> If the
     * <code>DBFile</code> is not owned by this
     * <code>DBCollection</code> it throws an exception indicating the error.
     *
     * @param file the <code>DBFile</code> representing the file to be removed.
     * @throws DBMSException with expected error codes.
     */
    public void removeFile(DBFile file) throws DBMSException {
        try {
            if (!file.getCollection().getName().equals(getName())) {
                throw new DBMSException("File could not be removed. Collection " + this.getName()
                        + " not owner");
            }
            this.Collection.removeResource(file.getResource());
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (this.Collection != null) {
                try {
                    this.Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Stores a
     * <code>DBFile</code> into the
     * <code>DBCollection</code>. The file associated with the specified
     * <code>DBFile</code> is stored into the database. If the file does NOT
     * already exist it will be created. If it does already exist it will be
     * updated. <br><br> <b>The owner collection of the specified
     * <code>DBFile</code> is NOT changed.</b>
     *
     * @param file the <code>DBFile</code> representing the file to be stored.
     * @throws DBMSException with expected error codes.
     */
    public void storeFile(DBFile file) throws DBMSException {
        try {
            this.Collection.storeResource(file.getResource());
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (this.Collection != null) {
                try {
                    this.Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Stores a
     * <code>DBFile</code> into the
     * <code>DBCollection</code>, under a particular name. The file associated
     * with the specified
     * <code>DBFile</code> is stored into the database under the specified name.
     * If the file does NOT already exist it will be created. If it does already
     * exist it will be updated. It returns a
     * <code>DBFile</code> representing the newly stored file<br> <br> If the
     * content of the
     * <code>DBFile</code> was changed but NOT stored, the file under the
     * original name in the database will not reflect the changes (until
     * <code>store</code> is called). Only the file under the 'new' name will
     * reflect the content of this
     * <code>DBFile</code>. <br><br> <b>The owner collection of the specified
     * <code>DBFile</code> is NOT changed.</b>
     *
     * @param file the <code>DBFile</code> representing the file to be stored.
     * @param fileName the name under which the file will be stored.
     * @return the <code>DBFile</code> representing the newly stored file.
     * @throws DBMSException with expected error codes.
     */
    public DBFile storeFileAs(DBFile file, String fileName) throws DBMSException {
        String type = file.getType();
        DBFile f;
        if (type.equals("QueryDBFile") || type.equals("XMLDBFile")) {
            type = "XMLDBFile";
            f = this.createFile(fileName, type);
            f.setXMLAsString(file.getXMLAsString());
        } else {
            f = this.createFile(fileName, type);
            f.setBinary(file.getBinary());
        }
        this.storeFile(f);
        return f;
    }

   
    
     /**
     * Executes a query (either XPath or XQuery) against this
     * <code>DBCollection</code>. The result is an array containing the results
     * of the query.
     *
     * @param query the XQuery query string to use.
     * @return an array of <code>String</code> containing the results
     * of the query.
     * @throws DBMSException
     */
    public String[] query(String query) throws DBMSException {
        
         String[] ret = null;
            try {
                XPathQueryService service = (XPathQueryService) this.Collection.getService("XQueryService", "1.0");
                ResourceSet rs = service.query( query);
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

    /**
     * Runs a set of XUpdate operations against this
     * <code>DBCollection</code>. All affected documents are updated and stored
     * back into the collection.
     *
     * @param updateQuery The XUpdate commands to use.
     * @return the number of modified nodes in the file.
     * @throws DBMSException
     */
    public long update(String updateQuery) throws DBMSException {
        try {
            XUpdateQueryService service = (XUpdateQueryService) this.Collection.getService("XUpdateQueryService", "1.0");
            return service.update(updateQuery);
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        } finally {
            if (this.Collection != null) {
                try {
                    this.Collection.close();
                } catch (XMLDBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected Collection getCollection() {
        return this.Collection;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return Password;
    }

    /**
     * @return Returns the user.
     */
    public String getUser() {
        return User;
    }
    /**
     * private static Collection obtainCollection(String database, String
     * collection, String user, String password) throws XMLDBException,
     * DBMSException { try{ //register Driver String driver =
     * DBMSConfig.getEXISTDRIVER(); Class cl = Class.forName(driver);
     * DatabaseManager.registerDatabase((Database) cl.newInstance()); //get the
     * collection Collection col = DatabaseManager.getCollection(database +
     * collection, user, password); if (col == null) throw new
     * DBMSException("Collection could not be located: " + collection);
     * col.setProperty(OutputKeys.INDENT, "yes"); return col;
     * }catch(InstantiationException IEx){ throw new DBMSException(IEx);
     * }catch(IllegalAccessException IAEx){ throw new DBMSException(IAEx);
     * }catch(ClassNotFoundException CNFEx){ throw new DBMSException(CNFEx); } }
     *
     */
}

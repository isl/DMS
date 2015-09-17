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
package isl.dbms.eXist;

import isl.dbms.DBCollection;
import isl.dbms.DBFile;
import isl.dbms.DBMSException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.exist.backup.Backup;
import org.exist.backup.Restore;
import org.exist.backup.restore.listener.DefaultRestoreListener;
import org.exist.backup.restore.listener.RestoreListener;
import org.exist.xmldb.*;
import org.exist.xmldb.XmldbURI;
import org.xml.sax.SAXException;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

/**
 * ExistCollection extends DBCollection to allow the programmer to specify the
 * permissions that will be in assigned to files. It is eXist-specified and uses
 * the management service from package org.exist.xmldb.
 * <br/><br/>
 * eXist allows for Unix-like permissions on each resource. Here, only those
 * resources that can be handled through DBFile instances are supported. The
 * permissions provided are <i>read</i>, <i>write</i>, and <i>update</i> for the
 * owner, group, and world, respectively. Thus, the permissions can be
 * represented as a string of 9 characters:
 * <b>'---------'</b> gives no permissions, <b>'rwu------'</b> gives read,
 * write, and update permissions to the owner of the resource only, and
 * <b>'rwur--r--'</b> gives rwu permissions to the owner and also r permissions
 * to the group and the world.
 * <br/><br/>
 * To convert from the string to the integer that is required in ExistCollection
 * methods, simply write the permissions in binary, using 0 and 1 instead or r,
 * w, or u. The above examples become: <b>000000000</b>, <b>111000000</b>, and
 * <b>111100100</b>. You can now convert this number to decimal and pass it to
 * ExistCollection methods.
 * <br/><br/>
 * A reasonable permission scheme is to set the default permissions to 504,
 * giving rwu to user and group, and no access to the world.
 * <br/><br/>
 * ExistCollection provides two related methods: <br/>
 * <b>setPermission(isl.dbms.DBFile f, int p)</b> changes the permissions on a
 * DBFile object already stored in the collection.<br/>
 * <b>setDefaultPermissions(int p)</b> configures the collection to
 * automatically set permissions 'p' to all new files stored in the collection.
 * To accomplish this, it overrides methods <b>storeFile</b> and
 * <b>storeFileAs</b> of DBCollection.
 * <br/><br/>
 * The simplest, set-and-forget usage is:<br/><pre>
 * 		DBCollection collection = new ExistCollection(existPath, dbPath, username, password);
 * 		collection.setDefaultPermissions(permissions);</pre>
 * <br/><br/>
 */
public class ExistCollection extends DBCollection {

    private UserManagementService manager = null;
    private boolean defaultSet = false;
    private int defaultPermissions = -1;

    protected ExistCollection(String database, Collection col) throws DBMSException {
        super(database, col);

        this.createManager();
    }

    /**
     * Constructs a new <code>ExistCollection</code> instance associated with
     * the specified collection in the specified database.
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
    public ExistCollection(String database, String collection, String user, String password)
            throws DBMSException {
        super(database, collection, user, password);

        this.createManager();
    }

    /**
     * Sets the collection properties that eXist supports.
     *
     * @param property The property that will be modified. Can be:
     * <br/><ul>
     * <li>EXPAND_XINCLUDES</li>
     * <li>INDENT_SPACES</li>
     * <li>PROCESS_XSL_PI</li>
     * </ul><br/>
     * @param flag The new value of the property. This is property-specific.
     * Please consult the documentation of eXist.
     *
     * @throws DBMSException with expected error codes.
     */
    public void setProperty(String property, String flag)
            throws DBMSException {
        try {
            //eXist dependency
            if (property.equals("EXPAND_XINCLUDES")) {
                this.getCollection().setProperty(org.exist.storage.serializers.EXistOutputKeys.EXPAND_XINCLUDES, flag);
            } else if (property.equals("INDENT_SPACES")) {
                this.getCollection().setProperty(org.exist.storage.serializers.EXistOutputKeys.INDENT_SPACES, flag);
            } else if (property.equals("PROCESS_XSL_PI")) {
                this.getCollection().setProperty(org.exist.storage.serializers.EXistOutputKeys.PROCESS_XSL_PI, flag);
            }
        } catch (XMLDBException XMLDBEx) {
            throw new DBMSException(XMLDBEx);
        }
    }

    /**
     * Sets the permissions on a DBFile resource stored in this collection.
     *
     * @param f The DBFile instance to be modified.
     * @param p The new permissions. Must be a decimal integer.
     *
     * @throws XMLDBException as per the chmod method of interface
     * org.exist.xmldb.UserManagementService.
     */
    public void setPermission(DBFile f, int p)
            throws XMLDBException {
        manager.chmod(f.getResource(), p);
    }

    /**
     * Sets the default permissions for files created through this
     * ExistCollection instance. All files stored in the collection via methods
     * storeFile and storeFileAs will be given these permissions.
     *
     * @param p The new permissions. Must be a decimal integer.
     *
     */
    public void setDefaultPermissions(int p) {
        this.defaultSet = true;
        this.defaultPermissions = p;
        System.out.println("EXISTCOLLECTION default set to " + this.defaultPermissions);
    }

    /**
     * Functions as storeFileAs in DBCollection, but also sets the permissions
     * specified through setDefaultPermissions, if any.
     *
     */
    public DBFile storeFileAs(DBFile file, String fileName)
            throws DBMSException {
        DBFile f = super.storeFileAs(file, fileName);
        if (defaultSet) {
            try {
                this.setPermission(f, this.defaultPermissions);
                System.out.println("EXISTCOLLECTION 1\n\n");
            } catch (XMLDBException ex) {
                System.out.println("XMLDBException in ExistCollection:storeFileAs. Re-thrown...");
                throw new DBMSException(ex.getMessage());
            }
        }
        return f;
    }

    /**
     * Functions as storeFile in DBCollection, but also sets the permissions
     * specified through setDefaultPermissions, if any.
     *
     */
    public void storeFile(DBFile file)
            throws DBMSException {
        super.storeFile(file);
        if (defaultSet) {
            try {
                this.setPermission(file, this.defaultPermissions);
                System.out.println("EXISTCOLLECTION 2\n\n");
            } catch (XMLDBException ex) {
                System.out.println("XMLDBException in ExistCollection:storeFileAs. Re-thrown...");
                throw new DBMSException(ex.getMessage());
            }
        }
    }

    /**
     * Creates a backup of the current collection to a directory specified by
     * the param target.
     *
     * @param user the username to use for authentication to the database or
     * <code>null</code> if the database does not support authentication.
     *
     * @param pass the password to use for authentication to the database or
     * <code>null</code> if the database does not support authentication.
     *
     * @param target the directory that the backup will be stored
     *
     * @param rootCollection the collection to backup.
     *
     * @param DBURI the Database URI.
     *
     * @param systemDbCollection the system Database Collection.
     *
     * @throws DBMSException with expected error codes.
     */
    public void backup(String user, String pass, String target, String rootCollection, String DBURI, String systemDbCollection)
            throws DBMSException {
        Database database = null;

        XmldbURI collection = XmldbURI.create(DBURI + systemDbCollection);
        try {
            database = registerDatabase();
            Collection current = DatabaseManager.getCollection(collection.toString(), user, pass);
        } catch (XMLDBException e2) {
            e2.printStackTrace();
        }
        try {
            Backup backup = new Backup(user, pass, target, XmldbURI.xmldbUriFor(rootCollection));
            backup.backup(false, null);
        } catch (XMLDBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    /**
     * Restores a backup from a directory specified by the param contents.
     *
     * @param user the username to use for authentication to the database or
     * <code>null</code> if the database does not support authentication.
     *
     * @param pass the password to use for authentication to the database or
     * <code>null</code> if the database does not support authentication.
     *
     * @param newAdminPass the password to use for authentication of backup or
     * <code>null</code> if the backup file was not created with a password
     *
     * @param contents the directory that the backup is stored
     *
     * @param DBURI the Database URI.
     *
     * @param systemDbCollection the system Database Collection.
     *
     * @throws DBMSException with expected error codes.
     */
    public void restore(String user, String pass, String newAdminPass, File contents, String DBURI, String systemDbCollection) {
        Database database = null;

        XmldbURI collection = XmldbURI.create(DBURI + systemDbCollection);
        try {
            database = registerDatabase();
            Collection current = DatabaseManager.getCollection(collection.toString(), user, pass);
        } catch (XMLDBException e2) {
            e2.printStackTrace();
        }
        try {
            RestoreListener listener = new DefaultRestoreListener();
            //   restore.restore(listener, user, pass, newAdminPass, contents, DBURI);
            Restore restore = new Restore();
            restore.restore(listener, user, pass, newAdminPass, contents, DBURI);
        } catch (XMLDBException ex) {
            Logger.getLogger(ExistCollection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExistCollection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ExistCollection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ExistCollection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ExistCollection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Database registerDatabase()
            throws DBMSException {
        Database database = null;
        try {
            Class cl = null;
            try {
                cl = Class.forName("org.exist.xmldb.DatabaseImpl");
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            try {
                database = (Database) cl.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
        } catch (XMLDBException e2) {
            e2.printStackTrace();
        }
        return database;

    }

    private void createManager()
            throws DBMSException {
        try {
            CollectionImpl impl = (CollectionImpl) this.getCollection();

            manager = (UserManagementService) impl.getService("UserManagementService", "1.0");
            /*   if (impl.isRemoteCollection()) {
             System.out.println("ExistCollection: setting up Remote manager");
             manager = new RemoteUserManagementService((RemoteCollection) impl);
             } else {
             System.out.println("ExistCollection: setting up Local manager");
             manager = new LocalUserManagementService(new User(this.getUser(), this.getPassword()) {
             },
             BrokerPool.getInstance(), (LocalCollection) impl);
             }*/

            if (manager == null) {
                throw new Error("UserManagementService could not be instantiated in ExistCollection:init<>");
            }
        } catch (Exception ex) {
            System.out.println("Exception in ExistCollection constructor: " + ex.getMessage());
            ex.printStackTrace(System.out);
            throw new DBMSException(ex.getMessage());
        }
    }

}

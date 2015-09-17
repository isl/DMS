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
package isl.dms.file;

import isl.dbms.DBMSException;
import isl.dms.DMSConfig;
import isl.dms.DMSException;

/**
 * The <code>DMSAdmin</code> represents an 'admin' part of a
 * file of the system. An admin part is strictly associated
 * with a file and a collection. The <code>DMSAdmin</code>
 * provides basic functionality for handling and managing
 * an 'admin' part. <br>
 * <br>
 * An admin part of a file of the system is identified by the
 * 'file', it belongs to, and the 'collection' where that file
 * is in.
 * Each admin part can have various 'fields' for describing it.
 * Such 'fields' can be added by the 'add' method. In general
 * the 'fields' are managed by the 'add', 'remove', 'get', 'set' methods.
 * <br>
 */
public class DMSAdmin extends DMSFile {
	private String file;
	private String collection;
	private int id;

	/**
     * Creates a new <code>DMSAdmin</code>.
     * The new <code>DMSAdmin</code> represents the admin part
     * of a file in a collection.
     * @param conf a DMSConfig object
     * @param file the name (id) of the file,
     *            to which the admin part belongs.
     * @param collection the name (id) of the collection,
     *            into which the file is.
     * @throws EntryNotFoundException
     *            if there is not any admin part for the specified file
     *            in the specified collection.
     * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public DMSAdmin(String file, String collection, DMSConfig conf) throws EntryNotFoundException, DMSException, DBMSException {
		if (checkAdmin(file, collection, conf) == false)
			throw new EntryNotFoundException("Admin of file " + file
					+ " in collection " + collection + " not found");
		this.setDMSfile(conf.ADMINS_FILE, conf);
		this.file = file;
		this.collection = collection;
		String query = "/DMS/admins/admin[file='" + this.file
			+ "' and collection='" + this.collection + "']/@id/string()";
		this.id = Integer.parseInt(this.queryString(query)[0]);
	}

	/**
     * Adds a new admin part for a file to the system.
     * A <code>DMSAdmin</code> is returned representing
     * the new admin part for a file. If an admin part
     * for the specified filename allready exist, an exception
     * is thrown indicating the error.
     * @return a <code>DMSCollection</code> representing the new collection.
     * @param conf a DMSConfig object
     * @param file the name (id) of the file,
     *            into which the new admin part will be added.
     * @param collection the name (id) of the collection,
     *            into which the file is.
     * @throws EntryExistException
     *            if an admin part already exists for the specified file
     *            in the specified collection.
     * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static DMSAdmin addAdmin(String file, String collection, DMSConfig conf) throws EntryExistException, DMSException, DBMSException {
		String newData = "<file>" + file + "</file>\n"
			+ "<collection>" + collection + "</collection>\n";
		DMSFile DMSfile = new DMSFile(conf.ADMINS_FILE, conf);
		if (checkAdmin(file, collection, conf))
			throw new EntryExistException("Admin of file " + file
					+ " in collection " + collection + " already exists");
		DMSfile.addEntity("admin", newData);
		return new DMSAdmin(file, collection, conf);
	}

	/**
	 * Removes this admin from the system.
	 * 
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void remove() throws DMSException, DBMSException {
		this.removeEntity("id", String.valueOf(this.id));
	}

	/**
     * Checks the existance of an admin part of a file.
     * @return <code>true</code> if the admin exists,
     *         <code>false</code> otherwise.
     * @param conf a DMSConfig object
     * @param file the name of the file,
     *            of which we want to find the admin part.
     * @param collection the name (id) of the collection,
     *            into which the file is.
     * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static boolean checkAdmin(String file, String collection, DMSConfig conf) throws DMSException, DBMSException {
		DMSFile DMSfile = new DMSFile(conf.ADMINS_FILE, conf);
		return DMSfile.exist("/DMS/admins/admin[file='"+file+"' and collection='"+collection+"']");
	}

	/**
	 * Returns the 'system' id of the admin.
	 * 
	 * @return the system id of the admin.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Adds a new field into the admin.
	 * 
	 * @param field
	 *            the name of the field to be added.
	 * @param value
	 *            the (text) value of the tag.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void addField(String field, String value) throws DMSException, DBMSException {
		if (this.hasField(field))
			throw new DMSException("Admin of file " + this.file + " already has field: " + field);
		this.addIntoEntity(this.id, field, value);
	}

	/**
	 * Removes a field from the admin.
	 * 
	 * @param field
	 *            the name of the field to be removed.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void removeField(String field) throws DMSException, DBMSException {
		if (this.hasField(field) == false)
			throw new DMSException("Admin of file " + this.file + " does not have the field " + field);
		this.removeFromEntity(this.id, field);
	}

	/**
	 * Gets the value of a field, of the admin. If there are
	 * more than one fields with the specified name, the value of
	 * the first is return.
	 * 
	 * @param field
	 *            the name of the field.
	 * @return the value of the specified field
	 *         or <code>null</code> if there is no such field.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public String getField(String field) throws DMSException, DBMSException {
		return this.getFromEntity(this.id, field);
	}

	/**
	 * Sets the value for a field, of the admin.
	 * 
	 * @param field
	 *            the name of the field.
	 * @param value
	 *            the (new) value of the field.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void setField(String field, String value) throws DMSException, DBMSException {
		if (this.hasField(field) == false)
			throw new DMSException("Admin of file " + this.file + " does not have tag: " + field);
		this.setIntoEntity(this.id, field, value);
	}

	/**
	 * Checks the existance of a field
	 * @param field the name of the field we want to check.
	 * @return <code>true</code> if the field exists,
	 *         <code>false</code> otherwise.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public boolean hasField(String field) throws DMSException, DBMSException{
		return this.hasIntoEntity(this.id, field);		
	}
}

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
 * The <code>DMSCollection</code> represents a collection of the system.
 * It provides basic functionality for handling and managing a collection. <br>
 * <br>
 * A collection of the system is identified by a 'collectionName'.
 * Each collection can have various 'tags' for describing it.
 * Such 'tag' can be added by the 'add' method. In general
 * the 'tags' are managed by the 'add', 'remove', 'get', 'set' methods.
 * <br>
 */
public class DMSCollection extends DMSFile {
	private String name;
	private int id;

	/**
     * Creates a new <code>DMSCollection</code>.
     * The new <code>DMSCollection</code>
     * represents a collection of the system.
     * @param conf a DMSConfig object
     * @param name the name of the collection.
     * @throws EntryNotFoundException if there is not any collection with the specified name.
     * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public DMSCollection(String name, DMSConfig conf) throws EntryNotFoundException, DMSException, DBMSException {
		if (checkCollection(name, conf) == false)
			throw new EntryNotFoundException("Collection not found: " + name);
		this.setDMSfile(conf.COLLECTIONS_FILE, conf);
		this.name = name;
		String query = "/DMS/collections/collection[name='" + this.name + "']/@id/string()";
		this.id = Integer.parseInt(this.queryString(query)[0]);
	}

	/**
     * Adds a new collection to the system. A <code>DMSCollection</code>
     * is returned representing the new collection. If a collection with the
     * specified name allready exist, an exception is thrown
     * indicating the error.
     * @return a <code>DMSCollection</code> representing the new collection.
     * @param conf a DMSConfig object
     * @param name the name of the new collection.
     * @throws EntryExistException if a collection with the specified name already exists.
     * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static DMSCollection addCollection(String name, DMSConfig conf) throws EntryExistException, DMSException, DBMSException {
		String newData = "<name>"+ name + "</name>\n";
		DMSFile file = new DMSFile(conf.COLLECTIONS_FILE, conf);
		if (checkCollection(name, conf))
			throw new EntryExistException("Collection already exists: " + name);
		file.addEntity("collection", newData);
		return new DMSCollection(name, conf);
	}

	/**
	 * Removes this collection from the system.
	 * 
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void remove() throws DMSException, DBMSException {
		this.removeEntity("id", String.valueOf(this.id));
	}

	/**
     * Checks the existance of a collection.
     * @return <code>true</code> if the collection exists,
     *         <code>false</code> otherwise.
     * @param conf a DMSConfig object
     * @param name the name of the collection to find.
     * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static boolean checkCollection(String name, DMSConfig conf) throws DMSException, DBMSException {
		DMSFile file = new DMSFile(conf.COLLECTIONS_FILE, conf);
		return file.exist("/DMS/collections/collection[name='"+name+"']");
	}

	/**
	 * Adds a new field into the collection.
	 * 
	 * @param field
	 *            the name of the field to be added.
	 * @param value
	 *            the (text) value of the field.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void addField(String field, String value) throws DMSException, DBMSException {
		if (this.hasField(field))
			throw new DMSException("Collection " + this.name + " already has tag: " + field);
		this.addIntoEntity(this.id, field, value);
	}

	/**
	 * Removes a field from the collection.
	 * 
	 * @param field
	 *            the name of the field to be removed.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void removeField(String field) throws DMSException, DBMSException {
		if (this.hasField(field) == false)
			throw new DMSException("Collection " + this.name + " does not have the tag " + field);
		this.removeFromEntity(this.id, field);
	}

	/**
	 * Gets the value of a field, of the collection. If there are
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
	 * Sets the value for a field of the collection.
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
			throw new DMSException("Collection " + this.name + " does not have tag: " + field);
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

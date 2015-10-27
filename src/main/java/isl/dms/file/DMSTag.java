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

public class DMSTag extends DMSFile {
	private String xPath;
	private int id;

	/**
     * Creates a new <code>DMSTag</code>. The new <code>DMSTag</code>
     * represents a tag.
     * @param conf a DMSConfig object
     * @param xPath the xPath for the tag.
     * @throws EntryNotFoundException if there is not any tag for the specified xPath.
     * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public DMSTag(String xPath, DMSConfig conf) throws EntryNotFoundException, DMSException, DBMSException {
		if (checkTag(xPath, conf) == false)
			throw new EntryNotFoundException("Tag not found for " + xPath);
		this.setDMSfile(conf.TAGS_FILE, conf);
		this.xPath = xPath;
		String query = "/DMS/tags/tag[xpath='" + this.xPath + "']/@id/string()";
		this.id = Integer.parseInt(this.queryString(query)[0]);
	}

	/**
     * Adds a new tag to the system. A <code>DMSTag</code>
     * is returned representing the new tag. If a tag with the
     * specified xPath allready exist, an exception is thrown
     * indicating the error.
     * @return a <code>DMSTag</code> representing the new tag.
     * @param tagName 
     * @param displayName 
     * @param order 
     * @param conf the name of the group to find.
     * @param xPath the xPath of the new tag.
     * @throws EntryExistException if a tag for the specified xPath already exists.
     * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static DMSTag addTag(String xPath, String tagName, String displayName, String order, DMSConfig conf) throws EntryExistException, DMSException, DBMSException {
		String newData = "<xpath>" + xPath + "</xpath>\n"
			+ "<tagName>" + tagName + "</tagName>\n" 
			+ "<displayName>" + displayName + "</displayName>\n"
			+ "<order>" + order + "</order>\n";
		DMSFile file = new DMSFile(conf.TAGS_FILE, conf);
		if (checkTag(xPath, conf))
			throw new EntryExistException("Tag already exists for: " + xPath);
		file.addEntity("tag", newData);
		return new DMSTag(xPath, conf);
	}

	/**
	 * Removes this tag from the system.
	 * 
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void remove() throws DMSException, DBMSException {
		this.removeEntity("id", String.valueOf(this.id));
	}

	/**
     * Checks the existance of a tag.
     * @return <code>true</code> if the tag exists, <code>false</code>
     *         otherwise.
     * @param conf a DMSConfig object
     * @param xPath the xPath of the tag to find.
     * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static boolean checkTag(String xPath, DMSConfig conf) throws DMSException, DBMSException {
		DMSFile file = new DMSFile(conf.TAGS_FILE, conf);
		return file.exist("/DMS/tags/tag[xpath='"+xPath+"']");
	}

	/**
	 * Adds a new field into the tag.
	 * 
	 * @param field the name of the field to add.
	 * @param value tha value of the new field.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void addField(String field, String value) throws DMSException, DBMSException{
		this.addIntoEntity(this.id, field, value);
	}

	/**
	 * Removes a field from the tag.
	 * 
	 * @param field the name of the field to remove. 
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void removeField(String field) throws DMSException, DBMSException{
		this.removeFromEntity(this.id, field);
	}

	/**
	 * Gets tha value of a field of the tag.
	 * 
	 * @param field the name of the field we want.
	 * @return a <code>String</code> representing
	 *         the value of the specfied field.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public String getField(String field) throws DMSException, DBMSException{
		return this.getFromEntity(this.id, field);
	}

	/**
	 * Sets tha value for a field of the tag.
	 * 
	 * @param field the name of the field we want.
	 * @param value the 'new' value of the field.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void setField(String field, String value) throws DMSException, DBMSException{
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

	/**
     * Gets the value(s) of all fields named field, of the tags
     * that are of the specified type.
     * @return an array of <code>String (String[])</code> containing
     *         the value(s) of all fields named fields,
     *         of the tags, that are of the specified type.
     * @param conf a DMSConfig object
     * @param field the name of the field
     * @param type the type of the tag.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static String[] valueOf(String field, String type, DMSConfig conf) throws DMSException, DBMSException{
		DMSFile file = new DMSFile(conf.TAGS_FILE, conf);
		return file.queryString("/DMS/tags/tag[type='"+type+"']/"+field+"/text()");
	}

	/**
     * Returns the value(s) of all fields, with a particular name,
     * of the tags that are of the specified type and category.
     * @return an array of <code>String (String[])</code> containing
     *         the value(s) of all fields with the specified name,
     *         of the tags, that are of the specified type and category.
     * @param conf a DMSConfig object
     * @param field the name of the field.
     * @param type the type of the tag.
     * @param category the category of the tag.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static String[] valueOf(String field, String type, String category, DMSConfig conf) throws DMSException, DBMSException{
		DMSFile file = new DMSFile(conf.TAGS_FILE, conf);
		return file.queryString("/DMS/tags/tag[type='"+type+"' and category='"+category+"']/"+field+"/text()");
	}
}

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

import isl.dbms.DBFile;
import isl.dbms.DBCollection;
import isl.dbms.DBMSException;
import isl.dms.DMSConfig;
import isl.dms.DMSException;

/**
 * The <code>DMSFile</code> represents a file of the system. It provides basic
 * functionality for handling and managing a file.
 * A file of the system is used for storing 'entities' of something.
 * Each 'entity' can be treated as an XML <code>String</code>. <br>
 * <br>
 * All the files of the system have a <b>root</b> element named <b>DMS</b>,
 * which cannot be changed. Under that root there is another
 * element ('entitiesRoot') for describing all the 'entinties'
 * of the <code>DMSFile</code>. Each 'entity' has a default <b>id</b>,
 * describing uniquely that 'entity'. <br>
 * <br>
 * The methods:
 * <ul>
 * <li><code>addIntoEntity(int, String, String)</code></li>
 * <li><code>removeFromEntity(int, String)</code></li>
 * <li><code>getFromEntity(int, String)</code></li>
 * <li><code>setIntoEntity(int, String, String)</code></li>
 * <li><code>hasIntoEntity(int, String)</code></li>
 * </ul>
 * are used to manage simple 'entities' (one level of tags),
 * while the methods:
 * <ul>
 * <li><code>addData(String, String)</code></li>
 * <li><code>removeData(String)</code></li>
 * <li><code>getData(String)</code></li>
 * <li><code>setData(String, String)</code></li>
 * <li><code>exist(String)</code></li>
 * </ul>
 * are used to manage eny kind of 'entities'.
 * <br><br>
 * For example, the <code>DMSExamples</code> file should have the
 * following structure:
 * <br>
 * <code>
 * &LT;DMS&GT
 * <br>
 * &nbsp&nbsp&nbsp&nbsp&LT;examples&GT
 * <br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&LT;example id="25"&GT
 * <br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp;...
 * <br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&LT;/example&GT
 * <br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp;...
 * <br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&LT;example id="45"&GT
 * <br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp;...
 * <br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&LT;/example&GT
 * <br>
 * &nbsp&nbsp&nbsp&nbsp&LT;/examples&GT
 * <br>
 * &LT;/DMS&GT
 * </code>
 */
public class DMSFile {
    
    protected DMSConfig conf;
    protected DBFile DBfile;
    protected String DBfilename;
    
    protected DMSFile(){
    }
    
    /**
     * Constructs a new <code>DMSFile</code> instance associated with the
     * specified file in the database.
     * @param conf a DMSConfig object
     * @param file the name of the file.
     * @throws DBMSException with expected error codes.
     * @throws DMSException with expected error codes.
     */
    public DMSFile(String file, DMSConfig conf) throws DBMSException {
        this.DBfile = new DBFile(conf.DB, conf.COLLECTION, file, conf.DB_USERNAME, conf.DB_PASSWORD);
        this.DBfilename = file;
        this.conf = conf;
    }
    
    /**
     * Creates a new <code>DMSFile</code>. The new file is stored in
     * the database and has the specified parameter 'root' as
     * 'entitiesRoot' element.
     * @param conf a DMSConfig object
     * @param file the name of the file to be created.
     * @param root the name of the 'entitiesRoot' element.
     * @return a new DMSFile.
     * @throws DMSException with expected error codes.
     * @throws DBMSException with expected error codes.
     */
    public static DMSFile createNew(String file, String root, DMSConfig conf) throws DMSException, DBMSException{
    	DBCollection col = new DBCollection(conf.DB, conf.COLLECTION, conf.DB_USERNAME, conf.DB_PASSWORD);
        DBFile f = col.createFile(file, "XMLDBFile");
        String content = "<DMS>\n"
                + "<"+root+">\n"
                + "</"+root+">\n"
                + "</DMS>";
        f.setXMLAsString(content);
        f.store();
        return new DMSFile(file, conf);
    }
    
    /**
     * Adds an new 'entity' under the 'entitiesRoot'. The new 'entity' is
     * specified/described by the xml parameter.<br>
     * <br>
     * A new 'id' must have been obtained for the new 'entity'
     * (<i>newId()</i> method) and should be included
     * (as atribute of the 'entity') in the xml parameter.
     *
     * @param xml <code>String</code> specifying the 'entity' to be added.
     * @throws DBMSException with expected error codes.
     */
    public void addEntity(String xml) throws DBMSException{
        this.DBfile.xAppend(this.getEntitiesRootXPath(), xml);
    }
    
    /**
     * Adds an new 'entity' under the 'entitiesRoot'. The new 'entity' is
     * specified by the entity parameter and it contains the xml that
     * is specified/described by the xml parameter. The new 'entity' is
     * automatically assigned with a new 'id' in the system, which is
     * returned.
     * @param entity the name of the 'entity' to be added.
     * @param xml <code>String</code> specifying the xml of the
     * 'entity' to be added.
     * @return the id of the new 'entity'.
     * @throws DBMSException with expected error codes.
     */
    public int addEntity(String entity, String xml) throws DBMSException{
        int newId = newId(this.DBfilename, this.conf);
        String newEntity = "<"+entity+" id=\""+newId+"\">\n"
                + xml
                + "</"+entity+">\n";
        this.DBfile.xAppend(this.getEntitiesRootXPath(), newEntity);
        return newId;
    }
    
    /**
     * Removes an 'entity' under the 'entitiesRoot'. The 'entity'
     * to be removed is specified by an attribute and the value of it.
     *
     * @param attribute the attribute of the 'entity' to be removed.
     * @param value the value of the attribute.
     * @throws DBMSException with expected error codes.
     */
    public void removeEntity(String attribute, String value) throws DBMSException{
        this.DBfile.xRemove(this.getEntitiesRootXPath()
        +"/*[@"+attribute+"='"+value+"']");
    }
    
    /**
     * Adds a 'new' tag and the value of it, as XML <code>String</code>,
     * into an 'entity' in the <code>DMSFile</code>.
     *
     * @param id
     *            the id of the 'entity' into which new data will be added.
     * @param tag
     *            the name of the tag to add, into the 'entity' with
     *            the specified id.
     * @param value
     *            the value of the 'new' tag. The value might be an
     *            XML <code>String</code> (XML sub-tree).
     * @return the number of modified nodes in the file.
     * @throws DMSException with expected error codes.
     * @throws DBMSException with expected error codes.
     */
    public long addIntoEntity(int id, String tag, String value) throws DMSException, DBMSException{
        if (this.hasIntoEntity(id, tag) == true)
            throw new DMSException("Tag '"+tag+"' already exist in 'entity' with id "+id+".");
        return this.addData(this.queryMe(id), "<"+tag+">"+value+"</"+tag+">");
    }
    
    /**
     * Removes a node/tag, which is inside an 'entity',
     * from the <code>DMSFile</code>. The whole sub-tree XML that is
     * under that tag is removed.
     *
     * @param id
     *            the id of the 'entity' from which we want to remove.
     * @param tag the name of the tag to be removed, which must exist
     * 	       inside the 'entity' with the specified id.
     * @return the number of modified nodes in the file.
     * @throws DMSException with expected error codes.
     * @throws DBMSException with expected error codes.
     */
    public long removeFromEntity(int id, String tag) throws DMSException, DBMSException{
        if (this.hasIntoEntity(id, tag) == false)
            throw new DMSException("No tag '"+tag+"' found in 'entity' with id "+id+".");
        return this.removeData(this.queryMe(id)+"/"+tag);
    }
    
    /**
     * Gets the value that a node/tag, which is inside an 'entity',
     * has in the <code>DMSFile</code>. The returned value is an
     * XML <code>String</code>, so it might be an XML tree
     * (includes nested nodes).
     *
     * @param id
     *            the id of the 'entity' we want.
     * @param tag the name of the tag to get the value of. The tag
     *         must exist inside the 'entity' with the specified id.
     * @return an XML <code>String</code> representing the value
     *         of the specified node/tag, or <code>null</code>
     *         if there is no such 'node'
     * @throws DMSException with expected error codes.
     * @throws DBMSException with expected error codes.
     */
    public String getFromEntity(int id, String tag) throws DMSException, DBMSException{
        return this.getData(this.queryMe(id)+"/"+tag);
    }
    
    /**
     * Sets the value of a node in the <code>DMSFile</code>.
     * The new value is specified as an XML <code>String</code>, so
     * it might be an XML tree (includes nested tags).
     *
     * @param id
     *            the id of the 'entity' we want.
     * @param tag
     *            the name of the tag to set the value of. The tag muse
     *            exist inside the 'entity' with the specified id.
     * @param value
     *            the 'new' value of the tag. The value might be an
     *            XML <code>String</code> (XML sub-tree).
     * @return the number of modified nodes in the file.
     * @throws DMSException with expected error codes.
     * @throws DBMSException with expected error codes.
     */
    public long setIntoEntity(int id, String tag, String value) throws DMSException, DBMSException{
        System.out.println("file"+ this.DBfilename);
        if (this.hasIntoEntity(id, tag) == false)
            throw new DMSException("No tag '"+tag+"' found inside 'entity' with id "+id+".");
        return this.setData(this.queryMe(id)+"/"+tag, value);
    }
    
    /**
     * Checks if there is a node/tag inside an 'entity'.
     *
     * @param id
     *            the id of the 'entity' into which we want to check.
     * @param tag
     *            the name of the tag, we want to check.
     * @return <code>true</code> if there is a tag with the specified
     *         name, inside the 'entity' with the the specified id,
     *         or <code>false</code> otherwise.
     * @throws DMSException with expected error codes.
     * @throws DBMSException with expected error codes.
     */
    public boolean hasIntoEntity(int id, String tag) throws DMSException, DBMSException{
        return (this.getFromEntity(id, tag) == null) ? false : true;
    }
    
    /**
     * Deletes this <code>DMSFile</code> from the system.
     * @throws DBMSException with expected error codes.
     */
    public void delete() throws DBMSException{
        this.DBfile.remove();
    }
    
    /**
     * Adds data, as XML <code>String</code>, into the
     * <code>DMSFile</code>.
     *
     * @param xPath
     *            XPath that selects where to add the data into the file.
     * @param xml
     *            the XML to add as <code>String</code>.
     * @return the number of modified nodes in the file.
     * @throws DBMSException with expected error codes.
     */
    public long addData(String xPath, String xml) throws DBMSException{
        return this.DBfile.xAppend(xPath, xml);
    }
    
    /**
     * Removes data from the <code>DMSFile</code>.
     *
     * @param xPath
     *            XPath that selects what to remove from the file.
     * @return the number of modified nodes in the file.
     * @throws DBMSException with expected error codes.
     */
    public long removeData(String xPath) throws DBMSException{
        return this.DBfile.xRemove(xPath);
    }
    
    /**
     * Gets the value that a node/tag has in the <code>DMSFile</code>.
     * The returned value is an XML <code>String</code>, so it
     * might be an XML tree (includes nested nodes).
     *
     * @param xPath
     *            XPath that selects which node's value to get from
     *            the file.
     * @return an XML <code>String</code> representing the value
     *         of the specified node/tag in the file, or <code>null</code>
     *         if there is no such 'node'.
     * @throws DMSException with expected error codes.
     * @throws DBMSException with expected error codes.
     */
    public String getData(String xPath) throws DMSException, DBMSException{
        if (this.exist(xPath)){
            String ret[] = this.DBfile.queryString(xPath + "/text()");
            if (ret.length == 0) return "";
            else return ret[0];
        }else
            return null;
    }
    
    /**
     * Sets the value of a node in the <code>DMSFile</code>.
     * The new value is specified as an XML <code>String</code>, so
     * it might be an XML tree (includes nested tags).
     * @param xml the XML to set as <code>String</code>.
     * @param xPath XPath that selects which node's value to get from
     *            the file.
     * @return the number of modified nodes in the file.
     * @throws DBMSException with expected error codes.
     */
    public long setData(String xPath, String xml) throws DBMSException{
        return this.DBfile.xUpdate(xPath, xml);
    }
    
    /**
     * Checks whether a query inside a <CODE>DMSFile</CODE> returns any
     * results.
     *
     * @param selectQuery
     *            The query to perform.
     * @return <code>true</code> if the query returns any results,
     *         <code>false</code> otherwise.
     * @throws DMSException
     *             with expected error codes.
     * @throws DBMSException with expected error codes.
     */
    public boolean exist(String selectQuery) throws DMSException, DBMSException {
        return this.DBfile.exist(selectQuery);
    }
    
    /**
     * Executes a query (either XPath or XQuery) against this
     * <code>DMSFile</code>. The result is an array (of
     * <code>String</code>) containing the results of the query.
     *
     * @return an array of <code>String (String[])</code> containing the
     *         results of the query.
     * @param query
     *            The XPath or XQuery query string to use.
     * @throws isl.dms.DMSException
     *             with expected error codes
     * @throws DBMSException with expected error codes.
     */
    /*****************************TZORTZAK MODIFICATION*************************/
    public String[] queryString(String query) throws DMSException, DBMSException {
        return this.DBfile.queryString(query);
    }
    
    /**
     * Returns all the ids of the 'entities' in the <code>DMSFile</code>.
     * @return an array of <code>String (String[])</code> containing
     *         the ids of the 'entities' in this <code>DMSFile</code>.
     * @throws DBMSException with expected error codes.
     */
    public String[] getIds() throws DMSException{
    	if (this.DBfile.exist(this.getEntitiesRootXPath()+"/*") == false)
    		return new String[0];
    	else
    		return this.DBfile.queryString(this.getEntitiesRootXPath()+"/*/@id/string()");
    }
    
    /**
     * Returns a new 'id' for the 'entities' described in this
     * <code>DMSFile</code>. The new 'id' is return for the
     * <b>default 'id'</b>, which is assumed to be the <code>String</code>
     * <i>id</i>.
     * @param conf a DMSConfig object
     * @param file the name of the file in which we want to get a new id.
     * @return an integer that is a new id.
     * @throws DBMSException with expected error codes.
     */
    public static int newId(String file, DMSConfig conf) throws DBMSException {
        String selectId = "/DMS/*[1]/*/@id";
        return newId(file, selectId, conf);
    }
    
    /**
     * Returns a new 'id' for the 'entities' described in this
     * <code>DMSFile</code>.
     * @param conf a DMSConfig object
     * @param file the name of the file in which we want to get a new id.
     * @param selectId xPath that selects the id, for which we want
     *            to get a new one.
     * @return an integer that is a new id.
     * @throws DBMSException with expected error codes.
     */
    public static int newId(String file, String selectId, DMSConfig conf) throws DBMSException {
        DBFile DMSFile = new DBFile(conf.DB, conf.COLLECTION, file, conf.DB_USERNAME, conf.DB_PASSWORD);
        DBFile[] maxId = DMSFile.query("max(" + selectId + ")");
        if (maxId.length == 0)
        	return 1;

        return (int) Double.parseDouble(maxId[0].getXMLAsString()) + 1;
    }
    
    private String getEntitiesRootXPath(){
        //The DMSFile has a root 'DMS' with one child.
        return "/DMS/*[1]";
    }
    
    protected String queryMe(int id){
        return this.getEntitiesRootXPath()+"/*[@id='" + id + "']";
    }
    
    /**
     * Sets the file that this <code>DMSFile</code> represents.
     * 
     * @param file The name of the file to set.
     * @param conf a DMSConfig object
     * @throws DBMSException with expected error codes.
     */
    protected void setDMSfile(String file, DMSConfig conf) throws DBMSException {
        this.DBfile = new DBFile(conf.DB, conf.COLLECTION, file, conf.DB_USERNAME, conf.DB_PASSWORD);
        this.DBfilename = this.DBfile.getName();
        this.conf = conf;
    }
}

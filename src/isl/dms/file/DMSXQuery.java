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

import isl.binaryFile.BinaryFile;
import isl.dbms.DBMSException;
import isl.dms.DMSConfig;
import isl.dms.DMSException;

/**
 * The
 * <code>XMLQuery</code> represents an XML query to the system. It provides
 * basic functionality for handling and managing queries. <br> <br> An XML query
 * is related to a user of the system, meaning that the user has created and
 * stored it. Also an XML query has a 'type', indicating what is the type of the
 * query and a 'category', indicating the category of the query. What 'type' and
 * 'category' really means may vary per application. An XML query is described
 * by some 'sections'. The <b>info</b>, the <b>targets</b>, the <b>inputs</b>,
 * the <b>outputs</b> and the <b>orderBy</b>.
 *
 * These 'sections' can be extended, depending on the needs of each application.
 * In that case the corresponding functionality must be provided by the
 * application. <br> <br> The <b>targets</b> describes the targets (either
 * collection or documents) of a query.<br> <br> The <b>info</b> describes
 * general info about a query.<br> <br> The <b>inputs</b> describes the input
 * fields of a query.<br> <br> The <b>outputs</b> describes the (output) fields
 * of the result of a query to be presented.<br> <br> The <b>orderBy</b>
 * describes the orderBy fields of a query.<br>
 */
public class DMSXQuery extends DMSFile {

    private String queryname;
    private int uID;

    /**
     * Constructs a new
     * <code>DMSQuery</code> instance associated with the query of the system,
     * which has the specified name and is related to the user with the
     * specified userId.
     *
     * @param conf a DMSConfig object
     * @param name the name of the query.
     * @param userId the id of the 'related to the query' user.
     * @throws EntryNotFoundException if there is not any query with the
     * specified name for the specified user.
     * @throws DMSException with expected error codes.
     */
    public DMSXQuery(String name, int userId, DMSConfig conf)
            throws EntryNotFoundException, DMSException {
        if (name == null || (!checkQuery(name, userId, conf) && !checkQueryIndepentUser(name, conf))) {
            throw new EntryNotFoundException("Query not found: " + name);
        }
        this.setDMSfile(conf.QUERIES_FILE, conf);
        this.queryname = name;
        this.uID = userId;
    }

    /**
     * Returns the name of a query for a user in the system.
     *
     * @return the name of the specified query for the specified user in the
     * system, or
     * <code>null</code> if there is not such query for that user.
     * @param conf a DMSConfig object
     * @param queryId the id of the query.
     * @param userId the id of the 'related to the query' user.
     * @throws DMSException with expected error codes.
     */
    public static String getNameOf(int queryId, int userId, DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.QUERIES_FILE, conf);
        String selectQuery = "/DMS/queries/query[@id='" + queryId + "' and @uid='" + userId + "']/info/name/text()";
        String[] ret = file.queryString(selectQuery);
        if (ret.length == 0) {
            return null;
        } else {
            return ret[0];
        }
    }

    /**
     * Returns the name of a query in the system.
     *
     * @return the name of the specified query independent user in the system,
     * or
     * <code>null</code> if there is not such query
     * @param conf a DMSConfig object
     * @param queryId the id of the query.
     * @throws DMSException with expected error codes.
     */
    public static String getNameOfNoUser(int queryId, DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.QUERIES_FILE, conf);
        String selectQuery = "/DMS/queries/query[@id='" + queryId + "']/info/name/text()";
        String[] ret = file.queryString(selectQuery);
        if (ret.length == 0) {
            return null;
        } else {
            return ret[0];
        }
    }

    /**
     * Adds a new query to the system for a user. An
     * <code>XMLQuery</code> is returned representing the new query. If a query
     * with the specified name allready exist, an exception is thrown indicating
     * the error.<br> <br> The new query has no data related to it. Thus the
     * appropriate 'set' methods should be called.
     *
     * @return an
     * <code>XMLQuery</code> representing the new query.
     * @param conf a DMSConfig object
     * @param name the name of the new query.
     * @param userId the id of the 'related to the query' user.
     * @param type the type of the the query.
     * @param category the category of the the query.
     * @throws EntryExistException if a query with the specified name already
     * exists for the specified user.
     * @throws DMSException with expected error codes.
     */
    public static DMSXQuery addQuery(String name, int userId, String type, String category, DMSConfig conf)
            throws EntryExistException, DMSException {
        String id = String.valueOf(newQueryId(conf));
        DMSFile file = new DMSFile(conf.QUERIES_FILE, conf);
        String newQuery =
                "<query id=\"" + id + "\" uid=\"" + userId + "\" type=\"" + type + "\">\n"
                + "<info>\n"
                + "<name>" + name + "</name>\n"
                + "<category>" + category + "</category>\n"
                + "<source/>\n"
                + "<external_source/>\n"
                + "<operator/>\n"
                + "</info>\n"
                + "<targets/>\n"
                + "<inputs/>\n"
                + "<outputs/>\n"
                + "<orderBy/>\n"
                + "</query>\n";
        if (checkQuery(name, userId, conf)) {
            throw new EntryExistException("Query already exists: " + name);
        }
        file.addEntity(newQuery);
        return new DMSXQuery(name, userId, conf);
    }

    /**
     * Removes this query from the system.
     *
     * @throws DMSException with expected error codes.
     */
    public void remove() throws DMSException {
        String selectQuery = this.queryMe();
        this.removeData(selectQuery);
    }

    /**
     * Checks the existance of a query for a particular user.
     *
     * @return
     * <code>true</code> if the query exists,
     * <code>false</code> otherwise.
     * @param conf a DMSConfig object
     * @param name the name of the query to find.
     * @param userId the id of the 'related to the query' user.
     * @throws DMSException with expected error codes.
     */
    public static boolean checkQuery(String name, int userId, DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.QUERIES_FILE, conf);
        return file.exist("/DMS/queries/query[@uid='" + userId + "' and info/name='" + name + "']");
    }
    
    

    /**
     * Checks the existance of a query indepedent user.
     *
     * @return
     * <code>true</code> if the query exists,
     * <code>false</code> otherwise.
     * @param conf a DMSConfig object
     * @param name the name of the query to find.
     * @throws DMSException with expected error codes.
     */
    public static boolean checkQueryIndepentUser(String name, DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.QUERIES_FILE, conf);
        return file.exist("/DMS/queries/query[info/name='" + name + "']");
    }

    /**
     *
     * @param conf a DMSConfig object
     * @return
     */
    private static int newQueryId(DMSConfig conf) {
        return newId(conf.QUERIES_FILE, conf);
    }

    /**
     *
     * @return
     */
    private String queryMe() {
        return "/DMS/queries/query[info/name='" + this.queryname + "']";
    }

    /**
     *
     * @param conf a DMSConfig object
     * @return
     */
    private int newInputId(DMSConfig conf) {
        return DMSFile.newId(conf.QUERIES_FILE, this.queryMe() + "/inputs/input/@id", conf);
    }

    /**
     * Returns the 'system' id of the query.
     *
     * @return the system id of the query.
     * @throws DMSException with expected error codes.
     */
    public int getId() throws DMSException {
        String selectQuery = this.queryMe() + "/@id/string()";
        return Integer.valueOf(this.queryString(selectQuery)[0]);
    }
    
        /**
     * Returns the 'system' id of the query independent.
     *
     * @return the system id of the query.
     * @throws DMSException with expected error codes.
     */
    public int getIdindependentUser() throws DMSException {
        String selectQuery = "/DMS/queries/query[info/name='" + this.queryname + "']" + "/@id/string()";
        return Integer.valueOf(this.queryString(selectQuery)[0]);
    }


    /**
     * Returns the type of the query.
     *
     * @return the type of the query.
     * @throws DMSException with expected error codes.
     */
    public int getType() throws DMSException {
        String selectQuery = this.queryMe() + "/@type/string()";
        return Integer.valueOf(this.queryString(selectQuery)[0]);
    }

    /**
     * Returns the names of the info fields of the query.
     *
     * @return an array of
     * <code>String</code> containing the names of the info fields of the query.
     * @throws DMSException with expected error codes.
     */
    public String[] getInfos() throws DMSException {
        String selectQuery = this.queryMe() + "/info/*/name()";
        return this.queryString(selectQuery);
    }

    /**
     * Adds a general info for the query.
     *
     * @param info the name of the info to be added.
     * @param value the (text) value of the info.
     * @throws DMSException with expected error codes.
     */
    public void addInfo(String info, String value) throws DMSException {
        String selectQuery = this.queryMe() + "/info";
        String newData = "<" + info + ">" + value + "</" + info + ">\n";
        if (this.getInfo(info) != null) {
            throw new DMSException("Query already has info: " + info);
        }
        this.addData(selectQuery, newData);
    }

    /**
     * Removes a info from a query.
     *
     * @param info the name of the info to be removed.
     * @throws DMSException with expected error codes.
     */
    public void removeInfo(String info) throws DMSException {
        if (this.getInfo(info) == null) {
            throw new DMSException("Query " + this.queryname + " does not have the info " + info);
        }
        String selectQuery = this.queryMe() + "/info/" + info;
        this.removeData(selectQuery);
    }

    /**
     * Gets the value of an info for the query. If there are more than one
     * 'info' with the specified name, the value of the first is return.
     *
     * @param info the name of the info.
     * @return the value of the specified info or
     * <code>null</code> if there is no such info.
     * @throws DMSException with expected error codes.
     */
    public String getInfo(String info) throws DMSException {
        String query = this.queryMe() + "/info/" + info;
        if (this.exist(query)) {
            String ret[] = this.queryString(query + "/text()");
            if (ret.length == 0) {
                return "";
            }
            if (info.equals("source")) {
                ret[0] = ret[0].replace("&lt;", "<");
                ret[0] = ret[0].replace("&gt;", ">");
            }
//            //SAM's addition
//            if (info.equals("external_source")){
//                String col = ret[0].substring(0,ret[0].lastIndexOf("/"));
//                String file = ret[0].substring(ret[0].lastIndexOf("/")+1);
//                ret[0]= new BinaryFile(col, file, conf).toString();
//            }
            //
            return ret[0];
        } else {
            return null;
        }
    }

    /**
     * Sets the value of an 'info' for the query.
     *
     * @param info the name of the info.
     * @param value the (new) value of the info.
     * @throws DMSException with expected error codes.
     */
    public void setInfo(String info, String value) throws DMSException {
        String selectQuery = this.queryMe() + "/info/" + info;
        if (this.getInfo(info) == null) {
            throw new DMSException("Query does not have info: " + info);
        }
        this.setData(selectQuery, value);
    }

    /**
     * Returns the names (path representation) of the targets of the query.
     *
     * @return an array of
     * <code>String</code> containing the names (XPath representation) of the
     * targets of the query.
     * @throws DMSException with expected error codes.
     */
    public String[] getTargets() throws DMSException {
        String selectQuery = this.queryMe() + "/targets/path/text()";
        return this.queryString(selectQuery);
    }

    /**
     * Adds a target to the query.
     *
     * @param path the path representing the target to be added.
     * @throws DMSException with expected error codes.
     */
    public void addTarget(String path) throws DMSException {
        if (this.hasInput(path)) {
            throw new DMSException("Query already has target " + path);
        }
        String selectQuery = this.queryMe() + "/targets";
        String newData = "<path>" + path + "</path>\n";
        this.addData(selectQuery, newData);
    }

    /**
     * Removes a target from the query.
     *
     * @param path the path representing the target to be removed.
     * @throws DMSException with expected error codes.
     */
    public void removeTarget(String path) throws DMSException {
        if (this.hasInput(path) == false) {
            throw new DMSException("Query does not have target " + path);
        }
        String selectQuery = this.queryMe() + "/targets[path='" + path + "']";
        this.removeData(selectQuery);
    }

    /**
     * Check if the query has a target.
     *
     * @param path the path representing the target, we want to check.
     * @return
     * <code>true</code> if the query has the specified target or
     * <code>false</code> otherwise.
     * @throws DMSException with expected error codes.
     */
    public boolean hasTarget(String path) throws DMSException {
        String selectQuery = this.queryMe() + "/targets[path='" + path + "']";
        return this.exist(selectQuery);
    }

    /**
     * Returns the ids of the input fields of the query.
     *
     * @return an array of
     * <code>int (int[])</code> containing the ids of the input fields of the
     * query.
     * @throws DMSException with expected error codes.
     */
    public int[] getInputs() throws DMSException {
        String selectQuery = this.queryMe() + "/inputs/input";

        if (this.exist(selectQuery) == false) {
            return new int[0];
        }

        String[] ids = this.queryString(selectQuery + "/@id/string()");
        int[] ret = new int[ids.length];
        for (int i = 0; i < ids.length; i++) {
            ret[i] = Integer.parseInt(ids[i]);
        }
        return ret;
    }

    /**
     * Adds an input field to the query. The new input field is 'empty', thus
     * the appropriate 'setIntoInput' methods should be called.<br> <br> Each
     * input field has a unique id, which is automatically assigned to it. This
     * id is returned upon succesfull completion.
     *
     * @return the id of the new input.
     * @throws DMSException with expected error codes.
     */
    public int addInput(DMSConfig conf) throws DMSException {
        String selectQuery = this.queryMe() + "/inputs";
        int newId = this.newInputId(conf);
        String newData = "<input id=\"" + newId + "\" parameter=\"no\"></input>\n";
        this.addData(selectQuery, newData);
        return newId;
    }

    /**
     * Adds an input field to the query. The new input field is 'empty' (except
     * the <i>path</i> 'tag'), thus the appropriate 'setIntoInput' methods
     * should be called.<br> <br> Each input field has a unique id, which is
     * automatically assigned to it. This id is returned upon succesfull
     * completion.
     *
     * @param xPath the value of the 'path' tag of the input field to be added.
     * @return the id of the new input.
     * @throws DMSException with expected error codes.
     */
    public int addInput(String xPath, DMSConfig conf) throws DMSException {
        String selectQuery = this.queryMe() + "/inputs";
        int newId = this.newInputId(conf);
        String newData = "<input id=\"" + newId + "\" parameter=\"no\">\n"
                + "<path>" + xPath + "</path>\n"
                + "</input>\n";
        this.addData(selectQuery, newData);
        return newId;
    }

    /**
     * Removes an input field from the query. If there are more than one 'input
     * fields', that the value of the 'path' tag equals the specified xPath, the
     * first one is removed.
     *
     * @param xPath the value of the 'path' tag of the input to be removed.
     * @throws DMSException with expected error codes.
     */
    public void removeInput(String xPath) throws DMSException {
        if (this.hasInput(xPath) == false) {
            throw new DMSException("Query does not have input " + xPath);
        }
        String selectQuery = this.queryMe() + "/inputs/input[path='" + xPath + "']";
        this.removeData(selectQuery);
    }

    /**
     * Removes an input field from the query by id.
     *
     * @param id the id of the input to be removed.
     * @throws DMSException with expected error codes.
     */
    public void removeInput(int id) throws DMSException {
        if (this.hasInput(id) == false) {
            throw new DMSException("Query does not have input " + id);
        }
        String selectQuery = this.queryMe() + "/inputs/input[@id='" + id + "']";
        this.removeData(selectQuery);
    }

    /**
     * Check if the query has an input field, by specifying the value of its
     * 'path' tag.
     *
     * @param xPath the value of the 'path' tag of the input field, we want to
     * check.
     * @return
     * <code>true</code> if the query has the specified input field or
     * <code>false</code> otherwise.
     * @throws DMSException with expected error codes.
     */
    public boolean hasInput(String xPath) throws DMSException {
        String selectQuery = this.queryMe() + "/inputs/input[path='" + xPath + "']";
        return this.exist(selectQuery);
    }

    /**
     * Check if the query has an input field by id.
     *
     * @param id the id of the input field, we want to check.
     * @return
     * <code>true</code> if the query has the specified input field or
     * <code>false</code> otherwise.
     * @throws DMSException with expected error codes.
     */
    public boolean hasInput(int id) throws DMSException {
        String selectQuery = this.queryMe() + "/inputs/input[@id='" + id + "']";
        return this.exist(selectQuery);
    }

    /**
     * Sets an input field of the query as 'parameter'.
     *
     * @param id the id of the input field, we want to set as parameter.
     * @param flag a
     * <code>boolean</code> indicating whether to set or unset the input field
     * as parameter.
     * @throws DMSException with expected error codes.
     * @throws DBMSException with expected error codes.
     */
    public void setParameter(int id, boolean flag) throws DMSException, DBMSException {
        if (this.hasInput(id) == false) {
            throw new DMSException("Query does not have input " + id);
        }
        String selectQuery = this.queryMe() + "/inputs/input[@id='" + id + "']/@parameter";
        this.DBfile.xUpdate(selectQuery, (flag) ? "yes" : "no");
    }

    /**
     * Returns the ids of the input fields, which are parameters of the query,.
     *
     * @return an array of
     * <code>int (int[])</code> containing the ids of the 'parameter' input
     * fields of the query.
     * @throws DMSException with expected error codes.
     */
    public int[] getParameters() throws DMSException {
        String selectQuery = this.queryMe() + "/inputs/input[@parameter='yes']";

        if (this.exist(selectQuery) == false) {
            return new int[0];
        }

        String[] ids = this.queryString(selectQuery + "/@id/string()");
        int[] ret = new int[ids.length];
        for (int i = 0; i < ids.length; i++) {
            ret[i] = Integer.parseInt(ids[i]);
        }
        return ret;
    }

    /**
     * Check if the query has a 'parameter' input field by id.
     *
     * @param id the id of the input field, we want to check.
     * @return
     * <code>true</code> if the query has the specified input field or
     * <code>false</code> otherwise.
     * @throws DMSException with expected error codes.
     */
    public boolean isParameter(int id) throws DMSException {
        if (this.hasInput(id) == false) {
            throw new DMSException("Query does not have input " + id);
        }
        String selectQuery = this.queryMe() + "/inputs/input[@id='" + id + "' and @parameter='yes']";
        return this.exist(selectQuery);
    }

    /**
     * Adds a new 'tag' to an input field of the query.
     *
     * @param id the id of the input field where we want to add into.
     * @param name the name of the new 'tag' to be added.
     * @param value the value of the 'tag' to be added.
     * @throws DMSException with expected error codes.
     */
    public void addIntoInput(int id, String name, String value) throws DMSException {
        if (this.hasIntoInput(id, name)) {
            throw new DMSException("Query input already has tag " + name);
        }
        String selectQuery = this.queryMe() + "/inputs/input[@id='" + id + "']";
        String newData = "<" + name + ">" + value + "</" + name + ">\n";
        this.addData(selectQuery, newData);
    }

    /**
     * Removes a 'tag' from an input field of the query.
     *
     * @param id the id of the input field where we want to remove from.
     * @param name the name of the 'tag' to be removed.
     * @throws DMSException with expected error codes.
     */
    public void removeFromInput(int id, String name) throws DMSException {
        if (this.hasIntoInput(id, name) == false) {
            throw new DMSException("Query input does not have tag " + name);
        }
        String selectQuery = this.queryMe() + "/inputs/input[@id='" + id + "']/" + name;
        this.removeData(selectQuery);
    }

    /**
     * Gets the value of a 'tag' of an input field of the query.
     *
     * @param id the id of the input field.
     * @param name the name of the 'tag' of the input field, of which we want
     * the value.
     * @return the value of the specified 'tag' or
     * <code>null</code> if there is no such 'tag'.
     * @throws DMSException with expected error codes.
     * @throws DBMSException with expected error codes.
     */
    public String getFromInput(int id, String name) throws DMSException, DBMSException {
        String query = this.queryMe() + "/inputs/input[@id='" + id + "']/" + name;
        if (this.DBfile.exist(query)) {
            String[] ret = this.queryString(query + "/text()");
            if (ret.length == 0) {
                return "";
            } else {
                return ret[0];
            }
        } else {
            return null;
        }
    }

    /**
     * Gets the 'tags' of an input field of the query.
     *
     * @param id the id of the input field.
     * @return an array of
     * <code>String (String[])</code> containing the names of the 'tags' of the
     * input.
     * @throws DMSException with expected error codes.
     */
    public String[] getFromInput(int id) throws DMSException {
        String query = this.queryMe() + "/inputs/input[@id='" + id + "']/*/name()";
        return this.queryString(query);
    }

    /**
     * Gets the values of a 'tag' of all input fields of the query.
     *
     * @param name the name of the 'tag', of which we want the value.
     * @return an array of
     * <code>String (String[])</code> containing the values of all input fields.
     * @throws DMSException with expected error codes.
     */
    public String[] getFromInputs(String name) throws DMSException {
        String query = this.queryMe() + "/inputs/input/" + name + "/text()";
        return this.queryString(query);
    }

    /**
     * Sets the value of a 'tag' of an input field of the query.
     *
     * @param id the id of the input field.
     * @param name the name of the 'tag' of the input field, we want to set.
     * @param value the (new) value of the input field.
     * @throws DMSException with expected error codes.
     */
    public void setIntoInput(int id, String name, String value) throws DMSException {
        String selectQuery = this.queryMe() + "/inputs/input[@id='" + id + "']/" + name;
        if (this.hasIntoInput(id, name) == false) {
            throw new DMSException("Query input does not have tag: " + name);
        }
        this.setData(selectQuery, value);
    }

    /**
     * Check if the input field of a query has a 'tag'.
     *
     * @param id the id of the input field.
     * @param name the name of the 'tag' of the input field, we want to check.
     * @return
     * <code>true</code> if the query has the specified input or
     * <code>false</code> otherwise.
     * @throws DMSException with expected error codes.
     */
    public boolean hasIntoInput(int id, String name) throws DMSException {
        String selectQuery = this.queryMe() + "/inputs/input[@id='" + id + "']/" + name;
        return this.exist(selectQuery);
    }

    /**
     * Returns the names (XPath representation) of the output fields of the
     * query.
     *
     * @return an array of
     * <code>String</code> containing the names (XPath representation) of the
     * output fields of the query.
     * @throws DMSException with expected error codes.
     */
    public String[] getOutputs() throws DMSException {
        String selectQuery = this.queryMe() + "/outputs/path/text()";
        return this.queryString(selectQuery);
    }

    /**
     * Adds an orderBy field for the query.
     *
     * @param xPath the xPath representing the output field to be added.
     * @throws DMSException with expected error codes.
     */
    public void addOutput(String xPath) throws DMSException {
        if (this.hasOutput(xPath)) {
            throw new DMSException("Query already has the output " + xPath);
        }
        String selectQuery = this.queryMe() + "/outputs";
        String newData = "<path>" + xPath + "</path>\n";
        this.addData(selectQuery, newData);
    }

    /**
     * Removes an output field from the query.
     *
     * @param xPath the xPath representing the output to be removed.
     * @throws DMSException with expected error codes.
     */
    public void removeOutput(String xPath) throws DMSException {
        if (this.hasOutput(xPath) == false) {
            throw new DMSException("Query does not have output " + xPath);
        }
        String selectQuery = this.queryMe() + "/outputs[path='" + xPath + "']";
        this.removeData(selectQuery);
    }

    /**
     * Checks if the query has an output.
     *
     * @param xPath the xPath representing the output, we want to check.
     * @return
     * <code>true</code> if the query has the specified output or
     * <code>false</code> otherwise.
     * @throws DMSException with expected error codes.
     */
    public boolean hasOutput(String xPath) throws DMSException {
        String selectQuery = this.queryMe() + "/outputs[path='" + xPath + "']";
        return this.exist(selectQuery);
    }

    /**
     * Returns the names (XPath representation) of the orderBy fields of the
     * query.
     *
     * @return an array of
     * <code>String</code> containing the names (XPath representation) of the
     * orderBy fields of the query.
     * @throws DMSException with expected error codes.
     */
    public String[] getOrderBy() throws DMSException {
        String selectQuery = this.queryMe() + "/orderBy/path/text()";
        return this.queryString(selectQuery);
    }

    /**
     * Adds an orderBy field for the query.
     *
     * @param xPath the xPath representing the orderBy field to be added.
     * @throws DMSException with expected error codes.
     */
    public void addOrderBy(String xPath) throws DMSException {
        if (this.hasOrderBy(xPath)) {
            throw new DMSException("Query already has the orderBy " + xPath);
        }
        String selectQuery = this.queryMe() + "/orderBy";
        String newData = "<path>" + xPath + "</path>\n";
        this.addData(selectQuery, newData);
    }

    /**
     * Removes an orderBy field from the query.
     *
     * @param xPath the xPath representing the orderBy to be removed.
     * @throws DMSException with expected error codes.
     */
    public void removeOrderBy(String xPath) throws DMSException {
        if (this.hasOrderBy(xPath) == false) {
            throw new DMSException("Query does not have orderBy " + xPath);
        }
        String selectQuery = this.queryMe() + "/orderBy[path='" + xPath + "']";
        this.removeData(selectQuery);
    }

    /**
     * Checks if the query has an orderBy.
     *
     * @param xPath the xPath representing the orderBy, we want to check.
     * @return
     * <code>true</code> if the query has the specified orderBy or
     * <code>false</code> otherwise.
     * @throws DMSException with expected error codes.
     */
    public boolean hasOrderBy(String xPath) throws DMSException {
        String selectQuery = this.queryMe() + "/orderBy[path='" + xPath + "']";
        return this.exist(selectQuery);
    }
}

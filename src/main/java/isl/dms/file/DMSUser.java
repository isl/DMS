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

import java.util.Hashtable;

import isl.dms.DMSConfig;
import isl.dms.DMSException;

/**
 * The
 * <code>DMSUser</code> represents a user of the system. It provides basic
 * functionality for handling and managing a user. <br> <br> A user of the
 * system is described by three main 'sections'. The <b>info</b>, the
 * <b>groups</b> and the <b>actions</b>. These 'sections' can be extended,
 * depending on the needs of each application. In that case the corresponding
 * functionality must be provided by the application. <br> <br> The <b>info</b>
 * describes the general information a user might have (such as firstname,
 * lastname, address). Info can be added by the 'addInfo' method. In general the
 * info 'section' is managed by the 'xxxInfo' methods. <br> <br> The
 * <b>groups</b> describes the groups a user might belong to. A user can be
 * added to a new group by the 'addToGroup' method. In general the groups
 * 'section' is managed by the appropriate methods. <br> <br> The <b>actions</b>
 * describes the actions a user might have. Each action has a <b>level</b>
 * attribute, which describes more that action (HIGH, LOW, ADMIN, etc). The
 * <b>level</b> can take any values depending on the application. The action
 * 'section' is managed by the appropriate 'xxxAction' methods.
 */
public class DMSUser extends DMSFile {

    private String username;

    /**
     * Constructs a new
     * <code>DMSUser</code>. The new
     * <code>DMSUser</code> represents the user with the specified username.
     *
     * @param conf a DMSConfig object.
     * @param username the username of the user.
     * @throws EntryNotFoundException if there is not any user with the
     * specified username.
     * @throws DMSException with expected error codes.
     */
    public DMSUser(String username, DMSConfig conf) throws EntryNotFoundException, DMSException {
        if (checkUser(username, conf) == false) {
            throw new EntryNotFoundException("User not found: " + username);
        }
        this.setDMSfile(conf.USERS_FILE, conf);
        this.username = username;
    }

    /**
     * Constructs a new
     * <code>DMSUser</code>. The new
     * <code>DMSUser</code> represents the user with the specified id.
     *
     * @param id the id of the user.
     * @param conf a DMSConfig object.
     * @throws EntryNotFoundException if there is not any user with the
     * specified id.
     * @throws DMSException with expected error codes.
     */
    public DMSUser(int id, DMSConfig conf) throws EntryNotFoundException, DMSException {
        String username = getUsernameOf(id, conf);
        if (username == null) {
            throw new EntryNotFoundException("User not found for id: " + id);
        }
        this.setDMSfile(conf.USERS_FILE, conf);
        this.username = username;
    }

    /**
     * Returns the username of the user having a specified id.
     *
     * @return the username of the user with the specified id, or
     * <code>null</code> if there is no user with this id.
     * @param id the id of the user.
     * @param conf a DMSConfig object.
     * @throws DMSException with expected error codes.
     */
    public static String getUsernameOf(int id, DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.USERS_FILE, conf);
        String selectQuery = "/DMS/users/*[@id='" + id + "']";

        if (file.exist(selectQuery) == false) {
            return null;
        }

        String[] res = file.queryString(selectQuery + "/@username/string()");
        if (res.length == 0) {
            return null;
        } else {
            return res[0];
        }
    }

    /**
     * Returns the id of the user having a specified username.
     *
     * @return the id of the user with the specified username, or
     * <code>null</code> if there is no user with this username.
     * @param username the username of the user.
     * @param conf a DMSConfig object.
     * @throws DMSException with expected error codes.
     */
    public static String getIdOf(String username, DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.USERS_FILE, conf);

        String selectQuery = "/DMS/users/*[@username='" + username + "']";

        if (file.exist(selectQuery) == false) {
            return null;
        }

        String[] res = file.queryString(selectQuery + "/@id/string()");
        if (res.length == 0) {
            return null;
        } else {
            return res[0];
        }
    }

    /**
     * Returns a mapping of the ids of the users of the system to their
     * usernames.
     *
     * @return a
     * <code>Hashtable</code> containing the ids of the users of the system
     * mapped to their usernames.
     * @param conf a DMSConfig object
     * @throws DMSException with expected error codes.
     */
    public static Hashtable getUserToIdMapping(DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.USERS_FILE, conf);

        String[] ids = file.getIds();
        Hashtable ret = new Hashtable();

        for (int i = 0; i < ids.length; i++) {
            ret.put(ids[i], file.queryString("/DMS/users/user[@id='" + ids[i] + "']/@username/string()")[0]);
        }

        return ret;
    }

    /**
     * Returns the users of the system.
     *
     * @return an array of
     * <code>String</code> containing the usernames of the users of the system.
     * @param conf a DMSConfig object.
     * @throws DMSException with expected error codes.
     */
    public static String[] getUsers(DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.USERS_FILE, conf);
        String selectQuery = "/DMS/users/*";

        if (file.exist(selectQuery) == false) {
            return new String[0];
        } else {
            return file.queryString(selectQuery + "/@username/string()");
        }
    }

    /**
     * Returns the users of the system that belong to a particular group.
     *
     * @return an array of
     * <code>String</code> containing the usernames of the users of the system
     * in the specified group.
     * @param groupname the name of the group.
     * @param conf a DMSConfig object.
     * @throws DMSException with expected error codes.
     */
    public static String[] getUsersInGroup(String groupname, DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.USERS_FILE, conf);
        String selectQuery = "/DMS/users/user/groups/group[text()='" + groupname + "']"
                + "/../..";

        if (file.exist(selectQuery) == false) {
            return new String[0];
        } else {
            return file.queryString(selectQuery + "/@username/string()");
        }
    }

    /**
     * Returns the users of the system that have particular action.
     *
     * @return an array of
     * <code>String</code> containing the usernames of the users of the system
     * with the specified action.
     * @param Action the name of the action.
     * @param conf a DMSConfig object.
     * @throws DMSException with expected error codes.
     */
    public static String[] getUsersByAction(String Action, DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.USERS_FILE, conf);
        String selectQuery = "/DMS/users/user/actions/" + Action
                + "/../..";

        if (file.exist(selectQuery) == false) {
            return new String[0];
        } else {
            return file.queryString(selectQuery + "/@username/string()");
        }
    }

    /**
     * Returns the users of the system that have a particular info.
     *
     * @return an array of
     * <code>String</code> containing the usernames of the users of the system
     * having the specified info.
     * @param conf a DMSConfig object
     * @param info the name of the info.
     * @param value the value of the info.
     * @throws DMSException with expected error codes.
     */
    public static String[] getUsersByInfo(String info, String value, DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.USERS_FILE, conf);
        String selectQuery = "/DMS/users/user/info/" + info + "[text()='" + value + "']"
                + "/../..";

        if (file.exist(selectQuery) == false) {
            return new String[0];
        } else {
            return file.queryString(selectQuery + "/@username/string()");
        }
    }

    /**
     * Adds a new user to the system. A
     * <code>DMSUser</code> is returned representing the new user. If a user
     * with the specified username allready exist, an exception is thrown
     * indicating the error.<br> <br> The new user has no data related to her.
     * Thus the appropriate 'set' methods should be called.
     *
     * @return a
     * <code>DMSUser</code> representing the new user.
     * @param conf a DMSConfig object
     * @param username the username of the new user.
     * @param password the password of the new user.
     * @throws EntryExistException if a user with the specified name already
     * exists.
     * @throws DMSException with expected error codes.
     */
    public static DMSUser addUser(String username, String password, DMSConfig conf)
            throws EntryExistException, DMSException {
        int id = newUserId(conf);
        String newData = "<user active=\"yes\" username=\"" + username + "\" password=\""
                + password + "\" id=\"" + id + "\">" + "<info>"
                + "<firstname/>" + "<lastname/>" + "<address/>" + "<email/>"
                + "<tel/>" + "<mobile/>" + "<role/>" + "<comment/>" + "<accepted/>" + "</info>"
                + "<groups/>" + "<actions/>" + "</user>";
        DMSFile file = new DMSFile(conf.USERS_FILE, conf);
        if (checkUser(username, conf)) {
            throw new EntryExistException("User already exists: " + username);
        }
        file.addEntity(newData);
        return new DMSUser(username, conf);
    }

    /**
     * Removes this user from the system.
     *
     * @throws DMSException with expected error codes.
     */
    public void remove() throws DMSException {
        this.removeEntity("username", this.username);
    }

    /**
     * Checks the existance of a user.
     *
     * @return
     * <code>true</code> if the user exists,
     * <code>false</code> otherwise.
     * @param conf a DMSConfig object
     * @param username the username of the user to find.
     * @throws DMSException with expected error codes.
     */
    public static boolean checkUser(String username, DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.USERS_FILE, conf);
        return file.exist("/DMS/users/user[@username='" + username + "']");
    }

    /**
     * Checks the existence of a user with a given username and password.
     *
     * @return
     * <code>true</code> if the user exists,
     * <code>false</code> otherwise.
     * @param conf a DMSConfig object
     * @param username the username of the user to find.
     * @param password the password of the user to find
     * @throws DMSException with expected error codes.
     */
    public static boolean checkUser(String username, String password, DMSConfig conf) throws DMSException {
        DMSFile file = new DMSFile(conf.USERS_FILE, conf);
        return file.exist("/DMS/users/user[@username='" + username
                + "' and @password='" + password + "']");
    }

    /**
     *
     * @param conf a DMSConfig object
     * @return
     */
    private static int newUserId(DMSConfig conf) {
        return DMSFile.newId(conf.USERS_FILE, "/DMS/users/user/@id", conf);
    }

    private String queryMe() {
        return "/DMS/users/user[@username='" + this.username + "']";
    }

    /**
     * Sets the value of the username for the user.
     *
     * @param username the (new) username of the user.
     * @throws EntryExistException if a user with the specified username already
     * exists.
     * @throws DMSException with expected error codes.
     */
    public void setUsername(String username) throws EntryExistException, DMSException {
        if (this.username.equals(username)) {
            return;
        }
        if (checkUser(username, this.conf)) {
            throw new EntryExistException("User already exist: " + username);
        }
        String selectQuery = this.queryMe() + "/@username";
        this.setData(selectQuery, username);
        this.username = username;
    }

    /**
     * Returns the password of the user.
     *
     * @return the password of the user.
     * @throws DMSException with expected error codes.
     */
    public String getPassword() throws DMSException {
        String selectQuery = this.queryMe() + "/@password/string()";
        return this.queryString(selectQuery)[0];
    }

    /**
     * Sets the value of the password for the user.
     *
     * @param password the (new) password of the user.
     * @throws DMSException with expected error codes.
     */
    public void setPassword(String password) throws DMSException {
        String selectQuery = this.queryMe() + "/@password";
        this.setData(selectQuery, password);
    }

    /**
     * Returns the 'system' id of the user.
     *
     * @return the system id of the user.
     * @throws DMSException with expected error codes.
     */
    public int getId() throws DMSException {
        String selectQuery = this.queryMe() + "/@id/string()";
        return Integer.valueOf(this.queryString(selectQuery)[0]);
    }

    /**
     * Returns the 'info' fields of the user.
     *
     * @return an array of
     * <code>String</code> containing the names of the 'info' fields of the
     * user.
     * @throws DMSException with expected error codes.
     */
    public String[] getInfos() throws DMSException {
        String selectQuery = this.queryMe() + "/info/*/name()";
        return this.queryString(selectQuery);
    }

    /**
     * Adds an 'info' for the user.
     *
     * @param info the name of the info to be added.
     * @param value the (text) value of the info.
     * @throws DMSException with expected error codes.
     */
    public void addInfo(String info, String value) throws DMSException {
        String selectQuery = this.queryMe() + "/info";
        String newData = "<" + info + ">" + value + "</" + info + ">";
        if (this.getInfo(info) != null) {
            throw new DMSException("User " + this.username + " already has info: " + info);
        }
        this.addData(selectQuery, newData);
    }

    /**
     * Removes a info from a user.
     *
     * @param info the name of the info to be removed.
     * @throws DMSException with expected error codes.
     */
    public void removeInfo(String info) throws DMSException {
        if (this.getInfo(info) == null) {
            throw new DMSException("User " + this.username + " does not have the info " + info);
        }
        String selectQuery = this.queryMe() + "/info/" + info;
        this.removeData(selectQuery);
    }

    /**
     * Gets the value of an 'info' for the user. If there are more than one
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
            } else {
                return ret[0];
            }
        } else {
            return null;
        }
    }

    /**
     * Sets the value of an 'info' for the user.
     *
     * @param info the name of the info.
     * @param value the (new) value of the info.
     * @throws DMSException with expected error codes.
     */
    public void setInfo(String info, String value) throws DMSException {
        String selectQuery = this.queryMe() + "/info/" + info;
        if (this.getInfo(info) == null) {
            throw new DMSException("User " + this.username + " does not have info: " + info);
        }
        this.setData(selectQuery, value);
    }

    /**
     * Returns the groups a user belongs to.
     *
     * @return an array of
     * <code>String</code> containing the names of the groups the user belongs
     * to.
     * @throws DMSException with expected error codes.
     */
    public String[] getGroups() throws DMSException {
        String selectQuery = "count(" + this.queryMe() + "/groups/group)";
        String count = this.queryString(selectQuery)[0];
        if (count.equals("0")) {
            return new String[0];
        }
        selectQuery = this.queryMe() + "/groups/group/text()";
        return this.queryString(selectQuery);
    }

    /**
     * Adds the user to a group.
     *
     * @param group the name of the group, where the user will be added to.
     * @throws EntryExistException if the user already exists in the group.
     * @throws DMSException with expected error codes.
     */
    public void addToGroup(String group) throws EntryExistException, DMSException {
        // check if group exist...
        if (this.belongsToGroup(group)) {
            throw new EntryExistException("User " + this.username + " already member of group " + group);
        }
        String selectQuery = this.queryMe() + "/groups";
        String newData = "<group>" + group + "</group>\n";
        this.addData(selectQuery, newData);
    }

    /**
     * Removes the user from a group.
     *
     * @param group the name of the group, where the user will be removed from.
     * @throws EntryNotFoundException if the user is not a member of the
     * specified group.
     * @throws DMSException with expected error codes.
     */
    public void removeFromGroup(String group) throws EntryNotFoundException, DMSException {
        if (this.belongsToGroup(group) == false) {
            throw new EntryNotFoundException("User " + this.username + " not member of group " + group);
        }
        String selectQuery = this.queryMe() + "/groups/group[text()='" + group + "']";
        this.removeData(selectQuery);
    }

    /**
     * Check if the user belongs to a group.
     *
     * @param group the name of the group, we want to check if the user belongs
     * to.
     * @return
     * <code>true</code> if the user belongs to the specified group or
     * <code>false</code> otherwise.
     * @throws DMSException with expected error codes.
     */
    public boolean belongsToGroup(String group) throws DMSException {
        String selectQuery = this.queryMe() + "/groups/group[text()='" + group + "']";
        return this.exist(selectQuery);
    }

    /**
     * Returns the actions of a user.
     *
     * @return an array of
     * <code>String</code> containing the names of the actions of the user.
     * @throws DMSException with expected error codes.
     */
    public String[] getActions() throws DMSException {
        String selectQuery = this.queryMe() + "/actions/*/name()";
        return this.queryString(selectQuery);
    }

    /**
     * Adds a new action for the user. Each action might have a level attribute.
     *
     * @param action tha name of the action tb added.
     * @param level the level of the action.
     * @throws DMSException with expected error codes.
     */
    public void addAction(String action, String level) throws DMSException {
        if (this.hasAction(action)) {
            throw new DMSException("User " + this.username + " already has the action " + action);
        }
        String selectQuery = this.queryMe() + "/actions";
        String newData = "<" + action + " level=\"" + level + "\"/>";
        this.addData(selectQuery, newData);
    }

    /**
     * Removes an action from a user.
     *
     * @param action the name of the action to be removed.
     * @throws DMSException with expected error codes.
     */
    public void removeAction(String action) throws DMSException {
        if (this.hasAction(action) == false) {
            throw new DMSException("User " + this.username + " does not have the action " + action);
        }
        String selectQuery = this.queryMe() + "/actions/" + action;
        this.removeData(selectQuery);
    }

    /**
     * Checks if the user has an action.
     *
     * @param action the name of the action, we want to check.
     * @return
     * <code>true</code> if the user has the specified action or
     * <code>false</code> otherwise.
     * @throws DMSException with expected error codes.
     */
    public boolean hasAction(String action) throws DMSException {
        String selectQuery = this.queryMe() + "/actions/" + action;
        return this.exist(selectQuery);
    }

    /**
     * Gets the level an action has.
     *
     * @param action the name of the action.
     * @return the level of the action or
     * <code>null</code> if there is no such acttion.
     * @throws DMSException with expected error codes.
     */
    public String getActionLevel(String action) throws DMSException {
        if (this.hasAction(action) == false) {
            return null;
        }
        String selectQuery = this.queryMe() + "/actions/" + action;

        if (this.exist(selectQuery) == false) {
            return null;
        }

        String[] ret = this.queryString(selectQuery + "/@level/string()");
        if (ret.length == 0) {
            return null;
        } else {
            return ret[0];
        }
    }

    /**
     * Sets the level of an action.
     *
     * @param action the name of the action.
     * @param level the value of the level.
     * @throws DMSException with expected error codes.
     */
    public void setActionLevel(String action, String level) throws DMSException {
        if (this.hasAction(action) == false) {
            throw new DMSException("User " + this.username + " does not have the action " + action);
        }
        String selectQuery = this.queryMe() + "/actions/" + action + "/@level";
        this.setData(selectQuery, level);
    }

    /**
     * Activates the user in the system.
     *
     * @throws DMSException
     */
    public void activate() throws DMSException {
        if (this.isActive()) {
            throw new DMSException("User " + this.username + " already active.");
        }
        String selectQuery = this.queryMe() + "/@active";
        this.setData(selectQuery, "yes");
    }

    /**
     * Deactivates the user in the system.
     *
     * @throws DMSException
     */
    public void deactivate() throws DMSException {
        if (this.isActive() == false) {
            throw new DMSException("User " + this.username + " already inactive.");
        }
        String selectQuery = this.queryMe() + "/@active";
        this.setData(selectQuery, "no");
    }

    /**
     * Checks if the user is active in the system.
     *
     * @return
     * <code>true</code> if the user is active
     * <code>false</code> otherwise.
     * @throws DMSException
     */
    public boolean isActive() throws DMSException {
        String selectQuery = "/DMS/users/user[@username='" + this.username
                + "' and @active='yes']";
        return this.exist(selectQuery);
    }

    /**
     * Returns the ids of the queries of the user in the system.
     *
     * @return an array of int (
     * <code>int[]</code>) containing the ids of the queries of this user in the
     * system.
     * @throws DMSException with expected error codes.
     */
    public int[] getQueryIds() throws DMSException {
        DMSFile file = new DMSFile(this.conf.QUERIES_FILE, this.conf);
        String selectQuery = "/DMS/queries/query[@uid='" + this.getId() + "']";

        if (file.exist(selectQuery) == false) {
            return new int[0];
        }

        String[] res = file.queryString(selectQuery + "/@id/string()");
        int[] ret = new int[res.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Integer.parseInt(res[i]);
        }
        return ret;
    }

    /**
     * Returns the ids of the queries of a particular category, of the user in
     * the system.
     *
     * @param category the category of the queries.
     * @return an array of int (
     * <code>int[]</code>) containing the ids of the queries of the specified
     * category of this user in the system.
     * @throws DMSException with expected error codes.
     */
    public int[] getQueryIds(String category) throws DMSException {
        DMSFile file = new DMSFile(this.conf.QUERIES_FILE, this.conf);
        String selectQuery = "/DMS/queries/query[@uid='" + this.getId()
                + "' and info/category='" + category + "']";

        if (file.exist(selectQuery) == false) {
            return new int[0];
        }

        String[] res = file.queryString(selectQuery + "/@id/string()");
        int[] ret = new int[res.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Integer.parseInt(res[i]);
        }
        return ret;
    }

    /**
     * Returns the ids of the queries of a particular category and type(personal
     * or general), of the user in the system.
     *
     * @param type the type of the queries.
     * @param category the category of the queries.
     * @return an array of int (
     * <code>int[]</code>) containing the ids of the queries of the specified
     * category of this user in the system.
     * @throws DMSException with expected error codes.
     */
    public int[] getQueryIdsType_Category(String type, String category) throws DMSException {
        DMSFile file = new DMSFile(this.conf.QUERIES_FILE, this.conf);
        String selectQuery = "/DMS/queries/query[@uid='" + this.getId()
                + "' and @type='" + type + "' and info/category='" + category + "']";

        if (file.exist(selectQuery) == false) {
            return new int[0];
        }

        String[] res = file.queryString(selectQuery + "/@id/string()");
        int[] ret = new int[res.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Integer.parseInt(res[i]);
        }
        return ret;
    }
    
     /**
     * Returns the ids of the queries of a particular of type=personal
     * of the user in the system.
     *     * 
     * @param category the category of the queries.
     * @return an array of int (
     * <code>int[]</code>) containing the ids of the queries of the specified
     * category of this user in the system.
     * @throws DMSException with expected error codes.
     */
    public int[] getQueryIds_Personal(String category) throws DMSException {
        DMSFile file = new DMSFile(this.conf.QUERIES_FILE, this.conf);
        String selectQuery = "/DMS/queries/query[@uid='" + this.getId()
                + "' and @type='" + "personal" + "' and info/category='" + category + "']";

        if (file.exist(selectQuery) == false) {
            return new int[0];
        }

        String[] res = file.queryString(selectQuery + "/@id/string()");
        int[] ret = new int[res.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Integer.parseInt(res[i]);
        }
        return ret;
    }
    
     /**
     * Returns the ids of the queries of a particular of type=public
     * of all users in the system.
     *     * 
     * @param category the category of the queries.
     * @return an array of int (
     * <code>int[]</code>) containing the ids of the queries of the specified
     * category of this user in the system.
     * @throws DMSException with expected error codes.
     */
    public int[] getQueryIds_Public(String category) throws DMSException {
        DMSFile file = new DMSFile(this.conf.QUERIES_FILE, this.conf);
        String selectQuery = "/DMS/queries/query[@type='" + "public" + "' and info/category='" + category + "']";

        if (file.exist(selectQuery) == false) {
            return new int[0];
        }

        String[] res = file.queryString(selectQuery + "/@id/string()");
        int[] ret = new int[res.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Integer.parseInt(res[i]);
        }
        return ret;
    }


    /**
     * Returns the names of the queries of the user in the system.
     *
     * @return an array of
     * <code>String</code> containing the names of the queries of this user in
     * the system.
     * @throws DMSException with expected error codes.
     */
    public String[] getQueryNames() throws DMSException {
        DMSFile file = new DMSFile(this.conf.QUERIES_FILE, this.conf);
        String selectQuery = "/DMS/queries/query[@uid='" + this.getId() + "']/info/name/text()";
        return file.queryString(selectQuery);
    }
}

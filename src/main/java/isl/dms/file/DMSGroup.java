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

import isl.dbms.DBMSException;
import isl.dms.DMSConfig;
import isl.dms.DMSException;

public class DMSGroup extends DMSFile {
	private String groupname;

	/**
	 * Creates a new <code>DMSGroup</code>. The new <code>DMSGroup</code>
	 * represents the group with the specified groupname.
	 * 
	 * @param groupname
	 *            the name of the group.
	 * @throws EntryNotFoundException if there is not any group with the specified groupname.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public DMSGroup(String groupname, DMSConfig conf) throws EntryNotFoundException, DMSException, DBMSException {
		if (checkGroup(groupname, conf) == false)
			throw new EntryNotFoundException("Group not found: " + groupname);
		this.setDMSfile(conf.GROUPS_FILE, conf);
		this.groupname = groupname;
	}

	/**
     * Constructs a new <code>DMSGroup</code>. The new <code>DMSGroup</code>
	 * represents the group with the specified id.
     * @param id the id of the group.
     * @param conf a DMSConfig object.
     * @throws EntryNotFoundException if there is not any group with the specified id.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public DMSGroup(int id, DMSConfig conf) throws EntryNotFoundException, DMSException, DBMSException {
		String groupname = getGroupnameOf(id, conf);
		if (groupname == null)
			throw new EntryNotFoundException("Group not found for id: " + id);
		this.setDMSfile(conf.GROUPS_FILE, conf);
		this.groupname = groupname;
	}

	/**
     * Returns the groupname of the group having a specified id.
     * @return the groupname of the group with the specified id,
     * or <code>null</code> if there is no group with this id.
     * @param id the id of the group.
     * @param conf a DMSConfig object.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static String getGroupnameOf(int id, DMSConfig conf) throws DMSException, DBMSException{
		DMSFile file = new DMSFile(conf.GROUPS_FILE, conf);
		String selectQuery = "/DMS/groups/group[@id='"+id+"']";
		
		if (file.exist(selectQuery) == false)
			return null;

		String[] res = file.queryString(selectQuery+"/@groupname/string()");
		if (res.length == 0)
			return null;
		else
			return res[0];
	}
	
	/**
     * Returns the name of the group having a specified id.
     * @return the name of the group with the specified id,
     * or <code>null</code> if there is no group with this id.
     * @param id the id of the group.
     * @param conf a DMSConfig object.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static String getNameOf(int id, DMSConfig conf) throws DMSException, DBMSException{
		DMSFile file = new DMSFile(conf.GROUPS_FILE, conf);
		String selectQuery = "/DMS/groups/group[@id='"+id+"']";
		
		if (file.exist(selectQuery) == false)
			return null;

		String[] res = file.queryString(selectQuery+"/info/name/string()");
		if (res.length == 0)
			return null;
		else
			return res[0];
	}
	
	/**
     * Returns the seat of the group having a specified id.
     * @return the seat of the group with the specified id,
     * or <code>null</code> if there is no group with this id.
     * @param id the id of the group.
     * @param conf a DMSConfig object.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static String getSeatOf(int id, DMSConfig conf) throws DMSException, DBMSException{
		DMSFile file = new DMSFile(conf.GROUPS_FILE, conf);
		String selectQuery = "/DMS/groups/group[@id='"+id+"']";
		
		if (file.exist(selectQuery) == false)
			return null;

		String[] res = file.queryString(selectQuery+"/info/seat/string()");
		if (res.length == 0)
			return null;
		else
			return res[0];
	}
		
	/**
     * Returns the country of the group having a specified id.
     * @return the country of the group with the specified id,
     * or <code>null</code> if there is no group with this id.
     * @param id the id of the group.
     * @param conf a DMSConfig object.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static String getCountryOf(int id, DMSConfig conf) throws DMSException, DBMSException{
		DMSFile file = new DMSFile(conf.GROUPS_FILE, conf);
		String selectQuery = "/DMS/groups/group[@id='"+id+"']";
		
		if (file.exist(selectQuery) == false)
			return null;

		String[] res = file.queryString(selectQuery+"/info/country/string()");
		if (res.length == 0)
			return null;
		else
			return res[0];
	}
	
	/**
     * Returns the information of the group having a specified id.
     * @return the information of the group with the specified id,
     * or <code>null</code> if there is no group with this id.
     * @param id the id of the group.
     * @param conf a DMSConfig object.
	 * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static String getInformationOf(int id, DMSConfig conf) throws DMSException, DBMSException{
		DMSFile file = new DMSFile(conf.GROUPS_FILE, conf);
		String selectQuery = "/DMS/groups/group[@id='"+id+"']";
		
		if (file.exist(selectQuery) == false)
			return null;

		String[] res = file.queryString(selectQuery+"/info/information/string()");
		if (res.length == 0)
			return null;
		else
			return res[0];
	}

	/**
     * Returns a mapping of the ids of the groups of the system to their groupnames.
     * @return a <code>Hashtable</code> containing the ids of the groups
     * 		of the system mapped to their groupnames.
     * @param conf a DMSConfig object
     * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static Hashtable getGroupToIdMapping(DMSConfig conf) throws DMSException, DBMSException {
		DMSFile file = new DMSFile(conf.GROUPS_FILE, conf);
		
		String[] ids = file.getIds();
		Hashtable ret = new Hashtable();
		
		for(int i=0; i<ids.length; i++)
			ret.put(ids[i], file.queryString("/DMS/groups/group[@id='"+ids[i]+"']/@groupname/string()")[0]);

		return ret;
	}

	/**
     * Returns the groups of the system.
     * @return an array of <code>String</code> containing the groupnames
     * of the groups of the system.
     * @param conf a DMSConfig object
     * @throws DMSException with expected error codes.
	 * @throws DBMSException with expected error codes.
     */
	public static String[] getGroups(DMSConfig conf) throws DMSException, DBMSException {
		DMSFile file = new DMSFile(conf.GROUPS_FILE, conf);
		String selectQuery = "/DMS/groups/*";

		if (file.exist(selectQuery) == false)
			return new String[0];

		return file.queryString(selectQuery+"/@groupname/string()");
	}

	/**
	 * Adds a new group to the system. A <code>DMSGroup</code>
	 * is returned representing the new group. If a group with the
	 * specified name allready exist, an exception is thrown
	 * indicating the error.
	 * 
	 * @param groupname
	 *            the name of the new group.
	 * @return a <code>DMSGroup</code> representing the new group.
	 * @throws EntryExistException if a group with the specified groupname already exists.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public static DMSGroup addGroup(String orgName,String groupname, String seat, String country, String information, DMSConfig conf) throws EntryExistException, DMSException, DBMSException {
		String id = String.valueOf(newGroupId(conf));
		String newData = "<group id=\"" + id + "\" groupname=\""+ groupname + "\">"
		+"<info><name>"+orgName+"</name><seat>"+seat+"</seat><country>"+country+"</country><information>"
		+information+"</information></info></group>";

		DMSFile file = new DMSFile(conf.GROUPS_FILE, conf);
		if (checkGroup(groupname, conf))
			throw new EntryExistException("Group already exist: " + groupname);
		file.addEntity(newData);
		return new DMSGroup(groupname, conf);
	}

	/**
	 * Sets the value of the groupname for the group.
	 * 
	 * @param groupname
	 *            the (new) groupname of the group.
	 * @throws EntryExistException if a group with the specified groupname already exists.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void setGroupname(String groupname) throws EntryExistException, DMSException, DBMSException {
		if (this.groupname.equals(groupname))
			return;
		if (checkGroup(groupname, this.conf))
			throw new EntryExistException("Group already exist: " + groupname);
		String selectQuery = this.queryMe() + "/@groupname";
		this.setData(selectQuery, groupname);
		this.groupname = groupname;
	}
	
	/**
	 * Sets the value of the orgName for the group.
	 * 
	 * @param orgName
	 *            the (new) orgName of the group.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void setName(String orgName) throws EntryExistException, DMSException, DBMSException {

		String selectQuery = this.queryMe() + "/info/name";
		this.setData(selectQuery, orgName);
	}
	
	/**
	 * Sets the value of the seat for the group.
	 * 
	 * @param seat
	 *            the (new) seat of the group.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void setSeat(String seat) throws EntryExistException, DMSException, DBMSException {

		String selectQuery = this.queryMe() + "/info/seat";
		this.setData(selectQuery, seat);
	}
	
	/**
	 * Sets the value of the country for the group.
	 * 
	 * @param country
	 *            the (new) country of the group.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void setCountry(String country) throws EntryExistException, DMSException, DBMSException {

		String selectQuery = this.queryMe() + "/info/country";
		this.setData(selectQuery, country);
	}
	
	/**
	 * Sets the value of the information for the group.
	 * 
	 * @param information
	 *            the (new) information of the group.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void setInformation(String information) throws EntryExistException, DMSException, DBMSException {

		String selectQuery = this.queryMe() + "/info/information";
		this.setData(selectQuery, information);
	}

	/**
	 * Removes this group from the system.
	 * 
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public void remove() throws DMSException, DBMSException {
		if (this.getUsers().length != 0)
			throw new DMSException("Group not empty: " + this.groupname);
		this.removeEntity("groupname", this.groupname);
	}

	/**
	 * Checks the existance of a group.
	 * 
	 * @param groupname
	 *            the name of the group to find.
	 * @return <code>true</code> if the group exists, <code>false</code>
	 *         otherwise.
	 * @throws DMSException
	 *             with expected error codes.
	 * @throws DBMSException with expected error codes.
	 */
	public static boolean checkGroup(String groupname, DMSConfig conf) throws DMSException, DBMSException {
		DMSFile file = new DMSFile(conf.GROUPS_FILE, conf);
		return file.exist("/DMS/groups/group[@groupname='"+groupname+"']");
	}

	/**
	 * Returns the users of the system that belongs to this group.
	 *
	 * @return an array of <code>String</code> containing the usernames
	 *         of the users of the system in this group.
	 * @throws DMSException with expected error codes.
	 */
	public String[] getUsers() throws DMSException {
		return DMSUser.getUsersInGroup(this.groupname, this.conf);
	}

	/**
	 * Returns the users of the system that belongs to a group.
	 *
	 * @return an array of <code>String</code> containing the usernames
	 *         of the users of the system in the specified group.
	 * @throws DMSException with expected error codes.
	 */
	public static String[] getUsersOf(String groupname, DMSConfig conf) throws DMSException {
		return DMSUser.getUsersInGroup(groupname, conf);
	}

	private String queryMe(){
		return "/DMS/groups/group[@groupname='" + this.groupname + "']";
	}

	private static int newGroupId(DMSConfig conf) throws DBMSException {
		return DMSFile.newId(conf.GROUPS_FILE, conf);
	}
}

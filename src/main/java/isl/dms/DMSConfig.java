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
package isl.dms;

public class DMSConfig {
    public String DB;
    public String DB_USERNAME;
    public String DB_PASSWORD;
    public String COLLECTION;
    
    public String LANG;
    
    public String USERS_FILE;
    public String GROUPS_FILE;
    public String QUERIES_FILE;
    public String TAGS_FILE;
    public String COLLECTIONS_FILE;
    public String ADMINS_FILE;
    public String VERSIONS_FILE;
    
    public DMSConfig(String db, String collection, String username, String password){
        DB 			= db;
        COLLECTION 	= collection;
        DB_USERNAME = username;
        DB_PASSWORD = password;

        LANG		= "";

        USERS_FILE 	= "DMSUsers.xml";
        GROUPS_FILE = "DMSGroups.xml";
        QUERIES_FILE= "DMSXQueries.xml";
        TAGS_FILE 	= "DMSTags.xml";
        COLLECTIONS_FILE= "DMSCollections.xml";
        ADMINS_FILE	= "DMSAdmins.xml";
	VERSIONS_FILE= "DMS_owl_versions.xml";
    }

}

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

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Database;

/**
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DBObject {
	Database database;

	/**
	 * Register the driver for the connection to the database.
	 * 
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	protected void registerDriver() throws DBMSException {
		try {
			String driver = DBMSConfig.getEXISTDRIVER();
			Class cl = Class.forName(driver);
			this.database = (Database) cl.newInstance();
			DatabaseManager.registerDatabase(this.database);
		} catch (Exception Ex) {
			throw new DBMSException(Ex.getMessage());
		}
	}

	/**
	 * Deregister the driver for the connection to the database.
	 * Once a database has been deregistered it can no longer be
	 * used to handle requests. 
	 * 
	 * @throws DBMSException
	 *             with expected error codes.
	 */
	protected void deregisterDriver() throws DBMSException {
		try {
			DatabaseManager.deregisterDatabase(this.database);
		} catch (Exception Ex) {
			throw new DBMSException(Ex.getMessage());
		}
	}
}

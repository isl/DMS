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
package isl.dms.xml;

import isl.dbms.DBCollection;
import isl.dbms.DBMSException;
import isl.dms.DMSException;

/**
 * This class handles Collections of XMLDocuments
 * @author samarita
 */
public class XMLCollection extends DBCollection{

    /**
     * Creates a new <code>XMLCollection</code>. The new
     * <code>XMLDocument</code> represents a collection of XML file stored
     * in a database.
     *
     * @param database the database where the collection (next argument) is in.
     * @param collection the name of the collection.
     * @param user the username to use for authentication to the database or
     *            <code>null</code> if the database does not support
     *            authentication.
     * @param password the password to use for authentication to the database or
     *            <code>null</code> if the database does not support
     *            authentication.
     * @throws DMSException with expected error codes.
     * @throws DBMSException with expected error codes.
     */
    public XMLCollection(String database, String collection, String user, String password) throws DMSException, DBMSException {
        super(database, collection, user, password);
    }
}

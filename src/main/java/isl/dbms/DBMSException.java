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

import org.xmldb.api.base.XMLDBException;

/**
 * DBMSException is thrown for all errors in the DBMS API. It contains a message
 * error <code>String</code> describing the error.
 */
public class DBMSException extends RuntimeException {
	private String message;

	public DBMSException(String msg) {
		message = msg;
	}

	public DBMSException(XMLDBException XMLDBEx) {
		String msg = XMLDBEx.getMessage();
		msg = msg.replaceAll("Resource|resource", "DBFile");
		String[] msgParts = msg.split("\n")[0].split(":");
		String msgOK = "";
		for(int i=1;i<msgParts.length;i++) msgOK += msgParts[i];
		String[] msgs = msgParts[0].split("\\.");
		msg = msgs[msgs.length-1] + ":" + msgOK;
		message = "["+msg+"]";
		//message = XMLDBEx.getMessage();
	}

	public DBMSException(Exception Ex) {
		String msg = Ex.getMessage();
		msg = msg.replaceAll("Resource|resource", "DBFile");
		String[] msgParts = msg.split("\n")[0].split(":");
		String msgOK = "";
		for(int i=1;i<msgParts.length;i++) msgOK += msgParts[i];
		String[] msgs = msgParts[0].split("\\.");
		msg = msgs[msgs.length-1] + ":" + msgOK;
		message = "["+msg+"]";
		//message = Ex.getMessage();
	}
	
	public String getMessage() {
		return message;
	}
}

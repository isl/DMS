Copyright 2006-2015 Institute of Computer Science,
Foundation for Research and Technology - Hellas

Licensed under the EUPL, Version 1.1 or - as soon they will be approved
by the European Commission - subsequent versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl

Unless required by applicable law or agreed to in writing, software distributed
under the Licence is distributed on an "AS IS" basis,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the Licence for the specific language governing permissions and limitations
under the Licence.

Contact:  POBox 1385, Heraklio Crete, GR-700 13 GREECE
Tel:+30-2810-391632
Fax: +30-2810-391638
E-mail: isl@ics.forth.gr
http://www.ics.forth.gr/isl

Authors : Nikos Papadopoulos, Georgios Samaritakis, Konstantina Konsolaki.

This file is part of the DMS project.

DMS
======

DMS is a Java API to work with an [eXist] (http://www.exist-db.org "eXist") database (versions 2.1+).

## Build - Run
Folder src contain all the files needed to build and create a jar file. This project is a Maven project, providing all the libs in pom.xml.

## Usage
The DMS dependecies and licenses used are described in file DMS-Dependencies-LicensesUsed.txt 

Usage examples:

How to create a file inside an eXist Collection:
```java
public int createFile(String collection, String filename, String content) {
        try{
            DBCollection Col = new DBCollection(Config.DB, collection ,Config.DBUSERNAME, Config.DBPASSWORD);
            DBFile document = Col.createFile(filename,"XMLDBFile");
            document.setXMLAsString(content);
            Col.storeFile(document);
            return 0;
        } catch(Exception e) {
            e.printStackTrace(System.out);
            System.out.println("ERROR occured in Utils.putFile");
            return -1;
        }
}
```

List files inside an eXist Collection:
```java
  String [] files = null;      
        try {
          DBCollection  dbcol = new DBCollection("xmldb:exist://139.91.183.31:8080/exist/xmlrpc","/db/Logs","admin","admin");
          files = dbcol.listFiles();       
        } catch (DBMSException ex) {      
            ex.printStackTrace();
        }
        return files;
```

Perform a xquery in a eXist file :
```java
   String q1[] = null;
        String q = null;
        DBFile Log = new DBFile("xmldb:exist://139.91.183.31:8080/exist/xmlrpc","/db/Logs", filename,"admin","admin");										                      
        q = "for $x in //task\n"+
             "where $x/type='Error'\n"+
             "return <task>{$x/file}</task>";
        q1 =  Log.queryString(q);
```

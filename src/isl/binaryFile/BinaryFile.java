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
package isl.binaryFile;


import isl.dbms.DBCollection;
import isl.dbms.DBFile;
import isl.dbms.DBMSException;
import isl.dms.DMSConfig;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The <code>BinaryFile</code> represents a binary file of the system. 
 * It provides set & get methods and can be used to easily store, get and change
 * all non-XML files. Most of its methods work well only for
 * textual files (e.g. xq, txt, etc...). These files may also have variable parts,
 * declared as "___XXX___", which can be given values using an <code>ArrayList</code>
 * with <CODE>String</CODE> params.
 * This class will be used to handle XQueries stored in the DB as xq files.
 */
public class BinaryFile {
    
    private static final String PARAMETER = "___XXX___";
    /**
     * a <CODE>DMSConfig</CODE> object
     */
    protected DMSConfig conf;
    /**
     * <CODE>DBFile</CODE> object
     */
    protected DBFile DBfile;
    /**
     * filename as a <CODE>String</CODE>
     */
    protected String DBfilename;
    
    /**
     * Default constructor
     */
    protected BinaryFile(){
    }
    
    /**
     * Constructs a new <code>BinaryFile</code> instance associated with the
     * specified file in the database. If the file does not exist, it is created!
     * @param conf a DMSConfig object
     * @param collection the name of the collection.
     * @param file the name of the file.
     * @throws DBMSException with expected error codes.
     */
    public BinaryFile(String collection, String file, DMSConfig conf) throws DBMSException {
        try {
            this.DBfile = new DBFile(conf.DB, collection, file, conf.DB_USERNAME, conf.DB_PASSWORD);
        } catch (DBMSException ex) {
            
            //ex.printStackTrace();
            DBCollection motherCol = new DBCollection(conf.DB, collection, conf.DB_USERNAME, conf.DB_PASSWORD);
            this.DBfile = motherCol.createFile(file,"BinaryDBFile");
            this.DBfile.setBinary(new String("").getBytes());
            this.DBfile.store();
        }
        this.DBfilename = file;
        this.conf = conf;
    }
    
    
    
    /**
     * Sets the binary content for this <code>BinaryFile</code> using an array of
     * bytes <code>byte[]</code> as the source.
     *
     * @param content
     *            The new content value.
     * @throws DBMSException
     *             with expected error codes.
     */
    public void set(byte[] content) throws DBMSException {
        this.DBfile.setBinary(content);
        this.DBfile.store();
    }
    
    /**
     * Sets the binary content for this <code>BinaryFile</code> using a <code>String</code> as the source.
     *
     * @param content
     *            The new content value.
     * @throws DBMSException
     *             with expected error codes.
     */
    public void set(String content) throws DBMSException {
        try {
            this.DBfile.setBinary(content.getBytes("UTF-8"));
            this.DBfile.store();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (DBMSException ex) {
            ex.printStackTrace();
        }
    }
    
    
    /**
     * Returns the binary content of this <code>BinaryFile</code> as an array of
     * bytes <code>(byte[])</code>.
     *
     * @return the binary content of this <code>BinaryFile</code> as an array of
     *         bytes <code>(byte[])</code>.
     * @throws DBMSException
     *             with expected error codes.
     */
    public byte[] get() throws DBMSException {
        return this.DBfile.getBinary();
    }
    
    /**
     * Returns a <code>String</code> representing the content
     * of the <code>BinaryFile</code>.
     * @return <code>String</code> representation of <code>BinaryFile</code>
     */
    public String toString() {
        String content = null;
        try {
            content = new String(this.get(), "UTF-8");
        } catch (DBMSException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return content;
    }
    
    /**
     * Returns a <code>String</code> representing the content
     * of the <code>BinaryFile</code> after having substituted all parameters.
     * @param params an <code>ArrayList <String></code> containing the parameters of the <code>BinaryFile</code>
     * @return <code>String</code> representation of <code>BinaryFile</code>
     */
    public String toString(ArrayList <String> params) {
        
        String content = this.toString();
        
        CharSequence inputStr = content;
        String patternStr = PARAMETER;
        
        // Compile regular expression
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);
        
        // Replace all occurrences of pattern in input
        StringBuffer buf = new StringBuffer();
        boolean found = false;
        while ((found = matcher.find())) {
            
            if (params.size()>0) {
                //Always get first param
                String replaceStr = params.get(0);
                
                // Insert replacement
                matcher.appendReplacement(buf, replaceStr);
                
                //Delete used param
                params.remove(0);
            }
        }
        matcher.appendTail(buf);
        
        return buf.toString();
    }
    
    
    /**
     * Deletes this <code>BinaryFile</code> from the system.
     * @throws DBMSException with expected error codes.
     */
    public void delete() throws DBMSException{
        this.DBfile.remove();
    }
    
    
    
}


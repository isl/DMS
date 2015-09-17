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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

abstract public class DBXUpdate extends DBObject {

    /**
     * Runs an append operation using XUpdate.
     *
     * @param selectQuery XPath that selects where to append.
     * @param xml What to append as <CODE>String</CODE>.
     * @return the number of modified nodes.
     * @throws DBMSException with expected error codes.
     */
    public long xAppend(String selectQuery, String xml) throws DBMSException {
        String updateQuery = "<?xml version=\"1.0\"?>"
                + "<xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:append select=\"" + selectQuery + "\">" + xml
                + "</xupdate:append>" + "</xupdate:modifications>";
        return this.update(updateQuery);
    }

    /**
     * Adds an attribute and its value using XUpdate.
     *
     * @param selectQuery XPath that selects where to append.
     * @param name Name of the attribute as <CODE>String</CODE>.
     * @param value Value of the attribute as <CODE>String</CODE>.
     * @return the number of modified nodes.
     * @throws DBMSException with expected error codes.
     */
    public long xAddAttribute(String selectQuery, String name, String value) throws DBMSException {
        String updateQuery = "<?xml version=\"1.0\"?>"
                + "<xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:append select=\"" + selectQuery + "\">"
                + "<xupdate:attribute name=\"" + name + "\">" + value
                + "</xupdate:attribute>" + "</xupdate:append>"
                + "</xupdate:modifications>";
        return this.update(updateQuery);
    }

    /**
     * Runs an insert-before operation using XUpdate.
     *
     * @return the number of modified nodes.
     * @param selectQuery XPath that selects the node before which the insertion
     * will happen.
     * @param xml What to insert as <CODE>String</CODE>.
     * @throws DBMSException with expected error codes.
     */
    public long xInsertBefore(String selectQuery, String xml) throws DBMSException {

        String update_start = "<?xml version=\"1.0\"?><xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:insert-before select=\"" + selectQuery + "\">";
        String update_middle = xml;
        String update_end = "</xupdate:insert-before></xupdate:modifications>";
        String updateQuery = update_start + update_middle + update_end;

        return this.update(updateQuery);
    }

    /**
     * Runs an insert-after operation using XUpdate.
     *
     * @return the number of modified nodes.
     * @param selectQuery XPath that selects the node after which the insertion
     * will happen.
     * @param xml What to insert as <CODE>String</CODE>.
     * @throws DBMSException with expected error codes.
     */
    public long xInsertAfter(String selectQuery, String xml) throws DBMSException {

        String update_start = "<?xml version=\"1.0\"?> <xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:insert-after select=\"" + selectQuery + "\">";
        String update_middle = xml;
        String update_end = "</xupdate:insert-after></xupdate:modifications>";
        String updateQuery = update_start + update_middle + update_end;

        return this.update(updateQuery);
    }

    /**
     * Runs a remove operation using XUpdate. It removes the first occurence of
     * 'selectQuery'.
     *
     * @return the number of modified nodes.
     * @param selectQuery XPath that selects what to remove.
     * @throws DBMSException with expected error codes.
     */
    public long xRemove(String selectQuery) throws DBMSException {

        String update_start = "<?xml version=\"1.0\"?> <xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:remove select=\"" + selectQuery + "\">";
        String update_end = "</xupdate:remove></xupdate:modifications>";
        String updateQuery = update_start + update_end;

        return this.update(updateQuery);
    }

    /**
     * Runs a rename operation using XUpdate.
     *
     * @return the number of modified nodes.
     * @param selectQuery XPath that selects what to rename.
     * @param xml New name as <CODE>String</CODE>.
     * @throws DBMSException with expected error codes.
     */
    public long xRename(String selectQuery, String xml) throws DBMSException {

        String update_start = "<?xml version=\"1.0\"?> <xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:rename select=\"" + selectQuery + "\">";
        String update_middle = xml;
        String update_end = "</xupdate:rename></xupdate:modifications>";
        String updateQuery = update_start + update_middle + update_end;

        return this.update(updateQuery);
    }

    /**
     * Runs a copy operation using XUpdate.
     *
     * @return the number of modified nodes.
     * @param sourceQuery XPath that selects what to copy.
     * @param destinationQuery XPath indicating after where to copy.
     * @throws DBMSException with expected error codes.
     */
    public long xCopyAfter(String sourceQuery, String destinationQuery) throws DBMSException {

        String update_start = "<?xml version=\"1.0\"?> <xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:variable name=\"copy\" select=\""
                + sourceQuery + "\"/>";
        String update_middle = "<xupdate:insert-after select=\""
                + destinationQuery + "\">"
                + "<xupdate:value-of select=\"$copy\"/></xupdate:insert-after>";
        String update_end = "</xupdate:modifications>";
        String updateQuery = update_start + update_middle + update_end;

        return this.update(updateQuery);
    }

    /**
     * Runs a move operation using XUpdate.
     *
     * @return the number of modified nodes.
     * @param sourceQuery XPath that selects what to move.
     * @param destinationQuery XPath indicating after where to move.
     * @throws DBMSException with expected error codes.
     */
    public long xMoveAfter(String sourceQuery, String destinationQuery) throws DBMSException {

        String update_start = "<?xml version=\"1.0\"?> <xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:variable name=\"copy\" select=\""
                + sourceQuery + "\"/>";
        String update_middle = "<xupdate:remove select=\"$copy\"/>"
                + "<xupdate:insert-after select=\"" + destinationQuery + "\">"
                + "<xupdate:value-of select=\"$copy\"/></xupdate:insert-after>";
        String update_end = "</xupdate:modifications>";
        String updateQuery = update_start + update_middle + update_end;

        return this.update(updateQuery);
    }

    /**
     * Runs a copy operation using XUpdate.
     *
     * @return the number of modified nodes.
     * @param sourceQuery XPath that selects what to copy.
     * @param destinationQuery XPath indicating before where to copy.
     * @throws DBMSException with expected error codes.
     */
    public long xCopyBefore(String sourceQuery, String destinationQuery) throws DBMSException {

        String update_start = "<?xml version=\"1.0\"?> <xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:variable name=\"copy\" select=\""
                + sourceQuery + "\"/>";
        String update_middle = "<xupdate:insert-before select=\""
                + destinationQuery
                + "\">"
                + "<xupdate:value-of select=\"$copy\"/></xupdate:insert-before>";
        String update_end = "</xupdate:modifications>";
        String updateQuery = update_start + update_middle + update_end;

        return this.update(updateQuery);
    }

    /**
     * Runs a move operation using XUpdate.
     *
     * @return the number of modified nodes.
     * @param sourceQuery XPath that selects what to move.
     * @param destinationQuery XPath indicating before where to move.
     * @throws DBMSException with expected error codes.
     */
    public long xMoveBefore(String sourceQuery, String destinationQuery) throws DBMSException {

        String update_start = "<?xml version=\"1.0\"?> <xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:variable name=\"copy\" select=\""
                + sourceQuery + "\"/>";
        String update_middle = "<xupdate:remove select=\"$copy\"/>"
                + "<xupdate:insert-before select=\""
                + destinationQuery
                + "\">"
                + "<xupdate:value-of select=\"$copy\"/></xupdate:insert-before>";
        String update_end = "</xupdate:modifications>";
        String updateQuery = update_start + update_middle + update_end;

        return this.update(updateQuery);
    }

    /**
     * Runs a copy operation using XUpdate.
     *
     * @return the number of modified nodes.
     * @param sourceQuery XPath that selects what to copy.
     * @param destinationQuery XPath indicating inside where to copy.
     * @throws DBMSException with expected error codes.
     */
    public long xCopyInside(String sourceQuery, String destinationQuery) throws DBMSException {

        String update_start = "<?xml version=\"1.0\"?> <xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:variable name=\"copy\" select=\""
                + sourceQuery + "\"/>";
        String update_middle = "<xupdate:append select=\"" + destinationQuery
                + "\">"
                + "<xupdate:value-of select=\"$copy\"/></xupdate:append>";
        String update_end = "</xupdate:modifications>";
        String updateQuery = update_start + update_middle + update_end;

        return this.update(updateQuery);
    }

    /**
     * Runs a move operation using XUpdate. The 'sourceQuery' is moved inside
     * the 'destinationQuery' and is appended as last child of it.
     *
     * @return the number of modified nodes.
     * @param sourceQuery XPath that selects what to move.
     * @param destinationQuery XPath indicating inside where to move.
     * @throws DBMSException with expected error codes.
     */
    public long xMoveInside(String sourceQuery, String destinationQuery) throws DBMSException {

        String update_start = "<?xml version=\"1.0\"?> <xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:variable name=\"copy\" select=\""
                + sourceQuery + "\"/>";
        String update_middle = "<xupdate:remove select=\"$copy\"/>"
                + "<xupdate:append select=\"" + destinationQuery + "\">"
                + "<xupdate:value-of select=\"$copy\"/></xupdate:append>";
        String update_end = "</xupdate:modifications>";
        String updateQuery = update_start + update_middle + update_end;

        return this.update(updateQuery);
    }

    /**
     * Runs an update operation using XUpdate.
     *
     * @return the number of modified nodes.
     * @param selectQuery XPath that selects what to update.
     * @param xml What to update as <CODE>String</CODE>.
     * @throws DBMSException with expected error codes.
     */
    public long xUpdate(String selectQuery, String xml) throws DBMSException {

        if (xml.trim().equals("")) { //SPECIAL CASE
            if (pathIsAttribute(selectQuery)) { //attr mode
                String attributeName = selectQuery.substring(selectQuery.lastIndexOf("/") + 2);
                String fatherXpath = selectQuery.substring(0, selectQuery.lastIndexOf("/"));
                return xAddAttribute(fatherXpath, attributeName, "");
            } else { //element mode
                xRemove(selectQuery+"/*");
                return xRemove(selectQuery+"/text()");
            }
        }

        String update_start = "<?xml version=\"1.0\"?> <xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + "<xupdate:update select=\"" + selectQuery + "\">";
        String update_middle = xml;
        String update_end = "</xupdate:update></xupdate:modifications>";
        String updateQuery = update_start + update_middle + update_end;
        return this.update(updateQuery);
    }

    /**
     * Checks if a given path is an attribute.
     *
     * @return true if attribute, false otherwise.
     * @param input xpath.
     */
    
    private boolean pathIsAttribute(String input) {
        boolean foundMatch = false;
        try {
            Pattern regex = Pattern.compile("/@[^/]+\\z");
            Matcher regexMatcher = regex.matcher(input);
            foundMatch = regexMatcher.find();
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
            ex.printStackTrace();
            return false;
        }
        return foundMatch;
    }

    /**
     * Runs a set of XUpdate operations.
     *
     * @param updateQuery The XUpdate commands to use.
     * @return the number of modified nodes.
     * @throws DBMSException with expected error codes.
     */
    abstract public long update(String updateQuery) throws DBMSException;
}

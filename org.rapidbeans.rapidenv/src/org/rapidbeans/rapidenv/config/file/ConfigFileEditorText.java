/*
 * RapidEnv: ConfigFileEditorText.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/11/2010
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copies of the GNU Lesser General Public License and the
 * GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package org.rapidbeans.rapidenv.config.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.core.util.StringHelper.FillMode;
import org.rapidbeans.core.util.TrimMode;

/**
 * @author bluemel
 */
public final class ConfigFileEditorText extends ConfigFileEditor {

    private List<String> lines = null;

    /**
     * getter for the lines List.
     * 
     * @return List with all the text file's lines
     */
    public List<String> getLines() {
        return this.lines;
    }

    /**
     * the constructor.
     * 
     * @param cfile
     *            - the file to configure
     * @param file
     *            - the file to edit (may be null)
     */
    public ConfigFileEditorText(final ConfigFile cfile, final File file) {
        super(cfile, file);
    }

    /**
     * searches for a Java regular expression.
     * 
     * @param pattern
     *            - regular expression
     * @return the line indices of lines where the pattern is found
     */
    public int[] search(final String pattern) {
        this.load();

        int i;
        final List<Integer> l = new ArrayList<Integer>();
        final String matchPattern = ".*" + pattern + ".*";
        for (i = 0; i < this.lines.size(); i++) {
            String line = (String) lines.get(i);
            if (line.matches(matchPattern)) {
                l.add(new Integer(i));
            }
        }

        int[] ret = new int[l.size()];
        for (i = 0; i < l.size(); i++) {
            ret[i] = ((Integer) l.get(i)).intValue();
        }
        return ret;
    }

    /**
     * replacing all text from regular expression to new text.
     * 
     * @param from
     *            - regular expression
     * @param to
     *            - new text
     */
    public void replace(final String from, final String to) {
        this.load();

        final String pattern = ".*" + from + ".*";
        final String to1 = to.replaceAll("[\\\\]", "\\\\\\\\");
        for (int i = 0; i < lines.size(); i++) {
            String line = (String) lines.get(i);
            if (line.matches(pattern)) {
                final String newLine = line.replaceAll(from, to1);
                lines.set(i, newLine);
                this.setChangedSomething();
            }
        }
    }

    /**
     * appends a new line to the text file.
     * 
     * @param newLine
     *            a String containing the text of the new line.
     */
    public void appendLine(final String newLine) {
        this.load();
        this.lines.add(newLine);
        this.setChangedSomething();
    }

    /**
     * prepends a new line to the top the text file.
     * 
     * @param newLine
     *            a String containing the text of the new line.
     */
    public void prependLine(final String newLine) {
        this.load();
        this.lines.add(0, newLine);
        this.setChangedSomething();
    }

    /**
     * searches for a Java regular expression.
     * 
     * @param pattern
     *            - regular expression
     * @param commentsign
     *            - sign for comments
     * @param commentswitch
     *            - switch the comment
     * @return the line indices of lines where the pattern is found
     */
    public int[] searchLinesWithComment(final String pattern, final String commentsign, final boolean commentswitch) {
        this.load();

        int i;
        final List<Integer> l = new ArrayList<Integer>();
        final String matchPattern = ".*" + pattern + ".*";
        final char[] tc = { '\n', '\t', ' ' };
        String line;
        String trimmedLine;
        for (i = 0; i < this.lines.size(); i++) {
            line = (String) lines.get(i);
            trimmedLine = StringHelper.trim(line, tc, TrimMode.leading);
            if (line.matches(matchPattern)) {
                if ((commentswitch && trimmedLine.startsWith(commentsign))
                        || (!commentswitch && !trimmedLine.startsWith(commentsign))) {
                    l.add(new Integer(i));
                }
            }
        }

        int[] ret = new int[l.size()];
        for (i = 0; i < l.size(); i++) {
            ret[i] = ((Integer) l.get(i)).intValue();
        }
        return ret;
    }

    /**
     * replacing all text from regular expression to new text.
     * 
     * @param lineindices
     *            - line numbers
     * @param commentsign
     *            - sign for comments
     * @param inout
     *            -
     */
    public void comment(final int[] lineindices, final String commentsign, final boolean inout) {
        this.load();

        String newline;
        final char[] ca = { ' ', '\n', '\t' };
        for (int i = 0; i < lineindices.length; i++) {
            String line = (String) lines.get(lineindices[i]);
            if (!inout) {
                newline = commentsign + " " + line;
                lines.set(lineindices[i], newline);
            } else {
                newline = StringHelper.trim(line, ca, TrimMode.leading);
                newline = newline.substring(commentsign.length());
                newline = StringHelper.trim(newline, ca, TrimMode.leading);
                lines.set(lineindices[i], newline);
            }
            this.setChangedSomething();
        }
    }

    /**
     * load a property file.
     */
    protected void load() {
        if (this.lines != null) {
            return;
        }
        this.lines = super.loadFile();
    }

    /**
     * save a property file.
     */
    protected void save() {
        super.saveFile((List<String>) lines);
    }

    /**
     * find a line that exactly matches the given string.
     * 
     * @param searchline
     *            the line we search for
     * 
     * @return an integer array containing the line indices (beginning with 0).
     */
    public int[] searchLinesExactly(final String searchline) {
        this.load();
        final List<Integer> foundLineNumbers = new ArrayList<Integer>();
        String line;
        int i;
        for (i = 0; i < this.lines.size(); i++) {
            line = (String) lines.get(i);
            if (line.equals(searchline)) {
                foundLineNumbers.add(new Integer(i));
            }
        }
        int[] result = new int[foundLineNumbers.size()];
        for (i = 0; i < foundLineNumbers.size(); i++) {
            result[i] = ((Integer) foundLineNumbers.get(i)).intValue();
        }
        return result;
    }

    /**
     * Insert a new line before or after the specified position
     *
     * @param line the line to insert
     * @param i the line number that specifies the position
     *          where to insert the new line
     * @param mode specifies if to insert
     *             after (append) or before (prepend)
     */
    public void insertLine(String line, int i, InsertMode mode) {
        switch (mode) {
        case prepend:
            this.lines.add(i, line);
            break;
        case append:
            if (i == (this.lines.size() - 1)) {
                // i is the last index
                this.lines.add(line);
            } else {
                this.lines.add(i + 1, line);
            }
            break;
        }
        this.setChangedSomething();
    }

    /**
     * Delete the line(s) specified by the given regular expression.
     *
     * @param regexp the regular expression specifying the lines to delete.
     * @param withLineNumbers specify if the lines found should be given
     *                        back with line numbers
     *
     * @return the lines deleted
     */
    public List<String> deleteLines(final String regexp,
            final boolean withLineNumbers) {
        final List<String> lines = new ArrayList<String>();
        final int[] matches = search(regexp);
        final int len = matches.length;
        int deleted = 0;
        for (int i = 0; i < len; i++) {
            if (withLineNumbers) {
                lines.add(StringHelper.fillUp(
                    Integer.toString(matches[i] + 1), 5, ' ', FillMode.left)
                    + ": " + this.lines.get(matches[i] - deleted));
            } else {
                lines.add(this.lines.get(matches[i] - deleted));
            }
            this.lines.remove(matches[i] - deleted);
            deleted++;
        }
        setChangedSomething();
        return lines;
    }

    /**
     * Search the line(s) specified by the given regular expression.
     *
     * @param regexp the regular expression specifying the lines to search.
     * @param withLineNumbers specify if the lines found should be given
     *                        back with line numbers
     *
     * @return the lines found
     */
    public List<String> searchLines(String regexp,
            final boolean withLineNumbers) {
        final List<String> lines = new ArrayList<String>();
        final int[] matches = search(regexp);
        final int len = matches.length;
        for (int i = 0; i < len; i++) {
            if (withLineNumbers) {
                lines.add(StringHelper.fillUp(
                    Integer.toString(matches[i] + 1), 5, ' ', FillMode.left)
                    + ": " + this.lines.get(matches[i]));
            } else {
                lines.add(this.lines.get(matches[i]));
            }
        }
        return lines;
    }
}

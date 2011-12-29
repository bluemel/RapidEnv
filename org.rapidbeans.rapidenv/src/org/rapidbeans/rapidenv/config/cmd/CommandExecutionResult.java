/*
 * RapidEnv: CommandExecutionResult.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 09/30/2010
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

package org.rapidbeans.rapidenv.config.cmd;

 /**
  * A System command execution result encapsulates
  * output of stdout and stderror as well as the
  * command's returncode.
  *
  * @author Martin Bluemel
  */
public class CommandExecutionResult {

    /**
     * standard out.
     */
    private String stdout = null;

    /**
     * standard error.
     */
    private String stderr = null;

    /**
     * return code.
     */
    private int returncode = 0;

    /**
     * constructor.
     *
     * @param out stdout
     * @param err stderr
     * @param ret returncode
     */
    public CommandExecutionResult(
            final String out, final String err, final int ret) {
        this.stdout = out;
        this.stderr = err;
        this.returncode = ret;
    }

    /**
     * @return the returncode
     */
    public int getReturncode() {
        return returncode;
    }

    /**
     * @return the stderr
     */
    public String getStderr() {
        return stderr;
    }

    /**
     * @return the stdout
     */
    public String getStdout() {
        return stdout;
    }
}

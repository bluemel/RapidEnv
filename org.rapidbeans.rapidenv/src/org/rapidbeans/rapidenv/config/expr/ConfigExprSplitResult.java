package org.rapidbeans.rapidenv.config.expr;

/**
 * Simple container for the split for function name analysis.
 */
public class ConfigExprSplitResult {

    private String before = null;

    private String funcname = null;

    private String whitespacesAfter = null;

    /**
     * @return the before
     */
    public String getBefore() {
        return before;
    }

    /**
     * @param before the before to set
     */
    public void setBefore(String before) {
        this.before = before;
    }

    /**
     * @return the funcname
     */
    public String getFuncname() {
        return funcname;
    }

    /**
     * @param funcname the funcname to set
     */
    public void setFuncname(String funcname) {
        this.funcname = funcname;
    }

    /**
     * @return the whitespacesAfter
     */
    public String getWhitespacesAfter() {
        if (this.whitespacesAfter == null) {
            return "";
        } else {
            return whitespacesAfter;
        }
    }

    /**
     * @param whitespacesAfter the whitespacesAfter to set
     */
    public void setWhitespacesAfter(String whitespacesAfter) {
        this.whitespacesAfter = whitespacesAfter;
    }
}

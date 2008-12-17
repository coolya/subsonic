package net.sourceforge.subsonic.taglib;

import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

/**
 * Escapes the characters in a <code>String</code> using JavaScript String rules.
 * <p/>
 * Escapes any values it finds into their JavaScript String form.
 * Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.)
 * <p/>
 * So a tab becomes the characters <code>'\\'</code> and
 * <code>'t'</code>.
 * <p/>
 * The only difference between Java strings and JavaScript strings
 * is that in JavaScript, a single quote must be escaped.
 * <p/>
 * Example:
 * <pre>
 * input string: He didn't say, "Stop!"
 * output string: He didn\'t say, \"Stop!\"
 * </pre>
 *
 * @author Sindre Mehus
 */
public class EscapeJavaScriptTag extends BodyTagSupport {

    private String string;

    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print(StringEscapeUtils.escapeJavaScript(string));
        } catch (IOException x) {
            throw new JspTagException(x);
        }
        return EVAL_PAGE;
    }

    public void release() {
        string = null;
        super.release();
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
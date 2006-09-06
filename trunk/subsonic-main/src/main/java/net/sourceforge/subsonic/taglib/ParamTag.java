package net.sourceforge.subsonic.taglib;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

/**
 * A tag representing an URL query parameter.
 *
 * @see ParamTag
 * @author Sindre Mehus
 */
public class ParamTag extends TagSupport {

    private String name;
    private String value;

    public int doEndTag() throws JspTagException {

        // Add parameter name and value to surrounding 'url' tag.
        UrlTag tag = (UrlTag) findAncestorWithClass(this, UrlTag.class);
        if (tag == null) {
            throw new JspTagException("'sub:param' tag used outside 'sub:url'");
        }
        tag.addParameter(name, value);
        return EVAL_PAGE;
    }

    public void release() {
        name = null;
        value = null;
        super.release();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

package net.sourceforge.subsonic.taglib;

import org.radeox.api.engine.*;
import org.radeox.api.engine.context.*;
import org.radeox.engine.*;
import org.radeox.engine.context.*;
import org.apache.commons.lang.*;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.io.*;

/**
 * Renders a Wiki text with markup to HTML, using the Radeox render engine.
 *
 * @author Sindre Mehus
 */
public class WikiTag extends BodyTagSupport {

    private static final RenderContext RENDER_CONTEXT = new BaseRenderContext();
    private static final RenderEngine RENDER_ENGINE = new BaseRenderEngine();

    private String text;

    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    public int doEndTag() throws JspException {
        String result = RENDER_ENGINE.render(StringEscapeUtils.unescapeXml(text), RENDER_CONTEXT);

        try {
            pageContext.getOut().print(result);
        } catch (IOException x) {
            throw new JspTagException(x);
        }
        return EVAL_PAGE;
    }

    public void release() {
        text = null;
        super.release();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

package net.sourceforge.subsonic.domain;

import net.sourceforge.subsonic.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

/**
 * A music index is a mapping from an index string to a list of prefixes.  A complete index consists of a list of
 * <code>MusicIndex</code> instances.<p/>
 *
 * For a normal alphabetical index, such a mapping would typically be <em>"A" -&gt; ["A"]</em>.  The index can also be used
 * to group less frequently used letters, such as  <em>"X-&Aring;" -&gt; ["X", "Y", "Z", "&AElig;", "&Oslash;", "&Aring;"]</em>, or to make multiple
 * indexes for frequently used letters, such as <em>"SA" -&gt; ["SA"]</em> and <em>"SO" -&gt; ["SO"]</em><p/>
 *
 * Clicking on an index in the user interface will typically bring up a list of all music files that are categorized
 * under that index.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.9 $ $Date: 2005/12/15 19:04:32 $
 */
public class MusicIndex {
    private static final Logger LOG = Logger.getLogger(MusicIndex.class);
    private static final MusicIndex OTHER = new MusicIndex("#");

    private String index;
    private List<String> prefixes = new ArrayList<String>();

    /**
     * Creates a new index with the given index string.
     * @param index The index string, e.g., "A" or "The".
     */
    public MusicIndex(String index) {
        this.index = index;
    }

    /**
     * Adds a prefix to this index. Music files that starts with this prefix will be categorized under this index entry.
     * @param prefix The prefix.
     */
    public void addPrefix(String prefix) {
        prefixes.add(prefix);
    }

    /**
     * Returns the index name.
     * @return The index name.
     */
    public String getIndex() {
        return index;
    }

    /**
     * Returns the list of prefixes.
     * @return The list of prefixes.
     */
    public List<String> getPrefixes() {
        return prefixes;
    }

    /**
     * Returns whether this object is equal to another one.
     * @param o Object to compare to.
     * @return <code>true</code> if, and only if, the other object is a <code>MusicIndex</code> with the same
     * index name as this one.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MusicIndex)) return false;

        final MusicIndex musicIndex = (MusicIndex) o;

        if (index != null ? !index.equals(musicIndex.index) : musicIndex.index != null) return false;

        return true;
    }

    /**
     * Returns a hash code for this object.
     * @return A hash code for this object.
     */
    public int hashCode() {
        return (index != null ? index.hashCode() : 0);
    }

    /**
     * Creates a new instance by parsing the given expression.  The expression consists of an index name, followed by
     * an optional list of one-character prefixes. For example:<p/>
     *
     * The expression <em>"A"</em> will create the index <em>"A" -&gt; ["A"]</em><br/>
     * The expression <em>"The"</em> will create the index <em>"The" -&gt; ["The"]</em><br/>
     * The expression <em>"A(A&Aring;&AElig;)"</em> will create the index <em>"A" -&gt; ["A", "&Aring;", "&AElig;"]</em><br/>
     * The expression <em>"X-Z(XYZ)"</em> will create the index <em>"X-Z" -&gt; ["X", "Y", "Z"]</em>
     *
     * @param expr The expression to parse.
     * @return A new instance.
     */
    public static MusicIndex createIndexFromExpression(String expr) {
        int separatorIndex = expr.indexOf('(');
        if (separatorIndex == -1) {

            MusicIndex index = new MusicIndex(expr);
            index.addPrefix(expr);
            return index;
        }

        MusicIndex index = new MusicIndex(expr.substring(0, separatorIndex));
        String prefixString = expr.substring(separatorIndex + 1, expr.length() - 1);
        for (int i = 0; i < prefixString.length(); i++) {
            index.addPrefix(prefixString.substring(i, i + 1));
        }
        return index;
    }

    /**
     * Creates a list of music indexes by parsing the given expression.  The expression is a space-separated list of
     * sub-expressions, for which the rules described in {@link #createIndexFromExpression} apply.
     * @param expr The expression to parse.
     * @return A list of music indexes.
     */
    public static List<MusicIndex> createIndexesFromExpression(String expr) {
        List<MusicIndex> result = new ArrayList<MusicIndex>();

        StringTokenizer tokenizer = new StringTokenizer(expr, " ");
        while (tokenizer.hasMoreTokens()) {
            MusicIndex index = createIndexFromExpression(tokenizer.nextToken());
            result.add(index);
        }

        return result;
    }

    /**
     * Returns a map from music indexes to lists of music files that are direct children of the given music folder.
     * @param folders The music folders.
     * @param indexes The list of indexes to use when grouping the children.
     * @param ignoredArticles Articles to ignore (typically "The", "El", "Las" etc),
     * @return A map from music indexes to lists of music files that are direct children of this music file.
     * @exception IOException If an I/O error occurs.
     */
    public static Map<MusicIndex, List<MusicFile>> getIndexedChildren(MusicFolder[] folders,
                                                                      final List<MusicIndex> indexes,
                                                                      String[] ignoredArticles ) throws IOException {
        Comparator<MusicIndex> comp = new Comparator<MusicIndex>() {
            public int compare(MusicIndex a, MusicIndex b) {
                return indexes.indexOf(a) - indexes.indexOf(b);
            }
        };
        Map<MusicIndex, List<MusicFile>> result = new TreeMap<MusicIndex, List<MusicFile>>(comp);

        for (MusicFolder folder : folders) {

            MusicFile[] children = new MusicFile(folder.getPath()).getChildren(false, true);
            for (MusicFile child : children) {
                MusicIndex index = getIndex(child, indexes, ignoredArticles);
                List<MusicFile> list = result.get(index);
                if (list == null) {
                    list = new ArrayList<MusicFile>();
                    result.put(index, list);
                }
                list.add(child);
            }
        }

        return result;
    }

    /**
     * Returns the music index to which the given music file belongs.
     * @param musicFile The music file in question.
     * @param indexes List of available indexes.
     * @param ignoredArticles Articles to ignore (typically "The", "El", "Las" etc),
     * @return The music index to which this music file belongs, or {@link MusicIndex#OTHER} if no index applies.
     */
    private static MusicIndex getIndex(MusicFile musicFile, List<MusicIndex> indexes, String[] ignoredArticles) {

        // Remove ignored articles.
        String name = musicFile.getName().toUpperCase();
        for (String article : ignoredArticles) {
            try {
                name = name.replaceFirst('^' + article.toUpperCase() + "\\s+", "");
            } catch (PatternSyntaxException x) {
                LOG.warn("Invalid regexp.", x);
            }
        }

        // Look for applicable index.
        for (MusicIndex index : indexes) {
            List<String> prefixes = index.getPrefixes();
            for (String prefix : prefixes) {
                if (name.startsWith(prefix.toUpperCase())) {
                    return index;
                }
            }
        }
        return MusicIndex.OTHER;
    }
}

package net.sourceforge.subsonic.util;

import java.util.*;

/**
 * Simple implementation of a bounded list. If the maximum size is reached, adding a new element will
 * remove the first element in the list.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2005/05/09 20:01:25 $
 */
public class BoundedList<E> extends LinkedList<E> {
    private int maxSize;

    /**
     * Creates a new bounded list with the given maximum size.
     * @param maxSize The maximum number of elements the list may hold.
     */
    public BoundedList(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Adds an element to the list. If the list is full, the first element is removed.
     * @param e The element to add.
     * @return Always <code>true</code>.
     */
    public boolean add(E e) {
        if (isFull()) {
            removeFirst();
        }
        return super.add(e);
    }

    /**
     * Returns whether the list if full.
     * @return Whether the list is full.
     */
    private boolean isFull() {
        return size() == maxSize;
    }
}

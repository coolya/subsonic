package net.sourceforge.subsonic.domain;

import java.io.*;
import java.util.*;

/**
 * A playlist is a list of music files that are associated to a remote player.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.11 $ $Date: 2006/01/10 22:39:35 $
 */
public class Playlist {

    private List<MusicFile> files = new ArrayList<MusicFile>();
    private boolean repeatEnabled;
    private String name = "(unnamed)";
    private Status status = Status.PLAYING;

    /** The index of the current song, or -1 is the end of the playlist is reached.
     * Note that both the index and the playlist size can be zero. */
    private int index = 0;

    /** Used for undo functionality. */
    private List<MusicFile> filesBackup = new ArrayList<MusicFile>();
    private int indexBackup = 0;

    /**
     * Returns the user-defined name of the playlist.
     * @return The name of the playlist, or <code>null</code> if no name has been assigned.
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * Sets the user-defined name of the playlist.
     * @param name The name of the playlist.
     */
    public synchronized void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the current song in the playlist.
     * @return The current song in the playlist, or <code>null</code> if no current song exists.
     */
    public synchronized MusicFile getCurrentFile() {
        if (index == -1 || index == 0 && size() == 0) {
            setStatus(Status.STOPPED);
            return null;
        } else {
            MusicFile file = files.get(index);

            // Remove file from playlist if it doesn't exist.
            if (!file.exists()) {
                files.remove(index);
                index = Math.max(0, Math.min(index, size() - 1));
                return getCurrentFile();
            }

            return file;
        }
    }

    /**
     * Returns all music files in the playlist.
     * @return All music files in the playlist.
     */
    public synchronized MusicFile[] getFiles() {
        return files.toArray(new MusicFile[0]);
    }

    /**
     * Returns the music file at the given index.
     * @param index The index.
     * @return The music file at the given index.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public synchronized MusicFile getFile(int index) {
        return files.get(index);
    }

    /**
    * Skip to the next song in the playlist.
    */
    public synchronized void next() {
        index++;

        // Reached the end?
        if (index >= size()) {
            index = isRepeatEnabled() ? 0 : -1;
        }
    }

    /**
     * Returns the number of songs in the playlists.
     * @return The number of songs in the playlists.
     */
    public synchronized int size() {
        return files.size();
    }

    /**
     * Returns whether the playlist is empty.
     * @return Whether the playlist is empty.
     */
    public synchronized boolean isEmpty() {
        return files.isEmpty();
    }

    /**
     * Returns the index of the current song.
     * @return The index of the current song, or -1 if the end of the playlist is reached.
     */
    public synchronized int getIndex() {
        return index;
    }

    /**
     * Sets the index of the current song.
     * @param index The index of the current song.
     */
    public synchronized void setIndex(int index) {
        makeBackup();
        this.index = Math.max(0, Math.min(index, size() - 1));
        setStatus(Status.PLAYING);
    }

    /**
     * Adds a music file to the playlist.  If the given file is a directory, all its children
     * will be added recursively.
     * @param file The music file to add.
     * @param append Whether existing songs in the playlist should be kept.
     * @exception IOException If an I/O error occurs.
     */
    public synchronized void addFile(MusicFile file, boolean append) throws IOException {
        makeBackup();
        if (!append) {
            index = 0;
            files.clear();
        }
        Collections.addAll(files, file.getChildren(true));
        setStatus(Status.PLAYING);
    }

    /**
     * Adds a music file to the playlist.  If the given file is a directory, all its children
     * will be added recursively.
     * @param file The music file to add.
     * @exception IOException If an I/O error occurs.
     */
    public synchronized void addFile(MusicFile file) throws IOException {
        addFile(file, true);
    }

    /**
     * Removes the music file at the given index.
     * @param index The playlist index.
     */
    public synchronized void removeFileAt(int index) {
        makeBackup();
        index = Math.max(0, Math.min(index, size() - 1));
        if (this.index > index) {
            this.index--;
        }
        files.remove(index);

        if (index != -1) {
            this.index = Math.max(0, Math.min(this.index, size() - 1));
        }
    }

    /**
     * Clears the playlist.
     */
    public synchronized void clear() {
        makeBackup();
        files.clear();
        index = 0;
    }

    /**
     * Shuffles the playlist.
     */
    public synchronized void shuffle() {
        makeBackup();
        MusicFile currentFile = getCurrentFile();
        Collections.shuffle(files);
        if (currentFile != null) {
            index = files.indexOf(currentFile);
        }
    }

    /**
     * Moves the song at the given index one step up.
     * @param index The playlist index.
     */
    public synchronized void moveUp(int index) {
        makeBackup();
        if (index <= 0 || index >= size()) {
            return;
        }
        Collections.swap(files, index, index - 1);

        if (this.index == index) {
            this.index--;
        } else if (this.index == index - 1) {
            this.index++;
        }
    }

    /**
     * Moves the song at the given index one step down.
     * @param index The playlist index.
     */
    public synchronized void moveDown(int index) {
        makeBackup();
        if (index < 0 || index >= size() - 1) {
            return;
        }
        Collections.swap(files, index, index + 1);

        if (this.index == index) {
            this.index++;
        } else if (this.index == index + 1) {
            this.index--;
        }
    }

    /**
     * Returns whether the playlist is repeating.
     * @return Whether the playlist is repeating.
     */
    public synchronized boolean isRepeatEnabled() {
        return repeatEnabled;
    }

    /**
     * Sets whether the playlist is repeating.
     * @param repeatEnabled Whether the playlist is repeating.
     */
    public synchronized void setRepeatEnabled(boolean repeatEnabled) {
        this.repeatEnabled = repeatEnabled;
    }

    /**
     * Revert the last operation.
     */
    public synchronized void undo() {
        List<MusicFile> filesTmp = new ArrayList<MusicFile>(files);
        int indexTmp = index;

        index = indexBackup;
        files = filesBackup;

        indexBackup = indexTmp;
        filesBackup = filesTmp;
    }

    /**
     * Returns the playlist status.
     * @return The playlist status.
     */
    public synchronized Status getStatus() {
        return status;
    }

    /**
     * Sets the playlist status.
     * @param status The playlist status.
     */
    public synchronized void setStatus(Status status) {
        this.status = status;
        if (index == -1) {
            index = Math.max(0, Math.min(index, size() - 1));
        }
    }

    /**
     * Returns the total length in bytes.
     * @return The total length in bytes.
     */
    public synchronized long length() {
        long length = 0;
        for (MusicFile musicFile : files) {
            length += musicFile.length();
        }
        return length;
    }

    /**
    * Playlist status.
    */
    public enum Status {
        PLAYING,
        STOPPED
    }

    private void makeBackup() {
        filesBackup = new ArrayList<MusicFile>(files);
        indexBackup = index;
    }
}
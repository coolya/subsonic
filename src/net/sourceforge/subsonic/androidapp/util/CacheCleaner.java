package net.sourceforge.subsonic.androidapp.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.service.DownloadFile;
import net.sourceforge.subsonic.androidapp.service.DownloadService;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class CacheCleaner {

    private static final String TAG = CacheCleaner.class.getSimpleName();

    private final Context context;
    private final DownloadService downloadService;

    public CacheCleaner(Context context, DownloadService downloadService) {
        this.context = context;
        this.downloadService = downloadService;
    }

    public void clean() {

        Log.i(TAG, "Starting cache cleaning.");

        if (downloadService == null) {
            Log.e(TAG, "DownloadService not set. Aborting cache cleaning.");
            return;
        }

        try {

            List<File> files = new ArrayList<File>();
            List<File> dirs = new ArrayList<File>();

            findCandidatesForDeletion(FileUtil.getMusicDirectory(context), files, dirs);
            sortByAscendingModificationTime(files);

            Set<File> undeletable = findUndeletableFiles();

            deleteFiles(files, undeletable);
            deleteEmptyDirs(dirs, undeletable);
            Log.i(TAG, "Completed cache cleaning.");

        } catch (RuntimeException x) {
            Log.e(TAG, "Error in cache cleaning.", x);
        }
    }

    private void deleteEmptyDirs(List<File> dirs, Set<File> undeletable) {
        for (File dir : dirs) {
            if (undeletable.contains(dir)) {
                continue;
            }

            // Delete album art if it's the only remaining file.
            File[] children = dir.listFiles();
            if (children.length == 1 && Constants.ALBUM_ART_FILE.equals(children[0].getName())) {
                Util.delete(children[0]);
                children = dir.listFiles();
            }

            // Delete empty directory.
            if (children.length == 0) {
                Util.delete(dir);
            }
        }
    }

    private void deleteFiles(List<File> files, Set<File> undeletable) {
        long cacheSizeBytes = Util.getCacheSizeMB(context) * 1024L * 1024L;

        long bytesUsed = 0L;
        for (File file : files) {
            bytesUsed += file.length();
        }

        Log.i(TAG, "Cache size limit: " + Util.formatBytes(cacheSizeBytes));
        Log.i(TAG, "Cache size before: " + Util.formatBytes(bytesUsed));

        for (File file : files) {
            if (bytesUsed > cacheSizeBytes || file.getName().endsWith(".partial") || file.getName().contains(".partial.")) {
                if (!undeletable.contains(file)) {
                    long size = file.length();
                    if (Util.delete(file)) {
                        bytesUsed -= size;
                    }
                }
            }
        }

        Log.i(TAG, "Cache size after: " + Util.formatBytes(bytesUsed));
    }

    private void findCandidatesForDeletion(File file, List<File> files, List<File> dirs) {
        if (file.isFile()) {
            String name = file.getName();
            boolean isCacheFile = name.endsWith(".partial") || name.contains(".partial.") || name.endsWith(".complete") || name.contains(".complete.");
            if (isCacheFile) {
                files.add(file);
            }
        } else {
            // Depth-first
            for (File child : FileUtil.listFiles(file)) {
                findCandidatesForDeletion(child, files, dirs);
            }
            dirs.add(file);
        }
    }

    private void sortByAscendingModificationTime(List<File> files) {
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File a, File b) {
                if (a.lastModified() < b.lastModified()) {
                    return -1;
                }
                if (a.lastModified() > b.lastModified()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    private Set<File> findUndeletableFiles() {
        Set<File> undeletable = new HashSet<File>(5);

        for (DownloadFile downloadFile : downloadService.getDownloads()) {
            undeletable.add(downloadFile.getPartialFile());
            undeletable.add(downloadFile.getCompleteFile());
        }

        undeletable.add(FileUtil.getMusicDirectory(context));
        return undeletable;
    }
}

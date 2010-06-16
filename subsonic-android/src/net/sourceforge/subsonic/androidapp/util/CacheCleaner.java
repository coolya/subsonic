package net.sourceforge.subsonic.androidapp.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
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
        List<File> files = new ArrayList<File>();
        List<File> dirs = new ArrayList<File>();

        findCandidatesForDeletion(FileUtil.getMusicDirectory(), files, dirs);
        sortByAscendingModificationTime(files);

        Set<File> undeletable = findUndeletableFiles();

        deleteFiles(files, undeletable);
        deleteEmptyDirs(dirs, undeletable);
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

        for (File file : files) {
            if (bytesUsed < cacheSizeBytes) {
                break;
            }

            if (!undeletable.contains(file)) {
                long size = file.length();
                if (Util.delete(file)) {
                    bytesUsed -= size;
                }
            }
        }
    }

    private void findCandidatesForDeletion(File file, List<File> files, List<File> dirs) {
        if (file.isFile()) {
            String name = file.getName();
            boolean isCacheFile = name.endsWith(".partial") || name.endsWith(".complete");
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

        DownloadFile currentDownload = downloadService.getCurrentDownloading();
        if (currentDownload != null) {
            undeletable.add(currentDownload.getPartialFile());
            undeletable.add(currentDownload.getCompleteFile());
        }
        DownloadFile currentPlaying = downloadService.getCurrentPlaying();
        if (currentPlaying != null) {
            undeletable.add(currentPlaying.getPartialFile());
            undeletable.add(currentPlaying.getCompleteFile());
        }

        undeletable.add(FileUtil.getMusicDirectory());
        return undeletable;
    }
}

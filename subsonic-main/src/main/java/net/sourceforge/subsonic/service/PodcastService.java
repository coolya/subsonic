package net.sourceforge.subsonic.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.dao.PodcastDao;
import net.sourceforge.subsonic.domain.MusicFileInfo;
import net.sourceforge.subsonic.domain.PodcastChannel;
import net.sourceforge.subsonic.domain.PodcastEpisode;
import net.sourceforge.subsonic.domain.PodcastStatus;
import net.sourceforge.subsonic.util.StringUtil;

/**
 * Provides services for Podcast reception.
 *
 * @author Sindre Mehus
 */
public class PodcastService {

    private static final Logger LOG = Logger.getLogger(PodcastService.class);
    private static final DateFormat RSS_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
    private static final Namespace[] ITUNES_NAMESPACES = {Namespace.getNamespace("http://www.itunes.com/DTDs/Podcast-1.0.dtd"),
            Namespace.getNamespace("http://www.itunes.com/dtds/podcast-1.0.dtd")};

    private final ExecutorService refreshExecutor;
    private final ExecutorService downloadExecutor;
    private final ScheduledExecutorService scheduledExecutor;
    private ScheduledFuture<?> scheduledRefresh;
    private PodcastDao podcastDao;
    private SettingsService settingsService;
    private SecurityService securityService;
    private MusicInfoService musicInfoService;

    public PodcastService() {
        ThreadFactory threadFactory = new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        };
        refreshExecutor = Executors.newFixedThreadPool(5, threadFactory);
        downloadExecutor = Executors.newFixedThreadPool(3, threadFactory);
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    public synchronized void init() {
        // Clean up partial downloads.
        for (PodcastChannel channel : getAllChannels()) {
            for (PodcastEpisode episode : getEpisodes(channel.getId(), false)) {
                if (episode.getStatus() == PodcastStatus.DOWNLOADING) {
                    deleteEpisode(episode.getId(), false);
                    LOG.info("Deleted Podcast episode '" + episode.getTitle() + "' since download was interrupted.");
                }
            }
        }

        schedule();
    }

    public synchronized void schedule() {
        Runnable task = new Runnable() {
            public void run() {
                LOG.info("Starting scheduled Podcast refresh.");
                refreshAllChannels(true);
                LOG.info("Completed scheduled Podcast refresh.");
            }
        };

        if (scheduledRefresh != null) {
            scheduledRefresh.cancel(true);
        }

        int hoursBetween = settingsService.getPodcastUpdateInterval();

        if (hoursBetween == -1) {
            LOG.info("Automatic Podcast update disabled.");
            return;
        }

        long periodMillis = hoursBetween * 60L * 60L * 1000L;
        long initialDelayMillis = 5L * 60L * 1000L;

        scheduledRefresh = scheduledExecutor.scheduleAtFixedRate(task, initialDelayMillis, periodMillis, TimeUnit.MILLISECONDS);
        Date firstTime = new Date(System.currentTimeMillis() + initialDelayMillis);
        LOG.info("Automatic Podcast update scheduled to run every " + hoursBetween + " hour(s), starting at " + firstTime);
    }

    /**
     * Creates a new Podcast channel.
     *
     * @param url The URL of the Podcast channel.
     */
    public void createChannel(String url) {
        PodcastChannel channel = new PodcastChannel(url);
        int channelId = podcastDao.createChannel(channel);

        refreshChannels(Arrays.asList(getChannel(channelId)), true);
    }

    private PodcastChannel getChannel(int channelId) {
        for (PodcastChannel channel : getAllChannels()) {
            if (channelId == channel.getId()) {
                return channel;
            }
        }
        return null;
    }

    /**
     * Returns all Podcast channels.
     *
     * @return Possibly empty list of all Podcast channels.
     */
    public List<PodcastChannel> getAllChannels() {
        return podcastDao.getAllChannels();
    }

    /**
     * Returns all Podcast episodes for a given channel.
     *
     * @param channelId      The Podcast channel ID.
     * @param includeDeleted Whether to include logically deleted episodes in the result.
     * @return Possibly empty list of all Podcast episodes for the given channel, sorted in
     *         reverse chronological order (newest episode first).
     */
    public List<PodcastEpisode> getEpisodes(int channelId, boolean includeDeleted) {
        List<PodcastEpisode> all = podcastDao.getEpisodes(channelId);
        if (includeDeleted) {
            return all;
        }

        List<PodcastEpisode> filtered = new ArrayList<PodcastEpisode>();
        for (PodcastEpisode episode : all) {
            if (episode.getStatus() != PodcastStatus.DELETED) {
                filtered.add(episode);
            }
        }
        return filtered;
    }

    public PodcastEpisode getEpisode(int episodeId, boolean includeDeleted) {
        PodcastEpisode episode = podcastDao.getEpisode(episodeId);
        if (episode == null) {
            return null;
        }
        if (episode.getStatus() == PodcastStatus.DELETED && !includeDeleted) {
            return null;
        }
        return episode;
    }

    private PodcastEpisode getEpisode(int channelId, String url) {
        if (url == null) {
            return null;
        }

        for (PodcastEpisode episode : getEpisodes(channelId, true)) {
            if (url.equals(episode.getUrl())) {
                return episode;
            }
        }
        return null;
    }

    public void refreshAllChannels(boolean downloadEpisodes) {
        refreshChannels(getAllChannels(), downloadEpisodes);
    }

    private void refreshChannels(final List<PodcastChannel> channels, final boolean downloadEpisodes) {
        for (final PodcastChannel channel : channels) {
            Runnable task = new Runnable() {
                public void run() {
                    doRefreshChannel(channel, downloadEpisodes);
                }
            };
            refreshExecutor.submit(task);
        }
    }

    @SuppressWarnings({"unchecked"})
    private void doRefreshChannel(PodcastChannel channel, boolean downloadEpisodes) {
        InputStream in = null;
        try {
            channel.setStatus(PodcastStatus.DOWNLOADING);
            channel.setErrorMessage(null);
            podcastDao.updateChannel(channel);

            URL url = new URL(channel.getUrl());
            in = url.openStream();
            Document document = new SAXBuilder().build(in);
            Element channelElement = document.getRootElement().getChild("channel");

            channel.setTitle(channelElement.getChildTextTrim("title"));
            channel.setDescription(channelElement.getChildTextTrim("description"));
            channel.setStatus(PodcastStatus.COMPLETED);
            channel.setErrorMessage(null);
            podcastDao.updateChannel(channel);

            refreshEpisodes(channel, channelElement.getChildren("item"));

        } catch (Exception x) {
            LOG.warn("Failed to get/parse RSS file for Podcast channel " + channel.getUrl(), x);
            channel.setStatus(PodcastStatus.ERROR);
            channel.setErrorMessage(x.toString());
            podcastDao.updateChannel(channel);
        } finally {
            IOUtils.closeQuietly(in);
        }

        if (downloadEpisodes) {
            for (final PodcastEpisode episode : getEpisodes(channel.getId(), false)) {
                if (episode.getStatus() == PodcastStatus.NEW && episode.getUrl() != null) {
                    downloadEpisode(episode);
                }
            }
        }
    }

    public void downloadEpisode(final PodcastEpisode episode) {
        Runnable task = new Runnable() {
            public void run() {
                doDownloadEpisode(episode);
            }
        };
        downloadExecutor.submit(task);
    }

    private void refreshEpisodes(PodcastChannel channel, List<Element> episodeElements) {

        List<PodcastEpisode> episodes = new ArrayList<PodcastEpisode>();

        for (Element episodeElement : episodeElements) {

            String title = episodeElement.getChildTextTrim("title");
            String duration = getITunesElement(episodeElement, "duration");
            String description = episodeElement.getChildTextTrim("description");
            if (StringUtils.isBlank(description)) {
                description = getITunesElement(episodeElement, "summary");
            }

            Element enclosure = episodeElement.getChild("enclosure");
            if (enclosure == null) {
                LOG.debug("No enclosure found for episode " + title);
                continue;
            }

            String url = enclosure.getAttributeValue("url");
            if (url == null) {
                LOG.debug("No enclosure URL found for episode " + title);
                continue;
            }

            if (getEpisode(channel.getId(), url) == null) {
                Long length = null;
                try {
                    length = new Long(enclosure.getAttributeValue("length"));
                } catch (Exception x) {
                    LOG.warn("Failed to parse enclosure length.", x);
                }

                Date date = null;
                try {
                    date = RSS_DATE_FORMAT.parse(episodeElement.getChildTextTrim("pubDate"));
                } catch (Exception x) {
                    LOG.warn("Failed to parse publish date.", x);
                }
                PodcastEpisode episode = new PodcastEpisode(null, channel.getId(), url, null, title, description, date,
                        duration, length, 0L, PodcastStatus.NEW, null);
                episodes.add(episode);
                LOG.info("Created Podcast episode " + title);
            }
        }

        // Sort episode in reverse chronological order (newest first)
        Collections.sort(episodes, new Comparator<PodcastEpisode>() {
            public int compare(PodcastEpisode a, PodcastEpisode b) {
                long timeA = a.getPublishDate() == null ? 0L : a.getPublishDate().getTime();
                long timeB = b.getPublishDate() == null ? 0L : b.getPublishDate().getTime();

                if (timeA < timeB) {
                    return 1;
                }
                if (timeA > timeB) {
                    return -1;
                }
                return 0;
            }
        });

        // Create episodes in database, skipping the proper number of episodes.
        int downloadCount = settingsService.getPodcastEpisodeDownloadCount();
        if (downloadCount == -1) {
            downloadCount = Integer.MAX_VALUE;
        }

        for (int i = 0; i < episodes.size(); i++) {
            PodcastEpisode episode = episodes.get(i);
            if (i >= downloadCount) {
                episode.setStatus(PodcastStatus.SKIPPED);
            }
            podcastDao.createEpisode(episode);
        }
    }

    private String getITunesElement(Element element, String childName) {
        for (Namespace ns : ITUNES_NAMESPACES) {
            String value = element.getChildTextTrim(childName, ns);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private void doDownloadEpisode(PodcastEpisode episode) {
        InputStream in = null;
        OutputStream out = null;

        if (getEpisode(episode.getId(), false) == null) {
            LOG.info("Podcast " + episode.getUrl() + " was deleted. Aborting download.");
            return;
        }

        try {
            PodcastChannel channel = getChannel(episode.getChannelId());

            URL url = new URL(episode.getUrl());
            in = url.openStream();
            File file = getFile(channel, episode);
            out = new FileOutputStream(file);

            episode.setStatus(PodcastStatus.DOWNLOADING);
            episode.setBytesDownloaded(0L);
            episode.setErrorMessage(null);
            episode.setPath(file.getPath());
            podcastDao.updateEpisode(episode);

            LOG.info("Starting to download Podcast from " + episode.getUrl());

            byte[] buffer = new byte[4096];
            long bytesDownloaded = 0;
            int n;
            long nextLogCount = 30000L;

            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
                bytesDownloaded += n;

                if (bytesDownloaded > nextLogCount) {
                    episode.setBytesDownloaded(bytesDownloaded);
                    nextLogCount += 30000L;
                    if (getEpisode(episode.getId(), false) == null) {
                        break;
                    }
                    podcastDao.updateEpisode(episode);
                }
            }

            if (getEpisode(episode.getId(), false) == null) {
                LOG.info("Podcast " + episode.getUrl() + " was deleted. Aborting download.");
                IOUtils.closeQuietly(out);
                file.delete();
            } else {
                episode.setBytesDownloaded(bytesDownloaded);
                podcastDao.updateEpisode(episode);
                LOG.info("Downloaded " + bytesDownloaded + " bytes from Podcast " + episode.getUrl());
                IOUtils.closeQuietly(out);
                episode.setStatus(PodcastStatus.COMPLETED);
                podcastDao.updateEpisode(episode);
                deleteObsoleteEpisodes(channel);
            }

        } catch (Exception x) {
            LOG.warn("Failed to download Podcast from " + episode.getUrl(), x);
            episode.setStatus(PodcastStatus.ERROR);
            episode.setErrorMessage(x.toString());
            podcastDao.updateEpisode(episode);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    private synchronized void deleteObsoleteEpisodes(PodcastChannel channel) {
        int episodeCount = settingsService.getPodcastEpisodeRetentionCount();
        if (episodeCount == -1) {
            return;
        }

        List<PodcastEpisode> episodes = getEpisodes(channel.getId(), false);

        // Don't do anything if other episodes of the same channel is currently downloading.
        for (PodcastEpisode episode : episodes) {
            if (episode.getStatus() == PodcastStatus.DOWNLOADING) {
                return;
            }
        }

        // Reverse array to get chronological order (oldest episodes first).
        Collections.reverse(episodes);

        int episodesToDelete = Math.max(0, episodes.size() - episodeCount);
        for (int i = 0; i < episodesToDelete; i++) {
            deleteEpisode(episodes.get(i).getId(), true);
            LOG.info("Deleted old Podcast episode " + episodes.get(i).getUrl());
        }
    }

    private synchronized File getFile(PodcastChannel channel, PodcastEpisode episode) {

        File podcastDir = new File(settingsService.getPodcastFolder());
        File channelDir = new File(podcastDir, StringUtil.fileSystemSafe(channel.getTitle()));

        if (!channelDir.exists()) {
            boolean ok = channelDir.mkdirs();
            if (!ok) {
                throw new RuntimeException("Failed to create directory " + channelDir);
            }
            MusicFileInfo info = musicInfoService.getMusicFileInfoForPath(channelDir.getPath());
            if (info == null) {
                musicInfoService.createMusicFileInfo(new MusicFileInfo(channelDir.getPath()));
                info = musicInfoService.getMusicFileInfoForPath(channelDir.getPath());
            }
            info.setEnabled(true);
            info.setComment(channel.getDescription());
            musicInfoService.updateMusicFileInfo(info);
        }

        String filename = StringUtil.getUrlFile(episode.getUrl());
        if (filename == null) {
            filename = episode.getTitle();
        }
        filename = StringUtil.fileSystemSafe(filename);
        String extension = FilenameUtils.getExtension(filename);
        filename = FilenameUtils.removeExtension(filename);
        if (StringUtils.isBlank(extension)) {
            extension = "mp3";
        }

        File file = new File(channelDir, filename + "." + extension);
        for (int i = 0; file.exists(); i++) {
            file = new File(channelDir, filename + i + "." + extension);
        }

        if (!securityService.isWriteAllowed(file)) {
            throw new SecurityException("Access denied to file " + file);
        }
        return file;
    }

    /**
     * Deletes the Podcast channel with the given ID.
     *
     * @param channelId The Podcast channel ID.
     */
    public void deleteChannel(int channelId) {
        // Delete all associated episodes (in case they have files that need to be deleted).
        List<PodcastEpisode> episodes = getEpisodes(channelId, false);
        for (PodcastEpisode episode : episodes) {
            deleteEpisode(episode.getId(), false);
        }
        podcastDao.deleteChannel(channelId);
    }

    /**
     * Deletes the Podcast episode with the given ID.
     *
     * @param episodeId     The Podcast episode ID.
     * @param logicalDelete Whether to perform a logical delete by setting the
     *                      episode status to {@link PodcastStatus#DELETED}.
     */
    public void deleteEpisode(int episodeId, boolean logicalDelete) {
        PodcastEpisode episode = podcastDao.getEpisode(episodeId);
        if (episode == null) {
            return;
        }

        // Delete file.
        if (episode.getPath() != null) {
            File file = new File(episode.getPath());
            if (file.exists()) {
                file.delete();
                // TODO: Delete directory if empty?
            }
        }

        if (logicalDelete) {
            episode.setStatus(PodcastStatus.DELETED);
            episode.setErrorMessage(null);
            podcastDao.updateEpisode(episode);
        } else {
            podcastDao.deleteEpisode(episodeId);
        }
    }

    public void setPodcastDao(PodcastDao podcastDao) {
        this.podcastDao = podcastDao;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMusicInfoService(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
    }
}

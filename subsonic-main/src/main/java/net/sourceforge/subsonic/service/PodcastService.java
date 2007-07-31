package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.dao.PodcastDao;
import net.sourceforge.subsonic.domain.PodcastChannel;
import net.sourceforge.subsonic.domain.PodcastEpisode;
import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;
import java.util.concurrent.*;

/**
 * Provides services for Podcast reception.
 *
 * @author Sindre Mehus
 */
public class PodcastService {

    private static final Logger LOG = Logger.getLogger(PodcastService.class);
    private static final DateFormat RSS_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
    private static final Namespace ITUNES_NAMESPACE = Namespace.getNamespace("http://www.itunes.com/DTDs/Podcast-1.0.dtd");

    private final ExecutorService refreshExecutor;
    private final ExecutorService downloadExecutor;
    private final ScheduledExecutorService scheduledExecutor;
    private ScheduledFuture<?> scheduledRefresh;
    private PodcastDao podcastDao;
    private SettingsService settingsService;

    // TODO: Create podcast settings.


    public PodcastService() {
        refreshExecutor = Executors.newSingleThreadExecutor();
        downloadExecutor = Executors.newFixedThreadPool(3);
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    public synchronized void schedule() {
        Runnable task = new Runnable() {
            public void run() {
                refresh(true);
            }
        };

        if (scheduledRefresh != null) {
            scheduledRefresh.cancel(true);
        }

        int hoursBetween = settingsService.getPodcastUpdateInterval();
        int hour = settingsService.getPodcastUpdateHour();

        if (hoursBetween == -1) {
            LOG.info("Automatic Podcast update disabled.");
            return;
        }

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        if (cal.getTime().before(now)) {
            cal.add(Calendar.DATE, 1);
        }

        Date firstTime = cal.getTime();
        long periodMillis = hoursBetween * 60L * 1000L;
        long initialDelayMillis = firstTime.getTime() - now.getTime();

        scheduledRefresh = scheduledExecutor.scheduleAtFixedRate(task, initialDelayMillis, periodMillis, TimeUnit.MILLISECONDS);
        LOG.info("Automatic Podcast update scheduleds to run every " + hoursBetween + " hour(s), starting at " + firstTime);
    }

    /**
     * Creates a new Podcast channel.
     *
     * @param url The URL of the Podcast channel.
     */
    public void createChannel(String url) {
        PodcastChannel channel = new PodcastChannel(url);
        podcastDao.createChannel(channel);

        refresh(false);
    }

    /**
     * Returns all Podcast channels.
     *
     * @return Possibly empty array of all Podcast channels.
     */
    public PodcastChannel[] getAllChannels() {
        return podcastDao.getAllChannels();
    }

    /**
     * Returns all Podcast episodes for a given channel.
     *
     * @return Possibly empty array of all Podcast episodes for the given channel.
     */
    public PodcastEpisode[] getEpisodes(int channelId) {
        return podcastDao.getEpisodes(channelId);
    }

    public PodcastEpisode getEpisode(int channelId, String url) {
        if (url == null) {
            return null;
        }

        PodcastEpisode[] episodes = getEpisodes(channelId);
        for (PodcastEpisode episode : episodes) {
            if (url.equals(episode.getUrl())) {
                return episode;
            }
        }
        return null;
    }

    public void refresh(final boolean downloadEpisodes) {
        Runnable task = new Runnable() {
            public void run() {
                doRefresh(downloadEpisodes);
            }
        };
        refreshExecutor.submit(task);
    }

    private void doRefresh(boolean downloadEpisodes) {
        for (PodcastChannel channel : getAllChannels()) {

            InputStream in = null;
            try {
                URL url = new URL(channel.getUrl());
                in = url.openStream();
                Document document = new SAXBuilder().build(in);
                Element channelElement = document.getRootElement().getChild("channel");

                channel.setTitle(channelElement.getChildTextTrim("title"));
                channel.setDescription(channelElement.getChildTextTrim("description"));
                podcastDao.updateChannel(channel);

                refreshEpisodes(channel, channelElement.getChildren("item"));

            } catch (Exception x) {
                LOG.warn("Failed to get/parse RSS file for Podcast channel " + channel.getUrl(), x);
            } finally {
                IOUtils.closeQuietly(in);
            }
        }

        if (downloadEpisodes) {
            for (final PodcastChannel channel : getAllChannels()) {
                for (final PodcastEpisode episode : getEpisodes(channel.getId())) {
                    if (episode.getStatus() == PodcastEpisode.Status.NEW && episode.getUrl() != null) {
                        Runnable task = new Runnable() {
                            public void run() {
                                downloadEpisode(channel, episode);
                            }
                        };
                        downloadExecutor.submit(task);
                    }
                }
            }
        }
    }

    private void refreshEpisodes(PodcastChannel channel, List<Element> episodeElements) {

        for (Element episodeElement : episodeElements) {

            String title = episodeElement.getChildTextTrim("title");
            String description = episodeElement.getChildTextTrim("description");
            String duration = episodeElement.getChildTextTrim("duration", ITUNES_NAMESPACE);

            Element enclosure = episodeElement.getChild("enclosure");
            String url = enclosure.getAttributeValue("url");
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
                                                            duration, length, PodcastEpisode.Status.NEW);
                podcastDao.createEpisode(episode);
                LOG.info("Created Podcast episode " + title);
            }
        }
    }

    private void downloadEpisode(PodcastChannel channel, PodcastEpisode episode) {
        episode.setStatus(PodcastEpisode.Status.DOWNLOADING);
        podcastDao.updateEpisode(episode);

        InputStream in = null;
        OutputStream out = null;

        try {

            URL url = new URL(episode.getUrl());
            in = url.openStream();
            File file = getFile(channel, episode);
            out = new FileOutputStream(file);

            // TODO: Cancel download if episode is deleted.
            LOG.info("Starting to download Podcast from " + episode.getUrl());
            int n = IOUtils.copy(in, out);
            LOG.info("Downloaded " + n + " bytes from Podcast " + episode.getUrl());

            episode.setPath(file.getPath());
            episode.setStatus(PodcastEpisode.Status.DOWNLOADED);
            podcastDao.updateEpisode(episode);
        } catch (Exception x) {
            LOG.warn("Failed to download Podcast from " + episode.getUrl(), x);
            episode.setStatus(PodcastEpisode.Status.ERROR);
            podcastDao.updateEpisode(episode);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    private File getFile(PodcastChannel channel, PodcastEpisode episode) {

        // TODO: Make sure it's allowed to write to given directory.
        File podcastDir = new File("c:/podcasts"); // TODO
        File channelDir = new File(podcastDir, channel.getTitle()); // TODO: Make title file-system safe.
        channelDir.mkdirs();

        return new File(channelDir, episode.getTitle() + ".mp3");
    }

    /**
     * Deletes the Podcast channel with the given ID.
     *
     * @param channelId The Podcast channel ID.
     */
    public void deleteChannel(int channelId) {
        podcastDao.deleteChannel(channelId);
    }

    /**
     * Deletes the Podcast episode with the given ID.
     *
     * @param episodeId The Podcast episode ID.
     */
    public void deleteEpisode(int episodeId) {
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

        podcastDao.deleteEpisode(episodeId);
    }

    public void setPodcastDao(PodcastDao podcastDao) {
        this.podcastDao = podcastDao;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}

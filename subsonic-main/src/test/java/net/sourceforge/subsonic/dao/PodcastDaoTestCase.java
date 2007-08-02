package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.domain.PodcastChannel;
import net.sourceforge.subsonic.domain.PodcastEpisode;

import java.util.Date;

/**
 * Unit test of {@link PodcastDao}.
 *
 * @author Sindre Mehus
 */
public class PodcastDaoTestCase extends DaoTestCaseBase {

    protected void setUp() throws Exception {
        getJdbcTemplate().execute("delete from podcast_channel");
    }

    public void testCreateChannel() {
        PodcastChannel channel = new PodcastChannel("http://foo");
        podcastDao.createChannel(channel);

        PodcastChannel newChannel = podcastDao.getAllChannels()[0];
        assertNotNull("Wrong ID.", newChannel.getId());
        assertChannelEquals(channel, newChannel);
    }

    public void testChannelId() {
        int channelId = podcastDao.createChannel(new PodcastChannel("http://foo"));

        assertEquals("Error in createChannel.", channelId + 1, podcastDao.createChannel(new PodcastChannel("http://foo")));
        assertEquals("Error in createChannel.", channelId + 2, podcastDao.createChannel(new PodcastChannel("http://foo")));
        assertEquals("Error in createChannel.", channelId + 3, podcastDao.createChannel(new PodcastChannel("http://foo")));

        podcastDao.deleteChannel(channelId + 1);
        assertEquals("Error in createChannel.", channelId + 4, podcastDao.createChannel(new PodcastChannel("http://foo")));

        podcastDao.deleteChannel(channelId + 4);
        assertEquals("Error in createChannel.", channelId + 5, podcastDao.createChannel(new PodcastChannel("http://foo")));
    }

    public void testUpdateChannel() {
        PodcastChannel channel = new PodcastChannel("http://foo");
        podcastDao.createChannel(channel);
        channel = podcastDao.getAllChannels()[0];

        channel.setUrl("http://bar");
        channel.setTitle("Title");
        channel.setDescription("Description");

        podcastDao.updateChannel(channel);
        PodcastChannel newChannel = podcastDao.getAllChannels()[0];

        assertEquals("Wrong ID.", channel.getId(), newChannel.getId());
        assertChannelEquals(channel, newChannel);
    }

    public void testDeleteChannel() {
        assertEquals("Wrong number of channels.", 0, podcastDao.getAllChannels().length);

        PodcastChannel channel = new PodcastChannel("http://foo");
        podcastDao.createChannel(channel);
        assertEquals("Wrong number of channels.", 1, podcastDao.getAllChannels().length);

        podcastDao.createChannel(channel);
        assertEquals("Wrong number of channels.", 2, podcastDao.getAllChannels().length);

        podcastDao.deleteChannel(podcastDao.getAllChannels()[0].getId());
        assertEquals("Wrong number of channels.", 1, podcastDao.getAllChannels().length);

        podcastDao.deleteChannel(podcastDao.getAllChannels()[0].getId());
        assertEquals("Wrong number of channels.", 0, podcastDao.getAllChannels().length);
    }

    public void testCreateEpisode() {
        int channelId = createChannel();
        PodcastEpisode episode = new PodcastEpisode(null, channelId, "http://bar", "path", "title", "description",
                                                    new Date(), "12:34", null, null, PodcastEpisode.Status.NEW);
        podcastDao.createEpisode(episode);

        PodcastEpisode newEpisode = podcastDao.getEpisodes(channelId)[0];
        assertNotNull("Wrong ID.", newEpisode.getId());
        assertEpisodeEquals(episode, newEpisode);
    }

    public void testGetEpisode() {
        assertNull("Error in getEpisode()", podcastDao.getEpisode(23));

        int channelId = createChannel();
        PodcastEpisode episode = new PodcastEpisode(null, channelId, "http://bar", "path", "title", "description",
                                                    new Date(), "12:34", 3276213L, 2341234L, PodcastEpisode.Status.NEW);
        podcastDao.createEpisode(episode);

        int episodeId = podcastDao.getEpisodes(channelId)[0].getId();
        PodcastEpisode newEpisode = podcastDao.getEpisode(episodeId);
        assertEpisodeEquals(episode, newEpisode);
    }

    public void testGetEpisodes() {
        int channelId = createChannel();
        PodcastEpisode a = new PodcastEpisode(null, channelId, "a", null, null, null,
                                              new Date(3000), null, null, null, PodcastEpisode.Status.NEW);
        PodcastEpisode b = new PodcastEpisode(null, channelId, "b", null, null, null,
                                              new Date(1000), null, null, null, PodcastEpisode.Status.NEW);
        PodcastEpisode c = new PodcastEpisode(null, channelId, "c", null, null, null,
                                              new Date(2000), null, null, null, PodcastEpisode.Status.NEW);
        PodcastEpisode d = new PodcastEpisode(null, channelId, "c", null, null, null,
                                              null, null, null, null, PodcastEpisode.Status.NEW);
        podcastDao.createEpisode(a);
        podcastDao.createEpisode(b);
        podcastDao.createEpisode(c);
        podcastDao.createEpisode(d);

        PodcastEpisode[] episodes = podcastDao.getEpisodes(channelId);
        assertEquals("Error in getEpisodes().", 4, episodes.length);
        assertEpisodeEquals(a,  episodes[0]);
        assertEpisodeEquals(c,  episodes[1]);
        assertEpisodeEquals(b,  episodes[2]);
        assertEpisodeEquals(d,  episodes[3]);
    }


    public void testUpdateEpisode() {
        int channelId = createChannel();
        PodcastEpisode episode = new PodcastEpisode(null, channelId, "http://bar", null, null, null,
                                                    null, null, null, null, PodcastEpisode.Status.NEW);
        podcastDao.createEpisode(episode);
        episode = podcastDao.getEpisodes(channelId)[0];

        episode.setUrl("http://bar");
        episode.setPath("c:/tmp");
        episode.setTitle("Title");
        episode.setDescription("Description");
        episode.setPublishDate(new Date());
        episode.setDuration("1:20");
        episode.setBytesTotal(87628374612L);
        episode.setBytesDownloaded(9086L);
        episode.setStatus(PodcastEpisode.Status.DOWNLOADING);

        podcastDao.updateEpisode(episode);
        PodcastEpisode newEpisode = podcastDao.getEpisodes(channelId)[0];
        assertEquals("Wrong ID.", episode.getId(), newEpisode.getId());
        assertEpisodeEquals(episode, newEpisode);
    }

    public void testDeleteEpisode() {
        int channelId = createChannel();

        assertEquals("Wrong number of episodes.", 0, podcastDao.getEpisodes(channelId).length);

        PodcastEpisode episode = new PodcastEpisode(null, channelId, "http://bar", null, null, null,
                                                    null, null, null, null, PodcastEpisode.Status.NEW);

        podcastDao.createEpisode(episode);
        assertEquals("Wrong number of episodes.", 1, podcastDao.getEpisodes(channelId).length);

        podcastDao.createEpisode(episode);
        assertEquals("Wrong number of episodes.", 2, podcastDao.getEpisodes(channelId).length);

        podcastDao.deleteEpisode(podcastDao.getEpisodes(channelId)[0].getId());
        assertEquals("Wrong number of episodes.", 1, podcastDao.getEpisodes(channelId).length);

        podcastDao.deleteEpisode(podcastDao.getEpisodes(channelId)[0].getId());
        assertEquals("Wrong number of episodes.", 0, podcastDao.getEpisodes(channelId).length);
    }


    public void testCascadingDelete() {
        int channelId = createChannel();
        PodcastEpisode episode = new PodcastEpisode(null, channelId, "http://bar", null, null, null,
                                                    null, null, null, null, PodcastEpisode.Status.NEW);
        podcastDao.createEpisode(episode);
        podcastDao.createEpisode(episode);
        assertEquals("Wrong number of episodes.", 2, podcastDao.getEpisodes(channelId).length);

        podcastDao.deleteChannel(channelId);
        assertEquals("Wrong number of episodes.", 0, podcastDao.getEpisodes(channelId).length);
    }

    private int createChannel() {
        PodcastChannel channel = new PodcastChannel("http://foo");
        podcastDao.createChannel(channel);
        channel = podcastDao.getAllChannels()[0];return channel.getId();
    }

    private void assertChannelEquals(PodcastChannel expected, PodcastChannel actual) {
        assertEquals("Wrong URL.", expected.getUrl(), actual.getUrl());
        assertEquals("Wrong title.", expected.getTitle(), actual.getTitle());
        assertEquals("Wrong description.", expected.getDescription(), actual.getDescription());
    }

    private void assertEpisodeEquals(PodcastEpisode expected, PodcastEpisode actual) {
        assertEquals("Wrong URL.", expected.getUrl(), actual.getUrl());
        assertEquals("Wrong path.", expected.getPath(), actual.getPath());
        assertEquals("Wrong title.", expected.getTitle(), actual.getTitle());
        assertEquals("Wrong description.", expected.getDescription(), actual.getDescription());
        assertEquals("Wrong date.", expected.getPublishDate(), actual.getPublishDate());
        assertEquals("Wrong duration.", expected.getDuration(), actual.getDuration());
        assertEquals("Wrong bytes total.", expected.getBytesTotal(), actual.getBytesTotal());
        assertEquals("Wrong bytes downloaded.", expected.getBytesDownloaded(), actual.getBytesDownloaded());
        assertEquals("Wrong status.", expected.getStatus(), actual.getStatus());
    }

}

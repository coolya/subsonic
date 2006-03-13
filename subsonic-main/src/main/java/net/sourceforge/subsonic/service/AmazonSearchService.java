package net.sourceforge.subsonic.service;

import com.amazon.webservices.AWSECommerceService.*;
import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;

import java.util.*;
import java.util.List;

/**
 * Provides services for searching for resources at Amazon.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.2 $ $Date: 2005/10/06 19:43:27 $
 */
public class AmazonSearchService {

    private static final Logger LOG = Logger.getLogger(AmazonSearchService.class);
    private static final String AMAZON_SUBSCRIPTION_ID = "095SQ9JEK68AYDWNKPR2";
    private static final String AMAZON_ASSOCIATE_ID    = "subsonic-20";

    private AWSECommerceServicePortType webService;

    /**
     * Returns a list of URLs of cover art images from Amazon for the given artist and album.
     * @param artist The artist to search for.
     * @param album The album to search for.
     * @return A list of URLs of cover art images from Amazon.com.
     * @throws Exception If anything goes wrong.
     */
    public String[] getCoverArtImages(String artist, String album) throws Exception {
        long t0 = System.currentTimeMillis();

        List<Item> items = search(artist, album, "Images");
        List<String> result = new ArrayList<String>();

        for (Item item : items) {
            Image image = item.getLargeImage();
            if (image != null) {
                result.add(image.getURL());
            }
        }

        long t1 = System.currentTimeMillis();
        LOG.info("Found " + result.size() + " cover image(s) at Amazon.com in " + (t1 - t0) + " ms.");

        return result.toArray(new String[0]);
    }

    /**
     * Returns a list of album info from Amazon for the given artist and album.  The returned array is sorted by relevance.
     * @param artist The artist to search for.
     * @param album The album to search for.
     * @return A list of album info from Amazon.com.
     * @throws Exception If anything goes wrong.
     */
    public AmazonAlbumInfo[] getAlbumInfo(String artist, String album) throws Exception {
        long t0 = System.currentTimeMillis();

        List<Item> items = search(artist, album, "Small", "EditorialReview", "ItemAttributes", "Images");
        SortedSet<AmazonAlbumInfo> result = new TreeSet<AmazonAlbumInfo>();

        for (Item item : items) {
            AmazonAlbumInfo albumInfo = new AmazonAlbumInfo();
            albumInfo.setAsin(item.getASIN());
            albumInfo.setDetailPageUrl(item.getDetailPageURL());

            ItemAttributes itemAttributes = item.getItemAttributes();
            if (itemAttributes == null) {
                continue;
            }
            albumInfo.setArtists(itemAttributes.getArtist());
            albumInfo.setAlbum(itemAttributes.getTitle());
            albumInfo.setFormats(itemAttributes.getFormat());
            albumInfo.setLabel(itemAttributes.getLabel());
            albumInfo.setReleaseDate(itemAttributes.getReleaseDate());

            EditorialReviews review = item.getEditorialReviews();
            if (review != null) {
                albumInfo.setEditorialReviews(review.getEditorialReview());
            }

            Image image = item.getMediumImage();
            if (image != null) {
                albumInfo.setImageUrl(image.getURL());
            }

            result.add(albumInfo);
        }

        long t1 = System.currentTimeMillis();
        LOG.info("Found " + result.size() + " album(s) at Amazon.com in " + (t1 - t0) + " ms.");

        return result.toArray(new AmazonAlbumInfo[0]);
    }

    private List<Item> search(String artist, String album, String... responseGroups) throws Exception {
        ItemSearch search = new ItemSearch();
        search.setSubscriptionId(AMAZON_SUBSCRIPTION_ID);
        search.setAssociateTag(AMAZON_ASSOCIATE_ID);

        ItemSearchRequest request = new ItemSearchRequest();
        request.setArtist(artist);
        request.setTitle(album);
        request.setResponseGroup(responseGroups);
        request.setSearchIndex("Music");
        search.setRequest(new ItemSearchRequest[] {request});

        ItemSearchResponse response = getAmazonWebService().itemSearch(search);

        List<Item> result = new ArrayList<Item>();
        Items[] allItems = response.getItems();
        if (allItems == null) {
            allItems = new Items[0];
        }

        for (Items someItems : allItems) {
            Item[] items = someItems.getItem();
            if (items == null) {
                items = new Item[0];
            }
            for (Item item : items) {
                result.add(item);
            }
        }
        return result;
    }

    private synchronized AWSECommerceServicePortType getAmazonWebService() throws Exception {
        if (webService == null) {
            AWSECommerceServiceLocator locator = new AWSECommerceServiceLocator();
            webService = locator.getAWSECommerceServicePort();
        }
        return webService;
    }

}


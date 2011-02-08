/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.service;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

/**
 * Provides services for shortening URLs.
 *
 * @author Sindre Mehus
 */
public class UrlShortenerService {

    private final static String REST_URL = "https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyDCqQ1Xo-2_RBMA4vahxDlTN5xP1u346N0";

    public String shorten(String url) throws Exception {
        HttpPost request = new HttpPost(REST_URL);

        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
        HttpConnectionParams.setSoTimeout(client.getParams(), 10000);

        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("longUrl", url);

            StringEntity entity = new StringEntity(jsonRequest.toString());
            entity.setContentType("application/json");
            request.setEntity(entity);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response = client.execute(request, responseHandler);

            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getString("id");

        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

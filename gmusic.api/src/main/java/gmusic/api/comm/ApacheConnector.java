/*******************************************************************************
 * Copyright (c) 2012 Jens Kristian Villadsen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Jens Kristian Villadsen - initial API and implementation
 ******************************************************************************/
package gmusic.api.comm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.common.base.Strings;
import gmusic.api.interfaces.IGoogleHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class ApacheConnector implements IGoogleHttpClient {
	private final HttpClient httpClient;
	private final HttpContext localContext;
	private final CookieStore cookieStore;
	private boolean isStartup = true;
	private String authorizationToken = null;

	public ApacheConnector() {
		HttpParams params = new BasicHttpParams();
		params.removeParameter("User-Agent");
		params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
		// HttpConnectionParams.setConnectionTimeout(params, 150000);
		// HttpConnectionParams.setSoTimeout(params, socketTimeoutMillis);
		httpClient = new DefaultHttpClient(params);
		cookieStore = new BasicCookieStore();
		localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	private HttpResponse execute(URI uri, HttpRequestBase request) throws IOException, URISyntaxException {
		request.addHeader("Accept-Encoding", "gzip, deflate");
		HttpResponse response = httpClient.execute(adjustAddress(uri, request), localContext);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			EntityUtils.toString(response.getEntity());
			throw new IllegalStateException("Statuscode " + response.getStatusLine().getStatusCode() + " not supported");
		}
		return response;
	}

	@Override
	public final synchronized String dispatchGet(URI address) throws URISyntaxException, IOException {
		return EntityUtils.toString(execute(address, new HttpGet()).getEntity());
	}

	@Override
	public final synchronized String dispatchPost(URI address, FormBuilder form) throws IOException, URISyntaxException {
		HttpPost request = new HttpPost();
		request.setEntity(new ByteArrayEntity(form.getBytes()));

		if (!Strings.isNullOrEmpty(form.getContentType())) {
			request.setHeader("Content-Type", form.getContentType());
		}

		String response = EntityUtils.toString(execute(address, request).getEntity());
		if (!isStartup) {
			return response;
		}
		return setupAuthentication(response);
	}

	private String setupAuthentication(String response) throws IOException, URISyntaxException {
		isStartup = false;
		authorizationToken = Util.extractAuthenticationToken(response);
		return dispatchPost(new URI(HTTPS_PLAY_GOOGLE_COM_MUSIC_LISTEN), FormBuilder.getEmpty());
	}

	private HttpRequestBase adjustAddress(URI address, HttpRequestBase request) throws MalformedURLException, URISyntaxException {
		if (address.toString().startsWith(HTTPS_PLAY_GOOGLE_COM_MUSIC_SERVICES)) {
			address = new URI(address.toURL() + String.format(COOKIE_FORMAT, getCookieValue("xt")));
		}

		request.setURI(address);

		if (authorizationToken != null) {
			request.addHeader(GOOGLE_LOGIN_AUTH_KEY, String.format(GOOGLE_LOGIN_AUTH_VALUE, authorizationToken));
		}
		// if((address.toString().startsWith("https://android.clients.google.com/music/mplay")) && deviceId != null)
		// {
		// request.addHeader("X-Device-ID", deviceId);
		// }

		return request;
	}

	private String getCookieValue(String cookieName) {
		for (Cookie cookie : cookieStore.getCookies()) {
			if (cookie.getName().equals(cookieName)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	@Override
	public String dispatchPost(URI address, String json) throws IOException, URISyntaxException {
		HttpPost request = new HttpPost();
		request.setEntity(new StringEntity(json));
		request.setHeader("Content-Type", "application/json");

		String response = EntityUtils.toString(execute(address, request).getEntity());
		if (!isStartup) {
			return response;
		}
		return setupAuthentication(response);
	}
}

package com.fumbbl.ffb.server.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Kalimar
 */
public class UtilServerHttpClient {

	public static final int CONNECTION_TIMEOUT = 10000;
	public static final String CHARACTER_ENCODING = "UTF-8";

	static {
		Logger.getLogger("org.apache.commons.httpclient.HttpMethodBase").setLevel(Level.OFF);
	}

	public static String fetchPage(String url) throws IOException {

		HttpClientBuilder clientBuilder = getHttpClientBuilder();

		try (CloseableHttpClient client = clientBuilder.build()) {

			HttpGet request = new HttpGet(url);
			request.addHeader("Accept-Encoding", "gzip");

			try (CloseableHttpResponse response = client.execute(request)) {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity, CHARACTER_ENCODING);
			}

		}

	}

	public static String postMultipartXml(String url, String challengeResponse, String resultXml) throws IOException {

		HttpClientBuilder clientBuilder = getHttpClientBuilder();

		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.addTextBody("response", challengeResponse);
		entityBuilder.addBinaryBody("f", resultXml.getBytes(CHARACTER_ENCODING), ContentType.TEXT_XML, "result.xml");

		return post(clientBuilder, url, entityBuilder.build());

	}

	public static String postAuthorizedForm(String url, String challengeResponse, String key, String payload) throws IOException {

		HttpClientBuilder clientBuilder = getHttpClientBuilder();

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("response", challengeResponse));
		params.add(new BasicNameValuePair(key, payload));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
		return post(clientBuilder, url, entity);
	}

	public static String post(String url, File file) throws IOException {

		HttpClientBuilder clientBuilder = getHttpClientBuilder();

		return post(clientBuilder, url, new FileEntity(file));

	}

	private static String post(HttpClientBuilder clientBuilder, String url, HttpEntity entity) throws IOException {
		try (CloseableHttpClient client = clientBuilder.build()) {

			HttpPost request = new HttpPost(url);
			request.setEntity(entity);

			try (CloseableHttpResponse response = client.execute(request)) {
				return EntityUtils.toString(response.getEntity(), CHARACTER_ENCODING);
			}

		}
	}

	private static HttpClientBuilder getHttpClientBuilder() {
		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder.setConnectTimeout(CONNECTION_TIMEOUT);
		requestBuilder.setRedirectsEnabled(true);

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		clientBuilder.setDefaultRequestConfig(requestBuilder.build());
		return clientBuilder;
	}
}

package com.balancedbytes.games.ffb.server.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

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

  public static String decodeHtml(String pSource) {
    String result = pSource.replaceAll("&amp;", "&");
    result = result.replaceAll("&nbsp;", " ");
    return result;
  }

  public static String fetchPage(String url) throws IOException {
    
    RequestConfig.Builder requestBuilder = RequestConfig.custom();
    requestBuilder.setConnectTimeout(CONNECTION_TIMEOUT);
    requestBuilder.setRedirectsEnabled(true);
    
    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    clientBuilder.setDefaultRequestConfig(requestBuilder.build());
    
    try (CloseableHttpClient client = clientBuilder.build()) {

      HttpGet request = new HttpGet(url);
      request.addHeader("Accept-Encoding", "gzip");
      
      try (CloseableHttpResponse response = client.execute(request)) {
        return EntityUtils.toString(response.getEntity(), CHARACTER_ENCODING);
      }

    }
    
  }

  /*
  public static byte[] fetchGzippedPage(String url) throws IOException {
    
    RequestConfig.Builder requestBuilder = RequestConfig.custom();
    requestBuilder.setConnectTimeout(CONNECTION_TIMEOUT);
    requestBuilder.setRedirectsEnabled(true);
    
    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    clientBuilder.setDefaultRequestConfig(requestBuilder.build());
    
    try (CloseableHttpClient client = clientBuilder.build()) {
      
      HttpGet request = new HttpGet(url);
      request.addHeader("Accept-Encoding", "gzip");
      
      try (CloseableHttpResponse response = client.execute(request)) {
        return EntityUtils.toByteArray(response.getEntity());
      }
    
    }

  }
  */

  public static String postMultipartXml(String url, String challengeResponse, String resultXml) throws IOException {

    RequestConfig.Builder requestBuilder = RequestConfig.custom();
    requestBuilder.setConnectTimeout(CONNECTION_TIMEOUT);
    requestBuilder.setRedirectsEnabled(true);
    
    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    clientBuilder.setDefaultRequestConfig(requestBuilder.build());
    
    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
    entityBuilder.addTextBody("response", challengeResponse);
    entityBuilder.addBinaryBody("f", resultXml.getBytes(CHARACTER_ENCODING), ContentType.TEXT_XML, "result.xml");
    
    try (CloseableHttpClient client = clientBuilder.build()) {    

      HttpPost request = new HttpPost(url);
      request.setEntity(entityBuilder.build());
  
      try (CloseableHttpResponse response = client.execute(request)) {
        return EntityUtils.toString(response.getEntity(), CHARACTER_ENCODING);
      }

    }
      
  }
  
}

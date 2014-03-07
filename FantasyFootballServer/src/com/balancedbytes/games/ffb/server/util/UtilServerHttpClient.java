package com.balancedbytes.games.ffb.server.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

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

  public static String fetchPage(String pUrl) throws IOException {
    HttpClient client = new HttpClient();
    client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
    HttpMethod method = new GetMethod(pUrl);
    method.setFollowRedirects(true);
    client.executeMethod(method);
    String responseBody = new String(method.getResponseBody(), CHARACTER_ENCODING);
    method.releaseConnection();
    return responseBody;
  }
  
  public static byte[] fetchGzippedPage(String pUrl) throws IOException {
    HttpClient client = new HttpClient();
    client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
    HttpMethod method = new GetMethod(pUrl);
    method.setFollowRedirects(true);
    method.addRequestHeader("Accept-Encoding", "gzip");
    client.executeMethod(method);
    byte[] responseBody = method.getResponseBody();
    method.releaseConnection();
    return responseBody;
  }
  
}

package com.balancedbytes.games.ffb.server.admin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.balancedbytes.games.ffb.PasswordChallenge;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.util.UtilServerHttpClient;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class AdminConnector {

  private static final String _USAGE = "java com.balancedbytes.games.ffb.server.admin.AdminConnector block\n"
  		+ "java com.balancedbytes.games.ffb.server.admin.AdminConnector close <gameId>\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector concede <gameId> <teamId>\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector delete <gameId>\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector list <status>\n"
      + "  [status being one of: scheduled, starting, active, paused, finished or uploaded]\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector message <message>\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector refresh\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector shutdown\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector schedule <teamHomeId> <teamAwayId>\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector unblock"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector upload <gameId>";

  private static final Pattern _PATTERN_CHALLENGE = Pattern.compile("<challenge>([^<]+)</challenge>");

  public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

    if (!ArrayTool.isProvided(args) || !StringTool.isProvided(args[0])) {

      System.out.println(_USAGE);

    } else {

      Properties serverProperties = new Properties();
      BufferedInputStream in = null;
      try {
        in = new BufferedInputStream(AdminConnector.class.getResourceAsStream("/server.ini")); 
        serverProperties.load(in);
      } finally {
        if (in != null) {
          in.close();
        }
      }
    	
      String adminChallengeUrl = serverProperties.getProperty(IServerProperty.ADMIN_URL_CHALLENGE);
      System.out.println(adminChallengeUrl);
      String adminChallengeXml = UtilServerHttpClient.fetchPage(adminChallengeUrl);
      System.out.println(adminChallengeXml);

      String challenge = null;
      BufferedReader xmlReader = new BufferedReader(new StringReader(adminChallengeXml));
      String line = null;
      while ((line = xmlReader.readLine()) != null) {
        Matcher challengeMatcher = _PATTERN_CHALLENGE.matcher(line);
        if (challengeMatcher.find()) {
          challenge = challengeMatcher.group(1);
          break;
        }
      }
      xmlReader.close();

      byte[] md5Password = PasswordChallenge.fromHexString(serverProperties.getProperty(IServerProperty.ADMIN_PASSWORD));
      String response = PasswordChallenge.createResponse(challenge, md5Password);

      if (AdminServlet.SHUTDOWN.equals(args[0])) {
        String shutdownUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_SHUTDOWN), response);
        System.out.println(shutdownUrl);
        String shutdownXml = UtilServerHttpClient.fetchPage(shutdownUrl);
        System.out.println(shutdownXml);
      }

      if (AdminServlet.REFRESH.equals(args[0])) {
        String refreshUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_REFRESH), response);
        System.out.println(refreshUrl);
        String refreshXml = UtilServerHttpClient.fetchPage(refreshUrl);
        System.out.println(refreshXml);
      }

      if (AdminServlet.BLOCK.equals(args[0])) {
        String blockUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_BLOCK), response);
        System.out.println(blockUrl);
        String blockXml = UtilServerHttpClient.fetchPage(blockUrl);
        System.out.println(blockXml);
      }

      if (AdminServlet.UNBLOCK.equals(args[0])) {
        String blockUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_UNBLOCK), response);
        System.out.println(blockUrl);
        String blockXml = UtilServerHttpClient.fetchPage(blockUrl);
        System.out.println(blockXml);
      }

      if (AdminServlet.LIST.equals(args[0])) {
        String adminListUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_LIST_STATUS), response, args[1]);
        System.out.println(adminListUrl);
        String adminListXml = UtilServerHttpClient.fetchPage(adminListUrl);
        System.out.println(adminListXml);
      }

      if (AdminServlet.CLOSE.equals(args[0])) {
        String closeUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_CLOSE), response, args[1]);
        System.out.println(closeUrl);
        String closeXml = UtilServerHttpClient.fetchPage(closeUrl);
        System.out.println(closeXml);
      }

      if (AdminServlet.CONCEDE.equals(args[0])) {
        String concedeUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_CONCEDE), response, args[1], args[2]);
        System.out.println(concedeUrl);
        String concedeXml = UtilServerHttpClient.fetchPage(concedeUrl);
        System.out.println(concedeXml);
      }

      if (AdminServlet.UPLOAD.equals(args[0])) {
        String uploadUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_UPLOAD), response, args[1]);
        System.out.println(uploadUrl);
        String uploadXml = UtilServerHttpClient.fetchPage(uploadUrl);
        System.out.println(uploadXml);
      }

      if (AdminServlet.DELETE.equals(args[0])) {
        String deleteUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_DELETE), response, args[1], args[2]);
        System.out.println(deleteUrl);
        String deleteXml = UtilServerHttpClient.fetchPage(deleteUrl);
        System.out.println(deleteXml);
      }

      if (AdminServlet.MESSAGE.equals(args[0])) {
        String message = URLEncoder.encode(args[1], "UTF-8");
        String messageUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_MESSAGE), response, message);
        System.out.println(messageUrl);
        String messageXml = UtilServerHttpClient.fetchPage(messageUrl);
        System.out.println(messageXml);
      }

      if (AdminServlet.SCHEDULE.equals(args[0])) {
        String scheduleUrl = StringTool.bind(serverProperties.getProperty(IServerProperty.ADMIN_URL_SCHEDULE), response, args[1], args[2]);
        System.out.println(scheduleUrl);
        String scheduleXml = UtilServerHttpClient.fetchPage(scheduleUrl);
        System.out.println(scheduleXml);
      }

    }

  }

}

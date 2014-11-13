package com.balancedbytes.games.ffb.server.request.fumbbl;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.report.ReportFumbblResultUpload;
import com.balancedbytes.games.ffb.report.ReportList;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.request.ServerRequest;
import com.balancedbytes.games.ffb.server.request.ServerRequestProcessor;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class FumbblRequestUploadResults extends ServerRequest {

  private static final Pattern _PATTERN_RESULT = Pattern.compile("<result>([^<]+)</result>");
  private static final Pattern _PATTERN_DESCRIPTION = Pattern.compile("<description>([^<]+)</description>");

  private GameState fGameState;
  private boolean fUploadSuccessful;
  private String fUploadStatus;
  
  public FumbblRequestUploadResults(GameState pGameState) {
    fGameState = pGameState;
  }

  public GameState getGameState() {
    return fGameState;
  }
  
  public boolean isUploadSuccessful() {
    return fUploadSuccessful;
  }
  
  public String getUploadStatus() {
    return fUploadStatus;
  }
  
  @Override
  public void process(ServerRequestProcessor pRequestProcessor) {
    
    FantasyFootballServer server = pRequestProcessor.getServer();
    String challengeResponse = UtilFumbblRequest.getFumbblAuthChallengeResponseForFumbblUser(server);
    FumbblResult fumbblResult = new FumbblResult(getGameState().getGame());
    String resultXml = fumbblResult.toXml(true);
    server.getDebugLog().log(IServerLogLevel.DEBUG, getGameState().getId(), resultXml);
    setRequestUrl(server.getProperty(IServerProperty.FUMBBL_RESULT));
    server.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_REQUEST, getRequestUrl());
    PostMethod multipartPost = new PostMethod(getRequestUrl());
    multipartPost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
    
    try {
      
      Part[] parts = {
        new StringPart("response", challengeResponse),
        new FilePart("f", new ByteArrayPartSource("result.xml", resultXml.getBytes("UTF-8")))
      };
      multipartPost.setRequestEntity(new MultipartRequestEntity(parts, multipartPost.getParams()));
      HttpClient client = new HttpClient();
      client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
      client.executeMethod(multipartPost);
      String responseXml = new String(multipartPost.getResponseBody(), "UTF-8");
      server.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_RESPONSE, responseXml);

      if (StringTool.isProvided(responseXml)) {
        BufferedReader xmlReader = new BufferedReader(new StringReader(responseXml));
        String line = null;
        while ((line = xmlReader.readLine()) != null) {
          Matcher resultMatcher = _PATTERN_RESULT.matcher(line);
          if (resultMatcher.find()) {
            fUploadSuccessful = "success".equalsIgnoreCase(resultMatcher.group(1));
          }
          Matcher descriptionMatcher = _PATTERN_DESCRIPTION.matcher(line);
          if (descriptionMatcher.find()) {
            fUploadStatus = descriptionMatcher.group(1);
          }
        }
        xmlReader.close();
      }
      
      if (isUploadSuccessful()) {
        getGameState().setStatus(GameStatus.UPLOADED);
        server.getGameCache().queueDbUpdate(getGameState(), true);
        server.getDebugLog().log(IServerLogLevel.WARN, getGameState().getId(), "GAME UPLOADED");
      }

      ReportList reportList = new ReportList();
      reportList.add(new ReportFumbblResultUpload(isUploadSuccessful(), getUploadStatus()));
      UtilServerGame.syncGameModel(getGameState(), reportList, null, null);
      
    } catch (Exception ex) {
      throw new FantasyFootballException(ex);
    } finally {
      multipartPost.releaseConnection();
    }
    
  }
  
}

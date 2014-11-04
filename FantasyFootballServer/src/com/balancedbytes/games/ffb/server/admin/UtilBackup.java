package com.balancedbytes.games.ffb.server.admin;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class UtilBackup {
  
  private static File findBackupFile(FantasyFootballServer pServer, long pGameId) {
    if ((pServer == null) || (pGameId <= 0)) {
      return null;
    }
    String gameIdString = Long.toString(pGameId);
    StringBuilder pathName = new StringBuilder();
    pathName.append(pServer.getProperty(IServerProperty.BACKUP_DIR)).append("/");
    int index = 0;
    for (int i = 7; i > 3; i--) {
      if (gameIdString.length() < i) {
        pathName.append("0");
      } else {
        pathName.append(gameIdString.charAt(index++));
      }
      pathName.append("/");
    }
    pathName.append(gameIdString).append(".").append(pServer.getProperty(IServerProperty.BACKUP_EXTENSION));
    return new File(pathName.toString());
  }
  
  public static boolean save(GameState pGameState) {
    if (pGameState == null) {
      return false;
    }
    File backupFile = findBackupFile(pGameState.getServer(), pGameState.getId());
    backupFile.getParentFile().mkdirs();
    BufferedOutputStream out = null;
    try {
      out = new BufferedOutputStream(new FileOutputStream(backupFile));
      out.write(UtilJson.gzip(pGameState.toJsonValue()));
    } catch (IOException pIoException) {
      pGameState.getServer().getDebugLog().log(pGameState.getId(), pIoException);
      return false;
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException pIoException2) {
          // nothing to be done here
        }
      }
    }
    return true;
  }
  
  public static byte[] loadAsGzip(FantasyFootballServer pServer, long pGameId) {
    if ((pServer == null) || (pGameId <= 0)) {
      return null;
    }
    File backupFile = findBackupFile(pServer, pGameId);
    if (!backupFile.exists()) {
      return null;
    }
    byte[] gzippedJson = new byte[(int) backupFile.length()];
    DataInputStream in = null;
    try {
      in = new DataInputStream(new FileInputStream(backupFile));
      in.readFully(gzippedJson);
    } catch (IOException pIoException) {
      pServer.getDebugLog().log(pGameId, pIoException);
      return null;
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException pIoException2) {
          // nothing to be done here
        }
      }
    }
    return gzippedJson;
  }
  
  public static GameState load(FantasyFootballServer pServer, long pGameId) {
    byte[] gzippedJson = loadAsGzip(pServer, pGameId);
    if (ArrayTool.isProvided(gzippedJson)) {
      JsonValue jsonValue = null; 
      try {
        jsonValue = UtilJson.gunzip(gzippedJson);
      } catch (IOException pIoException) {
        pServer.getDebugLog().log(pGameId, pIoException);
      }
      GameState gameState = null;
      if (jsonValue != null) {
        gameState = new GameState(pServer);
        gameState.initFrom(jsonValue);
        return gameState;
      }
      
    }
    return null;
  }

}

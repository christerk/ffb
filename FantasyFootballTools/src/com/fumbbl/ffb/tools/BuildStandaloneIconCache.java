package com.fumbbl.ffb.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Builds the icons.ini for the client.
 */
public class BuildStandaloneIconCache {
  
  private Properties fIconIniProperties;
  
  private void addIconUrls(File pIconDir, String pBaseUrl, String pBasePath) {
    if ((pIconDir == null) || (pBaseUrl == null) || (pBasePath == null)) {
      return;
    }
    if (!pIconDir.exists() || !pIconDir.isDirectory()) {
      return;
    }
    for (File file : pIconDir.listFiles()) {
      String fileName = file.getName();
      String key = new StringBuilder().append(pBaseUrl).append(fileName).toString();
      String value = new StringBuilder().append(pBasePath).append(fileName).toString();
      fIconIniProperties.setProperty(key, value);
    }
  }
  
  private void collectIcons(File pIconDir, File pIconsIniFile) throws IOException {
    if ((pIconDir == null) || (pIconsIniFile == null)) {
      return;
    }
    fIconIniProperties = new Properties();
    addIconUrls(
        new File(pIconDir, "players/iconsets"),
        "http://localhost:2224/icons/players/iconsets/",
        "players/iconsets/"
    );
    addIconUrls(
        new File(pIconDir, "players/portraits"),
        "http://localhost:2224/icons/players/portraits/",
        "players/portraits/"
    );
    fIconIniProperties.setProperty("http://localhost:2224/icons/pitches/basic.zip", "pitches/basic.zip");
    fIconIniProperties.setProperty("http://localhost:2224/icons/pitches/default.zip", "pitches/default.zip");
    if (fIconIniProperties.size() > 0) {
      try (FileOutputStream fileOutputStream = new FileOutputStream(pIconsIniFile);
           Writer out = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)) {
        fIconIniProperties.store(out, null);
      }
    }
    UtilFile.sortPropertyFile(pIconsIniFile);
  }
  
  public static void main(String[] args) {
    if ((args == null) || (args.length < 2)) {
      System.out.println("java com.balancedbytes.games.ffb.tools.BuildStandaloneIconCache <iconDir> <iconsIniFile>");
      return;
    }
    BuildStandaloneIconCache buildIconsIni = new BuildStandaloneIconCache();
    try {
      buildIconsIni.collectIcons(new File(args[0]), new File(args[1]));
    } catch (Exception pAnyException) {
      pAnyException.printStackTrace();
    }
  }

}

package com.balancedbytes.games.ffb.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.model.RosterPosition;
import com.balancedbytes.games.ffb.xml.XmlHandler;

/**
 * Ergebnis eines XML Imports.
 */
public class BuildFumbblIconCache {
  
	// division overview via https://fumbbl.com/xml:roster
  private static final String _URL_DIVISION = "https://fumbbl.com/xml:roster?division=$1&server=test";
  private static final String _URL_ROSTER = "https://fumbbl.com/xml:roster?id=$1&server=test";
  
  private static final String _PATH_PORTRAITS = "players/portraits/";
  private static final String _PATH_ICONSETS = "players/iconsets/";
  
  private Map<String, Integer> collectRosterIds(int pDivision) throws IOException, SAXException, ParserConfigurationException {

    Map<String, Integer> rosterIdByName = new HashMap<String, Integer>();
    String responseXml = loadPage(StringTool.bind(_URL_DIVISION, pDivision));
    if (StringTool.isProvided(responseXml)) {

      SAXParserFactory xmlParserFactory = SAXParserFactory.newInstance();
      xmlParserFactory.setNamespaceAware(false);
      XMLReader xmlReader = xmlParserFactory.newSAXParser().getXMLReader();
      DivisionContentHandler divisionHandler = new DivisionContentHandler(rosterIdByName);
      xmlReader.setContentHandler(divisionHandler);
      
      try (StringReader stringReader = new StringReader(responseXml);
           BufferedReader xmlIn = new BufferedReader(stringReader)) {
        InputSource inputSource = new InputSource(xmlIn);
        xmlReader.parse(inputSource);
      }
    }
    
    return rosterIdByName;
    
  }
  
  private String loadPage(String pUrl) throws IOException {
    System.out.println("load " + pUrl);
    return UtilHttpClient.fetchPage(pUrl);
  }  
  
  private void collectRosterIconUrls(int pRosterId, Properties pIconCache) throws IOException {    
 
    String responseXml = loadPage(StringTool.bind(_URL_ROSTER, pRosterId));
    if (!StringTool.isProvided(responseXml)) {
      return;
    }

    Roster roster = new Roster();
    try (StringReader stringReader = new StringReader(responseXml);
         BufferedReader xmlIn = new BufferedReader(stringReader)) {
      InputSource inputSource = new InputSource(xmlIn);
      try {
        XmlHandler.parse(null, inputSource, roster);
      } catch (FantasyFootballException pFfe) {
        throw new FantasyFootballException("Error initializing roster id " + pRosterId, pFfe);
      }
    }
    
    for (RosterPosition position : roster.getPositions()) {
      StringBuilder iconName = new StringBuilder();
      if (PlayerType.STAR == position.getType()) {
        iconName.append(transformName(position.getName(), false));
      } else {
        iconName.append(transformName(roster.getName(), true));
        iconName.append("_");
        iconName.append(transformName(position.getName(), true));
      } 
      iconName.append(".png");
      if (StringTool.isProvided(position.getUrlPortrait())) {
        StringBuilder iconUrl = new StringBuilder();
        iconUrl.append(StringTool.print(roster.getBaseIconPath())).append(position.getUrlPortrait());
        pIconCache.setProperty(iconUrl.toString(), _PATH_PORTRAITS + iconName.toString());
      }
      if (StringTool.isProvided(position.getUrlIconSet())) {
        StringBuilder iconUrl = new StringBuilder();
        iconUrl.append(StringTool.print(roster.getBaseIconPath())).append(position.getUrlIconSet());
        pIconCache.setProperty(iconUrl.toString(), _PATH_ICONSETS + iconName.toString());
      }
    }
    
  }
  
  private String transformName(String pName, boolean pToLowerCase) {
    StringBuilder transformed = new StringBuilder();
    if (StringTool.isProvided(pName)) {
      boolean toUpperCase = true;
      for (char character : pName.toCharArray()) {
        if (Character.isWhitespace(character) || (character == '.') || (character == '-')) {
          toUpperCase = true;
        } else if (character == '\'') {
          // just skip this character
        } else {
          if (pToLowerCase || !toUpperCase) {
            transformed.append(Character.toLowerCase(character));
          } else {
            transformed.append(Character.toUpperCase(character));
          }
          toUpperCase = false;
        } 
      }
    }
    return transformed.toString();
  }
    
  public void collectRosterIcons(int pDivision, Properties pIconCache) throws IOException, SAXException, ParserConfigurationException {
    Map<String, Integer> rosterIdByName = collectRosterIds(pDivision);
    for (int rosterId : rosterIdByName.values()) {
      collectRosterIconUrls(rosterId, pIconCache);
    }
  }
  
  public void saveIconCache(File pDownloadDir, Properties pIconCache) throws IOException {
    
    if ((pDownloadDir == null) || (pIconCache == null)) {
      return;
    }

    pDownloadDir.mkdirs();
    
    File iniFile = new File(pDownloadDir, "icons.ini");
    System.out.println("save " + iniFile.getAbsolutePath());
    try (BufferedWriter iniWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(iniFile), StandardCharsets.UTF_8))) {
      pIconCache.store(iniWriter, null);
    }

    UtilFile.sortPropertyFile(iniFile);
    
    for (Object key : pIconCache.keySet()) {
      String iconUrl = key.toString();
      String iconPath = pIconCache.getProperty(iconUrl);
      File iconFile = new File(pDownloadDir, iconPath);
      System.out.println("save " + iconFile.getAbsolutePath());
      URL downloadUrl = new URL(iconUrl);
      BufferedInputStream in = null;
      BufferedOutputStream out = null;
      try {
        in = new BufferedInputStream(downloadUrl.openStream());
        iconFile.getParentFile().mkdirs();
        out = new BufferedOutputStream(new FileOutputStream(iconFile), 1024);
        int bytesRead = 0;
        byte data[] = new byte[1024];
        while((bytesRead = in.read(data,0,1024)) >=0) {
         out.write(data, 0, bytesRead);
        }
      } catch (IOException pIoException) {
        // just continue with the next URL
      } finally {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
      }
    }
  
  }
  
  public static void main(String[] args) {
    if ((args != null) && (args.length > 0)) {
      BuildFumbblIconCache collectPlayerIcons = new BuildFumbblIconCache();
      try {
      	Properties iconCache = new Properties();
        collectPlayerIcons.collectRosterIcons(1, iconCache);  // Ranked
        collectPlayerIcons.collectRosterIcons(3, iconCache);  // Stunty Leeg
        collectPlayerIcons.saveIconCache(new File(args[0]), iconCache);
      } catch (Exception pAnyException) {
        pAnyException.printStackTrace();
      }
    } else {
      System.out.println("java com.balancedbytes.games.ffb.tools.BuildFumbblIconCache <downloadDirectory>");
    }
  }

}

package com.balancedbytes.games.ffb.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Ergebnis eines XML Imports.
 */
public class CollectPlayerIcons {
  
	// division overview via https://fumbbl.com/xml:roster
  private static final String _URL_DIVISION = "https://fumbbl.com/xml:roster?division=$1";
  private static final String _URL_ROSTER = "https://fumbbl.com/xml:roster?id=$1";
  private static final String _BASE_URL = "http://fumbbl.com/";
  
  private Map<String, Integer> collectRosterIds(int pDivision) throws IOException, SAXException, ParserConfigurationException {

    Map<String, Integer> rosterIdByName = new HashMap<String, Integer>();
    String responseXml = UtilHttpClient.fetchPage(StringTool.bind(_URL_DIVISION, pDivision));
    if (StringTool.isProvided(responseXml)) {

      SAXParserFactory xmlParserFactory = SAXParserFactory.newInstance();
      xmlParserFactory.setNamespaceAware(false);
      XMLReader xmlReader = xmlParserFactory.newSAXParser().getXMLReader();
      DivisionContentHandler divisionHandler = new DivisionContentHandler(rosterIdByName);
      xmlReader.setContentHandler(divisionHandler);
      
      BufferedReader xmlIn = new BufferedReader(new StringReader(responseXml));
      InputSource inputSource = new InputSource(xmlIn);
      xmlReader.parse(inputSource);

    }
    
    return rosterIdByName;
    
  }
  
  private List<String> collectRosterIconUrls(int pRosterId) throws IOException, SAXException, ParserConfigurationException {    
 
    List<String> rosterIconUrls = new ArrayList<String>();
    String responseXml = UtilHttpClient.fetchPage(StringTool.bind(_URL_ROSTER, pRosterId));
    if (StringTool.isProvided(responseXml)) {

      SAXParserFactory xmlParserFactory = SAXParserFactory.newInstance();
      xmlParserFactory.setNamespaceAware(false);
      XMLReader xmlReader = xmlParserFactory.newSAXParser().getXMLReader();
      RosterContentHandler rosterHandler = new RosterContentHandler(rosterIconUrls);
      xmlReader.setContentHandler(rosterHandler);
      
      BufferedReader xmlIn = new BufferedReader(new StringReader(responseXml));
      InputSource inputSource = new InputSource(xmlIn);
      xmlReader.parse(inputSource);

    }

    return rosterIconUrls;
    
  }
  
  public void collectRosterIcons(String pDownloadDir, int pDivision) throws IOException, SAXException, ParserConfigurationException {
    Map<String, Integer> rosterIdByName = collectRosterIds(pDivision);
    for (int rosterId : rosterIdByName.values()) {
      List<String> iconUrls = collectRosterIconUrls(rosterId);
      for (String iconUrl : iconUrls) {
        if (iconUrl.startsWith(_BASE_URL)) {
          String iconPath = iconUrl.substring(_BASE_URL.length());
          File iconFile = new File(pDownloadDir, iconPath);
          System.out.println(iconFile.getAbsolutePath());
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
    }
  }
  
  
  
  public static void main(String[] args) {
    if ((args != null) && (args.length > 0)) {
      CollectPlayerIcons collectPlayerIcons = new CollectPlayerIcons();
      try {
      	String downloadDir = args[0];
        collectPlayerIcons.collectRosterIcons(downloadDir, 1);  // Ranked
        collectPlayerIcons.collectRosterIcons(downloadDir, 3);  // Stunty Leeg
      } catch (Exception pAnyException) {
        pAnyException.printStackTrace();
      }
    } else {
      System.out.println("java com.balancedbytes.games.ffb.tools.CollectPlayerIcons <downloadDirectory>");
    }
  }

}

package com.balancedbytes.games.ffb.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Ergebnis eines XML Imports.
 */
public class CollectRosters {
  
	// division overview via https://fumbbl.com/xml:roster
  private static final String _URL_DIVISION = "https://fumbbl.com/xml:roster?division=$1";
  private static final String _URL_ROSTER = "https://fumbbl.com/xml:roster?id=$1";
  
  public void collectRosters(File pDownloadDir, int pDivision) throws IOException, SAXException, ParserConfigurationException {

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
    
    for (String rosterName : rosterIdByName.keySet()) {
    	
    	int rosterId = rosterIdByName.get(rosterName);
      responseXml = UtilHttpClient.fetchPage(StringTool.bind(_URL_ROSTER, rosterId));
      
      if (StringTool.isProvided(responseXml)) {

      	StringBuilder fileName = new StringBuilder();
      	fileName.append(rosterName.toLowerCase().replace(' ', '_')).append(".xml");
      	File targetFile = new File(pDownloadDir, fileName.toString());
        System.out.println(targetFile.getAbsolutePath());
      	BufferedWriter out = new BufferedWriter(new FileWriter(targetFile));
      	out.write(responseXml);
      	out.close();
      	
      }
      
    }
    
  }

  public static void main(String[] args) {
    if ((args != null) && (args.length > 0)) {
    	CollectRosters collectRosters = new CollectRosters();
      try {
      	
      	File downloadDir = new File(args[0], "ranked");
      	downloadDir.mkdirs();
      	collectRosters.collectRosters(downloadDir, 1);
      	
      	downloadDir = new File(args[0], "stunty_leeg");
      	downloadDir.mkdirs();
        collectRosters.collectRosters(downloadDir, 3);
        
      } catch (Exception pAnyException) {
        pAnyException.printStackTrace();
      }
    } else {
      System.out.println("java com.balancedbytes.games.ffb.tools.CollectRosters <downloadDirectory>");
    }
  }

}

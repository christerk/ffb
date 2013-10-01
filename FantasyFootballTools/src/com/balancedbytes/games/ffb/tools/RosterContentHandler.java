package com.balancedbytes.games.ffb.tools;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Kalimar
 */
public class RosterContentHandler extends DefaultHandler {
  
  private static final String _TAG_BASE_ICON_PATH = "baseIconPath";
  private static final String _TAG_PORTRAIT = "portrait";
  private static final String _TAG_HOME = "home";
  private static final String _TAG_AWAY = "away";
  private static final String _ATTRIBUTE_STANDING = "standing";
  private static final String _ATTRIBUTE_MOVING = "moving";
  
  private StringBuilder fValue;
  private List<String> fIconUrls;
  private String fBaseIconPath;

  public RosterContentHandler(List<String> pIconUrls) {
    fValue = new StringBuilder();
    fIconUrls = pIconUrls;
  }

  /**
   * @see org.xml.sax.ContentHandler#characters(char, int, int)
   */
  public void characters(char[] ch, int start, int length) {
    fValue.append(new String(ch, start, length));
  }

  /**
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  public void endElement(String uri, String localName, String qName) {
    if (_TAG_BASE_ICON_PATH.equals(qName)) {     
      fBaseIconPath = fValue.toString().trim();
      // System.out.println("BaseIconPath: " + fBaseIconPath);
    }
    if (_TAG_PORTRAIT.equals(qName)) {
      addIconUrl(fValue.toString().trim());
    }
    fValue = new StringBuilder();
  }

  /**
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  public void startElement(String uri, String localName, String qName, Attributes atts) {
    if (_TAG_HOME.equals(qName) || _TAG_AWAY.equals(qName)) {
      addIconUrl(atts.getValue(_ATTRIBUTE_MOVING));
      addIconUrl(atts.getValue(_ATTRIBUTE_STANDING));
    }
  }
  
  public List<String> getIconUrls() {
    return fIconUrls;
  }
  
  private void addIconUrl(String pRelativeUrl) {
    String iconUrl = UtilUrl.createUrl(fBaseIconPath, pRelativeUrl);
    // System.out.println(pRelativeUrl + " -> " + iconUrl);
    fIconUrls.add(iconUrl);
  }

}

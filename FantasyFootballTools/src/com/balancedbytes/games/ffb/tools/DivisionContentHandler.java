package com.balancedbytes.games.ffb.tools;

import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Kalimar
 */
public class DivisionContentHandler extends DefaultHandler {
  
  private static final String _TAG_RACE = "race";
  private static final String _ATTRIBUTE_ID = "id";
  
  private StringBuilder fValue;
  private int fRaceId;
  private Map<String, Integer> fRosterIdByName;

  public DivisionContentHandler(Map<String, Integer> pRosterIdByName) {
    fValue = new StringBuilder();
    fRosterIdByName = pRosterIdByName;
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
    if (_TAG_RACE.equals(qName) && (fRaceId > 0)) {
      fRosterIdByName.put(fValue.toString().trim(), fRaceId);
    }
    fValue = new StringBuilder();
  }

  /**
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  public void startElement(String uri, String localName, String qName, Attributes atts) {
    if (_TAG_RACE.equals(qName)) {
      fRaceId = 0;
      String idValue = atts.getValue(_ATTRIBUTE_ID);
      if (idValue != null) {
        try {
          fRaceId = Integer.parseInt(idValue);
        } catch (NumberFormatException pNumberFormatException) {
          // fRaceId = 0;
        }
      }
    }
  }
  
  public Map<String, Integer> getIdByRace() {
    return fRosterIdByName;
  }

}

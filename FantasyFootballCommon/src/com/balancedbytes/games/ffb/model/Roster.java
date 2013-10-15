package com.balancedbytes.games.ffb.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class Roster implements IXmlSerializable, IByteArraySerializable, IJsonSerializable {

  public static final String XML_TAG = "roster";

  private static final String _XML_ATTRIBUTE_ID = "id";
  private static final String _XML_ATTRIBUTE_TEAM = "team";

  private static final String _XML_TAG_NAME = "name";
  private static final String _XML_TAG_RE_ROLL_COST = "reRollCost";
  private static final String _XML_TAG_MAX_RE_ROLLS = "maxReRolls";
  private static final String _XML_TAG_BASE_ICON_PATH = "baseIconPath";
  private static final String _XML_TAG_LOGO_URL = "logo";
  private static final String _XML_TAG_RAISED_POSITION_ID = "raisedPositionId";
  private static final String _XML_TAG_APOTHECARY = "apothecary";
  private static final String _XML_TAG_NECROMANCER = "necromancer";
  private static final String _XML_TAG_UNDEAD = "undead";

  private String fId;
  private String fName;
  private int fReRollCost;
  private int fMaxReRolls;
  private String fBaseIconPath;
  private String fLogoUrl;
  private String fRaisedPositionId;
  private boolean fApothecary;
  private boolean fNecromancer;
  private boolean fUndead;

  private RosterPosition fCurrentlyParsedRosterPosition;
  
  private Map<String, RosterPosition> fRosterPositionById;
  private Map<String, RosterPosition> fRosterPositionByName;

  public Roster() {
    fRosterPositionById = new HashMap<String, RosterPosition>();
    fRosterPositionByName = new HashMap<String, RosterPosition>();
    fApothecary = true;
  }

  public String getName() {
    return fName;
  }

  public void setName(String name) {
    fName = name;
  }

  public int getReRollCost() {
    return fReRollCost;
  }

  public void setReRollCost(int reRollCost) {
    fReRollCost = reRollCost;
  }

  public RosterPosition[] getPositions() {
    return fRosterPositionById.values().toArray(new RosterPosition[fRosterPositionById.size()]);
  }

  public RosterPosition getPositionById(String pPositionId) {
    return fRosterPositionById.get(pPositionId);
  }
  
  public RosterPosition getPositionByName(String pPositionName) {
    return fRosterPositionByName.get(pPositionName);
  }

  public int getMaxReRolls() {
    return fMaxReRolls;
  }

  public void setMaxReRolls(int maxReRolls) {
    fMaxReRolls = maxReRolls;
  }

  public String getId() {
    return fId;
  }
  
  public void setId(String pId) {
	  fId = pId;
  }
  
  public RosterPosition getRaisedRosterPosition() {
    return fRosterPositionById.get(fRaisedPositionId);
  }
  
  private void add(RosterPosition pPosition) {
    if (pPosition != null) {
      fRosterPositionById.put(pPosition.getId(), pPosition);
      fRosterPositionByName.put(pPosition.getName(), pPosition);
      pPosition.setRoster(this);
    }
  }

  public String getBaseIconPath() {
    return fBaseIconPath;
  }

  public void setBaseIconPath(String pBaseIconPath) {
    fBaseIconPath = pBaseIconPath;
  }

  public void setLogoUrl(String pLogoUrl) {
    fLogoUrl = pLogoUrl;
  }

  public String getLogoUrl() {
    return fLogoUrl;
  }

  public boolean hasApothecary() {
		return fApothecary;
	}
  
  public void setApothecary(boolean pApothecary) {
		fApothecary = pApothecary;
	}
  
  public boolean hasNecromancer() {
		return fNecromancer;
	}
  
  public void setNecromancer(boolean pNecromancer) {
		fNecromancer = pNecromancer;
	}
  
  public boolean isUndead() {
		return fUndead;
	}
  
  public void setUndead(boolean pUndead) {
		fUndead = pUndead;
	}
  
  // XML serialization

  public void addToXml(TransformerHandler pHandler) {

  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getId());
  	UtilXml.startElement(pHandler, XML_TAG);

    UtilXml.addValueElement(pHandler, _XML_TAG_NAME, getName());
    UtilXml.addValueElement(pHandler, _XML_TAG_RE_ROLL_COST, getReRollCost());
    UtilXml.addValueElement(pHandler, _XML_TAG_MAX_RE_ROLLS, getMaxReRolls());
    UtilXml.addValueElement(pHandler, _XML_TAG_BASE_ICON_PATH, getBaseIconPath());
    UtilXml.addValueElement(pHandler, _XML_TAG_LOGO_URL, getLogoUrl());
    UtilXml.addValueElement(pHandler, _XML_TAG_RAISED_POSITION_ID, fRaisedPositionId);
    UtilXml.addValueElement(pHandler, _XML_TAG_APOTHECARY, hasApothecary());
    UtilXml.addValueElement(pHandler, _XML_TAG_NECROMANCER, hasNecromancer());
    UtilXml.addValueElement(pHandler, _XML_TAG_UNDEAD, isUndead());
 
    for (RosterPosition position : getPositions()) {
    	position.addToXml(pHandler);
    }

    UtilXml.endElement(pHandler, XML_TAG);

  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }  

  public IXmlSerializable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    IXmlSerializable xmlElement = this;
    if (XML_TAG.equals(pXmlTag)) {
    	if (StringTool.isProvided(pXmlAttributes.getValue(_XML_ATTRIBUTE_ID))) {
    		fId = pXmlAttributes.getValue(_XML_ATTRIBUTE_ID).trim();
    	}
    	if (StringTool.isProvided(pXmlAttributes.getValue(_XML_ATTRIBUTE_TEAM))) {
    		fId = pXmlAttributes.getValue(_XML_ATTRIBUTE_TEAM).trim();
    	}
    }
    if (RosterPosition.XML_TAG.equals(pXmlTag)) {
      fCurrentlyParsedRosterPosition = new RosterPosition(null);
      fCurrentlyParsedRosterPosition.startXmlElement(pXmlTag, pXmlAttributes);
      xmlElement = fCurrentlyParsedRosterPosition;
    }
    return xmlElement;
  }

  public boolean endXmlElement(String pXmlTag, String pValue) {
    boolean complete = XML_TAG.equals(pXmlTag);
    if (!complete) {
      if (_XML_TAG_NAME.equals(pXmlTag)) {
        setName(pValue);
      }
      if (_XML_TAG_RE_ROLL_COST.equals(pXmlTag)) {
        setReRollCost(Integer.parseInt(pValue));
      }
      if (_XML_TAG_MAX_RE_ROLLS.equals(pXmlTag)) {
        setMaxReRolls(Integer.parseInt(pValue));
      }
      if (_XML_TAG_BASE_ICON_PATH.equals(pXmlTag)) {
        setBaseIconPath(pValue);
      }
      if (_XML_TAG_LOGO_URL.equals(pXmlTag)) {
        setLogoUrl(pValue);
      }
      if (_XML_TAG_RAISED_POSITION_ID.equals(pXmlTag)) {
        fRaisedPositionId = pValue;
      }
      if (RosterPosition.XML_TAG.equals(pXmlTag)) {
        add(fCurrentlyParsedRosterPosition);
      }
      if (_XML_TAG_APOTHECARY.equals(pXmlTag)) {
      	setApothecary(Boolean.parseBoolean(pValue));
      }
      if (_XML_TAG_NECROMANCER.equals(pXmlTag)) {
      	setNecromancer(Boolean.parseBoolean(pValue));
      }
      if (_XML_TAG_UNDEAD.equals(pXmlTag)) {
      	setUndead(Boolean.parseBoolean(pValue));
      }
    }
    return complete;
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
  	
  	pByteList.addSmallInt(getByteArraySerializationVersion());
    
  	pByteList.addString(getId());
    pByteList.addString(getName());
    pByteList.addInt(getReRollCost());
    pByteList.addByte((byte) getMaxReRolls());
    pByteList.addString(getBaseIconPath());
    pByteList.addString(getLogoUrl());
    pByteList.addString(fRaisedPositionId);
    RosterPosition[] positions = getPositions();
    pByteList.addByte((byte) positions.length);
    if (ArrayTool.isProvided(positions)) {
      for (RosterPosition position : positions) {
        position.addTo(pByteList);
      }
    }
    
    pByteList.addBoolean(hasApothecary());
    pByteList.addBoolean(hasNecromancer());
    pByteList.addBoolean(isUndead());
    
  }

  public int initFrom(ByteArray pByteArray) {
  	
  	int byteArraySerializationVersion = pByteArray.getSmallInt();
    
  	fId = pByteArray.getString();
    setName(pByteArray.getString());
    setReRollCost(pByteArray.getInt());
    setMaxReRolls(pByteArray.getByte());
    setBaseIconPath(pByteArray.getString());
    setLogoUrl(pByteArray.getString());
    fRaisedPositionId = pByteArray.getString();
    int nrOfPositions = pByteArray.getByte();
    for (int i = 0; i < nrOfPositions; i++) {
      RosterPosition position = new RosterPosition(null);
      position.initFrom(pByteArray);
      add(position);
    }
    
    setApothecary(pByteArray.getBoolean());
    setNecromancer(pByteArray.getBoolean());
    setUndead(pByteArray.getBoolean());
    
    return byteArraySerializationVersion;
    
  }

}
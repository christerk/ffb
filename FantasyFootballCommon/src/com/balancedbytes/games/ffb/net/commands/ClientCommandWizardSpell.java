package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandWizardSpell extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_WIZARD_SPELL = "wizardSpell";

  private static final String _XML_TAG_TARGET_COORDINATE = "targetCoordinate";
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  
  private SpecialEffect fWizardSpell;
  private FieldCoordinate fTargetCoordinate;
  
  public ClientCommandWizardSpell() {
    super();
  }

  public ClientCommandWizardSpell(SpecialEffect pWizardSpell) {
  	fWizardSpell = pWizardSpell;
  }

  public ClientCommandWizardSpell(SpecialEffect pWizardSpell, FieldCoordinate pTargetCoordinate) {
  	this(pWizardSpell);
  	fTargetCoordinate = pTargetCoordinate;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_WIZARD_SPELL;
  }
  
  public SpecialEffect getWizardSpell() {
		return fWizardSpell;
	}
  
  public FieldCoordinate getTargetCoordinate() {
		return fTargetCoordinate;
	}

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_WIZARD_SPELL, (getWizardSpell() != null) ? getWizardSpell().getName() : null);
  	UtilXml.startElement(pHandler, getId().getName(), attributes);
  	
  	if (getTargetCoordinate() != null) {
  		attributes = new AttributesImpl();
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getTargetCoordinate().getX());
  		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getTargetCoordinate().getY());
  		UtilXml.startElement(pHandler, _XML_TAG_TARGET_COORDINATE, attributes);
  		UtilXml.endElement(pHandler, _XML_TAG_TARGET_COORDINATE);
  	}
  	
  	UtilXml.endElement(pHandler, getId().getName());
  	
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getWizardSpell() != null) ? getWizardSpell().getId() : 0));
    pByteList.addFieldCoordinate(getTargetCoordinate());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fWizardSpell = SpecialEffect.fromId(pByteArray.getByte());
    fTargetCoordinate = pByteArray.getFieldCoordinate();
    return byteArraySerializationVersion;
  }
      
}

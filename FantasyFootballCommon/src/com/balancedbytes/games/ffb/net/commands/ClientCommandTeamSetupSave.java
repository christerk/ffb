package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandTeamSetupSave extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_NR = "nr";
  private static final String _XML_ATTRIBUTE_SETUP_NAME = "setupName";

  private static final String _XML_TAG_PLAYER = "player";
  private static final String _XML_TAG_COORDINATE = "coordinate";
    
  private String fSetupName;
  private List<Integer> fPlayerNumbers;
  private List<FieldCoordinate> fPlayerCoordinates;
  
  public ClientCommandTeamSetupSave() {
    fPlayerNumbers = new ArrayList<Integer>();
    fPlayerCoordinates = new ArrayList<FieldCoordinate>();
  }

  public ClientCommandTeamSetupSave(String pSetupName, int[] pPlayerNumbers, FieldCoordinate[] pPlayerCoordinates) {
    this();
    fSetupName = pSetupName;
    add(pPlayerNumbers);
    add(pPlayerCoordinates);
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_TEAM_SETUP_SAVE;
  }
  
  public String getSetupName() {
    return fSetupName;
  }
  
  public int[] getPlayerNumbers() {
    int[] playerNumbers = new int[fPlayerNumbers.size()];
    for (int i = 0; i < playerNumbers.length; i++) {
      playerNumbers[i] = fPlayerNumbers.get(i);
    }
    return playerNumbers;
  }
  
  private void add(int pPlayerNumber) {
    fPlayerNumbers.add(pPlayerNumber);
  }

  private void add(int[] pPlayerNumbers) {
    if (ArrayTool.isProvided(pPlayerNumbers)) {
      for (int i = 0; i < pPlayerNumbers.length; i++) {
        add(pPlayerNumbers[i]);
      }
    }
  }
  
  public FieldCoordinate[] getPlayerCoordinates() {
    return fPlayerCoordinates.toArray(new FieldCoordinate[fPlayerCoordinates.size()]);
  }
  
  private void add(FieldCoordinate pPlayerCoordinate) {
    if (pPlayerCoordinate != null) {
      fPlayerCoordinates.add(pPlayerCoordinate);
    }
  }
  
  private void add(FieldCoordinate[] pPlayerCoordinates) {
    if (ArrayTool.isProvided(pPlayerCoordinates)) {
      for (FieldCoordinate playerCoordinate : pPlayerCoordinates) {
        add(playerCoordinate);
      }
    }
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SETUP_NAME, getSetupName());
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    int[] playerNumbers = getPlayerNumbers();
    FieldCoordinate[] playerCoordinates = getPlayerCoordinates();
    if (ArrayTool.isProvided(playerNumbers) && ArrayTool.isProvided(playerCoordinates)) {
      for (int i = 0; i < playerNumbers.length; i++) {
        attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NR, playerNumbers[i]);
        UtilXml.startElement(pHandler, _XML_TAG_PLAYER, attributes);
        if (playerCoordinates[i] != null) {
          UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, playerCoordinates[i].getX());
          UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, playerCoordinates[i].getY());
          UtilXml.addEmptyElement(pHandler, _XML_TAG_COORDINATE, attributes);
        }
        UtilXml.endElement(pHandler, _XML_TAG_PLAYER);
      }
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
    pByteList.addString(getSetupName());
    pByteList.addByteArray(getPlayerNumbers());
    pByteList.addByte((byte) getPlayerCoordinates().length);
    for (int i = 0; i < getPlayerCoordinates().length; i++) {
      pByteList.addFieldCoordinate(getPlayerCoordinates()[i]);
    }
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fSetupName = pByteArray.getString();
    add(pByteArray.getByteArrayAsIntArray());
    int nrOfPlayerCoordinates = pByteArray.getByte();
    for (int i = 0; i < nrOfPlayerCoordinates; i++) {
      add(pByteArray.getFieldCoordinate());
    }
    return byteArraySerializationVersion;
  }

}

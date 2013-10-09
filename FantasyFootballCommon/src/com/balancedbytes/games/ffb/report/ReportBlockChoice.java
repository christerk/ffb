package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.BlockResult;
import com.balancedbytes.games.ffb.BlockResultFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportBlockChoice implements IReport {

  private static final String _XML_ATTRIBUTE_NR_OF_DICE = "nrOfDice";
  private static final String _XML_ATTRIBUTE_BLOCK_ROLL = "blockRoll";
  private static final String _XML_ATTRIBUTE_DICE_INDEX = "diceIndex";
  private static final String _XML_ATTRIBUTE_BLOCK_RESULT = "blockResult";
  private static final String _XML_ATTRIBUTE_DEFENDER_ID = "defenderId";
  
  private int fNrOfDice;
  private int[] fBlockRoll;
  private int fDiceIndex;
  private BlockResult fBlockResult;
  private String fDefenderId;
  
  public ReportBlockChoice() {
    super();
  }
  
  public ReportBlockChoice(int pNrOfDice, int[] pBlockRoll, int pDiceIndex, BlockResult pBlockResult, String pDefenderId) {
    fNrOfDice = pNrOfDice;
    fBlockRoll = pBlockRoll;
    fDiceIndex = pDiceIndex;
    fBlockResult = pBlockResult;
    fDefenderId = pDefenderId;
  }
  
  public ReportId getId() {
    return ReportId.BLOCK_CHOICE;
  }
  
  public int getNrOfDice() {
    return fNrOfDice;
  }
  
  public int[] getBlockRoll() {
    return fBlockRoll;
  }
  
  public int getDiceIndex() {
    return fDiceIndex;
  }
  
  public BlockResult getBlockResult() {
    return fBlockResult;
  }
  
  public String getDefenderId() {
    return fDefenderId;
  }
    
  // transformation
  
  public IReport transform() {
    return new ReportBlockChoice(getNrOfDice(), getBlockRoll(), getDiceIndex(), getBlockResult(), getDefenderId());
  }
  
  // XML serialization
    
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NR_OF_DICE, getNrOfDice());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_BLOCK_ROLL, getBlockRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DICE_INDEX, getDiceIndex());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_BLOCK_RESULT, (getBlockResult() != null) ? getBlockResult().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DEFENDER_ID, getDefenderId());
    UtilXml.addEmptyElement(pHandler, XML_TAG, attributes);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getNrOfDice());
    pByteList.addByteArray(getBlockRoll());
    pByteList.addByte((byte) getDiceIndex());
    pByteList.addByte((byte) ((getBlockResult() != null) ? getBlockResult().getId() : 0)); 
    pByteList.addString(getDefenderId());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fNrOfDice = pByteArray.getByte();
    fBlockRoll = pByteArray.getByteArrayAsIntArray();
    fDiceIndex = pByteArray.getByte();
    fBlockResult = new BlockResultFactory().forId(pByteArray.getByte());
    fDefenderId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.NR_OF_DICE.addTo(jsonObject, fNrOfDice);
    IJsonOption.BLOCK_ROLL.addTo(jsonObject, fBlockRoll);
    IJsonOption.DICE_INDEX.addTo(jsonObject, fDiceIndex);
    IJsonOption.BLOCK_RESULT.addTo(jsonObject, fBlockResult);
    IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
    return jsonObject;
  }
  
  public ReportBlockChoice initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fNrOfDice = IJsonOption.NR_OF_DICE.getFrom(jsonObject);
    fBlockRoll = IJsonOption.BLOCK_ROLL.getFrom(jsonObject);
    fDiceIndex = IJsonOption.DICE_INDEX.getFrom(jsonObject);
    fBlockResult = (BlockResult) IJsonOption.BLOCK_RESULT.getFrom(jsonObject);
    fDefenderId = IJsonOption.DEFENDER_ID.getFrom(jsonObject);
    return this;
  }
  
}

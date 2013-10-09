package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.HeatExhaustion;
import com.balancedbytes.games.ffb.KnockoutRecovery;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ReportTurnEnd implements IReport {

  private static final String _XML_ATTRIBUTE_PLAYER_ID_TOUCHDOWN = "playerIdTouchdown"; 
  
  private static final String _XML_TAG_KNOCKOUT_RECOVERIES = "knockoutRecoveries";
  private static final String _XML_TAG_HEAT_EXHAUSTIONS = "heatExhaustions";
  
  private String fPlayerIdTouchdown;
  private List<KnockoutRecovery> fKnockoutRecoveries;
  private List<HeatExhaustion> fHeatExhaustions;
  
  public ReportTurnEnd() {
    fKnockoutRecoveries = new ArrayList<KnockoutRecovery>();
    fHeatExhaustions = new ArrayList<HeatExhaustion>();
  }

  public ReportTurnEnd(String pPlayerIdTouchdown, KnockoutRecovery[] pKnockoutRecoveries, HeatExhaustion[] pHeatExhaustions) {
    this();
    fPlayerIdTouchdown = pPlayerIdTouchdown;
    add(pKnockoutRecoveries);
    add(pHeatExhaustions);
  }
  
  public ReportId getId() {
    return ReportId.TURN_END;
  }
  
  public String getPlayerIdTouchdown() {
    return fPlayerIdTouchdown;
  }
    
  public KnockoutRecovery[] getKnockoutRecoveries() {
    return fKnockoutRecoveries.toArray(new KnockoutRecovery[fKnockoutRecoveries.size()]);
  }
  
  private void add(KnockoutRecovery pKnockoutRecovery) {
    if (pKnockoutRecovery != null) {
      fKnockoutRecoveries.add(pKnockoutRecovery);
    }
  }
  
  private void add(KnockoutRecovery[] pKnockoutRecoveries) {
    if (ArrayTool.isProvided(pKnockoutRecoveries)) {
      for (KnockoutRecovery knockoutRecovery : pKnockoutRecoveries) {
        add(knockoutRecovery);
      }
    }
  }
  
  public HeatExhaustion[] getHeatExhaustions() {
    return fHeatExhaustions.toArray(new HeatExhaustion[fHeatExhaustions.size()]);
  }
  
  private void add(HeatExhaustion pHeatExhaustion) {
    if (pHeatExhaustion != null) {
      fHeatExhaustions.add(pHeatExhaustion);
    }
  }
  
  private void add(HeatExhaustion[] pHeatExhaustions) {
    if (ArrayTool.isProvided(pHeatExhaustions)) {
      for (HeatExhaustion heatExhaustion : pHeatExhaustions) {
        add(heatExhaustion);
      }
    }
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportTurnEnd(getPlayerIdTouchdown(), getKnockoutRecoveries(), getHeatExhaustions());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID_TOUCHDOWN, getPlayerIdTouchdown());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    KnockoutRecovery[] knockoutRecoveries = getKnockoutRecoveries();
    if (ArrayTool.isProvided(knockoutRecoveries)) {
      UtilXml.startElement(pHandler, _XML_TAG_KNOCKOUT_RECOVERIES);
      for (KnockoutRecovery knockoutRecovery : knockoutRecoveries) {
        knockoutRecovery.addToXml(pHandler);
      }
      UtilXml.endElement(pHandler, _XML_TAG_KNOCKOUT_RECOVERIES);
    }
    HeatExhaustion[] heatExhaustions = getHeatExhaustions();
    if (ArrayTool.isProvided(heatExhaustions)) {
      UtilXml.startElement(pHandler, _XML_TAG_HEAT_EXHAUSTIONS);
      for (HeatExhaustion heatExhaustion : heatExhaustions) {
        heatExhaustion.addToXml(pHandler);
      }
      UtilXml.endElement(pHandler, _XML_TAG_HEAT_EXHAUSTIONS);
    }
    UtilXml.endElement(pHandler, XML_TAG);
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
    pByteList.addString(getPlayerIdTouchdown());
    KnockoutRecovery[] knockoutRecoveries = getKnockoutRecoveries();
    pByteList.addByte((byte) knockoutRecoveries.length);
    if (ArrayTool.isProvided(knockoutRecoveries)) {
      for (KnockoutRecovery knockoutRecovery : knockoutRecoveries) {
        knockoutRecovery.addTo(pByteList);
      }
    }
    HeatExhaustion[] heatExhaustions = getHeatExhaustions();
    pByteList.addByte((byte) heatExhaustions.length);
    if (ArrayTool.isProvided(heatExhaustions)) {
      for (HeatExhaustion heatExhaustion : heatExhaustions) {
        heatExhaustion.addTo(pByteList);
      }
    }
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerIdTouchdown = pByteArray.getString();
    int nrOfRecoveries = pByteArray.getByte();
    for (int i = 0; i < nrOfRecoveries; i++) {
      KnockoutRecovery knockoutRecovery = new KnockoutRecovery();
      knockoutRecovery.initFrom(pByteArray);
      add(knockoutRecovery);
    }
    int nrOfExhaustions = pByteArray.getByte();
    for (int i = 0; i < nrOfExhaustions; i++) {
      HeatExhaustion heatExhaustion = new HeatExhaustion();
      heatExhaustion.initFrom(pByteArray);
      add(heatExhaustion);
    }
    return byteArraySerializationVersion;
  }
  
}

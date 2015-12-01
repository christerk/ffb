package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandJourneymen extends NetCommand {

  private List<Integer> fSlots;
  private List<String> fPositionIds;

  public ClientCommandJourneymen() {
    fSlots = new ArrayList<Integer>();
    fPositionIds = new ArrayList<String>();
  }

  public ClientCommandJourneymen(String[] pPositionsIds, int[] pSlots) {
    this();
    addPositionIds(pPositionsIds);
    addSlots(pSlots);
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_JOURNEYMEN;
  }

  public String[] getPositionIds() {
    return fPositionIds.toArray(new String[fPositionIds.size()]);
  }

  public int[] getSlots() {
    int[] slots = new int[fSlots.size()];
    for (int i = 0; i < slots.length; i++) {
      slots[i] = fSlots.get(i);
    }
    return slots;
  }

  public int getSlotsTotal() {
    int total = 0;
    int[] slots = getSlots();
    for (int i = 0; i < slots.length; i++) {
      total += slots[i];
    }
    return total;
  }

  private void addPositionId(String pPositionId) {
    if (StringTool.isProvided(pPositionId)) {
      fPositionIds.add(pPositionId);
    }
  }

  private void addPositionIds(String[] pPositionIds) {
    if (ArrayTool.isProvided(pPositionIds)) {
      for (String positionId : pPositionIds) {
        addPositionId(positionId);
      }
    }
  }

  private void addSlots(int[] pSlots) {
    if (ArrayTool.isProvided(pSlots)) {
      for (int slots : pSlots) {
        fSlots.add(slots);
      }
    }
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.POSITION_IDS.addTo(jsonObject, fPositionIds);
    IJsonOption.SLOTS.addTo(jsonObject, fSlots);
    return jsonObject;
  }

  public ClientCommandJourneymen initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    addPositionIds(IJsonOption.POSITION_IDS.getFrom(jsonObject));
    addSlots(IJsonOption.SLOTS.getFrom(jsonObject));
    return this;
  }
}

package com.fumbbl.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

/**
 *
 * @author Kalimar
 */
public class ClientCommandJourneymen extends ClientCommand {

	private List<Integer> fSlots;
	private List<String> fPositionIds;

	public ClientCommandJourneymen() {
		fSlots = new ArrayList<>();
		fPositionIds = new ArrayList<>();
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
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.POSITION_IDS.addTo(jsonObject, fPositionIds);
		IJsonOption.SLOTS.addTo(jsonObject, fSlots);
		return jsonObject;
	}

	public ClientCommandJourneymen initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		addPositionIds(IJsonOption.POSITION_IDS.getFrom(game, jsonObject));
		addSlots(IJsonOption.SLOTS.getFrom(game, jsonObject));
		return this;
	}
}

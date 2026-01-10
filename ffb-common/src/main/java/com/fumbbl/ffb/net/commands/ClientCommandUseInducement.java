package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class ClientCommandUseInducement extends ClientCommand {

	private InducementType fInducementType;
	private Card fCard;
	private final List<String> fPlayerIds;

	public ClientCommandUseInducement() {
		fPlayerIds = new ArrayList<>();
	}

	public ClientCommandUseInducement(InducementType pInducementType) {
		this();
		fInducementType = pInducementType;
	}

	public ClientCommandUseInducement(InducementType pInducement, String pPlayerId) {
		this(pInducement);
		addPlayerId(pPlayerId);
	}

	public ClientCommandUseInducement(Card pCard) {
		this();
		fCard = pCard;
	}

	public ClientCommandUseInducement(Card pCard, String pPlayerId) {
		this(pCard);
		addPlayerId(pPlayerId);
	}

	public ClientCommandUseInducement(InducementType pInducement, String[] pPlayerIds) {
		this(pInducement);
		addPlayerIds(pPlayerIds);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_INDUCEMENT;
	}

	public InducementType getInducementType() {
		return fInducementType;
	}

	public Card getCard() {
		return fCard;
	}

	public String[] getPlayerIds() {
		return fPlayerIds.toArray(new String[0]);
	}

	public boolean hasPlayerId(String pPlayerId) {
		return fPlayerIds.contains(pPlayerId);
	}

	private void addPlayerId(String pPlayerId) {
		if (StringTool.isProvided(pPlayerId)) {
			fPlayerIds.add(pPlayerId);
		}
	}

	private void addPlayerIds(String[] pPlayerIds) {
		if (ArrayTool.isProvided(pPlayerIds)) {
			for (String playerId : pPlayerIds) {
				addPlayerId(playerId);
			}
		}
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.INDUCEMENT_TYPE.addTo(jsonObject, fInducementType);
		IJsonOption.PLAYER_IDS.addTo(jsonObject, fPlayerIds);
		IJsonOption.CARD.addTo(jsonObject, fCard);
		return jsonObject;
	}

	public ClientCommandUseInducement initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fInducementType = (InducementType) IJsonOption.INDUCEMENT_TYPE.getFrom(source, jsonObject);
		addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(source, jsonObject));
		fCard = (Card) IJsonOption.CARD.getFrom(source, jsonObject);
		return this;
	}

}

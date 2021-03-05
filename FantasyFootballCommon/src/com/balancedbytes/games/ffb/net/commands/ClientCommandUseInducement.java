package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.InducementType;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 *
 * @author Kalimar
 */
public class ClientCommandUseInducement extends ClientCommand {

	private InducementType fInducementType;
	private Card fCard;
	private List<String> fPlayerIds;

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
		return fPlayerIds.toArray(new String[fPlayerIds.size()]);
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

	public ClientCommandUseInducement initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fInducementType = (InducementType) IJsonOption.INDUCEMENT_TYPE.getFrom(game, jsonObject);
		addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));
		fCard = (Card) IJsonOption.CARD.getFrom(game, jsonObject);
		return this;
	}

}

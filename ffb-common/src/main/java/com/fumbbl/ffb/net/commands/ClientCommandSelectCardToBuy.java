package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.Arrays;

public class ClientCommandSelectCardToBuy extends ClientCommand {
	private Selection selection;

	public ClientCommandSelectCardToBuy() {
		super();
	}

	public ClientCommandSelectCardToBuy(Selection selection) {
		this.selection = selection;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_SELECT_CARD_TO_BUY;
	}

	public Selection getSelection() {
		return selection;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		if (selection != null) {
			IJsonOption.CARD_SELECTION.addTo(jsonObject, selection.name());
		}
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		String selectionName = IJsonOption.CARD_SELECTION.getFrom(source, jsonObject);
		if (selectionName != null) {
			selection = Selection.valueOf(selectionName);
		}
		return this;
	}

	public enum Selection {
		INITIAL_FIRST(true, true),
		INITIAL_SECOND(true, false),
		REROLLED_FIRST(false, true),
		REROLLED_SECOND(false, false)
		;
		private final boolean initialDeckChoice, firstCardChoice;

		Selection(boolean initialDeckChoice, boolean firstCardChoice) {
			this.initialDeckChoice = initialDeckChoice;
			this.firstCardChoice = firstCardChoice;
		}

		public boolean isInitialDeckChoice() {
			return initialDeckChoice;
		}

		public boolean isFirstCardChoice() {
			return firstCardChoice;
		}

		public static Selection valueOf(boolean initialDeckChoice, boolean firstCardChoice) {
			return Arrays.stream(values())
				.filter(value -> value.initialDeckChoice == initialDeckChoice && value.firstCardChoice == firstCardChoice)
				.findFirst().orElse(null);
		}
	}
}

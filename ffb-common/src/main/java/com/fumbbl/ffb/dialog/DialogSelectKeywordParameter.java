package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.KeywordChoiceMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Keyword;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DialogSelectKeywordParameter implements IDialogParameter {

	private final List<Keyword> keywords = new ArrayList<>();
	private String playerId;
	private KeywordChoiceMode keywordChoiceMode;
	private int minSelect, maxSelect;

	public DialogSelectKeywordParameter() {
	}

	public DialogSelectKeywordParameter(String playerId, List<Keyword> keywords, KeywordChoiceMode keywordChoiceMode,
		int minSelect, int maxSelect) {
		this.playerId = playerId;
		this.minSelect = minSelect;
		this.maxSelect = maxSelect;
		this.keywords.addAll(keywords);
		this.keywordChoiceMode = keywordChoiceMode;
	}

	@Override
	public DialogId getId() {
		return DialogId.SELECT_KEYWORD;
	}

	public String getPlayerId() {
		return playerId;
	}

	public List<Keyword> getKeywords() {
		return keywords;
	}

	public KeywordChoiceMode getKeywordChoiceMode() {
		return keywordChoiceMode;
	}

	public int getMinSelect() {
		return minSelect;
	}

	public int getMaxSelect() {
		return maxSelect;
	}

	@Override
	public IDialogParameter transform() {
		return new DialogSelectKeywordParameter(playerId, keywords, keywordChoiceMode, 1, 1);
	}

	@Override
	public IDialogParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		String[] keyWordArray = IJsonOption.KEYWORDS.getFrom(source, jsonObject);
		for (String s : keyWordArray) {
			keywords.add(Keyword.forName(s));
		}

		keywordChoiceMode = KeywordChoiceMode.valueOf(IJsonOption.KEYWORD_CHOICE_MODE.getFrom(source, jsonObject));
		minSelect = IJsonOption.MIN_SELECTS.getFrom(source, jsonObject);
		maxSelect = IJsonOption.MAX_SELECTS.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.KEYWORDS.addTo(jsonObject, keywords.stream().map(Keyword::getName).collect(Collectors.toList()));
		IJsonOption.KEYWORD_CHOICE_MODE.addTo(jsonObject, keywordChoiceMode.name());
		IJsonOption.MIN_SELECTS.addTo(jsonObject, minSelect);
		IJsonOption.MAX_SELECTS.addTo(jsonObject, maxSelect);
		return jsonObject;
	}
}

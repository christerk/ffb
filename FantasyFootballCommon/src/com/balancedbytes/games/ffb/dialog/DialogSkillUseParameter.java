package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Skill;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogSkillUseParameter implements IDialogParameter {

	private String fPlayerId;
	private Skill fSkill;
	private int fMinimumRoll;

	public DialogSkillUseParameter() {
		super();
	}

	public DialogSkillUseParameter(String pPlayerId, Skill pSkill, int pMinimumRoll) {
		fPlayerId = pPlayerId;
		fSkill = pSkill;
		fMinimumRoll = pMinimumRoll;
	}

	public DialogId getId() {
		return DialogId.SKILL_USE;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public Skill getSkill() {
		return fSkill;
	}

	public int getMinimumRoll() {
		return fMinimumRoll;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogSkillUseParameter(getPlayerId(), getSkill(), getMinimumRoll());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.SKILL.addTo(jsonObject, fSkill);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
		return jsonObject;
	}

	public DialogSkillUseParameter initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fSkill = (Skill) IJsonOption.SKILL.getFrom(game, jsonObject);
		fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(game, jsonObject);
		return this;
	}

}

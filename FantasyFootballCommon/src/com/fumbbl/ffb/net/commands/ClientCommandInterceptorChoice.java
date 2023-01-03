package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandInterceptorChoice extends ClientCommand {

	private String fInterceptorId;
	private Skill interceptionSkill;

	public ClientCommandInterceptorChoice() {
		super();
	}

	public ClientCommandInterceptorChoice(String pInterceptorId, Skill interceptionSkill) {
		fInterceptorId = pInterceptorId;
		this.interceptionSkill = interceptionSkill;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_INTERCEPTOR_CHOICE;
	}

	public String getInterceptorId() {
		return fInterceptorId;
	}

	public Skill getInterceptionSkill() {
		return interceptionSkill;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.INTERCEPTOR_ID.addTo(jsonObject, fInterceptorId);
		IJsonOption.SKILL.addTo(jsonObject, interceptionSkill);
		return jsonObject;
	}

	public ClientCommandInterceptorChoice initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fInterceptorId = IJsonOption.INTERCEPTOR_ID.getFrom(source, jsonObject);
		interceptionSkill = (Skill) IJsonOption.SKILL.getFrom(source, jsonObject);
		return this;
	}

}

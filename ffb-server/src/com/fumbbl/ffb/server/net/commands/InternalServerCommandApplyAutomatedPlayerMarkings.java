package com.fumbbl.ffb.server.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.marking.AutoMarkingConfig;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.IServerJsonOption;

public class InternalServerCommandApplyAutomatedPlayerMarkings extends InternalServerCommand {

	private AutoMarkingConfig autoMarkingConfig;

	public InternalServerCommandApplyAutomatedPlayerMarkings(AutoMarkingConfig autoMarkingConfig, long gameId) {
		super(gameId);
		this.autoMarkingConfig = autoMarkingConfig;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.INTERNAL_APPLY_AUTOMATIC_PLAYER_MARKINGS;
	}

	public AutoMarkingConfig getAutoMarkingConfig() {
		return autoMarkingConfig;
	}

// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.AUTO_MARKING_CONFIG.addTo(jsonObject, UtilJson.toJsonObject(autoMarkingConfig.toJsonValue()));

		return jsonObject;
	}

	public InternalServerCommandApplyAutomatedPlayerMarkings initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		autoMarkingConfig = new AutoMarkingConfig().initFrom(source, IServerJsonOption.AUTO_MARKING_CONFIG.getFrom(source, jsonObject));
		return this;
	}
}

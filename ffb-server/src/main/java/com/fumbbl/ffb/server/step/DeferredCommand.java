package com.fumbbl.ffb.server.step;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.IServerJsonOption;

public abstract class DeferredCommand implements IJsonSerializable, INamedObject {
	public abstract void execute(IStep step);

	public abstract DeferredCommandId getId();

	@Override
	public String getName() {
		return getId().getName();
	}

	@Override
	public final DeferredCommand initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		DeferredCommandId idFromJson = (DeferredCommandId) IServerJsonOption.DEFERRED_COMMAND_ID.getFrom(source, jsonObject);
		if (getId() != idFromJson) {
				throw new IllegalStateException("Wrong command id. Expected " + getId().getName() + " received "
					+ ((idFromJson != null) ? idFromJson.getName() : "null"));
			}
		return initChildMember(source, jsonObject);
	}

	@Override
	public final JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.DEFERRED_COMMAND_ID.addTo(jsonObject, getId());
		return addChildMember(jsonObject);
	}

	protected DeferredCommand initChildMember(IFactorySource source, JsonObject jsonObject) {
		return this;
	}

	protected JsonObject addChildMember(JsonObject jsonObject) {
		return jsonObject;
	}
}

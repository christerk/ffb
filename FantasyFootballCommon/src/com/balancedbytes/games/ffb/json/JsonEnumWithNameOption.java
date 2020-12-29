package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.GameRules;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonEnumWithNameOption extends JsonAbstractOption {

	private static GameRules defaultRules;
	private Factory factory;
	
	public JsonEnumWithNameOption(String pKey, Factory factory) {
		super(pKey);
		
		this.factory = factory;
	}

	public INamedObject getFrom(Game game, JsonObject pJsonObject) {
		return asEnumWithName(game, getValueFrom(pJsonObject));
	}

	public void addTo(JsonObject pJsonObject, INamedObject pValue) {
		addValueTo(pJsonObject, asJsonValue(pValue));
	}

	private INamedObject asEnumWithName(Game game, JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		return getFactory(game).forName(pJsonValue.asString());
	}

	private JsonValue asJsonValue(INamedObject pEnumWithName) {
		if (pEnumWithName == null) {
			return JsonValue.NULL;
		}
		return JsonValue.valueOf(pEnumWithName.getName());
	}

	@SuppressWarnings("unchecked")
	private <T extends INamedObjectFactory> T getFactory(Game game) {
		GameRules rules = null;
		if (game != null) {
			rules = game.getRules(); 
		}
		if (rules == null) {
			rules = getDefaultRules();
		}
		return (T) rules.getFactory(factory);
	}

	private GameRules getDefaultRules() {
		if (defaultRules == null) {
			defaultRules = new GameRules(new GameOptions(null)); 
		}
		
		return defaultRules;
	}
	
}

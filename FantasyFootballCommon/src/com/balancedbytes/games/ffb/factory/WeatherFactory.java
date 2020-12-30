package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.WEATHER)
@RulesCollection(Rules.COMMON)
public class WeatherFactory implements INamedObjectFactory {

	public Weather forName(String pName) {
		for (Weather weather : Weather.values()) {
			if (weather.getName().equalsIgnoreCase(pName)) {
				return weather;
			}
		}
		return null;
	}

	public Weather forShortName(String pShortName) {
		for (Weather weather : Weather.values()) {
			if (weather.getShortName().equalsIgnoreCase(pShortName)) {
				return weather;
			}
		}
		return null;
	}

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}

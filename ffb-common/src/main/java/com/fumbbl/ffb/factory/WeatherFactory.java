package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

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
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}

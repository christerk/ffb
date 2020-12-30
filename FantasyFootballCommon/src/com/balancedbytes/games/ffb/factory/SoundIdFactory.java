package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.SOUND_ID)
@RulesCollection(Rules.COMMON)
public class SoundIdFactory implements INamedObjectFactory {

	public SoundId forName(String pName) {
		for (SoundId sound : SoundId.values()) {
			if (sound.getName().equalsIgnoreCase(pName)) {
				return sound;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}

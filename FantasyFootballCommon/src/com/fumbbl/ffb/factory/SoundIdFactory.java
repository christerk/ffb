package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

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

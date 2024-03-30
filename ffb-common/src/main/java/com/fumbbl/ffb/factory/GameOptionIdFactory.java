package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.GAME_OPTION_ID)
@RulesCollection(Rules.COMMON)
public class GameOptionIdFactory implements INamedObjectFactory {

	public GameOptionId forName(String pName) {
		if (StringTool.isProvided(pName)) {
			for (GameOptionId optionId : GameOptionId.values()) {
				if (pName.equalsIgnoreCase(optionId.getName())) {
					return optionId;
				}
			}
			//
			// backwards compatibility (wrong Fumbbl option names)
			//
			if ("maxCards".equalsIgnoreCase(pName)) {
				return GameOptionId.MAX_NR_OF_CARDS;
			}
			if ("cardGold".equalsIgnoreCase(pName)) {
				return GameOptionId.FREE_CARD_CASH;
			}
			if ("freeInducementMoney".equalsIgnoreCase(pName)) {
				return GameOptionId.FREE_INDUCEMENT_CASH;
			}
			if ("wideZonePlayers".equalsIgnoreCase(pName)) {
				return GameOptionId.MAX_PLAYERS_IN_WIDE_ZONE;
			}
			if ("playersOnField".equalsIgnoreCase(pName)) {
				return GameOptionId.MAX_PLAYERS_ON_FIELD;
			}
			if ("playersOnLos".equalsIgnoreCase(pName)) {
				return GameOptionId.MIN_PLAYERS_ON_LOS;
			}
			if ("clawNoStack".equalsIgnoreCase(pName)) {
				return GameOptionId.CLAW_DOES_NOT_STACK;
			}
			if ("pilingOnNoStack".equalsIgnoreCase(pName)) {
				return GameOptionId.PILING_ON_DOES_NOT_STACK;
			}
			if ("pilingOnKoDouble".equalsIgnoreCase(pName)) {
				return GameOptionId.PILING_ON_TO_KO_ON_DOUBLE;
			}
			if ("sneakyAsFoul".equalsIgnoreCase(pName)) {
				return GameOptionId.SNEAKY_GIT_AS_FOUL_GUARD;
			}
			if ("sneakyBanToKo".equalsIgnoreCase(pName)) {
				return GameOptionId.SNEAKY_GIT_BAN_TO_KO;
			}
			if ("standFirmNoFall".equalsIgnoreCase(pName)) {
				return GameOptionId.STAND_FIRM_NO_DROP_ON_FAILED_DODGE;
			}
			if ("rightStuffCancelTackle".equalsIgnoreCase(pName)) {
				return GameOptionId.RIGHT_STUFF_CANCELS_TACKLE;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}

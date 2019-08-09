package com.balancedbytes.games.ffb.util;

import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.option.*;

public final class UtilInducements {

	public static int findInducementCost(Roster pRoster, InducementType pInducement, GameOptions gameOptions) {
		switch (pInducement) {
			case BLOODWEISER_BABES:
				return 50000;
			case BRIBES:
				return (pRoster.getName().equals("Goblin") ? 50000 : 100000);
			case EXTRA_TEAM_TRAINING:
				return 100000;
			case IGOR:
				return 100000;
			case MASTER_CHEF:
				return (pRoster.getName().equals("Halfling") ? 100000 : 300000);
			case WANDERING_APOTHECARIES:
				return 100000;
			case WIZARD:
				return 150000;
			default:
				return 0;
		}
	}

	public static int findInducementsAvailable(Roster pRoster, InducementType pInducement, GameOptions gameOptions) {
		if (InducementType.WIZARD == pInducement) {
			IGameOption wizardOption = gameOptions.getOptionWithDefault(InducementType.WIZARD.getMaxId());
			if (!wizardOption.isChanged()) {
				return ((GameOptionBoolean)gameOptions.getOptionWithDefault(GameOptionId.WIZARD_AVAILABLE)).isEnabled() ? 1 : 0;
			}
		}

		if (InducementType.IGOR == pInducement && pRoster.hasApothecary()) {
			return 0;
		}

		if (InducementType.WANDERING_APOTHECARIES == pInducement && !pRoster.hasApothecary()) {
			return 0;
		}

		IGameOption gameOption = gameOptions.getOptionWithDefault(pInducement.getMaxId());

		if (gameOption instanceof GameOptionInt) {
			return ((GameOptionInt) gameOption).getValue();
		}

		return 0;
	}
}

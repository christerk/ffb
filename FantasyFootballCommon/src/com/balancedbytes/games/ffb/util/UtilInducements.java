package com.balancedbytes.games.ffb.util;

import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.Roster;

public final class UtilInducements {

	public static int findInducementCost(Roster pRoster, InducementType pInducement) {
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

	public static int findInducementsAvailable(Roster pRoster, InducementType pInducement) {
		switch (pInducement) {
			case BLOODWEISER_BABES:
				return 2;
			case BRIBES:
				return 3;
			case EXTRA_TEAM_TRAINING:
				return 4;
			case IGOR:
				return (!pRoster.hasApothecary() ? 1 : 0);
			case MASTER_CHEF:
				return 1;
			case WANDERING_APOTHECARIES:
				return (!pRoster.hasApothecary() ? 0 : 2);
			case WIZARD:
				return 1;
			default:
				return 0;
		}
	}

}

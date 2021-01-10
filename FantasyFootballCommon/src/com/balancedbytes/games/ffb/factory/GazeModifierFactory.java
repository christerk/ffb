package com.balancedbytes.games.ffb.factory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.GazeModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.GAZE_MODIFIER)
@RulesCollection(Rules.COMMON)
public class GazeModifierFactory implements IRollModifierFactory<GazeModifier> {

	public GazeModifier forName(String pName) {
		for (GazeModifier modifier : GazeModifier.values()) {
			if (modifier.getName().equalsIgnoreCase(pName)) {
				return modifier;
			}
		}
		return null;
	}

	public Set<GazeModifier> findGazeModifiers(Game pGame) {
		Set<GazeModifier> gazeModifiers = new HashSet<GazeModifier>();
		Player<?> player = pGame.getActingPlayer().getPlayer();
		if (player != null) {
			GazeModifier tacklezoneModifier = getTacklezoneModifier(pGame, player);
			if (tacklezoneModifier != null) {
				gazeModifiers.add(tacklezoneModifier);
			}
		}
		return gazeModifiers;
	}

	public GazeModifier[] toArray(Set<GazeModifier> pGazeModifierSet) {
		if (pGazeModifierSet != null) {
			GazeModifier[] gazeModifierArray = pGazeModifierSet.toArray(new GazeModifier[pGazeModifierSet.size()]);
			Arrays.sort(gazeModifierArray, new Comparator<GazeModifier>() {
				public int compare(GazeModifier pO1, GazeModifier pO2) {
					return pO1.getName().compareTo(pO2.getName());
				}
			});
			return gazeModifierArray;
		} else {
			return new GazeModifier[0];
		}
	}

	private GazeModifier getTacklezoneModifier(Game pGame, Player<?> pPlayer) {
		int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
		if (tacklezones > 1) {
			for (GazeModifier modifier : GazeModifier.values()) {
				if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones - 1)) {
					return modifier;
				}
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}

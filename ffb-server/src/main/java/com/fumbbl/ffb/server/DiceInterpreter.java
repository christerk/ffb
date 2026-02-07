package com.fumbbl.ffb.server;

import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.DirectionFactory;
import com.fumbbl.ffb.factory.KickoffResultFactory;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.kickoff.KickoffResult;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.GoForItModifier;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class DiceInterpreter {

	private static final DiceInterpreter _INSTANCE = new DiceInterpreter();

	/**
	 * @return the only instance of this class.
	 */
	public static DiceInterpreter getInstance() {
		return _INSTANCE;
	}

	private DiceInterpreter() {
		super();
	}

	public KickoffResult interpretRollKickoff(Game game, int[] roll) {
		int kickoffRoll = roll[0] + roll[1];
		return ((KickoffResultFactory) game.getFactory(Factory.KICKOFF_RESULT)).forRoll(kickoffRoll);
	}

	public Weather interpretRollWeather(int[] roll) {
		int total = roll[0] + roll[1];
		return interpretWeather(total);
	}

	public Weather interpretWeather(int total) {
		switch (total) {
			case 2:
				return Weather.SWELTERING_HEAT;
			case 3:
				return Weather.VERY_SUNNY;
			case 11:
				return Weather.POURING_RAIN;
			case 12:
				return Weather.BLIZZARD;
			default: // 4 - 10
				return Weather.NICE;
		}
	}

	public boolean interpretPickMeUp(int roll) {
		return roll >= 5;
	}

	public int minimumRollGoingForIt(Set<GoForItModifier> pGoForItModifiers) {
		int modifierTotal = 0;
		for (GoForItModifier goForItModifier : pGoForItModifiers) {
			modifierTotal += goForItModifier.getModifier();
		}
		return Math.max(2, 2 + modifierTotal);
	}

	public int minimumRollResistingFoulAppearance() {
		return 2;
	}

	public boolean isPassFumble(int roll, PassingDistance pPassingDistance,
			Set<PassModifier> pPassModifiers) {
		if (roll == 1) {
			return true;
		} else if (roll == 6) {
			return false;
		} else {
			int modifierTotal = 0;
			for (PassModifier passModifier : pPassModifiers) {
				modifierTotal += passModifier.getModifier();
			}
			return ((roll + pPassingDistance.getModifier2016() - modifierTotal) <= 1);
		}
	}

	public boolean isSkillRollSuccessful(int roll, int pMinimumRoll) {
		return ((roll == 6) || ((roll != 1) && (roll >= pMinimumRoll)));
	}

	public boolean isSpecialEffectSuccesful(SpecialEffect pSpecialEffect, Player<?> targetPlayer, int roll) {
		if (pSpecialEffect == SpecialEffect.LIGHTNING) {
			return (roll >= 2);
		} else if (pSpecialEffect == SpecialEffect.ZAP) {
			return (roll == 6 || (roll > 1 && roll >= targetPlayer.getStrengthWithModifiers()));
		} else if ((pSpecialEffect == SpecialEffect.FIREBALL) || (pSpecialEffect == SpecialEffect.BOMB)) {
			return (roll >= 4);
		} else {
			return false;
		}
	}

	public boolean isRegenerationSuccessful(int roll) {
		return (roll >= 4);
	}

	public boolean isAffectedByPitchInvasion(int roll, int pFameOtherTeam) {
		return ((roll > 1) && (roll + pFameOtherTeam >= 6));
	}

	public boolean isRecoveringFromKnockout(int roll, int pBloodweiserBabes) {
		return ((roll > 1) && ((roll + pBloodweiserBabes) > 3));
	}

	public boolean isAlwaysHungrySuccessful(int roll) {
		return (roll >= 2);
	}

	public boolean isEscapeFromAlwaysHungrySuccessful(int roll) {
		return (roll >= 2);
	}

	public boolean isExhausted(int roll) {
		return (roll == 1);
	}

	public boolean isTentaclesEscapeSuccessful(int[] roll, int pTentaclePlayerStrength, int pDodgingPlayerStrength) {
		return (ArrayTool.isProvided(roll) && (roll.length > 1)
				&& ((roll[0] + roll[1]) >= minimumRollTentaclesEscape(pTentaclePlayerStrength, pDodgingPlayerStrength)));
	}

	public int minimumRollTentaclesEscape(int pTentaclePlayerStrength, int pDodgingPlayerStrength) {
		return (6 + pTentaclePlayerStrength - pDodgingPlayerStrength);
	}

	public boolean isShadowingEscapeSuccessful(int[] roll, int pShadowingPlayerMovement, int pDodgingPlayerMovement) {
		return (ArrayTool.isProvided(roll) && (roll.length > 1)
				&& ((roll[0] + roll[1]) >= minimumRollShadowingEscape(pShadowingPlayerMovement, pDodgingPlayerMovement)));
	}

	public int minimumRollShadowingEscape(int pShadowingPlayerMovement, int pDodgingPlayerMovement) {
		return (8 + pShadowingPlayerMovement - pDodgingPlayerMovement);
	}

	public int minimumRollDauntless(int pAttackerStrength, int pDefenderStrength) {
		return Math.min(6, (pDefenderStrength - pAttackerStrength + 1));
	}

	public int minimumRollChainsaw() {
		return 2;
	}

	public int minimumRollProjectileVomit() {
		return 2;
	}

	public int minimumRollConfusion(boolean pGoodConditions) {
		return pGoodConditions ? 2 : 4;
	}

	public int minimumRollBloodLust() {
		return 2;
	}

	public int minimumRollAnimosity() {
		return 2;
	}

	public int minimumRollWeepingDagger() {
		return 4;
	}

	public int interpretFanFactorRoll(int[] pFanFactorRoll, int pFanFactor, int pScoreDiff) {
		int fanFactorModifier = 0;
		int fanFactorTotal = 0;
		if (ArrayTool.isProvided(pFanFactorRoll)) {
			for (int roll : pFanFactorRoll) {
				fanFactorTotal += roll;
			}
		}
		if ((pScoreDiff >= 0) && (fanFactorTotal > pFanFactor)) {
			fanFactorModifier = 1;
		}
		if ((pScoreDiff <= 0) && (fanFactorTotal < pFanFactor)) {
			fanFactorModifier = -1;
		}
		return fanFactorModifier;
	}

	public int interpretMasterChefRoll(int[] pMasterChefRoll) {
		int reRollsStolen = 0;
		if (ArrayTool.isProvided(pMasterChefRoll)) {
			for (int roll : pMasterChefRoll) {
				if (roll > 3) {
					reRollsStolen++;
				}
			}
		}
		return reRollsStolen;
	}

	public boolean isArmourBroken(GameState pGameState, InjuryContext pInjuryContext) {
		Game game = pGameState.getGame();
		StatsMechanic mechanic = (StatsMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.STAT.name());
		int[] armourRoll = pInjuryContext.getArmorRoll();
		Player<?> defender = game.getPlayerById(pInjuryContext.getDefenderId());
		int armour = defender.getArmourWithModifiers();
		if (defender.hasSkillProperty(NamedProperties.preventArmourModifications)) {
			pInjuryContext.clearArmorModifiers();
		}
		return mechanic.armourIsBroken(armour, armourRoll, pInjuryContext, pGameState.getGame());
	}

	public boolean isBribesSuccessful(int roll) {
		return (roll > 1);
	}

	public boolean isArgueTheCallSuccessful(int roll) {
		return (roll > 5);
	}

	public boolean isCoachBanned(int roll) {
		return (roll < 2);
	}

	public boolean isStandUpSuccessful(int roll, int pModifier) {
		return (roll > 1 && roll + pModifier > 3);
	}

	public boolean isPlayerDefecting(int roll) {
		return ((roll > 0) && (roll < 4));
	}

	public Direction interpretScatterDirectionRoll(Game game, int roll) {
		return game.<DirectionFactory>getFactory(Factory.DIRECTION).forRoll(roll);
	}

	public int interpretRiotRoll(int pRiotRoll) {
		return ((pRiotRoll < 4) ? 1 : -1);
	}

	public boolean isDouble(int[] roll) {
		return ((roll != null) && (roll.length == 2) && (roll[0] == roll[1]));
	}

	public CardEffect interpretWitchBrewRoll(int roll) {
		switch (roll) {
		case 1:
			return CardEffect.MAD_CAP_MUSHROOM_POTION;
		case 3:
		case 4:
		case 5:
		case 6:
			return CardEffect.SEDATIVE;
		default:
			return null;
		}
	}

}

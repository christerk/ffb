package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.KickoffResult;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.factory.DirectionFactory;
import com.balancedbytes.games.ffb.factory.KickoffResultFactory;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.mechanics.StatsMechanic;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.GoForItModifier;
import com.balancedbytes.games.ffb.modifiers.PassModifier;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilRangeRuler;

import java.util.Arrays;
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

	public KickoffResult interpretRollKickoff(int[] roll) {
		int kickoffRoll = roll[0] + roll[1];
		return new KickoffResultFactory().forRoll(kickoffRoll);
	}

	public Weather interpretRollWeather(int[] roll) {
		int total = roll[0] + roll[1];
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

	public int minimumRollThrowTeamMate(PassingDistance pPassingDistance,
	                                    Set<PassModifier> pPassModifiers) {
		return UtilRangeRuler.minimumRollThrowTeamMate(pPassingDistance, pPassModifiers);
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
			return (roll == 6 || (roll > 1 && roll >= targetPlayer.getStrength()));
		} else if ((pSpecialEffect == SpecialEffect.FIREBALL) || (pSpecialEffect == SpecialEffect.BOMB)) {
			return (roll >= 4);
		} else {
			return false;
		}
	}

	public boolean isRegenerationSuccessful(int roll) {
		return (roll >= 4);
	}

	public boolean isLonerSuccessful(int roll) {
		return (roll >= 4);
	}

	public boolean isProSuccessful(int roll) {
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
		return (pDefenderStrength - pAttackerStrength + 1);
	}

	public int minimumRollChainsaw() {
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

	public PlayerState interpretRollInjury(GameState pGameState, InjuryContext pInjuryContext) {
		PlayerState playerState = null;
		if ((pGameState != null) && (pInjuryContext != null)) {
			Game game = pGameState.getGame();
			int[] injuryRoll = pInjuryContext.getInjuryRoll();
			Player<?> defender = game.getPlayerById(pInjuryContext.getDefenderId());
			if ((defender != null) && UtilCards.hasCard(game, defender, Card.GOOD_OLD_MAGIC_CODPIECE)) {
				pInjuryContext.clearInjuryModifiers();
			}
			if (injuryRoll == null) {
				// This is a forced injury, for example triggered by the player being eaten
				// We expect an injury being available in the injury context
				playerState = pInjuryContext.getInjury();
			} else {
				boolean isStunty = Arrays.stream(pInjuryContext.getInjuryModifiers()).anyMatch(injuryModifier -> injuryModifier.isRegisteredToSkillWithProperty(NamedProperties.isHurtMoreEasily));
				int total = injuryRoll[0] + injuryRoll[1] + pInjuryContext.getInjuryModifierTotal();
				if ((total == 8) && (defender != null)
						&& defender.hasSkillWithProperty(NamedProperties.convertKOToStunOn8)) {
					playerState = new PlayerState(PlayerState.STUNNED);
					defender.getSkillWithProperty(NamedProperties.convertKOToStunOn8).getInjuryModifiers()
						.forEach(pInjuryContext::addInjuryModifier);
				} else if ((total == 7) && isStunty) {
					playerState = new PlayerState(PlayerState.KNOCKED_OUT);
				} else if ((total == 9) && (defender != null) && isStunty) {
					playerState = new PlayerState(PlayerState.BADLY_HURT);
				} else if (total > 9) {
					playerState = null;
				} else if (total > 7) {
					playerState = new PlayerState(PlayerState.KNOCKED_OUT);
				} else {
					playerState = new PlayerState(PlayerState.STUNNED);
				}
			}
		}
		return playerState;
	}

	public PlayerState interpretRollCasualty(int[] pCasualtyRoll) {
		if (ArrayTool.isProvided(pCasualtyRoll)) {
			switch (pCasualtyRoll[0]) {
			case 6:
				return new PlayerState(PlayerState.RIP);
			case 5:
			case 4:
				return new PlayerState(PlayerState.SERIOUS_INJURY);
			default: // 1 - 3
				return new PlayerState(PlayerState.BADLY_HURT);
			}
		} else {
			return null;
		}
	}

	public SeriousInjury interpretRollSeriousInjury(int[] pCasualtyRoll) {

		// 11-38 Badly Hurt No long term effect
		// 41 Broken Ribs Miss next game
		// 42 Groin Strain Miss next game
		// 43 Gouged Eye Miss next game
		// 44 Broken Jaw Miss next game
		// 45 Fractured Arm Miss next game
		// 46 Fractured Leg Miss next game
		// 47 Smashed Hand Miss next game
		// 48 Pinched Nerve Miss next game
		// 51 Damaged Back Niggling Injury
		// 52 Smashed Knee Niggling Injury
		// 53 Smashed Hip -1 MA
		// 54 Smashed Ankle -1 MA
		// 55 Serious Concussion -1 AV
		// 56 Fractured Skull -1 AV
		// 57 Broken Neck -1 AG
		// 58 Smashed Collar Bone -1 ST
		// 61-68 DEAD Dead!

		SeriousInjury seriousInjury = null;
		switch (pCasualtyRoll[0]) {
		case 4:
			switch (pCasualtyRoll[1]) {
			case 1:
				seriousInjury = SeriousInjury.BROKEN_RIBS;
				break;
			case 2:
				seriousInjury = SeriousInjury.GROIN_STRAIN;
				break;
			case 3:
				seriousInjury = SeriousInjury.GOUGED_EYE;
				break;
			case 4:
				seriousInjury = SeriousInjury.BROKEN_JAW;
				break;
			case 5:
				seriousInjury = SeriousInjury.FRACTURED_ARM;
				break;
			case 6:
				seriousInjury = SeriousInjury.FRACTURED_LEG;
				break;
			case 7:
				seriousInjury = SeriousInjury.SMASHED_HAND;
				break;
			case 8:
				seriousInjury = SeriousInjury.PINCHED_NERVE;
				break;
			}
			break;
		case 5:
			switch (pCasualtyRoll[1]) {
			case 1:
				seriousInjury = SeriousInjury.DAMAGED_BACK;
				break;
			case 2:
				seriousInjury = SeriousInjury.SMASHED_KNEE;
				break;
			case 3:
				seriousInjury = SeriousInjury.SMASHED_HIP;
				break;
			case 4:
				seriousInjury = SeriousInjury.SMASHED_ANKLE;
				break;
			case 5:
				seriousInjury = SeriousInjury.SERIOUS_CONCUSSION;
				break;
			case 6:
				seriousInjury = SeriousInjury.FRACTURED_SKULL;
				break;
			case 7:
				seriousInjury = SeriousInjury.BROKEN_NECK;
				break;
			case 8:
				seriousInjury = SeriousInjury.SMASHED_COLLAR_BONE;
				break;
			}
			break;
		}
		return seriousInjury;
	}

	public boolean isArmourBroken(GameState pGameState, InjuryContext pInjuryContext) {
		Game game = pGameState.getGame();
		StatsMechanic mechanic = (StatsMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.STAT.name());
		int[] armourRoll = pInjuryContext.getArmorRoll();
		Player<?> defender = game.getPlayerById(pInjuryContext.getDefenderId());
		int armour = defender.getArmour();
		if (UtilCards.hasCard(game, defender, Card.BELT_OF_INVULNERABILITY)) {
			pInjuryContext.clearArmorModifiers();
		}
		if ((armour > 7) &&
			Arrays.stream(pInjuryContext.getArmorModifiers())
				.anyMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.reducesArmourToFixedValue))) {
			armour = 7;
		}
		return mechanic.armourIsBroken(armour, armourRoll, pInjuryContext);
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

	public Direction interpretThrowInDirectionRoll(FieldCoordinate pStartCoordinate, int roll) {
		// Endzone Home Team
		if (pStartCoordinate.getX() < 1) {
			return interpretThrowInDirectionRoll(Direction.EAST, roll);
		}
		// Endzone Away Team
		if (pStartCoordinate.getX() > 24) {
			return interpretThrowInDirectionRoll(Direction.WEST, roll);
		}
		// Lower Sideline
		if (pStartCoordinate.getY() > 13) {
			return interpretThrowInDirectionRoll(Direction.NORTH, roll);
		}
		// Upper Sideline
		if (pStartCoordinate.getY() < 1) {
			return interpretThrowInDirectionRoll(Direction.SOUTH, roll);
		}
		throw new IllegalStateException("Unable to determine throwInDirection.");
	}

	public Direction interpretThrowInDirectionRoll(Direction pTemplateDirection, int roll) {
		if (pTemplateDirection == Direction.EAST) {
			switch (roll) {
			case 1:
			case 2:
				return Direction.NORTHEAST;
			case 3:
			case 4:
				return Direction.EAST;
			case 5:
			case 6:
				return Direction.SOUTHEAST;
			}
		}
		if (pTemplateDirection == Direction.WEST) {
			switch (roll) {
			case 1:
			case 2:
				return Direction.SOUTHWEST;
			case 3:
			case 4:
				return Direction.WEST;
			case 5:
			case 6:
				return Direction.NORTHWEST;
			}
		}
		if (pTemplateDirection == Direction.NORTH) {
			switch (roll) {
			case 1:
			case 2:
				return Direction.NORTHWEST;
			case 3:
			case 4:
				return Direction.NORTH;
			case 5:
			case 6:
				return Direction.NORTHEAST;
			}
		}
		if (pTemplateDirection == Direction.SOUTH) {
			switch (roll) {
			case 1:
			case 2:
				return Direction.SOUTHEAST;
			case 3:
			case 4:
				return Direction.SOUTH;
			case 5:
			case 6:
				return Direction.SOUTHWEST;
			}
		}
		throw new IllegalStateException("Unable to determine throwInDirection.");
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

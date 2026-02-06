package com.fumbbl.ffb.server.mechanic.bb2025;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.bb2025.SeriousInjury;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
import com.fumbbl.ffb.factory.mixed.CasualtyModifierFactory;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.ZappedPlayer;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifier;
import com.fumbbl.ffb.report.ReportReRoll;
import com.fumbbl.ffb.report.bb2025.ReportMascotUsed;
import com.fumbbl.ffb.report.bb2025.ReportTeamCaptainRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepResult;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class RollMechanic extends com.fumbbl.ffb.server.mechanic.RollMechanic {

	private static final int MASCOT_MINIMUM_ROLL = 4;
	private static final int TEAM_CAPTAIN_MINIMUM_ROLL = 6;
	private final Map<InjuryAttribute, Integer> reductionThresholds = new HashMap<InjuryAttribute, Integer>() {{
		put(InjuryAttribute.MA, 1);
		put(InjuryAttribute.ST, 1);
		put(InjuryAttribute.AG, 6);
		put(InjuryAttribute.PA, 6);
		put(InjuryAttribute.AV, 3);
	}};

	private final List<SeriousInjury> orderedInjuries = new ArrayList<SeriousInjury>() {{
		add(SeriousInjury.HEAD_INJURY);
		add(SeriousInjury.HEAD_INJURY);
		add(SeriousInjury.SMASHED_KNEE);
		add(SeriousInjury.BROKEN_ARM);
		add(SeriousInjury.DISLOCATED_HIP);
		add(SeriousInjury.DISLOCATED_SHOULDER);
	}};

	@Override
	public int[] rollCasualty(DiceRoller diceRoller) {
		return new int[]{diceRoller.rollDice(16), diceRoller.rollDice(6)};
	}

	@Override
	public PlayerState interpretInjuryRoll(Game game, InjuryContext pInjuryContext) {
		PlayerState playerState = null;
		if ((game != null) && (pInjuryContext != null)) {
			int[] injuryRoll = pInjuryContext.getInjuryRoll();
			Player<?> defender = game.getPlayerById(pInjuryContext.getDefenderId());
			if ((defender != null) && defender.hasSkillProperty(NamedProperties.preventDamagingInjuryModifications)) {
				pInjuryContext.clearInjuryModifiers();
			}
			if (injuryRoll == null) {
				// This is a forced injury, for example triggered by the player being eaten
				// We expect an injury being available in the injury context
				playerState = pInjuryContext.getInjury();
			} else {
				boolean isStunty = Arrays.stream(pInjuryContext.getInjuryModifiers())
					.anyMatch(injuryModifier -> injuryModifier.isRegisteredToSkillWithProperty(NamedProperties.isHurtMoreEasily));
				int total = injuryRoll[0] + injuryRoll[1] + pInjuryContext.getInjuryModifierTotal(game);
				boolean hasThickSkull = defender != null && defender.hasSkillProperty(NamedProperties.convertKOToStunOn8);

				if (total == 7 && isStunty) {
					if (hasThickSkull) {
						playerState = new PlayerState(PlayerState.STUNNED);
						defender.getSkillWithProperty(NamedProperties.convertKOToStunOn8).getInjuryModifiers()
							.forEach(pInjuryContext::addInjuryModifier);
					} else {
						playerState = new PlayerState(PlayerState.KNOCKED_OUT);
					}
				} else if ((total == 8) && hasThickSkull && !isStunty) {
					playerState = new PlayerState(PlayerState.STUNNED);
					defender.getSkillWithProperty(NamedProperties.convertKOToStunOn8).getInjuryModifiers()
						.forEach(pInjuryContext::addInjuryModifier);
				} else if ((total == 9) && isStunty) {
					playerState = new PlayerState(PlayerState.BADLY_HURT);
				} else if (total > 9) {
					//noinspection DataFlowIssue
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

	@Override
	public PlayerState interpretCasualtyRollAndAddModifiers(Game game, InjuryContext injuryContext, Player<?> player,
		boolean useDecayRoll) {
		if (player instanceof ZappedPlayer) {
			return new PlayerState(PlayerState.BADLY_HURT);
		}
		int[] roll = injuryContext.getCasualtyRoll();
		CasualtyModifierFactory factory = game.getFactory(FactoryType.Factory.CASUALTY_MODIFIER);
		Set<CasualtyModifier> casualtyModifiers = factory.findModifiers(player);
		injuryContext.addCasualtyModifiers(casualtyModifiers);
		int modifierSum = casualtyModifiers.stream().mapToInt(CasualtyModifier::getModifier).sum();
		return new PlayerState(mapCasualtyRoll(roll[0] + modifierSum));
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext, boolean useDecay) {
		return interpretSeriousInjuryRoll(game, injuryContext);
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext) {
		int casModifier = injuryContext.casualtyModifiers.stream().mapToInt(CasualtyModifier::getModifier).sum();
		return interpretSeriousInjuryRoll(game, injuryContext, injuryContext.getCasualtyRoll()[0] + casModifier,
			injuryContext.getCasualtyRoll()[1]);
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext, int[] roll) {
		return interpretSeriousInjuryRoll(game, injuryContext, roll[0], roll[1]);
	}

	@Override
	public int multiBlockAttackerModifier() {
		return -2;
	}

	@Override
	public int multiBlockDefenderModifier() {
		return 0;
	}

	private SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext, int casRoll, int siRoll) {
		if (isSI(casRoll)) {
			return mapSIRoll(game, injuryContext, siRoll);
		}

		if (casRoll >= 11 && casRoll <= 12) {
			return SeriousInjury.SERIOUS_INJURY;
		}

		if (casRoll >= 9 && casRoll <= 10) {
			return SeriousInjury.SERIOUSLY_HURT;
		}

		return null;
	}

	private SeriousInjury mapSIRoll(Game game, InjuryContext injuryContext, int roll) {
		Player<?> defender = game.getPlayerById(injuryContext.getDefenderId());
		SeriousInjury originalInjury = mapSIRoll(roll);
		InjuryAttribute attribute = originalInjury.getInjuryAttribute();

		if (canBeReduced(attribute, currentValue(attribute, defender))) {
			return originalInjury;
		}

		injuryContext.setOriginalSeriousInjury(originalInjury);

		return SeriousInjury.SERIOUSLY_HURT;
	}

	/**
	 * @return current stat value WITHOUT temporary modifiers
	 */
	private int currentValue(InjuryAttribute attribute, Player<?> player) {
		switch (attribute) {
			case MA:
				return player.getMovement();
			case ST:
				return player.getStrength();
			case AG:
				return player.getAgility();
			case PA:
				return player.getPassing();
			case AV:
				return player.getArmour();
			default:
				return 0;
		}
	}

	private boolean isSI(int roll) {
		return roll == 13 || roll == 14;
	}

	@Override
	public int minimumLonerRoll(Player<?> player) {
		return player.getSkillIntValue(NamedProperties.hasToRollToUseTeamReroll);
	}

	@Override
	public int minimumProRoll() {
		return 3;
	}

	@Override
	public boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
		int minimumRoll, boolean fumble, Skill modificationSkill, Skill reRollSkill, CommonProperty menuProperty,
		String defaultValueKey, List<String> messages) {
		boolean dialogShown = false;
		Game game = gameState.getGame();
		if (minimumRoll >= 0) {
			List<ReRollProperty> properties = new ArrayList<>();
			if (isMascotAvailable(gameState, player)) {
				properties.add(ReRollProperty.MASCOT);
			}
			if (isTeamReRollAvailable(gameState, player)) {
				properties.add(ReRollProperty.TRR);
			}
			findAdditionalReRollProperty(game.getTurnData()).ifPresent(properties::add);

			if (player.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)) {
				properties.add(ReRollProperty.LONER);
			}

			if (isProReRollAvailable(player, game, gameState.getPassState())) {
				properties.add(ReRollProperty.PRO);
			}
			if (reRollSkill == null) {
				Optional<Skill> reRollOnce =
					UtilCards.getUnusedSkillWithProperty(player, NamedProperties.canRerollSingleDieOncePerPeriod);
				if (reRollOnce.isPresent()) {
					reRollSkill = reRollOnce.get();
				}
			}

			dialogShown =
				(properties.stream().anyMatch(ReRollProperty::isActualReRoll) || reRollSkill != null ||
					modificationSkill != null);
			if (dialogShown) {
				Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
				String playerId = player.getId();
				UtilServerDialog.showDialog(gameState,
					new DialogReRollPropertiesParameter(playerId, reRolledAction, minimumRoll, properties, fumble, reRollSkill,
						modificationSkill, menuProperty, defaultValueKey, messages), !actingTeam.hasPlayer(player));
			}
		}
		return dialogShown;
	}

	public boolean useReRoll(IStep pStep, ReRollSource reRollSource, Player<?> pPlayer) {
		if (pPlayer == null) {
			throw new IllegalArgumentException("Parameter player must not be null.");
		}
		boolean successful = false;
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		StepResult stepResult = pStep.getResult();
		TurnData turnData = game.getTurnData();
		if (reRollSource != null) {

			boolean teamReRoll = ReRollSources.TEAM_RE_ROLL == reRollSource;
			InducementType mascotType = turnData.getInducementSet().forUsage(Usage.CONDITIONAL_REROLL);
			boolean mascotAvailable = mascotType != null && turnData.getInducementSet().hasUsesLeft(mascotType);

			boolean mascot =
				Arrays.asList(ReRollSources.MASCOT, ReRollSources.MASCOT_TRR).contains(reRollSource) && mascotAvailable;

			if (mascot) {
				int mascotRoll = gameState.getDiceRoller().rollDice(6);
				successful = mascotRoll >= MASCOT_MINIMUM_ROLL;
				boolean fallback = !successful && reRollSource == ReRollSources.MASCOT_TRR && turnData.getReRolls() > 0;

				useMascot(stepResult, gameState, mascotRoll, successful, fallback, mascotType, turnData);

				if (successful) {
					return checkForLoner(pPlayer, gameState, stepResult);
				} else if (!fallback) {
					return false;
				} else {
					teamReRoll = true;
				}
			}

			Skill reRollSourceSkill = reRollSource.getSkill(game);
			if (teamReRoll) {

				successful = useTeamReRoll(reRollSource, pPlayer, turnData, stepResult, successful, gameState);

			} else if (reRollSourceSkill != null) {
				if (reRollSourceSkill.hasSkillProperty(NamedProperties.canRerollOncePerTurn)) {
					PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
					successful = (pPlayer.hasSkillProperty(NamedProperties.canRerollOncePerTurn)
						&& !playerState.hasUsedPro());
					if (successful) {
						game.getFieldModel().setPlayerState(pPlayer, playerState.changeUsedPro(true));
						int proRoll = gameState.getDiceRoller().rollSkill();
						successful = DiceInterpreter.getInstance().isSkillRollSuccessful(proRoll, minimumProRoll());
						stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.PRO, successful, proRoll));
						if (!successful &&
							Arrays.asList(ReRollSources.PRO_MASCOT, ReRollSources.PRO_TRR, ReRollSources.PRO_MASCOT_TRR)
								.contains(reRollSource)) {
							boolean proMascot =
								Arrays.asList(ReRollSources.PRO_MASCOT, ReRollSources.PRO_MASCOT_TRR).contains(reRollSource) &&
									mascotAvailable;
							if (proMascot) {
								int mascotRoll = gameState.getDiceRoller().rollDice(6);
								successful = mascotRoll >= MASCOT_MINIMUM_ROLL;
								boolean fallback =
									!successful && reRollSource == ReRollSources.PRO_MASCOT_TRR && turnData.getReRolls() > 0;

								useMascot(stepResult, gameState, mascotRoll, successful, fallback, mascotType, turnData);

								if (successful) {
									if (checkForLoner(pPlayer, gameState, stepResult)) {
										proRoll = gameState.getDiceRoller().rollSkill();
										successful = DiceInterpreter.getInstance().isSkillRollSuccessful(proRoll, minimumProRoll());
										stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.PRO, successful, proRoll));
										return successful;
									} else {
										return false;
									}
								} else if (!fallback) {
									return false;
								}
							}

							if (Arrays.asList(ReRollSources.PRO_TRR, ReRollSources.PRO_MASCOT_TRR).contains(reRollSource)) {
								if (useTeamReRoll(ReRollSources.TEAM_RE_ROLL, pPlayer, turnData, stepResult, successful, gameState)) {
									proRoll = gameState.getDiceRoller().rollSkill();
									successful = DiceInterpreter.getInstance().isSkillRollSuccessful(proRoll, minimumProRoll());
									stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.PRO, successful, proRoll));
									return successful;
								} else {
									return false;
								}
							}
						}
					}
				} else {
					if (reRollSourceSkill.getSkillUsageType().isTrackOutsideActivation()) {
						successful = !pPlayer.isUsed(reRollSourceSkill);
					} else {
						successful = UtilCards.hasSkill(pPlayer, reRollSourceSkill);
					}
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), reRollSource, successful, 0));
				}
				if (reRollSourceSkill.getSkillUsageType().isTrackOutsideActivation()) {
					ActingPlayer actingPlayer = game.getActingPlayer();
					if (actingPlayer.getPlayer() == pPlayer) {
						actingPlayer.markSkillUsed(reRollSourceSkill);
					} else {
						pPlayer.markUsed(reRollSourceSkill, game);
					}
				}
			}
		}
		return successful;
	}

	private void useMascot(StepResult stepResult, GameState gameState, int mascotRoll, boolean successful,
		boolean fallback,
		InducementType mascotType, TurnData turnData) {
		stepResult.addReport(
			new ReportMascotUsed(gameState.getGame().getActingTeam().getId(), MASCOT_MINIMUM_ROLL, mascotRoll, successful,
				fallback));

		if (!successful || !checkTeamCaptain(stepResult, gameState)) {
			UtilServerInducementUse.useInducement(mascotType, 1, turnData.getInducementSet());
		}
	}

	private boolean checkTeamCaptain(StepResult stepResult, GameState gameState) {

		if (Arrays.stream(gameState.getGame().getActingTeam().getPlayers())
			.noneMatch(player -> player.hasSkillProperty(NamedProperties.canSaveReRolls))) {
			return false;
		}

		int roll = gameState.getDiceRoller().rollDice(6);
		boolean rrSaved = roll >= TEAM_CAPTAIN_MINIMUM_ROLL;
		stepResult.addReport(
			new ReportTeamCaptainRoll(gameState.getGame().getActingTeam().getId(), TEAM_CAPTAIN_MINIMUM_ROLL, roll, rrSaved));
		return rrSaved;
	}

	private boolean useTeamReRoll(ReRollSource reRollSource, Player<?> pPlayer, TurnData turnData, StepResult stepResult,
		boolean successful, GameState gameState) {

		boolean rrSaved = checkTeamCaptain(stepResult, gameState);

		ReRollSource usedAdditionalReRollSource = updateTurnDataAfterReRollUsage(turnData, !rrSaved);

		if (usedAdditionalReRollSource != null) {
			stepResult.addReport(new ReportReRoll(pPlayer.getId(), usedAdditionalReRollSource, successful, 0));
		} else if (LeaderState.AVAILABLE.equals(turnData.getLeaderState())) {
			stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.LEADER, successful, 0));
			turnData.setLeaderState(LeaderState.USED);
		} else {
			stepResult.addReport(new ReportReRoll(pPlayer.getId(), reRollSource, successful, 0));
		}

		successful = checkForLoner(pPlayer, gameState, stepResult);
		return successful;
	}

	private boolean checkForLoner(Player<?> pPlayer, GameState gameState, StepResult stepResult) {
		if (pPlayer.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)) {
			int roll = gameState.getDiceRoller().rollSkill();
			int minimumRoll = minimumLonerRoll(pPlayer);
			boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
			stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.LONER, successful, roll));
			return successful;
		} else {
			return true;
		}
	}

	private ReRollSource updateTurnDataAfterReRollUsage(TurnData turnData, boolean rrActuallyUsed) {
		if (rrActuallyUsed) {
			turnData.setReRolls(turnData.getReRolls() - 1);
		}
		if (turnData.getReRollsBrilliantCoachingOneDrive() > 0) {
			if (rrActuallyUsed) {
				turnData.setReRollsBrilliantCoachingOneDrive(turnData.getReRollsBrilliantCoachingOneDrive() - 1);
			}
			return ReRollSources.BRILLIANT_COACHING;
		}
		if (turnData.getReRollsPumpUpTheCrowdOneDrive() > 0) {
			if (rrActuallyUsed) {
				turnData.setReRollsPumpUpTheCrowdOneDrive(turnData.getReRollsPumpUpTheCrowdOneDrive() - 1);
			}
			return ReRollSources.PUMP_UP_THE_CROWD;
		}
		if (turnData.getReRollShowStarOneDrive() > 0) {
			if (rrActuallyUsed) {
				turnData.setReRollShowStarOneDrive(turnData.getReRollShowStarOneDrive() - 1);
			}
			return ReRollSources.SHOW_STAR;
		}

		return null;
	}

	@Override
	public boolean allowsTeamReRoll(TurnMode turnMode) {
		return true;
	}


	@Override
	public boolean isMascotAvailable(GameState pGameState, Player<?> pPlayer) {
		Game game = pGameState.getGame();
		InducementSet inducementSet = game.isHomePlaying() ?
			game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();

		return Arrays.stream(inducementSet.getInducements())
			.anyMatch(ind -> ind.getType().hasUsage(Usage.CONDITIONAL_REROLL) && ind.getUsesLeft() > 0)
			&& isTeamReRollAvailable(pGameState, pPlayer, 1);

	}

	private boolean canBeReduced(InjuryAttribute attribute, int currentValue) {
		return currentValue > 0 && reductionThresholds.get(attribute) != currentValue;
	}

	private SeriousInjury mapSIRoll(int roll) {
		return orderedInjuries.get(roll - 1);
	}

	private int mapCasualtyRoll(int roll) {
		if (roll >= 15) {
			return PlayerState.RIP;
		}
		if (roll >= 9) {
			return PlayerState.SERIOUS_INJURY;
		}

		return PlayerState.BADLY_HURT;
	}

	@Override
	public Optional<ReRollProperty> findAdditionalReRollProperty(TurnData turnData) {
		if (turnData.getReRollsBrilliantCoachingOneDrive() > 0) {
			return Optional.of(ReRollProperty.BRILLIANT_COACHING);
		}
		if (turnData.getReRollsPumpUpTheCrowdOneDrive() > 0) {
			return Optional.of(ReRollProperty.PUMP_UP_THE_CROWD);
		}
		if (turnData.getReRollShowStarOneDrive() > 0) {
			return Optional.of(ReRollProperty.SHOW_STAR);
		}
		return Optional.empty();
	}


}

package com.fumbbl.ffb.server.mechanic.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSourceComposer;
import com.fumbbl.ffb.ReRollSourceComposition;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.bb2025.SeriousInjury;
import com.fumbbl.ffb.factory.mixed.CasualtyModifierFactory;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
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
import com.fumbbl.ffb.ReRollOptions;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepResult;
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;
import com.fumbbl.ffb.server.util.ReRollDialogParameterFactory;
import com.fumbbl.ffb.server.util.ReRollRequest;
import com.fumbbl.ffb.server.util.ServerUtilPlayer;
import com.fumbbl.ffb.server.util.bb2025.ReRollPropertiesDialogParameterFactory;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class RollMechanic extends com.fumbbl.ffb.server.mechanic.RollMechanic {

	private static final Set<TurnMode> modesProhibitingReRolls = new HashSet<TurnMode>() {{
		add(TurnMode.KICKOFF);
		add(TurnMode.DUMP_OFF);
		add(TurnMode.QUICK_SNAP);
		add(TurnMode.BETWEEN_TURNS);
	}};

	private static final Set<ReRolledAction> passiveReRollActions = new HashSet<ReRolledAction>() {{
		add(ReRolledActions.SWOOP_DISTANCE);
		add(ReRolledActions.SWOOP_DIRECTION);
		// This is a workaround, in case of a multi block double self cas if both initial regeneration rolls fail
		// and the then the first roll is rerolled successfully the player state is already set to not-stunned/prone
		// so the pro check would then be positive for the second re-roll
		// the "proper" fix would be to rework the multi apo step again to inject another phase between re-rolling
		// regeneration and apo that is then used to reset the player state
		add(ReRolledActions.REGENERATION);
	}};

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

	private static final ReRollDialogParameterFactory DEFAULT_DIALOG_PARAMETER_FACTORY
		= new ReRollPropertiesDialogParameterFactory();

	private final ReRollSourceComposer reRollSourceComposer = new ReRollSourceComposer();

	@Override
	public int minimumProRoll() {
		return 3;
	}

	@Override
	public boolean askForReRollIfAvailable(ReRollRequest request) {
		GameState gameState = request.getGameState();
		Player<?> player = request.getPlayer();
		Game game = gameState.getGame();
		if (request.getMinimumRoll() < 0) {
			return false;
		}
		ReRollOptions reRollOptions = findReRollOptions(gameState, player, request.getReRolledAction(),
			request.getReRollSkill());

		ReRollDialogParameterFactory dialogParameterFactory = request.getDialogParameterFactory();
		if (dialogParameterFactory == null) {
			dialogParameterFactory = DEFAULT_DIALOG_PARAMETER_FACTORY;
		}
		IDialogParameter dialogParameter = dialogParameterFactory.create(request, reRollOptions);
		if (dialogParameter == null) {
			return false;
		}
		Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
		UtilServerDialog.showDialog(gameState, dialogParameter, !actingTeam.hasPlayer(player));
		return true;
	}

	public boolean useReRoll(IStep pStep, ReRollSource reRollSource, Player<?> pPlayer) {
		if (pPlayer == null) {
			throw new IllegalArgumentException("Parameter player must not be null.");
		}
		if (reRollSource == null) {
			return false;
		}
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		StepResult stepResult = pStep.getResult();

		ReRollSourceComposition composition = reRollSourceComposer.decompose(reRollSource);
		if (composition != null) {
			if (composition.isPro()) {
				return useProReRoll(composition, pPlayer, gameState, stepResult);
			}
			return useTeamReRolls(composition, pPlayer, gameState, stepResult);
		}

		boolean successful = false;
		Skill reRollSourceSkill = reRollSource.getSkill(game);
		if (reRollSourceSkill != null) {
			if (reRollSourceSkill.getSkillUsageType().isTrackOutsideActivation()) {
				successful = !pPlayer.isUsed(reRollSourceSkill);
			} else {
				successful = UtilCards.hasSkill(pPlayer, reRollSourceSkill);
			}
			stepResult.addReport(new ReportReRoll(pPlayer.getId(), reRollSource, successful, 0));
			markSkillUsed(game, pPlayer, reRollSourceSkill);
		}
		return successful;
	}

	private void markSkillUsed(Game game, Player<?> player, Skill skill) {
		if (skill.getSkillUsageType().isTrackOutsideActivation()) {
			ActingPlayer actingPlayer = game.getActingPlayer();
			if (actingPlayer.getPlayer() == player) {
				actingPlayer.markSkillUsed(skill);
			} else {
				player.markUsed(skill, game);
			}
		}
	}

	/**
	 * Rolls for Pro and if that fails uses the fallback re-rolls of the composition to re-roll the Pro roll.
	 */
	private boolean useProReRoll(ReRollSourceComposition composition, Player<?> player, GameState gameState,
		StepResult stepResult) {
		Game game = gameState.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		Skill proSkill = player.getSkillWithProperty(NamedProperties.canRerollOncePerTurn);
		if (proSkill == null || playerState.hasUsedPro()) {
			return false;
		}
		game.getFieldModel().setPlayerState(player, playerState.changeUsedPro(true));
		markSkillUsed(game, player, proSkill);

		boolean successful = rollPro(player, gameState, stepResult);
		boolean hasFallback = composition.isMascot() || composition.getTeamReRollSource() != null;
		if (!successful && hasFallback && useTeamReRolls(composition, player, gameState, stepResult)) {
			successful = rollPro(player, gameState, stepResult);
		}
		return successful;
	}

	private boolean rollPro(Player<?> player, GameState gameState, StepResult stepResult) {
		int proRoll = gameState.getDiceRoller().rollSkill();
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(proRoll, minimumProRoll());
		stepResult.addReport(new ReportReRoll(player.getId(), ReRollSources.PRO, successful, proRoll));
		return successful;
	}

	/**
	 * Uses the mascot and/or team re-roll of the composition. Parts that are not available count as failed.
	 *
	 * @return whether a re-roll was granted (including a successful Loner roll)
	 */
	private boolean useTeamReRolls(ReRollSourceComposition composition, Player<?> player, GameState gameState,
		StepResult stepResult) {
		TurnData turnData = findReRollTurnData(gameState.getGame());
		ReRollSource teamReRollSource = composition.getTeamReRollSource();
		boolean teamReRollAvailable =
			teamReRollSource != null && isTeamReRollSourceAvailable(gameState, player, turnData, teamReRollSource);

		if (composition.isMascot()) {
			if (!isMascotAvailable(gameState, player)) {
				return false;
			}
			InducementType mascotType = turnData.getInducementSet().forUsage(Usage.CONDITIONAL_REROLL);
			int mascotRoll = gameState.getDiceRoller().rollDice(6);
			boolean successful = mascotRoll >= MASCOT_MINIMUM_ROLL;
			useMascot(stepResult, gameState, mascotRoll, successful, !successful && teamReRollAvailable, mascotType,
				turnData);
			if (successful) {
				return checkForLoner(player, gameState, stepResult);
			}
		}

		if (!teamReRollAvailable) {
			return false;
		}
		return useTeamReRoll(teamReRollSource, player, turnData, stepResult, gameState);
	}

	private boolean isTeamReRollSourceAvailable(GameState gameState, Player<?> player, TurnData turnData,
		ReRollSource teamReRollSource) {
		if (!isTeamReRollAvailable(gameState, player)) {
			return false;
		}
		if (ReRollSources.TEAM_RE_ROLL == teamReRollSource) {
			return LeaderState.AVAILABLE.equals(turnData.getLeaderState()) || regularReRolls(turnData) > 0;
		}
		return isSpecialReRollSourceAvailable(turnData, teamReRollSource);
	}

	// leader and single use re-rolls are included in the total amount of re-rolls
	private int regularReRolls(TurnData turnData) {
		int reRolls = turnData.getReRolls() - turnData.getReRollsBrilliantCoachingOneDrive()
			- turnData.getReRollsPumpUpTheCrowdOneDrive() - turnData.getReRollShowStarOneDrive();
		if (LeaderState.AVAILABLE.equals(turnData.getLeaderState())) {
			reRolls--;
		}
		return reRolls;
	}

	private void useMascot(StepResult stepResult, GameState gameState, int mascotRoll, boolean successful,
		boolean fallback, InducementType mascotType, TurnData turnData) {
		stepResult.addReport(
			new ReportMascotUsed(findReRollTeam(gameState.getGame()).getId(), MASCOT_MINIMUM_ROLL, mascotRoll, successful,
				fallback));

		if (!successful || !checkTeamCaptain(stepResult, gameState)) {
			UtilServerInducementUse.useInducement(mascotType, 1, turnData.getInducementSet());
		}
	}

	private boolean checkTeamCaptain(StepResult stepResult, GameState gameState) {

		FieldModel fieldModel = gameState.getGame().getFieldModel();
		Team reRollTeam = findReRollTeam(gameState.getGame());
		if (Arrays.stream(reRollTeam.getPlayers())
			.noneMatch(player -> player.hasSkillProperty(NamedProperties.canSaveReRolls) &&
				FieldCoordinateBounds.FIELD.isInBounds(fieldModel.getPlayerCoordinate(player)))) {
			return false;
		}

		int roll = gameState.getDiceRoller().rollDice(6);
		boolean rrSaved = roll >= TEAM_CAPTAIN_MINIMUM_ROLL;
		stepResult.addReport(
			new ReportTeamCaptainRoll(reRollTeam.getId(), TEAM_CAPTAIN_MINIMUM_ROLL, roll, rrSaved));
		return rrSaved;
	}

	private boolean useTeamReRoll(ReRollSource reRollSource, Player<?> pPlayer, TurnData turnData, StepResult stepResult,
		GameState gameState) {

		boolean rrSaved = checkTeamCaptain(stepResult, gameState);
		ReRollSource usedReRollSource = findUsedTeamReRollSource(turnData, reRollSource);

		if (!rrSaved) {
			updateTurnDataAfterReRollUsage(turnData, usedReRollSource);
		}

		stepResult.addReport(new ReportReRoll(pPlayer.getId(), usedReRollSource, false, 0));

		return checkForLoner(pPlayer, gameState, stepResult);
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

	// single use re-rolls have to be selected explicitly, a regular team re-roll uses Leader first
	private ReRollSource findUsedTeamReRollSource(TurnData turnData, ReRollSource selectedSource) {
		if (isSpecialReRollSourceAvailable(turnData, selectedSource)) {
			return selectedSource;
		}
		if (LeaderState.AVAILABLE.equals(turnData.getLeaderState())) {
			return ReRollSources.LEADER;
		}

		return ReRollSources.TEAM_RE_ROLL;
	}

	private void updateTurnDataAfterReRollUsage(TurnData turnData, ReRollSource usedReRollSource) {
		turnData.setReRolls(turnData.getReRolls() - 1);
		if (isSpecialReRollSourceAvailable(turnData, usedReRollSource)) {
			consumeSpecialReRoll(turnData, usedReRollSource);
			return;
		}
		if (ReRollSources.LEADER == usedReRollSource && LeaderState.AVAILABLE.equals(turnData.getLeaderState())) {
			turnData.setLeaderState(LeaderState.USED);
		}
	}

	private boolean isSpecialReRollSourceAvailable(TurnData turnData, ReRollSource reRollSource) {
		if (ReRollSources.BRILLIANT_COACHING == reRollSource) {
			return turnData.getReRollsBrilliantCoachingOneDrive() > 0;
		}
		if (ReRollSources.PUMP_UP_THE_CROWD == reRollSource) {
			return turnData.getReRollsPumpUpTheCrowdOneDrive() > 0;
		}
		if (ReRollSources.SHOW_STAR == reRollSource) {
			return turnData.getReRollShowStarOneDrive() > 0;
		}

		return false;
	}

	private void consumeSpecialReRoll(TurnData turnData, ReRollSource reRollSource) {
		if (ReRollSources.BRILLIANT_COACHING == reRollSource) {
			turnData.setReRollsBrilliantCoachingOneDrive(turnData.getReRollsBrilliantCoachingOneDrive() - 1);
		} else if (ReRollSources.PUMP_UP_THE_CROWD == reRollSource) {
			turnData.setReRollsPumpUpTheCrowdOneDrive(turnData.getReRollsPumpUpTheCrowdOneDrive() - 1);
		} else if (ReRollSources.SHOW_STAR == reRollSource) {
			turnData.setReRollShowStarOneDrive(turnData.getReRollShowStarOneDrive() - 1);
		}
	}

	@Override
	public boolean allowsTeamReRoll(TurnMode turnMode) {
		return !modesProhibitingReRolls.contains(turnMode);
	}

	@Override
	public boolean isProReRollAvailable(Player<?> player, Game game, PassState passState) {
		// a pass block during a dump-off is not an activation, so Pro cannot be used
		return game.getTurnMode() != TurnMode.DUMP_OFF_PASS_BLOCK && super.isProReRollAvailable(player, game, passState);
	}


	@Override
	public boolean isMascotAvailable(GameState pGameState, Player<?> pPlayer) {
		Game game = pGameState.getGame();
		InducementSet inducementSet = findReRollTurnData(game).getInducementSet();

		return Arrays.stream(inducementSet.getInducements())
			.anyMatch(ind -> ind.getType().hasUsage(Usage.CONDITIONAL_REROLL) && ind.getUsesLeft() > 0)
			&& isTeamReRollAvailable(pGameState, pPlayer, 1);

	}

	@Override
	public boolean isSingleUseReRollAvailable(GameState pGameState, Player<?> pPlayer) {
		Game game = pGameState.getGame();
		return isTeamReRollAvailable(pGameState, pPlayer, findReRollTurnData(game).getSingleUseReRolls());
	}

	@Override
	public boolean isTeamReRollAvailable(GameState pGameState, Player<?> pPlayer) {
		Game game = pGameState.getGame();
		return isTeamReRollAvailable(pGameState, pPlayer, findReRollTurnData(game).getReRolls());
	}

	@Override
	protected boolean isTeamReRollAvailable(GameState pGameState, Player<?> pPlayer, int amount) {
		Game game = pGameState.getGame();
		TurnMode turnMode = game.getTurnMode();
		boolean homeHasPlayer = game.getTeamHome().hasPlayer(pPlayer);
		boolean awayHasPlayer = game.getTeamAway().hasPlayer(pPlayer);
		return (findReRollTeam(game).hasPlayer(pPlayer) && !findReRollTurnData(game).isReRollUsed() && (amount > 0)
			&& allowsTeamReRoll(turnMode)
			&& ((turnMode != TurnMode.BOMB_HOME) || homeHasPlayer)
			&& ((turnMode != TurnMode.BOMB_HOME_BLITZ) || homeHasPlayer)
			&& ((turnMode != TurnMode.BOMB_AWAY) || awayHasPlayer)
			&& ((turnMode != TurnMode.BOMB_AWAY_BLITZ) || awayHasPlayer));
	}

	// during a pass block homePlaying is switched to the pass blocking team, but the team of the thrower is still the active one
	private boolean isHomeTeamActive(Game game) {
		return game.isHomePlaying() != (game.getTurnMode() == TurnMode.PASS_BLOCK);
	}

	private Team findReRollTeam(Game game) {
		return isHomeTeamActive(game) ? game.getTeamHome() : game.getTeamAway();
	}

	private TurnData findReRollTurnData(Game game) {
		return isHomeTeamActive(game) ? game.getTurnDataHome() : game.getTurnDataAway();
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

	@Override
	public int getTotalAttackerStrength(GameState gameState, Player<?> attacker, Player<?> defender,
		boolean usingMultiBlock,
		boolean successfulDauntless, boolean doubleTargetStrength, int defenderStrength) {
		Game game = gameState.getGame();
		int blockStrengthAttacker = getAttackerBaseStrength(game, attacker, defender, usingMultiBlock);

		if (successfulDauntless) {
			blockStrengthAttacker =
				Math.max(blockStrengthAttacker, doubleTargetStrength ? 2 * defenderStrength : defenderStrength);
		}

		ActingPlayer actingPlayer = game.getActingPlayer();

		if (attacker.hasSkillProperty(NamedProperties.addStrengthOnBlitz)
			&& ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ)
			|| (actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE))) {
			blockStrengthAttacker++;
		}

		blockStrengthAttacker =
			ServerUtilPlayer.findBlockStrength(game, attacker, blockStrengthAttacker, defender, usingMultiBlock);


		Set<String> multiBlockTargets = gameState.getGame().getMultiBlockTargets();
		int additionalAssists = gameState.getAdditionalAssist(game.getActingTeam().getId());
		// add additional assist when:
		// - effect is present
		// - either no multi block
		// - or no multiblock target yet selected (we are only showing decorations)
		// - or only this player is selected (also showing decorations but keep the assist for the "first" target)
		// - or two multiblock targets are selected
		//
		// if two players are selected we are actually blocking, so we can simply check for the existing effect as it
		// is removed after the "first" block
		if (!usingMultiBlock || multiBlockTargets.isEmpty() || multiBlockTargets.size() == 2 ||
			multiBlockTargets.size() == 1 && multiBlockTargets.contains(defender.getId())) {
			blockStrengthAttacker += additionalAssists;
		}
		return blockStrengthAttacker;
	}

	@Override
	public int getAttackerBaseStrength(Game game, Player<?> attacker, Player<?> defender, boolean isMultiBlock) {
		int strength = attacker.getStrengthWithModifiers();

		if (isMultiBlock) {
			strength += multiBlockAttackerModifier();
		}

		return Math.max(strength, 1);
	}

	@Override
	public List<ReRollProperty> findTeamReRollProperties(GameState gameState, Player<?> player) {
		TurnData turnData = findReRollTurnData(gameState.getGame());
		List<ReRollProperty> properties = new ArrayList<>();
		if (isMascotAvailable(gameState, player)) {
			properties.add(ReRollProperty.MASCOT);
		}
		if (isTeamReRollAvailable(gameState, player)) {
			properties.add(ReRollProperty.TRR);
			reRollSourceComposer.singleUseSources().forEach((property, source) -> {
				if (isSpecialReRollSourceAvailable(turnData, source)) {
					properties.add(property);
				}
			});
			if (LeaderState.AVAILABLE.equals(turnData.getLeaderState())) {
				properties.add(ReRollProperty.LEADER);
			}
		}
		return properties;
	}

	@Override
	public ReRollOptions findReRollOptions(GameState gameState, Player<?> player, ReRolledAction reRolledAction, Skill reRollSkill) {
		Game game = gameState.getGame();
		List<ReRollProperty> properties = new ArrayList<>(findTeamReRollProperties(gameState, player));

		if (player.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)) {
			properties.add(ReRollProperty.LONER);
		}

		if (!passiveReRollActions.contains(reRolledAction) &&
			isProReRollAvailable(player, game, gameState.getPassState())) {
			properties.add(ReRollProperty.PRO);
		}

		if (reRollSkill == null) {
			Optional<Skill> reRollOnce =
				UtilCards.getUnusedSkillWithProperty(player, NamedProperties.canRerollSingleDieOncePerPeriod);
			if (reRollOnce.isPresent()) {
				reRollSkill = reRollOnce.get();
			}
		}

		return new ReRollOptions(properties, reRollSkill);
	}
}

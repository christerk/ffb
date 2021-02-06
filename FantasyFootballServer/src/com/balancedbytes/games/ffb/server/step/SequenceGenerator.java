package com.balancedbytes.games.ffb.server.step;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.skillbehaviour.StepHook.HookPoint;
import com.balancedbytes.games.ffb.server.step.generator.Sequence;
import com.balancedbytes.games.ffb.server.step.phase.inducement.StepRiotousRookies;

/**
 * Generator class adding sequences of steps to the stepStack of a gameState.
 *
 * @author Kalimar
 */
public class SequenceGenerator {

	private static final SequenceGenerator _INSTANCE = new SequenceGenerator();

	/**
	 * @return the only instance of this class.
	 */
	public static SequenceGenerator getInstance() {
		return _INSTANCE;
	}

	private SequenceGenerator() {
		super();
	}

	public void pushBlockSequence(GameState pGameState) {
		pushBlockSequence(pGameState, null, false, null);
	}

	public void pushBlockSequence(GameState pGameState, String pBlockDefenderId, boolean pUsingStab,
	                              String pMultiBlockDefenderId) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push blockSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_BLOCKING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_BLOCKING),
			from(StepParameterKey.BLOCK_DEFENDER_ID, pBlockDefenderId), from(StepParameterKey.USING_STAB, pUsingStab),
			from(StepParameterKey.MULTI_BLOCK_DEFENDER_ID, pMultiBlockDefenderId));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.BLOOD_LUST, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.GO_FOR_IT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.FOUL_APPEARANCE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.HORNS);
		sequence.add(StepId.BLOCK_STATISTICS);
		sequence.add(StepId.DAUNTLESS);
		sequence.add(StepId.DUMP_OFF);
		sequence.add(StepId.STAB, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.APOTHECARY_DEFENDER));
		sequence.add(StepId.BLOCK_CHAINSAW, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.APOTHECARY_DEFENDER),
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.APOTHECARY_ATTACKER));
		sequence.add(StepId.BLOCK_BALL_AND_CHAIN, from(StepParameterKey.GOTO_LABEL_ON_PUSHBACK, IStepLabel.PUSHBACK));
		sequence.add(StepId.BLOCK_ROLL);
		sequence.add(StepId.BLOCK_CHOICE, from(StepParameterKey.GOTO_LABEL_ON_DODGE, IStepLabel.DODGE_BLOCK),
			from(StepParameterKey.GOTO_LABEL_ON_JUGGERNAUT, IStepLabel.JUGGERNAUT),
			from(StepParameterKey.GOTO_LABEL_ON_PUSHBACK, IStepLabel.PUSHBACK));
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);

		// on blockChoice = BOTH_DOWN
		sequence.add(StepId.JUGGERNAUT, IStepLabel.JUGGERNAUT,
			from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.PUSHBACK));
		sequence.add(StepId.BOTH_DOWN);
		sequence.add(StepId.WRESTLE);
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);

		// on blockChoice = POW_PUSHBACK
		sequence.add(StepId.BLOCK_DODGE, IStepLabel.DODGE_BLOCK);

		// on blockChoice = POW or PUSHBACK
		sequence.add(StepId.PUSHBACK, IStepLabel.PUSHBACK);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CROWD_PUSH));
		sequence.add(StepId.FOLLOWUP);
		sequence.add(StepId.SHADOWING);
		sequence.add(StepId.PICK_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.DROP_FALLING_PLAYERS));
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);
		sequence.add(StepId.FALL_DOWN, IStepLabel.FALL_DOWN);
		sequence.jump(IStepLabel.APOTHECARY_ATTACKER);

		// on blockChoice = SKULL
		sequence.add(StepId.DROP_FALLING_PLAYERS, IStepLabel.DROP_FALLING_PLAYERS);
		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_DEFENDER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));

		// GFI for ball & chain should go here.
		sequence.add(StepId.GO_FOR_IT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.DROP_FALLING_PLAYERS),
			from(StepParameterKey.BALL_AND_CHAIN_GFI, true));
		sequence.jump(IStepLabel.APOTHECARY_ATTACKER);
		sequence.add(StepId.DROP_FALLING_PLAYERS, IStepLabel.DROP_FALLING_PLAYERS);
		sequence.add(StepId.FALL_DOWN);

		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_ATTACKER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.SCATTER_BALL);
		sequence.add(StepId.END_BLOCKING, IStepLabel.END_BLOCKING);
		// may insert endTurn sequence add this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushPassSequence(GameState pGameState) {
		pushPassSequence(pGameState, null);
	}

	public void pushPassSequence(GameState pGameState, FieldCoordinate pTargetCoordinate) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push passSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_PASSING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING),
			from(StepParameterKey.TARGET_COORDINATE, pTargetCoordinate));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.BLOOD_LUST, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.BOMBARDIER);
		sequence.add(StepId.ANIMOSITY, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.PASS_BLOCK, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING));
		sequence.add(StepId.DISPATCH_PASSING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING),
			from(StepParameterKey.GOTO_LABEL_ON_HAND_OVER, IStepLabel.HAND_OVER),
			from(StepParameterKey.GOTO_LABEL_ON_HAIL_MARY_PASS, IStepLabel.HAIL_MARY_PASS));
		sequence.add(StepId.INTERCEPT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.PASS));

		sequence.insertHooks(HookPoint.PASS_INTERCEPT,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));

		sequence.add(StepId.PASS, IStepLabel.PASS, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING),
			from(StepParameterKey.GOTO_LABEL_ON_MISSED_PASS, IStepLabel.MISSED_PASS));
		sequence.jump(IStepLabel.SCATTER_BALL);
		sequence.add(StepId.HAIL_MARY_PASS, IStepLabel.HAIL_MARY_PASS,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.SCATTER_BALL));
		sequence.add(StepId.MISSED_PASS, IStepLabel.MISSED_PASS);
		sequence.jump(IStepLabel.SCATTER_BALL);
		sequence.add(StepId.HAND_OVER, IStepLabel.HAND_OVER);
		sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.SCATTER_BALL);

		sequence.add(StepId.END_PASSING, IStepLabel.END_PASSING);
		// may insert bomb or endPlayerAction sequence add this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushFoulSequence(GameState pGameState) {
		pushFoulSequence(pGameState, null);
	}

	public void pushFoulSequence(GameState pGameState, String pFouldDefenderId) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push foulSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_FOULING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING),
			from(StepParameterKey.FOUL_DEFENDER_ID, pFouldDefenderId));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		sequence.add(StepId.BLOOD_LUST);
		sequence.add(StepId.FOUL_CHAINSAW, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.APOTHECARY_ATTACKER));
		sequence.add(StepId.FOUL);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.REFEREE, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
		sequence.add(StepId.BRIBES, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
		sequence.add(StepId.EJECT_PLAYER, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.jump(IStepLabel.END_FOULING);
		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_ATTACKER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.END_FOULING, IStepLabel.END_FOULING);

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushKickTeamMateSequence(GameState pGameState) {
		pushKickTeamMateSequence(pGameState, 0, null);
	}

	public void pushKickTeamMateSequence(GameState pGameState, int numDice, String pKickedPlayerId) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push kickTeamMateSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_KICK_TEAM_MATE, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICK_TEAM_MATE),
			from(StepParameterKey.KICKED_PLAYER_ID, pKickedPlayerId), from(StepParameterKey.NR_OF_DICE, numDice));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_KICK_TEAM_MATE));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_KICK_TEAM_MATE));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_KICK_TEAM_MATE));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_KICK_TEAM_MATE));
		sequence.add(StepId.BLOOD_LUST);
		sequence.add(StepId.KICK_TEAM_MATE,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.KICK_TM_DOUBLE_ROLLED));
		// insert scatterPlayerSequence at this point
		sequence.jump(IStepLabel.RIGHT_STUFF);
		sequence.add(StepId.KICK_TM_DOUBLE_ROLLED, IStepLabel.KICK_TM_DOUBLE_ROLLED);
		sequence.jump(IStepLabel.APOTHECARY_KICKED_PLAYER);
		sequence.add(StepId.RIGHT_STUFF, IStepLabel.RIGHT_STUFF);
		sequence.jump(IStepLabel.APOTHECARY_KICKED_PLAYER);
		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_KICKED_PLAYER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.THROWN_PLAYER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_KICK_TEAM_MATE, IStepLabel.END_KICK_TEAM_MATE);

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushThrowTeamMateSequence(GameState pGameState) {
		pushThrowTeamMateSequence(pGameState, null, null);
	}

	public void pushThrowTeamMateSequence(GameState pGameState, String pThrownPlayerId,
	                                      FieldCoordinate pTargetCoordinate) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push throwTeamMateSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_THROW_TEAM_MATE, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_THROW_TEAM_MATE),
			from(StepParameterKey.THROWN_PLAYER_ID, pThrownPlayerId),
			from(StepParameterKey.TARGET_COORDINATE, pTargetCoordinate));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.BLOOD_LUST);
		sequence.add(StepId.ALWAYS_HUNGRY,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.EAT_TEAM_MATE),
			from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.FUMBLE_TTM_PASS));
		sequence.add(StepId.THROW_TEAM_MATE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FUMBLE_TTM_PASS));
		// insert scatterPlayerSequence at this point
		sequence.jump(IStepLabel.RIGHT_STUFF);
		sequence.add(StepId.FUMBLE_TTM_PASS, IStepLabel.FUMBLE_TTM_PASS);
		sequence.add(StepId.RIGHT_STUFF, IStepLabel.RIGHT_STUFF);
		sequence.jump(IStepLabel.APOTHECARY_THROWN_PLAYER);
		sequence.add(StepId.EAT_TEAM_MATE, IStepLabel.EAT_TEAM_MATE);
		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_THROWN_PLAYER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.THROWN_PLAYER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_THROW_TEAM_MATE, IStepLabel.END_THROW_TEAM_MATE);

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushScatterPlayerSequence(GameState pGameState, String pThrownPlayerId, PlayerState pThrownPlayerState,
	                                      boolean pThrownPlayerHasBall, FieldCoordinate pThrownPlayerCoordinate, boolean hasSwoop, boolean pThrowScatter) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push scatterPlayerSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		if (hasSwoop) {
			sequence.add(StepId.SWOOP, from(StepParameterKey.THROWN_PLAYER_ID, pThrownPlayerId),
				from(StepParameterKey.THROWN_PLAYER_STATE, pThrownPlayerState),
				from(StepParameterKey.THROWN_PLAYER_HAS_BALL, pThrownPlayerHasBall),
				from(StepParameterKey.THROWN_PLAYER_COORDINATE, pThrownPlayerCoordinate),
				from(StepParameterKey.THROW_SCATTER, pThrowScatter),
				from(StepParameterKey.GOTO_LABEL_ON_FALL_DOWN, IStepLabel.APOTHECARY_HIT_PLAYER));
		} else {
			sequence.add(StepId.INIT_SCATTER_PLAYER, from(StepParameterKey.THROWN_PLAYER_ID, pThrownPlayerId),
				from(StepParameterKey.THROWN_PLAYER_STATE, pThrownPlayerState),
				from(StepParameterKey.THROWN_PLAYER_HAS_BALL, pThrownPlayerHasBall),
				from(StepParameterKey.THROWN_PLAYER_COORDINATE, pThrownPlayerCoordinate),
				from(StepParameterKey.THROW_SCATTER, pThrowScatter));
		}
		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_HIT_PLAYER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.HIT_PLAYER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_SCATTER_PLAYER);
		// may insert a new scatterPlayerSequence at this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushStartGameSequence(GameState pGameState) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push startGameSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_START_GAME);
		sequence.add(StepId.WEATHER);
		sequence.add(StepId.PETTY_CASH);
		sequence.add(StepId.BUY_CARDS);
		sequence.add(StepId.BUY_INDUCEMENTS);
		// inserts inducement sequence at this point
		sequence.add(StepId.SPECTATORS);
		// continues with kickoffSequence after that

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushKickoffSequence(GameState pGameState, boolean pWithCoinChoice) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push kickoffSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		if (pWithCoinChoice) {
			sequence.add(StepId.COIN_CHOICE);
			sequence.add(StepId.RECEIVE_CHOICE);
		}
		sequence.add(StepId.INIT_KICKOFF);
		// inserts inducement sequence at this point
		sequence.add(StepId.SETUP, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICKOFF));
		// inserts inducement sequence at this point
		sequence.add(StepId.SETUP, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICKOFF));
		sequence.add(StepId.KICKOFF);
		sequence.add(StepId.KICKOFF_SCATTER_ROLL);
		sequence.add(StepId.SWARMING, from(StepParameterKey.HANDLE_RECEIVING_TEAM, false));
		sequence.add(StepId.SWARMING, from(StepParameterKey.HANDLE_RECEIVING_TEAM, true));
		sequence.add(StepId.KICKOFF_RETURN);
		// may insert select sequence at this point
		sequence.add(StepId.KICKOFF_RESULT_ROLL);
		sequence.add(StepId.APPLY_KICKOFF_RESULT, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICKOFF),
			from(StepParameterKey.GOTO_LABEL_ON_BLITZ, IStepLabel.BLITZ_TURN));
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.HOME));
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.AWAY));
		sequence.jump(IStepLabel.KICKOFF_ANIMATION);
		sequence.add(StepId.BLITZ_TURN, IStepLabel.BLITZ_TURN);
		// may insert selectSequence at this point
		sequence.add(StepId.KICKOFF_ANIMATION, IStepLabel.KICKOFF_ANIMATION);
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.TOUCHBACK);
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_KICKOFF, IStepLabel.END_KICKOFF);
		// continues with endTurnSequence after that

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushSelectSequence(GameState pGameState, boolean pUpdatePersistence) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push selectSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_SELECTING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_SELECTING),
			from(StepParameterKey.UPDATE_PERSISTENCE, pUpdatePersistence));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.BLOOD_LUST, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.JUMP_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.STAND_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.END_SELECTING, IStepLabel.END_SELECTING);
		// may insert endTurn, pass, throwTeamMate, block, foul or moveSequence add
		// this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushEndGameSequence(GameState pGameState, boolean adminMode) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push endGameSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_END_GAME, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_GAME),
			from(StepParameterKey.ADMIN_MODE, adminMode));
		sequence.add(StepId.PENALTY_SHOOTOUT);
		sequence.add(StepId.MVP);
		sequence.add(StepId.WINNINGS);
		sequence.add(StepId.FAN_FACTOR);
		sequence.add(StepId.PLAYER_LOSS);
		sequence.add(StepId.END_GAME, IStepLabel.END_GAME);

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushMoveSequence(GameState pGameState) {
		pushMoveSequence(pGameState, null, null);
	}

	public void pushMoveSequence(GameState pGameState, FieldCoordinate[] pMoveStack, String pGazeVictimId) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push moveSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_MOVING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING),
			from(StepParameterKey.MOVE_STACK, pMoveStack), from(StepParameterKey.GAZE_VICTIM_ID, pGazeVictimId));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.BLOOD_LUST, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.HYPNOTIC_GAZE, IStepLabel.HYPNOTIC_GAZE,
			from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING));
		sequence.add(StepId.MOVE_BALL_AND_CHAIN, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING),
			from(StepParameterKey.GOTO_LABEL_ON_FALL_DOWN, IStepLabel.FALL_DOWN));
		sequence.add(StepId.MOVE);
		// Do GFI twice to deal with Ball and Chain separately.
		sequence.add(StepId.GO_FOR_IT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.GO_FOR_IT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN),
			from(StepParameterKey.BALL_AND_CHAIN_GFI, true));
		sequence.add(StepId.TENTACLES, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.END_MOVING));
		sequence.add(StepId.LEAP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.MOVE_DODGE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.DIVING_TACKLE, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.RETRY_DODGE));
		sequence.jump(IStepLabel.SHADOWING);
		sequence.add(StepId.MOVE_DODGE, IStepLabel.RETRY_DODGE,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.DROP_DIVING_TACKLER);
		sequence.add(StepId.SHADOWING, IStepLabel.SHADOWING);
		sequence.add(StepId.PICK_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.SCATTER_BALL));
		sequence.jump(IStepLabel.SCATTER_BALL);
		sequence.add(StepId.DROP_DIVING_TACKLER, IStepLabel.FALL_DOWN);
		sequence.add(StepId.SHADOWING); // falling player can be shadowed
		sequence.add(StepId.FALL_DOWN);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.SCATTER_BALL);
		sequence.add(StepId.END_MOVING, IStepLabel.END_MOVING);
		// may insert endTurn or block sequence add this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushEndPlayerActionSequence(GameState pGameState, boolean feedingAllowed, boolean endPlayerAction,
	                                        boolean endTurn) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push endPlayerActionSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_FEEDING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FEEDING),
			from(StepParameterKey.FEEDING_ALLOWED, feedingAllowed),
			from(StepParameterKey.END_PLAYER_ACTION, endPlayerAction), from(StepParameterKey.END_TURN, endTurn));
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.FEEDING));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_FEEDING, IStepLabel.END_FEEDING);
		// inserts select or inducement sequence at this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushInducementSequence(GameState pGameState, InducementPhase pInducementPhase, boolean pHomeTeam) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push inducementSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_INDUCEMENT, from(StepParameterKey.INDUCEMENT_PHASE, pInducementPhase),
			from(StepParameterKey.HOME_TEAM, pHomeTeam));
		// may insert wizard or card sequence at this point
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_INDUCEMENT);
		// may insert endTurn or inducement sequence at this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushRiotousRookies(GameState pGameState) {
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push riotous rookies step onto stack");

		pGameState.getStepStack().push(new StepRiotousRookies(pGameState));
	}

	public void pushCardSequence(GameState pGameState, Card pCard, boolean pHomeTeam) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push cardSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.PLAY_CARD, from(StepParameterKey.CARD, pCard), from(StepParameterKey.HOME_TEAM, pHomeTeam));

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushWizardSequence(GameState pGameState) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push wizardSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.WIZARD);
		// may insert multiple specialEffect sequences at this point
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushBombSequence(GameState pGameState, String pCatcherId, boolean pPassFumble) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push bombSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_BOMB, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_BOMB),
			from(StepParameterKey.CATCHER_ID, pCatcherId), from(StepParameterKey.PASS_FUMBLE, pPassFumble));
		// may insert multiple specialEffect sequences add this point
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_BOMB, IStepLabel.END_BOMB);
		// may insert endPlayerAction or pass sequence add this point
		pGameState.getStepStack().push(sequence.getSequence());
	}

	public void pushSpecialEffectSequence(GameState pGameState, SpecialEffect pSpecialEffect, String pPlayerId,
	                                      boolean pRollForEffect) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push specialEffectSequence onto stack (player " + pPlayerId + ")");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.SPECIAL_EFFECT, from(StepParameterKey.SPECIAL_EFFECT, pSpecialEffect),
			from(StepParameterKey.PLAYER_ID, pPlayerId), from(StepParameterKey.ROLL_FOR_EFFECT, pRollForEffect),
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SPECIAL_EFFECT));
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.SPECIAL_EFFECT));
		sequence.add(StepId.NEXT_STEP, IStepLabel.END_SPECIAL_EFFECT);

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushEndTurnSequence(GameState pGameState) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
			"push endTurnSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.END_TURN);
		// may insert new sequence at this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushSpikedBallApoSequence(GameState gameState) {
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push spikedBallApoSequence onto stack");

		Sequence sequence = new Sequence(gameState);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
		gameState.getStepStack().push(sequence.getSequence());
	}


}

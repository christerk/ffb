package com.balancedbytes.games.ffb.server.step;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;

/**
 * Generator class adding sequences of steps to the stepStack of a gameState.
 * 
 * @author Kalimar
 */
public class SequenceGenerator {

	private static SequenceGenerator _INSTANCE = new SequenceGenerator();

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

		sequence.add(StepId.INIT_BLOCKING, 			param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_BLOCKING),
													param(StepParameterKey.BLOCK_DEFENDER_ID, pBlockDefenderId),
													param(StepParameterKey.USING_STAB, pUsingStab),
													param(StepParameterKey.MULTI_BLOCK_DEFENDER_ID, pMultiBlockDefenderId));
		sequence.add(StepId.BONE_HEAD,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.REALLY_STUPID,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.TAKE_ROOT,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.WILD_ANIMAL,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.BLOOD_LUST,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.GO_FOR_IT,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.FOUL_APPEARANCE,		param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.HORNS);
		sequence.add(StepId.BLOCK_STATISTICS);
		sequence.add(StepId.DAUNTLESS);
		sequence.add(StepId.DUMP_OFF);
		sequence.add(StepId.STAB,					param(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.APOTHECARY_DEFENDER));
		sequence.add(StepId.BLOCK_CHAINSAW, 		param(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.APOTHECARY_DEFENDER),
													param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.APOTHECARY_ATTACKER));
		sequence.add(StepId.BLOCK_BALL_AND_CHAIN,	param(StepParameterKey.GOTO_LABEL_ON_PUSHBACK, IStepLabel.PUSHBACK));
		sequence.add(StepId.BLOCK_ROLL);
		sequence.add(StepId.BLOCK_CHOICE, 			param(StepParameterKey.GOTO_LABEL_ON_DODGE, IStepLabel.DODGE_BLOCK),
													param(StepParameterKey.GOTO_LABEL_ON_JUGGERNAUT, IStepLabel.JUGGERNAUT),
													param(StepParameterKey.GOTO_LABEL_ON_PUSHBACK, IStepLabel.PUSHBACK));
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);

		// on blockChoice = BOTH_DOWN
		sequence.add(StepId.JUGGERNAUT,				IStepLabel.JUGGERNAUT,
													param(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.PUSHBACK));
		sequence.add(StepId.BOTH_DOWN);
		sequence.add(StepId.WRESTLE);
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);

		// on blockChoice = POW_PUSHBACK
		sequence.add(StepId.BLOCK_DODGE,			IStepLabel.DODGE_BLOCK);

		// on blockChoice = POW or PUSHBACK
		sequence.add(StepId.PUSHBACK,				IStepLabel.PUSHBACK);
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CROWD_PUSH));
		sequence.add(StepId.FOLLOWUP);
		sequence.add(StepId.SHADOWING);
		sequence.add(StepId.PICK_UP,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.DROP_FALLING_PLAYERS));
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);
		sequence.add(StepId.FALL_DOWN,				IStepLabel.FALL_DOWN);
		sequence.jump(IStepLabel.APOTHECARY_ATTACKER);

		// on blockChoice = SKULL
		sequence.add(StepId.DROP_FALLING_PLAYERS,	IStepLabel.DROP_FALLING_PLAYERS);
		sequence.add(StepId.APOTHECARY,				IStepLabel.APOTHECARY_DEFENDER,
													param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.APOTHECARY,				IStepLabel.APOTHECARY_ATTACKER,
													param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN,	IStepLabel.SCATTER_BALL);
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
		sequence.add(StepId.END_BLOCKING,			IStepLabel.END_BLOCKING);
		// may insert endTurn sequence add this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushPassSequence(GameState pGameState) {
		pushPassSequence(pGameState, null);
	}

	public void pushPassSequence(GameState pGameState, FieldCoordinate pTargetCoordinate) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
				"push passSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_PASSING, 			param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING),
													param(StepParameterKey.TARGET_COORDINATE, pTargetCoordinate));
		sequence.add(StepId.BONE_HEAD,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.REALLY_STUPID,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.TAKE_ROOT,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.WILD_ANIMAL,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.BLOOD_LUST,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.BOMBARDIER);
		sequence.add(StepId.ANIMOSITY,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.PASS_BLOCK,				param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING));
		sequence.add(StepId.DISPATCH_PASSING,		param(StepParameterKey.GOTO_LABEL_ON_END,IStepLabel.END_PASSING),
													param(StepParameterKey.GOTO_LABEL_ON_HAND_OVER, IStepLabel.HAND_OVER),
													param(StepParameterKey.GOTO_LABEL_ON_HAIL_MARY_PASS, IStepLabel.HAIL_MARY_PASS));
		sequence.add(StepId.INTERCEPT,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.PASS));
		sequence.add(StepId.SAFE_THROW,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.PASS, 					IStepLabel.PASS,
													param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING),
													param(StepParameterKey.GOTO_LABEL_ON_MISSED_PASS, IStepLabel.MISSED_PASS));
		sequence.jump(IStepLabel.SCATTER_BALL);
		sequence.add(StepId.HAIL_MARY_PASS,			IStepLabel.HAIL_MARY_PASS,
													param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.SCATTER_BALL));
		sequence.add(StepId.MISSED_PASS,			IStepLabel.MISSED_PASS);
		sequence.jump(IStepLabel.SCATTER_BALL);
		sequence.add(StepId.HAND_OVER,				IStepLabel.HAND_OVER);
		sequence.add(StepId.CATCH_SCATTER_THROW_IN,	IStepLabel.SCATTER_BALL);
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));

		sequence.add(StepId.END_PASSING,			IStepLabel.END_PASSING);
		// may insert bomb or endPlayerAction sequence add this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushFoulSequence(GameState pGameState) {
		pushFoulSequence(pGameState, null);
	}

	public void pushFoulSequence(GameState pGameState, String pFouldDefenderId) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
				"push foulSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_FOULING,			param(StepParameterKey.GOTO_LABEL_ON_END,IStepLabel.END_FOULING),
													param(StepParameterKey.FOUL_DEFENDER_ID, pFouldDefenderId));
		sequence.add(StepId.BONE_HEAD,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		sequence.add(StepId.REALLY_STUPID,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		sequence.add(StepId.TAKE_ROOT,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		sequence.add(StepId.WILD_ANIMAL,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		sequence.add(StepId.BLOOD_LUST);
		sequence.add(StepId.FOUL_CHAINSAW,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.APOTHECARY_ATTACKER));
		sequence.add(StepId.FOUL);
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.REFEREE,				param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
		sequence.add(StepId.BRIBES,					param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
		sequence.add(StepId.EJECT_PLAYER,			param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.jump(IStepLabel.END_FOULING);
		sequence.add(StepId.APOTHECARY,				IStepLabel.APOTHECARY_ATTACKER,
													param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.END_FOULING,			IStepLabel.END_FOULING);

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

		sequence.add(StepId.INIT_THROW_TEAM_MATE,	param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_THROW_TEAM_MATE),
													param(StepParameterKey.THROWN_PLAYER_ID, pThrownPlayerId),
													param(StepParameterKey.TARGET_COORDINATE, pTargetCoordinate));
		sequence.add(StepId.BONE_HEAD,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.REALLY_STUPID,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.TAKE_ROOT,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.WILD_ANIMAL,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.BLOOD_LUST);
		sequence.add(StepId.ALWAYS_HUNGRY,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.EAT_TEAM_MATE),
													param(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.THROW_TEAM_MATE,		param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FUMBLE_TTM_PASS));
		// insert scatterPlayerSequence at this point
		sequence.jump(IStepLabel.RIGHT_STUFF);
		sequence.add(StepId.FUMBLE_TTM_PASS,		IStepLabel.FUMBLE_TTM_PASS);
		sequence.add(StepId.RIGHT_STUFF,			IStepLabel.RIGHT_STUFF);
		sequence.jump(IStepLabel.APOTHECARY_THROWN_PLAYER);
		sequence.add(StepId.EAT_TEAM_MATE,			IStepLabel.EAT_TEAM_MATE);
		sequence.add(StepId.APOTHECARY,				IStepLabel.APOTHECARY_THROWN_PLAYER,
													param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.THROWN_PLAYER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
		sequence.add(StepId.END_THROW_TEAM_MATE, IStepLabel.END_THROW_TEAM_MATE);

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushScatterPlayerSequence(GameState pGameState, String pThrownPlayerId, PlayerState pThrownPlayerState,
			boolean pThrownPlayerHasBall, FieldCoordinate pThrownPlayerCoordinate, boolean pThrowScatter) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
				"push scatterPlayerSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_SCATTER_PLAYER,	param(StepParameterKey.THROWN_PLAYER_ID, pThrownPlayerId),
													param(StepParameterKey.THROWN_PLAYER_STATE, pThrownPlayerState),
													param(StepParameterKey.THROWN_PLAYER_HAS_BALL, pThrownPlayerHasBall),
													param(StepParameterKey.THROWN_PLAYER_COORDINATE, pThrownPlayerCoordinate),
													param(StepParameterKey.THROW_SCATTER, pThrowScatter));
		sequence.add(StepId.APOTHECARY,				IStepLabel.APOTHECARY_HIT_PLAYER,
													param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.HIT_PLAYER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
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
		sequence.add(StepId.SETUP,
				param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICKOFF));
		// inserts inducement sequence at this point
		sequence.add(StepId.SETUP,
				param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICKOFF));
		sequence.add(StepId.KICKOFF);
		sequence.add(StepId.KICKOFF_SCATTER_ROLL);
		sequence.add(StepId.KICKOFF_RETURN);
		// may insert select sequence at this point
		sequence.add(StepId.KICKOFF_RESULT_ROLL);
		sequence.add(StepId.APPLY_KICKOFF_RESULT,	param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICKOFF),
													param(StepParameterKey.GOTO_LABEL_ON_BLITZ, IStepLabel.BLITZ_TURN));
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.HOME));
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.AWAY));
		sequence.jump(IStepLabel.KICKOFF_ANIMATION);
		sequence.add(StepId.BLITZ_TURN,				IStepLabel.BLITZ_TURN);
		// may insert selectSequence at this point
		sequence.add(StepId.KICKOFF_ANIMATION,		IStepLabel.KICKOFF_ANIMATION);
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
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

		sequence.add(StepId.INIT_SELECTING,			param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_SELECTING),
													param(StepParameterKey.UPDATE_PERSISTENCE, pUpdatePersistence));
		sequence.add(StepId.BONE_HEAD,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.REALLY_STUPID,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.TAKE_ROOT,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.WILD_ANIMAL,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.BLOOD_LUST,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.JUMP_UP,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.STAND_UP,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.END_SELECTING,			IStepLabel.END_SELECTING);
		// may insert endTurn, pass, throwTeamMate, block, foul or moveSequence add
		// this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushEndGameSequence(GameState pGameState, boolean adminMode) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
				"push endGameSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_END_GAME,			param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_GAME),
													param(StepParameterKey.ADMIN_MODE, adminMode));
		sequence.add(StepId.PENALTY_SHOOTOUT);
		sequence.add(StepId.MVP);
		sequence.add(StepId.WINNINGS);
		sequence.add(StepId.FAN_FACTOR);
		sequence.add(StepId.PLAYER_LOSS);
		sequence.add(StepId.END_GAME,				IStepLabel.END_GAME);

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushMoveSequence(GameState pGameState) {
		pushMoveSequence(pGameState, null, null);
	}

	public void pushMoveSequence(GameState pGameState, FieldCoordinate[] pMoveStack, String pGazeVictimId) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
				"push moveSequence onto stack");

		Sequence sequence = new Sequence(pGameState);
		
		sequence.add(StepId.INIT_MOVING,			param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING),
													param(StepParameterKey.MOVE_STACK, pMoveStack),
													param(StepParameterKey.GAZE_VICTIM_ID, pGazeVictimId));
		sequence.add(StepId.BONE_HEAD,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.REALLY_STUPID,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.TAKE_ROOT,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.WILD_ANIMAL,			param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.BLOOD_LUST,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.HYPNOTIC_GAZE,			IStepLabel.HYPNOTIC_GAZE,
													param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING));
		sequence.add(StepId.MOVE_BALL_AND_CHAIN,	param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING),
													param(StepParameterKey.GOTO_LABEL_ON_FALL_DOWN, IStepLabel.FALL_DOWN));
		sequence.add(StepId.MOVE);
		sequence.add(StepId.GO_FOR_IT,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.TENTACLES,				param(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.END_MOVING));
		sequence.add(StepId.LEAP,					param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.MOVE_DODGE,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.DIVING_TACKLE,			param(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.RETRY_DODGE));
		sequence.jump(IStepLabel.SHADOWING);
		sequence.add(StepId.MOVE_DODGE,				IStepLabel.RETRY_DODGE,
													param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.DROP_DIVING_TACKLER);
		sequence.add(StepId.SHADOWING,				IStepLabel.SHADOWING);
		sequence.add(StepId.PICK_UP,				param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.SCATTER_BALL));
		sequence.jump(IStepLabel.SCATTER_BALL);
		sequence.add(StepId.DROP_DIVING_TACKLER,	IStepLabel.FALL_DOWN);
		sequence.add(StepId.SHADOWING); // falling player can be shadowed
		sequence.add(StepId.FALL_DOWN);
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN,	IStepLabel.SCATTER_BALL);
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
		sequence.add(StepId.END_MOVING,				IStepLabel.END_MOVING);
		// may insert endTurn or block sequence add this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushEndPlayerActionSequence(GameState pGameState, boolean feedingAllowed, boolean endPlayerAction,
			boolean endTurn) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
				"push endPlayerActionSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_FEEDING,			param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FEEDING),
													param(StepParameterKey.FEEDING_ALLOWED, feedingAllowed),
													param(StepParameterKey.END_PLAYER_ACTION, endPlayerAction),
													param(StepParameterKey.END_TURN, endTurn));
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.FEEDING));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_FEEDING,			IStepLabel.END_FEEDING);
		// inserts select or inducement sequence at this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushInducementSequence(GameState pGameState, InducementPhase pInducementPhase, boolean pHomeTeam) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
				"push inducementSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.INIT_INDUCEMENT,		param(StepParameterKey.INDUCEMENT_PHASE, pInducementPhase),
													param(StepParameterKey.HOME_TEAM, pHomeTeam));
		// may insert wizard or card sequence at this point
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
		sequence.add(StepId.END_INDUCEMENT);
		// may insert endTurn or inducement sequence at this point

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushCardSequence(GameState pGameState, Card pCard, boolean pHomeTeam) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
				"push cardSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.PLAY_CARD, 				param(StepParameterKey.CARD, pCard),
													param(StepParameterKey.HOME_TEAM, pHomeTeam));

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushWizardSequence(GameState pGameState) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
				"push wizardSequence onto stack");

		Sequence sequence = new Sequence(pGameState);

		sequence.add(StepId.WIZARD);
		// may insert multiple specialEffect sequences at this point
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));

		pGameState.getStepStack().push(sequence.getSequence());

	}

	public void pushBombSequence(GameState pGameState, String pCatcherId, boolean pPassFumble) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
				"push bombSequence onto stack");

		Sequence sequence = new Sequence(pGameState);
		
		sequence.add(StepId.INIT_BOMB, 				param(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_BOMB),
													param(StepParameterKey.CATCHER_ID, pCatcherId),
													param(StepParameterKey.PASS_FUMBLE, pPassFumble));
		// may insert multiple specialEffect sequences add this point
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
		sequence.add(StepId.END_BOMB,				IStepLabel.END_BOMB);
		// may insert endPlayerAction or pass sequence add this point
		pGameState.getStepStack().push(sequence.getSequence());
	}

	public void pushSpecialEffectSequence(GameState pGameState, SpecialEffect pSpecialEffect, String pPlayerId,
			boolean pRollForEffect) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(),
				"push specialEffectSequence onto stack (player " + pPlayerId + ")");

		Sequence sequence = new Sequence(pGameState);
		
		sequence.add(StepId.SPECIAL_EFFECT,			param(StepParameterKey.SPECIAL_EFFECT, pSpecialEffect),
													param(StepParameterKey.PLAYER_ID, pPlayerId),
													param(StepParameterKey.ROLL_FOR_EFFECT, pRollForEffect),
													param(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SPECIAL_EFFECT));
		sequence.add(StepId.APOTHECARY,				param(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.SPECIAL_EFFECT));
		sequence.add(StepId.NEXT_STEP, 				IStepLabel.END_SPECIAL_EFFECT);

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

	private class Sequence {
		private GameState gameState;
		private List<IStep> sequence;

		public Sequence(GameState gameState) {
			this.sequence = new ArrayList<IStep>();
			this.gameState = gameState;
		}

		public void add(StepId step, StepParameter... params) {
			add(step, null, params);
		}

		public void add(StepId step, String label, StepParameter... params) {
			StepParameterSet parameterSet = null;
			if (params != null) {
				parameterSet = new StepParameterSet();
				for (StepParameter p : params) {
					parameterSet.add(p);
				}
			}

			sequence.add(new StepFactory(gameState).create(step, label, parameterSet));
		}
		
		public void jump(String targetLabel) {
			add(StepId.GOTO_LABEL, param(StepParameterKey.GOTO_LABEL, targetLabel));
		}
		
		public List<IStep> getSequence() {
			return sequence;
		}
	}
	
	private StepParameter param(StepParameterKey pKey, Object pValue) {
		return new StepParameter(pKey, pValue);
	}

}

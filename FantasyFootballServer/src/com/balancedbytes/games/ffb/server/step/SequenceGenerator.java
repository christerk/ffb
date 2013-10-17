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
	
	private void add(List<IStep> pSequence, StepId pStepId, GameState pGameState) {
		add(pSequence, pStepId, pGameState, null, null);
	}
	
	private void add(List<IStep> pSequence, StepId pStepId, GameState pGameState, String pLabel) {
		add(pSequence, pStepId, pGameState, pLabel, null);
	}

	private void add(List<IStep> pSequence, StepId pStepId, GameState pGameState, StepParameterSet pParameterSet) {
		add(pSequence, pStepId, pGameState, null, pParameterSet);
	}

	private void add(List<IStep> pSequence, StepId pStepId, GameState pGameState, String pLabel, StepParameterSet pParameterSet) {
		pSequence.add(new StepFactory(pGameState).create(pStepId, pLabel, pParameterSet));
	}

	public void pushBlockSequence(GameState pGameState) {
		pushBlockSequence(pGameState, null, false, null);
	}

	public void pushBlockSequence(GameState pGameState, String pBlockDefenderId, boolean pUsingStab, String pMultiBlockDefenderId) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push blockSequence onto stack");

		List<IStep> blockSequence = new ArrayList<IStep>();

  	StepParameterSet initBlockingParameters = new StepParameterSet();
		initBlockingParameters.add(new StepParameter(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_BLOCKING));
		initBlockingParameters.add(new StepParameter(StepParameterKey.BLOCK_DEFENDER_ID, pBlockDefenderId));
		initBlockingParameters.add(new StepParameter(StepParameterKey.USING_STAB, pUsingStab));
		initBlockingParameters.add(new StepParameter(StepParameterKey.MULTI_BLOCK_DEFENDER_ID, pMultiBlockDefenderId));
		add(blockSequence, StepId.INIT_BLOCKING, pGameState, initBlockingParameters);
		
		add(blockSequence, StepId.BONE_HEAD, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		add(blockSequence, StepId.REALLY_STUPID, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		add(blockSequence, StepId.TAKE_ROOT, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		add(blockSequence, StepId.WILD_ANIMAL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		
		add(blockSequence, StepId.BLOOD_LUST, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		
		add(blockSequence, StepId.GO_FOR_IT, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		add(blockSequence, StepId.FOUL_APPEARANCE, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		add(blockSequence, StepId.HORNS, pGameState);
		add(blockSequence, StepId.BLOCK_STATISTICS, pGameState);
		add(blockSequence, StepId.DAUNTLESS, pGameState);
		add(blockSequence, StepId.DUMP_OFF, pGameState);
		add(blockSequence, StepId.STAB, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.APOTHECARY_DEFENDER));
		
		StepParameterSet chainsawParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.APOTHECARY_DEFENDER);
		addParameter(chainsawParameters, StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.APOTHECARY_ATTACKER);		
		add(blockSequence, StepId.BLOCK_CHAINSAW, pGameState, chainsawParameters);

		add(blockSequence, StepId.BLOCK_BALL_AND_CHAIN, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_PUSHBACK, IStepLabel.PUSHBACK));

		add(blockSequence, StepId.BLOCK_ROLL, pGameState);
		
		StepParameterSet blockChoiceParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_DODGE, IStepLabel.DODGE_BLOCK);
		addParameter(blockChoiceParameters, StepParameterKey.GOTO_LABEL_ON_JUGGERNAUT, IStepLabel.JUGGERNAUT);
		addParameter(blockChoiceParameters, StepParameterKey.GOTO_LABEL_ON_PUSHBACK, IStepLabel.PUSHBACK);
		add(blockSequence, StepId.BLOCK_CHOICE, pGameState,	blockChoiceParameters);
		add(blockSequence, StepId.GOTO_LABEL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL, IStepLabel.DROP_FALLING_PLAYERS));

		// on blockChoice = BOTH_DOWN
		add(blockSequence, StepId.JUGGERNAUT, pGameState, IStepLabel.JUGGERNAUT, createParameterSet(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.PUSHBACK));
		add(blockSequence, StepId.BOTH_DOWN, pGameState);
		add(blockSequence, StepId.WRESTLE, pGameState);
		add(blockSequence, StepId.GOTO_LABEL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL, IStepLabel.DROP_FALLING_PLAYERS));

		// on blockChoice = POW_PUSHBACK
		add(blockSequence, StepId.BLOCK_DODGE, pGameState, IStepLabel.DODGE_BLOCK);

		// on blockChoice = POW or PUSHBACK
		add(blockSequence, StepId.PUSHBACK, pGameState, IStepLabel.PUSHBACK);
		add(blockSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CROWD_PUSH));
		add(blockSequence, StepId.FOLLOWUP, pGameState);
		add(blockSequence, StepId.SHADOWING, pGameState);
		add(blockSequence, StepId.PICK_UP, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.DROP_FALLING_PLAYERS));
		add(blockSequence, StepId.GOTO_LABEL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL, IStepLabel.DROP_FALLING_PLAYERS));

		add(blockSequence, StepId.FALL_DOWN, pGameState, IStepLabel.FALL_DOWN);
		add(blockSequence, StepId.GOTO_LABEL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL, IStepLabel.APOTHECARY_ATTACKER));

		// on blockChoice = SKULL
		add(blockSequence, StepId.DROP_FALLING_PLAYERS, pGameState, IStepLabel.DROP_FALLING_PLAYERS);
		add(blockSequence, StepId.APOTHECARY, pGameState, IStepLabel.APOTHECARY_DEFENDER, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		add(blockSequence, StepId.APOTHECARY, pGameState, IStepLabel.APOTHECARY_ATTACKER,	createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		add(blockSequence, StepId.CATCH_SCATTER_THROW_IN, pGameState, IStepLabel.SCATTER_BALL);
		add(blockSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
		
		add(blockSequence, StepId.END_BLOCKING, pGameState, IStepLabel.END_BLOCKING);		
		// may insert endTurn sequence add this point

		pGameState.getStepStack().push(blockSequence);
		
	}

	public void pushPassSequence(GameState pGameState) {
		pushPassSequence(pGameState, null);
	}

	public void pushPassSequence(GameState pGameState, FieldCoordinate pTargetCoordinate) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push passSequence onto stack");

		List<IStep> passSequence = new ArrayList<IStep>();
		
  	StepParameterSet initPassingParameters = new StepParameterSet();
  	initPassingParameters.add(new StepParameter(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING));
  	initPassingParameters.add(new StepParameter(StepParameterKey.TARGET_COORDINATE, pTargetCoordinate));
		add(passSequence, StepId.INIT_PASSING, pGameState, initPassingParameters);
		
		add(passSequence, StepId.BONE_HEAD, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		add(passSequence, StepId.REALLY_STUPID, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		add(passSequence, StepId.TAKE_ROOT, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		add(passSequence, StepId.WILD_ANIMAL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		
		add(passSequence, StepId.BLOOD_LUST, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		
		add(passSequence, StepId.BOMBARDIER, pGameState);
		
		add(passSequence, StepId.ANIMOSITY, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		
		add(passSequence, StepId.PASS_BLOCK, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING));
		
		StepParameterSet dispatchPassingParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING);
		addParameter(dispatchPassingParameters, StepParameterKey.GOTO_LABEL_ON_HAND_OVER, IStepLabel.HAND_OVER);
		addParameter(dispatchPassingParameters, StepParameterKey.GOTO_LABEL_ON_HAIL_MARY_PASS, IStepLabel.HAIL_MARY_PASS);
		add(passSequence, StepId.DISPATCH_PASSING, pGameState, dispatchPassingParameters);
		add(passSequence, StepId.INTERCEPT, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.PASS));
		add(passSequence, StepId.SAFE_THROW, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		
		StepParameterSet passParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING);
		addParameter(passParameters, StepParameterKey.GOTO_LABEL_ON_MISSED_PASS, IStepLabel.MISSED_PASS);
		add(passSequence, StepId.PASS, pGameState, IStepLabel.PASS, passParameters);
		add(passSequence, StepId.GOTO_LABEL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL, IStepLabel.SCATTER_BALL));

		add(passSequence, StepId.HAIL_MARY_PASS, pGameState, IStepLabel.HAIL_MARY_PASS, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.SCATTER_BALL));

		add(passSequence, StepId.MISSED_PASS, pGameState, IStepLabel.MISSED_PASS);
		add(passSequence, StepId.GOTO_LABEL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL, IStepLabel.SCATTER_BALL));
		
		add(passSequence, StepId.HAND_OVER, pGameState, IStepLabel.HAND_OVER);

		add(passSequence, StepId.CATCH_SCATTER_THROW_IN, pGameState, IStepLabel.SCATTER_BALL);
		add(passSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));

		add(passSequence, StepId.END_PASSING, pGameState, IStepLabel.END_PASSING);		
		// may insert bomb or endPlayerAction sequence add this point
		
		pGameState.getStepStack().push(passSequence);
		
	}

	public void pushFoulSequence(GameState pGameState) {
		pushFoulSequence(pGameState, null);
	}

	public void pushFoulSequence(GameState pGameState, String pFouldDefenderId) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push foulSequence onto stack");
		
		List<IStep> foulSequence = new ArrayList<IStep>();
	
		StepParameterSet initParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING);
		addParameter(initParameters, StepParameterKey.FOUL_DEFENDER_ID, pFouldDefenderId);
		add(foulSequence, StepId.INIT_FOULING, pGameState, initParameters);

		add(foulSequence, StepId.BONE_HEAD, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		add(foulSequence, StepId.REALLY_STUPID, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		add(foulSequence, StepId.TAKE_ROOT, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		add(foulSequence, StepId.WILD_ANIMAL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		
		add(foulSequence, StepId.BLOOD_LUST, pGameState);
		
		add(foulSequence, StepId.FOUL_CHAINSAW, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.APOTHECARY_ATTACKER));
		add(foulSequence, StepId.FOUL, pGameState);
		add(foulSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		add(foulSequence, StepId.REFEREE, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
		add(foulSequence, StepId.BRIBES, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
		add(foulSequence, StepId.EJECT_PLAYER, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
		add(foulSequence, StepId.CATCH_SCATTER_THROW_IN, pGameState);

		add(foulSequence, StepId.GOTO_LABEL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL, IStepLabel.END_FOULING));
		add(foulSequence, StepId.APOTHECARY, pGameState, IStepLabel.APOTHECARY_ATTACKER, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		
		add(foulSequence, StepId.END_FOULING, pGameState, IStepLabel.END_FOULING);
		
		pGameState.getStepStack().push(foulSequence);
		
	}

	public void pushThrowTeamMateSequence(GameState pGameState) {
		pushThrowTeamMateSequence(pGameState, null, null);
	}

	public void pushThrowTeamMateSequence(GameState pGameState, String pThrownPlayerId, FieldCoordinate pTargetCoordinate) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push throwTeamMateSequence onto stack");
		
		List<IStep> throwTeamMateSequence = new ArrayList<IStep>();

		StepParameterSet initParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_THROW_TEAM_MATE);
		addParameter(initParameters, StepParameterKey.THROWN_PLAYER_ID, pThrownPlayerId);
		addParameter(initParameters, StepParameterKey.TARGET_COORDINATE, pTargetCoordinate);
		add(throwTeamMateSequence, StepId.INIT_THROW_TEAM_MATE, pGameState, initParameters);
		
		add(throwTeamMateSequence, StepId.BONE_HEAD, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		add(throwTeamMateSequence, StepId.REALLY_STUPID, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		add(throwTeamMateSequence, StepId.TAKE_ROOT, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		add(throwTeamMateSequence, StepId.WILD_ANIMAL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		
		add(throwTeamMateSequence, StepId.BLOOD_LUST, pGameState);
		
		StepParameterSet alwaysHungryParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.EAT_TEAM_MATE);
		addParameter(alwaysHungryParameters, StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.END_THROW_TEAM_MATE);
		add(throwTeamMateSequence, StepId.ALWAYS_HUNGRY, pGameState, alwaysHungryParameters);
		
		add(throwTeamMateSequence, StepId.THROW_TEAM_MATE, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FUMBLE_TTM_PASS));
		// insert scatterPlayerSequence at this point
		
		add(throwTeamMateSequence, StepId.GOTO_LABEL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL, IStepLabel.RIGHT_STUFF));
		
		add(throwTeamMateSequence, StepId.FUMBLE_TTM_PASS, pGameState, IStepLabel.FUMBLE_TTM_PASS);
		add(throwTeamMateSequence, StepId.RIGHT_STUFF, pGameState, IStepLabel.RIGHT_STUFF);
		add(throwTeamMateSequence, StepId.GOTO_LABEL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL, IStepLabel.APOTHECARY_THROWN_PLAYER));

		add(throwTeamMateSequence, StepId.EAT_TEAM_MATE, pGameState, IStepLabel.EAT_TEAM_MATE);
		
		add(throwTeamMateSequence, StepId.APOTHECARY, pGameState, IStepLabel.APOTHECARY_THROWN_PLAYER, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.THROWN_PLAYER));
		add(throwTeamMateSequence, StepId.CATCH_SCATTER_THROW_IN, pGameState);
		add(throwTeamMateSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));

		add(throwTeamMateSequence, StepId.END_THROW_TEAM_MATE, pGameState, IStepLabel.END_THROW_TEAM_MATE);

		pGameState.getStepStack().push(throwTeamMateSequence);

	}
	
	public void pushScatterPlayerSequence(
		GameState pGameState,
		String pThrownPlayerId,
		PlayerState pThrownPlayerState,
		boolean pThrownPlayerHasBall,
		FieldCoordinate pThrownPlayerCoordinate,
		boolean pThrowScatter
	) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push scatterPlayerSequence onto stack");

		List<IStep> scatterPlayerSequence = new ArrayList<IStep>();

		StepParameterSet initParameters = createParameterSet(StepParameterKey.THROWN_PLAYER_ID, pThrownPlayerId);
		addParameter(initParameters, StepParameterKey.THROWN_PLAYER_STATE, pThrownPlayerState);
		addParameter(initParameters, StepParameterKey.THROWN_PLAYER_HAS_BALL, pThrownPlayerHasBall);
		addParameter(initParameters, StepParameterKey.THROWN_PLAYER_COORDINATE, pThrownPlayerCoordinate);
		addParameter(initParameters, StepParameterKey.THROW_SCATTER, pThrowScatter);
		add(scatterPlayerSequence, StepId.INIT_SCATTER_PLAYER, pGameState, initParameters);
		
		add(scatterPlayerSequence, StepId.APOTHECARY, pGameState, IStepLabel.APOTHECARY_HIT_PLAYER, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.HIT_PLAYER));
		add(scatterPlayerSequence, StepId.CATCH_SCATTER_THROW_IN, pGameState);
		add(scatterPlayerSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
		
		add(scatterPlayerSequence, StepId.END_SCATTER_PLAYER, pGameState);
		// may insert a new scatterPlayerSequence at this point

		pGameState.getStepStack().push(scatterPlayerSequence);

	}
	

	public void pushStartGameSequence(GameState pGameState) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push startGameSequence onto stack");

		List<IStep> startGameSequence = new ArrayList<IStep>();

		add(startGameSequence, StepId.INIT_START_GAME, pGameState);
		
		add(startGameSequence, StepId.WEATHER, pGameState);
		add(startGameSequence, StepId.PETTY_CASH, pGameState);
		add(startGameSequence, StepId.BUY_CARDS, pGameState);
		add(startGameSequence, StepId.BUY_INDUCEMENTS, pGameState);
		// inserts inducement sequence at this point
		add(startGameSequence, StepId.SPECTATORS, pGameState);
		// continues with kickoffSequence after that

		pGameState.getStepStack().push(startGameSequence);

	}

	public void pushKickoffSequence(GameState pGameState, boolean pWithCoinChoice) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push kickoffSequence onto stack");

		List<IStep> kickoffSequence = new ArrayList<IStep>();
		
		if (pWithCoinChoice) {
			add(kickoffSequence, StepId.COIN_CHOICE, pGameState);
			add(kickoffSequence, StepId.RECEIVE_CHOICE, pGameState);
		}

		add(kickoffSequence, StepId.INIT_KICKOFF, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICKOFF));
		
		add(kickoffSequence, StepId.KICKOFF_SCATTER_ROLL, pGameState);
		add(kickoffSequence, StepId.KICKOFF_RETURN, pGameState);
		
		// may insert select sequence at this point
		
		add(kickoffSequence, StepId.KICKOFF_RESULT_ROLL, pGameState);

		StepParameterSet applyKickoffResultParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICKOFF);
		addParameter(applyKickoffResultParameters, StepParameterKey.GOTO_LABEL_ON_BLITZ, IStepLabel.BLITZ_TURN);
		add(kickoffSequence, StepId.APPLY_KICKOFF_RESULT, pGameState, applyKickoffResultParameters);
		
		add(kickoffSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.HOME));
		add(kickoffSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.AWAY));
		add(kickoffSequence, StepId.GOTO_LABEL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL, IStepLabel.KICKOFF_ANIMATION));
		
		add(kickoffSequence, StepId.BLITZ_TURN, pGameState, IStepLabel.BLITZ_TURN);
		// may insert selectSequence at this point
		
		add(kickoffSequence, StepId.KICKOFF_ANIMATION, pGameState, IStepLabel.KICKOFF_ANIMATION);
		add(kickoffSequence, StepId.CATCH_SCATTER_THROW_IN, pGameState);
		add(kickoffSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
		
		add(kickoffSequence, StepId.TOUCHBACK, pGameState);
		add(kickoffSequence, StepId.CATCH_SCATTER_THROW_IN, pGameState);

		add(kickoffSequence, StepId.END_KICKOFF, pGameState, IStepLabel.END_KICKOFF);
		// continues with endTurnSequence after that
		
		pGameState.getStepStack().push(kickoffSequence);

	}
	
	public void pushSelectSequence(GameState pGameState, boolean pUpdatePersistence) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push selectSequence onto stack");

		List<IStep> selectSequence = new ArrayList<IStep>();
    
		StepParameterSet initParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_SELECTING);
		addParameter(initParameters, StepParameterKey.UPDATE_PERSISTENCE, pUpdatePersistence);
		add(selectSequence, StepId.INIT_SELECTING, pGameState, initParameters);
		
		add(selectSequence, StepId.BONE_HEAD, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		add(selectSequence, StepId.REALLY_STUPID, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		add(selectSequence, StepId.TAKE_ROOT, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		add(selectSequence, StepId.WILD_ANIMAL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		
		add(selectSequence, StepId.BLOOD_LUST, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));

		add(selectSequence, StepId.JUMP_UP, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		add(selectSequence, StepId.STAND_UP, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));

		add(selectSequence, StepId.END_SELECTING, pGameState, IStepLabel.END_SELECTING);
		// may insert endTurn, pass, throwTeamMate, block, foul or moveSequence add this point

		pGameState.getStepStack().push(selectSequence);

	}

	public void pushEndGameSequence(GameState pGameState, boolean pAutomaticWinnings) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push endGameSequence onto stack");

		List<IStep> endGameSequence = new ArrayList<IStep>();

		add(endGameSequence, StepId.INIT_END_GAME, pGameState);
		
		add(endGameSequence, StepId.PENALTY_SHOOTOUT, pGameState);
		add(endGameSequence, StepId.MVP, pGameState);
		add(endGameSequence, StepId.WINNINGS, pGameState, createParameterSet(StepParameterKey.AUTOMATIC_RE_ROLL, pAutomaticWinnings));
		add(endGameSequence, StepId.FAN_FACTOR, pGameState);
		add(endGameSequence, StepId.PLAYER_LOSS, pGameState);
		
		add(endGameSequence, StepId.END_GAME, pGameState);

		pGameState.getStepStack().push(endGameSequence);
		
	}

	public void pushMoveSequence(GameState pGameState) {
		pushMoveSequence(pGameState, null, null);
	}

	public void pushMoveSequence(GameState pGameState, FieldCoordinate[] pMoveStack, String pGazeVictimId) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push moveSequence onto stack");

		List<IStep> moveSequence = new ArrayList<IStep>();

		StepParameterSet initMovingParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING);
		addParameter(initMovingParameters, StepParameterKey.MOVE_STACK, pMoveStack);
		addParameter(initMovingParameters, StepParameterKey.GAZE_VICTIM_ID, pGazeVictimId);
		add(moveSequence, StepId.INIT_MOVING, pGameState,	initMovingParameters);

		add(moveSequence, StepId.BONE_HEAD, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		add(moveSequence, StepId.REALLY_STUPID, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		add(moveSequence, StepId.TAKE_ROOT, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		add(moveSequence, StepId.WILD_ANIMAL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		
		add(moveSequence, StepId.BLOOD_LUST, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		
		add(moveSequence, StepId.HYPNOTIC_GAZE, pGameState, IStepLabel.HYPNOTIC_GAZE, createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING));

		StepParameterSet ballAndChainParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING);
		addParameter(ballAndChainParameters, StepParameterKey.GOTO_LABEL_ON_FALL_DOWN, IStepLabel.FALL_DOWN);
		add(moveSequence, StepId.MOVE_BALL_AND_CHAIN, pGameState,	ballAndChainParameters);

		add(moveSequence, StepId.MOVE, pGameState);
		add(moveSequence, StepId.GO_FOR_IT, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		add(moveSequence, StepId.TENTACLES, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.END_MOVING));
		add(moveSequence, StepId.LEAP, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));

		add(moveSequence, StepId.MOVE_DODGE, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		add(moveSequence, StepId.DIVING_TACKLE, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.RETRY_DODGE));
		add(moveSequence, StepId.GOTO_LABEL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL, IStepLabel.SHADOWING));
		add(moveSequence, StepId.MOVE_DODGE, pGameState, IStepLabel.RETRY_DODGE, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		
		add(moveSequence, StepId.DROP_DIVING_TACKLER, pGameState);
		add(moveSequence, StepId.SHADOWING, pGameState, IStepLabel.SHADOWING);
		add(moveSequence, StepId.PICK_UP, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.SCATTER_BALL));
		add(moveSequence, StepId.GOTO_LABEL, pGameState, createParameterSet(StepParameterKey.GOTO_LABEL, IStepLabel.SCATTER_BALL));
		
		add(moveSequence, StepId.DROP_DIVING_TACKLER, pGameState, IStepLabel.FALL_DOWN);
		add(moveSequence, StepId.SHADOWING, pGameState);  // falling player can be shadowed
		add(moveSequence, StepId.FALL_DOWN, pGameState);
		
		add(moveSequence, StepId.APOTHECARY, pGameState, IStepLabel.APOTHECARY_ATTACKER, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		add(moveSequence, StepId.CATCH_SCATTER_THROW_IN, pGameState, IStepLabel.SCATTER_BALL);
		add(moveSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
		
		add(moveSequence, StepId.END_MOVING, pGameState, IStepLabel.END_MOVING);		
		// may insert endTurn or block sequence add this point

		pGameState.getStepStack().push(moveSequence);

	}

	public void pushEndPlayerActionSequence(GameState pGameState, boolean pFeedingAllowed, boolean pEndTurn) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push endPlayerActionSequence onto stack");

		List<IStep> feedSequence = new ArrayList<IStep>();
		
		StepParameterSet initParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FEEDING);
		initParameters.add(new StepParameter(StepParameterKey.FEEDING_ALLOWED, pFeedingAllowed));
		initParameters.add(new StepParameter(StepParameterKey.END_TURN, pEndTurn));
		add(feedSequence, StepId.INIT_FEEDING, pGameState, initParameters);
		
		add(feedSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.FEEDING));
		add(feedSequence, StepId.CATCH_SCATTER_THROW_IN, pGameState);
		
		add(feedSequence, StepId.END_FEEDING, pGameState, IStepLabel.END_FEEDING);
		// inserts select or inducement sequence at this point
		
		pGameState.getStepStack().push(feedSequence);
		
	}

	public void pushInducementSequence(GameState pGameState, InducementPhase pInducementPhase, boolean pHomeTeam) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push inducementSequence onto stack");

		List<IStep> inducementSequence = new ArrayList<IStep>();

		StepParameterSet initParameters = createParameterSet(StepParameterKey.INDUCEMENT_PHASE, pInducementPhase);
		addParameter(initParameters, StepParameterKey.HOME_TEAM, pHomeTeam);
		add(inducementSequence, StepId.INIT_INDUCEMENT, pGameState, initParameters);
		// may insert wizard or card sequence at this point

		add(inducementSequence, StepId.END_INDUCEMENT, pGameState);
		// may insert endTurn or inducement sequence at this point
		
		pGameState.getStepStack().push(inducementSequence);

	}

	public void pushCardSequence(GameState pGameState, Card pCard, boolean pHomeTeam) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push cardSequence onto stack");

		List<IStep> cardSequence = new ArrayList<IStep>();

		StepParameterSet initParameters = createParameterSet(StepParameterKey.CARD, pCard);
		addParameter(initParameters, StepParameterKey.HOME_TEAM, pHomeTeam);
		add(cardSequence, StepId.INIT_CARD, pGameState, initParameters);
		
		pGameState.getStepStack().push(cardSequence);

	}

	public void pushWizardSequence(GameState pGameState) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push wizardSequence onto stack");

		List<IStep> wizardSequence = new ArrayList<IStep>();

		add(wizardSequence, StepId.WIZARD, pGameState);
		// may insert multiple specialEffect sequences at this point
		
		add(wizardSequence, StepId.CATCH_SCATTER_THROW_IN, pGameState);
		add(wizardSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
		
		pGameState.getStepStack().push(wizardSequence);

	}

	public void pushBombSequence(GameState pGameState, String pCatcherId, boolean pPassFumble) {
		
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push bombSequence onto stack");

		List<IStep> bombSequence = new ArrayList<IStep>();

		StepParameterSet initParameters = createParameterSet(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_BOMB);
		initParameters.add(new StepParameter(StepParameterKey.CATCHER_ID, pCatcherId));
		initParameters.add(new StepParameter(StepParameterKey.PASS_FUMBLE, pPassFumble));
		add(bombSequence, StepId.INIT_BOMB, pGameState, initParameters);
		// may insert multiple specialEffect sequences add this point

		add(bombSequence, StepId.CATCH_SCATTER_THROW_IN, pGameState);
		add(bombSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));

		add(bombSequence, StepId.END_BOMB, pGameState, IStepLabel.END_BOMB);
		// may insert endPlayerAction or pass sequence add this point
		
		pGameState.getStepStack().push(bombSequence);

	}
	
	public void pushSpecialEffectSequence(GameState pGameState, SpecialEffect pSpecialEffect, String pPlayerId, boolean pRollForEffect) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push specialEffectSequence onto stack (player " + pPlayerId + ")");

		List<IStep> spellEffectSequence = new ArrayList<IStep>();
		
		StepParameterSet spellEffectParameters = createParameterSet(StepParameterKey.SPECIAL_EFFECT, pSpecialEffect);
		addParameter(spellEffectParameters, StepParameterKey.PLAYER_ID, pPlayerId);
		addParameter(spellEffectParameters, StepParameterKey.ROLL_FOR_EFFECT, pRollForEffect);
		addParameter(spellEffectParameters, StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SPECIAL_EFFECT);
		add(spellEffectSequence, StepId.SPECIAL_EFFECT, pGameState, spellEffectParameters);
		
		add(spellEffectSequence, StepId.APOTHECARY, pGameState, createParameterSet(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.SPECIAL_EFFECT));
		add(spellEffectSequence, StepId.NEXT_STEP, pGameState, IStepLabel.END_SPECIAL_EFFECT);
		
		pGameState.getStepStack().push(spellEffectSequence);
		
	}

	public void pushEndTurnSequence(GameState pGameState) {

		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pGameState.getId(), "push endTurnSequence onto stack");

		List<IStep> endTurnSequence = new ArrayList<IStep>();

		add(endTurnSequence, StepId.END_TURN, pGameState);
		// may insert new sequence at this point
		
		pGameState.getStepStack().push(endTurnSequence);

	}
	
	private StepParameterSet createParameterSet(StepParameterKey pKey, Object pValue) {
		StepParameterSet parameterSet = new StepParameterSet(); 
		addParameter(parameterSet, pKey, pValue);
		return parameterSet;
	}
	
	private void addParameter(StepParameterSet pParameterSet, StepParameterKey pKey, Object pValue) {
		pParameterSet.add(new StepParameter(pKey, pValue));
	}

}

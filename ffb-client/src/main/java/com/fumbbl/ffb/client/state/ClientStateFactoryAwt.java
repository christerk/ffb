package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.bb2016.ClientStateKickTeamMate;
import com.fumbbl.ffb.client.state.bb2020.ClientStateGazeMove;
import com.fumbbl.ffb.client.state.bb2020.ClientStateHitAndRun;
import com.fumbbl.ffb.client.state.bb2020.ClientStateKickEmBlitz;
import com.fumbbl.ffb.client.state.bb2020.ClientStateKickEmBlock;
import com.fumbbl.ffb.client.state.bb2020.ClientStateKickTeamMateLikeThrow;
import com.fumbbl.ffb.client.state.bb2020.ClientStateMaximumCarnage;
import com.fumbbl.ffb.client.state.bb2020.ClientStatePutridRegurgitationBlitz;
import com.fumbbl.ffb.client.state.bb2020.ClientStatePutridRegurgitationBlock;
import com.fumbbl.ffb.client.state.bb2020.ClientStateRaidingParty;
import com.fumbbl.ffb.client.state.bb2020.ClientStateSelectBlitzTarget;
import com.fumbbl.ffb.client.state.bb2020.ClientStateSelectBlockKind;
import com.fumbbl.ffb.client.state.bb2020.ClientStateSelectGazeTarget;
import com.fumbbl.ffb.client.state.bb2020.ClientStateStab;
import com.fumbbl.ffb.client.state.bb2020.ClientStateSynchronousMultiBlock;
import com.fumbbl.ffb.client.state.bb2020.ClientStateThenIStartedBlastin;
import com.fumbbl.ffb.client.state.bb2020.ClientStateThrowKeg;
import com.fumbbl.ffb.client.state.bb2020.ClientStateTrickster;

/**
 * @author Kalimar
 */
public class ClientStateFactoryAwt extends ClientStateFactory<FantasyFootballClientAwt> {
	
	public ClientStateFactoryAwt(FantasyFootballClientAwt client) {
		super(client);
	}

	public void registerStates() {
		register(new ClientStateLogin(client));
		register(new ClientStateStartGame(client));
		register(new ClientStateSpectate(client));
		register(new ClientStateSelect(client));
		register(new ClientStatePass(client));
		register(new ClientStateHandOver(client));
		register(new ClientStateMove(client));
		register(new ClientStateKickoff(client));
		register(new ClientStateBlock(client));
		register(new ClientStatePushback(client));
		register(new ClientStateInterception(client));
		register(new ClientStateBlitz(client));
		register(new ClientStateFoul(client));
		register(new ClientStateSetup(client));
		register(new ClientStateQuickSnap(client));
		register(new ClientStateHighKick(client));
		register(new ClientStateTouchback(client));
		register(new ClientStateWaitForOpponent(client));
		register(new ClientStateReplay(client));
		register(new ClientStateThrowTeamMate(client));
		register(new ClientStateKickTeamMateLikeThrow(client));
		register(new ClientStateKickTeamMate(client));
		register(new ClientStateSwoop(client));
		register(new ClientStateDumpOff(client));
		register(new ClientStateWaitForSetup(client));
		register(new ClientStateGaze(client));
		register(new ClientStateKickoffReturn(client));
		register(new ClientStateSwarming(client));
		register(new ClientStateWizard(client));
		register(new ClientStatePassBlock(client));
		register(new ClientStateBomb(client));
		register(new ClientStateIllegalSubstitution(client));
		register(new ClientStateSelectBlitzTarget(client));
		register(new ClientStateSynchronousMultiBlock(client));
		register(new ClientStatePlaceBall(client));
		register(new ClientStateSolidDefence(client));
		register(new ClientStateSelectGazeTarget(client));
		register(new ClientStateGazeMove(client));
		register(new ClientStateThrowKeg(client));
		register(new ClientStateRaidingParty(client));
		register(new ClientStateSelectBlockKind(client));
		register(new ClientStateMaximumCarnage(client));
		register(new ClientStateHitAndRun(client));
		register(new ClientStatePutridRegurgitationBlitz(client));
		register(new ClientStatePutridRegurgitationBlock(client));
		register(new ClientStateKickEmBlitz(client));
		register(new ClientStateKickEmBlock(client));
		register(new ClientStateTrickster(client));
		register(new ClientStateThenIStartedBlastin(client));
		register(new ClientStateStab(client));
	}
	
}

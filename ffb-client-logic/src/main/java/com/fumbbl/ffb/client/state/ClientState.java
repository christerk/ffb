package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.LogicModule;

public abstract class ClientState<T extends LogicModule> {

	private final FantasyFootballClient fClient;
	protected final T logicModule;

	protected FieldCoordinate fSelectSquareCoordinate;

	public ClientState(FantasyFootballClient pClient, T logicModule) {
		fClient = pClient;
		this.logicModule = logicModule;
	}

	public abstract void leaveState();

	public abstract void enterState();

	public abstract ClientStateId getId();

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public void hideSelectSquare() {
		fSelectSquareCoordinate = null;
	}

	public void showSelectSquare(FieldCoordinate pCoordinate) {
		if (pCoordinate != null) {
			fSelectSquareCoordinate = pCoordinate;
			drawSelectSquare();
		}
	}

	abstract protected void drawSelectSquare();

	protected void prePerform() {
	}

	protected void postPerform() {
	}

	protected boolean playerActivationUsed() {
		return getClient().getGame().getActingPlayer().hasActed();
	}

	public final void endTurn() {
		logicModule.endTurn();
		postEndTurn();
	}

	protected void postEndTurn() {
	}
}


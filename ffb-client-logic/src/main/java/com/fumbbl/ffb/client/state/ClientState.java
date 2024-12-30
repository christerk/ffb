package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.net.NetCommand;

import java.awt.event.MouseEvent;

public abstract class ClientState<T extends LogicModule, C extends FantasyFootballClient> {

	private final C fClient;
	protected final T logicModule;

	protected FieldCoordinate fSelectSquareCoordinate;

	public ClientState(C pClient, T logicModule) {
		fClient = pClient;
		this.logicModule = logicModule;
	}

	public void leaveState() {
		logicModule.teardown();
	}

	public void enterState() {
		logicModule.postInit();
		initUI();
	}

	public abstract void initUI();

	public final ClientStateId getId() {
		return logicModule.getId();
	}

	public C getClient() {
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

	protected void prePerform(int menuKey) {
	}

	protected void postPerform(int menuKey) {
	}

	public final void endTurn() {
		logicModule.endTurn();
		postEndTurn();
	}

	protected void postEndTurn() {
	}

	public T getLogicModule() {
		return logicModule;
	}

	public void handleCommand(NetCommand pNetCommand) {

	}

	// TODO remove once components and dialogs are moved to UI module
	public void setClickable(boolean b) {
	}

	public void mouseMoved(MouseEvent pMouseEvent) {
	}

	public void mouseDragged(MouseEvent pMouseEvent) {
	}

	public void mouseClicked(MouseEvent pMouseEvent) {
	}

	public void mouseEntered(MouseEvent pMouseEvent) {
	}

	public void mouseExited(MouseEvent pMouseEvent) {
	}

	public void mousePressed(MouseEvent pMouseEvent) {
	}

	public void mouseReleased(MouseEvent pMouseEvent) {
	}

	public boolean actionKeyPressed(ActionKey actionKey) {
		return false;
	}

	public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
		return true;
	}

	public boolean isDragAllowed(FieldCoordinate coordinate) {
		return true;
	}

	public boolean isDropAllowed(FieldCoordinate dragEndPosition) {
		return true;
	}
}


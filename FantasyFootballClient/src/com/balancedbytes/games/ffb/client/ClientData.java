package com.balancedbytes.games.ffb.client;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.model.Player;

public class ClientData {

	private Player<?> fSelectedPlayer;
	private FieldCoordinate fDragEndPosition;
	private FieldCoordinate fDragStartPosition;

	private String fStatusTitle;
	private String fStatusMessage;
	private StatusType fStatusType;

	private int fNrOfBlockDice;
	private int[] fBlockRoll;
	private int fBlockDiceIndex;

	private boolean fActingPlayerUpdated;
	private boolean fTurnTimerStopped;
	private boolean fEndTurnButtonHidden;

	private int fSpectators;
	private SpecialEffect fWizardSpell;

	public ClientData() {
		super();
	}

	public Player<?> getSelectedPlayer() {
		return fSelectedPlayer;
	}

	public void setSelectedPlayer(Player<?> pPlayer) {
		fSelectedPlayer = pPlayer;
	}

	public FieldCoordinate getDragEndPosition() {
		return fDragEndPosition;
	}

	public void setDragEndPosition(FieldCoordinate pEndPosition) {
		fDragEndPosition = pEndPosition;
	}

	public FieldCoordinate getDragStartPosition() {
		return fDragStartPosition;
	}

	public void setDragStartPosition(FieldCoordinate pStartPosition) {
		fDragStartPosition = pStartPosition;
	}

	public void setBlockDiceResult(int pNrOfBlockDice, int[] pBlockRoll, int pBlockDiceIndex) {
		fNrOfBlockDice = pNrOfBlockDice;
		fBlockRoll = pBlockRoll;
		fBlockDiceIndex = pBlockDiceIndex;
	}

	public void clearBlockDiceResult() {
		setBlockDiceResult(0, null, -1);
	}

	public int getNrOfBlockDice() {
		return fNrOfBlockDice;
	}

	public int[] getBlockRoll() {
		return fBlockRoll;
	}

	public int getBlockDiceIndex() {
		return fBlockDiceIndex;
	}

	public void setStatus(String pTitle, String pMessage, StatusType pType) {
		fStatusTitle = pTitle;
		fStatusMessage = pMessage;
		fStatusType = pType;
	}

	public void clearStatus() {
		setStatus(null, null, null);
	}

	public String getStatusTitle() {
		return fStatusTitle;
	}

	public String getStatusMessage() {
		return fStatusMessage;
	}

	public StatusType getStatusType() {
		return fStatusType;
	}

	public void setActingPlayerUpdated(boolean pActingPlayerUpdated) {
		fActingPlayerUpdated = pActingPlayerUpdated;
	}

	public boolean isActingPlayerUpdated() {
		return fActingPlayerUpdated;
	}

	public void setTurnTimerStopped(boolean pTimerStopped) {
		fTurnTimerStopped = pTimerStopped;
	}

	public boolean isTurnTimerStopped() {
		return fTurnTimerStopped;
	}

	public int getSpectators() {
		return fSpectators;
	}

	public void setSpectators(int pSpectators) {
		fSpectators = pSpectators;
	}

	public void setWizardSpell(SpecialEffect pWizardSpell) {
		fWizardSpell = pWizardSpell;
	}

	public SpecialEffect getWizardSpell() {
		return fWizardSpell;
	}

	public boolean isEndTurnButtonHidden() {
		return fEndTurnButtonHidden;
	}

	public void setEndTurnButtonHidden(boolean pEndTurnButtonHidden) {
		fEndTurnButtonHidden = pEndTurnButtonHidden;
	}

	public void clear() {
		setSelectedPlayer(null);
		setDragStartPosition(null);
		setDragEndPosition(null);
		clearBlockDiceResult();
		clearStatus();
		setActingPlayerUpdated(false);
		setWizardSpell(null);
		setEndTurnButtonHidden(false);
	}

}

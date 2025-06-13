package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.model.BlockRoll;
import com.fumbbl.ffb.model.Player;

import java.util.ArrayList;
import java.util.List;

public class ClientData {

	private Player<?> fSelectedPlayer;
	private FieldCoordinate fDragEndPosition;
	private FieldCoordinate fDragStartPosition;

	private String fStatusTitle;
	private String fStatusMessage;
	private StatusType fStatusType;

	List<BlockRoll> blockRolls = new ArrayList<>();

	private boolean fActingPlayerUpdated;
	private boolean fTurnTimerStopped;
	private boolean fEndTurnButtonHidden;

	private int spectatorCount;
	private SpecialEffect fWizardSpell;
	private List<String> spectators = new ArrayList<>();
	private String coachControllingReplay;

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

	public void setBlockDiceResult(List<BlockRoll> blockRolls) {
		clearBlockDiceResult();
		this.blockRolls.addAll(blockRolls);
	}

	public void clearBlockDiceResult() {
		blockRolls.clear();
	}

	public List<BlockRoll> getBlockRolls() {
		return blockRolls;
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

	public int getSpectatorCount() {
		return spectatorCount;
	}

	public void setSpectatorCount(int pSpectators) {
		spectatorCount = pSpectators;
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

	public void setSpectators(List<String> spectators) {
		this.spectators = spectators;
	}

	public List<String> getSpectators() {
		return spectators;
	}

	public String getCoachControllingReplay() {
		return coachControllingReplay;
	}

	public void setCoachControllingReplay(String coachControllingReplay) {
		this.coachControllingReplay = coachControllingReplay;
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

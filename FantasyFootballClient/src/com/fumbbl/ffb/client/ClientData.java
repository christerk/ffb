package com.fumbbl.ffb.client;

import java.util.ArrayList;
import java.util.List;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.model.BlockRoll;
import com.fumbbl.ffb.model.Player;

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

	private boolean log;

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
		if (log && fSelectedPlayer == null) {
			try {
				throw new Exception("Setting selectedPlayer to null");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public FieldCoordinate getDragEndPosition() {
		return fDragEndPosition;
	}

	public void setDragEndPosition(FieldCoordinate pEndPosition) {
		fDragEndPosition = pEndPosition;
		if (log && fDragEndPosition == null) {
			try {
				throw new Exception("Setting dragEndPosition to null");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public FieldCoordinate getDragStartPosition() {
		return fDragStartPosition;
	}

	public void setDragStartPosition(FieldCoordinate pStartPosition) {
		fDragStartPosition = pStartPosition;
		if (log && fDragStartPosition == null) {
			try {
				throw new Exception("Setting dragStartPosition to null");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
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

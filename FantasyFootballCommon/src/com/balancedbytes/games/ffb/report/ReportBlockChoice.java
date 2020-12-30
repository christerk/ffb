package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.BlockResult;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportBlockChoice implements IReport {

	private int fNrOfDice;
	private int[] fBlockRoll;
	private int fDiceIndex;
	private BlockResult fBlockResult;
	private String fDefenderId;

	public ReportBlockChoice() {
		super();
	}

	public ReportBlockChoice(int pNrOfDice, int[] pBlockRoll, int pDiceIndex, BlockResult pBlockResult,
			String pDefenderId) {
		fNrOfDice = pNrOfDice;
		fBlockRoll = pBlockRoll;
		fDiceIndex = pDiceIndex;
		fBlockResult = pBlockResult;
		fDefenderId = pDefenderId;
	}

	public ReportId getId() {
		return ReportId.BLOCK_CHOICE;
	}

	public int getNrOfDice() {
		return fNrOfDice;
	}

	public int[] getBlockRoll() {
		return fBlockRoll;
	}

	public int getDiceIndex() {
		return fDiceIndex;
	}

	public BlockResult getBlockResult() {
		return fBlockResult;
	}

	public String getDefenderId() {
		return fDefenderId;
	}

	// transformation

	public IReport transform(Game game) {
		return new ReportBlockChoice(getNrOfDice(), getBlockRoll(), getDiceIndex(), getBlockResult(), getDefenderId());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.NR_OF_DICE.addTo(jsonObject, fNrOfDice);
		IJsonOption.BLOCK_ROLL.addTo(jsonObject, fBlockRoll);
		IJsonOption.DICE_INDEX.addTo(jsonObject, fDiceIndex);
		IJsonOption.BLOCK_RESULT.addTo(jsonObject, fBlockResult);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
		return jsonObject;
	}

	public ReportBlockChoice initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fNrOfDice = IJsonOption.NR_OF_DICE.getFrom(game, jsonObject);
		fBlockRoll = IJsonOption.BLOCK_ROLL.getFrom(game, jsonObject);
		fDiceIndex = IJsonOption.DICE_INDEX.getFrom(game, jsonObject);
		fBlockResult = (BlockResult) IJsonOption.BLOCK_RESULT.getFrom(game, jsonObject);
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(game, jsonObject);
		return this;
	}

}

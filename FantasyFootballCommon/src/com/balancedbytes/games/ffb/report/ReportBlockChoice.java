package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.BlockResult;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportBlockChoice implements IReport {

	private int fNrOfDice;
	private int[] fBlockRoll;
	private int fDiceIndex;
	private BlockResult fBlockResult;
	private String fDefenderId;
	// use negative boolean variable to remain backwards compatible with old replays
	private boolean suppressExtraEffectHandling;
	private boolean showNameInReport;

	public ReportBlockChoice() {
		super();
	}

	public ReportBlockChoice(int pNrOfDice, int[] pBlockRoll, int pDiceIndex, BlockResult pBlockResult,
			String pDefenderId, boolean suppressExtraEffectHandling, boolean showNameInReport) {
		fNrOfDice = pNrOfDice;
		fBlockRoll = pBlockRoll;
		fDiceIndex = pDiceIndex;
		fBlockResult = pBlockResult;
		fDefenderId = pDefenderId;
		this.suppressExtraEffectHandling = suppressExtraEffectHandling;
		this.showNameInReport = showNameInReport;
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

	public boolean isSuppressExtraEffectHandling() {
		return suppressExtraEffectHandling;
	}

	public boolean isShowNameInReport() {
		return showNameInReport;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportBlockChoice(getNrOfDice(), getBlockRoll(), getDiceIndex(), getBlockResult(), getDefenderId(), suppressExtraEffectHandling, showNameInReport);
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
		IJsonOption.SUPPRESS_EXTRA_EFFECT_HANDLING.addTo(jsonObject, suppressExtraEffectHandling);
		IJsonOption.SHOW_NAME_IN_REPORT.addTo(jsonObject, showNameInReport);
		return jsonObject;
	}

	public ReportBlockChoice initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fNrOfDice = IJsonOption.NR_OF_DICE.getFrom(game, jsonObject);
		fBlockRoll = IJsonOption.BLOCK_ROLL.getFrom(game, jsonObject);
		fDiceIndex = IJsonOption.DICE_INDEX.getFrom(game, jsonObject);
		fBlockResult = (BlockResult) IJsonOption.BLOCK_RESULT.getFrom(game, jsonObject);
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(game, jsonObject);
		suppressExtraEffectHandling = IJsonOption.SUPPRESS_EXTRA_EFFECT_HANDLING.getFrom(game, jsonObject);
		showNameInReport = IJsonOption.SHOW_NAME_IN_REPORT.getFrom(game, jsonObject);
		return this;
	}

}

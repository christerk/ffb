package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
public class DialogBlockRollPartialReRollParameter implements IDialogParameter {

	private String fChoosingTeamId;
	private int fNrOfDice, brawlerOptions;
	private int[] fBlockRoll, reRolledDiceIndexes;
	private boolean fTeamReRollOption;
	private boolean fProReRollOption;

	public DialogBlockRollPartialReRollParameter() {
		super();
	}

	public DialogBlockRollPartialReRollParameter(String pChoosingTeamId, int pNrOfDice, int[] pBlockRoll, boolean pTeamReRollOption,
	                                             boolean pProReRollOption, int brawlerOptions, int[] reRolledDiceIndexes) {
		fChoosingTeamId = pChoosingTeamId;
		fNrOfDice = pNrOfDice;
		fBlockRoll = pBlockRoll;
		fTeamReRollOption = pTeamReRollOption;
		fProReRollOption = pProReRollOption;
		this.brawlerOptions = brawlerOptions;
		this.reRolledDiceIndexes = reRolledDiceIndexes;
	}

	public DialogId getId() {
		return DialogId.BLOCK_ROLL_PARTIAL_RE_ROLL;
	}

	public String getChoosingTeamId() {
		return fChoosingTeamId;
	}

	public int getNrOfDice() {
		return fNrOfDice;
	}

	public int[] getBlockRoll() {
		return fBlockRoll;
	}

	public boolean hasTeamReRollOption() {
		return fTeamReRollOption;
	}

	public boolean hasProReRollOption() {
		return fProReRollOption;
	}

	public int getBrawlerOptions() {
		return brawlerOptions;
	}

	public int[] getReRolledDiceIndexes() {
		return reRolledDiceIndexes;
	}
// transformation

	public IDialogParameter transform() {
		return new DialogBlockRollPartialReRollParameter(getChoosingTeamId(), getNrOfDice(), getBlockRoll(), hasTeamReRollOption(),
				hasProReRollOption(), brawlerOptions, reRolledDiceIndexes);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.CHOOSING_TEAM_ID.addTo(jsonObject, fChoosingTeamId);
		IJsonOption.NR_OF_DICE.addTo(jsonObject, fNrOfDice);
		IJsonOption.BLOCK_ROLL.addTo(jsonObject, fBlockRoll);
		IJsonOption.RE_ROLLED_DICE_INDEXES.addTo(jsonObject, reRolledDiceIndexes);
		IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, fTeamReRollOption);
		IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, fProReRollOption);
		IJsonOption.BRAWLER_OPTIONS.addTo(jsonObject, brawlerOptions);
		return jsonObject;
	}

	public DialogBlockRollPartialReRollParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fChoosingTeamId = IJsonOption.CHOOSING_TEAM_ID.getFrom(game, jsonObject);
		fNrOfDice = IJsonOption.NR_OF_DICE.getFrom(game, jsonObject);
		fBlockRoll = IJsonOption.BLOCK_ROLL.getFrom(game, jsonObject);
		reRolledDiceIndexes = IJsonOption.RE_ROLLED_DICE_INDEXES.getFrom(game, jsonObject);
		fTeamReRollOption = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(game, jsonObject);
		fProReRollOption = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(game, jsonObject);
		brawlerOptions = IJsonOption.BRAWLER_OPTIONS.getFrom(game, jsonObject);
		return this;
	}

}
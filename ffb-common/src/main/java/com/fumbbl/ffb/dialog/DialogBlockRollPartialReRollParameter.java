package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogBlockRollPartialReRollParameter implements IDialogParameter {

	private String fChoosingTeamId;
	private int fNrOfDice;
	private int[] fBlockRoll, reRolledDiceIndexes;
	private boolean fTeamReRollOption, fProReRollOption, brawlerOption, consummateOption;
	private ReRollSource singleUseReRollSource;

	public DialogBlockRollPartialReRollParameter() {
		super();
	}

	public DialogBlockRollPartialReRollParameter(String pChoosingTeamId, int pNrOfDice, int[] pBlockRoll, boolean pTeamReRollOption,
																							 boolean pProReRollOption, boolean brawlerOption, boolean consummateOption,
																							 int[] reRolledDiceIndexes, ReRollSource singleUseReRollSource) {
		fChoosingTeamId = pChoosingTeamId;
		fNrOfDice = pNrOfDice;
		fBlockRoll = pBlockRoll;
		fTeamReRollOption = pTeamReRollOption;
		fProReRollOption = pProReRollOption;
		this.brawlerOption = brawlerOption;
		this.consummateOption = consummateOption;
		this.reRolledDiceIndexes = reRolledDiceIndexes;
		this.singleUseReRollSource = singleUseReRollSource;
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

	public boolean hasBrawlerOption() {
		return brawlerOption;
	}

	public int[] getReRolledDiceIndexes() {
		return reRolledDiceIndexes;
	}

	public ReRollSource getSingleUseReRollSource() {
		return singleUseReRollSource;
	}

	public boolean hasConsummateOption() {
		return consummateOption;
	}

// transformation

	public IDialogParameter transform() {
		return new DialogBlockRollPartialReRollParameter(getChoosingTeamId(), getNrOfDice(), getBlockRoll(), hasTeamReRollOption(),
			hasProReRollOption(), brawlerOption, consummateOption, reRolledDiceIndexes, singleUseReRollSource);
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
		IJsonOption.BRAWLER_OPTION.addTo(jsonObject, brawlerOption);
		IJsonOption.RE_ROLL_SOURCE_SINGLE_USE.addTo(jsonObject, singleUseReRollSource);
		IJsonOption.CONSUMMATE_OPTION.addTo(jsonObject, consummateOption);
		return jsonObject;
	}

	public DialogBlockRollPartialReRollParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fChoosingTeamId = IJsonOption.CHOOSING_TEAM_ID.getFrom(source, jsonObject);
		fNrOfDice = IJsonOption.NR_OF_DICE.getFrom(source, jsonObject);
		fBlockRoll = IJsonOption.BLOCK_ROLL.getFrom(source, jsonObject);
		reRolledDiceIndexes = IJsonOption.RE_ROLLED_DICE_INDEXES.getFrom(source, jsonObject);
		fTeamReRollOption = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(source, jsonObject);
		fProReRollOption = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(source, jsonObject);
		brawlerOption = IJsonOption.BRAWLER_OPTION.getFrom(source, jsonObject);
		if (IJsonOption.CONSUMMATE_OPTION.isDefinedIn(jsonObject)) {
			consummateOption = IJsonOption.CONSUMMATE_OPTION.getFrom(source, jsonObject);
		}
		singleUseReRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE_SINGLE_USE.getFrom(source, jsonObject);
		return this;
	}

}

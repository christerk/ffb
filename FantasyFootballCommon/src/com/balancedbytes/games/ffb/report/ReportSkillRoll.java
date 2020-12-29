package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.factory.CatchModifierFactory;
import com.balancedbytes.games.ffb.factory.DodgeModifierFactory;
import com.balancedbytes.games.ffb.factory.GazeModifierFactory;
import com.balancedbytes.games.ffb.factory.GoForItModifierFactory;
import com.balancedbytes.games.ffb.factory.IRollModifierFactory;
import com.balancedbytes.games.ffb.factory.InterceptionModifierFactory;
import com.balancedbytes.games.ffb.factory.LeapModifierFactory;
import com.balancedbytes.games.ffb.factory.PassModifierFactory;
import com.balancedbytes.games.ffb.factory.PickupModifierFactory;
import com.balancedbytes.games.ffb.factory.RightStuffModifierFactory;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportSkillRoll implements IReport {

	private ReportId fId;
	private String fPlayerId;
	private boolean fSuccessful;
	private int fRoll;
	private int fMinimumRoll;
	private boolean fReRolled;
	private List<IRollModifier> fRollModifierList;

	public ReportSkillRoll(ReportId pId) {
		fId = pId;
		initRollModifiers(null);
	}

	public ReportSkillRoll(ReportId pId, String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
			boolean pReRolled) {
		this(pId, pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, null);
	}

	public ReportSkillRoll(ReportId pId, String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
			boolean pReRolled, IRollModifier[] pRollModifiers) {
		fId = pId;
		fPlayerId = pPlayerId;
		fSuccessful = pSuccessful;
		fRoll = pRoll;
		fMinimumRoll = pMinimumRoll;
		fReRolled = pReRolled;
		initRollModifiers(pRollModifiers);
	}

	private void initRollModifiers(IRollModifier[] pRollModifiers) {
		fRollModifierList = new ArrayList<IRollModifier>();
		if (ArrayTool.isProvided(pRollModifiers)) {
			for (IRollModifier rollModifier : pRollModifiers) {
				addRollModifier(rollModifier);
			}
		}
	}

	public void addRollModifier(IRollModifier pRollModifier) {
		if (pRollModifier != null) {
			fRollModifierList.add(pRollModifier);
		}
	}

	public IRollModifier[] getRollModifiers() {
		return fRollModifierList.toArray(new IRollModifier[fRollModifierList.size()]);
	}

	public boolean hasRollModifier(IRollModifier pRollModifier) {
		return fRollModifierList.contains(pRollModifier);
	}

	protected List<IRollModifier> getRollModifierList() {
		return fRollModifierList;
	}

	public ReportId getId() {
		return fId;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public boolean isSuccessful() {
		return fSuccessful;
	}

	public int getRoll() {
		return fRoll;
	}

	public int getMinimumRoll() {
		return fMinimumRoll;
	}

	public boolean isReRolled() {
		return fReRolled;
	}

	// transformation

	public IReport transform() {
		return new ReportSkillRoll(getId(), getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
				getRollModifiers());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, fId);
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
		IJsonOption.ROLL.addTo(jsonObject, fRoll);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
		IJsonOption.RE_ROLLED.addTo(jsonObject, fReRolled);
		if (fRollModifierList.size() > 0) {
			JsonArray modifierArray = new JsonArray();
			for (IRollModifier modifier : fRollModifierList) {
				modifierArray.add(UtilJson.toJsonValue(modifier));
			}
			IJsonOption.ROLL_MODIFIERS.addTo(jsonObject, modifierArray);
		}
		return jsonObject;
	}

	public ReportSkillRoll initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fSuccessful = IJsonOption.SUCCESSFUL.getFrom(game, jsonObject);
		fRoll = IJsonOption.ROLL.getFrom(game, jsonObject);
		fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(game, jsonObject);
		fReRolled = IJsonOption.RE_ROLLED.getFrom(game, jsonObject);
		JsonArray modifierArray = IJsonOption.ROLL_MODIFIERS.getFrom(game, jsonObject);
		if (modifierArray != null) {
			IRollModifierFactory modifierFactory = createRollModifierFactory();
			if (modifierFactory != null) {
				for (int i = 0; i < modifierArray.size(); i++) {
					fRollModifierList.add((IRollModifier) UtilJson.toEnumWithName(modifierFactory, modifierArray.get(i)));
				}
			}
		}
		return this;
	}

	private IRollModifierFactory createRollModifierFactory() {
		switch (getId()) {
		case CATCH_ROLL:
			return new CatchModifierFactory();
		case DODGE_ROLL:
			return new DodgeModifierFactory();
		case GO_FOR_IT_ROLL:
			return new GoForItModifierFactory();
		case INTERCEPTION_ROLL:
			return new InterceptionModifierFactory();
		case LEAP_ROLL:
			return new LeapModifierFactory();
		case PASS_ROLL:
		case THROW_TEAM_MATE_ROLL:
			return new PassModifierFactory();
		case PICK_UP_ROLL:
			return new PickupModifierFactory();
		case RIGHT_STUFF_ROLL:
			return new RightStuffModifierFactory();
		case HYPNOTIC_GAZE_ROLL:
			return new GazeModifierFactory();
		default:
			return null;
		}
	}

}

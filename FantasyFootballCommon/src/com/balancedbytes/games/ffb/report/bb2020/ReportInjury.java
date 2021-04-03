package com.balancedbytes.games.ffb.report.bb2020;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.InjuryModifierFactory;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.modifiers.ArmorModifier;
import com.balancedbytes.games.ffb.modifiers.ArmorModifierFactory;
import com.balancedbytes.games.ffb.modifiers.InjuryModifier;
import com.balancedbytes.games.ffb.modifiers.bb2020.CasualtyModifier;
import com.balancedbytes.games.ffb.modifiers.bb2020.CasualtyModifierFactory;
import com.balancedbytes.games.ffb.report.IReport;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.UtilReport;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportInjury implements com.balancedbytes.games.ffb.report.ReportInjury {

	private String fAttackerId;
	private String fDefenderId;
	private InjuryType fInjuryType;
	private boolean fArmorBroken;
	private final List<ArmorModifier> fArmorModifiers;
	private int[] fArmorRoll;
	private final List<InjuryModifier> fInjuryModifiers;
	private int[] fInjuryRoll;
	private int[] fCasualtyRoll;
	private SeriousInjury fSeriousInjury;
	private int[] fCasualtyRollDecay;
	private SeriousInjury fSeriousInjuryDecay;
	private PlayerState fInjury;
	private PlayerState fInjuryDecay;
	private final Set<CasualtyModifier> casualtyModifiers;

	public ReportInjury() {
		fArmorModifiers = new ArrayList<>();
		fInjuryModifiers = new ArrayList<>();
		casualtyModifiers = new HashSet<>();
	}

	private ReportInjury(String pDefenderId, InjuryType pInjuryType, boolean pArmorBroken, ArmorModifier[] pArmorModifiers,
	                     int[] pArmorRoll, InjuryModifier[] pInjuryModifiers, int[] pInjuryRoll, int[] pCasualtyRoll,
	                     SeriousInjury pSeriousInjury, int[] pCasualtyRollDecay, SeriousInjury pSeriousInjuryDecay, PlayerState pInjury,
	                     PlayerState pInjuryDecay, String pAttackerId, Set<CasualtyModifier> casualtyModifiers) {
		this();
		init(pDefenderId, pInjuryType, pArmorBroken, pArmorModifiers, pArmorRoll, pInjuryModifiers, pInjuryRoll, pCasualtyRoll,
			pSeriousInjury, pCasualtyRollDecay, pSeriousInjuryDecay, pInjury, pInjuryDecay, pAttackerId, casualtyModifiers);
	}

	@Override
	public ReportInjury init(InjuryContext injuryContext) {
		init(injuryContext.getDefenderId(), injuryContext.getInjuryType(),
			injuryContext.isArmorBroken(), injuryContext.getArmorModifiers(), injuryContext.getArmorRoll(),
			injuryContext.getInjuryModifiers(), injuryContext.getInjuryRoll(), injuryContext.getCasualtyRoll(),
			injuryContext.getSeriousInjury(), injuryContext.getCasualtyRollDecay(), injuryContext.getSeriousInjuryDecay(),
			injuryContext.getInjury(), injuryContext.getInjuryDecay(), injuryContext.getAttackerId(), injuryContext.casualtyModifiers);
		return this;
	}

	private void init(String pDefenderId, InjuryType pInjuryType, boolean pArmorBroken, ArmorModifier[] pArmorModifiers,
	                  int[] pArmorRoll, InjuryModifier[] pInjuryModifiers, int[] pInjuryRoll, int[] pCasualtyRoll,
	                  SeriousInjury pSeriousInjury, int[] pCasualtyRollDecay, SeriousInjury pSeriousInjuryDecay, PlayerState pInjury,
	                  PlayerState pInjuryDecay, String pAttackerId, Set<CasualtyModifier> casualtyModifiers) {
		fDefenderId = pDefenderId;
		fInjuryType = pInjuryType;
		fArmorBroken = pArmorBroken;
		add(pArmorModifiers);
		fArmorRoll = pArmorRoll;
		add(pInjuryModifiers);
		fInjuryRoll = pInjuryRoll;
		fCasualtyRoll = pCasualtyRoll;
		fSeriousInjury = pSeriousInjury;
		fCasualtyRollDecay = pCasualtyRollDecay;
		fSeriousInjuryDecay = pSeriousInjuryDecay;
		fInjury = pInjury;
		fInjuryDecay = pInjuryDecay;
		fAttackerId = pAttackerId;
		add(casualtyModifiers);
	}

	public ReportId getId() {
		return ReportId.INJURY;
	}

	public String getDefenderId() {
		return fDefenderId;
	}

	public InjuryType getInjuryType() {
		return fInjuryType;
	}

	public boolean isArmorBroken() {
		return fArmorBroken;
	}

	public ArmorModifier[] getArmorModifiers() {
		return fArmorModifiers.toArray(new ArmorModifier[0]);
	}

	private void add(ArmorModifier pArmorModifier) {
		if (pArmorModifier != null) {
			fArmorModifiers.add(pArmorModifier);
		}
	}

	private void add(ArmorModifier[] pArmorModifiers) {
		if (ArrayTool.isProvided(pArmorModifiers)) {
			for (ArmorModifier armorModifier : pArmorModifiers) {
				add(armorModifier);
			}
		}
	}

	private void add(Set<CasualtyModifier> casualtyModifiers) {
		if (casualtyModifiers != null) {
			this.casualtyModifiers.addAll(casualtyModifiers);
		}
	}

	public int[] getArmorRoll() {
		return fArmorRoll;
	}

	public InjuryModifier[] getInjuryModifiers() {
		return fInjuryModifiers.toArray(new InjuryModifier[0]);
	}

	private void add(InjuryModifier pInjuryModifier) {
		if (pInjuryModifier != null) {
			fInjuryModifiers.add(pInjuryModifier);
		}
	}

	private void add(InjuryModifier[] pInjuryModifiers) {
		if (ArrayTool.isProvided(pInjuryModifiers)) {
			for (InjuryModifier injuryModifier : pInjuryModifiers) {
				add(injuryModifier);
			}
		}
	}

	public int[] getInjuryRoll() {
		return fInjuryRoll;
	}

	public int[] getCasualtyRoll() {
		return fCasualtyRoll;
	}

	public PlayerState getInjury() {
		return fInjury;
	}

	public PlayerState getInjuryDecay() {
		return fInjuryDecay;
	}

	public SeriousInjury getSeriousInjury() {
		return fSeriousInjury;
	}

	public int[] getCasualtyRollDecay() {
		return fCasualtyRollDecay;
	}

	public SeriousInjury getSeriousInjuryDecay() {
		return fSeriousInjuryDecay;
	}

	public String getAttackerId() {
		return fAttackerId;
	}

	public Set<CasualtyModifier> getCasualtyModifiers() {
		return casualtyModifiers;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportInjury(getDefenderId(), getInjuryType(), isArmorBroken(), getArmorModifiers(), getArmorRoll(),
				getInjuryModifiers(), getInjuryRoll(), getCasualtyRoll(), getSeriousInjury(), getCasualtyRollDecay(),
				getSeriousInjuryDecay(), getInjury(), getInjuryDecay(), getAttackerId(), casualtyModifiers);
	}

	// JSON serialization

	public JsonObject toJsonValue() {

		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());

		IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
		IJsonOption.INJURY_TYPE.addTo(jsonObject, fInjuryType);
		IJsonOption.ARMOR_BROKEN.addTo(jsonObject, fArmorBroken);
		IJsonOption.ARMOR_ROLL.addTo(jsonObject, fArmorRoll);
		IJsonOption.INJURY_ROLL.addTo(jsonObject, fInjuryRoll);
		IJsonOption.CASUALTY_ROLL.addTo(jsonObject, fCasualtyRoll);
		IJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
		IJsonOption.CASUALTY_ROLL_DECAY.addTo(jsonObject, fCasualtyRollDecay);
		IJsonOption.SERIOUS_INJURY_DECAY.addTo(jsonObject, fSeriousInjuryDecay);
		IJsonOption.INJURY.addTo(jsonObject, fInjury);
		IJsonOption.INJURY_DECAY.addTo(jsonObject, fInjuryDecay);
		IJsonOption.ATTACKER_ID.addTo(jsonObject, fAttackerId);

		JsonArray armorModifiers = new JsonArray();
		for (ArmorModifier armorModifier : getArmorModifiers()) {
			armorModifiers.add(UtilJson.toJsonValue(armorModifier));
		}
		IJsonOption.ARMOR_MODIFIERS.addTo(jsonObject, armorModifiers);

		JsonArray injuryModifiers = new JsonArray();
		for (InjuryModifier injuryModifier : getInjuryModifiers()) {
			injuryModifiers.add(UtilJson.toJsonValue(injuryModifier));
		}
		IJsonOption.INJURY_MODIFIERS.addTo(jsonObject, injuryModifiers);

		JsonArray casualtyModifiers = new JsonArray();
		this.casualtyModifiers.forEach(modifier -> casualtyModifiers.add(UtilJson.toJsonValue(modifier)));
		IJsonOption.CASUALTY_MODIFIERS.addTo(jsonObject, casualtyModifiers);
		return jsonObject;

	}

	public ReportInjury initFrom(IFactorySource game, JsonValue pJsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));

		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(game, jsonObject);
		fInjuryType = (InjuryType) IJsonOption.INJURY_TYPE.getFrom(game, jsonObject);
		fArmorBroken = IJsonOption.ARMOR_BROKEN.getFrom(game, jsonObject);
		fArmorRoll = IJsonOption.ARMOR_ROLL.getFrom(game, jsonObject);
		fInjuryRoll = IJsonOption.INJURY_ROLL.getFrom(game, jsonObject);
		fCasualtyRoll = IJsonOption.CASUALTY_ROLL.getFrom(game, jsonObject);
		fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(game, jsonObject);
		fCasualtyRollDecay = IJsonOption.CASUALTY_ROLL_DECAY.getFrom(game, jsonObject);
		fSeriousInjuryDecay = (SeriousInjury) IJsonOption.SERIOUS_INJURY_DECAY.getFrom(game, jsonObject);
		fInjury = IJsonOption.INJURY.getFrom(game, jsonObject);
		fInjuryDecay = IJsonOption.INJURY_DECAY.getFrom(game, jsonObject);
		fAttackerId = IJsonOption.ATTACKER_ID.getFrom(game, jsonObject);

		fArmorModifiers.clear();
		ArmorModifierFactory armorModifierFactory = game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);
		JsonArray armorModifiers = IJsonOption.ARMOR_MODIFIERS.getFrom(game, jsonObject);
		for (int i = 0; i < armorModifiers.size(); i++) {
			fArmorModifiers.add((ArmorModifier) UtilJson.toEnumWithName(armorModifierFactory, armorModifiers.get(i)));
		}

		fInjuryModifiers.clear();
		InjuryModifierFactory injuryModifierFactory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		JsonArray injuryModifiers = IJsonOption.INJURY_MODIFIERS.getFrom(game, jsonObject);
		for (int i = 0; i < injuryModifiers.size(); i++) {
			fInjuryModifiers.add((InjuryModifier) UtilJson.toEnumWithName(injuryModifierFactory, injuryModifiers.get(i)));
		}

		casualtyModifiers.clear();
		CasualtyModifierFactory casualtyModifierFactory = game.getFactory(FactoryType.Factory.CASUALTY_MODIFIER);
		JsonArray casualtyModifiers = IJsonOption.CASUALTY_MODIFIERS.getFrom(game, jsonObject);
		casualtyModifiers.values().forEach( value ->
			this.casualtyModifiers.add((CasualtyModifier) UtilJson.toEnumWithName(casualtyModifierFactory, value))
		);
		return this;

	}

}

package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.factory.ArmorModifierFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.InjuryModification;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifier;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifierFactory;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;
import com.fumbbl.ffb.report.logcontrol.SkipInjuryParts;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportInjury implements com.fumbbl.ffb.report.ReportInjury {

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
	private SeriousInjury originalInjury;
	private PlayerState fInjury;
	private PlayerState fInjuryDecay;
	private final Set<CasualtyModifier> casualtyModifiers;
	private SkipInjuryParts skip = SkipInjuryParts.NONE;

	public ReportInjury() {
		fArmorModifiers = new ArrayList<>();
		fInjuryModifiers = new ArrayList<>();
		casualtyModifiers = new HashSet<>();
	}

	private ReportInjury(String pDefenderId, InjuryType pInjuryType, boolean pArmorBroken, ArmorModifier[] pArmorModifiers,
	                     int[] pArmorRoll, InjuryModifier[] pInjuryModifiers, int[] pInjuryRoll, int[] pCasualtyRoll,
	                     SeriousInjury pSeriousInjury, int[] pCasualtyRollDecay, SeriousInjury pSeriousInjuryDecay, PlayerState pInjury,
	                     PlayerState pInjuryDecay, String pAttackerId, Set<CasualtyModifier> casualtyModifiers,
	                     SeriousInjury originalInjury, SkipInjuryParts skip) {
		this();
		init(pDefenderId, pInjuryType, pArmorBroken, pArmorModifiers, pArmorRoll, pInjuryModifiers, pInjuryRoll, pCasualtyRoll,
			pSeriousInjury, pCasualtyRollDecay, pSeriousInjuryDecay, pInjury, pInjuryDecay, pAttackerId, casualtyModifiers, originalInjury, skip);
	}

	@Override
	public ReportInjury init(InjuryContext injuryContext, SkipInjuryParts skip) {

		int[] injuryRoll = injuryContext.getInjuryRoll();
		int[] casualtyRoll = injuryContext.getCasualtyRoll();
		PlayerState injury = injuryContext.getInjury();

		ModifiedInjuryContext modifiedInjuryContext = injuryContext.getModifiedInjuryContext();
		if (modifiedInjuryContext != null) {
			InjuryModification modification = modifiedInjuryContext.getModification();
			switch (modification) {
				case ARMOUR:
					injuryRoll = null;
					casualtyRoll = null;
					injury = null;
					break;
				case INJURY:
					casualtyRoll = null;
					injury = null;
					break;
				default:
					break;
			}
		}


		init(injuryContext.getDefenderId(), injuryContext.getInjuryType(),
			injuryContext.isArmorBroken(), injuryContext.getArmorModifiers(), injuryContext.getArmorRoll(),
			injuryContext.getInjuryModifiers(), injuryRoll, casualtyRoll,
			injuryContext.getSeriousInjury(), injuryContext.getCasualtyRollDecay(), injuryContext.getSeriousInjuryDecay(),
			injury, injuryContext.getInjuryDecay(), injuryContext.getAttackerId(),
			injuryContext.casualtyModifiers, injuryContext.originalSeriousInjury, skip);
		return this;
	}

	private void init(String pDefenderId, InjuryType pInjuryType, boolean pArmorBroken, ArmorModifier[] pArmorModifiers,
	                  int[] pArmorRoll, InjuryModifier[] pInjuryModifiers, int[] pInjuryRoll, int[] pCasualtyRoll,
	                  SeriousInjury pSeriousInjury, int[] pCasualtyRollDecay, SeriousInjury pSeriousInjuryDecay, PlayerState pInjury,
	                  PlayerState pInjuryDecay, String pAttackerId, Set<CasualtyModifier> casualtyModifiers, SeriousInjury originalInjury,
	                  SkipInjuryParts skip) {
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
		this.originalInjury = originalInjury;
		add(casualtyModifiers);
		this.skip = skip;
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

	public SeriousInjury getOriginalInjury() {
		return originalInjury;
	}

	public SkipInjuryParts getSkip() {
		return skip;
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
			getSeriousInjuryDecay(), getInjury(), getInjuryDecay(), getAttackerId(), casualtyModifiers, originalInjury, skip);
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
		IJsonOption.SERIOUS_INJURY_OLD.addTo(jsonObject, originalInjury);
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
		IJsonOption.SKIP_INJURY_PARTS.addTo(jsonObject, skip.name());
		return jsonObject;

	}

	public ReportInjury initFrom(IFactorySource source, JsonValue jsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));

		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		fInjuryType = (InjuryType) IJsonOption.INJURY_TYPE.getFrom(source, jsonObject);
		fArmorBroken = IJsonOption.ARMOR_BROKEN.getFrom(source, jsonObject);
		fArmorRoll = IJsonOption.ARMOR_ROLL.getFrom(source, jsonObject);
		fInjuryRoll = IJsonOption.INJURY_ROLL.getFrom(source, jsonObject);
		fCasualtyRoll = IJsonOption.CASUALTY_ROLL.getFrom(source, jsonObject);
		fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(source, jsonObject);
		fCasualtyRollDecay = IJsonOption.CASUALTY_ROLL_DECAY.getFrom(source, jsonObject);
		fSeriousInjuryDecay = (SeriousInjury) IJsonOption.SERIOUS_INJURY_DECAY.getFrom(source, jsonObject);
		originalInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY_OLD.getFrom(source, jsonObject);
		fInjury = IJsonOption.INJURY.getFrom(source, jsonObject);
		fInjuryDecay = IJsonOption.INJURY_DECAY.getFrom(source, jsonObject);
		fAttackerId = IJsonOption.ATTACKER_ID.getFrom(source, jsonObject);

		fArmorModifiers.clear();
		ArmorModifierFactory armorModifierFactory = source.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);
		JsonArray armorModifiers = IJsonOption.ARMOR_MODIFIERS.getFrom(source, jsonObject);
		for (int i = 0; i < armorModifiers.size(); i++) {
			fArmorModifiers.add((ArmorModifier) UtilJson.toEnumWithName(armorModifierFactory, armorModifiers.get(i)));
		}

		fInjuryModifiers.clear();
		InjuryModifierFactory injuryModifierFactory = source.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		JsonArray injuryModifiers = IJsonOption.INJURY_MODIFIERS.getFrom(source, jsonObject);
		for (int i = 0; i < injuryModifiers.size(); i++) {
			fInjuryModifiers.add((InjuryModifier) UtilJson.toEnumWithName(injuryModifierFactory, injuryModifiers.get(i)));
		}

		casualtyModifiers.clear();
		CasualtyModifierFactory casualtyModifierFactory = source.getFactory(FactoryType.Factory.CASUALTY_MODIFIER);
		JsonArray casualtyModifiers = IJsonOption.CASUALTY_MODIFIERS.getFrom(source, jsonObject);
		casualtyModifiers.values().forEach(value ->
			this.casualtyModifiers.add((CasualtyModifier) UtilJson.toEnumWithName(casualtyModifierFactory, value))
		);

		if (IJsonOption.SKIP_INJURY_PARTS.isDefinedIn(jsonObject)) {
			skip = SkipInjuryParts.valueOf(IJsonOption.SKIP_INJURY_PARTS.getFrom(source, jsonObject));
		}
		return this;

	}

}

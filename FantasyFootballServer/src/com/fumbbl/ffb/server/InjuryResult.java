package com.fumbbl.ffb.server;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.BloodSpot;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.factory.ReportFactory;
import com.fumbbl.ffb.factory.SeriousInjuryFactory;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.ArmorModifierFactory;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifier;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifierFactory;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportInjury;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.UtilBox;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class InjuryResult implements IJsonSerializable {

	private static List<Integer> basePrecedenceList = new ArrayList<Integer>() {{
		add(PlayerState.PRONE);
		add(PlayerState.STUNNED);
		add(PlayerState.KNOCKED_OUT);
		add(PlayerState.BADLY_HURT);
		add(PlayerState.SERIOUS_INJURY);
		add(PlayerState.RIP);
	}};

	private InjuryContext injuryContext;

	public InjuryResult() {
		injuryContext = new InjuryContext();
	}

	public InjuryContext injuryContext() {
		return injuryContext;
	}

	public void setInjuryContext(InjuryContext context) {
		injuryContext = context;
	}

	public void applyTo(IStep pStep) {
		applyTo(pStep, true);
	}

	public void applyTo(IStep pStep, boolean updateStats) {
		Game game = pStep.getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		Player<?> defender = game.getPlayerById(injuryContext.getDefenderId());

		PlayerResult playerResult = gameResult.getPlayerResult(defender);
		if (defender.hasSkillProperty(NamedProperties.getsSentOffAtEndOfDrive)) {
			playerResult.setHasUsedSecretWeapon(true);
		}

		boolean isCausedByOpponent = injuryContext.getInjuryType().isCausedByOpponent();

		PlayerState oldPlayerState = game.getFieldModel().getPlayerState(defender);
		if (injuryContext.getPlayerState() != null) {
			// Make sure the player isn't converted e.g. from a stun to prone (for example when
			// fouling a stunned player) or in case of two cas vs a multiblock player a BH does override a RIP
			if (!basePrecedenceList.contains(oldPlayerState.getBase()) ||
				basePrecedenceList.indexOf(injuryContext.getPlayerState().getBase()) > basePrecedenceList.indexOf(oldPlayerState.getBase())) {
				PlayerState playerState = game.getFieldModel().getPlayerState(defender);
				game.getFieldModel().setPlayerState(defender, playerState.changeBase(injuryContext.getPlayerState().getBase()));
				if ((injuryContext.getPlayerState().getBase() == PlayerState.STUNNED)
					&& (((defender.getTeam() == game.getTeamHome()) && game.isHomePlaying())
					|| ((defender.getTeam() == game.getTeamAway()) && !game.isHomePlaying()))) {
					game.getFieldModel().setPlayerState(defender,
						game.getFieldModel().getPlayerState(defender).changeActive(false));
				}
				if (injuryContext.isCasualty() || injuryContext.isKnockedOut() || injuryContext.isReserve()) {
					UtilBox.putPlayerIntoBox(game, defender);
					UtilServerGame.updateLeaderReRolls(pStep);
				}
			}
		}
		// death is also a serious injury
		if ((injuryContext.getPlayerState() != null) && (injuryContext.getPlayerState().getBase() == PlayerState.RIP)) {
			playerResult.setSeriousInjury(((SeriousInjuryFactory) game.getFactory(FactoryType.Factory.SERIOUS_INJURY)).dead());
			playerResult.setSeriousInjuryDecay(null);
		} else if (playerResult.getSeriousInjury() != null) {
			// hacky workaround for 2 cas against an attacker during multiblock
			playerResult.setSeriousInjuryDecay(injuryContext.getSeriousInjury());
		} else {
			playerResult.setSeriousInjury(injuryContext.getSeriousInjury());
			playerResult.setSeriousInjuryDecay(injuryContext.getSeriousInjuryDecay());
		}
		if (injuryContext.getSendToBoxReason() != null) {
			playerResult.setSendToBoxReason(injuryContext.getSendToBoxReason());
			playerResult.setSendToBoxTurn(injuryContext.getSendToBoxTurn());
			playerResult.setSendToBoxHalf(injuryContext.getSendToBoxHalf());
			playerResult.setSendToBoxByPlayerId(injuryContext.getAttackerId());
		}
		if (injuryContext.getSufferedInjury() != null && updateStats) {
			if (isCausedByOpponent) {
				if ((injuryContext.fApothecaryStatus == ApothecaryStatus.RESULT_CHOICE)
						&& (injuryContext.getPlayerState().getBase() == PlayerState.RESERVE)) {
					if (game.getTeamHome().hasPlayer(defender)) {
						gameResult.getTeamResultHome().sufferInjury(new PlayerState(PlayerState.BADLY_HURT));
					} else {
						gameResult.getTeamResultAway().sufferInjury(new PlayerState(PlayerState.BADLY_HURT));
					}
				} else {
					if (game.getTeamHome().hasPlayer(defender)) {
						gameResult.getTeamResultHome().sufferInjury(injuryContext.getPlayerState());
					} else {
						gameResult.getTeamResultAway().sufferInjury(injuryContext.getPlayerState());
					}
				}
				Player<?> attacker = game.getPlayerById(injuryContext.getAttackerId());
				if (injuryContext.getSufferedInjury().isCasualty() && injuryContext.getInjuryType().isWorthSpps()
						&& (attacker.getTeam() != defender.getTeam())) {
					PlayerResult attackerResult = gameResult.getPlayerResult(attacker);
					attackerResult.setCasualties(attackerResult.getCasualties() + 1);
				}
			}
			game.getFieldModel().add(new BloodSpot(injuryContext.getDefenderPosition(), injuryContext.getSufferedInjury()));
		}
	}

	public void report(IStep pStep) {
		ReportFactory factory = pStep.getGameState().getGame().getFactory(FactoryType.Factory.REPORT);
		ReportInjury reportInjury = (ReportInjury) factory.forId(ReportId.INJURY);
		pStep.getResult().addReport(reportInjury.init(injuryContext));
		pStep.getResult().setSound(injuryContext.getSound());
	}

	// JSON serialization

	public JsonObject toJsonValue() {

		JsonObject jsonObject = new JsonObject();

		IServerJsonOption.INJURY_TYPE.addTo(jsonObject, injuryContext.fInjuryType);
		IServerJsonOption.DEFENDER_ID.addTo(jsonObject, injuryContext.fDefenderId);
		IServerJsonOption.DEFENDER_POSITION.addTo(jsonObject, injuryContext.fDefenderPosition);
		IServerJsonOption.ATTACKER_ID.addTo(jsonObject, injuryContext.fAttackerId);
		IServerJsonOption.ARMOR_ROLL.addTo(jsonObject, injuryContext.fArmorRoll);
		IServerJsonOption.ARMOR_BROKEN.addTo(jsonObject, injuryContext.fArmorBroken);
		IServerJsonOption.INJURY_ROLL.addTo(jsonObject, injuryContext.fInjuryRoll);
		IServerJsonOption.INJURY.addTo(jsonObject, injuryContext.fInjury);
		IServerJsonOption.INJURY_DECAY.addTo(jsonObject, injuryContext.fInjuryDecay);
		IServerJsonOption.CASUALTY_ROLL.addTo(jsonObject, injuryContext.fCasualtyRoll);
		IServerJsonOption.SERIOUS_INJURY.addTo(jsonObject, injuryContext.fSeriousInjury);
		IServerJsonOption.CASUALTY_ROLL_DECAY.addTo(jsonObject, injuryContext.fCasualtyRollDecay);
		IServerJsonOption.SERIOUS_INJURY_DECAY.addTo(jsonObject, injuryContext.fSeriousInjuryDecay);
		IServerJsonOption.APOTHECARY_STATUS.addTo(jsonObject, injuryContext.fApothecaryStatus);
		IServerJsonOption.SEND_TO_BOX_REASON.addTo(jsonObject, injuryContext.fSendToBoxReason);
		IServerJsonOption.SEND_TO_BOX_TURN.addTo(jsonObject, injuryContext.fSendToBoxTurn);
		IServerJsonOption.SEND_TO_BOX_HALF.addTo(jsonObject, injuryContext.fSendToBoxHalf);
		IServerJsonOption.SOUND.addTo(jsonObject, injuryContext.fSound);
		IServerJsonOption.APOTHECARY_MODE.addTo(jsonObject, injuryContext.fApothecaryMode);

		JsonArray armorModifiers = new JsonArray();
		for (ArmorModifier armorModifier : injuryContext.getArmorModifiers()) {
			armorModifiers.add(UtilJson.toJsonValue(armorModifier));
		}
		IServerJsonOption.ARMOR_MODIFIERS.addTo(jsonObject, armorModifiers);

		JsonArray injuryModifiers = new JsonArray();
		for (InjuryModifier injuryModifier : injuryContext.getInjuryModifiers()) {
			injuryModifiers.add(UtilJson.toJsonValue(injuryModifier));
		}
		IServerJsonOption.INJURY_MODIFIERS.addTo(jsonObject, injuryModifiers);

		JsonArray casualtyModifiers = new JsonArray();
		injuryContext.getCasualtyModifiers().forEach(modifier ->  casualtyModifiers.add(UtilJson.toJsonValue(modifier)));
		IServerJsonOption.CASUALTY_MODIFIERS.addTo(jsonObject, casualtyModifiers);

		return jsonObject;

	}

	public InjuryResult initFrom(IFactorySource source, JsonValue pJsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);

		injuryContext.fInjuryType = (InjuryType) IServerJsonOption.INJURY_TYPE.getFrom(source, jsonObject);
		injuryContext.fDefenderId = IServerJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		injuryContext.fDefenderPosition = IServerJsonOption.DEFENDER_POSITION.getFrom(source, jsonObject);
		injuryContext.fAttackerId = IServerJsonOption.ATTACKER_ID.getFrom(source, jsonObject);
		injuryContext.fArmorRoll = IServerJsonOption.ARMOR_ROLL.getFrom(source, jsonObject);
		injuryContext.fArmorBroken = IServerJsonOption.ARMOR_BROKEN.getFrom(source, jsonObject);
		injuryContext.fInjuryRoll = IServerJsonOption.INJURY_ROLL.getFrom(source, jsonObject);
		injuryContext.fInjury = IServerJsonOption.INJURY.getFrom(source, jsonObject);
		injuryContext.fInjuryDecay = IServerJsonOption.INJURY_DECAY.getFrom(source, jsonObject);
		injuryContext.fCasualtyRoll = IServerJsonOption.CASUALTY_ROLL.getFrom(source, jsonObject);
		injuryContext.fSeriousInjury = (SeriousInjury) IServerJsonOption.SERIOUS_INJURY.getFrom(source, jsonObject);
		injuryContext.fCasualtyRollDecay = IServerJsonOption.CASUALTY_ROLL_DECAY.getFrom(source, jsonObject);
		injuryContext.fSeriousInjuryDecay = (SeriousInjury) IServerJsonOption.SERIOUS_INJURY_DECAY.getFrom(source, jsonObject);
		injuryContext.fApothecaryStatus = (ApothecaryStatus) IServerJsonOption.APOTHECARY_STATUS.getFrom(source, jsonObject);
		injuryContext.fSendToBoxReason = (SendToBoxReason) IServerJsonOption.SEND_TO_BOX_REASON.getFrom(source, jsonObject);
		injuryContext.fSendToBoxTurn = IServerJsonOption.SEND_TO_BOX_TURN.getFrom(source, jsonObject);
		injuryContext.fSendToBoxHalf = IServerJsonOption.SEND_TO_BOX_HALF.getFrom(source, jsonObject);
		injuryContext.fSound = (SoundId) IServerJsonOption.SOUND.getFrom(source, jsonObject);
		injuryContext.fApothecaryMode = (ApothecaryMode) IServerJsonOption.APOTHECARY_MODE.getFrom(source, jsonObject);

		injuryContext.fArmorModifiers.clear();
		ArmorModifierFactory armorModifierFactory = source.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);
		JsonArray armorModifiers = IServerJsonOption.ARMOR_MODIFIERS.getFrom(source, jsonObject);
		for (int i = 0; i < armorModifiers.size(); i++) {
			injuryContext.fArmorModifiers
					.add((ArmorModifier) UtilJson.toEnumWithName(armorModifierFactory, armorModifiers.get(i)));
		}

		injuryContext.fInjuryModifiers.clear();
		InjuryModifierFactory injuryModifierFactory = source.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		JsonArray injuryModifiers = IServerJsonOption.INJURY_MODIFIERS.getFrom(source, jsonObject);
		for (int i = 0; i < injuryModifiers.size(); i++) {
			injuryContext.fInjuryModifiers
					.add((InjuryModifier) UtilJson.toEnumWithName(injuryModifierFactory, injuryModifiers.get(i)));
		}

		injuryContext.casualtyModifiers.clear();
		CasualtyModifierFactory casualtyModifierFactory = source.getFactory(FactoryType.Factory.CASUALTY_MODIFIER);
		JsonArray casualtyModifiers = IServerJsonOption.CASUALTY_MODIFIERS.getFrom(source, jsonObject);
		casualtyModifiers.values().forEach(jsonValue -> injuryContext.casualtyModifiers
			.add((CasualtyModifier) UtilJson.toEnumWithName(casualtyModifierFactory, jsonValue)));
		return this;

	}

}

package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ApothecaryStatus;
import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.modifiers.InjuryModifier;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.InjuryModifierFactory;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.ArmorModifier;
import com.balancedbytes.games.ffb.modifiers.ArmorModifierFactory;
import com.balancedbytes.games.ffb.report.ReportInjury;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 *
 * @author Kalimar
 */
public class InjuryResult implements IJsonSerializable {

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
		Game game = pStep.getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		Player<?> defender = game.getPlayerById(injuryContext.getDefenderId());

		PlayerResult playerResult = gameResult.getPlayerResult(defender);
		if (defender.hasSkillWithProperty(NamedProperties.getsSentOffAtEndOfDrive)) {
			playerResult.setHasUsedSecretWeapon(true);
		}

		boolean isCausedByOpponent = injuryContext.getInjuryType().isCausedByOpponent();

		PlayerState oldPlayerState = game.getFieldModel().getPlayerState(defender);
		if (injuryContext.getPlayerState() != null) {
			// Make sure the player isn't converted from a stun to prone (for example when
			// fouling a stunned player)
			if ((injuryContext.getPlayerState().getBase() != PlayerState.PRONE)
					|| (oldPlayerState.getBase() != PlayerState.STUNNED)) {
				PlayerState playerState = game.getFieldModel().getPlayerState(defender);
				game.getFieldModel().setPlayerState(defender, playerState.changeBase(injuryContext.getPlayerState().getBase()));
				if ((injuryContext.getPlayerState().getBase() == PlayerState.STUNNED)
						&& (((defender.getTeam() == game.getTeamHome()) && game.isHomePlaying())
								|| ((defender.getTeam() == game.getTeamAway()) && !game.isHomePlaying()))) {
					game.getFieldModel().setPlayerState(defender,
							game.getFieldModel().getPlayerState(defender).changeActive(false));
				}
			}
			if (injuryContext.isCasualty() || injuryContext.isKnockedOut() || injuryContext.isReserve()) {
				UtilBox.putPlayerIntoBox(game, defender);
				UtilServerGame.updateLeaderReRolls(pStep);
			}
		}
		// death is also a serious injury
		if ((injuryContext.getPlayerState() != null) && (injuryContext.getPlayerState().getBase() == PlayerState.RIP)) {
			playerResult.setSeriousInjury(SeriousInjury.DEAD);
			playerResult.setSeriousInjuryDecay(null);
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
		if (injuryContext.getSufferedInjury() != null) {
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
		pStep.getResult().addReport(new ReportInjury(injuryContext.getDefenderId(), injuryContext.getInjuryType(),
				injuryContext.isArmorBroken(), injuryContext.getArmorModifiers(), injuryContext.getArmorRoll(),
				injuryContext.getInjuryModifiers(), injuryContext.getInjuryRoll(), injuryContext.getCasualtyRoll(),
				injuryContext.getSeriousInjury(), injuryContext.getCasualtyRollDecay(), injuryContext.getSeriousInjuryDecay(),
				injuryContext.getInjury(), injuryContext.getInjuryDecay(), injuryContext.getAttackerId()));
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

		return this;

	}

}

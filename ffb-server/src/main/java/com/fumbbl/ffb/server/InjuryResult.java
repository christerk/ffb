package com.fumbbl.ffb.server;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.BloodSpot;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.CardFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SeriousInjuryFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.SppMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.mechanic.StateMechanic;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.util.UtilServerCards;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilBox;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class InjuryResult implements IJsonSerializable {

	private boolean alreadyReported;
	private boolean preRegeneration = true;

	private static final List<Integer> basePrecedenceList = new ArrayList<Integer>() {{
		add(PlayerState.PRONE);
		add(PlayerState.STUNNED);
		add(PlayerState.KNOCKED_OUT);
		add(PlayerState.BADLY_HURT);
		add(PlayerState.SERIOUS_INJURY);
		add(PlayerState.RIP);
		add(PlayerState.RESERVE);
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

	public void setAlreadyReported(boolean alreadyReported) {
		this.alreadyReported = alreadyReported;
	}

	public boolean isPreRegeneration() {
		return preRegeneration;
	}

	public void passedRegeneration() {
		this.preRegeneration = false;
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
			// fouling a stunned player) or in case of two cas from a multiblock player a BH does override a RIP
			if (!basePrecedenceList.contains(oldPlayerState.getBase()) ||
				basePrecedenceList.indexOf(injuryContext.getPlayerState().getBase()) > basePrecedenceList.indexOf(oldPlayerState.getBase())) {
				PlayerState playerState = game.getFieldModel().getPlayerState(defender);
				game.getFieldModel().setPlayerState(defender, playerState.changeBase(injuryContext.getPlayerState().getBase()));
				boolean homeBomb = false, awayBomb = false;
				String originalBombardier = pStep.getGameState().getPassState().getOriginalBombardier();
				if (StringTool.isProvided(originalBombardier)) {
					Player<?> player = game.getPlayerById(originalBombardier);
					if (game.getTeamHome().hasPlayer(player)) {
						homeBomb = true;
					} else {
						awayBomb = true;
					}
				}
				if ((injuryContext.getPlayerState().getBase() == PlayerState.STUNNED)
					&& ((defender.getTeam() == game.getTeamHome() && (game.isHomePlaying() || homeBomb))
					|| (defender.getTeam() == game.getTeamAway() && (!game.isHomePlaying() || awayBomb)))) {
					game.getFieldModel().setPlayerState(defender,
						game.getFieldModel().getPlayerState(defender).changeActive(false));
				}
				if (injuryContext.isCasualty() || injuryContext.isKnockedOut() || injuryContext.isReserve()) {
					UtilBox.putPlayerIntoBox(game, defender);
					UtilServerGame.checkForWastedSkills(defender, pStep, game.getFieldModel());
					UtilServerGame.updatePlayerStateDependentProperties(pStep);
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
				if (attacker != null
					&& injuryContext.getSufferedInjury().isCasualty()
					&& injuryContext.getInjuryType().isWorthSpps()
					&& (attacker.getTeam() != defender.getTeam())) {
					PlayerResult attackerResult = gameResult.getPlayerResult(attacker);
					SppMechanic spp = (SppMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.SPP.name());
					spp.addCasualty(pStep.getGameState().getPrayerState().getAdditionalCasSppTeams(), attackerResult);

				}
			}
			game.getFieldModel().add(new BloodSpot(injuryContext.getDefenderPosition(), injuryContext.getSufferedInjury()));
		}
		if (injuryContext.getSufferedInjury() != null && injuryContext.getSufferedInjury().getBase() == PlayerState.SERIOUS_INJURY) {
			pStep.getGameState().updatePlayerMarkings();
		}
	}

	public void report(IStep pStep) {
		Game game = pStep.getGameState().getGame();
		StateMechanic mechanic = game.getMechanic(Mechanic.Type.STATE);
		mechanic.reportInjury(pStep, this);
	}

	public boolean handleIgnoringArmourBreaks(IStep pStep, Player<?> pDefender, Game game) {
		if (injuryContext.isArmorBroken()) {
			if (pDefender.hasSkillProperty(NamedProperties.ignoreFirstArmourBreak) && (injuryContext.getArmorRoll() != null)) {
				injuryContext.setArmorBroken(false);
				injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
				String source = pDefender.getSource(NamedProperties.ignoreFirstArmourBreak);
				Card card = ((CardFactory) game.getFactory(FactoryType.Factory.CARD)).forName(source);
				UtilServerCards.deactivateCard(pStep, card);
				return true;
			}
		}

		return false;
	}

	public void swapToAlternateContext(IStep pStep, Game game) {
		if (injuryContext.getModifiedInjuryContext() != null) {
			injuryContext = injuryContext.getModifiedInjuryContext();
			alreadyReported = false;
			if (handleIgnoringArmourBreaks(pStep, game.getPlayerById(injuryContext.fDefenderId), game)) {
				injuryContext.setSendToBoxReason(null);
				injuryContext.setSendToBoxHalf(0);
				injuryContext.setSendToBoxTurn(0);
				injuryContext.setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
				injuryContext.setSeriousInjury(null);
				injuryContext.setSeriousInjuryDecay(null);
				injuryContext.setSound(SoundId.FALL);
			}
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isAlreadyReported() {
		return alreadyReported;
	}

	// JSON serialization
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.ALREADY_REPORTED.addTo(jsonObject, alreadyReported);
		IServerJsonOption.PRE_REGENERATION.addTo(jsonObject, preRegeneration);
		injuryContext.toJsonValue(jsonObject);

		return jsonObject;
	}

	public InjuryResult initFrom(IFactorySource source, JsonValue jsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);

		alreadyReported = IServerJsonOption.ALREADY_REPORTED.getFrom(source, jsonObject);

		injuryContext.initFrom(source, jsonObject);

		if (IServerJsonOption.PRE_REGENERATION.isDefinedIn(jsonObject)) {
			preRegeneration = IServerJsonOption.PRE_REGENERATION.getFrom(source, jsonObject);
		}

		return this;
	}


}

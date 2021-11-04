package com.fumbbl.ffb.server.step.bb2020.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.GazeModifierFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.GazeModifier;
import com.fumbbl.ffb.modifiers.GazeModifierContext;
import com.fumbbl.ffb.report.bb2020.ReportHypnoticGazeRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Optional;
import java.util.Set;

/**
 * Step in move sequence to handle skill HYPNOTIC_GAZE.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * <p>
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepHypnoticGaze extends AbstractStepWithReRoll {

	private String fGotoLabelOnEnd;

	public StepHypnoticGaze(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.HYPNOTIC_GAZE;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				// mandatory
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_END) {
					fGotoLabelOnEnd = (String) parameter.getValue();
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		boolean doGaze = ((actingPlayer.getPlayerAction() == PlayerAction.GAZE) && (game.getDefender() != null)) && game.getDefender().getTeam() != game.getActingTeam();
		Optional<Skill> gazeSkill = UtilCards.getSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.inflictsConfusion);
		if (!doGaze) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			game.setDefenderId(null);
			return;
		}
		boolean gotoEndLabel = true;
		if (ReRolledActions.HYPNOTIC_GAZE == getReRolledAction()) {
			if ((getReRollSource() == null)
				|| !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
				doGaze = false;
			}
		} else {
			doGaze = gazeSkill.isPresent() && !UtilCards.hasSkillToCancelProperty(actingPlayer.getPlayer(), NamedProperties.inflictsConfusion);
		}
		if (doGaze && gazeSkill.isPresent()) {
			actingPlayer.markSkillUsed(gazeSkill.get());
			int roll = getGameState().getDiceRoller().rollSkill();
			GazeModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.GAZE_MODIFIER);
			Set<GazeModifier> gazeModifiers = modifierFactory.findModifiers(new GazeModifierContext(game, actingPlayer.getPlayer()));
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
			int minimumRoll = mechanic.minimumRollHypnoticGaze(actingPlayer.getPlayer(), gazeModifiers);
			boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
			boolean reRolled = ((getReRolledAction() == ReRolledActions.HYPNOTIC_GAZE) && (getReRollSource() != null));
			if (!reRolled) {
				getResult().setSound(SoundId.HYPNO);
			}
			getResult().addReport(new ReportHypnoticGazeRoll(actingPlayer.getPlayerId(), successful,
				roll, minimumRoll, reRolled, gazeModifiers.toArray(new GazeModifier[0]), game.getDefenderId()));
			if (successful) {
				PlayerState oldVictimState = game.getFieldModel().getPlayerState(game.getDefender());
				if (!oldVictimState.isConfused() && !oldVictimState.isHypnotized()) {
					game.getFieldModel().setPlayerState(game.getDefender(), oldVictimState.changeHypnotized(true));
				}
			} else {
				if ((getReRolledAction() != ReRolledActions.HYPNOTIC_GAZE) && UtilServerReRoll.askForReRollIfAvailable(
					getGameState(), actingPlayer.getPlayer(), ReRolledActions.HYPNOTIC_GAZE, minimumRoll, false)) {
					gotoEndLabel = false;
				}
			}
		}
		if (gotoEndLabel) {
			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
			game.setDefenderId(null);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		return jsonObject;
	}

	@Override
	public StepHypnoticGaze initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(game, jsonObject);
		return this;
	}

}

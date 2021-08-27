package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.report.bb2020.ReportCloudBurster;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.skillbehaviour.StepHook;
import com.fumbbl.ffb.server.skillbehaviour.StepHook.HookPoint;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.bb2020.pass.state.PassState;
import com.fumbbl.ffb.skill.bb2020.CloudBurster;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2020)
public class CloudBursterBehaviour extends SkillBehaviour<CloudBurster> {
	public CloudBursterBehaviour() {
		super();

		registerStep(StepId.CLOUD_BURSTER, StepCloudBurster.class);
	}

	@StepHook(HookPoint.PASS_INTERCEPT)
	@RulesCollection(Rules.BB2020)
	public static class StepCloudBurster extends AbstractStep {
		private String fGotoLabelOnFailure;

		public StepCloudBurster(GameState pGameState) {
			super(pGameState, StepAction.NEXT_STEP);
		}

		@Override
		public StepId getId() {
			return StepId.CLOUD_BURSTER;
		}

		@Override
		public void init(StepParameterSet pParameterSet) {
			if (pParameterSet != null) {
				for (StepParameter parameter : pParameterSet.values()) {
					// mandatory
					if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_FAILURE) {
						fGotoLabelOnFailure = (String) parameter.getValue();
					}
				}
			}
			if (fGotoLabelOnFailure == null) {
				throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
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
			PassState state = getGameState().getPassState();
			Player<?> interceptor = game.getPlayerById(state.getInterceptorId());
			if (!state.isDeflectionSuccessful() || (game.getThrower() == null) || (interceptor == null)) {
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
				return;
			}

			Skill canForceInterceptionRerollSkill = game.getThrower().getSkillWithProperty(NamedProperties.canForceInterceptionRerollOfLongPasses);

			FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
			PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
			PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(), false);

			boolean useCloudBurster = canForceInterceptionRerollSkill != null
					&& !UtilCards.cancelsSkill(interceptor, canForceInterceptionRerollSkill)
					&& NamedProperties.canForceInterceptionRerollOfLongPasses.appliesToContext(new PassContext(game, interceptor, passingDistance, false));
			if (useCloudBurster) {
				getResult().addReport(new ReportCloudBurster(game.getThrowerId(), state.getInterceptorId(), game.getThrower().getTeam().getId()));

				state.setDeflectionSuccessful(false);
				StepParameterSet params = new StepParameterSet();
				params.add(StepParameter.from(StepParameterKey.GOTO_LABEL_ON_FAILURE, fGotoLabelOnFailure));
				IStep interceptStep = getGameState().getStepFactory().create(StepId.INTERCEPT, null, params);
				getGameState().getStepStack().push(interceptStep);
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
			}
		}

		// JSON serialization

		@Override
		public JsonObject toJsonValue() {
			JsonObject jsonObject = super.toJsonValue();
			IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
			return jsonObject;
		}

		@Override
		public AbstractStep initFrom(IFactorySource game, JsonValue pJsonValue) {
			super.initFrom(game, pJsonValue);
			JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
			fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
			return this;
		}
	}
}

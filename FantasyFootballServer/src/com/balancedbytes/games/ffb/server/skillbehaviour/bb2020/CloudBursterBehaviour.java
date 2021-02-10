package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.mechanics.PassMechanic;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.skillbehaviour.StepHook;
import com.balancedbytes.games.ffb.server.skillbehaviour.StepHook.HookPoint;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.bb2020.state.PassState;
import com.balancedbytes.games.ffb.skill.bb2020.CloudBurster;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
			if (!state.isInterceptionSuccessful() || (game.getThrower() == null) || (interceptor == null)) {
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
				return;
			}

			Skill canForceInterceptionRerollSkill = game.getThrower().getSkillWithProperty(NamedProperties.canForceInterceptionReroll);

			FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
			PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
			PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(), false);

			boolean doSafeThrow = canForceInterceptionRerollSkill != null
					&& !UtilCards.cancelsSkill(interceptor, canForceInterceptionRerollSkill)
					&& (passingDistance == PassingDistance.LONG_PASS || passingDistance == PassingDistance.LONG_BOMB);
			if (doSafeThrow) {
				getResult().addReport(new ReportSkillRoll(ReportId.SAFE_THROW_ROLL, game.getThrowerId(), true,
						7, 1, false));

				state.setInterceptionSuccessful(false);
				StepParameterSet params = new StepParameterSet();
				params.add(StepParameter.from(StepParameterKey.GOTO_LABEL_ON_FAILURE, fGotoLabelOnFailure));
				IStep interceptStep = getGameState().getStepFactory().create(StepId.INTERCEPT, null, params);
				getGameState().getStepStack().push(interceptStep);
			}
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
			/* else {
				game.getFieldModel().setRangeRuler(null);
				FieldCoordinate startCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
				FieldCoordinate interceptorCoordinate = null;
				if (interceptor != null) {
					interceptorCoordinate = game.getFieldModel().getPlayerCoordinate(interceptor);
				}
				if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
					getResult().setAnimation(new Animation(AnimationType.THROW_BOMB, startCoordinate, game.getPassCoordinate(),
							interceptorCoordinate));
				} else {
					getResult().setAnimation(
							new Animation(AnimationType.PASS, startCoordinate, game.getPassCoordinate(), interceptorCoordinate));
				}
				UtilServerGame.syncGameModel(this);
				if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
					game.getFieldModel().setBombCoordinate(interceptorCoordinate);
					game.getFieldModel().setBombMoving(false);
				} else {
					game.getFieldModel().setBallCoordinate(interceptorCoordinate);
					game.getFieldModel().setBallMoving(false);
				}
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
			}*/
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

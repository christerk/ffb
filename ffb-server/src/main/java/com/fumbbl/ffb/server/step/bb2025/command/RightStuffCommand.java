package com.fumbbl.ffb.server.step.bb2025.command;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.DeferredCommandId;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerInjury;

@RulesCollection(RulesCollection.Rules.BB2025)
public class RightStuffCommand extends DeferredCommand {

	private String playerId;
	private boolean hasBall;

	@SuppressWarnings("unused")
	public RightStuffCommand() {
		// for json deserialization
	}

	public RightStuffCommand(String playerId, boolean hasBall) {
		this.playerId = playerId;
		this.hasBall = hasBall;
	}

	@Override
	public void execute(IStep step) {
		Player<?> thrownPlayer = step.getGameState().getGame().getPlayerById(playerId);
		StepParameterSet params = UtilServerInjury.dropPlayer(step, thrownPlayer, ApothecaryMode.THROWN_PLAYER);
		params.remove(StepParameterKey.END_TURN);
		step.publishParameters(params);
		if (hasBall) {
			step.publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
		}
		step.publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step

	}

	@Override
	public DeferredCommandId getId() {
		return DeferredCommandId.RIGHT_STUFF;
	}

	@Override
	protected DeferredCommand initChildMember(IFactorySource source, JsonObject jsonObject) {
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		hasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(source, jsonObject);
		return super.initChildMember(source, jsonObject);
	}

	@Override
	protected JsonObject addChildMember(JsonObject jsonObject) {
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.THROWN_PLAYER_HAS_BALL.addTo(jsonObject, hasBall);
		return super.addChildMember(jsonObject);
	}
}

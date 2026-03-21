package com.fumbbl.ffb.server.step.bb2025.start;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.util.UtilServerGame;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepMasterChef extends AbstractStep {

	public StepMasterChef(GameState gameState) {
		super(gameState);
	}

	@Override
	public StepId getId() {
		return StepId.MASTER_CHEF;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(com.fumbbl.ffb.server.net.ReceivedCommand receivedCommand) {
		StepCommandStatus status = super.handleCommand(receivedCommand);
		if (status == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return status;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		if (game.getHalf() < 3 && game.firstTurnOfHalf()) {
			UtilServerGame.handleChefRolls(this, game);
			setLeaders(game.getTeamHome());
			setLeaders(game.getTeamAway());
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void setLeaders(Team team) {
		Arrays.stream(team.getPlayers())
			.filter(player -> player.hasSkillProperty(NamedProperties.grantsTeamReRollWhenOnPitch))
			.forEach(player -> getGameState().addLeader(player));
	}

}

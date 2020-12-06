package com.balancedbytes.games.ffb.server.step.game.end;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.report.ReportDefectingPlayers;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in end game sequence to handle player loss.
 * 
 * @author Kalimar
 */
public final class StepPlayerLoss extends AbstractStep {

	public StepPlayerLoss(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.PLAYER_LOSS;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		Team team = null;
		if (gameResult.getTeamResultHome().hasConceded()
				&& (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamHome()).length > 2)) {
			team = game.getTeamHome();
		}
		if (gameResult.getTeamResultAway().hasConceded()
				&& (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamAway()).length > 2)) {
			team = game.getTeamAway();
		}
		if (team != null) {
			List<String> defectingPlayerIds = new ArrayList<String>();
			List<Integer> defectingRolls = new ArrayList<Integer>();
			List<Boolean> defectingFlags = new ArrayList<Boolean>();
			for (Player player : team.getPlayers()) {
				PlayerResult playerResult = gameResult.getPlayerResult(player);
				if (playerResult.getCurrentSpps() >= 51) {
					defectingPlayerIds.add(player.getId());
					int defectingRoll = getGameState().getDiceRoller().rollPlayerLoss();
					defectingRolls.add(defectingRoll);
					boolean playerDefecting = DiceInterpreter.getInstance().isPlayerDefecting(defectingRoll);
					defectingFlags.add(playerDefecting);
					playerResult.setDefecting(playerDefecting);
				}
			}
			if (defectingPlayerIds.size() > 0) {
				getResult()
						.addReport(new ReportDefectingPlayers(defectingPlayerIds.toArray(new String[defectingPlayerIds.size()]),
								ArrayTool.toIntArray(defectingRolls), ArrayTool.toBooleanArray(defectingFlags)));
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepPlayerLoss initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		return this;
	}

}

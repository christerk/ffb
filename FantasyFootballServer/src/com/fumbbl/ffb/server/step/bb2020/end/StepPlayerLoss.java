package com.fumbbl.ffb.server.step.bb2020.end;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.ReportDefectingPlayers;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Step in end game sequence to handle player loss.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
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
		if (gameResult.getTeamResultHome().hasConceded() && !game.isConcededLegally()) {
			team = game.getTeamHome();
		}
		if (gameResult.getTeamResultAway().hasConceded() && !game.isConcededLegally()) {
			team = game.getTeamAway();
		}
		if (team != null) {
			List<String> defectingPlayerIds = new ArrayList<>();
			List<Integer> defectingRolls = new ArrayList<>();
			List<Boolean> defectingFlags = new ArrayList<>();
			for (Player<?> player : team.getPlayers()) {
				PlayerResult playerResult = gameResult.getPlayerResult(player);
				int playerSkillCount = countAdvancements(player.getSkills());
				int positionSkillCount = countAdvancements(player.getPosition().getSkills());
				if (playerSkillCount - positionSkillCount >= 3 && (playerResult.getSeriousInjury() == null || !playerResult.getSeriousInjury().isDead())) {
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
						.addReport(new ReportDefectingPlayers(defectingPlayerIds.toArray(new String[0]),
								ArrayTool.toIntArray(defectingRolls), ArrayTool.toBooleanArray(defectingFlags)));
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private int countAdvancements(Skill[] skills) {
		return (int) Arrays.stream(skills).filter(skill -> skill.getCategory() != SkillCategory.STAT_DECREASE).count();
	}
	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepPlayerLoss initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		return this;
	}

}

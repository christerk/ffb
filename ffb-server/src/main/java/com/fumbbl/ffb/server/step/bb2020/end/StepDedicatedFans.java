package com.fumbbl.ffb.server.step.bb2020.end;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.report.mixed.ReportDedicatedFans;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepDedicatedFans extends AbstractStep {
	public StepDedicatedFans(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.DEDICATED_FANS;
	}

	@Override
	public void start() {

		Game game = getGameState().getGame();

		TeamResult homeResult = game.getGameResult().getTeamResultHome();
		TeamResult awayResult = game.getGameResult().getTeamResultAway();

		int homeDie;
		int awayDie;

		String concededTeam = null;
		Team winningTeam = null;

		Team teamHome = game.getTeamHome();
		Team teamAway = game.getTeamAway();

		if (homeResult.hasConceded() && !game.isConcededLegally()) {
			homeDie = 3;
			concededTeam = teamHome.getId();
			winningTeam = teamAway;
		} else {
			homeDie = 6;
		}

		if (awayResult.hasConceded() && !game.isConcededLegally()) {
			awayDie = 3;
			concededTeam = teamAway.getId();
			winningTeam = teamHome;
		} else {
			awayDie = 6;
		}

		if (concededTeam == null) {
			if (homeResult.getScore() + homeResult.getPenaltyScore() > awayResult.getScore() + awayResult.getPenaltyScore()) {
				winningTeam = teamHome;
			} else if (awayResult.getScore() + awayResult.getPenaltyScore() > homeResult.getScore() + homeResult.getPenaltyScore()) {
				winningTeam = teamAway;
			}
		}

		if (winningTeam == null) {
			getResult().addReport(new ReportDedicatedFans());
		} else {
			int rollHome =  getGameState().getDiceRoller().rollDice(homeDie);
			int rollAway =  getGameState().getDiceRoller().rollDice(awayDie);

			int modifierHome = modifier(rollHome, teamHome.getDedicatedFans(), winningTeam == teamHome, teamHome.getId().equals(concededTeam));
			int modifierAway = modifier(rollAway, teamAway.getDedicatedFans(), winningTeam == teamAway, teamAway.getId().equals(concededTeam));

			homeResult.setDedicatedFansModifier(modifierHome);
			awayResult.setDedicatedFansModifier(modifierAway);

			getResult().addReport(new ReportDedicatedFans(rollHome, modifierHome, rollAway, modifierAway, concededTeam, concededTeam != null));
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private int modifier(int roll, int dedicatedFans, boolean winning, boolean conceded) {
		if (conceded) {
			return Math.max(Math.min(roll, dedicatedFans - 1), 0) * -1;
		} else if (winning) {
			return (roll >= dedicatedFans) ? 1 : 0;
		} else {
			return (roll >= dedicatedFans) ? 0 : -1;
		}
	}
}

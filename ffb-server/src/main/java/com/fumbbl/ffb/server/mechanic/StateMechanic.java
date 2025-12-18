package com.fumbbl.ffb.server.mechanic;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.report.ReportInducement;
import com.fumbbl.ffb.server.step.IStep;

public abstract class StateMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.STATE;
	}

	public abstract void updateLeaderReRollsForTeam(TurnData pTurnData, Team pTeam, FieldModel pFieldModel,
	                                                IStep pStep);

	protected boolean teamHasLeaderOnField(Team pTeam, FieldModel pFieldModel) {
		for (Player<?> player : pTeam.getPlayers()) {
			if (playerOnField(player, pFieldModel)
					&& player.hasSkillProperty(NamedProperties.grantsTeamReRollWhenOnPitch)) {
				return true;
			}
		}
		return false;
	}

	protected static boolean playerOnField(Player<?> pPlayer, FieldModel pFieldModel) {
		FieldCoordinate fieldCoordinate = pFieldModel.getPlayerCoordinate(pPlayer);
		return ((fieldCoordinate != null) && !fieldCoordinate.isBoxCoordinate());
	}

	public abstract void startHalf(IStep pStep, int pHalf);

	protected void addApothecaries(IStep pStep, boolean pHomeTeam) {
		Game game = pStep.getGameState().getGame();
		Team team = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
		TurnData turnData = pHomeTeam ? game.getTurnDataHome() : game.getTurnDataAway();
		turnData.setApothecaries(team.getApothecaries());
		turnData.getInducementSet().getInducementMapping().entrySet().stream().filter(entry -> entry.getKey().hasSingleUsage(
						Usage.APOTHECARY))
				.findFirst().ifPresent(entry -> {
					Inducement wanderingApothecaries = entry.getValue();
					if (wanderingApothecaries.getValue() > 0) {
						turnData.setApothecaries(turnData.getApothecaries() + wanderingApothecaries.getValue());
						turnData.setWanderingApothecaries(wanderingApothecaries.getValue());
						pStep.getResult().addReport(
								new ReportInducement(team.getId(), entry.getKey(), wanderingApothecaries.getValue()));
					}
				});

		turnData.getInducementSet().getInducementMapping().entrySet().stream().filter(entry -> entry.getKey().hasUsage(Usage.APOTHECARY_JOURNEYMEN))
				.findFirst().ifPresent(entry -> {
					Inducement plagueDoctors = entry.getValue();
					if (plagueDoctors.getValue() > 0) {
						turnData.setPlagueDoctors(plagueDoctors.getValue());
					}
				});
	}

	protected void addReRolls(IStep pStep, boolean pHomeTeam) {
		Game game = pStep.getGameState().getGame();
		Team team = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
		TurnData turnData = pHomeTeam ? game.getTurnDataHome() : game.getTurnDataAway();
		turnData.setReRolls(team.getReRolls());
		turnData.getInducementSet().getInducementMapping().entrySet().stream().filter(entry -> entry.getKey().hasUsage(Usage.REROLL))
				.findFirst().ifPresent(entry -> {
					Inducement extraTraining = entry.getValue();
					if (extraTraining.getValue() > 0) {
						turnData.setReRolls(turnData.getReRolls() + extraTraining.getValue());
						pStep.getResult()
								.addReport(new ReportInducement(team.getId(), entry.getKey(), extraTraining.getValue()));
					}
				});
	}


	protected void resetLeaderState(Game pGame) {
		if (pGame.getHalf() <= 2) {
			pGame.getTurnDataHome().setLeaderState(LeaderState.NONE);
			pGame.getTurnDataAway().setLeaderState(LeaderState.NONE);
		}
	}

	public void resetSpecialSkillAtEndOfDrive(Game game) {
		for (Player<?> player : game.getPlayers()) {
			player.resetUsedSkills(SkillUsageType.ONCE_PER_DRIVE, game);
		}
	}
}

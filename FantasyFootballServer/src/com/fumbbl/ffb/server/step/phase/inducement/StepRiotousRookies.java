package com.fumbbl.ffb.server.step.phase.inducement;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerGender;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.PlayerStatus;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.report.ReportRiotousRookies;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilBox;

import java.io.IOException;

@RulesCollection(RulesCollection.Rules.COMMON)
public class StepRiotousRookies extends AbstractStep {

	public StepRiotousRookies(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.RIOTOUS_ROOKIES;
	}

	@Override
	public void start() {
		Game game = getGameState().getGame();
		hireRiotousRookies(game.getTurnDataHome(), game.getTeamHome());
		hireRiotousRookies(game.getTurnDataAway(), game.getTeamAway());
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void hireRiotousRookies(TurnData turnData, Team team) {
		GameMechanic mechanic = (GameMechanic) getGameState().getGame().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		turnData.getInducementSet().getInducementMapping().keySet().stream()
			.filter(type -> type.getUsages() == Usage.ADD_LINEMEN).findFirst().ifPresent(inducementType -> {
			int value = turnData.getInducementSet().getInducementMapping().get(inducementType).getValue();
			int rookieCounter = 0;
			for (int j = 0; j < value; j++) {
				int[] rookiesRoll = getGameState().getDiceRoller().rollRiotousRookies();
				int rookies = rookiesRoll[0] + rookiesRoll[1] + 1;
				RosterPosition position = mechanic.riotousRookiesPosition(team.getRoster());
				if (position != null) {
					for (int i = 0; i < rookies; i++) {
						riotousPlayer(getGameState().getGame(), team, i + rookieCounter, position);
					}
					getResult().addReport(new ReportRiotousRookies(rookiesRoll, rookies, team.getId()));
				}
				rookieCounter += rookies;
			}
		});
	}

	private void riotousPlayer(Game game, Team team, int index, RosterPosition position) {
		SkillFactory factory = game.getFactory(FactoryType.Factory.SKILL);
		int genderOrdinal = getGameState().getDiceRoller().rollGender();
		PlayerGender gender = PlayerGender.fromOrdinal(genderOrdinal);
		String name = rookieName(position.getNameGenerator(), gender, "RiotousRookie #" + index);
		RosterPlayer riotousPlayer = new RosterPlayer();
		riotousPlayer.setId(team.getId() + "Riotous" + index);
		riotousPlayer.updatePosition(position, game.getRules(), game.getId());
		riotousPlayer.setName(name);
		riotousPlayer.setNr(team.getMaxPlayerNr() + 1);
		riotousPlayer.setGender(gender);
		riotousPlayer.setType(PlayerType.RIOTOUS_ROOKIE);
		riotousPlayer.addSkill(factory.forName("Loner"));
		riotousPlayer.setPlayerStatus(PlayerStatus.JOURNEYMAN);
		team.addPlayer(riotousPlayer);
		game.getFieldModel().setPlayerState(riotousPlayer, new PlayerState(PlayerState.RESERVE));
		UtilBox.putPlayerIntoBox(game, riotousPlayer);
		getGameState().getServer().getCommunication().sendAddPlayer(getGameState(), team.getId(), riotousPlayer,
			game.getFieldModel().getPlayerState(riotousPlayer), game.getGameResult().getPlayerResult(riotousPlayer));
	}

	private String rookieName(String generator, PlayerGender gender, String fallback) {
		StringBuilder url = new StringBuilder(
			getGameState().getServer().getProperty(IServerProperty.FUMBBL_NAMEGENERATOR_BASE));

		if (!url.toString().endsWith("/")) {
			url.append("/");
		}
		url.append(generator).append("/").append(gender.getName());

		try {
			String name = unquote(UtilServerHttpClient.fetchPage(url.toString()));
			if (StringTool.isProvided(name)) {
				return name;
			}
		} catch (IOException e) {
			getGameState().getServer().getDebugLog().log(getGameState().getGame().getId(), e);
		}
		return fallback;
	}

	private String unquote(String quotedString) {
		if (StringTool.isProvided(quotedString)) {
			return quotedString.replace("\"", "");
		}
		return quotedString;
	}
}

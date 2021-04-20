package com.fumbbl.ffb.server.step.phase.inducement;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerGender;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.Game;
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
		if (turnData.getInducementSet().getInducementMapping().keySet().stream()
			.anyMatch(type -> type.getUsage() == Usage.ADD_LINEMEN)) {
			int[] rookiesRoll = getGameState().getDiceRoller().rollRiotousRookies();
			int rookies = rookiesRoll[0] + rookiesRoll[1] + 1;
			RosterPosition position = team.getRoster().getRiotousPosition();
			for (int i = 0; i < rookies; i++) {
				riotousPlayer(getGameState().getGame(), team, i, position);
			}
			getResult().addReport(new ReportRiotousRookies(rookiesRoll, rookies, team.getId()));
		}
	}

	private void riotousPlayer(Game game, Team team, int index, RosterPosition position) {
		SkillFactory factory = game.getFactory(FactoryType.Factory.SKILL);
		int genderOrdinal = getGameState().getDiceRoller().rollGender();
		PlayerGender gender = PlayerGender.fromOrdinal(genderOrdinal);
		String name = rookieName(position.getNameGenerator(), gender, "RiotousRookie #" + index);
		RosterPlayer riotousPlayer = new RosterPlayer();
		riotousPlayer.setId(team.getId() + "Riotous" + index);
		riotousPlayer.updatePosition(position, game.getRules());
		riotousPlayer.setName(name);
		riotousPlayer.setNr(team.getMaxPlayerNr() + 1);
		riotousPlayer.setGender(gender);
		riotousPlayer.setType(PlayerType.RIOTOUS_ROOKIE);
		riotousPlayer.addSkill(factory.forName("Loner"));
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
			getGameState().getServer().getDebugLog().log(e);
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

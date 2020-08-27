package com.balancedbytes.games.ffb.server.step.phase.inducement;

import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.RosterPlayer;
import com.balancedbytes.games.ffb.model.RosterPosition;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.report.ReportRiotousRookies;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.util.UtilBox;

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
    Inducement riotousRookies = turnData.getInducementSet().get(InducementType.RIOTOUS_ROOKIES);
    if (riotousRookies != null) {
      int[] rookiesRoll = getGameState().getDiceRoller().rollRiotousRookies();
      int rookies = rookiesRoll[0] + rookiesRoll[1] + 2;
      RosterPosition position = team.getRoster().getRiotousPosition();
      for (int i = 0; i < rookies; i++) {
        riotousPlayer(getGameState().getGame(), team, i, position);
      }
      getResult().addReport(new ReportRiotousRookies(rookiesRoll, rookies, team == getGameState().getGame().getTeamHome()));
    }
  }

  private void riotousPlayer(Game game, Team team, int index, RosterPosition position) {
    int genderOrdinal = getGameState().getDiceRoller().rollGender();
    RosterPlayer riotousPlayer = new RosterPlayer();
    riotousPlayer.setId(team.getId() + "Riotous" + index);
    riotousPlayer.updatePosition(position);
    riotousPlayer.setName("RiotousRookie #" + index);
    riotousPlayer.setNr(team.getMaxPlayerNr() + 1);
    riotousPlayer.setGender(PlayerGender.fromOrdinal(genderOrdinal));
    riotousPlayer.setType(PlayerType.RIOTOUS_ROOKIE);
    riotousPlayer.addSkill(Skill.LONER);
    team.addPlayer(riotousPlayer);
    game.getFieldModel().setPlayerState(riotousPlayer, new PlayerState(PlayerState.RESERVE));
    UtilBox.putPlayerIntoBox(game, riotousPlayer);
    getGameState().getServer().getCommunication().sendAddPlayer(
      getGameState(), team.getId(), riotousPlayer, game.getFieldModel().getPlayerState(riotousPlayer), game.getGameResult().getPlayerResult(riotousPlayer)
    );
  }
}

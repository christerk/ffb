package com.balancedbytes.games.ffb.server.step.game.end;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportMostValuablePlayers;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in end game sequence to determine the MVP.
 * 
 * @author Kalimar
 */
public final class StepMvp extends AbstractStep {

	public StepMvp(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.MVP;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
  private void executeStep() {
  	
    Game game = getGameState().getGame();
    GameResult gameResult = game.getGameResult();
    ReportMostValuablePlayers mvpReport = new ReportMostValuablePlayers();
    int nrOfHomeMvps = 1;
    int nrOfAwayMvps = 1;
    if (UtilGameOption.isOptionEnabled(game, GameOptionId.EXTRA_MVP)) {
      nrOfHomeMvps++;
      nrOfAwayMvps++;
    }
    if (gameResult.getTeamResultHome().hasConceded() && (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamHome()).length > 2)) {
      nrOfHomeMvps--;
      nrOfAwayMvps++;
    }
    if (gameResult.getTeamResultAway().hasConceded() && (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamAway()).length > 2)) {
      nrOfHomeMvps++;
      nrOfAwayMvps--;
    }
    for (int i = 0; i < nrOfHomeMvps; i++) {
      Player[] playersForMvp = findPlayersForMvp(game.getTeamHome());
      Player mvpHome = getGameState().getDiceRoller().randomPlayer(playersForMvp);
      PlayerResult playerResultHome = gameResult.getPlayerResult(mvpHome);
      playerResultHome.setPlayerAwards(playerResultHome.getPlayerAwards() + 1);
      mvpReport.addPlayerIdHome(mvpHome.getId());
    }
    for (int i = 0; i < nrOfAwayMvps; i++) {
      Player[] playersForMvp = findPlayersForMvp(game.getTeamAway());
      Player mvpAway = getGameState().getDiceRoller().randomPlayer(playersForMvp);
      PlayerResult playerResultAway = gameResult.getPlayerResult(mvpAway);
      playerResultAway.setPlayerAwards(playerResultAway.getPlayerAwards() + 1);
      mvpReport.addPlayerIdAway(mvpAway.getId());
    }

    getResult().addReport(mvpReport);
    getResult().setNextAction(StepAction.NEXT_STEP);
    
  }
  
  private Player[] findPlayersForMvp(Team pTeam) {
    List<Player> players = new ArrayList<Player>();
    GameResult gameResult = getGameState().getGame().getGameResult();
    for (Player player : pTeam.getPlayers()) {
      PlayerResult playerResult = gameResult.getPlayerResult(player);
      if ((SendToBoxReason.MNG != playerResult.getSendToBoxReason()) && (SendToBoxReason.NURGLES_ROT != playerResult.getSendToBoxReason())) {
        players.add(player);
      }
    }
    return players.toArray(new Player[players.size()]);
  }

  // ByteArray serialization
  
	public int getByteArraySerializationVersion() {
		return 1;
	}
	
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    return super.toJsonValue();
  }
  
  @Override
  public StepMvp initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    return this;
  }

}

package com.balancedbytes.games.ffb.server.db.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.DirectionFactory;
import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.PlayerMarker;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.RangeRuler;
import com.balancedbytes.games.ffb.TrackNumber;
import com.balancedbytes.games.ffb.WeatherFactory;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbFieldModelsType;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;

/**
 * 
 * @author Kalimar
 */
public class DbFieldModelsForGameStateQuery extends DbStatement {
  
  private class QueryResult {

    private DbFieldModelsType fType;
    private int fItem;
    private FieldCoordinate fCoordinate;
    private int fNumber1;
    private int fNumber2;
    private boolean fFlag1;
    private boolean fFlag2;
    private int fId1;
    private String fText1;
    private String fText2;
    
    public QueryResult(ResultSet pResultSet) throws SQLException {
      if (pResultSet != null) {
        int col = 1;
        pResultSet.getLong(col++);  // gameStateId
        fType = DbFieldModelsType.fromTypeString(pResultSet.getString(col++));
        fItem = pResultSet.getByte(col++);
        fCoordinate = new FieldCoordinate(pResultSet.getByte(col++), pResultSet.getByte(col++));
        if (pResultSet.wasNull()) {
          fCoordinate = null;
        }
        fNumber1 = pResultSet.getByte(col++);
        fNumber2 = pResultSet.getByte(col++);
        fFlag1 = pResultSet.getBoolean(col++);
        fFlag2 = pResultSet.getBoolean(col++);
        fId1 = pResultSet.getInt(col++);
        fText1 = pResultSet.getString(col++);
        fText2 = pResultSet.getString(col++);
      }
    }
    
    public DbFieldModelsType getType() {
      return fType;
    }
    
    public int getItem() {
      return fItem;
    }
    
    public FieldCoordinate getCoordinate() {
      return fCoordinate;
    }
    
    public int getNumber1() {
      return fNumber1;
    }
    
    public int getNumber2() {
      return fNumber2;
    }
        
    public boolean isFlag1() {
      return fFlag1;
    }

    public boolean isFlag2() {
      return fFlag2;
    }
    
    public int getId1() {
      return fId1;
    }
    
    public String getText1() {
      return fText1;
    }
    
    public String getText2() {
      return fText2;
    }
    
  }
  
  private PreparedStatement fStatement;
  
  public DbFieldModelsForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.FIELD_MODELS_QUERY;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTableFieldModels.TABLE_NAME).append(" WHERE game_state_id=? ORDER BY type, item");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    Game game = pGameState.getGame();
    FieldModel fieldModel = new FieldModel(game);
    game.setFieldModel(fieldModel);
    try {
      fStatement.setLong(1, pGameState.getId());
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
        QueryResult queryResult = new QueryResult(resultSet);
        if (queryResult.getType() != null) {
          switch (queryResult.getType()) {
            case WEATHER:
              addWeather(game, queryResult);
              break;
            case BALL:
              addBall(game, queryResult);
              break;
            case BOMB:
            	addBomb(game, queryResult);
            	break;
            case HOME_PLAYER:
              addPlayer(game, game.getTeamHome(), queryResult);
              break;
            case AWAY_PLAYER:
              addPlayer(game, game.getTeamAway(), queryResult);
              break;
            case BLOODSPOT:
              addBloodSpot(game, queryResult);
              break;
            case PUSHBACK_SQUARE:
              addPushbackSquare(game, queryResult);
              break;
            case TRACK_NUMBER:
              addTrackNumber(game, queryResult);
              break;
            case MOVE_SQUARE:
              addMoveSquare(game, queryResult);
              break;
            case DICE_DECORATION:
              addDiceDecoration(game, queryResult);
              break;
            case RANGE_RULER:
              addRangeRuler(game, queryResult);
              break;
            case FIELD_MARKER:
              addFieldMarker(game, queryResult);
              break;
            case PLAYER_MARKER:
              addPlayerMarker(game, queryResult);
              break;
            case HOME_CARDS:
              addCard(game, game.getTeamHome(), queryResult);
              break;
            case AWAY_CARDS:
              addCard(game, game.getTeamAway(), queryResult);
              break;
            default:
            	break;
          }
        }
      }
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  private void addWeather(Game pGame, QueryResult pQueryResult) {
    pGame.getFieldModel().setWeather(new WeatherFactory().forId(pQueryResult.getNumber1()));
  }

  private void addBall(Game pGame, QueryResult pQueryResult) {
    FieldModel fieldModel = pGame.getFieldModel();
    fieldModel.setBallCoordinate(pQueryResult.getCoordinate());
    fieldModel.setBallInPlay(pQueryResult.isFlag1());
    fieldModel.setBallMoving(pQueryResult.isFlag2());
  }

  private void addBomb(Game pGame, QueryResult pQueryResult) {
    FieldModel fieldModel = pGame.getFieldModel();
    fieldModel.setBombCoordinate(pQueryResult.getCoordinate());
    fieldModel.setBombMoving(pQueryResult.isFlag1());
  }

  private void addPlayer(Game pGame, Team pTeam, QueryResult pQueryResult) {
    FieldModel fieldModel = pGame.getFieldModel();
    Player player = pTeam.getPlayerByNr(pQueryResult.getItem());
    if (player != null) {
      if (pQueryResult.getCoordinate() != null) {
        fieldModel.setPlayerCoordinate(player, pQueryResult.getCoordinate());
      }
      fieldModel.setPlayerState(player, new PlayerState(pQueryResult.getId1()));
    }
  }

  private void addCard(Game pGame, Team pTeam, QueryResult pQueryResult) {
    FieldModel fieldModel = pGame.getFieldModel();
    Player player = pTeam.getPlayerByNr(pQueryResult.getItem());
    Card card = new CardFactory().forId(pQueryResult.getId1());
    if ((player != null) && (card != null)) {
    	fieldModel.addCard(player, card);
    }
  }

  private void addBloodSpot(Game pGame, QueryResult pQueryResult) {
    FieldModel fieldModel = pGame.getFieldModel();
    PlayerState injury = new PlayerState(pQueryResult.getId1());
    if ((pQueryResult.getCoordinate() != null) && (injury != null)) {
      BloodSpot bloodSpot = new BloodSpot(pQueryResult.getCoordinate(), injury); 
      fieldModel.add(bloodSpot);
    }
  }
  
  private void addPushbackSquare(Game pGame, QueryResult pQueryResult) {
    Direction direction = new DirectionFactory().forId(pQueryResult.getNumber1());
    if ((pQueryResult.getCoordinate() != null) && (direction != null)) {
      PushbackSquare pushbackSquare = new PushbackSquare(pQueryResult.getCoordinate(), direction, pQueryResult.isFlag1());
      pushbackSquare.setLocked(pQueryResult.isFlag2());
      pGame.getFieldModel().add(pushbackSquare);
    }
  }

  private void addTrackNumber(Game pGame, QueryResult pQueryResult) {
    if (pQueryResult.getCoordinate() != null) {
      int number = pQueryResult.getNumber1();
      TrackNumber trackNumber = new TrackNumber(pQueryResult.getCoordinate(), number);
      pGame.getFieldModel().add(trackNumber);
    }
  }
  
  private void addMoveSquare(Game pGame, QueryResult pQueryResult) {
    if (pQueryResult.getCoordinate() != null) {
      int minimumRollDodge = pQueryResult.getNumber1();
      int minimumRollGoForIt = pQueryResult.getNumber2();
      MoveSquare moveSquare = new MoveSquare(pQueryResult.getCoordinate(), minimumRollDodge, minimumRollGoForIt);
      pGame.getFieldModel().add(moveSquare);
    }
  }
  
  private void addDiceDecoration(Game pGame, QueryResult pQueryResult) {
    if (pQueryResult.getCoordinate() != null) {
      int nrOfDice = pQueryResult.getNumber1();
      DiceDecoration diceDecoration = new DiceDecoration(pQueryResult.getCoordinate(), nrOfDice);
      pGame.getFieldModel().add(diceDecoration);
    }
  }

  private void addRangeRuler(Game pGame, QueryResult pQueryResult) {
    if (pQueryResult.getCoordinate() != null) {
      boolean throwerFromHomeTeam = pQueryResult.isFlag1();
      boolean throwTeamMate = pQueryResult.isFlag2();
      int playerNr = pQueryResult.getNumber1();
      int minimumRoll = pQueryResult.getNumber2();
      Player thrower = throwerFromHomeTeam ? pGame.getTeamHome().getPlayerByNr(playerNr) : pGame.getTeamAway().getPlayerByNr(playerNr);
      if (thrower != null) {
        RangeRuler rangeRuler = new RangeRuler(thrower.getId(), pQueryResult.getCoordinate(), minimumRoll, throwTeamMate);
        pGame.getFieldModel().setRangeRuler(rangeRuler);
      }
    }
  }
  
  private void addFieldMarker(Game pGame, QueryResult pQueryResult) {
    if (pQueryResult.getCoordinate() != null) {
      FieldMarker fieldMarker = new FieldMarker(pQueryResult.getCoordinate());
      fieldMarker.setHomeText(pQueryResult.getText1());
      fieldMarker.setAwayText(pQueryResult.getText2());
      pGame.getFieldModel().add(fieldMarker);
    }
  }

  private void addPlayerMarker(Game pGame, QueryResult pQueryResult) {
    Player player = pQueryResult.isFlag1() ? pGame.getTeamHome().getPlayerByNr(pQueryResult.getNumber1()) : pGame.getTeamAway().getPlayerByNr(pQueryResult.getNumber1());
    if (player != null) {
      PlayerMarker playerMarker = new PlayerMarker(player.getId());
      playerMarker.setHomeText(pQueryResult.getText1());
      playerMarker.setAwayText(pQueryResult.getText2());
      pGame.getFieldModel().add(playerMarker);
    }
  }

}

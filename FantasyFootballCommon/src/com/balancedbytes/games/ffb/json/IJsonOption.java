package com.balancedbytes.games.ffb.json;

import com.balancedbytes.games.ffb.CardTypeFactory;
import com.balancedbytes.games.ffb.DirectionFactory;
import com.balancedbytes.games.ffb.GameOptionFactory;
import com.balancedbytes.games.ffb.InducementTypeFactory;
import com.balancedbytes.games.ffb.KickoffResultFactory;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.TeamStatusFactory;
import com.balancedbytes.games.ffb.dialog.DialogIdFactory;

/**
 * 
 * @author Kalimar
 */
public interface IJsonOption {

              JsonIntOption AVAILABLE_CARDS = new JsonIntOption("availableCards");
              JsonIntOption AVAILABLE_GOLD = new JsonIntOption("availableGold");
           JsonStringOption AWAY_TEXT = new JsonStringOption("awayText");
  JsonFieldCoordinateOption BALL_COORDINATE = new JsonFieldCoordinateOption("ballCoordinate");         
  JsonFieldCoordinateOption BALL_COORDINATE_WITH_KICK = new JsonFieldCoordinateOption("ballCoordinateWithKick");         
         JsonIntArrayOption BLOCK_ROLL = new JsonIntArrayOption("blockRoll");
  JsonFieldCoordinateOption BOTTOM_RIGHT = new JsonFieldCoordinateOption("bottomRight");
     JsonEnumWithNameOption CARD_TYPE = new JsonEnumWithNameOption("cardType", new CardTypeFactory());
           JsonStringOption CHOOSING_TEAM_ID = new JsonStringOption("choosingTeamId");
           JsonStringOption COACH = new JsonStringOption("coach");
  JsonFieldCoordinateOption COORDINATE = new JsonFieldCoordinateOption("coordinate");
     JsonEnumWithNameOption DIALOG_ID = new JsonEnumWithNameOption("dialogId", new DialogIdFactory());
     JsonEnumWithNameOption DIRECTION = new JsonEnumWithNameOption("direction", new DirectionFactory());
           JsonStringOption DIVISION = new JsonStringOption("division");
          JsonBooleanOption EXHAUSTED = new JsonBooleanOption("exhausted");
             JsonLongOption GAME_ID = new JsonLongOption("gameId");
            JsonArrayOption GAME_LIST = new JsonArrayOption("gameList");
     JsonEnumWithNameOption GAME_OPTION = new JsonEnumWithNameOption("gameOption", new GameOptionFactory());
            JsonArrayOption GAME_OPTIONS = new JsonArrayOption("gameOptions");
          JsonBooleanOption HOME_CHOICE = new JsonBooleanOption("homeChoice");
           JsonStringOption HOME_TEXT = new JsonStringOption("homeText");
     JsonEnumWithNameOption INDUCEMENT_TYPE = new JsonEnumWithNameOption("inducementType", new InducementTypeFactory());
      JsonPlayerStateOption INJURY = new JsonPlayerStateOption("injury");
     JsonEnumWithNameOption KICKOFF_RESULT = new JsonEnumWithNameOption("kickoffResult", new KickoffResultFactory());
          JsonBooleanOption LOCKED = new JsonBooleanOption("locked");
              JsonIntOption MAX_NR_OF_BRIBES = new JsonIntOption("maxNrOfBribes");
              JsonIntOption MINIMUM_ROLL = new JsonIntOption("minimumRoll");
              JsonIntOption MINIMUM_ROLL_DODGE = new JsonIntOption("minimumRollDodge");
              JsonIntOption MINIMUM_ROLL_GFI = new JsonIntOption("minimumRollGfi");
           JsonStringOption NAME = new JsonStringOption("name");
            JsonArrayOption NR_OF_CARDS_PER_TYPE = new JsonArrayOption("nrOfCardsPerType");
              JsonIntOption NR_OF_CARDS = new JsonIntOption("nrOfCards");
              JsonIntOption NR_OF_DICE = new JsonIntOption("nrOfDice");
              JsonIntOption NUMBER = new JsonIntOption("number");
              JsonIntOption OPPONENT_TEAM_VALUE = new JsonIntOption("opponentTeamValue");
           JsonStringOption PLAYER_ID = new JsonStringOption("playerId");
      JsonStringArrayOption PLAYER_IDS = new JsonStringArrayOption("playerIds");
              JsonIntOption PLAYER_NR = new JsonIntOption("playerNr");
            JsonArrayOption PLAYER_POSITIONS = new JsonArrayOption("playerPositions");
      JsonPlayerStateOption PLAYER_STATE_NEW = new JsonPlayerStateOption("playerStateNew");
      JsonPlayerStateOption PLAYER_STATE_OLD = new JsonPlayerStateOption("playerStateOld");
      JsonStringArrayOption POSITION_IDS = new JsonStringArrayOption("positionIds"); 
          JsonBooleanOption PRO_RE_ROLL_OPTION = new JsonBooleanOption("proReRollOption"); 
           JsonStringOption RACE = new JsonStringOption("race");
          JsonBooleanOption RE_ROLL_INJURY = new JsonBooleanOption("reRollInjury");
              JsonIntOption ROLL = new JsonIntOption("roll");
          JsonBooleanOption SELECTED = new JsonBooleanOption("selected");
     JsonEnumWithNameOption SERIOUS_INJURY_NEW = new JsonEnumWithNameOption("seriousInjuryNew", new SeriousInjuryFactory());
     JsonEnumWithNameOption SERIOUS_INJURY_OLD = new JsonEnumWithNameOption("seriousInjuryOld", new SeriousInjuryFactory());
              JsonIntOption SLOTS = new JsonIntOption("slots");
             JsonDateOption STARTED = new JsonDateOption("started");
  JsonFieldCoordinateOption TARGET = new JsonFieldCoordinateOption("target");
           JsonStringOption TEAM_AWAY_COACH = new JsonStringOption("teamAwayCoach");
           JsonStringOption TEAM_AWAY_ID = new JsonStringOption("teamAwayId");
           JsonStringOption TEAM_AWAY_NAME = new JsonStringOption("teamAwayName");
           JsonStringOption TEAM_HOME_COACH = new JsonStringOption("teamHomeCoach");
           JsonStringOption TEAM_HOME_ID = new JsonStringOption("teamHomeId");
           JsonStringOption TEAM_HOME_NAME = new JsonStringOption("teamHomeName");
           JsonStringOption TEAM_ID = new JsonStringOption("teamId");
            JsonArrayOption TEAM_LIST = new JsonArrayOption("teamList");
           JsonStringOption TEAM_NAME = new JsonStringOption("teamName");
          JsonBooleanOption TEAM_RE_ROLL_OPTION = new JsonBooleanOption("teamReRollOption"); 
     JsonEnumWithNameOption TEAM_STATUS = new JsonEnumWithNameOption("teamStatus", new TeamStatusFactory());
              JsonIntOption TEAM_VALUE = new JsonIntOption("teamValue");
           JsonStringOption THROWER_ID = new JsonStringOption("throwerId");
          JsonBooleanOption THROW_TEAM_MATE = new JsonBooleanOption("throwTeamMate");
  JsonFieldCoordinateOption TOP_LEFT = new JsonFieldCoordinateOption("topLeft");
              JsonIntOption TREASURY = new JsonIntOption("treasury");
              JsonIntOption USES = new JsonIntOption("uses");
              JsonIntOption VALUE = new JsonIntOption("value");
              
}

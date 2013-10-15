package com.balancedbytes.games.ffb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.PlayerMarker;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.RangeRuler;
import com.balancedbytes.games.ffb.TrackNumber;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.WeatherFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class FieldModel implements IByteArraySerializable, IJsonSerializable {
    
  private boolean fBallMoving;
  private boolean fBallInPlay;
  private FieldCoordinate fBallCoordinate;

  private FieldCoordinate fBombCoordinate;
  private boolean fBombMoving;
  
  private Weather fWeather;
  private RangeRuler fRangeRuler;
  private Map<String, FieldCoordinate> fCoordinateByPlayerId;
  private Map<String, PlayerState> fStateByPlayerId;
  private List<BloodSpot> fBloodspots;
  private Set<PushbackSquare> fPushbackSquares;
  private Set<MoveSquare> fMoveSquares;
  private Set<TrackNumber> fTrackNumbers;
  private Set<DiceDecoration> fDiceDecorations;
  private Set<FieldMarker> fFieldMarkers;
  private Set<PlayerMarker> fPlayerMarkers;
  private Map<String, Set<Card>> fCardsByPlayerId;

  private transient Map<FieldCoordinate, String> fPlayerIdByCoordinate;  // no need to serialize this, as it can be reconstructed

  private transient Game fGame;

  public FieldModel(Game pGame) {
    fGame = pGame;
    fPlayerIdByCoordinate = new HashMap<FieldCoordinate, String>();
    fCoordinateByPlayerId = new HashMap<String ,FieldCoordinate>();
    fBloodspots = new ArrayList<BloodSpot>();
    fPushbackSquares = new HashSet<PushbackSquare>();
    fMoveSquares = new HashSet<MoveSquare>();
    fTrackNumbers = new HashSet<TrackNumber>();
    fDiceDecorations = new HashSet<DiceDecoration>();
    fStateByPlayerId = new HashMap<String, PlayerState>();
    fFieldMarkers = new HashSet<FieldMarker>();
    fPlayerMarkers = new HashSet<PlayerMarker>();
    fCardsByPlayerId = new HashMap<String, Set<Card>>();
  }

  public Player getPlayer(FieldCoordinate pPlayerPosition) {
  	String playerId = ((pPlayerPosition != null) ? fPlayerIdByCoordinate.get(pPlayerPosition) : null);
  	return getGame().getPlayerById(playerId);
  }
  
  public void remove(Player pPlayer) {
    if (pPlayer == null) {
    	return;
    }
    FieldCoordinate coordinate = getPlayerCoordinate(pPlayer);
    fPlayerIdByCoordinate.remove(coordinate);
    fCoordinateByPlayerId.remove(pPlayer.getId());
    notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_PLAYER, pPlayer.getId(), coordinate);
  }
  
  public void remove(Team pTeam) {
    if (pTeam != null) {
      for (Player player : pTeam.getPlayers()) {
        remove(player);
        fStateByPlayerId.remove(player);
      }
    }
  }
  
  public FieldCoordinate getPlayerCoordinate(Player pPlayer) {
  	if (pPlayer == null)  {
  		return null;
  	}
    return fCoordinateByPlayerId.get(pPlayer.getId());
  }
  
  public void setPlayerCoordinate(Player pPlayer, FieldCoordinate pCoordinate) {
    if ((pCoordinate == null) || (pPlayer == null)) {
    	return;
    }
    FieldCoordinate oldCoordinate = getPlayerCoordinate(pPlayer);
    if (!FieldCoordinate.equals(pCoordinate, oldCoordinate)) {
      fCoordinateByPlayerId.put(pPlayer.getId(), pCoordinate);
      if (oldCoordinate != null) {
        fPlayerIdByCoordinate.remove(oldCoordinate);
      }
      String oldPlayerId = fPlayerIdByCoordinate.get(pCoordinate);
      if (StringTool.isProvided(oldPlayerId)) {
        fCoordinateByPlayerId.remove(oldPlayerId);
      }
      fPlayerIdByCoordinate.put(pCoordinate, pPlayer.getId());
      notifyObservers(ModelChangeId.FIELD_MODEL_SET_PLAYER_COORDINATE, pPlayer.getId(), pCoordinate);
    }
  }
  
  public FieldCoordinate[] getPlayerCoordinates() {
    return fPlayerIdByCoordinate.keySet().toArray(new FieldCoordinate[fPlayerIdByCoordinate.size()]);
  }
  
  public void setPlayerState(Player pPlayer, PlayerState pState) {
  	if (pPlayer == null) {
  		return;
  	}
    PlayerState oldState = fStateByPlayerId.get(pPlayer.getId());
    if ((oldState == null) || ((pState != null) && (pState.getId() != oldState.getId()))) {
      fStateByPlayerId.put(pPlayer.getId(), pState);
      notifyObservers(ModelChangeId.FIELD_MODEL_SET_PLAYER_STATE, pPlayer.getId(), pState);
    }
  }
  
  public PlayerState getPlayerState(Player pPlayer) {
  	if (pPlayer == null) {
  		return null;
  	}
    PlayerState playerState = fStateByPlayerId.get(pPlayer.getId());
    return (playerState != null) ? playerState : new PlayerState(PlayerState.UNKNOWN);
  }

  public void addCard(Player pPlayer, Card pCard) {
  	if ((pPlayer == null) || (pCard == null)) {
  		return;
  	}
  	Set<Card> cards = fCardsByPlayerId.get(pPlayer.getId());
  	if (cards == null) {
  		cards = new HashSet<Card>();
  		fCardsByPlayerId.put(pPlayer.getId(), cards);
  	}
  	cards.add(pCard);
  	notifyObservers(ModelChangeId.FIELD_MODEL_ADD_CARD, pPlayer.getId(), pCard);
  }
  
  public boolean removeCard(Player pPlayer, Card pCard) {
  	if ((pPlayer == null) || (pCard == null)) {
  		return false;
  	}
  	boolean removed = false;
  	Set<Card> cards = fCardsByPlayerId.get(pPlayer.getId());
  	if (cards != null) {
    	removed = cards.remove(pCard);
  	}
  	if (removed) {
  		notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_CARD, pPlayer.getId(), pCard);
  	}
  	return removed;
  }
  
  public Card[] getCards(Player pPlayer) {
  	if (pPlayer == null) {
  		return null;
  	}
  	Set<Card> cards = fCardsByPlayerId.get(pPlayer.getId());
  	if (cards == null) {
  		return new Card[0];
  	}
  	return cards.toArray(new Card[cards.size()]);
  }
  
  public Player findPlayer(Card pCard) {
  	for (String playerId : fCardsByPlayerId.keySet()) {
  		for (Card card : fCardsByPlayerId.get(playerId)) {
  			if (card == pCard) {
  				return getGame().getPlayerById(playerId);
  			}
  		}
  	}
  	return null;
  }
  
  public FieldCoordinate getBallCoordinate() {
    return fBallCoordinate;
  }
  
  public void setBallCoordinate(FieldCoordinate pBallCoordinate) {
  	if (FieldCoordinate.equals(pBallCoordinate, fBallCoordinate)) {
  		return;
  	}
    fBallCoordinate = pBallCoordinate;
    notifyObservers(ModelChangeId.FIELD_MODEL_SET_BALL_COORDINATE, null, fBallCoordinate);
  }

  public FieldCoordinate getBombCoordinate() {
    return fBombCoordinate;
  }
  
  public void setBombCoordinate(FieldCoordinate pBombCoordinate) {
  	if (FieldCoordinate.equals(pBombCoordinate, fBombCoordinate)) {
  		return;
  	}
  	fBombCoordinate = pBombCoordinate;
  	notifyObservers(ModelChangeId.FIELD_MODEL_SET_BOMB_COORDINATE, null, fBombCoordinate);
  }

  public FieldCoordinate[] findAdjacentCoordinates(FieldCoordinate pCoordinate, FieldCoordinateBounds pBounds, int pSteps, boolean pWithStartCoordinate) {
    List<FieldCoordinate> adjacentCoordinates = new ArrayList<FieldCoordinate>();
    if ((pCoordinate != null) && (pBounds != null)) {
      for (int y = -pSteps; y <= pSteps; y++) {
        for (int x = -pSteps; x <= pSteps; x++) {
          if ((x != 0) || (y != 0) || pWithStartCoordinate) {
            FieldCoordinate adjacentCoordinate = new FieldCoordinate(pCoordinate.getX() + x, pCoordinate.getY() + y);
            if (pBounds.isInBounds(adjacentCoordinate)) {
              adjacentCoordinates.add(adjacentCoordinate);
            }
          }
        }
      }
    }
    return adjacentCoordinates.toArray(new FieldCoordinate[adjacentCoordinates.size()]);
  }

  public void setBallMoving(boolean pBallMoving) {
    if (pBallMoving == fBallMoving) {
    	return;
    }
    fBallMoving = pBallMoving;
    notifyObservers(ModelChangeId.FIELD_MODEL_SET_BALL_MOVING, null, fBallMoving);
  }
  
  public boolean isBallMoving() {
    return fBallMoving;
  }

  public void setBombMoving(boolean pBombMoving) {
    if (pBombMoving == fBombMoving) {
    	return;
    }
    fBombMoving = pBombMoving;
    notifyObservers(ModelChangeId.FIELD_MODEL_SET_BOMB_MOVING, null, fBombMoving);
  }

  public boolean isBombMoving() {
    return fBombMoving;
  }
  
  public void setBallInPlay(boolean pBallInPlay) {
  	if (pBallInPlay == fBallInPlay) {
  		return;
  	}
    fBallInPlay = pBallInPlay;
    notifyObservers(ModelChangeId.FIELD_MODEL_SET_BALL_IN_PLAY, null, fBallInPlay);
  }
  
  public boolean isBallInPlay() {
    return fBallInPlay;
  }
  
  public void add(BloodSpot pBloodspot) {
  	if (pBloodspot == null) {
  		return;
  	}
    fBloodspots.add(pBloodspot);
    notifyObservers(ModelChangeId.FIELD_MODEL_ADD_BLOOD_SPOT, null, pBloodspot);
  }
  
  public BloodSpot[] getBloodSpots() {
    return fBloodspots.toArray(new BloodSpot[fBloodspots.size()]);
  }
  
  public void add(TrackNumber pTrackNumber) {
  	if (pTrackNumber == null) {
  		return;
  	}
    fTrackNumbers.add(pTrackNumber);
    notifyObservers(ModelChangeId.FIELD_MODEL_ADD_TRACK_NUMBER, null, pTrackNumber);
  }

  public boolean remove(TrackNumber pTrackNumber) {
    if (fTrackNumbers.remove(pTrackNumber)) {
      notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_TRACK_NUMBER, null, pTrackNumber);
      return true;
    }
    return false;
  }

  public void clearTrackNumbers() {
    for (TrackNumber trackNumber : getTrackNumbers()) {
      remove(trackNumber);
    }
  }
  
  public TrackNumber[] getTrackNumbers() {
    return fTrackNumbers.toArray(new TrackNumber[fTrackNumbers.size()]);
  }
  
  public TrackNumber getTrackNumber(FieldCoordinate pCoordinate) {
    for (TrackNumber trackNumber : fTrackNumbers) {
      if (trackNumber.getCoordinate().equals(pCoordinate)) {
        return trackNumber;
      }
    }
    return null;
  }
  
  public void add(PushbackSquare[] pPushbackSquares) {
    if (ArrayTool.isProvided(pPushbackSquares)) {
      for (int i = 0; i < pPushbackSquares.length; i++) {
        add(pPushbackSquares[i]);
      }
    }
  }

  public void add(PushbackSquare pPushbackSquare) {
  	if (pPushbackSquare == null) {
  		return;
  	}
    fPushbackSquares.add(pPushbackSquare);
    notifyObservers(ModelChangeId.FIELD_MODEL_ADD_PUSHBACK_SQUARE, null, pPushbackSquare);
  }

  public boolean remove(PushbackSquare pPushbackSquare) {
    if (fPushbackSquares.remove(pPushbackSquare)) {
      notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_PUSHBACK_SQUARE, null, pPushbackSquare);
      return true;
    }
    return false;
  }
  
  public void clearPushbackSquares() {
    for (PushbackSquare pushbackSquare : getPushbackSquares()) {
      remove(pushbackSquare);
    }
  }
  
  public PushbackSquare[] getPushbackSquares() {
    return fPushbackSquares.toArray(new PushbackSquare[fPushbackSquares.size()]);
  }
  
  public void add(MoveSquare pMoveSquare) {
    if (pMoveSquare == null) {
    	return;
    }
    fMoveSquares.add(pMoveSquare);
    notifyObservers(ModelChangeId.FIELD_MODEL_ADD_MOVE_SQUARE, null, pMoveSquare);
  }

  public void add(MoveSquare[] pMoveSquares) {
    if (ArrayTool.isProvided(pMoveSquares)) {
      for (MoveSquare moveSquare : pMoveSquares) {
        add(moveSquare);
      }
    }
  }
  
  public boolean remove(MoveSquare pMoveSquare) {
    if (fMoveSquares.remove(pMoveSquare)) {
      notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_MOVE_SQUARE, null, pMoveSquare);
      return true;
    }
    return false;
  }
  
  public void clearMoveSquares() {
    for (MoveSquare moveSquare : getMoveSquares()) {
      remove(moveSquare);
    }
  }
  
  public MoveSquare[] getMoveSquares() {
    return fMoveSquares.toArray(new MoveSquare[fMoveSquares.size()]);
  }

  public MoveSquare getMoveSquare(FieldCoordinate pCoordinate) {
    for (MoveSquare moveSquare : fMoveSquares) {
      if (moveSquare.getCoordinate().equals(pCoordinate)) {
        return moveSquare;
      }
    }
    return null;
  }
  
  public void add(DiceDecoration pDiceDecoration) {
    if (pDiceDecoration == null) {
    	return;
    }
    fDiceDecorations.add(pDiceDecoration);
    notifyObservers(ModelChangeId.FIELD_MODEL_ADD_DICE_DECORATION, null, pDiceDecoration);
  }

  public boolean remove(DiceDecoration pDiceDecoration) {
    if (fDiceDecorations.remove(pDiceDecoration)) {
      notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_DICE_DECORATION, null, pDiceDecoration);
      return true;
    }
  	return false;
  }

  public void clearDiceDecorations() {
    for (DiceDecoration diceDecoration : getDiceDecorations()) {
      remove(diceDecoration);
    }
  }

  public DiceDecoration[] getDiceDecorations() {
    return fDiceDecorations.toArray(new DiceDecoration[fDiceDecorations.size()]);
  }
  
  public DiceDecoration getDiceDecoration(FieldCoordinate pCoordinate) {
    for (DiceDecoration diceDecoration : fDiceDecorations) {
      if (diceDecoration.getCoordinate().equals(pCoordinate)) {
        return diceDecoration;
      }
    }
    return null;
  }

  public void add(FieldMarker pFieldMarker) {
    if (pFieldMarker == null) {
    	return;
    }
    fFieldMarkers.remove(pFieldMarker);
    fFieldMarkers.add(pFieldMarker);
    notifyObservers(ModelChangeId.FIELD_MODEL_ADD_FIELD_MARKER, null, pFieldMarker);
  }

  public boolean remove(FieldMarker pFieldMarker) {
  	if (fFieldMarkers.remove(pFieldMarker)) {
    	notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_FIELD_MARKER, null, pFieldMarker);
      return true;
  	}
		return false;
  }

  public void clearFieldMarkers() {
    for (FieldMarker fieldMarker : getFieldMarkers()) {
      remove(fieldMarker);
    }
  }

  public FieldMarker[] getFieldMarkers() {
    return fFieldMarkers.toArray(new FieldMarker[fFieldMarkers.size()]);
  }

  public FieldMarker getFieldMarker(FieldCoordinate pCoordinate) {
    for (FieldMarker fieldMarker : fFieldMarkers) {
      if (fieldMarker.getCoordinate().equals(pCoordinate)) {
        return fieldMarker;
      }
    }
    return null;
  }

  public void add(PlayerMarker pPlayerMarker) {
    if (pPlayerMarker == null) {
    	return;
    }
    fPlayerMarkers.remove(pPlayerMarker);
    fPlayerMarkers.add(pPlayerMarker);
    notifyObservers(ModelChangeId.FIELD_MODEL_ADD_PLAYER_MARKER, null, pPlayerMarker);
  }

  public boolean remove(PlayerMarker pPlayerMarker) {
    if (fPlayerMarkers.remove(pPlayerMarker)) {
    	notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_PLAYER_MARKER, null, pPlayerMarker);
      return true;
    }
  	return false;
  }

  public void clearPlayerMarkers() {
    for (PlayerMarker playerMarker : getPlayerMarkers()) {
      remove(playerMarker);
    }
  }

  public PlayerMarker[] getPlayerMarkers() {
    return fPlayerMarkers.toArray(new PlayerMarker[fPlayerMarkers.size()]);
  }

  public PlayerMarker getPlayerMarker(String pPlayerId) {
    for (PlayerMarker playerMarker : fPlayerMarkers) {
      if (playerMarker.getPlayerId().equals(pPlayerId)) {
        return playerMarker;
      }
    }
    return null;
  }

  public Weather getWeather() {
    return fWeather;
  }
  
  public void setWeather(Weather pWeather) {
    if (pWeather == fWeather) {
    	return;
    }
    fWeather = pWeather;
    notifyObservers(ModelChangeId.FIELD_MODEL_SET_WEATHER, null, fWeather);
  }
  
  public RangeRuler getRangeRuler() {
    return fRangeRuler;
  }
  
  public void setRangeRuler(RangeRuler pRangeRuler) {
    if ((pRangeRuler != null) ? pRangeRuler.equals(fRangeRuler) : (fRangeRuler == null)) {
    	return;
    }
    fRangeRuler = pRangeRuler;
    notifyObservers(ModelChangeId.FIELD_MODEL_SET_RANGE_RULER, null, fRangeRuler);
  }
    
  public boolean updatePlayerAndBallPosition(Player pPlayer, FieldCoordinate pCoordinate) {
    boolean ballPositionUpdated = false;
    FieldCoordinate oldPosition = getPlayerCoordinate(pPlayer);
    if (!isBallMoving() && (oldPosition != null) && oldPosition.equals(getBallCoordinate())) {
      setBallCoordinate(pCoordinate);
      ballPositionUpdated = true;
    }
    setPlayerCoordinate(pPlayer, pCoordinate);
    return ballPositionUpdated;
  }

  public void setGame(Game pGame) {
	  fGame = pGame;
  }
  
  public Game getGame() {
    return fGame;
  }
  
  // transformation
  
  public FieldModel transform() {
    
    FieldModel transformedModel = new FieldModel(getGame());
    
    // unmodified values
    
    transformedModel.setBallInPlay(isBallInPlay());
    transformedModel.setBallMoving(isBallMoving());
    transformedModel.setWeather(getWeather());
    
    for (String playerId : fStateByPlayerId.keySet()) {
    	Player player = getGame().getPlayerById(playerId);
      transformedModel.setPlayerState(player, getPlayerState(player));
      for (Card card : getCards(player)) {
      	transformedModel.addCard(player, card);
      }
    }
    
    // transformed values
    
    if (getBallCoordinate() != null) {
      transformedModel.setBallCoordinate(getBallCoordinate().transform());
    }
    
    for (FieldCoordinate playerCoordinate : getPlayerCoordinates()) {
      transformedModel.setPlayerCoordinate(getPlayer(playerCoordinate), playerCoordinate.transform());
    }
    
    for (BloodSpot bloodspot : getBloodSpots()) {
      transformedModel.add(bloodspot.transform());
    }

    for (PushbackSquare pushbackSquare : getPushbackSquares()) {
      transformedModel.add(pushbackSquare.transform());
    }
    
    for (MoveSquare moveSquare : getMoveSquares()) {
      transformedModel.add(moveSquare.transform());
    }
    
    for (TrackNumber trackNumber : getTrackNumbers()) {
      transformedModel.add(trackNumber.transform());
    }
    
    transformedModel.setRangeRuler(RangeRuler.transform(getRangeRuler()));
    
    for (FieldMarker fieldMarker : getFieldMarkers()) {
      transformedModel.add(fieldMarker.transform());
    }
    
    for (PlayerMarker playerMarker : getPlayerMarkers()) {
      transformedModel.add(playerMarker.transform());
    }

    return transformedModel;
    
  }
    
  // change tracking

  private void notifyObservers(ModelChangeId pChangeId, String pKey, Object pValue) {
  	if ((getGame() == null) || (pChangeId == null)) {
  		return;
  	}
  	getGame().notifyObservers(new ModelChange(pChangeId, pKey, pValue));
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
  	return 3;
  }
  
  public void addTo(ByteList pByteList) {

  	pByteList.addSmallInt(getByteArraySerializationVersion());

    pByteList.addByte((byte) getWeather().getId());
    pByteList.addFieldCoordinate(getBallCoordinate());
    pByteList.addBoolean(isBallInPlay());
    pByteList.addBoolean(isBallMoving());
    
    BloodSpot[] bloodspots = getBloodSpots();
    if (ArrayTool.isProvided(bloodspots)) {
      pByteList.addByte((byte) bloodspots.length);
      for (BloodSpot bloodspot : bloodspots) {
        bloodspot.addTo(pByteList);
      }
    } else {
      pByteList.addByte((byte) 0);
    }

    PushbackSquare[] pushbackSquares = getPushbackSquares();
    if (ArrayTool.isProvided(pushbackSquares)) {
      pByteList.addByte((byte) pushbackSquares.length);
      for (PushbackSquare pushbackSquare : pushbackSquares) {
        pushbackSquare.addTo(pByteList);
      }
    } else {
      pByteList.addByte((byte) 0);
    }
    
    MoveSquare[] moveSquares = getMoveSquares();
    if (ArrayTool.isProvided(moveSquares)) {
      pByteList.addByte((byte) moveSquares.length);
      for (MoveSquare moveSquare : moveSquares) {
        moveSquare.addTo(pByteList);
      }
    } else {
      pByteList.addByte((byte) 0);
    }
    
    TrackNumber[] trackNumbers = getTrackNumbers();
    if (ArrayTool.isProvided(trackNumbers)) {
      pByteList.addByte((byte) trackNumbers.length);
      for (TrackNumber trackNumber : trackNumbers) {
        trackNumber.addTo(pByteList);
      }
    } else {
      pByteList.addByte((byte) 0);
    }
    
    DiceDecoration[] diceDecorations = getDiceDecorations();
    if (ArrayTool.isProvided(diceDecorations)) {
      pByteList.addByte((byte) diceDecorations.length);
      for (DiceDecoration diceDecoration : diceDecorations) {
        diceDecoration.addTo(pByteList);
      }
    } else {
      pByteList.addByte((byte) 0);
    }

    FieldMarker[] fieldMarkers = getFieldMarkers();
    if (ArrayTool.isProvided(fieldMarkers)) {
      pByteList.addByte((byte) fieldMarkers.length);
      for (FieldMarker fieldMarker : fieldMarkers) {
        fieldMarker.addTo(pByteList);
      }
    } else {
      pByteList.addByte((byte) 0);
    }
    
    PlayerMarker[] playerMarkers = getPlayerMarkers();
    if (ArrayTool.isProvided(playerMarkers)) {
      pByteList.addByte((byte) playerMarkers.length);
      for (PlayerMarker playerMarker : playerMarkers) {
        playerMarker.addTo(pByteList);
      }
    } else {
      pByteList.addByte((byte) 0);
    }

    Player[] players = getGame().getPlayers();
    pByteList.addByte((byte) players.length);
    
    for (int i = 0; i < players.length; i++) {
      
    	pByteList.addString(players[i].getId());
      pByteList.addFieldCoordinate(getPlayerCoordinate(players[i]));
      PlayerState playerState = getPlayerState(players[i]);
      pByteList.addSmallInt(((playerState != null) ? playerState.getId() : 0));
      
      Card[] cards = getCards(players[i]);
      pByteList.addByte((byte) cards.length);
      for (Card card : cards) {
      	pByteList.addSmallInt(card.getId());
      }
      
    }
    
    pByteList.addFieldCoordinate(getBombCoordinate());
    pByteList.addBoolean(isBombMoving());
    
  }
  
  
  public int initFrom(ByteArray pByteArray) {
    
  	int byteArraySerializationVersion = pByteArray.getSmallInt();
  	
    deprecatedInitFrom(pByteArray, false);
    
    int nrOfPlayers = pByteArray.getByte();
    for (int i = 0; i < nrOfPlayers; i++) {
    	
    	Player player = getGame().getPlayerById(pByteArray.getString());
      setPlayerCoordinate(player, pByteArray.getFieldCoordinate());
      setPlayerState(player, new PlayerState(pByteArray.getSmallInt()));
      
      if (byteArraySerializationVersion > 2) {
      	int nrOfCards = pByteArray.getByte();
      	CardFactory cardFactory = new CardFactory();
      	for (int j = 0; j < nrOfCards; j++) {
      		addCard(player, cardFactory.forId(pByteArray.getSmallInt()));
      	}
      }
      
    }

    if (byteArraySerializationVersion > 1) {
    	setBombCoordinate(pByteArray.getFieldCoordinate());
    	setBombMoving(pByteArray.getBoolean());
    }

    return byteArraySerializationVersion;
    
  }
  
  // bad hack to cover up missing byteArraySerialization
  public void deprecatedInitFrom(ByteArray pByteArray, boolean pWithPlayerStates) {

    setWeather(new WeatherFactory().forId(pByteArray.getByte()));
    setBallCoordinate(pByteArray.getFieldCoordinate());
    setBallInPlay(pByteArray.getBoolean());
    setBallMoving(pByteArray.getBoolean());

    int nrOfBloodspots = pByteArray.getByte();
    for (int i = 0; i < nrOfBloodspots; i++) {
      BloodSpot bloodspot = new BloodSpot();
      bloodspot.initFrom(pByteArray);
      add(bloodspot);
    }
    
    int nrOfPushbackSquares = pByteArray.getByte();
    for (int i = 0; i < nrOfPushbackSquares; i++) {
      PushbackSquare pushbackSquare = new PushbackSquare();
      pushbackSquare.initFrom(pByteArray);
      add(pushbackSquare);
    }

    int nrOfMoveSquares = pByteArray.getByte();
    for (int i = 0; i < nrOfMoveSquares; i++) {
      MoveSquare moveSquare = new MoveSquare();
      moveSquare.initFrom(pByteArray);
      add(moveSquare);
    }

    int nrOfTrackNumbers = pByteArray.getByte();
    for (int i = 0; i < nrOfTrackNumbers; i++) {
      TrackNumber trackNumber = new TrackNumber();
      trackNumber.initFrom(pByteArray);
      add(trackNumber);
    }
    
    int nrOfDiceDecorations = pByteArray.getByte();
    for (int i = 0; i < nrOfDiceDecorations; i++) {
      DiceDecoration diceDecoration = new DiceDecoration();
      diceDecoration.initFrom(pByteArray);
      add(diceDecoration);
    }

    int nrOfFieldMarkers = pByteArray.getByte();
    for (int i = 0; i < nrOfFieldMarkers; i++) {
      FieldMarker fieldMarker = new FieldMarker();
      fieldMarker.initFrom(pByteArray);
      add(fieldMarker);
    }

    int nrOfPlayerMarkers = pByteArray.getByte();
    for (int i = 0; i < nrOfPlayerMarkers; i++) {
      PlayerMarker playerMarker = new PlayerMarker();
      playerMarker.initFrom(pByteArray);
      add(playerMarker);
    }

    if (pWithPlayerStates) {
      int nrOfPlayers = pByteArray.getByte();
      for (int i = 0; i < nrOfPlayers; i++) {
      	Player player = getGame().getPlayerById(pByteArray.getString());
        setPlayerCoordinate(player, pByteArray.getFieldCoordinate());
        setPlayerState(player, new PlayerState(pByteArray.getSmallInt()));
      }
    }
  
  }
  
}

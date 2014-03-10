package com.balancedbytes.games.ffb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.CardEffectFactory;
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
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class FieldModel implements IByteArrayReadable, IJsonSerializable {
    
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
  private Map<String, Set<CardEffect>> fCardEffectsByPlayerId;

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
    fCardEffectsByPlayerId = new HashMap<String, Set<CardEffect>>();
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

  public void addCardEffect(Player pPlayer, CardEffect pCardEffect) {
    if ((pPlayer == null) || (pCardEffect == null)) {
      return;
    }
    Set<CardEffect> cardEffects = fCardEffectsByPlayerId.get(pPlayer.getId());
    if (cardEffects == null) {
      cardEffects = new HashSet<CardEffect>();
      fCardEffectsByPlayerId.put(pPlayer.getId(), cardEffects);
    }
    cardEffects.add(pCardEffect);
    notifyObservers(ModelChangeId.FIELD_MODEL_ADD_CARD_EFFECT, pPlayer.getId(), pCardEffect);
  }
  
  public boolean removeCardEffect(Player pPlayer, CardEffect pCardEffect) {
    if ((pPlayer == null) || (pCardEffect == null)) {
      return false;
    }
    boolean removed = false;
    Set<CardEffect> cardEffects = fCardEffectsByPlayerId.get(pPlayer.getId());
    if (cardEffects != null) {
      removed = cardEffects.remove(pCardEffect);
    }
    if (removed) {
      notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_CARD_EFFECT, pPlayer.getId(), pCardEffect);
    }
    return removed;
  }
  
  public CardEffect[] getCardEffects(Player pPlayer) {
    if (pPlayer == null) {
      return null;
    }
    Set<CardEffect> cardEffects = fCardEffectsByPlayerId.get(pPlayer.getId());
    if (cardEffects == null) {
      return new CardEffect[0];
    }
    return cardEffects.toArray(new CardEffect[cardEffects.size()]);
  }
  
  public boolean hasCardEffect(Player pPlayer, CardEffect pCardEffect) {
    if ((pPlayer == null) || (pCardEffect == null)) {
      return false;
    }
    Set<CardEffect> cardEffects = fCardEffectsByPlayerId.get(pPlayer.getId());
    if (cardEffects == null) {
      return false;
    }
    return cardEffects.contains(pCardEffect);
  }
  
  public Player[] findPlayers(CardEffect pCardEffect) {
    Set<Player> players = new HashSet<Player>();
    for (String playerId : fCardEffectsByPlayerId.keySet()) {
      for (CardEffect cardEffect : fCardEffectsByPlayerId.get(playerId)) {
        if (cardEffect == pCardEffect) {
          players.add(getGame().getPlayerById(playerId));
          break;
        }
      }
    }
    return players.toArray(new Player[players.size()]);
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
  
  // JSON serialization
  
  public JsonObject toJsonValue() {

    JsonObject jsonObject = new JsonObject();
    
    IJsonOption.WEATHER.addTo(jsonObject, fWeather);
    IJsonOption.BALL_COORDINATE.addTo(jsonObject, fBallCoordinate);
    IJsonOption.BALL_IN_PLAY.addTo(jsonObject, fBallInPlay);
    IJsonOption.BALL_MOVING.addTo(jsonObject, fBallMoving);
    IJsonOption.BOMB_COORDINATE.addTo(jsonObject, fBombCoordinate);
    IJsonOption.BOMB_MOVING.addTo(jsonObject, fBombMoving);

    JsonArray bloodspotArray = new JsonArray();
    for (BloodSpot bloodSpot : fBloodspots) {
      bloodspotArray.add(bloodSpot.toJsonValue());
    }
    IJsonOption.BLOODSPOT_ARRAY.addTo(jsonObject, bloodspotArray);
    
    JsonArray pushbackSquareArray = new JsonArray();
    for (PushbackSquare pushbackSquare : fPushbackSquares) {
      pushbackSquareArray.add(pushbackSquare.toJsonValue());
    }
    IJsonOption.PUSHBACK_SQUARE_ARRAY.addTo(jsonObject, pushbackSquareArray);
    
    JsonArray moveSquareArray = new JsonArray();
    for (MoveSquare moveSquare : fMoveSquares) {
      moveSquareArray.add(moveSquare.toJsonValue());
    }
    IJsonOption.MOVE_SQUARE_ARRAY.addTo(jsonObject, moveSquareArray);

    JsonArray trackNumberArray = new JsonArray();
    for (TrackNumber trackNumber : fTrackNumbers) {
      trackNumberArray.add(trackNumber.toJsonValue());
    }
    IJsonOption.TRACK_NUMBER_ARRAY.addTo(jsonObject, trackNumberArray);

    JsonArray diceDecorationArray = new JsonArray();
    for (DiceDecoration diceDecoration : fDiceDecorations) {
      diceDecorationArray.add(diceDecoration.toJsonValue());
    }
    IJsonOption.DICE_DECORATION_ARRAY.addTo(jsonObject, diceDecorationArray);

    JsonArray fieldMarkerArray = new JsonArray();
    for (FieldMarker fieldMarker : fFieldMarkers) {
      fieldMarkerArray.add(fieldMarker.toJsonValue());
    }
    IJsonOption.FIELD_MARKER_ARRAY.addTo(jsonObject, fieldMarkerArray);

    JsonArray playerMarkerArray = new JsonArray();
    for (PlayerMarker playerMarker : fPlayerMarkers) {
      playerMarkerArray.add(playerMarker.toJsonValue());
    }
    IJsonOption.PLAYER_MARKER_ARRAY.addTo(jsonObject, playerMarkerArray);
    
    JsonArray playerDataArray = new JsonArray();
    for (Player player : getGame().getPlayers()) {
      
      JsonObject playerDataObject = new JsonObject();
      IJsonOption.PLAYER_ID.addTo(playerDataObject, player.getId());
      IJsonOption.PLAYER_COORDINATE.addTo(playerDataObject, getPlayerCoordinate(player));
      IJsonOption.PLAYER_STATE.addTo(playerDataObject,getPlayerState(player));
      
      List<String> cards = new ArrayList<String>();
      for (Card card : getCards(player)) {
        cards.add(card.getName());
      }
      IJsonOption.CARDS.addTo(jsonObject, cards);

      List<String> cardEffects = new ArrayList<String>();
      for (CardEffect cardEffect : getCardEffects(player)) {
        cardEffects.add(cardEffect.getName());
      }
      IJsonOption.CARD_EFFECTS.addTo(jsonObject, cardEffects);

      playerDataArray.add(playerDataObject);
      
    }
    IJsonOption.PLAYER_DATA_ARRAY.addTo(jsonObject, playerDataArray);
    
    return jsonObject;
    
  }
  
  public FieldModel initFrom(JsonValue pJsonValue) {

    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    
    fWeather = (Weather) IJsonOption.WEATHER.getFrom(jsonObject);
    fBallCoordinate = IJsonOption.BALL_COORDINATE.getFrom(jsonObject);
    fBallInPlay = IJsonOption.BALL_IN_PLAY.getFrom(jsonObject);
    fBallMoving = IJsonOption.BALL_MOVING.getFrom(jsonObject);
    fBombCoordinate = IJsonOption.BOMB_COORDINATE.getFrom(jsonObject);
    fBombMoving = IJsonOption.BOMB_MOVING.getFrom(jsonObject);

    fBloodspots.clear();
    JsonArray bloodspotArray = IJsonOption.BLOODSPOT_ARRAY.getFrom(jsonObject);
    for (int i = 0; i < bloodspotArray.size(); i++) {
      fBloodspots.add(new BloodSpot().initFrom(bloodspotArray.get(i)));
    }

    fPushbackSquares.clear();
    JsonArray pushbackSquareArray = IJsonOption.PUSHBACK_SQUARE_ARRAY.getFrom(jsonObject);
    for (int i = 0; i < pushbackSquareArray.size(); i++) {
      fPushbackSquares.add(new PushbackSquare().initFrom(pushbackSquareArray.get(i)));
    }
    
    fMoveSquares.clear();
    JsonArray moveSquareArray = IJsonOption.MOVE_SQUARE_ARRAY.getFrom(jsonObject);
    for (int i = 0; i < moveSquareArray.size(); i++) {
      fMoveSquares.add(new MoveSquare().initFrom(moveSquareArray.get(i)));
    }

    fTrackNumbers.clear();
    JsonArray trackNumberArray = IJsonOption.TRACK_NUMBER_ARRAY.getFrom(jsonObject);
    for (int i = 0; i < trackNumberArray.size(); i++) {
      fTrackNumbers.add(new TrackNumber().initFrom(trackNumberArray.get(i)));
    }

    fDiceDecorations.clear();
    JsonArray diceDecorationArray = IJsonOption.DICE_DECORATION_ARRAY.getFrom(jsonObject);
    for (int i = 0; i < diceDecorationArray.size(); i++) {
      fDiceDecorations.add(new DiceDecoration().initFrom(diceDecorationArray.get(i)));
    }
    
    fFieldMarkers.clear();
    JsonArray fieldMarkerArray = IJsonOption.FIELD_MARKER_ARRAY.getFrom(jsonObject);
    for (int i = 0; i < fieldMarkerArray.size(); i++) {
      fFieldMarkers.add(new FieldMarker().initFrom(fieldMarkerArray.get(i)));
    }
    
    fPlayerMarkers.clear();
    JsonArray playerMarkerArray = IJsonOption.PLAYER_MARKER_ARRAY.getFrom(jsonObject);
    for (int i = 0; i < playerMarkerArray.size(); i++) {
      fPlayerMarkers.add(new PlayerMarker().initFrom(playerMarkerArray.get(i)));
    }

    fPlayerIdByCoordinate.clear();
    fCoordinateByPlayerId.clear();
    fStateByPlayerId.clear();
    fCardsByPlayerId.clear();
    
    CardFactory cardFactory = new CardFactory();
    CardEffectFactory cardEffectFactory = new CardEffectFactory();
    
    JsonArray playerDataArray = IJsonOption.PLAYER_DATA_ARRAY.getFrom(jsonObject);
    for (int i = 0; i < playerDataArray.size(); i++) {
      
      JsonObject playerDataObject = UtilJson.toJsonObject(playerDataArray.get(i));
      
      String playerId = IJsonOption.PLAYER_ID.getFrom(playerDataObject);
      Player player = getGame().getPlayerById(playerId);
      
      FieldCoordinate playerCoordinate = IJsonOption.PLAYER_COORDINATE.getFrom(playerDataObject);
      setPlayerCoordinate(player, playerCoordinate);
      
      PlayerState playerState = IJsonOption.PLAYER_STATE.getFrom(playerDataObject);
      setPlayerState(player, playerState);
      
      String[] cards = IJsonOption.CARDS.getFrom(playerDataObject);
      if (ArrayTool.isProvided(cards)) {
        for (int j = 0; j < cards.length; j++) {
          addCard(player, cardFactory.forName(cards[j]));
        }
      }

      String[] cardEffects = IJsonOption.CARDS.getFrom(playerDataObject);
      if (ArrayTool.isProvided(cardEffects)) {
        for (int j = 0; j < cardEffects.length; j++) {
          addCardEffect(player, cardEffectFactory.forName(cardEffects[j]));
        }
      }

    }

    return this;
    
  }
  
}

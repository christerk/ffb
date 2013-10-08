package com.balancedbytes.games.ffb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.FieldModelChangeEvent;
import com.balancedbytes.games.ffb.IFieldModelChangeListener;
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
import com.balancedbytes.games.ffb.model.change.old.CommandFieldModelChange;
import com.balancedbytes.games.ffb.model.change.old.ModelChangeFieldModel;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class FieldModel implements IByteArraySerializable, IXmlWriteable {
  
  public static final String XML_TAG = "fieldModel";
  
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_MOVING = "moving";
  private static final String _XML_ATTRIBUTE_IN_PLAY = "inPlay";
  private static final String _XML_ATTRIBUTE_ID = "id";
  private static final String _XML_ATTRIBUTE_STATE = "state";
  private static final String _XML_ATTRIBUTE_NAME = "name";

  private static final String _XML_TAG_BALL = "ball";
  private static final String _XML_TAG_BOMB = "bomb";
  private static final String _XML_TAG_COORDINATE = "coordinate";
  private static final String _XML_TAG_WEATHER = "weather";
  private static final String _XML_TAG_BLOODSPOT_LIST = "bloodspotList";
  private static final String _XML_TAG_PUSHBACK_SQUARE_LIST = "pushbackSquareList";
  private static final String _XML_TAG_MOVE_SQUARE_LIST = "moveSquareList";
  private static final String _XML_TAG_TRACK_NUMBER_LIST = "trackNumberList";
  private static final String _XML_TAG_DICE_DECORATION_LIST = "diceDecorationList";
  private static final String _XML_TAG_PLAYER_LIST = "playerList";
  private static final String _XML_TAG_PLAYER = "player";
  private static final String _XML_TAG_FIELD_MARKER_LIST = "fieldMarkerList";
  private static final String _XML_TAG_PLAYER_MARKER_LIST = "playerMarkerList";
  private static final String _XML_TAG_CARD = "card";
  
  private boolean fBallMoving;
  private boolean fBallInPlay;
  private FieldCoordinate fBallCoordinate;

  private FieldCoordinate fBombCoordinate;
  private boolean fBombMoving;
  
  private transient Game fGame;
  
  private Weather fWeather;
  
  private RangeRuler fRangeRuler;
  
  private Map<FieldCoordinate, Player> fPlayerByCoordinate;
  
  private Map<Player, FieldCoordinate> fCoordinateByPlayer;
  
  private Map<Player, PlayerState> fStateByPlayer;
  
  private List<BloodSpot> fBloodspots;
  
  private Set<PushbackSquare> fPushbackSquares;
  
  private Set<MoveSquare> fMoveSquares;
  
  private Set<TrackNumber> fTrackNumbers;
  
  private Set<DiceDecoration> fDiceDecorations;
  
  private Set<FieldMarker> fFieldMarkers;
  
  private Set<PlayerMarker> fPlayerMarkers;
  
  private transient Set<IFieldModelChangeListener> fChangeListeners;

  private Map<Player, Set<Card>> fCardsByPlayer;

  public FieldModel(Game pGame) {
    fGame = pGame;
    fPlayerByCoordinate = new HashMap<FieldCoordinate,Player>();
    fCoordinateByPlayer = new HashMap<Player,FieldCoordinate>();
    fBloodspots = new ArrayList<BloodSpot>();
    fPushbackSquares = new HashSet<PushbackSquare>();
    fMoveSquares = new HashSet<MoveSquare>();
    fTrackNumbers = new HashSet<TrackNumber>();
    fDiceDecorations = new HashSet<DiceDecoration>();
    fChangeListeners = new HashSet<IFieldModelChangeListener>();
    fStateByPlayer = new HashMap<Player, PlayerState>();
    fFieldMarkers = new HashSet<FieldMarker>();
    fPlayerMarkers = new HashSet<PlayerMarker>();
    fCardsByPlayer = new HashMap<Player, Set<Card>>();
  }

  public Player getPlayer(FieldCoordinate pPlayerPosition) {
    return ((pPlayerPosition != null) ? fPlayerByCoordinate.get(pPlayerPosition) : null);
  }
  
  public void remove(Player pPlayer) {
    if (pPlayer != null) {
      FieldCoordinate coordinate = getPlayerCoordinate(pPlayer);
      fPlayerByCoordinate.remove(coordinate);
      fCoordinateByPlayer.remove(pPlayer);
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_PLAYER_POSITION, pPlayer, coordinate, null));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.REMOVE_PLAYER, pPlayer.getId()));
      }
    }
  }
  
  public void remove(Team pTeam) {
    if (pTeam != null) {
      for (Player player : pTeam.getPlayers()) {
        remove(player);
        fStateByPlayer.remove(player);
      }
    }
  }
  
  public FieldCoordinate getPlayerCoordinate(Player pPlayer) {
    return fCoordinateByPlayer.get(pPlayer);
  }
  
  public void setPlayerCoordinate(Player pPlayer, FieldCoordinate pCoordinate) {
    if (pCoordinate != null) {
      FieldCoordinate oldCoordinate = getPlayerCoordinate(pPlayer);
      fCoordinateByPlayer.put(pPlayer, pCoordinate);
      if (oldCoordinate != null) {
        fPlayerByCoordinate.remove(oldCoordinate);
      }
      Player oldPlayer = fPlayerByCoordinate.get(pCoordinate);
      if (oldPlayer != null) {
        fCoordinateByPlayer.remove(oldPlayer);
      }
      fPlayerByCoordinate.put(pCoordinate, pPlayer);
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_PLAYER_POSITION, pPlayer, oldCoordinate, pCoordinate));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.SET_PLAYER_COOORDINATE, pPlayer.getId(), pCoordinate));
      }
    }
  }
  
  public FieldCoordinate[] getPlayerCoordinates() {
    return fPlayerByCoordinate.keySet().toArray(new FieldCoordinate[fPlayerByCoordinate.size()]);
  }
  
  public void setPlayerState(Player pPlayer, PlayerState pState) {
    PlayerState oldState = fStateByPlayer.get(pPlayer);
    if ((oldState == null) || ((pState != null) && (pState.getId() != oldState.getId()))) {
      fStateByPlayer.put(pPlayer, pState);
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_PLAYER_STATE, pPlayer, oldState, pState));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.SET_PLAYER_STATE, pPlayer.getId(), pState));
      }
    }
  }
  
  public PlayerState getPlayerState(Player pPlayer) {
    PlayerState playerState = fStateByPlayer.get(pPlayer);
    return (playerState != null) ? playerState : new PlayerState(PlayerState.UNKNOWN);
  }

  public void addCard(Player pPlayer, Card pCard) {
  	if ((pPlayer == null) || (pCard == null)) {
  		return;
  	}
  	Set<Card> cards = fCardsByPlayer.get(pPlayer);
  	if (cards == null) {
  		cards = new HashSet<Card>();
  		fCardsByPlayer.put(pPlayer, cards);
  	}
  	cards.add(pCard);
    if (getGame().isTrackingChanges()) {
      getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.ADD_CARD, pPlayer.getId(), pCard));
    }
  }
  
  public boolean removeCard(Player pPlayer, Card pCard) {
  	if ((pPlayer == null) || (pCard == null)) {
  		return false;
  	}
    if (getGame().isTrackingChanges()) {
      getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.REMOVE_CARD, pPlayer.getId(), pCard));
    }
  	Set<Card> cards = fCardsByPlayer.get(pPlayer);
  	if (cards != null) {
    	return cards.remove(pCard);
  	} else {
  		return false;
  	}
  }
  
  public Card[] getCards(Player pPlayer) {
  	Set<Card> cards = fCardsByPlayer.get(pPlayer);
  	if (cards == null) {
  		return new Card[0];
  	}
  	return cards.toArray(new Card[cards.size()]);
  }
  
  public Player findPlayer(Card pCard) {
  	for (Player player : fCardsByPlayer.keySet()) {
  		for (Card card : fCardsByPlayer.get(player)) {
  			if (card == pCard) {
  				return player;
  			}
  		}
  	}
  	return null;
  }
  
  public FieldCoordinate getBallCoordinate() {
    return fBallCoordinate;
  }
  
  public void setBallCoordinate(FieldCoordinate pCoordinate) {
    // System.out.println("Ball at " + pCoordinate);
    if (fChangeListeners.size() > 0) {
      FieldModelChangeEvent changeEvent = new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_BALL_COORDINATE, this, getBallCoordinate(), pCoordinate);
      fBallCoordinate = pCoordinate;
      fireChangeEvent(changeEvent);
    } else {
      fBallCoordinate = pCoordinate;
    }
    if (getGame().isTrackingChanges()) {
      getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.SET_BALL_COORDINATE, pCoordinate));
    }
  }

  public FieldCoordinate getBombCoordinate() {
    return fBombCoordinate;
  }
  
  public void setBombCoordinate(FieldCoordinate pCoordinate) {
    // System.out.println("Ball at " + pCoordinate);
    if (fChangeListeners.size() > 0) {
      FieldModelChangeEvent changeEvent = new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_BOMB_COORDINATE, this, getBombCoordinate(), pCoordinate);
      fBombCoordinate = pCoordinate;
      fireChangeEvent(changeEvent);
    } else {
      fBombCoordinate = pCoordinate;
    }
    if (getGame().isTrackingChanges()) {
      getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.SET_BOMB_COORDINATE, pCoordinate));
    }
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
    if (pBallMoving != isBallMoving()) {
      fBallMoving = pBallMoving;
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_BALL_MOVING, this, new Boolean(!pBallMoving), new Boolean(pBallMoving)));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.SET_BALL_MOVING, pBallMoving));
      }
    }
  }
  
  public boolean isBallMoving() {
    return fBallMoving;
  }

  public void setBombMoving(boolean pBombMoving) {
    if (pBombMoving != isBombMoving()) {
      fBombMoving = pBombMoving;
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_BOMB_MOVING, this, new Boolean(!pBombMoving), new Boolean(pBombMoving)));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.SET_BOMB_MOVING, pBombMoving));
      }
    }
  }

  public boolean isBombMoving() {
    return fBombMoving;
  }
  
  public void setBallInPlay(boolean pBallInPlay) {
    if (getGame().isTrackingChanges() && (pBallInPlay != fBallInPlay)) {
      getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.SET_BALL_IN_PLAY, pBallInPlay));
    }
    fBallInPlay = pBallInPlay;
  }
  
  public boolean isBallInPlay() {
    return fBallInPlay;
  }
  
  public void add(BloodSpot pBloodspot) {
    fBloodspots.add(pBloodspot);
    if (fChangeListeners.size() > 0) {
      fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_BLOODSPOT, this, null, pBloodspot));
    }
    if (getGame().isTrackingChanges()) {
      getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.ADD_BLOOD_SPOT, pBloodspot));
    }
  }
  
  public BloodSpot[] getBloodSpots() {
    return fBloodspots.toArray(new BloodSpot[fBloodspots.size()]);
  }
  
  public void add(TrackNumber pTrackNumber) {
    fTrackNumbers.add(pTrackNumber);
    if (fChangeListeners.size() > 0) {
      fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_TRACK_NUMBER, this, null, pTrackNumber));
    }
    if (getGame().isTrackingChanges()) {
      getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.ADD_TRACK_NUMBER, pTrackNumber));
    }
  }

  public boolean remove(TrackNumber pTrackNumber) {
    boolean removed = fTrackNumbers.remove(pTrackNumber);
    if (removed) {
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_TRACK_NUMBER, this, pTrackNumber, null));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.REMOVE_TRACK_NUMBER, pTrackNumber));
      }
    }
    return removed;
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
    fPushbackSquares.add(pPushbackSquare);
    if (fChangeListeners.size() > 0) {
      fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_PUSHBACK_SQUARE, this, null, pPushbackSquare));
    }
    if (getGame().isTrackingChanges()) {
      getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.ADD_PUSHBACK_SQUARE, pPushbackSquare));
    }
  }

  public boolean remove(PushbackSquare pPushbackSquare) {
    boolean removed = fPushbackSquares.remove(pPushbackSquare);
    if (removed) {
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_PUSHBACK_SQUARE, this, pPushbackSquare, null));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.REMOVE_PUSHBACK_SQUARE, pPushbackSquare));
      }
    }
    return removed;
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
    if (pMoveSquare != null) {
      fMoveSquares.add(pMoveSquare);
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_MOVE_SQUARE, this, null, pMoveSquare));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.ADD_MOVE_SQUARE, pMoveSquare));
      }
    }
  }

  public void add(MoveSquare[] pMoveSquares) {
    if (ArrayTool.isProvided(pMoveSquares)) {
      for (MoveSquare moveSquare : pMoveSquares) {
        add(moveSquare);
      }
    }
  }
  
  public boolean remove(MoveSquare pMoveSquare) {
    boolean removed = fMoveSquares.remove(pMoveSquare);
    if (removed) {
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_MOVE_SQUARE, this, pMoveSquare, null));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.REMOVE_MOVE_SQUARE, pMoveSquare));
      }
    }
    return removed;
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
    if (pDiceDecoration != null) {
      fDiceDecorations.add(pDiceDecoration);
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_DICE_DECORATION, this, null, pDiceDecoration));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.ADD_DICE_DECORATION, pDiceDecoration));
      }
    }
  }

  public boolean remove(DiceDecoration pDiceDecoration) {
    boolean removed = fDiceDecorations.remove(pDiceDecoration);
    if (removed) {
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_DICE_DECORATION, this, pDiceDecoration, null));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.REMOVE_DICE_DECORATION, pDiceDecoration));
      }
    }
    return removed;
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
    if (pFieldMarker != null) {
      fFieldMarkers.remove(pFieldMarker);
      fFieldMarkers.add(pFieldMarker);
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_FIELD_MARKER, this, null, pFieldMarker));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.ADD_FIELD_MARKER, pFieldMarker));
      }
    }
  }

  public boolean remove(FieldMarker pFieldMarker) {
    boolean removed = fFieldMarkers.remove(pFieldMarker);
    if (removed) {
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_FIELD_MARKER, this, pFieldMarker, null));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.REMOVE_FIELD_MARKER, pFieldMarker));
      }
    }
    return removed;
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
    if (pPlayerMarker != null) {
      fPlayerMarkers.remove(pPlayerMarker);
      fPlayerMarkers.add(pPlayerMarker);
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_PLAYER_MARKER, this, null, pPlayerMarker));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.ADD_PLAYER_MARKER, pPlayerMarker));
      }
    }
  }

  public boolean remove(PlayerMarker pPlayerMarker) {
    boolean removed = fPlayerMarkers.remove(pPlayerMarker);
    if (removed) {
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_PLAYER_MARKER, this, pPlayerMarker, null));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.REMOVE_PLAYER_MARKER, pPlayerMarker));
      }
    }
    return removed;
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
    Weather oldWeather = getWeather();
    if ((pWeather != null) && (pWeather != oldWeather)) {
      fWeather = pWeather;
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_WEATHER, this, oldWeather, getWeather()));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.SET_WEATHER, pWeather));
      }
    }
  }
  
  public RangeRuler getRangeRuler() {
    return fRangeRuler;
  }
  
  public void setRangeRuler(RangeRuler pRangeRuler) {
    boolean changed = (pRangeRuler != null) ? !pRangeRuler.equals(fRangeRuler) : (fRangeRuler != null);
    if (changed) {
      RangeRuler oldRangeRuler = fRangeRuler;
      fRangeRuler = pRangeRuler;
      if (fChangeListeners.size() > 0) {
        fireChangeEvent(new FieldModelChangeEvent(this, FieldModelChangeEvent.TYPE_RANGE_RULER, this, oldRangeRuler, pRangeRuler));
      }
      if (getGame().isTrackingChanges()) {
        getGame().add(new ModelChangeFieldModel(CommandFieldModelChange.SET_RANGE_RULER, pRangeRuler));
      }
    }
  }
  
  public IFieldModelChangeListener[] getChangeListeners() {
    return (IFieldModelChangeListener[]) fChangeListeners.toArray(new IFieldModelChangeListener[fChangeListeners.size()]);
  }
  
  public void addListener(IFieldModelChangeListener pChangeListener) {
    fChangeListeners.add(pChangeListener);
  }
  
  public void removeListener(IFieldModelChangeListener pChangeListener) {
    fChangeListeners.remove(pChangeListener);
  }
  
  private void fireChangeEvent(FieldModelChangeEvent pChangeEvent) {
    Iterator<IFieldModelChangeListener> listenerIterator = fChangeListeners.iterator();
    while (listenerIterator.hasNext()) {
      listenerIterator.next().fieldModelChanged(pChangeEvent);
    }
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
    
    for (IFieldModelChangeListener changeListener : getChangeListeners()) {
      transformedModel.addListener(changeListener);
    }
    
    for (Player player : fStateByPlayer.keySet()) {
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
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
   
    UtilXml.startElement(pHandler, XML_TAG);
    
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_IN_PLAY, isBallInPlay());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MOVING, isBallMoving());
    UtilXml.startElement(pHandler, _XML_TAG_BALL, attributes);
    if (getBallCoordinate() != null) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getBallCoordinate().getX());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getBallCoordinate().getY());
      UtilXml.addEmptyElement(pHandler, _XML_TAG_COORDINATE, attributes);
    }
    UtilXml.endElement(pHandler, _XML_TAG_BALL);

    attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MOVING, isBombMoving());
    UtilXml.startElement(pHandler, _XML_TAG_BOMB, attributes);
    if (getBombCoordinate() != null) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getBombCoordinate().getX());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getBombCoordinate().getY());
      UtilXml.addEmptyElement(pHandler, _XML_TAG_COORDINATE, attributes);
    }
    UtilXml.endElement(pHandler, _XML_TAG_BOMB);

    UtilXml.addValueElement(pHandler, _XML_TAG_WEATHER, (getWeather() != null) ? getWeather().getName() : null);

    UtilXml.startElement(pHandler, _XML_TAG_BLOODSPOT_LIST);
    for (BloodSpot bloodspot : getBloodSpots()) {
      bloodspot.addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, _XML_TAG_BLOODSPOT_LIST);

    UtilXml.startElement(pHandler, _XML_TAG_PUSHBACK_SQUARE_LIST);
    for (PushbackSquare pushbackSquare : getPushbackSquares()) {
      pushbackSquare.addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, _XML_TAG_PUSHBACK_SQUARE_LIST);

    UtilXml.startElement(pHandler, _XML_TAG_MOVE_SQUARE_LIST);
    for (MoveSquare moveSquare : getMoveSquares()) {
      moveSquare.addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, _XML_TAG_MOVE_SQUARE_LIST);

    UtilXml.startElement(pHandler, _XML_TAG_TRACK_NUMBER_LIST);
    for (TrackNumber trackNumber : getTrackNumbers()) {
      trackNumber.addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, _XML_TAG_TRACK_NUMBER_LIST);
    
    UtilXml.startElement(pHandler, _XML_TAG_DICE_DECORATION_LIST);
    for (DiceDecoration diceDecoration : getDiceDecorations()) {
      diceDecoration.addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, _XML_TAG_DICE_DECORATION_LIST);

    UtilXml.startElement(pHandler, _XML_TAG_PLAYER_LIST);
    for (Player player : getGame().getPlayers()) {
      PlayerState playerState = getPlayerState(player);
      FieldCoordinate playerCoordinate = getPlayerCoordinate(player);
      Card[] cards = getCards(player);
      if ((playerState != null) || (playerCoordinate != null) || ArrayTool.isProvided(cards)) {
        int playerStateId = (playerState != null) ? playerState.getId() : 0;
        attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, player.getId());
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STATE, playerStateId);
        UtilXml.startElement(pHandler, _XML_TAG_PLAYER, attributes);
        if (playerCoordinate != null) {
          attributes = new AttributesImpl();
          UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, playerCoordinate.getX());
          UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, playerCoordinate.getY());
          UtilXml.addEmptyElement(pHandler, _XML_TAG_COORDINATE, attributes);
        }
        for (Card card : cards) {
          attributes = new AttributesImpl();
          UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, card.getName());
          UtilXml.addEmptyElement(pHandler, _XML_TAG_CARD, attributes);
        }
        UtilXml.endElement(pHandler, _XML_TAG_PLAYER);
      }
    }
    UtilXml.endElement(pHandler, _XML_TAG_PLAYER_LIST);

    UtilXml.startElement(pHandler, _XML_TAG_FIELD_MARKER_LIST);
    for (FieldMarker fieldMarker : getFieldMarkers()) {
      fieldMarker.addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, _XML_TAG_FIELD_MARKER_LIST);

    UtilXml.startElement(pHandler, _XML_TAG_PLAYER_MARKER_LIST);
    for (PlayerMarker playerMarker : getPlayerMarkers()) {
      playerMarker.addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, _XML_TAG_PLAYER_MARKER_LIST);
    
    UtilXml.endElement(pHandler, XML_TAG);
    
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
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

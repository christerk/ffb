package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.BloodSpot;
import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.DiceDecoration;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.marking.FieldMarker;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PushbackSquare;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.TrackNumber;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.CardEffectFactory;
import com.fumbbl.ffb.factory.CardFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillWithValue;
import com.fumbbl.ffb.model.stadium.OnPitchEnhancement;
import com.fumbbl.ffb.model.stadium.TrapDoor;
import com.fumbbl.ffb.skill.bb2020.special.WisdomOfTheWhiteDwarf;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class FieldModel implements IJsonSerializable {

	private boolean fBallMoving;
	private boolean fBallInPlay;
	private FieldCoordinate fBallCoordinate;

	private FieldCoordinate fBombCoordinate;
	private boolean fBombMoving;

	private Weather fWeather;
	private RangeRuler fRangeRuler;
	private final Map<String, FieldCoordinate> fCoordinateByPlayerId;
	private final Map<String, PlayerState> fStateByPlayerId;
	private final List<BloodSpot> fBloodspots;
	private final Set<PushbackSquare> fPushbackSquares;
	private final Set<MoveSquare> fMoveSquares;
	private final Set<TrackNumber> fTrackNumbers;
	private final Set<DiceDecoration> fDiceDecorations;
	private final Set<FieldMarker> fFieldMarkers;
	private final Set<PlayerMarker> fPlayerMarkers;
	private final Set<PlayerMarker> transientPlayerMarkers;
	private final Set<FieldMarker> transientFieldMarkers;
	private final Map<String, Set<Card>> fCardsByPlayerId;
	private final Map<String, Set<CardEffect>> fCardEffectsByPlayerId;
	private TargetSelectionState targetSelectionState;
	private final Set<String> multiBlockTargets = new HashSet<>();
	private final Set<FieldCoordinate> multiBlockTargetCoordinates = new HashSet<>();
	private final List<TrapDoor> trapDoors = new ArrayList<>();

	private final transient Map<FieldCoordinate, List<String>> fPlayerIdByCoordinate; // no need to serialize this, as it can be
	// reconstructed
	private transient Game fGame;

	public FieldModel(Game pGame) {
		fGame = pGame;
		fPlayerIdByCoordinate = new HashMap<>();
		fCoordinateByPlayerId = new HashMap<>();
		fBloodspots = new ArrayList<>();
		fPushbackSquares = new HashSet<>();
		fMoveSquares = new HashSet<>();
		fTrackNumbers = new HashSet<>();
		fDiceDecorations = new HashSet<>();
		fStateByPlayerId = new HashMap<>();
		fFieldMarkers = new HashSet<>();
		fPlayerMarkers = new HashSet<>();
		fCardsByPlayerId = new HashMap<>();
		fCardEffectsByPlayerId = new HashMap<>();
		transientFieldMarkers = new HashSet<>();
		transientPlayerMarkers = new HashSet<>();
	}

	public void add(TrapDoor trapDoor) {
		this.trapDoors.add(trapDoor);
		notifyObservers(ModelChangeId.FIELD_MODEL_ADD_TRAP_DOOR, null, trapDoor);
	}

	public void remove(TrapDoor trapDoor) {
		trapDoors.remove(trapDoor);
		notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_TRAP_DOOR, null, trapDoor);
	}

	public void clearTrapdoors() {
		while (!trapDoors.isEmpty()) {
			remove(trapDoors.get(0));
		}
	}

	public Set<OnPitchEnhancement> getOnPitchEnhancements() {
		return new HashSet<>(trapDoors);
	}

	public Set<TrapDoor> getTrapDoors() {
		return new HashSet<>(trapDoors);
	}

	public void addMultiBlockTarget(String playerId, BlockKind blockKind) {
		Player<?> player = getGame().getPlayerById(playerId);
		PlayerState playerState = getPlayerState(player);
		if (blockKind == BlockKind.STAB) {
			playerState = playerState.changeSelectedStabTarget(true);
		} else {
			playerState = playerState.changeSelectedBlockTarget(true);
		}
		setPlayerState(player, playerState);
		multiBlockTargets.add(playerId);
		multiBlockTargetCoordinates.add(getPlayerCoordinate(player));
	}

	public void removeMultiBlockTarget(String playerId) {
		Player<?> player = getGame().getPlayerById(playerId);
		PlayerState playerState = getPlayerState(player).changeSelectedStabTarget(false).changeSelectedBlockTarget(false);
		setPlayerState(player, playerState);

		multiBlockTargets.remove(playerId);
		multiBlockTargetCoordinates.remove(getPlayerCoordinate(player));
	}

	public void clearMultiBlockTargets() {

		multiBlockTargets.forEach(target -> {
			Player<?> player = getGame().getPlayerById(target);
			PlayerState playerState = getPlayerState(player);
			setPlayerState(player, playerState.changeSelectedStabTarget(false).changeSelectedBlockTarget(false));
		});

		multiBlockTargets.clear();
		multiBlockTargetCoordinates.clear();
	}

	public boolean isMultiBlockTarget(String playerId) {
		return multiBlockTargets.contains(playerId);
	}

	public boolean wasMultiBlockTargetSquare(FieldCoordinate coordinate) {
		return multiBlockTargetCoordinates.contains(coordinate);
	}

	public Player<?> getPlayer(FieldCoordinate pPlayerPosition) {
		String playerId = null;
		if (pPlayerPosition != null) {
			List<String> playersAtCoordinate = fPlayerIdByCoordinate.get(pPlayerPosition);
			if (playersAtCoordinate != null) {
				int numPlayers = playersAtCoordinate.size();
				if (numPlayers > 0) {
					playerId = playersAtCoordinate.get(numPlayers - 1);
				}
			}
		}
		if (playerId == null) {
			return null;
		}
		return getGame().getPlayerById(playerId);
	}

	public List<Player<?>> getPlayers(FieldCoordinate pPlayerPosition) {
		List<Player<?>> players = null;
		if (pPlayerPosition != null) {
			List<String> playersAtCoordinate = fPlayerIdByCoordinate.get(pPlayerPosition);
			if (playersAtCoordinate != null && playersAtCoordinate.size() > 0) {
				players = new ArrayList<>();
				for (String playerId : playersAtCoordinate) {
					players.add(getGame().getPlayerById(playerId));
				}
			}
		}
		return players;
	}

	public void remove(Player<?> pPlayer) {
		if (pPlayer == null) {
			return;
		}
		FieldCoordinate coordinate = getPlayerCoordinate(pPlayer);
		List<String> playersAtCoordinate = fPlayerIdByCoordinate.get(coordinate);
		if (playersAtCoordinate != null) {
			playersAtCoordinate.remove(pPlayer.getId());
		}
		fCoordinateByPlayerId.remove(pPlayer.getId());
		notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_PLAYER, pPlayer.getId(), coordinate);
	}

	public void remove(Team pTeam) {
		if (pTeam != null) {
			for (Player<?> player : pTeam.getPlayers()) {
				remove(player);
				fStateByPlayerId.remove(player.getId());
			}
		}
	}

	public TargetSelectionState getTargetSelectionState() {
		return targetSelectionState;
	}

	public void setTargetSelectionState(TargetSelectionState targetSelectionState) {
		if (this.targetSelectionState == null && targetSelectionState == null) {
			return;
		}
		if (this.targetSelectionState != null && this.targetSelectionState.equals(targetSelectionState)) {
			return;
		}

		this.targetSelectionState = targetSelectionState;
		notifyObservers(ModelChangeId.FIELD_MODEL_SET_TARGET_SELECTION_STATE, null, targetSelectionState);
	}

	public FieldCoordinate getPlayerCoordinate(Player<?> pPlayer) {
		if (pPlayer == null) {
			return null;
		}
		return fCoordinateByPlayerId.get(pPlayer.getId());
	}

	public void setPlayerCoordinate(Player<?> pPlayer, FieldCoordinate pCoordinate) {
		if ((pCoordinate == null) || (pPlayer == null)) {
			return;
		}
		FieldCoordinate oldCoordinate = getPlayerCoordinate(pPlayer);
		if (!FieldCoordinate.equals(pCoordinate, oldCoordinate)) {
			fCoordinateByPlayerId.put(pPlayer.getId(), pCoordinate);

			// Remove player from old coordinate
			if (oldCoordinate != null) {
				List<String> playerList = fPlayerIdByCoordinate.get(oldCoordinate);
				if (playerList != null) {
					playerList.remove(pPlayer.getId());
				}
			}
			// Add player to new coordinate
			List<String> playerList = fPlayerIdByCoordinate.computeIfAbsent(pCoordinate, k -> new ArrayList<>());
			playerList.add(pPlayer.getId());
			notifyObservers(ModelChangeId.FIELD_MODEL_SET_PLAYER_COORDINATE, pPlayer.getId(), pCoordinate);
		}
	}

	public FieldCoordinate[] getPlayerCoordinates() {
		List<FieldCoordinate> coordinates = new ArrayList<>();
		for (FieldCoordinate c : fPlayerIdByCoordinate.keySet()) {
			if (fPlayerIdByCoordinate.get(c).size() > 0) {
				coordinates.add(c);
			}
		}
		return coordinates.toArray(new FieldCoordinate[0]);
	}

	public void sendPosition(Player<?> pPlayer) {
		if (pPlayer == null) {
			return;
		}

		PlayerState oldState = fStateByPlayerId.get(pPlayer.getId());
		FieldCoordinate oldCoordinate = fCoordinateByPlayerId.get(pPlayer.getId());

		notifyObservers(ModelChangeId.FIELD_MODEL_SET_PLAYER_STATE, pPlayer.getId(), oldState);
		notifyObservers(ModelChangeId.FIELD_MODEL_SET_PLAYER_COORDINATE, pPlayer.getId(), oldCoordinate);
	}

	public void setPlayerState(Player<?> pPlayer, PlayerState pState) {
		setPlayerState(pPlayer, pState, false);
	}

	public void setPlayerState(Player<?> pPlayer, PlayerState pState, boolean force) {
		if (pPlayer == null) {
			return;
		}
		PlayerState oldState = fStateByPlayerId.get(pPlayer.getId());
		if ((oldState == null) || ((pState != null) && (pState.getId() != oldState.getId() || force))) {
			fStateByPlayerId.put(pPlayer.getId(), pState);
			notifyObservers(ModelChangeId.FIELD_MODEL_SET_PLAYER_STATE, pPlayer.getId(), pState);
		}
	}

	public PlayerState getPlayerState(Player<?> pPlayer) {
		if (pPlayer == null) {
			return null;
		}
		PlayerState playerState = fStateByPlayerId.get(pPlayer.getId());
		return (playerState != null) ? playerState : new PlayerState(PlayerState.UNKNOWN);
	}

	public void addCard(Player<?> pPlayer, Card pCard) {
		if ((pPlayer == null) || (pCard == null)) {
			return;
		}
		Set<Card> cards = fCardsByPlayerId.computeIfAbsent(pPlayer.getId(), k -> new HashSet<>());
		cards.add(pCard);

		StatsMechanic mechanic = (StatsMechanic) getGame().getFactory(Factory.MECHANIC).forName(Mechanic.Type.STAT.name());

		pPlayer.addActivationEnhancements(pCard, getGame().getFactory(Factory.SKILL), mechanic);
		notifyObservers(ModelChangeId.FIELD_MODEL_ADD_CARD, pPlayer.getId(), pCard);
	}

	public void keepDeactivatedCard(Player<?> player, Card card) {
		if ((player == null) || (card == null) || !fCardsByPlayerId.containsKey(player.getId())) {
			return;
		}
		StatsMechanic mechanic = (StatsMechanic) getGame().getFactory(Factory.MECHANIC).forName(Mechanic.Type.STAT.name());

		player.addDeactivationEnhancements(card, getGame().getFactory(Factory.SKILL), mechanic);
		notifyObservers(ModelChangeId.FIELD_MODEL_KEEP_DEACTIVATED_CARD, player.getId(), card);
	}

	public void removeCard(Player<?> pPlayer, Card pCard) {
		if ((pPlayer == null) || (pCard == null)) {
			return;
		}
		boolean removed = false;
		Set<Card> cards = fCardsByPlayerId.get(pPlayer.getId());
		if (cards != null) {
			removed = cards.remove(pCard);
		}
		if (removed) {
			pPlayer.removeEnhancements(pCard);
			notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_CARD, pPlayer.getId(), pCard);
		}
	}

	public Card[] getCards(Player<?> pPlayer) {
		if (pPlayer == null) {
			return null;
		}
		Set<Card> cards = fCardsByPlayerId.get(pPlayer.getId());
		if (cards == null) {
			return new Card[0];
		}
		return cards.toArray(new Card[0]);
	}

	public Player<?> findPlayer(Card pCard) {
		for (String playerId : fCardsByPlayerId.keySet()) {
			for (Card card : fCardsByPlayerId.get(playerId)) {
				if (card == pCard) {
					return getGame().getPlayerById(playerId);
				}
			}
		}
		return null;
	}

	public void addCardEffect(Player<?> pPlayer, CardEffect pCardEffect) {
		if ((pPlayer == null) || (pCardEffect == null)) {
			return;
		}
		Set<CardEffect> cardEffects = fCardEffectsByPlayerId.computeIfAbsent(pPlayer.getId(), k -> new HashSet<>());
		cardEffects.add(pCardEffect);
		SkillFactory factory = getGame().getFactory(Factory.SKILL);
		pPlayer.addTemporarySkills(pCardEffect.getName(), pCardEffect.skills().stream().map(cls -> new SkillWithValue(factory.forClass(cls))).collect(Collectors.toSet()));
		notifyObservers(ModelChangeId.FIELD_MODEL_ADD_CARD_EFFECT, pPlayer.getId(), pCardEffect);
	}

	public void addPrayerEnhancements(Player<?> player, Prayer prayer) {
		SkillFactory factory = getGame().getFactory(Factory.SKILL);
		StatsMechanic mechanic = (StatsMechanic) getGame().getFactory(Factory.MECHANIC).forName(Mechanic.Type.STAT.name());
		player.addEnhancement(prayer.getName(), prayer.enhancements(mechanic), factory);
		notifyObservers(ModelChangeId.FIELD_MODEL_ADD_PRAYER, player.getId(), prayer.name());
	}

	public void addSkillEnhancements(Player<?> player, Skill skill) {
		SkillFactory factory = getGame().getFactory(Factory.SKILL);
		player.addEnhancement(skill.getName(), skill.getEnhancements(), factory);
		notifyObservers(ModelChangeId.FIELD_MODEL_ADD_SKILL_ENHANCEMENTS, player.getId(), skill.getName());
	}

	public void removeSkillEnhancements(Player<?> player, Skill skill) {
		removeSkillEnhancements(player, skill.getName());
	}

	public void removeSkillEnhancements(Player<?> player, String name) {
		player.removeEnhancements(name);
		notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_SKILL_ENHANCEMENTS, player.getId(), name);
	}

	public void addIntensiveTrainingSkill(String playerId, Skill skill) {
		Player<?> player = getGame().getPlayerById(playerId);
		player.addTemporarySkills(Prayer.INTENSIVE_TRAINING.getName(), Collections.singleton(new SkillWithValue(skill, String.valueOf(skill.getDefaultSkillValue()))));
		notifyObservers(ModelChangeId.FIELD_MODEL_ADD_INTENSIVE_TRAINING, playerId, skill);
	}

	public void addWisdomSkill(String playerId, SkillWithValue skillWithValue) {
		Player<?> player = getGame().getPlayerById(playerId);
		Skill wisdomSkill = ((SkillFactory) getGame().getFactory(Factory.SKILL)).forClass(WisdomOfTheWhiteDwarf.class);
		player.addTemporarySkills(wisdomSkill.getName(), Collections.singleton(skillWithValue));
		notifyObservers(ModelChangeId.FIELD_MODEL_ADD_WISDOM, playerId, skillWithValue.getSkill());
	}

	public void removePrayerEnhancements(Player<?> player, Prayer prayer) {
		player.removeEnhancements(prayer);
		notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_PRAYER, player.getId(), prayer.name());
	}

	public boolean removeCardEffect(Player<?> pPlayer, CardEffect pCardEffect) {
		if ((pPlayer == null) || (pCardEffect == null)) {
			return false;
		}
		boolean removed = false;
		Set<CardEffect> cardEffects = fCardEffectsByPlayerId.get(pPlayer.getId());
		if (cardEffects != null) {
			removed = cardEffects.remove(pCardEffect);
		}
		if (removed) {
			pPlayer.removeTemporarySkills(pCardEffect.getName());
			notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_CARD_EFFECT, pPlayer.getId(), pCardEffect);
		}
		return removed;
	}

	public CardEffect[] getCardEffects(Player<?> pPlayer) {
		if (pPlayer == null) {
			return null;
		}
		Set<CardEffect> cardEffects = fCardEffectsByPlayerId.get(pPlayer.getId());
		if (cardEffects == null) {
			return new CardEffect[0];
		}
		return cardEffects.toArray(new CardEffect[0]);
	}

	public boolean hasCardEffect(Player<?> pPlayer, CardEffect pCardEffect) {
		if ((pPlayer == null) || (pCardEffect == null)) {
			return false;
		}
		Set<CardEffect> cardEffects = fCardEffectsByPlayerId.get(pPlayer.getId());
		if (cardEffects == null) {
			return false;
		}
		return cardEffects.contains(pCardEffect);
	}

	public Player<?>[] findPlayers(CardEffect pCardEffect) {
		Set<Player<?>> players = new HashSet<>();
		for (String playerId : fCardEffectsByPlayerId.keySet()) {
			for (CardEffect cardEffect : fCardEffectsByPlayerId.get(playerId)) {
				if (cardEffect == pCardEffect) {
					players.add(getGame().getPlayerById(playerId));
					break;
				}
			}
		}
		return players.toArray(new Player[0]);
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

	public FieldCoordinate[] findAdjacentCoordinates(FieldCoordinate pCoordinate, FieldCoordinateBounds pBounds,
	                                                 int pSteps, boolean pWithStartCoordinate) {
		List<FieldCoordinate> adjacentCoordinates = new ArrayList<>();
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
		return adjacentCoordinates.toArray(new FieldCoordinate[0]);
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
		return fBloodspots.toArray(new BloodSpot[0]);
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
		return fTrackNumbers.toArray(new TrackNumber[0]);
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
			Arrays.stream(pPushbackSquares).forEach(this::add);
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
		return fPushbackSquares.toArray(new PushbackSquare[0]);
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
		return fMoveSquares.toArray(new MoveSquare[0]);
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
		return fDiceDecorations.toArray(new DiceDecoration[0]);
	}

	public DiceDecoration getDiceDecoration(FieldCoordinate pCoordinate) {
		for (DiceDecoration diceDecoration : fDiceDecorations) {
			if (diceDecoration.getCoordinate().equals(pCoordinate)) {
				return diceDecoration;
			}
		}
		return null;
	}

	public void addTransient(FieldMarker pFieldMarker) {
		transientFieldMarkers.remove(pFieldMarker);
		transientFieldMarkers.add(pFieldMarker);
	}

	public void removeTransient(FieldMarker fieldMarker) {
		transientFieldMarkers.remove(fieldMarker);
	}

	public void add(FieldMarker pFieldMarker) {
		if (pFieldMarker == null) {
			return;
		}
		fFieldMarkers.remove(pFieldMarker);
		fFieldMarkers.add(pFieldMarker);
		notifyObservers(ModelChangeId.FIELD_MODEL_ADD_FIELD_MARKER, null, pFieldMarker);
	}

	public void remove(FieldMarker pFieldMarker) {
		if (fFieldMarkers.remove(pFieldMarker)) {
			notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_FIELD_MARKER, null, pFieldMarker);
		}
	}

	public FieldMarker[] getFieldMarkers() {
		return fFieldMarkers.toArray(new FieldMarker[0]);
	}


	public FieldMarker[] getTransientFieldMarkers() {
		return transientFieldMarkers.toArray(new FieldMarker[0]);
	}

	public FieldMarker getFieldMarker(FieldCoordinate pCoordinate) {
		for (FieldMarker fieldMarker : fFieldMarkers) {
			if (fieldMarker.getCoordinate().equals(pCoordinate)) {
				return fieldMarker;
			}
		}
		return null;
	}

	public FieldMarker getTransientFieldMarker(FieldCoordinate pCoordinate) {
		for (FieldMarker fieldMarker : transientFieldMarkers) {
			if (fieldMarker.getCoordinate().equals(pCoordinate)) {
				return fieldMarker;
			}
		}
		return null;
	}

	public void addTransient(PlayerMarker playerMarker) {
		removeTransient(playerMarker);
		transientPlayerMarkers.add(playerMarker);
	}

	public void removeTransient(PlayerMarker playerMarker) {
		transientPlayerMarkers.remove(playerMarker);
	}

	public void add(PlayerMarker pPlayerMarker) {
		if (pPlayerMarker == null) {
			return;
		}
		fPlayerMarkers.remove(pPlayerMarker);
		fPlayerMarkers.add(pPlayerMarker);
		notifyObservers(ModelChangeId.FIELD_MODEL_ADD_PLAYER_MARKER, null, pPlayerMarker);
	}

	public void remove(PlayerMarker pPlayerMarker) {
		if (fPlayerMarkers.remove(pPlayerMarker)) {
			notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_PLAYER_MARKER, null, pPlayerMarker);
		}
	}

	public PlayerMarker[] getPlayerMarkers() {
		return fPlayerMarkers.toArray(new PlayerMarker[0]);
	}

	public PlayerMarker getPlayerMarker(String pPlayerId) {
		for (PlayerMarker playerMarker : fPlayerMarkers) {
			if (playerMarker.getPlayerId().equals(pPlayerId)) {
				return playerMarker;
			}
		}
		return null;
	}

	public PlayerMarker[] getTransientPlayerMarkers() {
		return transientPlayerMarkers.toArray(new PlayerMarker[0]);
	}

	public PlayerMarker getTransientPlayerMarker(String pPlayerId) {
		for (PlayerMarker playerMarker : transientPlayerMarkers) {
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
		if (Objects.equals(pRangeRuler, fRangeRuler)) {
			return;
		}
		fRangeRuler = pRangeRuler;
		notifyObservers(ModelChangeId.FIELD_MODEL_SET_RANGE_RULER, null, fRangeRuler);
	}

	public boolean updatePlayerAndBallPosition(Player<?> pPlayer, FieldCoordinate pCoordinate) {
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
			Player<?> player = getGame().getPlayerById(playerId);
			transformedModel.setPlayerState(player, getPlayerState(player));
			Card[] cards = getCards(player);
			if (ArrayTool.isProvided(cards)) {
				for (Card card : getCards(player)) {
					transformedModel.addCard(player, card);
				}
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

		trapDoors.stream().map(TrapDoor::transform).forEach(transformedModel::add);

		return transformedModel;

	}

	// change tracking

	private void notifyObservers(ModelChangeId pChangeId, String pKey, Object pValue) {
		if ((getGame() == null) || (pChangeId == null)) {
			return;
		}
		getGame().notifyObservers(new ModelChange(pChangeId, pKey, pValue));
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
		for (Player<?> player : getGame().getPlayers()) {

			JsonObject playerDataObject = new JsonObject();
			IJsonOption.PLAYER_ID.addTo(playerDataObject, player.getId());
			IJsonOption.PLAYER_COORDINATE.addTo(playerDataObject, getPlayerCoordinate(player));
			IJsonOption.PLAYER_STATE.addTo(playerDataObject, getPlayerState(player));

			List<String> cards = new ArrayList<>();
			for (Card card : getCards(player)) {
				cards.add(card.getName());
			}
			IJsonOption.CARDS.addTo(playerDataObject, cards);

			List<String> cardEffects = new ArrayList<>();
			for (CardEffect cardEffect : getCardEffects(player)) {
				cardEffects.add(cardEffect.getName());
			}
			IJsonOption.CARD_EFFECTS.addTo(playerDataObject, cardEffects);

			playerDataArray.add(playerDataObject);

		}
		IJsonOption.PLAYER_DATA_ARRAY.addTo(jsonObject, playerDataArray);

		if (targetSelectionState != null) {
			IJsonOption.TARGET_SELECTION_STATE.addTo(jsonObject, targetSelectionState.toJsonValue());
		}

		JsonArray trapDoorArray = new JsonArray();
		trapDoors.stream().map(TrapDoor::toJsonValue).forEach(trapDoorArray::add);
		IJsonOption.TRAP_DOORS.addTo(jsonObject, trapDoorArray);

		return jsonObject;

	}

	public FieldModel initFrom(IFactorySource source, JsonValue jsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);

		fWeather = (Weather) IJsonOption.WEATHER.getFrom(source, jsonObject);
		fBallCoordinate = IJsonOption.BALL_COORDINATE.getFrom(source, jsonObject);
		fBallInPlay = IJsonOption.BALL_IN_PLAY.getFrom(source, jsonObject);
		fBallMoving = IJsonOption.BALL_MOVING.getFrom(source, jsonObject);
		fBombCoordinate = IJsonOption.BOMB_COORDINATE.getFrom(source, jsonObject);
		fBombMoving = IJsonOption.BOMB_MOVING.getFrom(source, jsonObject);

		fBloodspots.clear();
		JsonArray bloodspotArray = IJsonOption.BLOODSPOT_ARRAY.getFrom(source, jsonObject);
		for (int i = 0; i < bloodspotArray.size(); i++) {
			fBloodspots.add(new BloodSpot().initFrom(source, bloodspotArray.get(i)));
		}

		fPushbackSquares.clear();
		JsonArray pushbackSquareArray = IJsonOption.PUSHBACK_SQUARE_ARRAY.getFrom(source, jsonObject);
		for (int i = 0; i < pushbackSquareArray.size(); i++) {
			fPushbackSquares.add(new PushbackSquare().initFrom(source, pushbackSquareArray.get(i)));
		}

		fMoveSquares.clear();
		JsonArray moveSquareArray = IJsonOption.MOVE_SQUARE_ARRAY.getFrom(source, jsonObject);
		for (int i = 0; i < moveSquareArray.size(); i++) {
			fMoveSquares.add(new MoveSquare().initFrom(source, moveSquareArray.get(i)));
		}

		fTrackNumbers.clear();
		JsonArray trackNumberArray = IJsonOption.TRACK_NUMBER_ARRAY.getFrom(source, jsonObject);
		for (int i = 0; i < trackNumberArray.size(); i++) {
			fTrackNumbers.add(new TrackNumber().initFrom(source, trackNumberArray.get(i)));
		}

		fDiceDecorations.clear();
		JsonArray diceDecorationArray = IJsonOption.DICE_DECORATION_ARRAY.getFrom(source, jsonObject);
		for (int i = 0; i < diceDecorationArray.size(); i++) {
			fDiceDecorations.add(new DiceDecoration().initFrom(source, diceDecorationArray.get(i)));
		}

		fFieldMarkers.clear();
		JsonArray fieldMarkerArray = IJsonOption.FIELD_MARKER_ARRAY.getFrom(source, jsonObject);
		for (int i = 0; i < fieldMarkerArray.size(); i++) {
			fFieldMarkers.add(new FieldMarker().initFrom(source, fieldMarkerArray.get(i)));
		}

		fPlayerMarkers.clear();
		JsonArray playerMarkerArray = IJsonOption.PLAYER_MARKER_ARRAY.getFrom(source, jsonObject);
		for (int i = 0; i < playerMarkerArray.size(); i++) {
			fPlayerMarkers.add(new PlayerMarker().initFrom(source, playerMarkerArray.get(i)));
		}

		fPlayerIdByCoordinate.clear();
		fCoordinateByPlayerId.clear();
		fStateByPlayerId.clear();
		fCardsByPlayerId.clear();
		trapDoors.clear();

		CardFactory cardFactory = source.getFactory(Factory.CARD);
		CardEffectFactory cardEffectFactory = source.getFactory(Factory.CARD_EFFECT);

		JsonArray playerDataArray = IJsonOption.PLAYER_DATA_ARRAY.getFrom(source, jsonObject);
		for (int i = 0; i < playerDataArray.size(); i++) {

			JsonObject playerDataObject = UtilJson.toJsonObject(playerDataArray.get(i));

			String playerId = IJsonOption.PLAYER_ID.getFrom(source, playerDataObject);
			Player<?> player = getGame().getPlayerById(playerId);

			FieldCoordinate playerCoordinate = IJsonOption.PLAYER_COORDINATE.getFrom(source, playerDataObject);
			setPlayerCoordinate(player, playerCoordinate);

			PlayerState playerState = IJsonOption.PLAYER_STATE.getFrom(source, playerDataObject);
			setPlayerState(player, playerState);

			String[] cards = IJsonOption.CARDS.getFrom(source, playerDataObject);
			if (ArrayTool.isProvided(cards)) {
				for (String card : cards) addCard(player, cardFactory.forName(card));
			}

			String[] cardEffects = IJsonOption.CARD_EFFECTS.getFrom(source, playerDataObject);
			if (ArrayTool.isProvided(cardEffects)) {
				Arrays.stream(cardEffects).forEach(cardEffect -> addCardEffect(player, cardEffectFactory.forName(cardEffect)));
			}

		}

		JsonObject targetSelectionStateObject = IJsonOption.TARGET_SELECTION_STATE.getFrom(source, jsonObject);
		if (targetSelectionStateObject == null) {
			targetSelectionStateObject = IJsonOption.BLITZ_STATE.getFrom(source, jsonObject);
		}
		if (targetSelectionStateObject != null) {
			targetSelectionState = new TargetSelectionState().initFrom(source, targetSelectionStateObject);
		}

		JsonArray trapDoorArray = IJsonOption.TRAP_DOORS.getFrom(source, jsonObject);

		if (trapDoorArray != null) {
			trapDoorArray.values().stream().map(value -> new TrapDoor().initFrom(source, value)).forEach(trapDoors::add);
		}
		return this;

	}

}

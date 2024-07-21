package com.fumbbl.ffb.model.change;

import com.fumbbl.ffb.BloodSpot;
import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.DiceDecoration;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PushbackSquare;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.TrackNumber;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogBuyCardsAndInducementsParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardChoices;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.marking.FieldMarker;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.stadium.TrapDoor;
import com.fumbbl.ffb.option.IGameOption;

import java.util.Date;

/**
 * @author Kalimar
 */
public class ModelChangeProcessor {

	public boolean apply(Game pGame, ModelChange pModelChange) {

		if ((pGame == null) || (pModelChange == null) || (pModelChange.getChangeId() == null)) {
			return false;
		}
		SkillFactory skillFactory = pGame.getFactory(FactoryType.Factory.SKILL);

		switch (pModelChange.getChangeId()) {

			case ACTING_PLAYER_MARK_SKILL_USED:
				pGame.getActingPlayer().markSkillUsed((Skill) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_MARK_SKILL_UNUSED:
				pGame.getActingPlayer().markSkillUnused((Skill) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_CURRENT_MOVE:
				pGame.getActingPlayer().setCurrentMove((Integer) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_DODGING:
				pGame.getActingPlayer().setDodging((Boolean) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_GOING_FOR_IT:
				pGame.getActingPlayer().setGoingForIt((Boolean) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_HAS_BLOCKED:
				pGame.getActingPlayer().setHasBlocked((Boolean) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_HAS_FED:
				pGame.getActingPlayer().setHasFed((Boolean) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_HAS_FOULED:
				pGame.getActingPlayer().setHasFouled((Boolean) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_HAS_JUMPED:
				pGame.getActingPlayer().setHasJumped((Boolean) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_HAS_MOVED:
				pGame.getActingPlayer().setHasMoved((Boolean) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_HAS_PASSED:
				pGame.getActingPlayer().setHasPassed((Boolean) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_JUMPING:
				pGame.getActingPlayer().setJumping((Boolean) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_OLD_PLAYER_STATE:
				pGame.getActingPlayer().setOldPlayerState((PlayerState) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_PLAYER_ACTION:
				pGame.getActingPlayer().setPlayerAction((PlayerAction) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_PLAYER_ID:
				pGame.getActingPlayer().setPlayerId((String) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_STANDING_UP:
				pGame.getActingPlayer().setStandingUp((Boolean) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_STRENGTH:
				pGame.getActingPlayer().setStrength((Integer) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_SUFFERING_ANIMOSITY:
				pGame.getActingPlayer().setSufferingAnimosity((Boolean) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_SUFFERING_BLOOD_LUST:
				pGame.getActingPlayer().setSufferingBloodLust((Boolean) pModelChange.getValue());
				return true;
			case ACTING_PLAYER_SET_JUMPS_WITHOUT_MODIFIERS:
				pGame.getActingPlayer().setJumpsWithoutModifiers((Boolean) pModelChange.getValue());
				return true;
			case FIELD_MODEL_ADD_BLOOD_SPOT:
				pGame.getFieldModel().add((BloodSpot) pModelChange.getValue());
				return true;
			case FIELD_MODEL_ADD_CARD:
				pGame.getFieldModel().addCard(pGame.getPlayerById(pModelChange.getKey()), (Card) pModelChange.getValue());
				return true;
			case FIELD_MODEL_ADD_CARD_EFFECT:
				pGame.getFieldModel().addCardEffect(pGame.getPlayerById(pModelChange.getKey()),
					(CardEffect) pModelChange.getValue());
				return true;
			case FIELD_MODEL_ADD_DICE_DECORATION:
				pGame.getFieldModel().add((DiceDecoration) pModelChange.getValue());
				return true;
			case FIELD_MODEL_ADD_INTENSIVE_TRAINING:
				pGame.getFieldModel().addIntensiveTrainingSkill(pModelChange.getKey(), (Skill) pModelChange.getValue());
				return true;
			case FIELD_MODEL_ADD_FIELD_MARKER:
				pGame.getFieldModel().add((FieldMarker) pModelChange.getValue());
				return true;
			case FIELD_MODEL_ADD_MOVE_SQUARE:
				pGame.getFieldModel().add((MoveSquare) pModelChange.getValue());
				return true;
			case FIELD_MODEL_ADD_PLAYER_MARKER:
				PlayerMarker marker = (PlayerMarker) pModelChange.getValue();
				pGame.getFieldModel().add(marker);
				return true;
			case FIELD_MODEL_ADD_PRAYER:
				pGame.getFieldModel().addPrayerEnhancements(pGame.getPlayerById(pModelChange.getKey()), Prayer.valueOf((String) pModelChange.getValue()));
				return true;
			case FIELD_MODEL_ADD_PUSHBACK_SQUARE:
				pGame.getFieldModel().add((PushbackSquare) pModelChange.getValue());
				return true;
			case FIELD_MODEL_ADD_SKILL_ENHANCEMENTS:
				pGame.getFieldModel().addSkillEnhancements(pGame.getPlayerById(pModelChange.getKey()), skillFactory.forName((String) pModelChange.getValue()));
				return true;
			case FIELD_MODEL_ADD_TRACK_NUMBER:
				pGame.getFieldModel().add((TrackNumber) pModelChange.getValue());
				return true;
			case FIELD_MODEL_ADD_TRAP_DOOR:
				pGame.getFieldModel().add((TrapDoor) pModelChange.getValue());
				return true;
			case FIELD_MODEL_ADD_WISDOM:
				Constant.getGrantAbleSkills(skillFactory).stream()
					.filter(swv -> swv.getSkill().equals(pModelChange.getValue())).findFirst().ifPresent(swv ->
						pGame.getFieldModel().addWisdomSkill(pModelChange.getKey(), swv)
					);
				return true;
			case FIELD_MODEL_KEEP_DEACTIVATED_CARD:
				pGame.getFieldModel().keepDeactivatedCard(pGame.getPlayerById(pModelChange.getKey()), (Card) pModelChange.getValue());
				return true;
			case FIELD_MODEL_REMOVE_CARD:
				pGame.getFieldModel().removeCard(pGame.getPlayerById(pModelChange.getKey()), (Card) pModelChange.getValue());
				return true;
			case FIELD_MODEL_REMOVE_CARD_EFFECT:
				pGame.getFieldModel().removeCardEffect(pGame.getPlayerById(pModelChange.getKey()),
					(CardEffect) pModelChange.getValue());
				return true;
			case FIELD_MODEL_REMOVE_DICE_DECORATION:
				pGame.getFieldModel().remove((DiceDecoration) pModelChange.getValue());
				return true;
			case FIELD_MODEL_REMOVE_FIELD_MARKER:
				pGame.getFieldModel().remove((FieldMarker) pModelChange.getValue());
				return true;
			case FIELD_MODEL_REMOVE_MOVE_SQUARE:
				pGame.getFieldModel().remove((MoveSquare) pModelChange.getValue());
				return true;
			case FIELD_MODEL_REMOVE_PLAYER:
				pGame.getFieldModel().remove(pGame.getPlayerById(pModelChange.getKey()));
				return true;
			case FIELD_MODEL_REMOVE_PLAYER_MARKER:
				pGame.getFieldModel().remove((PlayerMarker) pModelChange.getValue());
				return true;
			case FIELD_MODEL_REMOVE_PRAYER:
				pGame.getFieldModel().removePrayerEnhancements(pGame.getPlayerById(pModelChange.getKey()), Prayer.valueOf((String) pModelChange.getValue()));
				return true;
			case FIELD_MODEL_REMOVE_PUSHBACK_SQUARE:
				pGame.getFieldModel().remove((PushbackSquare) pModelChange.getValue());
				return true;
			case FIELD_MODEL_REMOVE_SKILL_ENHANCEMENTS:
				pGame.getFieldModel().removeSkillEnhancements(pGame.getPlayerById(pModelChange.getKey()), (String) pModelChange.getValue());
				return true;
			case FIELD_MODEL_REMOVE_TRACK_NUMBER:
				pGame.getFieldModel().remove((TrackNumber) pModelChange.getValue());
				return true;
			case FIELD_MODEL_REMOVE_TRAP_DOOR:
				pGame.getFieldModel().remove((TrapDoor) pModelChange.getValue());
				return true;
			case FIELD_MODEL_SET_BALL_COORDINATE:
				pGame.getFieldModel().setBallCoordinate((FieldCoordinate) pModelChange.getValue());
				return true;
			case FIELD_MODEL_SET_BALL_IN_PLAY:
				pGame.getFieldModel().setBallInPlay((Boolean) pModelChange.getValue());
				return true;
			case FIELD_MODEL_SET_BALL_MOVING:
				pGame.getFieldModel().setBallMoving((Boolean) pModelChange.getValue());
				return true;
			case FIELD_MODEL_SET_BLITZ_STATE:
			case FIELD_MODEL_SET_TARGET_SELECTION_STATE:
				pGame.getFieldModel().setTargetSelectionState((TargetSelectionState) pModelChange.getValue());
				return true;
			case FIELD_MODEL_SET_BOMB_COORDINATE:
				pGame.getFieldModel().setBombCoordinate((FieldCoordinate) pModelChange.getValue());
				return true;
			case FIELD_MODEL_SET_BOMB_MOVING:
				pGame.getFieldModel().setBombMoving((Boolean) pModelChange.getValue());
				return true;
			case FIELD_MODEL_SET_PLAYER_COORDINATE:
				pGame.getFieldModel().setPlayerCoordinate(pGame.getPlayerById(pModelChange.getKey()),
					(FieldCoordinate) pModelChange.getValue());
				return true;
			case FIELD_MODEL_SET_PLAYER_STATE:
				pGame.getFieldModel().setPlayerState(pGame.getPlayerById(pModelChange.getKey()),
					(PlayerState) pModelChange.getValue());
				return true;
			case FIELD_MODEL_SET_RANGE_RULER:
				pGame.getFieldModel().setRangeRuler((RangeRuler) pModelChange.getValue());
				return true;
			case FIELD_MODEL_SET_WEATHER:
				pGame.getFieldModel().setWeather((Weather) pModelChange.getValue());
				return true;

			case GAME_SET_CONCEDED_LEGALLY:
				pGame.setConcededLegally((Boolean) pModelChange.getValue());
				return true;
			case GAME_SET_CONCESSION_POSSIBLE:
				pGame.setConcessionPossible((Boolean) pModelChange.getValue());
				return true;
			case GAME_SET_DEFENDER_ACTION:
				pGame.setDefenderAction((PlayerAction) pModelChange.getValue());
				return true;
			case GAME_SET_DEFENDER_ID:
				pGame.setDefenderId(pModelChange.getKey());
				return true;
			case GAME_SET_DIALOG_PARAMETER:
				pGame.setDialogParameter((IDialogParameter) pModelChange.getValue());
				return true;
			case GAME_SET_FINISHED:
				pGame.setFinished((Date) pModelChange.getValue());
				return true;
			case GAME_SET_HALF:
				pGame.setHalf((Integer) pModelChange.getValue());
				return true;
			case GAME_SET_HOME_FIRST_OFFENSE:
				pGame.setHomeFirstOffense((Boolean) pModelChange.getValue());
				return true;
			case GAME_SET_HOME_PLAYING:
				pGame.setHomePlaying((Boolean) pModelChange.getValue());
				return true;
			case GAME_SET_ID:
				pGame.setId((Long) pModelChange.getValue());
				return true;
			case GAME_SET_LAST_DEFENDER_ID:
				pGame.setLastDefenderId(pModelChange.getKey());
				return true;
			case GAME_SET_PASS_COORDINATE:
				pGame.setPassCoordinate((FieldCoordinate) pModelChange.getValue());
				return true;
			case GAME_SET_SCHEDULED:
				pGame.setScheduled((Date) pModelChange.getValue());
				return true;
			case GAME_SET_SETUP_OFFENSE:
				pGame.setSetupOffense((Boolean) pModelChange.getValue());
				return true;
			case GAME_SET_STARTED:
				pGame.setStarted((Date) pModelChange.getValue());
				return true;
			case GAME_SET_TESTING:
				pGame.setTesting((Boolean) pModelChange.getValue());
				return true;
			case GAME_SET_ADMIN_MODE:
				pGame.setAdminMode((Boolean) pModelChange.getValue());
				return true;
			case GAME_SET_THROWER_ID:
				pGame.setThrowerId(pModelChange.getKey());
				return true;
			case GAME_SET_THROWER_ACTION:
				pGame.setThrowerAction((PlayerAction) pModelChange.getValue());
				return true;
			case GAME_SET_TIMEOUT_ENFORCED:
				pGame.setTimeoutEnforced((Boolean) pModelChange.getValue());
				return true;
			case GAME_SET_TIMEOUT_POSSIBLE:
				pGame.setTimeoutPossible((Boolean) pModelChange.getValue());
				return true;
			case GAME_SET_TURN_MODE:
				pGame.setTurnMode((TurnMode) pModelChange.getValue());
				return true;
			case GAME_SET_LAST_TURN_MODE:
				pGame.setLastTurnMode((TurnMode) pModelChange.getValue());
				return true;
			case GAME_SET_WAITING_FOR_OPPONENT:
				pGame.setWaitingForOpponent((Boolean) pModelChange.getValue());
				return true;

			case GAME_OPTIONS_ADD_OPTION:
				pGame.getOptions().addOption((IGameOption) pModelChange.getValue());
				return true;

			case INDUCEMENT_SET_ACTIVATE_CARD:
				getInducementSet(pGame, isHomeData(pModelChange)).activateCard((Card) pModelChange.getValue());
				return true;
			case INDUCEMENT_SET_ADD_AVAILABLE_CARD:
				getInducementSet(pGame, isHomeData(pModelChange)).addAvailableCard((Card) pModelChange.getValue());
				return true;
			case INDUCEMENT_SET_ADD_INDUCEMENT:
				getInducementSet(pGame, isHomeData(pModelChange)).addInducement((Inducement) pModelChange.getValue());
				return true;
			case INDUCEMENT_SET_ADD_PRAYER:
				getInducementSet(pGame, isHomeData(pModelChange)).addPrayer((Prayer) pModelChange.getValue());
				return true;
			case INDUCEMENT_SET_CARD_CHOICES:
				IDialogParameter dialogParameter = pGame.getDialogParameter();
				if (dialogParameter instanceof DialogBuyCardsAndInducementsParameter) {
					((DialogBuyCardsAndInducementsParameter) dialogParameter).setCardChoices((CardChoices) pModelChange.getValue());
				}
				return true;
			case INDUCEMENT_SET_DEACTIVATE_CARD:
				getInducementSet(pGame, isHomeData(pModelChange)).deactivateCard((Card) pModelChange.getValue());
				return true;
			case INDUCEMENT_SET_REMOVE_AVAILABLE_CARD:
				getInducementSet(pGame, isHomeData(pModelChange)).removeAvailableCard((Card) pModelChange.getValue());
				return true;
			case INDUCEMENT_SET_REMOVE_INDUCEMENT:
				getInducementSet(pGame, isHomeData(pModelChange)).removeInducement((Inducement) pModelChange.getValue());
				return true;
			case INDUCEMENT_SET_REMOVE_PRAYER:
				getInducementSet(pGame, isHomeData(pModelChange)).removePrayer((Prayer) pModelChange.getValue());
				return true;

			case PLAYER_MARK_SKILL_USED:
				pGame.getPlayerById(pModelChange.getKey()).markUsed((Skill) pModelChange.getValue(), pGame);
				return true;
			case PLAYER_MARK_SKILL_UNUSED:
				pGame.getPlayerById(pModelChange.getKey()).markUnused((Skill) pModelChange.getValue(), pGame);
				return true;
			case PLAYER_RESULT_SET_BLOCKS:
				getPlayerResult(pGame, pModelChange.getKey()).setBlocks((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_CASUALTIES:
				getPlayerResult(pGame, pModelChange.getKey()).setCasualties((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_CASUALTIES_WITH_ADDITIONAL_SPP:
				getPlayerResult(pGame, pModelChange.getKey()).setCasualtiesWithAdditionalSpp((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_COMPLETIONS:
				getPlayerResult(pGame, pModelChange.getKey()).setCompletions((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_COMPLETIONS_WITH_ADDITIONAL_SPP:
				getPlayerResult(pGame, pModelChange.getKey()).setCompletionsWithAdditionalSpp((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_CURRENT_SPPS:
				getPlayerResult(pGame, pModelChange.getKey()).setCurrentSpps((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_DEFECTING:
				getPlayerResult(pGame, pModelChange.getKey()).setDefecting((Boolean) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_FOULS:
				getPlayerResult(pGame, pModelChange.getKey()).setFouls((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_HAS_USED_SECRET_WEAPON:
				getPlayerResult(pGame, pModelChange.getKey()).setHasUsedSecretWeapon((Boolean) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_INTERCEPTIONS:
				getPlayerResult(pGame, pModelChange.getKey()).setInterceptions((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_DEFLECTIONS:
				getPlayerResult(pGame, pModelChange.getKey()).setDeflections((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_PASSING:
				getPlayerResult(pGame, pModelChange.getKey()).setPassing((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_PLAYER_AWARDS:
				getPlayerResult(pGame, pModelChange.getKey()).setPlayerAwards((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_RUSHING:
				getPlayerResult(pGame, pModelChange.getKey()).setRushing((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_SEND_TO_BOX_BY_PLAYER_ID:
				getPlayerResult(pGame, pModelChange.getKey()).setSendToBoxByPlayerId((String) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_SEND_TO_BOX_HALF:
				getPlayerResult(pGame, pModelChange.getKey()).setSendToBoxHalf((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_SEND_TO_BOX_REASON:
				getPlayerResult(pGame, pModelChange.getKey()).setSendToBoxReason((SendToBoxReason) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_SEND_TO_BOX_TURN:
				getPlayerResult(pGame, pModelChange.getKey()).setSendToBoxTurn((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_SERIOUS_INJURY:
				getPlayerResult(pGame, pModelChange.getKey()).setSeriousInjury((SeriousInjury) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_SERIOUS_INJURY_DECAY:
				getPlayerResult(pGame, pModelChange.getKey()).setSeriousInjuryDecay((SeriousInjury) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_TOUCHDOWNS:
				getPlayerResult(pGame, pModelChange.getKey()).setTouchdowns((Integer) pModelChange.getValue());
				return true;
			case PLAYER_RESULT_SET_TURNS_PLAYED:
				getPlayerResult(pGame, pModelChange.getKey()).setTurnsPlayed((Integer) pModelChange.getValue());
				return true;

			case TARGET_SELECTION_COMMITTED:
				if (pGame.getFieldModel().getTargetSelectionState() != null) {
					pGame.getFieldModel().getTargetSelectionState().commit(pGame);
				}
				return true;

			case TEAM_RESULT_SET_CONCEDED:
				getTeamResult(pGame, isHomeData(pModelChange)).setConceded((Boolean) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_DEDICATED_FANS_MODIFIER:
				getTeamResult(pGame, isHomeData(pModelChange)).setDedicatedFansModifier((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_FAME:
				getTeamResult(pGame, isHomeData(pModelChange)).setFame((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_BADLY_HURT_SUFFERED:
				getTeamResult(pGame, isHomeData(pModelChange)).setBadlyHurtSuffered((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_FAN_FACTOR_MODIFIER:
				getTeamResult(pGame, isHomeData(pModelChange)).setFanFactorModifier((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_PENALTY_SCORE:
				getTeamResult(pGame, isHomeData(pModelChange)).setPenaltyScore((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_PETTY_CASH_TRANSFERRED:
				getTeamResult(pGame, isHomeData(pModelChange)).setPettyCashTransferred((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_PETTY_CASH_USED:
				getTeamResult(pGame, isHomeData(pModelChange)).setPettyCashUsed((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_RAISED_DEAD:
				getTeamResult(pGame, isHomeData(pModelChange)).setRaisedDead((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_RIP_SUFFERED:
				getTeamResult(pGame, isHomeData(pModelChange)).setRipSuffered((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_SCORE:
				getTeamResult(pGame, isHomeData(pModelChange)).setScore((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_SERIOUS_INJURY_SUFFERED:
				getTeamResult(pGame, isHomeData(pModelChange)).setSeriousInjurySuffered((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_SPECTATORS:
				getTeamResult(pGame, isHomeData(pModelChange)).setSpectators((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_SPIRALLING_EXPENSES:
				getTeamResult(pGame, isHomeData(pModelChange)).setSpirallingExpenses((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_TEAM_VALUE:
				getTeamResult(pGame, isHomeData(pModelChange)).setTeamValue((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_FAN_FACTOR:
				getTeamResult(pGame, isHomeData(pModelChange)).setFanFactor((Integer) pModelChange.getValue());
				return true;
			case TEAM_RESULT_SET_WINNINGS:
				getTeamResult(pGame, isHomeData(pModelChange)).setWinnings((Integer) pModelChange.getValue());
				return true;

			case TURN_DATA_SET_APOTHECARIES:
				getTurnData(pGame, isHomeData(pModelChange)).setApothecaries((Integer) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_BLITZ_USED:
				getTurnData(pGame, isHomeData(pModelChange)).setBlitzUsed((Boolean) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_BOMB_USED:
				getTurnData(pGame, isHomeData(pModelChange)).setBombUsed((Boolean) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_FIRST_TURN_AFTER_KICKOFF:
				getTurnData(pGame, isHomeData(pModelChange)).setFirstTurnAfterKickoff((Boolean) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_FOUL_USED:
				getTurnData(pGame, isHomeData(pModelChange)).setFoulUsed((Boolean) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_HAND_OVER_USED:
				getTurnData(pGame, isHomeData(pModelChange)).setHandOverUsed((Boolean) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_LEADER_STATE:
				getTurnData(pGame, isHomeData(pModelChange)).setLeaderState((LeaderState) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_PASS_USED:
				getTurnData(pGame, isHomeData(pModelChange)).setPassUsed((Boolean) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_KTM_USED:
				getTurnData(pGame, isHomeData(pModelChange)).setKtmUsed((Boolean) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_RE_ROLLS:
				getTurnData(pGame, isHomeData(pModelChange)).setReRolls((Integer) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_RE_ROLLS_BRILLIANT_COACHING_ONE_DRIVE:
				getTurnData(pGame, isHomeData(pModelChange)).setReRollsBrilliantCoachingOneDrive((Integer) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_RE_ROLLS_PUMP_UP_THE_CROWD_ONE_DRIVE:
				getTurnData(pGame, isHomeData(pModelChange)).setReRollsPumpUpTheCrowdOneDrive((Integer) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_RE_ROLLS_SHOW_STAR_ONE_DRIVE:
				getTurnData(pGame, isHomeData(pModelChange)).setReRollShowStarOneDrive((Integer) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_RE_ROLLS_SINGLE_USE:
				getTurnData(pGame, isHomeData(pModelChange)).setSingleUseReRolls((Integer) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_RE_ROLL_USED:
				getTurnData(pGame, isHomeData(pModelChange)).setReRollUsed((Boolean) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_TURN_NR:
				getTurnData(pGame, isHomeData(pModelChange)).setTurnNr((Integer) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_TURN_STARTED:
				getTurnData(pGame, isHomeData(pModelChange)).setTurnStarted((Boolean) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_COACH_BANNED:
				getTurnData(pGame, isHomeData(pModelChange)).setCoachBanned((Boolean) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_WANDERING_APOTHECARIES:
				getTurnData(pGame, isHomeData(pModelChange)).setWanderingApothecaries((Integer) pModelChange.getValue());
				return true;
			case TURN_DATA_SET_PLAGUE_DOCTORS:
				getTurnData(pGame, isHomeData(pModelChange)).setPlagueDoctors((Integer) pModelChange.getValue());
				return true;
		}

		return false;

	}

	public ModelChange transform(ModelChange pModelChange) {

		if ((pModelChange == null) || (pModelChange.getChangeId() == null)) {
			return null;
		}

		switch (pModelChange.getChangeId()) {

			case FIELD_MODEL_ADD_BLOOD_SPOT:
				BloodSpot bloodSpot = (BloodSpot) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(bloodSpot != null) ? bloodSpot.transform() : null);
			case FIELD_MODEL_ADD_DICE_DECORATION:
			case FIELD_MODEL_REMOVE_DICE_DECORATION:
				DiceDecoration diceDecoration = (DiceDecoration) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(diceDecoration != null) ? diceDecoration.transform() : null);
			case FIELD_MODEL_ADD_FIELD_MARKER:
			case FIELD_MODEL_REMOVE_FIELD_MARKER:
				FieldMarker fieldMarker = (FieldMarker) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(fieldMarker != null) ? fieldMarker.transform() : null);
			case FIELD_MODEL_ADD_MOVE_SQUARE:
			case FIELD_MODEL_REMOVE_MOVE_SQUARE:
				MoveSquare moveSquare = (MoveSquare) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(moveSquare != null) ? moveSquare.transform() : null);
			case FIELD_MODEL_ADD_PLAYER_MARKER:
			case FIELD_MODEL_REMOVE_PLAYER_MARKER:
				PlayerMarker playerMarker = (PlayerMarker) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(playerMarker != null) ? playerMarker.transform() : null);
			case FIELD_MODEL_ADD_PUSHBACK_SQUARE:
			case FIELD_MODEL_REMOVE_PUSHBACK_SQUARE:
				PushbackSquare pushbackSquare = (PushbackSquare) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(pushbackSquare != null) ? pushbackSquare.transform() : null);
			case FIELD_MODEL_ADD_TRACK_NUMBER:
			case FIELD_MODEL_REMOVE_TRACK_NUMBER:
				TrackNumber trackNumber = (TrackNumber) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(trackNumber != null) ? trackNumber.transform() : null);
			case FIELD_MODEL_ADD_TRAP_DOOR:
			case FIELD_MODEL_REMOVE_TRAP_DOOR:
				TrapDoor trapDoor = (TrapDoor) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(), trapDoor.transform());
			case FIELD_MODEL_SET_BALL_COORDINATE:
				FieldCoordinate ballCoordinate = (FieldCoordinate) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(ballCoordinate != null) ? ballCoordinate.transform() : null);
			case FIELD_MODEL_SET_BOMB_COORDINATE:
				FieldCoordinate bombCoordinate = (FieldCoordinate) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(bombCoordinate != null) ? bombCoordinate.transform() : null);
			case FIELD_MODEL_SET_PLAYER_COORDINATE:
				FieldCoordinate playerCoordinate = (FieldCoordinate) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(playerCoordinate != null) ? playerCoordinate.transform() : null);
			case FIELD_MODEL_SET_RANGE_RULER:
				RangeRuler rangeRuler = (RangeRuler) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(rangeRuler != null) ? rangeRuler.transform() : null);

			case GAME_SET_DIALOG_PARAMETER:
				IDialogParameter dialogParameter = (IDialogParameter) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(dialogParameter != null) ? dialogParameter.transform() : null);
			case GAME_SET_HOME_FIRST_OFFENSE:
			case GAME_SET_HOME_PLAYING:
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					!((Boolean) pModelChange.getValue()));
			case GAME_SET_PASS_COORDINATE:
				FieldCoordinate passCoordinate = (FieldCoordinate) pModelChange.getValue();
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(),
					(passCoordinate != null) ? passCoordinate.transform() : null);

			case INDUCEMENT_SET_ACTIVATE_CARD:
			case INDUCEMENT_SET_ADD_AVAILABLE_CARD:
			case INDUCEMENT_SET_ADD_INDUCEMENT:
			case INDUCEMENT_SET_ADD_PRAYER:
			case INDUCEMENT_SET_REMOVE_PRAYER:
			case INDUCEMENT_SET_DEACTIVATE_CARD:
			case INDUCEMENT_SET_REMOVE_AVAILABLE_CARD:
			case INDUCEMENT_SET_REMOVE_INDUCEMENT:
				return new ModelChange(pModelChange.getChangeId(), isHomeData(pModelChange) ? ModelChange.AWAY : ModelChange.HOME,
					pModelChange.getValue());

			default:
				return new ModelChange(pModelChange.getChangeId(), pModelChange.getKey(), pModelChange.getValue());

			case TEAM_RESULT_SET_CONCEDED:
			case TEAM_RESULT_SET_DEDICATED_FANS_MODIFIER:
			case TEAM_RESULT_SET_FAME:
			case TEAM_RESULT_SET_BADLY_HURT_SUFFERED:
			case TEAM_RESULT_SET_FAN_FACTOR_MODIFIER:
			case TEAM_RESULT_SET_PENALTY_SCORE:
			case TEAM_RESULT_SET_PETTY_CASH_TRANSFERRED:
			case TEAM_RESULT_SET_PETTY_CASH_USED:
			case TEAM_RESULT_SET_RAISED_DEAD:
			case TEAM_RESULT_SET_RIP_SUFFERED:
			case TEAM_RESULT_SET_SCORE:
			case TEAM_RESULT_SET_SERIOUS_INJURY_SUFFERED:
			case TEAM_RESULT_SET_SPECTATORS:
			case TEAM_RESULT_SET_SPIRALLING_EXPENSES:
			case TEAM_RESULT_SET_TEAM_VALUE:
			case TEAM_RESULT_SET_FAN_FACTOR:
			case TEAM_RESULT_SET_WINNINGS:
			case TURN_DATA_SET_APOTHECARIES:
			case TURN_DATA_SET_PLAGUE_DOCTORS:
			case TURN_DATA_SET_WANDERING_APOTHECARIES:
			case TURN_DATA_SET_BLITZ_USED:
			case TURN_DATA_SET_BOMB_USED:
			case TURN_DATA_SET_FIRST_TURN_AFTER_KICKOFF:
			case TURN_DATA_SET_FOUL_USED:
			case TURN_DATA_SET_HAND_OVER_USED:
			case TURN_DATA_SET_LEADER_STATE:
			case TURN_DATA_SET_PASS_USED:
			case TURN_DATA_SET_KTM_USED:
			case TURN_DATA_SET_RE_ROLLS:
			case TURN_DATA_SET_RE_ROLLS_BRILLIANT_COACHING_ONE_DRIVE:
			case TURN_DATA_SET_RE_ROLLS_PUMP_UP_THE_CROWD_ONE_DRIVE:
			case TURN_DATA_SET_RE_ROLLS_SHOW_STAR_ONE_DRIVE:
			case TURN_DATA_SET_RE_ROLLS_SINGLE_USE:
			case TURN_DATA_SET_RE_ROLL_USED:
			case TURN_DATA_SET_TURN_NR:
			case TURN_DATA_SET_TURN_STARTED:
			case TURN_DATA_SET_COACH_BANNED:
				return new ModelChange(pModelChange.getChangeId(), isHomeData(pModelChange) ? ModelChange.AWAY : ModelChange.HOME,
					pModelChange.getValue());

		}

	}

	private boolean isHomeData(ModelChange pChange) {
		return ModelChange.HOME.equals(pChange.getKey());
	}

	private TeamResult getTeamResult(Game pGame, boolean pHomeData) {
		return pHomeData ? pGame.getGameResult().getTeamResultHome() : pGame.getGameResult().getTeamResultAway();
	}

	private TurnData getTurnData(Game pGame, boolean pHomeData) {
		return pHomeData ? pGame.getTurnDataHome() : pGame.getTurnDataAway();
	}

	private InducementSet getInducementSet(Game pGame, boolean pHomeData) {
		return pHomeData ? pGame.getTurnDataHome().getInducementSet() : pGame.getTurnDataAway().getInducementSet();
	}

	private PlayerResult getPlayerResult(Game pGame, String pPlayerId) {
		return pGame.getGameResult().getPlayerResult(pGame.getPlayerById(pPlayerId));
	}

}

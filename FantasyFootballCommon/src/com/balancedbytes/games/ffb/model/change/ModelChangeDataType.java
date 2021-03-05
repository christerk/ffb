package com.balancedbytes.games.ffb.model.change;

import java.util.Date;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.inducement.Inducement;
import com.balancedbytes.games.ffb.LeaderState;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerMarker;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.RangeRuler;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.TrackNumber;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogParameterFactory;
import com.balancedbytes.games.ffb.factory.CardEffectFactory;
import com.balancedbytes.games.ffb.factory.CardFactory;
import com.balancedbytes.games.ffb.factory.DialogIdFactory;
import com.balancedbytes.games.ffb.factory.GameOptionFactory;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.LeaderStateFactory;
import com.balancedbytes.games.ffb.factory.PlayerActionFactory;
import com.balancedbytes.games.ffb.factory.SendToBoxReasonFactory;
import com.balancedbytes.games.ffb.factory.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.factory.TurnModeFactory;
import com.balancedbytes.games.ffb.factory.WeatherFactory;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.option.IGameOption;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public enum ModelChangeDataType implements INamedObject {

	NULL("null"), BOOLEAN("boolean"), STRING("string"), PLAYER_ACTION("playerAction"), SKILL("skill"), LONG("long"),
	DATE("date"), TURN_MODE("turnMode"), FIELD_COORDINATE("fieldCoordinate"), DIALOG_ID("dialogId"),
	DIALOG_PARAMETER("dialogParameter"), INTEGER("integer"), PLAYER_STATE("playerState"), SERIOUS_INJURY("seriousInjury"),
	SEND_TO_BOX_REASON("sendToBoxReason"), BLOOD_SPOT("bloodSpot"), TRACK_NUMBER("trackNumber"),
	PUSHBACK_SQUARE("pushbackSquare"), MOVE_SQUARE("moveSquare"), WEATHER("weather"), RANGE_RULER("rangeRuler"),
	DICE_DECORATION("diceDecoration"), INDUCEMENT("inducement"), FIELD_MARKER("fieldMarker"),
	PLAYER_MARKER("playerMarker"), GAME_OPTION("gameOption"), CARD("card"), LEADER_STATE("leaderState"),
	CARD_EFFECT("cardEffect");

	private String fName;

	private ModelChangeDataType(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

	// JSON serialization

	public JsonValue toJsonValue(Object pValue) {
		if (pValue == null) {
			return JsonValue.NULL;
		}
		switch (this) {
		case BLOOD_SPOT:
			return ((BloodSpot) pValue).toJsonValue();
		case BOOLEAN:
			return JsonValue.valueOf((Boolean) pValue);
		case CARD:
			return UtilJson.toJsonValue((Card) pValue);
		case CARD_EFFECT:
			return UtilJson.toJsonValue((CardEffect) pValue);
		case DATE:
			return UtilJson.toJsonValue((Date) pValue);
		case DIALOG_ID:
			return UtilJson.toJsonValue((DialogId) pValue);
		case DIALOG_PARAMETER:
			return ((IDialogParameter) pValue).toJsonValue();
		case DICE_DECORATION:
			return ((DiceDecoration) pValue).toJsonValue();
		case FIELD_COORDINATE:
			return UtilJson.toJsonValue((FieldCoordinate) pValue);
		case FIELD_MARKER:
			return ((FieldMarker) pValue).toJsonValue();
		case GAME_OPTION:
			return ((IGameOption) pValue).toJsonValue();
		case INDUCEMENT:
			return ((Inducement) pValue).toJsonValue();
		case INTEGER:
			if (pValue instanceof Byte) {
				return JsonValue.valueOf(((Byte) pValue).intValue());
			} else {
				return JsonValue.valueOf((Integer) pValue);
			}
		case LEADER_STATE:
			return UtilJson.toJsonValue((LeaderState) pValue);
		case LONG:
			return JsonValue.valueOf((Long) pValue);
		case MOVE_SQUARE:
			return ((MoveSquare) pValue).toJsonValue();
		case NULL:
			return null;
		case PLAYER_ACTION:
			return UtilJson.toJsonValue((PlayerAction) pValue);
		case PLAYER_MARKER:
			return ((PlayerMarker) pValue).toJsonValue();
		case PLAYER_STATE:
			return UtilJson.toJsonValue((PlayerState) pValue);
		case PUSHBACK_SQUARE:
			return ((PushbackSquare) pValue).toJsonValue();
		case RANGE_RULER:
			return ((RangeRuler) pValue).toJsonValue();
		case SEND_TO_BOX_REASON:
			return UtilJson.toJsonValue((SendToBoxReason) pValue);
		case SERIOUS_INJURY:
			return UtilJson.toJsonValue((SeriousInjury) pValue);
		case SKILL:
			return UtilJson.toJsonValue((Skill) pValue);
		case STRING:
			return JsonValue.valueOf((String) pValue);
		case TRACK_NUMBER:
			return ((TrackNumber) pValue).toJsonValue();
		case TURN_MODE:
			return UtilJson.toJsonValue((TurnMode) pValue);
		case WEATHER:
			return UtilJson.toJsonValue((Weather) pValue);
		default:
			throw new IllegalStateException("Unknown type " + this + ".");
		}
	}

	public Object fromJsonValue(IFactorySource source, JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		switch (this) {
		case BLOOD_SPOT:
			return new BloodSpot().initFrom(source, pJsonValue);
		case BOOLEAN:
			return pJsonValue.asBoolean();
		case CARD:
			return UtilJson.toEnumWithName(source.<CardFactory>getFactory(Factory.CARD), pJsonValue);
		case CARD_EFFECT:
			return UtilJson.toEnumWithName(source.<CardEffectFactory>getFactory(Factory.CARD_EFFECT), pJsonValue);
		case DATE:
			return UtilJson.toDate(pJsonValue);
		case DIALOG_ID:
			return UtilJson.toEnumWithName(source.<DialogIdFactory>getFactory(Factory.DIALOG_ID), pJsonValue);
		case DIALOG_PARAMETER:
			return new DialogParameterFactory().forJsonValue(source, pJsonValue);
		case DICE_DECORATION:
			return new DiceDecoration().initFrom(source, pJsonValue);
		case FIELD_COORDINATE:
			return UtilJson.toFieldCoordinate(pJsonValue);
		case FIELD_MARKER:
			return new FieldMarker().initFrom(source, pJsonValue);
		case GAME_OPTION:
			return new GameOptionFactory().fromJsonValue(source, pJsonValue);
		case INDUCEMENT:
			return new Inducement().initFrom(source, pJsonValue);
		case INTEGER:
			return pJsonValue.asInt();
		case LEADER_STATE:
			return UtilJson.toEnumWithName(new LeaderStateFactory(), pJsonValue);
		case LONG:
			return pJsonValue.asLong();
		case MOVE_SQUARE:
			return new MoveSquare().initFrom(source, pJsonValue);
		case NULL:
			return null;
		case PLAYER_ACTION:
			return UtilJson.toEnumWithName(new PlayerActionFactory(), pJsonValue);
		case PLAYER_MARKER:
			return new PlayerMarker().initFrom(source, pJsonValue);
		case PLAYER_STATE:
			return UtilJson.toPlayerState(pJsonValue);
		case PUSHBACK_SQUARE:
			return new PushbackSquare().initFrom(source, pJsonValue);
		case RANGE_RULER:
			return new RangeRuler().initFrom(source, pJsonValue);
		case SEND_TO_BOX_REASON:
			return UtilJson.toEnumWithName(new SendToBoxReasonFactory(), pJsonValue);
		case SERIOUS_INJURY:
			return UtilJson.toEnumWithName(new SeriousInjuryFactory(), pJsonValue);
		case SKILL:
			return UtilJson.toEnumWithName(source.<SkillFactory>getFactory(Factory.SKILL), pJsonValue);
		case STRING:
			return pJsonValue.asString();
		case TRACK_NUMBER:
			return new TrackNumber().initFrom(source, pJsonValue);
		case TURN_MODE:
			return UtilJson.toEnumWithName(new TurnModeFactory(), pJsonValue);
		case WEATHER:
			return UtilJson.toEnumWithName(new WeatherFactory(), pJsonValue);
		default:
			throw new IllegalStateException("Unknown type " + this + ".");
		}
	}

}

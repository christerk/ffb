package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.AnimationTypeFactory;
import com.fumbbl.ffb.factory.CardFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerAnimation extends TalkHandler {
	public TalkHandlerAnimation() {
		super("/animation", 1, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	public void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {

		AnimationType animationType = gameState.getGame().getRules().<AnimationTypeFactory>getFactory(FactoryType.Factory.ANIMATION_TYPE).forName(commands[1]);
		if ((animationType == null) || (animationType == AnimationType.PASS) || (animationType == AnimationType.KICK)
			|| (animationType == AnimationType.THROW_TEAM_MATE)) {
			return;
		}
		Card card = null;
		Animation animation;
		FieldCoordinate animationCoordinate = null;
		if ((commands.length > 2) && (animationType == AnimationType.CARD) && StringTool.isProvided(commands[2])) {
			card = gameState.getGame().<CardFactory>getFactory(FactoryType.Factory.CARD).forShortName(commands[2].replaceAll("_", " "));
		}
		if (commands.length > 3) {
			try {
				animationCoordinate = new FieldCoordinate(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]));
			} catch (NumberFormatException ignored) {
			}
		}
		StringBuilder info = new StringBuilder();
		info.append("Playing Animation ").append(animationType.getName());
		if (card != null) {
			animation = new Animation(card);
			info.append(" ").append(card.getShortName());
		} else if (animationCoordinate != null) {
			animation = new Animation(animationType, animationCoordinate);
			info.append(" at ").append(animationCoordinate);
		} else {
			animation = new Animation(animationType);
		}
		info.append(".");
		server.getCommunication().sendPlayerTalk(gameState, null, info.toString());
		UtilServerGame.syncGameModel(gameState, null, animation, null);
	}
}

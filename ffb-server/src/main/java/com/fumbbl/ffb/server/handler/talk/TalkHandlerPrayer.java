package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSelectSkillParameter;
import com.fumbbl.ffb.factory.bb2020.PrayerFactory;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.PrayerHandlerFactory;
import com.fumbbl.ffb.server.inducements.bb2020.prayers.PrayerDialogSelection;
import com.fumbbl.ffb.server.inducements.bb2020.prayers.PrayerHandler;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class TalkHandlerPrayer extends TalkHandler {
	public TalkHandlerPrayer() {
		super("/prayer", 0, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {

		Game game = gameState.getGame();
		if (commands.length < 2) {
			server.getCommunication().sendPlayerTalk(gameState, null, "Prayer roll/Number missing.");
			return;
		}

		int roll;
		try {
			roll = Integer.parseInt(commands[1]);
		} catch (NumberFormatException ex) {
			server.getCommunication().sendPlayerTalk(gameState, null, commands[1] + " is not a number.");
			return;
		}

		Prayer prayer = gameState.getGame().<PrayerFactory>getFactory(FactoryType.Factory.PRAYER).forRoll(roll);
		if (prayer == null) {
			server.getCommunication().sendPlayerTalk(gameState, null, "No prayer found for " + commands[1] + " must be between 1 and 16 (inclusive).");
			return;
		}

		PrayerHandlerFactory handlerFactory = game.getFactory(FactoryType.Factory.PRAYER_HANDLER);

		Optional<PrayerHandler> foundHandler = handlerFactory.forPrayer(prayer);
		if (foundHandler.isPresent()) {
			PrayerHandler handler = foundHandler.get();
			handler.initEffect(null, gameState, team.getId());

			IDialogParameter genericParameter = game.getDialogParameter();
			UtilServerDialog.hideDialog(gameState);
			boolean requiresArgument = Arrays.asList(4, 5, 8, 16).contains(roll);
			if (commands.length < 3 && requiresArgument) {
				server.getCommunication().sendPlayerTalk(gameState, null, "Prayer " + prayer.getName() + " requires an additional parameter.");
				return;
			}
			Skill skill = null;
			String playerId = null;

			if (genericParameter instanceof DialogSelectSkillParameter) {
				DialogSelectSkillParameter dialogParameter = (DialogSelectSkillParameter) genericParameter;
				String playerIdFromDialog = dialogParameter.getPlayerId();
				Player<?> player = null;
				if (StringTool.isProvided(playerIdFromDialog)) {
					player = game.getPlayerById(playerIdFromDialog);
				}
				if (player == null) {
					server.getCommunication().sendPlayerTalk(gameState, null,
						"Could not select a random player.");
					return;
				}
				if (dialogParameter.getSkills().isEmpty()) {
					server.getCommunication().sendPlayerTalk(gameState, null,
						"Randomly selected player " + player.getName() + " already has all primary skills.");
					return;
				} else {
					playerId = playerIdFromDialog;
					String skillName = commands[2].replace("_", " ");
					Optional<Skill> foundSkill = dialogParameter.getSkills().stream().filter(s -> s.getName().equalsIgnoreCase(skillName)).findFirst();
					if (foundSkill.isPresent()) {
						skill = foundSkill.get();
						server.getCommunication().sendPlayerTalk(gameState, null,
							"Adding " + skill.getName() + " to player " + game.getPlayerById(playerIdFromDialog).getName() + ".");
					} else {
						server.getCommunication().sendPlayerTalk(gameState, null,
							"Skill " + skillName + " is not available for this player.");
						return;
					}

				}

			} else if (genericParameter instanceof DialogPlayerChoiceParameter) {
				int playerNumber;
				try {
					playerNumber = Integer.parseInt(commands[2]);

				} catch (NumberFormatException ignored) {
					server.getCommunication().sendPlayerTalk(gameState, null, commands[2] + " is not a number.");
					return;
				}
				DialogPlayerChoiceParameter dialogPlayerChoiceParameter = (DialogPlayerChoiceParameter) genericParameter;
				Optional<? extends Player<?>> foundPlayer = Arrays.stream(dialogPlayerChoiceParameter.getPlayerIds())
					.map(game::getPlayerById).filter(Objects::nonNull).filter(p -> p.getNr() == playerNumber).findFirst();

				if (foundPlayer.isPresent()) {
					playerId = foundPlayer.get().getId();
					server.getCommunication().sendPlayerTalk(gameState, null,
						"Adding effect of " + prayer.getName() + " to player " + foundPlayer.get().getName() + ".");

				} else {
					server.getCommunication().sendPlayerTalk(gameState, null, "Player with #" + commands[2] + " is not eligible for selection.");
					return;
				}
			}

			if (StringTool.isProvided(playerId)) {
				handler.applySelection(null, gameState, new PrayerDialogSelection(playerId, skill));
			} else {
				if (requiresArgument) {
					server.getCommunication().sendPlayerTalk(gameState, null,
						"No eligible players/skills");
					return;
				}
			}

			String info = "Added prayer " + prayer.getName() + " for coach " + team.getCoach() + ".";
			server.getCommunication().sendPlayerTalk(gameState, null, info);

			UtilServerGame.syncGameModel(gameState, null, null, null);

		} else {
			server.getCommunication().sendPlayerTalk(gameState, null, "No handler found for prayer " + prayer.getName());
		}
	}
}

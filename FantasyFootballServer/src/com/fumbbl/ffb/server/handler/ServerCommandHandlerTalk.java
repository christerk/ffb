package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandTalk;
import com.fumbbl.ffb.net.commands.ServerCommandTalk;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author Kalimar
 */
public class ServerCommandHandlerTalk extends ServerCommandHandler {


	private static final Pattern BRANCH_PATTERN = Pattern.compile("[-_a-zA-Z0-9]+");
	private static final String MESSAGE_COMMAND = "/message";

	protected ServerCommandHandlerTalk(FantasyFootballServer server) {
		super(server);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_TALK;
	}

	public boolean handleCommand(ReceivedCommand receivedCommand) {

		ClientCommandTalk talkCommand = (ClientCommandTalk) receivedCommand.getCommand();
		SessionManager sessionManager = getServer().getSessionManager();
		ServerCommunication communication = getServer().getCommunication();
		long gameId = sessionManager.getGameIdForSession(receivedCommand.getSession());
		GameState gameState = getServer().getGameCache().getGameStateById(gameId);
		String talk = talkCommand.getTalk();

		if (talk != null) {

			String coach = sessionManager.getCoachForSession(receivedCommand.getSession());
			if ((gameState != null) && (sessionManager.getSessionOfHomeCoach(gameId) == receivedCommand.getSession())
				|| (sessionManager.getSessionOfAwayCoach(gameId) == receivedCommand.getSession())) {
				if (isServerInTestMode() && sessionManager.isSessionDev(receivedCommand.getSession()) && talk.startsWith("/redeploy")) {
					handleReDeployCommand(talkCommand);
				} else if (isServerInTestMode() && sessionManager.isSessionDev(receivedCommand.getSession()) && talk.startsWith("/games")) {
					handleGamesCommand(receivedCommand.getSession());
				} else if (isServerInTestMode() && sessionManager.isSessionDev(receivedCommand.getSession()) && talk.startsWith(MESSAGE_COMMAND)) {
					handleMessageCommand(receivedCommand);
				} else {
					communication.sendPlayerTalk(gameState, coach, talk);
				}

			} else {
				// Spectator
				if (talk.startsWith("/aah")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_AAH);
				} else if (talk.startsWith("/boo")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_BOO);
				} else if (talk.startsWith("/cheer")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_CHEER);
				} else if (talk.startsWith("/clap")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_CLAP);
				} else if (talk.startsWith("/crickets")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_CRICKETS);
				} else if (talk.startsWith("/hurt")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_HURT);
				} else if (talk.startsWith("/laugh")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_LAUGH);
				} else if (talk.startsWith("/ooh")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_OOH);
				} else if (talk.startsWith("/shock")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_SHOCK);
				} else if (talk.startsWith("/stomp")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_STOMP);
				} else {

					ServerCommandTalk.Mode mode = ServerCommandTalk.Mode.REGULAR;
					// Spectator chat
					if (sessionManager.isSessionAdmin(receivedCommand.getSession()) && ServerCommandTalk.Mode.STAFF.findIndicator(talk)) {
						mode = ServerCommandTalk.Mode.STAFF; // takes precedence
					} else if (sessionManager.isSessionDev(receivedCommand.getSession()) && ServerCommandTalk.Mode.DEV.findIndicator(talk)) {
						mode = ServerCommandTalk.Mode.DEV;
					}

					getServer().getCommunication().sendSpectatorTalk(gameState, coach, talk, mode);
				}
			}

		}

		return true;

	}

	private void handleMessageCommand(ReceivedCommand receivedCommand) {
		String message = ((ClientCommandTalk) receivedCommand.getCommand()).getTalk();
		if (message != null && message.length() > MESSAGE_COMMAND.length()) {
			getServer().getCommunication().sendAdminMessage(new String[]{message.substring(MESSAGE_COMMAND.length()).trim()});
		}
	}

	private void handleGamesCommand(Session session) {
		String[] response = Arrays.stream(getServer().getGameCache().findActiveGames().getEntriesSorted())
			.map(entry -> entry.getTeamHomeCoach() + " vs " + entry.getTeamAwayCoach()).toArray(String[]::new);

		getServer().getCommunication().sendTalk(session, null, response);
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void handleReDeployCommand(ClientCommandTalk talkCommand) {
		String branch = getServer().getProperty(IServerProperty.SERVER_REDEPLOY_DEFAULT_BRANCH);
		String talk = talkCommand.getTalk();
		String[] commands = talk.split(" ");
		if (commands.length > 1 && BRANCH_PATTERN.matcher(commands[1]).matches()) {
			branch = commands[1];
		}

		try {
			File file = new File(getServer().getProperty(IServerProperty.SERVER_REDEPLOY_FILE));
			if (!file.exists()) {
				file.createNewFile();
			}
			file.setWritable(true);
			Files.write(Paths.get(file.toURI()), branch.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
			System.exit(Integer.parseInt(getServer().getProperty(IServerProperty.SERVER_REDEPLOY_EXIT_CODE)));
		} catch (IOException e) {
			getServer().getDebugLog().logWithOutGameId(e);
		}
	}

	private boolean isTestMode(GameState pGameState) {
		if (pGameState == null) {
			return false;
		}
		return (pGameState.getGame().isTesting()
			|| isServerInTestMode());
	}


	private void playSoundAfterCooldown(GameState pGameState, String pCoach, SoundId pSound) {
		if ((pGameState != null) && (pCoach != null) && (pSound != null)) {
			if (StringTool.isProvided(getServer().getProperty(IServerProperty.SERVER_SPECTATOR_COOLDOWN))) {
				long spectatorCooldown = Long.parseLong(getServer().getProperty(IServerProperty.SERVER_SPECTATOR_COOLDOWN));
				long currentTime = System.currentTimeMillis();
				if (currentTime > (pGameState.getSpectatorCooldownTime(pCoach) + spectatorCooldown)) {
					getServer().getCommunication().sendSound(pGameState, pSound);
					pGameState.putSpectatorCooldownTime(pCoach, currentTime);
				}
			} else {
				getServer().getCommunication().sendSound(pGameState, pSound);
			}
		}
	}




}

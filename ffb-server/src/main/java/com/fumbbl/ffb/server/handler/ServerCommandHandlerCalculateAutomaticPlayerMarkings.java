package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.Pair;
import com.fumbbl.ffb.server.marking.AutoMarkingConfig;
import com.fumbbl.ffb.server.marking.MarkerGenerator;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandCalculateAutomaticPlayerMarkings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServerCommandHandlerCalculateAutomaticPlayerMarkings extends ServerCommandHandler {

	private final MarkerGenerator markerGenerator = new MarkerGenerator();

	protected ServerCommandHandlerCalculateAutomaticPlayerMarkings(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.INTERNAL_CALCULATE_AUTOMATIC_PLAYER_MARKINGS;
	}

	@Override
	public boolean handleCommand(ReceivedCommand receivedCommand) {
		InternalServerCommandCalculateAutomaticPlayerMarkings commandCalculateAutomaticPlayerMarkings = (InternalServerCommandCalculateAutomaticPlayerMarkings) receivedCommand.getCommand();
		Game game = commandCalculateAutomaticPlayerMarkings.getGame();
		AutoMarkingConfig config = commandCalculateAutomaticPlayerMarkings.getAutoMarkingConfig();

		if (config.getMarkings().isEmpty()) {
			config.getMarkings().addAll(AutoMarkingConfig.defaults(game.getRules().getSkillFactory()));
		}

		getServer().getCommunication().sendMarkings(receivedCommand.getSession(), commandCalculateAutomaticPlayerMarkings.getIndex(), handleGame(game, config));

		return true;
	}

	private Map<String, String> handleGame(Game game, AutoMarkingConfig config) {
		Map<String, String> markings = new HashMap<>();
		Arrays.stream(game.getPlayers()).map(player -> new Pair<>(player.getId(), markerGenerator.generate(game, player, config, false))).forEach(pair -> markings.put(pair.getLeft(), pair.getRight()));
		return markings;
	}
}

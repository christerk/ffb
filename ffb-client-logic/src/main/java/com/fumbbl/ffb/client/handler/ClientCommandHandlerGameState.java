package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.util.UtilClientThrowTeamMate;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandGameState;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Kalimar
 */
public class ClientCommandHandlerGameState extends ClientCommandHandler implements IDialogCloseListener {

	private final SubHandlerGameStateMarking subHandler;

	protected ClientCommandHandlerGameState(FantasyFootballClient pClient) {
		super(pClient);
		this.subHandler = new SubHandlerGameStateMarking(pClient);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_GAME_STATE;
	}

	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {

		ServerCommandGameState gameStateCommand = (ServerCommandGameState) pNetCommand;

		Game game = subHandler.handleNetCommand(gameStateCommand);

		IconCache iconCache = getClient().getUserInterface().getIconCache();

		// update player icons and collect all icon urls needed for the game
		Set<String> iconUrls = new HashSet<>();

		addIconUrl(iconUrls, IconCache.findTeamLogoUrl(game.getTeamHome()));
		addIconUrl(iconUrls, IconCache.findTeamLogoUrl(game.getTeamAway()));

		addRosterIconUrls(iconUrls, game.getTeamHome().getRoster());
		addRosterIconUrls(iconUrls, game.getTeamAway().getRoster());

		for (Player<?> player : game.getPlayers()) {
			addIconUrl(iconUrls, PlayerIconFactory.getPortraitUrl(player));
			addIconUrl(iconUrls, PlayerIconFactory.getIconSetUrl(player));
		}

		addIconUrl(iconUrls, getClient().getProperty(IIconProperty.ZAPPEDPLAYER_ICONSET_PATH));

		// load pitches for default, basic and custom (if defined)
		addIconUrl(iconUrls,
			iconCache.buildPitchUrl(getClient().getProperty(IIconProperty.PITCH_URL_DEFAULT), Weather.NICE));
		addIconUrl(iconUrls, iconCache.buildPitchUrl(getClient().getProperty(IIconProperty.PITCH_URL_BASIC), Weather.NICE));
		String pitchUrl = game.getOptions().getOptionWithDefault(GameOptionId.PITCH_URL).getValueAsString();
		if (StringTool.isProvided(pitchUrl)) {
			addIconUrl(iconUrls, iconCache.buildPitchUrl(pitchUrl, Weather.NICE));
		}

		Set<String> iconUrlsToDownload = new HashSet<>();
		for (String iconUrl : iconUrls) {
			if (!iconCache.loadIconFromArchive(iconUrl) && !iconUrl.endsWith("/i/")) {
				iconUrlsToDownload.add(iconUrl);
			}
		}

		int nrOfIcons = iconUrlsToDownload.size();
		if (nrOfIcons > 0) {

			getClient().getClientState().showIconProgress(this, nrOfIcons);

			// preload all icon urls now
			int currentIconNr = 0;
			for (String iconUrl : iconUrlsToDownload) {
				getClient().logDebug("download " + iconUrl);
				iconCache.loadIconFromUrl(iconUrl);
				String message = String.format("Loaded icon %d of %d.", ++currentIconNr, nrOfIcons);
				getClient().getClientState().updateIconProgress(currentIconNr, message);
			}

			getClient().getClientState().hideIconProgress();

		}

		UtilClientThrowTeamMate.updateThrownPlayer(getClient());

		if (pMode == ClientCommandHandlerMode.PLAYING) {
			try {
				SwingUtilities.invokeAndWait(() -> {
					UserInterface userInterface = getClient().getUserInterface();
					userInterface.init(game.getOptions());
					getClient().updateClientState();
					userInterface.getDialogManager().updateDialog();
					userInterface.getGameMenuBar().updateMissingPlayers();
					userInterface.getGameMenuBar().updateInducements();
					userInterface.getChat().requestChatInputFocus();
				});
			} catch (InterruptedException | InvocationTargetException e) {
				getClient().logWithOutGameId(e);
			}
		}

		getClient().initRulesDependentMembers();

		return true;

	}

	public void dialogClosed(IDialog pDialog) {
		getClient().exitClient();
	}

	private void addIconUrl(Set<String> pIconUrls, String pIconUrl) {
		if (StringTool.isProvided(pIconUrl)) {
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			if (iconCache.getIconByUrl(pIconUrl, getClient().getUserInterface().getUiDimensionProvider()) == null) {
				pIconUrls.add(pIconUrl);
			}
		}
	}

	private void addRosterIconUrls(Set<String> pIconUrls, Roster pRoster) {
		for (RosterPosition position : pRoster.getPositions()) {
			addIconUrl(pIconUrls, PlayerIconFactory.getPortraitUrl(position));
			addIconUrl(pIconUrls, PlayerIconFactory.getIconSetUrl(position));
		}
	}

}

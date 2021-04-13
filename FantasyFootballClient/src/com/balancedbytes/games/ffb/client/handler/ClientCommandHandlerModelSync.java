package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.client.ClientData;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.animation.AnimationSequenceFactory;
import com.balancedbytes.games.ffb.client.animation.IAnimationListener;
import com.balancedbytes.games.ffb.client.animation.IAnimationSequence;
import com.balancedbytes.games.ffb.client.layer.FieldLayer;
import com.balancedbytes.games.ffb.client.util.UtilClientThrowTeamMate;
import com.balancedbytes.games.ffb.client.util.UtilClientTimeout;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.BlockRoll;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandModelSync;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.IGameOption;
import com.balancedbytes.games.ffb.report.IReport;
import com.balancedbytes.games.ffb.report.ReportBlockChoice;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportList;
import com.balancedbytes.games.ffb.util.StringTool;

import java.util.Collections;

/**
 * @author Kalimar
 */
public class ClientCommandHandlerModelSync extends ClientCommandHandler implements IAnimationListener {

	private ServerCommandModelSync fSyncCommand;
	private ClientCommandHandlerMode fMode;

	private FieldCoordinate fBallCoordinate;
	private FieldCoordinate fBombCoordinate;
	private FieldCoordinate fThrownPlayerCoordinate;
	private FieldCoordinate fKickedPlayerCoordinate;

	private boolean fUpdateActingPlayer;
	private boolean fUpdateTurnNr;
	private boolean fUpdateTurnMode;
	private boolean fUpdateTimeout;
	private boolean fClearSelectedPlayer;
	private boolean fReloadPitch;

	protected ClientCommandHandlerModelSync(FantasyFootballClient pClient) {
		super(pClient);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_MODEL_SYNC;
	}

	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {

		fSyncCommand = (ServerCommandModelSync) pNetCommand;
		fMode = pMode;

		Game game = getClient().getGame();

		if ((fMode == ClientCommandHandlerMode.QUEUING) || (fMode == ClientCommandHandlerMode.PLAYING)) {
			game.setGameTime(fSyncCommand.getGameTime());
			game.setTurnTime(fSyncCommand.getTurnTime());
		}

		if (fMode == ClientCommandHandlerMode.QUEUING) {
			return true;
		}

		ModelChangeList modelChangeList = fSyncCommand.getModelChanges();
		modelChangeList.applyTo(game);

		UserInterface userInterface = getClient().getUserInterface();

		if (pMode != ClientCommandHandlerMode.REPLAYING) {
			userInterface.getLog().markCommandBegin(fSyncCommand.getCommandNr());
			userInterface.getStatusReport().report(fSyncCommand.getReportList());
			userInterface.getLog().markCommandEnd(fSyncCommand.getCommandNr());
		}

		findUpdates(fSyncCommand.getModelChanges());

		handleExtraEffects(fSyncCommand.getReportList());

		Animation animation = fSyncCommand.getAnimation();
		boolean waitForAnimation = ((animation != null)
			&& ((fMode == ClientCommandHandlerMode.PLAYING) || ((fMode == ClientCommandHandlerMode.REPLAYING)
			&& getClient().getReplayer().isReplayingSingleSpeedForward())));

		// prepare for animation by hiding ball, bomb or thrown player

		if (waitForAnimation) {
			switch (animation.getAnimationType()) {
				case THROW_BOMB:
				case HAIL_MARY_BOMB:
					game.getFieldModel().setRangeRuler(null);
					fBombCoordinate = game.getFieldModel().getBombCoordinate();
					game.getFieldModel().setBombCoordinate(null);
					break;
				case PASS:
				case KICK:
				case HAIL_MARY_PASS:
					game.getFieldModel().setRangeRuler(null);
					fBallCoordinate = game.getFieldModel().getBallCoordinate();
					game.getFieldModel().setBallCoordinate(null);
					break;
				case THROW_TEAM_MATE:
					game.getFieldModel().setRangeRuler(null);
					Player<?> thrownPlayer = game.getPlayerById(animation.getThrownPlayerId());
					fThrownPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(thrownPlayer);
					game.getFieldModel().remove(thrownPlayer);
					break;
				case KICK_TEAM_MATE:
					Player<?> kickedPlayer = game.getPlayerById(animation.getThrownPlayerId());
					fKickedPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(kickedPlayer);
					game.getFieldModel().remove(kickedPlayer);
					break;
				default:
					break;
			}
		}

		updateUserinterface();

		if (waitForAnimation) {
			startAnimation(animation);
		} else {
			playSound(fSyncCommand.getSound(), fMode, true);
		}

		return !waitForAnimation;

	}

	public void animationFinished() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();

		Animation animation = fSyncCommand.getAnimation();
		switch (animation.getAnimationType()) {
			case THROW_BOMB:
			case HAIL_MARY_BOMB:
				game.getFieldModel().setBombCoordinate(fBombCoordinate);
				break;
			case PASS:
			case KICK:
			case HAIL_MARY_PASS:
				game.getFieldModel().setBallCoordinate(fBallCoordinate);
				break;
			case THROW_TEAM_MATE:
				Player<?> thrownPlayer = game.getPlayerById(animation.getThrownPlayerId());
				game.getFieldModel().setPlayerCoordinate(thrownPlayer, fThrownPlayerCoordinate);
				break;
			case KICK_TEAM_MATE:
				Player<?> kickedPlayer = game.getPlayerById(animation.getThrownPlayerId());
				game.getFieldModel().setPlayerCoordinate(kickedPlayer, fKickedPlayerCoordinate);
				break;
			default:
				break;
		}

		userInterface.getFieldComponent().refresh();
		playSound(fSyncCommand.getSound(), fMode, true);

		getClient().getCommandHandlerFactory().updateClientState(fSyncCommand, fMode); // also signals to continue

		if (fMode == ClientCommandHandlerMode.REPLAYING) {
			getClient().getReplayer().resume();
		}

	}

	private void findUpdates(ModelChangeList pModelChangeList) {

		if (pModelChangeList != null) {

			fUpdateTurnNr = false;
			fUpdateTurnMode = false;
			fUpdateActingPlayer = false;
			fUpdateTimeout = false;
			fClearSelectedPlayer = false;
			fReloadPitch = false;

			for (ModelChange modelChange : pModelChangeList.getChanges()) {
				switch (modelChange.getChangeId()) {
					case ACTING_PLAYER_MARK_SKILL_USED:
					case ACTING_PLAYER_SET_CURRENT_MOVE:
					case ACTING_PLAYER_SET_DODGING:
					case ACTING_PLAYER_SET_GOING_FOR_IT:
					case ACTING_PLAYER_SET_HAS_BLOCKED:
					case ACTING_PLAYER_SET_HAS_FED:
					case ACTING_PLAYER_SET_HAS_FOULED:
					case ACTING_PLAYER_SET_HAS_MOVED:
					case ACTING_PLAYER_SET_HAS_PASSED:
					case ACTING_PLAYER_SET_JUMPING:
					case ACTING_PLAYER_SET_PLAYER_ACTION:
					case ACTING_PLAYER_SET_PLAYER_ID:
					case ACTING_PLAYER_SET_STANDING_UP:
					case ACTING_PLAYER_SET_STRENGTH:
					case ACTING_PLAYER_SET_SUFFERING_ANIMOSITY:
					case ACTING_PLAYER_SET_SUFFERING_BLOOD_LUST:
						fUpdateActingPlayer = true;
						break;
					case TURN_DATA_SET_TURN_NR:
						fUpdateTurnNr = true;
						break;
					case GAME_SET_TIMEOUT_POSSIBLE:
						fUpdateTimeout = true;
						break;
					case GAME_SET_DEFENDER_ID:
						fClearSelectedPlayer = (modelChange.getValue() != null);
						break;
					case GAME_SET_TURN_MODE:
						fUpdateTurnMode = true;
						break;
					case GAME_OPTIONS_ADD_OPTION:
						IGameOption gameOption = (IGameOption) modelChange.getValue();
						if ((gameOption != null) && (GameOptionId.PITCH_URL == gameOption.getId())) {
							fReloadPitch = true;
						}
						break;
					default:
						break;
				}
			}

		}

	}

	private void updateUserinterface() {

		ClientData clientData = getClient().getClientData();
		UserInterface userInterface = getClient().getUserInterface();
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (fUpdateTimeout && (fMode == ClientCommandHandlerMode.PLAYING)) {
			UtilClientTimeout.showTimeoutStatus(getClient());
		}

		if (fUpdateActingPlayer) {
			clientData.setActingPlayerUpdated(true);
			userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			if ((playerCoordinate != null) && !playerCoordinate.isBoxCoordinate()) {
				userInterface.getFieldComponent().getLayerPlayers().updateBallAndPlayers(playerCoordinate, true);
			}
		}

		if (fClearSelectedPlayer) {
			clientData.setSelectedPlayer(null);
		}

		if (fUpdateTurnNr || (fUpdateTurnMode && (TurnMode.KICKOFF != game.getTurnMode()))) {
			clientData.clear();
		}

		if (fReloadPitch) {

			String pitchUrl = game.getOptions().getOptionWithDefault(GameOptionId.PITCH_URL).getValueAsString();
			if (StringTool.isProvided(pitchUrl) && !userInterface.getIconCache().loadIconFromArchive(pitchUrl)) {
				// TODO: add dialog to show pitch download ...
				userInterface.getIconCache().loadIconFromUrl(pitchUrl);
			}
			userInterface.getFieldComponent().getLayerField().init();
		}

		if (fMode == ClientCommandHandlerMode.PLAYING) {
			UtilClientThrowTeamMate.updateThrownPlayer(getClient());
			refreshFieldComponent();
			updateDialog();
			refreshSideBars();
			refreshGameMenuBar();
		}

	}

	private void handleExtraEffects(ReportList pReportList) {
		ClientData clientData = getClient().getClientData();
		for (IReport report : pReportList.getReports()) {
			if (report.getId() == ReportId.BLOCK_CHOICE) {
				ReportBlockChoice reportBlockChoice = (ReportBlockChoice) report;
				if (!reportBlockChoice.isSuppressExtraEffectHandling()) {
					BlockRoll blockRoll = new BlockRoll();
					blockRoll.setNrOfDice(Math.abs(reportBlockChoice.getNrOfDice()));
					blockRoll.setOwnChoice(reportBlockChoice.getNrOfDice() >= 0);
					blockRoll.setBlockRoll(reportBlockChoice.getBlockRoll());
					blockRoll.setSelectedIndex(reportBlockChoice.getDiceIndex());
					clientData.setBlockDiceResult(Collections.singletonList(blockRoll));
				}
			}
		}
	}

	private void startAnimation(Animation pAnimation) {
		IAnimationSequence animationSequence = AnimationSequenceFactory.getInstance().getAnimationSequence(getClient(),
			pAnimation);
		if (animationSequence != null) {
			if (fMode == ClientCommandHandlerMode.REPLAYING) {
				getClient().getReplayer().pause();
			}
			FieldLayer fieldLayerRangeRuler = getClient().getUserInterface().getFieldComponent().getLayerRangeRuler();
			animationSequence.play(fieldLayerRangeRuler, this);
		}
	}

}

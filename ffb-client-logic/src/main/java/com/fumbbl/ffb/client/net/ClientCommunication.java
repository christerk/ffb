package com.fumbbl.ffb.client.net;

import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.ConcedeGameStatus;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.TeamSetup;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.bb2020.InjuryDescription;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.handler.ClientCommandHandlerMode;
import com.fumbbl.ffb.client.util.rng.MouseEntropySource;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardType;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.kickoff.bb2020.KickoffResult;
import com.fumbbl.ffb.marking.SortMode;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.sketch.Sketch;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.INetCommandHandler;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.commands.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientCommunication implements Runnable, INetCommandHandler {

	private boolean fStopped;
	private final List<NetCommand> fCommandQueue;
	private final FantasyFootballClient fClient;

	public ClientCommunication(FantasyFootballClient pClient) {
		fClient = pClient;
		fCommandQueue = new ArrayList<>();
	}

	public void handleCommand(NetCommand pNetCommand) {
		synchronized (fCommandQueue) {
			fCommandQueue.add(pNetCommand);
			fCommandQueue.notify();
		}
	}

	public void stop() {
		if (!fStopped) {
			fStopped = true;
			synchronized (fCommandQueue) {
				fCommandQueue.notifyAll();
			}
		}
	}

	public void run() {

		while (true) {

			NetCommand netCommand;
			synchronized (fCommandQueue) {
				try {
					while (fCommandQueue.isEmpty() && !fStopped) {
						fCommandQueue.wait();
					}
				} catch (InterruptedException e) {
					break;
				}
				if (fStopped) {
					break;
				}
				netCommand = fCommandQueue.remove(0);
			}

			try {
				switch (netCommand.getId()) {
					case SERVER_PONG:
					case SERVER_TALK:
					case SERVER_SOUND:
					case SERVER_REPLAY:
					case INTERNAL_SERVER_SOCKET_CLOSED:
					case SERVER_SKETCH_ADD_COORDINATE:
					case SERVER_SKETCH_SET_COLOR:
					case SERVER_SKETCH_SET_LABEL:
					case SERVER_ADD_SKETCHES:
					case SERVER_REMOVE_SKETCHES:
					case SERVER_CLEAR_SKETCHES:
						break;
					default:
						getClient().getReplayer().add((ServerCommand) netCommand);
						break;
				}
				ClientCommandHandlerMode mode = getClient().getReplayer().isReplaying() ? ClientCommandHandlerMode.QUEUING
					: ClientCommandHandlerMode.PLAYING;
				getClient().getCommandHandlerFactory().handleNetCommand(netCommand, mode);

			} catch (Exception e) {
				getClient().logWithOutGameId(e);
				throw e;
			}
		}

	}

	protected void send(ClientCommand clientCommand) {
		if (clientCommand == null) {
			return;
		}
		try {
			// add entropy payload if available
			MouseEntropySource entropySource = getClient().getUserInterface().getMouseEntropySource();
			if (entropySource.hasEnoughEntropy()) {
				clientCommand.setEntropy(entropySource.getEntropy());
			}
			// send command
			getClient().getCommandEndpoint().send(clientCommand);
		} catch (IOException pIoException) {
			throw new FantasyFootballException(pIoException);
		}
	}

	public void sendDebugClientState(ClientStateId pClientStateId) {
		send(new ClientCommandDebugClientState(pClientStateId));
	}

	public void sendJoin(String pCoach, String pPassword, long pGameId, String pGameName, String pTeamId,
											 String pTeamName) {
		ClientCommandJoin joinCommand = new ClientCommandJoin(getClient().getMode());
		joinCommand.setCoach(pCoach);
		joinCommand.setPassword(pPassword);
		joinCommand.setGameId(pGameId);
		joinCommand.setGameName(pGameName);
		joinCommand.setTeamId(pTeamId);
		joinCommand.setTeamName(pTeamName);
		send(joinCommand);
	}

	public void sendJourneymen(String[] pPositionsIds, int[] pSlots) {
		send(new ClientCommandJourneymen(pPositionsIds, pSlots));
	}

	public void sendTalk(String pTalk) {
		send(new ClientCommandTalk(pTalk));
	}

	public void sendPasswordChallenge() {
		send(new ClientCommandPasswordChallenge(getClient().getParameters().getCoach()));
	}

	public void sendPing(long timestamp) {
		send(new ClientCommandPing(timestamp));
	}

	public void sendSetupPlayer(Player<?> pPlayer, FieldCoordinate pCoordinate) {
		send(new ClientCommandSetupPlayer(pPlayer.getId(), pCoordinate));
	}

	public void sendTouchback(FieldCoordinate pBallCoordinate) {
		send(new ClientCommandTouchback(pBallCoordinate));
	}

	public void sendPlayerMove(String pActingPlayerId, FieldCoordinate pCoordinateFrom,
														 FieldCoordinate[] pCoordinatesTo, String ballAndChainRrSetting) {
		send(new ClientCommandMove(pActingPlayerId, pCoordinateFrom, pCoordinatesTo, ballAndChainRrSetting));
	}

	public void sendPlayerBlitzMove(String pActingPlayerId, FieldCoordinate pCoordinateFrom,
																	FieldCoordinate[] pCoordinatesTo) {
		send(new ClientCommandBlitzMove(pActingPlayerId, pCoordinateFrom, pCoordinatesTo));
	}

	public void sendTargetSelected(String selectedPlayerId) {
		send(new ClientCommandTargetSelected(selectedPlayerId));
	}

	public void sendStartGame() {
		send(new ClientCommandStartGame());
	}

	public void sendEndTurn(TurnMode turnMode) {
		getClient().logWithOutGameId(new Exception("Debug Exception"));
		send(new ClientCommandEndTurn(turnMode, null));
	}

	public void sendEndTurn(TurnMode turnMode, Team team, FieldModel fieldModel) {
		getClient().logWithOutGameId(new Exception("Debug Exception"));
		send(new ClientCommandEndTurn(turnMode, playerCoordinates(team, fieldModel)));
	}

	public void sendConfirm() {
		send(new ClientCommandConfirm());
	}

	public void sendCloseSession() {
		send(new ClientCommandCloseSession());
	}

	public void sendConcedeGame(ConcedeGameStatus pStatus) {
		send(new ClientCommandConcedeGame(pStatus));
	}

	public void sendIllegalProcedure() {
		send(new ClientCommandIllegalProcedure());
	}

	public void sendRequestVersion() {
		send(new ClientCommandRequestVersion());
	}

	public void sendCoinChoice(boolean pChoiceHeads) {
		send(new ClientCommandCoinChoice(pChoiceHeads));
	}

	public void sendReceiveChoice(boolean pChoiceReceive) {
		send(new ClientCommandReceiveChoice(pChoiceReceive));
	}

	public void sendPlayerChoice(PlayerChoiceMode pMode, Player<?>[] pPlayers) {
		send(new ClientCommandPlayerChoice(pMode, pPlayers));
	}

	public void sendPettyCash(int pPettyCash) {
		send(new ClientCommandPettyCash(pPettyCash));
	}

	public void sendActingPlayer(Player<?> pPlayer, PlayerAction pPlayerAction, boolean jumping) {
		String playerId = (pPlayer != null) ? pPlayer.getId() : null;
		send(new ClientCommandActingPlayer(playerId, pPlayerAction, jumping));
	}

	public void sendUseReRoll(ReRolledAction pReRolledAction, ReRollSource pReRollSource) {
		send(new ClientCommandUseReRoll(pReRolledAction, pReRollSource));
	}

	public void sendUseProReRollForBlock(int proIndex) {
		send(new ClientCommandUseProReRollForBlock(proIndex));
	}

	public void sendUseConsummateReRollForBlock(int proIndex) {
		send(new ClientCommandUseConsummateReRollForBlock(proIndex));
	}

	public void sendUseSingleBlockDieReRollForBlock(int index) {
		send(new ClientCommandUseSingleBlockDieReRoll(index));
	}

	public void sendUseMultiBlockDiceReRoll(int[] indexes) {
		send(new ClientCommandUseMultiBlockDiceReRoll(indexes));
	}

	public void sendUseSkill(Skill pSkill, boolean pSkillUsed, String playerId) {
		sendUseSkill(pSkill, pSkillUsed, playerId, null);
	}

	public void sendUseSkill(Skill pSkill, boolean pSkillUsed, String playerId, ReRolledAction reRolledAction) {
		sendUseSkill(pSkill, pSkillUsed, playerId, reRolledAction, false);
	}

	public void sendUseSkill(Skill pSkill, boolean pSkillUsed, String playerId, ReRolledAction reRolledAction, boolean neverUse) {
		send(new ClientCommandUseSkill(pSkill, pSkillUsed, playerId, reRolledAction, neverUse));
	}

	public void sendUseSkill(Skill pSkill, boolean pSkillUsed, String playerId, boolean neverUse) {
		sendUseSkill(pSkill, pSkillUsed, playerId, null, neverUse);
	}


	public void sendUseWisdom() {
		send(new ClientCommandUseTeamMatesWisdom());
	}

	public void sendKickoff(FieldCoordinate pBallCoordinate) {
		send(new ClientCommandKickoff(pBallCoordinate));
	}

	public void sendHandOver(String pActingPlayerId, Player<?> pCatcher) {
		String catcherId = (pCatcher != null) ? pCatcher.getId() : null;
		send(new ClientCommandHandOver(pActingPlayerId, catcherId));
	}

	public void sendGaze(String pActingPlayerId, Player<?> pVictim) {
		String victimId = (pVictim != null) ? pVictim.getId() : null;
		send(new ClientCommandGaze(pActingPlayerId, victimId));
	}

	public void sendPass(String pActingPlayerId, FieldCoordinate pTargetCoordinate) {
		send(new ClientCommandPass(pActingPlayerId, pTargetCoordinate));
	}

	public void sendBlock(String pActingPlayerId, Player<?> pDefender, boolean pUsingStab, boolean usingChainsaw,
												boolean usingVomit, boolean usingBreatheFire) {
		String defenderId = (pDefender != null) ? pDefender.getId() : null;
		send(new ClientCommandBlock(pActingPlayerId, defenderId, pUsingStab, usingChainsaw, usingVomit, usingBreatheFire));
	}

	public void sendFoul(String pActingPlayerId, Player<?> pDefender, boolean usingChainsaw) {
		String defenderId = (pDefender != null) ? pDefender.getId() : null;
		send(new ClientCommandFoul(pActingPlayerId, defenderId, usingChainsaw));
	}

	public void sendBlockChoice(int pDiceIndex) {
		send(new ClientCommandBlockChoice(pDiceIndex));
	}

	public void sendUseInducement(InducementType pInducement) {
		send(new ClientCommandUseInducement(pInducement));
	}

	public void sendUseInducement(Card pCard) {
		send(new ClientCommandUseInducement(pCard));
	}

	public void sendUseInducement(InducementType pInducement, String pPlayerId) {
		send(new ClientCommandUseInducement(pInducement, pPlayerId));
	}

	public void sendUseInducement(InducementType pInducement, String[] pPlayerIds) {
		send(new ClientCommandUseInducement(pInducement, pPlayerIds));
	}

	public void sendArgueTheCall(String playerId) {
		send(new ClientCommandArgueTheCall(playerId));
	}

	public void sendArgueTheCall(String[] playerIds) {
		send(new ClientCommandArgueTheCall(playerIds));
	}

	public void sendUseApothecaries(List<InjuryDescription> injuryDescriptions) {
		send(new ClientCommandUseApothecaries(injuryDescriptions));
	}

	public void sendUseIgors(List<InjuryDescription> injuryDescriptions) {
		send(new ClientCommandUseIgors(injuryDescriptions));
	}

	public void sendPushback(Pushback pPushback) {
		send(new ClientCommandPushback(pPushback));
	}

	public void sendFollowupChoice(boolean pFollowupChoice) {
		send(new ClientCommandFollowupChoice(pFollowupChoice));
	}

	public void sendInterceptorChoice(Player<?> pInterceptor, Skill interceptionSkill) {
		String interceptorId = (pInterceptor != null) ? pInterceptor.getId() : null;
		send(new ClientCommandInterceptorChoice(interceptorId, interceptionSkill));
	}

	public void sendTeamSetupLoad(String pSetupName) {
		send(new ClientCommandTeamSetupLoad(pSetupName));
	}

	public void sendTeamSetupDelete(String pSetupName) {
		send(new ClientCommandTeamSetupDelete(pSetupName));
	}

	public void sendTeamSetupSave(TeamSetup pTeamSetup) {
		send(new ClientCommandTeamSetupSave(pTeamSetup.getName(), pTeamSetup.getPlayerNumbers(),
			pTeamSetup.getCoordinates()));
	}

	public void sendUseApothecary(String pPlayerId, boolean pApothecaryUsed, ApothecaryType apothecaryType) {
		send(new ClientCommandUseApothecary(pPlayerId, pApothecaryUsed, apothecaryType));
	}

	public void sendApothecaryChoice(String pPlayerId, PlayerState pPlayerState, SeriousInjury pSeriousInjury, PlayerState oldPlayerState) {
		send(new ClientCommandApothecaryChoice(pPlayerId, pPlayerState, pSeriousInjury, oldPlayerState));
	}

	public void sendUserSettings(CommonProperty[] pSettingNames, String[] pSettingValues) {
		send(new ClientCommandUserSettings(pSettingNames, pSettingValues));
	}

	public void sendReplay(long pGameId, int pReplayToCommandNr, String coach) {
		send(new ClientCommandReplay(pGameId, pReplayToCommandNr, coach));
	}

	public void sendThrowTeamMate(String pActingPlayerId, FieldCoordinate pTargetCoordinate) {
		send(new ClientCommandThrowTeamMate(pActingPlayerId, pTargetCoordinate));
	}

	public void sendThrowTeamMate(String pActingPlayerId, FieldCoordinate pTargetCoordinate, boolean kicked) {
		send(new ClientCommandThrowTeamMate(pActingPlayerId, pTargetCoordinate, kicked));
	}

	public void sendKickTeamMate(String pActingPlayerId, String pPlayerId, int numDice) {
		send(new ClientCommandKickTeamMate(pActingPlayerId, pPlayerId, numDice));
	}

	public void sendThrowTeamMate(String pActingPlayerId, String pPlayerId) {
		send(new ClientCommandThrowTeamMate(pActingPlayerId, pPlayerId));
	}

	public void sendThrowTeamMate(String pActingPlayerId, String pPlayerId, boolean kicked) {
		send(new ClientCommandThrowTeamMate(pActingPlayerId, pPlayerId, kicked));
	}

	public void sendSwoop(String pActingPlayerId, FieldCoordinate pTargetCoordinate) {
		send(new ClientCommandSwoop(pActingPlayerId, pTargetCoordinate));
	}

	public void sendBuyInducements(String pTeamId, int pAvailableGold, InducementSet pInducementSet,
																 String[] pStarPlayerPositionIds, String[] pMercenaryPositionIds, Skill[] pMercenarySkills,
																 String[] staffPositionIds) {
		send(new ClientCommandBuyInducements(pTeamId, pAvailableGold, pInducementSet, pStarPlayerPositionIds,
			pMercenaryPositionIds, pMercenarySkills, staffPositionIds));
	}

	public void sendBuyCard(CardType pType) {
		send(new ClientCommandBuyCard(pType));
	}

	public void sendSetMarker(String pPlayerId, String pText) {
		send(new ClientCommandSetMarker(pPlayerId, pText));
	}

	public void sendSetMarker(FieldCoordinate pCoordinate, String pText) {
		send(new ClientCommandSetMarker(pCoordinate, pText));
	}

	public void sendWizardSpell(SpecialEffect pWizardSpell, FieldCoordinate pCoordinate) {
		send(new ClientCommandWizardSpell(pWizardSpell, pCoordinate));
	}

	public void sendCardSelection(ClientCommandSelectCardToBuy.Selection selection) {
		send(new ClientCommandSelectCardToBuy(selection));
	}

	public void sendSetBlockTarget(String playerId, BlockKind kind) {
		send(new ClientCommandSetBlockTargetSelection(playerId, kind));
	}

	public void sendUnsetBlockTarget(String playerId) {
		send(new ClientCommandUnsetBlockTargetSelection(playerId));
	}

	public void sendBlockTargets(List<BlockTarget> blockTargets) {
		send(new ClientCommandSynchronousMultiBlock(blockTargets));
	}

	public void sendUseReRollForTarget(ReRolledAction reRolledAction, ReRollSource reRollSource, String targetId) {
		send(new ClientCommandUseReRollForTarget(reRolledAction, reRollSource, targetId));
	}

	public void sendBlockOrReRollChoiceForTarget(String targetId, int selectedIndex, ReRollSource reRollSource, int proIndex) {
		send(new ClientCommandBlockOrReRollChoiceForTarget(targetId, selectedIndex, proIndex, reRollSource));
	}

	public void sendPileDriver(String playerId) {
		send(new ClientCommandPileDriver(playerId));
	}

	public void sendUseChainsaw(boolean useChainsaw) {
		send(new ClientCommandUseChainsaw(useChainsaw));
	}

	public void sendUseBrawler(String targetId) {
		send(new ClientCommandUseBrawler(targetId));
	}

	public void sendFieldCoordinate(FieldCoordinate fieldCoordinate) {
		send(new ClientCommandFieldCoordinate(fieldCoordinate));
	}

	public void sendUseFumblerooskie() {
		send(new ClientCommandUseFumblerooskie());
	}

	public void sendSkillSelection(String playerId, Skill skill) {
		send(new ClientCommandSkillSelection(playerId, skill));
	}

	public void sendThrowKeg(Player<?> player) {
		send(new ClientCommandThrowKeg(player.getId()));
	}

	public void sendSelectedWeather(int modifier, String weatherName) {
		send(new ClientCommandSelectWeather(modifier, weatherName));
	}

	public void sendUpdatePlayerMarkings(boolean auto, SortMode sortMode) {
		send(new ClientCommandUpdatePlayerMarkings(auto, sortMode));
	}

	public void sendKickOffResultChoice(KickoffResult kickoffResult) {
		send(new ClientCommandKickOffResultChoice(kickoffResult));
	}

	public void sendChangeBloodlustAction(boolean change) {
		send(new ClientCommandBloodlustAction(change));
	}

	public void sendLoadPlayerMarkings(int index, Game game, String coach) {
		send(new ClientCommandLoadAutomaticPlayerMarkings(index, game, coach));
	}

	public void sendReplayState(int commandNr, int speed, boolean running, boolean forward, boolean skip) {
		send(new ClientCommandReplayStatus(commandNr, speed, running, forward, skip));
	}

	public void sendJoinReplay(String replayName, String coach, long gameId) {
		send(new ClientCommandJoinReplay(replayName, coach, gameId));
	}

	public void sendClearSketches() {
		send(new ClientCommandClearSketches());
	}

	public void sendRemoveSketches(List<String> ids) {
		send(new ClientCommandRemoveSketches(ids));
	}

	public void sendAddSketch(Sketch sketch) {
		send(new ClientCommandAddSketch(sketch));
	}

	public void sendSketchAddCoordinate(String sketchId, FieldCoordinate coordinate) {
		send(new ClientCommandSketchAddCoordinate(sketchId, coordinate));
	}

	public void sendSketchSetColor(List<String> sketchIds, int rgb) {
		send(new ClientCommandSketchSetColor(sketchIds, rgb));
	}

	public void sendSketchSetLabel(List<String> sketchId, String label) {
		send(new ClientCommandSketchSetLabel(sketchId, label));
	}

	public void sendTransferReplayControl(String coach) {
		send(new ClientCommandTransferReplayControl(coach));
	}

	public void sendPreventFromSketching(String coach, boolean prevent) {
		send(new ClientCommandSetPreventSketching(coach, prevent));
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	private Map<String, FieldCoordinate> playerCoordinates(Team team, FieldModel fieldModel) {
		Map<String, FieldCoordinate> playerCoordinates = new HashMap<>();
		for (Player<?> player : team.getPlayers()) {
			PlayerState playerState = fieldModel.getPlayerState(player);
			if (playerState.canBeMovedDuringSetup()) {
				playerCoordinates.put(player.getId(), fieldModel.getPlayerCoordinate(player));
			}
		}

		return playerCoordinates;
	}
}

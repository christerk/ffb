package com.fumbbl.ffb.client.net;

import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.ClientStateId;
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
import com.fumbbl.ffb.model.BlockKind;
import com.fumbbl.ffb.model.BlockTarget;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.INetCommandHandler;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.commands.ClientCommand;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandApothecaryChoice;
import com.fumbbl.ffb.net.commands.ClientCommandArgueTheCall;
import com.fumbbl.ffb.net.commands.ClientCommandBlitzMove;
import com.fumbbl.ffb.net.commands.ClientCommandBlock;
import com.fumbbl.ffb.net.commands.ClientCommandBlockChoice;
import com.fumbbl.ffb.net.commands.ClientCommandBlockOrReRollChoiceForTarget;
import com.fumbbl.ffb.net.commands.ClientCommandBuyCard;
import com.fumbbl.ffb.net.commands.ClientCommandBuyInducements;
import com.fumbbl.ffb.net.commands.ClientCommandCloseSession;
import com.fumbbl.ffb.net.commands.ClientCommandCoinChoice;
import com.fumbbl.ffb.net.commands.ClientCommandConcedeGame;
import com.fumbbl.ffb.net.commands.ClientCommandConfirm;
import com.fumbbl.ffb.net.commands.ClientCommandDebugClientState;
import com.fumbbl.ffb.net.commands.ClientCommandEndTurn;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
import com.fumbbl.ffb.net.commands.ClientCommandFollowupChoice;
import com.fumbbl.ffb.net.commands.ClientCommandFoul;
import com.fumbbl.ffb.net.commands.ClientCommandGaze;
import com.fumbbl.ffb.net.commands.ClientCommandHandOver;
import com.fumbbl.ffb.net.commands.ClientCommandIllegalProcedure;
import com.fumbbl.ffb.net.commands.ClientCommandInterceptorChoice;
import com.fumbbl.ffb.net.commands.ClientCommandJoin;
import com.fumbbl.ffb.net.commands.ClientCommandJourneymen;
import com.fumbbl.ffb.net.commands.ClientCommandKickTeamMate;
import com.fumbbl.ffb.net.commands.ClientCommandKickoff;
import com.fumbbl.ffb.net.commands.ClientCommandMove;
import com.fumbbl.ffb.net.commands.ClientCommandPass;
import com.fumbbl.ffb.net.commands.ClientCommandPasswordChallenge;
import com.fumbbl.ffb.net.commands.ClientCommandPettyCash;
import com.fumbbl.ffb.net.commands.ClientCommandPileDriver;
import com.fumbbl.ffb.net.commands.ClientCommandPing;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandPushback;
import com.fumbbl.ffb.net.commands.ClientCommandReceiveChoice;
import com.fumbbl.ffb.net.commands.ClientCommandReplay;
import com.fumbbl.ffb.net.commands.ClientCommandRequestVersion;
import com.fumbbl.ffb.net.commands.ClientCommandSelectCardToBuy;
import com.fumbbl.ffb.net.commands.ClientCommandSelectWeather;
import com.fumbbl.ffb.net.commands.ClientCommandSetBlockTargetSelection;
import com.fumbbl.ffb.net.commands.ClientCommandSetMarker;
import com.fumbbl.ffb.net.commands.ClientCommandSetupPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandSkillSelection;
import com.fumbbl.ffb.net.commands.ClientCommandStartGame;
import com.fumbbl.ffb.net.commands.ClientCommandSwoop;
import com.fumbbl.ffb.net.commands.ClientCommandSynchronousMultiBlock;
import com.fumbbl.ffb.net.commands.ClientCommandTalk;
import com.fumbbl.ffb.net.commands.ClientCommandTargetSelected;
import com.fumbbl.ffb.net.commands.ClientCommandTeamSetupDelete;
import com.fumbbl.ffb.net.commands.ClientCommandTeamSetupLoad;
import com.fumbbl.ffb.net.commands.ClientCommandTeamSetupSave;
import com.fumbbl.ffb.net.commands.ClientCommandThrowKeg;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.net.commands.ClientCommandTouchback;
import com.fumbbl.ffb.net.commands.ClientCommandUnsetBlockTargetSelection;
import com.fumbbl.ffb.net.commands.ClientCommandUseApothecaries;
import com.fumbbl.ffb.net.commands.ClientCommandUseApothecary;
import com.fumbbl.ffb.net.commands.ClientCommandUseBrawler;
import com.fumbbl.ffb.net.commands.ClientCommandUseChainsaw;
import com.fumbbl.ffb.net.commands.ClientCommandUseConsummateReRollForBlock;
import com.fumbbl.ffb.net.commands.ClientCommandUseFumblerooskie;
import com.fumbbl.ffb.net.commands.ClientCommandUseIgors;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
import com.fumbbl.ffb.net.commands.ClientCommandUseProReRollForBlock;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRollForTarget;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.net.commands.ClientCommandUseTeamMatesWisdom;
import com.fumbbl.ffb.net.commands.ClientCommandUserSettings;
import com.fumbbl.ffb.net.commands.ClientCommandWizardSpell;
import com.fumbbl.ffb.net.commands.ServerCommand;

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

			switch (netCommand.getId()) {
				case SERVER_PONG:
				case SERVER_TALK:
				case SERVER_SOUND:
				case SERVER_REPLAY:
				case INTERNAL_SERVER_SOCKET_CLOSED:
					break;
				default:
					getClient().getReplayer().add((ServerCommand) netCommand);
					break;
			}
			ClientCommandHandlerMode mode = getClient().getReplayer().isReplaying() ? ClientCommandHandlerMode.QUEUING
				: ClientCommandHandlerMode.PLAYING;
			getClient().getCommandHandlerFactory().handleNetCommand(netCommand, mode);

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
	                           FieldCoordinate[] pCoordinatesTo) {
		send(new ClientCommandMove(pActingPlayerId, pCoordinateFrom, pCoordinatesTo));
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
		send(new ClientCommandEndTurn(turnMode, null));
	}

	public void sendEndTurn(TurnMode turnMode, Team team, FieldModel fieldModel) {
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

	public void sendBlock(String pActingPlayerId, Player<?> pDefender, boolean pUsingStab, boolean usingChainsaw, boolean usingVomit) {
		String defenderId = (pDefender != null) ? pDefender.getId() : null;
		send(new ClientCommandBlock(pActingPlayerId, defenderId, pUsingStab, usingChainsaw, usingVomit));
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

	public void sendInterceptorChoice(Player<?> pInterceptor) {
		String interceptorId = (pInterceptor != null) ? pInterceptor.getId() : null;
		send(new ClientCommandInterceptorChoice(interceptorId));
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

	public void sendUserSettings(String[] pSettingNames, String[] pSettingValues) {
		send(new ClientCommandUserSettings(pSettingNames, pSettingValues));
	}

	public void sendReplay(long pGameId, int pReplayToCommandNr) {
		send(new ClientCommandReplay(pGameId, pReplayToCommandNr));
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

package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.BlockLogicExtension;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;

import java.util.*;
import java.util.stream.Collectors;

public class SynchronousMultiBlockLogicModule extends LogicModule {

	private final Map<String, BlockKind> selectedPlayers = new HashMap<>();
	private final Map<String, PlayerState> originalPlayerStates = new HashMap<>();

	private final BlockLogicExtension extension;

	public SynchronousMultiBlockLogicModule(FantasyFootballClient pClient) {
		super(pClient);
		this.extension = new BlockLogicExtension(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.SYNCHRONOUS_MULTI_BLOCK;
	}

	@Override
	public void setUp() {
		super.setUp();
		selectedPlayers.clear();
		originalPlayerStates.clear();
	}

	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() == player) {
			return InteractionResult.selectAction(actionContext(actingPlayer));
		} else {
			return handlePlayerSelection(player);
		}
	}

	public InteractionResult handlePlayerSelection(Player<?> player) {
		if (selectedPlayers.containsKey(player.getId())) {
			selectedPlayers.remove(player.getId());
			originalPlayerStates.remove(player.getId());
			client.getCommunication().sendUnsetBlockTarget(player.getId());
			return InteractionResult.handled();
		} else {
			return handleDefenderSelection(player);
		}
	}

	private InteractionResult handleDefenderSelection(Player<?> defender) {
		if (defender == null) {
			return InteractionResult.ignore();
		}
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (extension.isBlockable(game, defender)) {
			FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(defender);
			if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.providesMultipleBlockAlternative)) {
				return extension.playerInteraction(defender, false, true);
			} else if (game.getFieldModel().getDiceDecoration(defenderCoordinate) != null) {
				selectPlayer(defender, BlockKind.BLOCK);
				return InteractionResult.handled();
			}
		}
		return InteractionResult.ignore();
	}

	private void selectPlayer(Player<?> player, BlockKind kind) {
		if (selectedPlayers.size() < 2) {
			selectedPlayers.put(player.getId(), kind);
			originalPlayerStates.put(player.getId(), client.getGame().getFieldModel().getPlayerState(player));
			client.getCommunication().sendSetBlockTarget(player.getId(), kind);
			sendIfSelectionComplete();
		}
	}

	private void sendIfSelectionComplete() {
		if (selectedPlayers.size() == 2) {
			List<BlockTarget> blockTargets = selectedPlayers.entrySet().stream()
				.map(entry -> new BlockTarget(entry.getKey(), entry.getValue(), originalPlayerStates.get(entry.getKey())))
				.sorted(Comparator.comparing(BlockTarget::getPlayerId))
				.collect(Collectors.toList());
			client.getCommunication().sendBlockTargets(blockTargets);
		}
	}

	public InteractionResult playerPeek(Player<?> pPlayer) {
		if (extension.isBlockable(client.getGame(), pPlayer)) {
			return InteractionResult.perform();
		} else {
			return InteractionResult.reset();
		}
	}


	@Override
	public void endTurn() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		perform(actingPlayer.getPlayer(), ClientAction.END_MOVE);
		client.getCommunication().sendEndTurn(client.getGame().getTurnMode());
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.MOVE);
			add(ClientAction.END_MOVE);
			add(ClientAction.BLOCK);
			add(ClientAction.STAB);
			add(ClientAction.TREACHEROUS);
			add(ClientAction.WISDOM);
			add(ClientAction.RAIDING_PARTY);
			add(ClientAction.LOOK_INTO_MY_EYES);
			add(ClientAction.BALEFUL_HEX);
			add(ClientAction.BLACK_INK);
			add(ClientAction.CATCH_OF_THE_DAY);
			add(ClientAction.THEN_I_STARTED_BLASTIN);
		}};
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		ActionContext actionContext = new ActionContext();
		if (actingPlayer.isSufferingBloodLust()) {
			actionContext.add(ClientAction.MOVE);
		}
		actionContext.add(ClientAction.END_MOVE);
		if (isTreacherousAvailable(actingPlayer)) {
			actionContext.add(ClientAction.TREACHEROUS);
		}
		if (isWisdomAvailable(actingPlayer)) {
			actionContext.add(ClientAction.WISDOM);
		}
		if (isRaidingPartyAvailable(actingPlayer)) {
			actionContext.add(ClientAction.RAIDING_PARTY);
		}
		if (isLookIntoMyEyesAvailable(actingPlayer)) {
			actionContext.add(ClientAction.LOOK_INTO_MY_EYES);
		}
		if (isBalefulHexAvailable(actingPlayer)) {
			actionContext.add(ClientAction.BALEFUL_HEX);
		}
		if (isBlackInkAvailable(actingPlayer)) {
			actionContext.add(ClientAction.BLACK_INK);
		}
		if (isCatchOfTheDayAvailable(actingPlayer)) {
			actionContext.add(ClientAction.CATCH_OF_THE_DAY);
		}
		if (isThenIStartedBlastinAvailable(actingPlayer)) {
			actionContext.add(ClientAction.THEN_I_STARTED_BLASTIN);
		}
		return actionContext;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		if (player != null) {
			ClientCommunication communication = client.getCommunication();
			switch (action) {
				case END_MOVE:
					selectedPlayers.keySet().forEach(communication::sendUnsetBlockTarget);
					selectedPlayers.clear();
					communication.sendActingPlayer(null, null, false);
					break;
				case BLOCK:
					selectPlayer(player, BlockKind.BLOCK);
					break;
				case STAB:
					selectPlayer(player, BlockKind.STAB);
					break;
				case TREACHEROUS:
					if (isTreacherousAvailable(player)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				case WISDOM:
					if (isWisdomAvailable(player)) {
						communication.sendUseWisdom();
					}
					break;
				case RAIDING_PARTY:
					if (isRaidingPartyAvailable(player)) {
						Skill raidingSkill = player.getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
						communication.sendUseSkill(raidingSkill, true, player.getId());
					}
					break;
				case LOOK_INTO_MY_EYES:
					if (isLookIntoMyEyesAvailable(player)) {
						UtilCards.getUnusedSkillWithProperty(player, NamedProperties.canStealBallFromOpponent)
							.ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, player.getId()));
					}
					break;
				case BALEFUL_HEX:
					if (isBalefulHexAvailable(player)) {
						Skill balefulSkill = player.getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
						communication.sendUseSkill(balefulSkill, true, player.getId());
					}
					break;
				case BLACK_INK:
					if (isBlackInkAvailable(player)) {
						Skill blackInkSkill = player.getSkillWithProperty(NamedProperties.canGazeAutomatically);
						communication.sendUseSkill(blackInkSkill, true, player.getId());
					}
					break;
				case CATCH_OF_THE_DAY:
					if (isCatchOfTheDayAvailable(player)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canGetBallOnGround);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				case THEN_I_STARTED_BLASTIN:
					if (isThenIStartedBlastinAvailable(player)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canBlastRemotePlayer);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				default:
					break;
			}
		}
	}
}

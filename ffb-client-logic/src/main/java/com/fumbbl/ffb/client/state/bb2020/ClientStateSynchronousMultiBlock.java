package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.client.util.UtilClientStateBlocking;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.BlockKind;
import com.fumbbl.ffb.model.BlockTarget;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientStateSynchronousMultiBlock extends ClientState {

	private final Map<String, BlockKind> selectedPlayers = new HashMap<>();
	private final Map<String, PlayerState> originalPlayerStates = new HashMap<>();

	public ClientStateSynchronousMultiBlock(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.SYNCHRONOUS_MULTI_BLOCK;
	}

	public void initUI() {
		super.initUI();
		selectedPlayers.clear();
		originalPlayerStates.clear();
	}

	protected void clickOnPlayer(Player<?> player) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() == player) {
			createAndShowPopupMenuForBlockingPlayer();
		} else {
			handlePlayerSelection(player);
		}
	}

	private void handlePlayerSelection(Player<?> player) {
		if (selectedPlayers.containsKey(player.getId())) {
			selectedPlayers.remove(player.getId());
			originalPlayerStates.remove(player.getId());
			getClient().getCommunication().sendUnsetBlockTarget(player.getId());
		} else {
			showPopupOrBlockPlayer(player);
		}
	}

	private void showPopupOrBlockPlayer(Player<?> defender) {
		if (defender == null) {
			return;
		}
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (UtilPlayer.isBlockable(game, defender)) {
			FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(defender);
			if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.providesMultipleBlockAlternative)) {
				UtilClientStateBlocking.createAndShowBlockOptionsPopupMenu(this, actingPlayer.getPlayer(), defender, true);
			} else if (game.getFieldModel().getDiceDecoration(defenderCoordinate) != null) {
				selectPlayer(defender, BlockKind.BLOCK);
			}
		}
	}

	private void selectPlayer(Player<?> player, BlockKind kind) {
		if (selectedPlayers.size() < 2) {
			selectedPlayers.put(player.getId(), kind);
			originalPlayerStates.put(player.getId(), getClient().getGame().getFieldModel().getPlayerState(player));
			getClient().getCommunication().sendSetBlockTarget(player.getId(), kind);
			sendIfSelectionComplete();
		}
	}

	private void sendIfSelectionComplete() {
		if (selectedPlayers.size() == 2) {
			List<BlockTarget> blockTargets = selectedPlayers.entrySet().stream()
				.map(entry -> new BlockTarget(entry.getKey(), entry.getValue(), originalPlayerStates.get(entry.getKey())))
				.sorted(Comparator.comparing(BlockTarget::getPlayerId))
				.collect(Collectors.toList());
			getClient().getCommunication().sendBlockTargets(blockTargets);
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		if (UtilPlayer.isBlockable(getClient().getGame(), pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		return true;
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		if (actingPlayer.isSufferingBloodLust()) {
			boolean actionHandled = true;
			switch (pActionKey) {
				case PLAYER_SELECT:
					createAndShowPopupMenuForBlockingPlayer();
					break;
				case PLAYER_ACTION_MOVE:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_MOVE);
					break;
				case PLAYER_ACTION_END_MOVE:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_END_MOVE);
					break;
				default:
					actionHandled = false;
					break;
			}
			return actionHandled;
		} else {
			switch (pActionKey) {
				case PLAYER_ACTION_TREACHEROUS:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_TREACHEROUS);
					break;
				case PLAYER_ACTION_WISDOM:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_WISDOM);
					break;
				case PLAYER_ACTION_BLOCK:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BLOCK);
					break;
				case PLAYER_ACTION_STAB:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_STAB);
					break;
				case PLAYER_ACTION_RAIDING_PARTY:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY);
					break;
				case PLAYER_ACTION_LOOK_INTO_MY_EYES:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES);
					break;
				case PLAYER_ACTION_BALEFUL_HEX:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX);
					return true;
				case PLAYER_ACTION_BLACK_INK:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BLACK_INK);
					return true;
				case PLAYER_ACTION_CATCH_OF_THE_DAY:
					menuItemSelected(player, IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY);
					return true;
				default:
					FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(player);
					FieldCoordinate moveCoordinate = UtilClientActionKeys.findMoveCoordinate(playerPosition,
						pActionKey);
					Player<?> defender = game.getFieldModel().getPlayer(moveCoordinate);
					if (defender != null) {
						handlePlayerSelection(defender);
					}
					break;
			}
			return true;
		}
	}

	@Override
	public void endTurn() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_END_MOVE);
		getClient().getCommunication().sendEndTurn(getClient().getGame().getTurnMode());
	}

	protected void menuItemSelected(Player<?> player, int pMenuKey) {
		if (player != null) {
			ClientCommunication communication = getClient().getCommunication();
			switch (pMenuKey) {
				case IPlayerPopupMenuKeys.KEY_END_MOVE:
					selectedPlayers.keySet().forEach(communication::sendUnsetBlockTarget);
					selectedPlayers.clear();
					communication.sendActingPlayer(null, null, false);
					break;
				case IPlayerPopupMenuKeys.KEY_BLOCK:
					selectPlayer(player, BlockKind.BLOCK);
					break;
				case IPlayerPopupMenuKeys.KEY_STAB:
					selectPlayer(player, BlockKind.STAB);
					break;
				case IPlayerPopupMenuKeys.KEY_TREACHEROUS:
					if (isTreacherousAvailable(player)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_WISDOM:
					if (isWisdomAvailable(player)) {
						communication.sendUseWisdom();
					}
					break;
				case IPlayerPopupMenuKeys.KEY_RAIDING_PARTY:
					if (isRaidingPartyAvailable(player)) {
						Skill raidingSkill = player.getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
						communication.sendUseSkill(raidingSkill, true, player.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES:
					if (isLookIntoMyEyesAvailable(player)) {
						UtilCards.getUnusedSkillWithProperty(player, NamedProperties.canStealBallFromOpponent)
							.ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, player.getId()));
					}
					break;
				case IPlayerPopupMenuKeys.KEY_BALEFUL_HEX:
					if (isBalefulHexAvailable(player)) {
						Skill balefulSkill = player.getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
						communication.sendUseSkill(balefulSkill, true, player.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_BLACK_INK:
					if (isBlackInkAvailable(player)) {
						Skill blackInkSkill = player.getSkillWithProperty(NamedProperties.canGazeAutomatically);
						communication.sendUseSkill(blackInkSkill, true, player.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_CATCH_OF_THE_DAY:
					if (isCatchOfTheDayAvailable(player)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canGetBallOnGround);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				default:
					break;
			}
		}
	}

	private void createAndShowPopupMenuForBlockingPlayer() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		List<JMenuItem> menuItemList = new ArrayList<>();
		UserInterface userInterface = getClient().getUserInterface();
		IconCache iconCache = userInterface.getIconCache();
		userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
		if (actingPlayer.isSufferingBloodLust()) {
			JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
			menuItemList.add(moveAction);
		}
		addEndActionLabel(iconCache, menuItemList);
		if (isTreacherousAvailable(actingPlayer)) {
			menuItemList.add(createTreacherousItem(iconCache));
		}
		if (isWisdomAvailable(actingPlayer)) {
			menuItemList.add(createWisdomItem(iconCache));
		}
		if (isRaidingPartyAvailable(actingPlayer)) {
			menuItemList.add(createRaidingPartyItem(iconCache));
		}
		if (isLookIntoMyEyesAvailable(actingPlayer)) {
			menuItemList.add(createLookIntoMyEyesItem(iconCache));
		}
		if (isBalefulHexAvailable(actingPlayer)) {
			menuItemList.add(createBalefulHexItem(iconCache));
		}
		if (isBlackInkAvailable(actingPlayer)) {
			menuItemList.add(createBlackInkItem(iconCache));
		}
		if (isCatchOfTheDayAvailable(actingPlayer)) {
			menuItemList.add(createCatchOfTheDayItem(iconCache));
		}
		createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
		showPopupMenuForPlayer(actingPlayer.getPlayer());
	}

}

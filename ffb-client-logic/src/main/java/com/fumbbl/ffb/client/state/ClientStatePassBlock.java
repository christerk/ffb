package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.dialog.DialogInformation;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.OnTheBallMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Kalimar
 */
public class ClientStatePassBlock extends ClientStateMove {

	private DialogInformation fInfoDialog;

	protected ClientStatePassBlock(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.PASS_BLOCK;
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if (game.getTeamHome().hasPlayer(pPlayer) && playerState.isActive()) {
			createAndShowPopupMenuForPlayer(pPlayer);
		}
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		MoveSquare moveSquare = game.getFieldModel().getMoveSquare(pCoordinate);
		if (moveSquare != null) {
			movePlayer(pCoordinate);
		}
	}

	public void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		if (pPlayer != null) {
			Game game = getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			ClientCommunication communication = getClient().getCommunication();
			switch (pMenuKey) {
				case IPlayerPopupMenuKeys.KEY_JUMP:
					if ((actingPlayer.getPlayer() != null)
						&& isJumpAvailableAsNextMove(game, actingPlayer, false)) {
						communication.sendActingPlayer(pPlayer, actingPlayer.getPlayerAction(), !actingPlayer.isJumping());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_MOVE:
					communication.sendActingPlayer(pPlayer, PlayerAction.MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_END_MOVE:
					communication.sendActingPlayer(null, null, false);
					break;
				case IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP:
					isBoundingLeapAvailable(game, actingPlayer).ifPresent(skill ->
						communication.sendUseSkill(skill, true, actingPlayer.getPlayerId()));
				default:
					break;
			}
		}
	}

	private void createAndShowPopupMenuForPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		IconCache iconCache = getClient().getUserInterface().getIconCache();
		List<JMenuItem> menuItemList = new ArrayList<>();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((actingPlayer.getPlayer() == null) && (playerState != null) && playerState.isAbleToMove()) {
			JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
			menuItemList.add(moveAction);
		}
		if ((actingPlayer.getPlayer() != null)
			&& isJumpAvailableAsNextMove(game, actingPlayer, false)) {
			if (actingPlayer.isJumping()) {
				JMenuItem jumpAction = new JMenuItem(dimensionProvider(), "Don't Jump",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
				jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
				jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
				menuItemList.add(jumpAction);
			} else {
				JMenuItem jumpAction = new JMenuItem(dimensionProvider(), "Jump",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_JUMP)));
				jumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_JUMP);
				jumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_JUMP, 0));
				menuItemList.add(jumpAction);
				Optional<Skill> boundingLeap = isBoundingLeapAvailable(game, actingPlayer);
				if (boundingLeap.isPresent()) {
					JMenuItem specialJumpAction = new JMenuItem(dimensionProvider(),
						"Jump (" + boundingLeap.get().getName() + ")",
						new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_JUMP)));
					specialJumpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP);
					specialJumpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP, 0));
					menuItemList.add(specialJumpAction);
				}
			}
		}
		if (game.getActingPlayer().getPlayer() == pPlayer) {
			String endMoveActionLabel = null;
			if (!actingPlayer.hasActed()) {
				endMoveActionLabel = "Deselect Player";
			} else {
				OnTheBallMechanic mechanic = (OnTheBallMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ON_THE_BALL.name());
				if (mechanic.hasReachedValidPosition(game, actingPlayer.getPlayer())) {
					endMoveActionLabel = "End Move";
				}
			}
			if (endMoveActionLabel != null) {
				JMenuItem endMoveAction = new JMenuItem(dimensionProvider(), endMoveActionLabel,
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_END_MOVE)));
				endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
				endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
				menuItemList.add(endMoveAction);
			}
		}
		if (menuItemList.size() > 0) {
			createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
			showPopupMenuForPlayer(pPlayer);
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = true;
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		UserInterface userInterface = getClient().getUserInterface();
		Player<?> selectedPlayer = getClient().getClientData().getSelectedPlayer();
		switch (pActionKey) {
			case PLAYER_SELECT:
				if (selectedPlayer != null) {
					createAndShowPopupMenuForPlayer(selectedPlayer);
				}
				break;
			case PLAYER_CYCLE_RIGHT:
				selectedPlayer = UtilClientActionKeys.cyclePlayer(game, selectedPlayer, true);
				if (selectedPlayer != null) {
					hideSelectSquare();
					FieldCoordinate selectedCoordinate = game.getFieldModel().getPlayerCoordinate(selectedPlayer);
					showSelectSquare(selectedCoordinate);
					getClient().getClientData().setSelectedPlayer(selectedPlayer);
					userInterface.refreshSideBars();
				}
				break;
			case PLAYER_CYCLE_LEFT:
				selectedPlayer = UtilClientActionKeys.cyclePlayer(game, selectedPlayer, false);
				if (selectedPlayer != null) {
					hideSelectSquare();
					FieldCoordinate selectedCoordinate = game.getFieldModel().getPlayerCoordinate(selectedPlayer);
					showSelectSquare(selectedCoordinate);
					getClient().getClientData().setSelectedPlayer(selectedPlayer);
					userInterface.refreshSideBars();
				}
				break;
			case PLAYER_ACTION_MOVE:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_MOVE);
				break;
			case PLAYER_ACTION_JUMP:
				menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_JUMP);
				break;
			case PLAYER_ACTION_BOUNDING_LEAP:
				menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_BOUNDING_LEAP);
				return true;
			default:
				actionHandled = false;
				break;
		}
		return actionHandled;
	}

	@Override
	public void endTurn() {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() != null) {
			OnTheBallMechanic mechanic = (OnTheBallMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ON_THE_BALL.name());
			if (!mechanic.hasReachedValidPosition(game, actingPlayer.getPlayer())) {
				fInfoDialog = new DialogInformation(getClient(), "End Turn not possible",
					new String[]{"You cannot end the turn before the acting player has reached a valid destination!"},
					DialogInformation.OK_DIALOG, IIconProperty.GAME_REF);
				fInfoDialog.showDialog(pDialog -> fInfoDialog.hideDialog());
				return;
			}
		}
		getClient().getCommunication().sendEndTurn(getClient().getGame().getTurnMode());
		getClient().getClientData().setEndTurnButtonHidden(true);
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		sideBarHome.refresh();
	}

}

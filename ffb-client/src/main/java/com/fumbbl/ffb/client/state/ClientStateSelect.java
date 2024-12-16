package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.SelectLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilCards;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientStateSelect extends ClientStateAwt<SelectLogicModule> {

	protected ClientStateSelect(FantasyFootballClientAwt pClient) {
		super(pClient, new SelectLogicModule(pClient));
	}


	public ClientStateId getId() {
		return ClientStateId.SELECT_PLAYER;
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);
		switch (result.getKind()) {
			case SHOW_ACTIONS:
				createAndShowPopupMenuForPlayer(pPlayer);
				break;
			default:
				break;
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return new HashMap<Integer, ClientAction>() {{
			put(IPlayerPopupMenuKeys.KEY_BLOCK, ClientAction.BLOCK);
			put(IPlayerPopupMenuKeys.KEY_BLITZ, ClientAction.BLITZ);
			put(IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH, ClientAction.FRENZIED_RUSH);
			put(IPlayerPopupMenuKeys.KEY_FOUL, ClientAction.FOUL);
			put(IPlayerPopupMenuKeys.KEY_MOVE, ClientAction.MOVE);
			put(IPlayerPopupMenuKeys.KEY_STAND_UP, ClientAction.STAND_UP);
			put(IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ, ClientAction.STAND_UP_BLITZ);
			put(IPlayerPopupMenuKeys.KEY_HAND_OVER, ClientAction.HAND_OVER);
			put(IPlayerPopupMenuKeys.KEY_PASS, ClientAction.PASS);
			put(IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE, ClientAction.THROW_TEAM_MATE);
			put(IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE, ClientAction.KICK_TEAM_MATE);
			put(IPlayerPopupMenuKeys.KEY_RECOVER, ClientAction.RECOVER);
			put(IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK, ClientAction.MULTIPLE_BLOCK);
			put(IPlayerPopupMenuKeys.KEY_BOMB, ClientAction.BOMB);
			put(IPlayerPopupMenuKeys.KEY_GAZE, ClientAction.GAZE);
			put(IPlayerPopupMenuKeys.KEY_GAZE_ZOAT, ClientAction.GAZE_ZOAT);
			put(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING, ClientAction.SHOT_TO_NOTHING);
			put(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING_BOMB, ClientAction.SHOT_TO_NOTHING_BOMB);
			put(IPlayerPopupMenuKeys.KEY_BEER_BARREL_BASH, ClientAction.BEER_BARREL_BASH);
			put(IPlayerPopupMenuKeys.KEY_ALL_YOU_CAN_EAT, ClientAction.ALL_YOU_CAN_EAT);
			put(IPlayerPopupMenuKeys.KEY_KICK_EM_BLOCK, ClientAction.KICK_EM_BLOCK);
			put(IPlayerPopupMenuKeys.KEY_KICK_EM_BLITZ, ClientAction.KICK_EM_BLITZ);
			put(IPlayerPopupMenuKeys.KEY_THE_FLASHING_BLADE, ClientAction.THE_FLASHING_BLADE);
		}};
	}

	private void createAndShowPopupMenuForPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		IconCache iconCache = getClient().getUserInterface().getIconCache();
		List<JMenuItem> menuItemList = new ArrayList<>();
		if (logicModule.isBlockActionAvailable(pPlayer)) {
			JMenuItem blockAction = new JMenuItem(dimensionProvider(), blockActionLabel(pPlayer),
				createMenuIcon(iconCache, IIconProperty.ACTION_BLOCK));
			blockAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BLOCK);
			blockAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLOCK, 0));
			menuItemList.add(blockAction);
		}
		if (logicModule.isMultiBlockActionAvailable(pPlayer)) {
			JMenuItem multiBlockAction = new JMenuItem(dimensionProvider(), "Multiple Block",
				createMenuIcon(iconCache, IIconProperty.ACTION_MUTIPLE_BLOCK));
			multiBlockAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK);
			multiBlockAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK, 0));
			menuItemList.add(multiBlockAction);
		}
		if (logicModule.isThrowBombActionAvailable(pPlayer)) {
			JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Throw Bomb Action",
				createMenuIcon(iconCache, IIconProperty.ACTION_BOMB));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BOMB);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BOMB, 0));
			menuItemList.add(moveAction);
			if (UtilCards.hasUnusedSkillWithProperty(pPlayer, NamedProperties.canGainHailMary)) {
				JMenuItem stnAction = new JMenuItem(dimensionProvider(), "Shot To Nothing Bomb",
					createMenuIcon(iconCache, IIconProperty.ACTION_BOMB));
				stnAction.setMnemonic(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING_BOMB);
				stnAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING_BOMB, 0));
				menuItemList.add(stnAction);
			}
		}
		if (logicModule.isHypnoticGazeActionAvailable(true, pPlayer, NamedProperties.inflictsConfusion)) {
			JMenuItem hypnoticGazeAction = new JMenuItem(dimensionProvider(), "Hypnotic Gaze",
				createMenuIcon(iconCache, IIconProperty.ACTION_GAZE));
			hypnoticGazeAction.setMnemonic(IPlayerPopupMenuKeys.KEY_GAZE);
			hypnoticGazeAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GAZE, 0));
			menuItemList.add(hypnoticGazeAction);
		}
		if (logicModule.isHypnoticGazeActionAvailable(true, pPlayer, NamedProperties.canGainGaze)) {
			JMenuItem hypnoticGazeAction = new JMenuItem(dimensionProvider(), "Hypnotic Gaze (Zoat)",
				createMenuIcon(iconCache, IIconProperty.ACTION_GAZE));
			hypnoticGazeAction.setMnemonic(IPlayerPopupMenuKeys.KEY_GAZE_ZOAT);
			hypnoticGazeAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GAZE_ZOAT, 0));
			menuItemList.add(hypnoticGazeAction);
		}
		if (logicModule.isMoveActionAvailable(pPlayer)) {
			JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move Action",
				createMenuIcon(iconCache, IIconProperty.ACTION_MOVE));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
			menuItemList.add(moveAction);
		}
		if (logicModule.isBlitzActionAvailable(pPlayer)) {
			JMenuItem blitzAction = new JMenuItem(dimensionProvider(), "Blitz Action",
				createMenuIcon(iconCache, IIconProperty.ACTION_BLITZ));
			blitzAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BLITZ);
			blitzAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLITZ, 0));
			menuItemList.add(blitzAction);
			if (UtilCards.hasUnusedSkillWithProperty(pPlayer, NamedProperties.canGainFrenzyForBlitz)) {
				JMenuItem blitzWithFrenzyAction = new JMenuItem(dimensionProvider(), "Frenzied Rush Blitz",
					createMenuIcon(iconCache, IIconProperty.ACTION_BLITZ));
				blitzWithFrenzyAction.setMnemonic(IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH);
				blitzWithFrenzyAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH, 0));
				menuItemList.add(blitzWithFrenzyAction);
			}
		}
		if (logicModule.isFoulActionAvailable(pPlayer)) {
			JMenuItem foulAction = new JMenuItem(dimensionProvider(), "Foul Action",
				createMenuIcon(iconCache, IIconProperty.ACTION_FOUL));
			foulAction.setMnemonic(IPlayerPopupMenuKeys.KEY_FOUL);
			foulAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_FOUL, 0));
			menuItemList.add(foulAction);
		}
		boolean treacherousAvailable = logicModule.isTreacherousAvailable(pPlayer);
		String suffix = treacherousAvailable ? " (Treacherous)" : "";
		if (logicModule.isPassActionAvailable(pPlayer, treacherousAvailable)) {
			JMenuItem passAction = new JMenuItem(dimensionProvider(), "Pass Action" + suffix,
				createMenuIcon(iconCache, IIconProperty.ACTION_PASS));
			passAction.setMnemonic(IPlayerPopupMenuKeys.KEY_PASS);
			passAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PASS, 0));
			menuItemList.add(passAction);
			if (UtilCards.hasUnusedSkillWithProperty(pPlayer, NamedProperties.canGainHailMary)) {
				JMenuItem stnAction = new JMenuItem(dimensionProvider(), "Shot To Nothing",
					createMenuIcon(iconCache, IIconProperty.ACTION_PASS));
				stnAction.setMnemonic(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING);
				stnAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING, 0));
				menuItemList.add(stnAction);
			}
		}
		if (logicModule.isHandOverActionAvailable(pPlayer, treacherousAvailable)) {
			JMenuItem handOverAction = new JMenuItem(dimensionProvider(), "Hand Over Action" + suffix,
				createMenuIcon(iconCache, IIconProperty.ACTION_HAND_OVER));
			handOverAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAND_OVER);
			handOverAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAND_OVER, 0));
			menuItemList.add(handOverAction);
		}
		if (logicModule.isThrowTeamMateActionAvailable(pPlayer)) {
			JMenuItem throwTeamMateAction = new JMenuItem(dimensionProvider(), "Throw Team-Mate Action",
				createMenuIcon(iconCache, IIconProperty.ACTION_PASS));
			throwTeamMateAction.setMnemonic(IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE);
			throwTeamMateAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE, 0));
			menuItemList.add(throwTeamMateAction);
		}
		if (logicModule.isKickTeamMateActionAvailable(pPlayer)) {
			JMenuItem kickTeamMateAction = new JMenuItem(dimensionProvider(), "Kick Team-Mate Action",
				createMenuIcon(iconCache, IIconProperty.ACTION_BLITZ));
			kickTeamMateAction.setMnemonic(IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE);
			kickTeamMateAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE, 0));
			menuItemList.add(kickTeamMateAction);
		}
		if (logicModule.isBeerBarrelBashAvailable(pPlayer)) {
			JMenuItem beerBashItem = new JMenuItem(dimensionProvider(), "Beer Barrel Bash",
				createMenuIcon(iconCache, IIconProperty.ACTION_BEER_BARREL_BASH));
			beerBashItem.setMnemonic(IPlayerPopupMenuKeys.KEY_BEER_BARREL_BASH);
			beerBashItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BEER_BARREL_BASH, 0));
			menuItemList.add(beerBashItem);
		}
		if (logicModule.isAllYouCanEatAvailable(pPlayer)) {
			JMenuItem allYouCanEatItem = new JMenuItem(dimensionProvider(), "All You Can Eat",
				createMenuIcon(iconCache, IIconProperty.ACTION_ALL_YOU_CAN_EAT));
			allYouCanEatItem.setMnemonic(IPlayerPopupMenuKeys.KEY_ALL_YOU_CAN_EAT);
			allYouCanEatItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_ALL_YOU_CAN_EAT, 0));
			menuItemList.add(allYouCanEatItem);
		}
		if (logicModule.isKickEmBlockAvailable(pPlayer)) {
			JMenuItem kickEmItem = new JMenuItem(dimensionProvider(), "Kick 'em while they are down! (Block)",
				createMenuIcon(iconCache, IIconProperty.ACTION_KICK_EM_BLOCK));
			kickEmItem.setMnemonic(IPlayerPopupMenuKeys.KEY_KICK_EM_BLOCK);
			kickEmItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_KICK_EM_BLOCK, 0));
			menuItemList.add(kickEmItem);
		}
		if (logicModule.isKickEmBlitzAvailable(pPlayer)) {
			JMenuItem kickEmItem = new JMenuItem(dimensionProvider(), "Kick 'em while they are down! (Blitz)",
				createMenuIcon(iconCache, IIconProperty.ACTION_KICK_EM_BLITZ));
			kickEmItem.setMnemonic(IPlayerPopupMenuKeys.KEY_KICK_EM_BLITZ);
			kickEmItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_KICK_EM_BLITZ, 0));
			menuItemList.add(kickEmItem);
		}
		if (logicModule.isFlashingBladeAvailable(pPlayer)) {
			menuItemList.add(createFlashingBladeItem(iconCache));
		}
		if (logicModule.isRecoverFromConfusionActionAvailable(pPlayer)) {
			JMenuItem confusionAction = new JMenuItem(dimensionProvider(), "Recover from Confusion & End Move",
				createMenuIcon(iconCache, IIconProperty.ACTION_STAND_UP));
			confusionAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RECOVER);
			confusionAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RECOVER, 0));
			menuItemList.add(confusionAction);
		}
		if (logicModule.isRecoverFromGazeActionAvailable(pPlayer)) {
			JMenuItem confusionAction = new JMenuItem(dimensionProvider(), "Recover from Gaze & End Move",
				createMenuIcon(iconCache, IIconProperty.ACTION_STAND_UP));
			confusionAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RECOVER);
			confusionAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RECOVER, 0));
			menuItemList.add(confusionAction);
		}
		if (logicModule.isStandUpActionAvailable(pPlayer)
			&& pPlayer.hasSkillProperty(NamedProperties.enableStandUpAndEndBlitzAction)
			&& !game.getTurnData().isBlitzUsed()) {
			JMenuItem standUpAction = new JMenuItem(dimensionProvider(), "Stand Up & End Move (using Blitz)",
				createMenuIcon(iconCache, IIconProperty.ACTION_STAND_UP));
			standUpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ);
			standUpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ, 0));
			menuItemList.add(standUpAction);
		}
		if (logicModule.isStandUpActionAvailable(pPlayer)) {
			JMenuItem standUpAction = new JMenuItem(dimensionProvider(), "Stand Up & End Move",
				createMenuIcon(iconCache, IIconProperty.ACTION_STAND_UP));
			standUpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_STAND_UP);
			standUpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_STAND_UP, 0));
			menuItemList.add(standUpAction);
		}
		if (menuItemList.size() > 0) {
			createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
			showPopupMenuForPlayer(pPlayer);
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = true;
		Game game = getClient().getGame();
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
			case PLAYER_ACTION_BLOCK:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_BLOCK);
				break;
			case PLAYER_ACTION_MOVE:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_MOVE);
				break;
			case PLAYER_ACTION_BLITZ:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_BLITZ);
				break;
			case PLAYER_ACTION_FRENZIED_RUSH:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH);
				break;
			case PLAYER_ACTION_FOUL:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_FOUL);
				break;
			case PLAYER_ACTION_STAND_UP:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_STAND_UP);
				break;
			case PLAYER_ACTION_HAND_OVER:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_HAND_OVER);
				break;
			case PLAYER_ACTION_PASS:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_PASS);
				break;
			case PLAYER_ACTION_MULTIPLE_BLOCK:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK);
				break;
			case PLAYER_ACTION_GAZE:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_GAZE);
				break;
			case PLAYER_ACTION_GAZE_ZOAT:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_GAZE_ZOAT);
				break;
			case PLAYER_ACTION_SHOT_TO_NOTHING:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING);
				break;
			case PLAYER_ACTION_SHOT_TO_NOTHING_BOMB:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING_BOMB);
				break;
			case PLAYER_ACTION_BEER_BARREL_BASH:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_BEER_BARREL_BASH);
				break;
			case PLAYER_ACTION_ALL_YOU_CAN_EAT:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_ALL_YOU_CAN_EAT);
				break;
			case PLAYER_ACTION_KICK_EM_BLOCK:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_KICK_EM_BLOCK);
				break;
			case PLAYER_ACTION_KICK_EM_BLITZ:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_KICK_EM_BLITZ);
				break;
			case PLAYER_ACTION_THE_FLASHING_BLADE:
				menuItemSelected(selectedPlayer, IPlayerPopupMenuKeys.KEY_THE_FLASHING_BLADE);
				break;
			default:
				actionHandled = super.actionKeyPressed(pActionKey);
				break;
		}
		return actionHandled;
	}

	@Override
	public void postEndTurn() {
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		sideBarHome.refresh();
	}

	private String blockActionLabel(Player<?> pPlayer) {
		List<String> actions = new ArrayList<>();
		actions.add("Block Action");
		actions.addAll(logicModule.findAlternateBlockActions(pPlayer));
		return String.join("/", actions);
	}

	protected JMenuItem createFlashingBladeItem(IconCache iconCache) {
		JMenuItem item = new JMenuItem(dimensionProvider(), "The Flashing Blade",
			createMenuIcon(iconCache, IIconProperty.ACTION_THE_FLASHING_BLADE));
		item.setMnemonic(IPlayerPopupMenuKeys.KEY_THE_FLASHING_BLADE);
		item.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_THE_FLASHING_BLADE, 0));
		return item;
	}

}

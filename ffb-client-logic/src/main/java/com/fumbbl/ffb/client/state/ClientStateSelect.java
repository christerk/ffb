package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.TtmMechanic;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class ClientStateSelect extends ClientState {

	protected ClientStateSelect(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void enterState() {
		super.enterState();
		getClient().getGame().setDefenderId(null);
		getClient().getClientData().clearBlockDiceResult();
	}

	public ClientStateId getId() {
		return ClientStateId.SELECT_PLAYER;
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if (game.getTeamHome().hasPlayer(pPlayer) && playerState.isActive()) {
			createAndShowPopupMenuForPlayer(pPlayer);
		}
	}

	public void menuItemSelected(Player<?> pPlayer, int pMenuKey) {
		if (pPlayer != null) {
			ClientCommunication communication = getClient().getCommunication();
			switch (pMenuKey) {
				case IPlayerPopupMenuKeys.KEY_BLOCK:
					communication.sendActingPlayer(pPlayer, PlayerAction.BLOCK, false);
					break;
				case IPlayerPopupMenuKeys.KEY_BLITZ:
					communication.sendActingPlayer(pPlayer, PlayerAction.BLITZ_MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH:
					communication.sendActingPlayer(pPlayer, PlayerAction.BLITZ_MOVE, false);
					Skill skill = pPlayer.getSkillWithProperty(NamedProperties.canGainFrenzyForBlitz);
					communication.sendUseSkill(skill, true, pPlayer.getId());
					break;
				case IPlayerPopupMenuKeys.KEY_FOUL:
					communication.sendActingPlayer(pPlayer, PlayerAction.FOUL_MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_MOVE:
					communication.sendActingPlayer(pPlayer, PlayerAction.MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_STAND_UP:
					communication.sendActingPlayer(pPlayer, PlayerAction.STAND_UP, false);
					break;
				case IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ:
					communication.sendActingPlayer(pPlayer, PlayerAction.STAND_UP_BLITZ, false);
					break;
				case IPlayerPopupMenuKeys.KEY_HAND_OVER:
					communication.sendActingPlayer(pPlayer, PlayerAction.HAND_OVER_MOVE, false);
					if (isTreacherousAvailable(pPlayer)) {
						Skill treacherous = pPlayer.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(treacherous, true, pPlayer.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_PASS:
					communication.sendActingPlayer(pPlayer, PlayerAction.PASS_MOVE, false);
					if (isTreacherousAvailable(pPlayer)) {
						Skill treacherous = pPlayer.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(treacherous, true, pPlayer.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE:
					communication.sendActingPlayer(pPlayer, PlayerAction.THROW_TEAM_MATE_MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE:
					communication.sendActingPlayer(pPlayer, PlayerAction.KICK_TEAM_MATE_MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_RECOVER:
					communication.sendActingPlayer(pPlayer, PlayerAction.REMOVE_CONFUSION, false);
					break;
				case IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK:
					communication.sendActingPlayer(pPlayer, PlayerAction.MULTIPLE_BLOCK, false);
					break;
				case IPlayerPopupMenuKeys.KEY_BOMB:
					if (isThrowBombActionAvailable(pPlayer)) {
						communication.sendActingPlayer(pPlayer, PlayerAction.THROW_BOMB, false);
					}
					break;
				case IPlayerPopupMenuKeys.KEY_GAZE:
					communication.sendActingPlayer(pPlayer, PlayerAction.GAZE_MOVE, false);
					break;
				case IPlayerPopupMenuKeys.KEY_GAZE_ZOAT:
					communication.sendActingPlayer(pPlayer, PlayerAction.GAZE_MOVE, false);
					Skill gazeSkill = pPlayer.getSkillWithProperty(NamedProperties.canGainGaze);
					communication.sendUseSkill(gazeSkill, true, pPlayer.getId());
					break;
				case IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING:
					communication.sendActingPlayer(pPlayer, PlayerAction.PASS_MOVE, false);
					Skill stnSkill = pPlayer.getSkillWithProperty(NamedProperties.canGainHailMary);
					communication.sendUseSkill(stnSkill, true, pPlayer.getId());
					if (isTreacherousAvailable(pPlayer)) {
						Skill treacherous = pPlayer.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
						communication.sendUseSkill(treacherous, true, pPlayer.getId());
					}
					break;
				case IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING_BOMB:
					if (isThrowBombActionAvailable(pPlayer)) {
						communication.sendActingPlayer(pPlayer, PlayerAction.THROW_BOMB, false);
						Skill stnbSkill = pPlayer.getSkillWithProperty(NamedProperties.canGainHailMary);
						communication.sendUseSkill(stnbSkill, true, pPlayer.getId());
						if (isTreacherousAvailable(pPlayer)) {
							Skill treacherous = pPlayer.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
							communication.sendUseSkill(treacherous, true, pPlayer.getId());
						}
					}
					break;
				case IPlayerPopupMenuKeys.KEY_BEER_BARREL_BASH:
					if (isBeerBarrelBashAvailable(pPlayer)) {
						communication.sendActingPlayer(pPlayer, PlayerAction.THROW_KEG, false);
					}
					break;
				case IPlayerPopupMenuKeys.KEY_ALL_YOU_CAN_EAT:
					if (isAllYouCanEatAvailable(pPlayer)) {
						communication.sendActingPlayer(pPlayer, PlayerAction.ALL_YOU_CAN_EAT, false);
					}
					break;
				case IPlayerPopupMenuKeys.KEY_KICK_EM_BLOCK:
					if (isKickEmBlockAvailable(pPlayer)) {
						communication.sendActingPlayer(pPlayer, PlayerAction.KICK_EM_BLOCK, false);
					}
					break;
				case IPlayerPopupMenuKeys.KEY_KICK_EM_BLITZ:
					if (isKickEmBlitzAvailable(pPlayer)) {
						communication.sendActingPlayer(pPlayer, PlayerAction.KICK_EM_BLITZ, false);
					}
					break;
				case IPlayerPopupMenuKeys.KEY_THE_FLASHING_BLADE:
					if (isFlashingBladeAvailable(pPlayer)) {
						communication.sendActingPlayer(pPlayer, PlayerAction.THE_FLASHING_BLADE, false);
					}
					break;
				default:
					break;
			}
		}

	}

	private void createAndShowPopupMenuForPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		IconCache iconCache = getClient().getUserInterface().getIconCache();
		List<JMenuItem> menuItemList = new ArrayList<>();
		if (isBlockActionAvailable(pPlayer)) {
			JMenuItem blockAction = new JMenuItem(dimensionProvider(), blockActionLabel(pPlayer),
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLOCK)));
			blockAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BLOCK);
			blockAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLOCK, 0));
			menuItemList.add(blockAction);
		}
		if (isMultiBlockActionAvailable(pPlayer)) {
			JMenuItem multiBlockAction = new JMenuItem(dimensionProvider(), "Multiple Block",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MUTIPLE_BLOCK)));
			multiBlockAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK);
			multiBlockAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK, 0));
			menuItemList.add(multiBlockAction);
		}
		if (isThrowBombActionAvailable(pPlayer)) {
			JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Throw Bomb Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BOMB)));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BOMB);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BOMB, 0));
			menuItemList.add(moveAction);
			if (UtilCards.hasUnusedSkillWithProperty(pPlayer, NamedProperties.canGainHailMary)) {
				JMenuItem stnAction = new JMenuItem(dimensionProvider(), "Shot To Nothing Bomb",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BOMB)));
				stnAction.setMnemonic(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING_BOMB);
				stnAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING_BOMB, 0));
				menuItemList.add(stnAction);
			}
		}
		if (isHypnoticGazeActionAvailable(true, pPlayer, NamedProperties.inflictsConfusion)) {
			JMenuItem hypnoticGazeAction = new JMenuItem(dimensionProvider(), "Hypnotic Gaze",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_GAZE)));
			hypnoticGazeAction.setMnemonic(IPlayerPopupMenuKeys.KEY_GAZE);
			hypnoticGazeAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GAZE, 0));
			menuItemList.add(hypnoticGazeAction);
		}
		if (isHypnoticGazeActionAvailable(true, pPlayer, NamedProperties.canGainGaze)) {
			JMenuItem hypnoticGazeAction = new JMenuItem(dimensionProvider(), "Hypnotic Gaze (Zoat)",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_GAZE)));
			hypnoticGazeAction.setMnemonic(IPlayerPopupMenuKeys.KEY_GAZE_ZOAT);
			hypnoticGazeAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GAZE_ZOAT, 0));
			menuItemList.add(hypnoticGazeAction);
		}
		if (isMoveActionAvailable(pPlayer)) {
			JMenuItem moveAction = new JMenuItem(dimensionProvider(), "Move Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
			moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
			moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
			menuItemList.add(moveAction);
		}
		if (isBlitzActionAvailable(pPlayer)) {
			JMenuItem blitzAction = new JMenuItem(dimensionProvider(), "Blitz Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLITZ)));
			blitzAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BLITZ);
			blitzAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLITZ, 0));
			menuItemList.add(blitzAction);
			if (UtilCards.hasUnusedSkillWithProperty(pPlayer, NamedProperties.canGainFrenzyForBlitz)) {
				JMenuItem blitzWithFrenzyAction = new JMenuItem(dimensionProvider(), "Frenzied Rush Blitz",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLITZ)));
				blitzWithFrenzyAction.setMnemonic(IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH);
				blitzWithFrenzyAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH, 0));
				menuItemList.add(blitzWithFrenzyAction);
			}
		}
		if (isFoulActionAvailable(pPlayer)) {
			JMenuItem foulAction = new JMenuItem(dimensionProvider(), "Foul Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_FOUL)));
			foulAction.setMnemonic(IPlayerPopupMenuKeys.KEY_FOUL);
			foulAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_FOUL, 0));
			menuItemList.add(foulAction);
		}
		boolean treacherousAvailable = isTreacherousAvailable(pPlayer);
		String suffix = treacherousAvailable ? " (Treacherous)" : "";
		if (isPassActionAvailable(pPlayer, treacherousAvailable)) {
			JMenuItem passAction = new JMenuItem(dimensionProvider(), "Pass Action" + suffix,
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_PASS)));
			passAction.setMnemonic(IPlayerPopupMenuKeys.KEY_PASS);
			passAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PASS, 0));
			menuItemList.add(passAction);
			if (UtilCards.hasUnusedSkillWithProperty(pPlayer, NamedProperties.canGainHailMary)) {
				JMenuItem stnAction = new JMenuItem(dimensionProvider(), "Shot To Nothing",
					new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_PASS)));
				stnAction.setMnemonic(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING);
				stnAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING, 0));
				menuItemList.add(stnAction);
			}
		}
		if (isHandOverActionAvailable(pPlayer, treacherousAvailable)) {
			JMenuItem handOverAction = new JMenuItem(dimensionProvider(), "Hand Over Action" + suffix,
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_HAND_OVER)));
			handOverAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAND_OVER);
			handOverAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAND_OVER, 0));
			menuItemList.add(handOverAction);
		}
		if (isThrowTeamMateActionAvailable(pPlayer)) {
			JMenuItem throwTeamMateAction = new JMenuItem(dimensionProvider(), "Throw Team-Mate Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_PASS)));
			throwTeamMateAction.setMnemonic(IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE);
			throwTeamMateAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE, 0));
			menuItemList.add(throwTeamMateAction);
		}
		if (isKickTeamMateActionAvailable(pPlayer)) {
			JMenuItem kickTeamMateAction = new JMenuItem(dimensionProvider(), "Kick Team-Mate Action",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLITZ)));
			kickTeamMateAction.setMnemonic(IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE);
			kickTeamMateAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE, 0));
			menuItemList.add(kickTeamMateAction);
		}
		if (isBeerBarrelBashAvailable(pPlayer)) {
			JMenuItem beerBashItem = new JMenuItem(dimensionProvider(), "Beer Barrel Bash",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BEER_BARREL_BASH)));
			beerBashItem.setMnemonic(IPlayerPopupMenuKeys.KEY_BEER_BARREL_BASH);
			beerBashItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BEER_BARREL_BASH, 0));
			menuItemList.add(beerBashItem);
		}
		if (isAllYouCanEatAvailable(pPlayer)) {
			JMenuItem allYouCanEatItem = new JMenuItem(dimensionProvider(), "All You Can Eat",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_ALL_YOU_CAN_EAT)));
			allYouCanEatItem.setMnemonic(IPlayerPopupMenuKeys.KEY_ALL_YOU_CAN_EAT);
			allYouCanEatItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_ALL_YOU_CAN_EAT, 0));
			menuItemList.add(allYouCanEatItem);
		}
		if (isKickEmBlockAvailable(pPlayer)) {
			JMenuItem kickEmItem = new JMenuItem(dimensionProvider(), "Kick 'em while they are down! (Block)",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_KICK_EM_BLOCK)));
			kickEmItem.setMnemonic(IPlayerPopupMenuKeys.KEY_KICK_EM_BLOCK);
			kickEmItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_KICK_EM_BLOCK, 0));
			menuItemList.add(kickEmItem);
		}
		if (isKickEmBlitzAvailable(pPlayer)) {
			JMenuItem kickEmItem = new JMenuItem(dimensionProvider(), "Kick 'em while they are down! (Blitz)",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_KICK_EM_BLITZ)));
			kickEmItem.setMnemonic(IPlayerPopupMenuKeys.KEY_KICK_EM_BLITZ);
			kickEmItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_KICK_EM_BLITZ, 0));
			menuItemList.add(kickEmItem);
		}
		if (isFlashingBladeAvailable(pPlayer)) {
			menuItemList.add(createFlashingBladeItem(iconCache));
		}
		if (isRecoverFromConfusionActionAvailable(pPlayer)) {
			JMenuItem confusionAction = new JMenuItem(dimensionProvider(), "Recover from Confusion & End Move",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STAND_UP)));
			confusionAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RECOVER);
			confusionAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RECOVER, 0));
			menuItemList.add(confusionAction);
		}
		if (isRecoverFromGazeActionAvailable(pPlayer)) {
			JMenuItem confusionAction = new JMenuItem(dimensionProvider(), "Recover from Gaze & End Move",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STAND_UP)));
			confusionAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RECOVER);
			confusionAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RECOVER, 0));
			menuItemList.add(confusionAction);
		}
		if (isStandUpActionAvailable(pPlayer)
			&& pPlayer.hasSkillProperty(NamedProperties.enableStandUpAndEndBlitzAction)
			&& !game.getTurnData().isBlitzUsed()) {
			JMenuItem standUpAction = new JMenuItem(dimensionProvider(), "Stand Up & End Move (using Blitz)",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STAND_UP)));
			standUpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ);
			standUpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ, 0));
			menuItemList.add(standUpAction);
		}
		if (isStandUpActionAvailable(pPlayer)) {
			JMenuItem standUpAction = new JMenuItem(dimensionProvider(), "Stand Up & End Move",
				new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STAND_UP)));
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
				actionHandled = false;
				break;
		}
		return actionHandled;
	}

	@Override
	public void endTurn() {
		getClient().getCommunication().sendEndTurn(getClient().getGame().getTurnMode());
		getClient().getClientData().setEndTurnButtonHidden(true);
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		sideBarHome.refresh();
	}

	private String blockActionLabel(Player<?> pPlayer) {
		List<String> actions = new ArrayList<>();
		actions.add("Block Action");
		pPlayer.getSkillsIncludingTemporaryOnes().stream().filter(skill ->
				skill.hasSkillProperty(NamedProperties.providesBlockAlternative)
					&& SkillUsageType.REGULAR == skill.getSkillUsageType())
			.map(Skill::getName)
			.forEach(actions::add);
		return String.join("/", actions);
	}

	private boolean isBlockActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerState != null) && !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& playerState.isActive() && !pPlayer.hasSkillProperty(NamedProperties.preventRegularBlockAction)
			&& mechanic.isBlockActionAllowed(game.getTurnMode())
			&& ((playerState.getBase() != PlayerState.PRONE) || ((playerState.getBase() == PlayerState.PRONE)
			&& pPlayer.hasSkillProperty(NamedProperties.canStandUpForFree)))) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			int blockablePlayers = UtilPlayer.findAdjacentBlockablePlayers(game, game.getTeamAway(), playerCoordinate).length;
			return (blockablePlayers > 0);
		}
		return false;
	}

	private boolean isMultiBlockActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerState != null) && !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& playerState.isActive()
			&& ((UtilCards.hasSkillWithProperty(pPlayer, NamedProperties.canBlockMoreThanOnce)
			&& !UtilCards.hasSkillToCancelProperty(pPlayer, NamedProperties.canBlockMoreThanOnce))
			|| (UtilCards.hasSkillWithProperty(pPlayer, NamedProperties.canBlockTwoAtOnce)
			&& !UtilCards.hasSkillToCancelProperty(pPlayer, NamedProperties.canBlockTwoAtOnce)))
			&& ((playerState.getBase() != PlayerState.PRONE) || ((playerState.getBase() == PlayerState.PRONE)
			&& pPlayer.hasSkillProperty(NamedProperties.canStandUpForFree)))) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			int blockablePlayers = UtilPlayer.findAdjacentBlockablePlayers(game, game.getTeamAway(), playerCoordinate).length;
			return (blockablePlayers > 1);
		}
		return false;
	}

	private boolean isThrowBombActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null)
			&& mechanic.isBombActionAllowed(game.getTurnMode())
			&& !game.getTurnData().isBombUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& !playerState.isProneOrStunned()
			&& pPlayer.hasSkillProperty(NamedProperties.enableThrowBombAction));
	}

	private boolean isMoveActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null) && playerState.isAbleToMove());
	}

	private boolean isBlitzActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return (!game.getTurnData().isBlitzUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED) && (playerState != null)
			&& playerState.isActive() && (playerState.isAbleToMove() || playerState.isRooted())
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRegularBlitzAction));
	}

	private boolean isFoulActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerState != null) && !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& mechanic.isFoulActionAllowed(game.getTurnMode())
			&& playerState.isActive() && (!game.getTurnData().isFoulUsed() || pPlayer.hasSkillProperty(NamedProperties.allowsAdditionalFoul))
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRegularFoulAction)) {
			for (Player<?> opponent : game.getTeamAway().getPlayers()) {
				PlayerState opponentState = game.getFieldModel().getPlayerState(opponent);
				if (opponentState.canBeFouled()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isPassActionAvailable(Player<?> pPlayer, boolean treacherousAvailable) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return (!game.getTurnData().isPassUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& (UtilPlayer.isBallAvailable(game, pPlayer) || treacherousAvailable) && (playerState != null)
			&& (playerState.isAbleToMove() || (UtilPlayer.hasBall(game, pPlayer) || treacherousAvailable))
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRegularPassAction));
	}

	private boolean isHandOverActionAvailable(Player<?> pPlayer, boolean treacherousAvailable) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return (!game.getTurnData().isHandOverUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& (UtilPlayer.isBallAvailable(game, pPlayer) || treacherousAvailable) && (playerState != null)
			&& (playerState.isAbleToMove() || (UtilPlayer.hasBall(game, pPlayer) || treacherousAvailable))
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRegularHandOverAction));
	}

	private boolean isThrowTeamMateActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());

		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerState == null) || pPlayer.hasSkillProperty(NamedProperties.preventThrowTeamMateAction)) {
			return false;
		}

		boolean rightStuffAvailable = false;
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		Player<?>[] teamPlayers = pPlayer.getTeam().getPlayers();
		for (Player<?> teamPlayer : teamPlayers) {
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(teamPlayer);
			if (mechanic.canBeThrown(game, teamPlayer)
				&& !playerCoordinate.isBoxCoordinate()) {
				rightStuffAvailable = true;
				break;
			}
		}

		boolean rightStuffAdjacent = ArrayTool.isProvided(mechanic.findThrowableTeamMates(game, pPlayer));

		return (!game.getTurnData().isPassUsed()
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& mechanic.canThrow(pPlayer) && rightStuffAvailable
			&& (playerState.isAbleToMove() || rightStuffAdjacent));
	}

	private boolean isKickTeamMateActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		GameMechanic gameMechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if (!gameMechanic.isKickTeamMateActionAllowed(game.getTurnMode()) || playerState == null || pPlayer.hasSkillProperty(NamedProperties.preventKickTeamMateAction)) {
			return false;
		}
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());

		boolean rightStuffAvailable = false;
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		Player<?>[] teamPlayers = pPlayer.getTeam().getPlayers();
		for (Player<?> teamPlayer : teamPlayers) {
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(teamPlayer);
			if (mechanic.canBeKicked(game, teamPlayer)
				&& !playerCoordinate.isBoxCoordinate()) {
				rightStuffAvailable = true;
				break;
			}
		}

		boolean rightStuffAdjacent = false;
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		Player<?>[] adjacentTeamPlayers = UtilPlayer.findAdjacentPlayersWithTacklezones(game, pPlayer.getTeam(),
			playerCoordinate, false);
		for (Player<?> adjacentTeamPlayer : adjacentTeamPlayers) {
			if (mechanic.canBeKicked(game, adjacentTeamPlayer)) {
				rightStuffAdjacent = true;
				break;
			}
		}

		return (mechanic.isKtmAvailable(game.getTurnData())
			&& !game.getFieldModel().hasCardEffect(pPlayer, CardEffect.ILLEGALLY_SUBSTITUTED)
			&& pPlayer.hasSkillProperty(NamedProperties.canKickTeamMates) && rightStuffAvailable
			&& (playerState.isAbleToMove() || rightStuffAdjacent));
	}

	private boolean isStandUpActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null) && (playerState.getBase() == PlayerState.PRONE) && playerState.isActive()
			&& !pPlayer.hasSkillProperty(NamedProperties.preventStandUpAction));
	}

	private boolean isRecoverFromConfusionActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null) && playerState.isConfused() && playerState.isActive()
			&& (playerState.getBase() != PlayerState.PRONE)
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRecoverFromConcusionAction));
	}

	private boolean isRecoverFromGazeActionAvailable(Player<?> pPlayer) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		return ((playerState != null) && playerState.isHypnotized() && (playerState.getBase() != PlayerState.PRONE)
			&& !pPlayer.hasSkillProperty(NamedProperties.preventRecoverFromGazeAction));
	}

	private boolean isBeerBarrelBashAvailable(Player<?> player) {
		Game game = getClient().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return game.getTurnMode() == TurnMode.REGULAR && playerState.getBase() == PlayerState.STANDING && UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canThrowKeg);
	}

	private boolean isAllYouCanEatAvailable(Player<?> player) {
		Game game = getClient().getGame();
		return isThrowBombActionAvailable(player) && game.getTurnMode() == TurnMode.REGULAR
			&& UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canUseThrowBombActionTwice);
	}

	private boolean isKickEmBlockAvailable(Player<?> player) {
		return isKickEmAvailable(player, false);
	}

	private boolean isKickEmBlitzAvailable(Player<?> player) {
		return isKickEmAvailable(player, true);
	}

	private boolean isKickEmAvailable(Player<?> player, boolean moveAllowed) {
		Game game = getClient().getGame();
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);
		PlayerState playerState = fieldModel.getPlayerState(player);
		if ((playerState != null) && playerState.isActive() && (!game.getTurnData().isBlitzUsed() || !moveAllowed)
			&& UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canUseChainsawOnDownedOpponents) && player.hasSkill(NamedProperties.blocksLikeChainsaw)) {
			for (Player<?> opponent : game.getTeamAway().getPlayers()) {
				PlayerState opponentState = fieldModel.getPlayerState(opponent);
				if (opponentState.canBeFouled() && (moveAllowed || playerCoordinate.isAdjacent(fieldModel.getPlayerCoordinate(opponent)))) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isFlashingBladeAvailable(Player<?> player) {
		Game game = getClient().getGame();
		Team opponentTeam = game.getOtherTeam(player.getTeam());
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());

		return (playerState != null) && playerState.isActive()
			&& mechanic.isBlockActionAllowed(game.getTurnMode())
			&& (playerState.getBase() != PlayerState.PRONE)
			&& player.hasUnusedSkillProperty(NamedProperties.canStabAndMoveAfterwards)
			&& ArrayTool.isProvided(UtilPlayer.findAdjacentBlockablePlayers(game, opponentTeam, game.getFieldModel().getPlayerCoordinate(player)));
	}

	protected JMenuItem createFlashingBladeItem(IconCache iconCache) {
		JMenuItem item = new JMenuItem(dimensionProvider(), "The Flashing Blade",
			new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_THE_FLASHING_BLADE)));
		item.setMnemonic(IPlayerPopupMenuKeys.KEY_THE_FLASHING_BLADE);
		item.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_THE_FLASHING_BLADE, 0));
		return item;
	}

}

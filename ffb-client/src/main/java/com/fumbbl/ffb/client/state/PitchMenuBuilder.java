package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.InfluencingAction;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PitchMenuBuilder {

	private final DimensionProvider dimensionProvider;
	private final IconCache iconCache;

	public PitchMenuBuilder(DimensionProvider dimensionProvider, IconCache iconCache) {
		this.dimensionProvider = dimensionProvider;
		this.iconCache = iconCache;
	}

	protected List<JMenuItem> populateMenu(ActionContext actionContext) {
		return populateMenu(actionContext, new ArrayList<>());
	}

	protected List<JMenuItem> populateMenu(ActionContext actionContext, List<JMenuItem> menuItemList) {
		boolean treacherousAvailable = actionContext.getInfluencingActions().contains(InfluencingAction.TREACHEROUS);
		String suffix = treacherousAvailable ? " (Treacherous)" : "";

		for (ClientAction action : actionContext.getActions()) {
			switch (action) {
				case BLOCK:
					JMenuItem blockAction = new JMenuItem(dimensionProvider, blockActionLabel(actionContext.getBlockAlternatives()),
						createMenuIcon(iconCache, IIconProperty.ACTION_BLOCK));
					blockAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BLOCK);
					blockAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLOCK, 0));
					menuItemList.add(blockAction);
					break;
				case MULTIPLE_BLOCK:
					JMenuItem multiBlockAction = new JMenuItem(dimensionProvider, "Multiple Block",
						createMenuIcon(iconCache, IIconProperty.ACTION_MUTIPLE_BLOCK));
					multiBlockAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK);
					multiBlockAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MULTIPLE_BLOCK, 0));
					menuItemList.add(multiBlockAction);
					break;
				case BOMB:
					JMenuItem bombAction = new JMenuItem(dimensionProvider, "Throw Bomb Action",
						createMenuIcon(iconCache, IIconProperty.ACTION_BOMB));
					bombAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BOMB);
					bombAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BOMB, 0));
					menuItemList.add(bombAction);
					break;
				case SHOT_TO_NOTHING_BOMB:
					JMenuItem stnbAction = new JMenuItem(dimensionProvider, "Shot To Nothing Bomb",
						createMenuIcon(iconCache, IIconProperty.ACTION_BOMB));
					stnbAction.setMnemonic(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING_BOMB);
					stnbAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING_BOMB, 0));
					menuItemList.add(stnbAction);
					break;
				case GAZE:
					JMenuItem hypnoticGazeAction = new JMenuItem(dimensionProvider, "Hypnotic Gaze",
						createMenuIcon(iconCache, IIconProperty.ACTION_GAZE));
					hypnoticGazeAction.setMnemonic(IPlayerPopupMenuKeys.KEY_GAZE);
					hypnoticGazeAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GAZE, 0));
					menuItemList.add(hypnoticGazeAction);
					break;
				case GAZE_ZOAT:
					JMenuItem zoatAction = new JMenuItem(dimensionProvider, "Hypnotic Gaze (Zoat)",
						createMenuIcon(iconCache, IIconProperty.ACTION_GAZE));
					zoatAction.setMnemonic(IPlayerPopupMenuKeys.KEY_GAZE_ZOAT);
					zoatAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GAZE_ZOAT, 0));
					menuItemList.add(zoatAction);
					break;
				case MOVE:
					JMenuItem moveAction = new JMenuItem(dimensionProvider, "Move Action",
						createMenuIcon(iconCache, IIconProperty.ACTION_MOVE));
					moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
					moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
					menuItemList.add(moveAction);
					break;
				case BLITZ:
					JMenuItem blitzAction = new JMenuItem(dimensionProvider, "Blitz Action",
						createMenuIcon(iconCache, IIconProperty.ACTION_BLITZ));
					blitzAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BLITZ);
					blitzAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLITZ, 0));
					menuItemList.add(blitzAction);
					break;
				case FRENZIED_RUSH:
					JMenuItem blitzWithFrenzyAction = new JMenuItem(dimensionProvider, "Frenzied Rush Blitz",
						createMenuIcon(iconCache, IIconProperty.ACTION_BLITZ));
					blitzWithFrenzyAction.setMnemonic(IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH);
					blitzWithFrenzyAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_FRENZIED_RUSH, 0));
					menuItemList.add(blitzWithFrenzyAction);
					break;
				case FOUL:
					JMenuItem foulAction = new JMenuItem(dimensionProvider, "Foul Action",
						createMenuIcon(iconCache, IIconProperty.ACTION_FOUL));
					foulAction.setMnemonic(IPlayerPopupMenuKeys.KEY_FOUL);
					foulAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_FOUL, 0));
					menuItemList.add(foulAction);
					break;
				case PASS:
					JMenuItem passAction = new JMenuItem(dimensionProvider, "Pass Action" + suffix,
						createMenuIcon(iconCache, IIconProperty.ACTION_PASS));
					passAction.setMnemonic(IPlayerPopupMenuKeys.KEY_PASS);
					passAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PASS, 0));
					menuItemList.add(passAction);
					break;
				case SHOT_TO_NOTHING:
					JMenuItem stnAction = new JMenuItem(dimensionProvider, "Shot To Nothing",
						createMenuIcon(iconCache, IIconProperty.ACTION_PASS));
					stnAction.setMnemonic(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING);
					stnAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_SHOT_TO_NOTHING, 0));
					menuItemList.add(stnAction);
					break;
				case HAND_OVER:
					JMenuItem handOverAction = new JMenuItem(dimensionProvider, "Hand Over Action" + suffix,
						createMenuIcon(iconCache, IIconProperty.ACTION_HAND_OVER));
					handOverAction.setMnemonic(IPlayerPopupMenuKeys.KEY_HAND_OVER);
					handOverAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_HAND_OVER, 0));
					menuItemList.add(handOverAction);
					break;
				case THROW_TEAM_MATE:
					JMenuItem throwTeamMateAction = new JMenuItem(dimensionProvider, "Throw Team-Mate Action",
						createMenuIcon(iconCache, IIconProperty.ACTION_PASS));
					throwTeamMateAction.setMnemonic(IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE);
					throwTeamMateAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE, 0));
					menuItemList.add(throwTeamMateAction);
					break;
				case KICK_TEAM_MATE:
					JMenuItem kickTeamMateAction = new JMenuItem(dimensionProvider, "Kick Team-Mate Action",
						createMenuIcon(iconCache, IIconProperty.ACTION_BLITZ));
					kickTeamMateAction.setMnemonic(IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE);
					kickTeamMateAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE, 0));
					menuItemList.add(kickTeamMateAction);
					break;
				case BEER_BARREL_BASH:
					JMenuItem beerBashItem = new JMenuItem(dimensionProvider, "Beer Barrel Bash",
						createMenuIcon(iconCache, IIconProperty.ACTION_BEER_BARREL_BASH));
					beerBashItem.setMnemonic(IPlayerPopupMenuKeys.KEY_BEER_BARREL_BASH);
					beerBashItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BEER_BARREL_BASH, 0));
					menuItemList.add(beerBashItem);
					break;
				case ALL_YOU_CAN_EAT:
					JMenuItem allYouCanEatItem = new JMenuItem(dimensionProvider, "All You Can Eat",
						createMenuIcon(iconCache, IIconProperty.ACTION_ALL_YOU_CAN_EAT));
					allYouCanEatItem.setMnemonic(IPlayerPopupMenuKeys.KEY_ALL_YOU_CAN_EAT);
					allYouCanEatItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_ALL_YOU_CAN_EAT, 0));
					menuItemList.add(allYouCanEatItem);
					break;
				case KICK_EM_BLOCK:
					JMenuItem kickEmItem = new JMenuItem(dimensionProvider, "Kick 'em while they are down! (Block)",
						createMenuIcon(iconCache, IIconProperty.ACTION_KICK_EM_BLOCK));
					kickEmItem.setMnemonic(IPlayerPopupMenuKeys.KEY_KICK_EM_BLOCK);
					kickEmItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_KICK_EM_BLOCK, 0));
					menuItemList.add(kickEmItem);
					break;
				case KICK_EM_BLITZ:
					JMenuItem kickEmBlitzItem = new JMenuItem(dimensionProvider, "Kick 'em while they are down! (Blitz)",
						createMenuIcon(iconCache, IIconProperty.ACTION_KICK_EM_BLITZ));
					kickEmBlitzItem.setMnemonic(IPlayerPopupMenuKeys.KEY_KICK_EM_BLITZ);
					kickEmBlitzItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_KICK_EM_BLITZ, 0));
					menuItemList.add(kickEmBlitzItem);
					break;
				case THE_FLASHING_BLADE:
					JMenuItem flashingBladeItem = new JMenuItem(dimensionProvider, "The Flashing Blade",
						createMenuIcon(iconCache, IIconProperty.ACTION_THE_FLASHING_BLADE));
					flashingBladeItem.setMnemonic(IPlayerPopupMenuKeys.KEY_THE_FLASHING_BLADE);
					flashingBladeItem.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_THE_FLASHING_BLADE, 0));
					menuItemList.add(flashingBladeItem);
					break;
				case RECOVER:
					JMenuItem confusionAction = new JMenuItem(dimensionProvider, "Recover tackle zone & End Move",
						createMenuIcon(iconCache, IIconProperty.ACTION_STAND_UP));
					confusionAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RECOVER);
					confusionAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RECOVER, 0));
					menuItemList.add(confusionAction);
					break;
				case STAND_UP_BLITZ:
					JMenuItem standUpBlitzAction = new JMenuItem(dimensionProvider, "Stand Up & End Move (using Blitz)",
						createMenuIcon(iconCache, IIconProperty.ACTION_STAND_UP));
					standUpBlitzAction.setMnemonic(IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ);
					standUpBlitzAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_STAND_UP_BLITZ, 0));
					menuItemList.add(standUpBlitzAction);
					break;
				case STAND_UP:
					JMenuItem standUpAction = new JMenuItem(dimensionProvider, "Stand Up & End Move",
						createMenuIcon(iconCache, IIconProperty.ACTION_STAND_UP));
					standUpAction.setMnemonic(IPlayerPopupMenuKeys.KEY_STAND_UP);
					standUpAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_STAND_UP, 0));
					menuItemList.add(standUpAction);
			}
		}
		return menuItemList;
	}

	private String blockActionLabel(List<String> blockActions) {
		List<String> actions = new ArrayList<>();
		actions.add("Block Action");
		actions.addAll(blockActions);
		return String.join("/", actions);
	}

	public ImageIcon createMenuIcon(IconCache iconCache, String iconProperty) {
		return new ImageIcon(iconCache.getIconByProperty(iconProperty, dimensionProvider));
	}

}

package com.fumbbl.ffb.client;

import com.fumbbl.ffb.IClientProperty;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ActionKeyBindings {

	private final FantasyFootballClient fClient;
	private final Map<ActionKeyGroup, List<ActionKeyAction>> fActionsByGroup;

	public ActionKeyBindings(FantasyFootballClient pClient) {
		fClient = pClient;
		fActionsByGroup = new HashMap<>();
		init();
	}

	private void init() {

		fActionsByGroup.clear();

		List<ActionKeyAction> playerMoves = new ArrayList<>();

		playerMoves.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, 0),
			ActionKey.PLAYER_MOVE_NORTH));
		playerMoves.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, 0),
			ActionKey.PLAYER_MOVE_NORTHEAST));
		playerMoves.add(
			new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, 0), ActionKey.PLAYER_MOVE_EAST));
		playerMoves.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, 0),
			ActionKey.PLAYER_MOVE_SOUTHEAST));
		playerMoves.add(
			new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, 0), ActionKey.PLAYER_MOVE_SOUTH));
		playerMoves.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, 0),
			ActionKey.PLAYER_MOVE_SOUTHWEST));
		playerMoves.add(
			new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, 0), ActionKey.PLAYER_MOVE_WEST));
		playerMoves.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD7, 0),
			ActionKey.PLAYER_MOVE_NORTHWEST));

		addActionBinding(IClientProperty.KEY_PLAYER_MOVE_NORTH, playerMoves, ActionKey.PLAYER_MOVE_NORTH);
		addActionBinding(IClientProperty.KEY_PLAYER_MOVE_NORTHEAST, playerMoves, ActionKey.PLAYER_MOVE_NORTHEAST);
		addActionBinding(IClientProperty.KEY_PLAYER_MOVE_EAST, playerMoves, ActionKey.PLAYER_MOVE_EAST);
		addActionBinding(IClientProperty.KEY_PLAYER_MOVE_SOUTHEAST, playerMoves, ActionKey.PLAYER_MOVE_SOUTHEAST);
		addActionBinding(IClientProperty.KEY_PLAYER_MOVE_SOUTH, playerMoves, ActionKey.PLAYER_MOVE_SOUTH);
		addActionBinding(IClientProperty.KEY_PLAYER_MOVE_SOUTHWEST, playerMoves, ActionKey.PLAYER_MOVE_SOUTHWEST);
		addActionBinding(IClientProperty.KEY_PLAYER_MOVE_WEST, playerMoves, ActionKey.PLAYER_MOVE_WEST);
		addActionBinding(IClientProperty.KEY_PLAYER_MOVE_NORTHWEST, playerMoves, ActionKey.PLAYER_MOVE_NORTHWEST);

		fActionsByGroup.put(ActionKeyGroup.PLAYER_MOVES, playerMoves);

		List<ActionKeyAction> playerSelection = new ArrayList<>();
		addActionBinding(IClientProperty.KEY_PLAYER_SELECT, playerSelection, ActionKey.PLAYER_SELECT);
		addActionBinding(IClientProperty.KEY_PLAYER_CYCLE_RIGHT, playerSelection, ActionKey.PLAYER_CYCLE_RIGHT);
		addActionBinding(IClientProperty.KEY_PLAYER_CYCLE_LEFT, playerSelection, ActionKey.PLAYER_CYCLE_LEFT);
		fActionsByGroup.put(ActionKeyGroup.PLAYER_SELECTION, playerSelection);

		List<ActionKeyAction> playerActions = new ArrayList<>();
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_BLOCK, playerActions, ActionKey.PLAYER_ACTION_BLOCK);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_BLITZ, playerActions, ActionKey.PLAYER_ACTION_BLITZ);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_FOUL, playerActions, ActionKey.PLAYER_ACTION_FOUL);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_MOVE, playerActions, ActionKey.PLAYER_ACTION_MOVE);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_STAND_UP, playerActions, ActionKey.PLAYER_ACTION_STAND_UP);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_HAND_OVER, playerActions, ActionKey.PLAYER_ACTION_HAND_OVER);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_PASS, playerActions, ActionKey.PLAYER_ACTION_PASS);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_JUMP, playerActions, ActionKey.PLAYER_ACTION_JUMP);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_END_MOVE, playerActions, ActionKey.PLAYER_ACTION_END_MOVE);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_STAB, playerActions, ActionKey.PLAYER_ACTION_STAB);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_CHAINSAW, playerActions, ActionKey.PLAYER_ACTION_CHAINSAW);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_PROJECTILE_VOMIT, playerActions, ActionKey.PLAYER_ACTION_PROJECTILE_VOMIT);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_GAZE, playerActions, ActionKey.PLAYER_ACTION_GAZE);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_FUMBLEROOSKIE, playerActions, ActionKey.PLAYER_ACTION_FUMBLEROOSKIE);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_RANGE_GRID, playerActions, ActionKey.PLAYER_ACTION_RANGE_GRID);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_HAIL_MARY_PASS, playerActions, ActionKey.PLAYER_ACTION_HAIL_MARY_PASS);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_MULTIPLE_BLOCK, playerActions, ActionKey.PLAYER_ACTION_MULTIPLE_BLOCK);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_FRENZIED_RUSH, playerActions, ActionKey.PLAYER_ACTION_FRENZIED_RUSH);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_SHOT_TO_NOTHING, playerActions, ActionKey.PLAYER_ACTION_SHOT_TO_NOTHING);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_SHOT_TO_NOTHING_BOMB, playerActions, ActionKey.PLAYER_ACTION_SHOT_TO_NOTHING_BOMB);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_TREACHEROUS, playerActions, ActionKey.PLAYER_ACTION_TREACHEROUS);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_WISDOM, playerActions, ActionKey.PLAYER_ACTION_WISDOM);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_BEER_BARREL_BASH, playerActions, ActionKey.PLAYER_ACTION_BEER_BARREL_BASH);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_CATCH_OF_THE_DAY, playerActions, ActionKey.PLAYER_ACTION_CATCH_OF_THE_DAY);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_RAIDING_PARTY, playerActions, ActionKey.PLAYER_ACTION_RAIDING_PARTY);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_LOOK_INTO_MY_EYES, playerActions, ActionKey.PLAYER_ACTION_LOOK_INTO_MY_EYES);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_BALEFUL_HEX, playerActions, ActionKey.PLAYER_ACTION_BALEFUL_HEX);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_HIT_AND_RUN, playerActions, ActionKey.PLAYER_ACTION_HIT_AND_RUN);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_ALL_YOU_CAN_EAT, playerActions, ActionKey.PLAYER_ACTION_ALL_YOU_CAN_EAT);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_KICK_EM_BLOCK, playerActions, ActionKey.PLAYER_ACTION_KICK_EM_BLOCK);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_KICK_EM_BLITZ, playerActions, ActionKey.PLAYER_ACTION_KICK_EM_BLITZ);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_GORED, playerActions, ActionKey.PLAYER_ACTION_GORED);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_BLACK_INK, playerActions, ActionKey.PLAYER_ACTION_BLACK_INK);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_BREATHE_FIRE, playerActions, ActionKey.PLAYER_ACTION_BREATHE_FIRE);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_BOUNDING_LEAP, playerActions, ActionKey.PLAYER_ACTION_BOUNDING_LEAP);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_THEN_I_STARTED_BLASTIN, playerActions, ActionKey.PLAYER_ACTION_THEN_I_STARTED_BLASTIN);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_THE_FLASHING_BLADE, playerActions, ActionKey.PLAYER_ACTION_THE_FLASHING_BLADE);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_VICIOUS_VINES, playerActions, ActionKey.PLAYER_ACTION_VICIOUS_VINES);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_FURIOUS_OUTBURST, playerActions, ActionKey.PLAYER_ACTION_FURIOUS_OUTBURST);
		addActionBinding(IClientProperty.KEY_PLAYER_ACTION_MORE_ACTIONS, playerActions, ActionKey.PLAYER_ACTION_MORE_ACTIONS);

		fActionsByGroup.put(ActionKeyGroup.PLAYER_ACTIONS, playerActions);

		List<ActionKeyAction> turnActions = new ArrayList<>();
		addActionBinding(IClientProperty.KEY_TOOLBAR_TURN_END, turnActions, ActionKey.TOOLBAR_TURN_END);
		addActionBinding(IClientProperty.KEY_TOOLBAR_ILLEGAL_PROCEDURE, turnActions, ActionKey.TOOLBAR_ILLEGAL_PROCEDURE);
		fActionsByGroup.put(ActionKeyGroup.TURN_ACTIONS, turnActions);

		List<ActionKeyAction> resizeActions = new ArrayList<>();
		addActionBinding(IClientProperty.KEY_RESIZE_LARGER, resizeActions, ActionKey.RESIZE_LARGER, JComponent.WHEN_IN_FOCUSED_WINDOW);
		addActionBinding(IClientProperty.KEY_RESIZE_SMALLER, resizeActions, ActionKey.RESIZE_SMALLER, JComponent.WHEN_IN_FOCUSED_WINDOW);
		addActionBinding(IClientProperty.KEY_RESIZE_RESET, resizeActions, ActionKey.RESIZE_RESET, JComponent.WHEN_IN_FOCUSED_WINDOW);
		addActionBinding(IClientProperty.KEY_RESIZE_SMALLER2, resizeActions, ActionKey.RESIZE_SMALLER2, JComponent.WHEN_IN_FOCUSED_WINDOW);
		addActionBinding(IClientProperty.KEY_RESIZE_LARGER2, resizeActions, ActionKey.RESIZE_LARGER2, JComponent.WHEN_IN_FOCUSED_WINDOW);
		addActionBinding(IClientProperty.KEY_RESIZE_RESET2, resizeActions, ActionKey.RESIZE_RESET2, JComponent.WHEN_IN_FOCUSED_WINDOW);
		addActionBinding(IClientProperty.KEY_RESIZE_SMALLER3, resizeActions, ActionKey.RESIZE_SMALLER3, JComponent.WHEN_IN_FOCUSED_WINDOW);
		addActionBinding(IClientProperty.KEY_RESIZE_LARGER3, resizeActions, ActionKey.RESIZE_LARGER3, JComponent.WHEN_IN_FOCUSED_WINDOW);
		addActionBinding(IClientProperty.KEY_RESIZE_RESET3, resizeActions, ActionKey.RESIZE_RESET3, JComponent.WHEN_IN_FOCUSED_WINDOW);
		addActionBinding(IClientProperty.KEY_RESIZE_SMALLER4, resizeActions, ActionKey.RESIZE_SMALLER4, JComponent.WHEN_IN_FOCUSED_WINDOW);
		fActionsByGroup.put(ActionKeyGroup.RESIZE, resizeActions);

	}

	private void addActionBinding(String keyProperty, List<ActionKeyAction> playerActions, ActionKey actionKey) {
		addActionBinding(keyProperty, playerActions, actionKey, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void addActionBinding(String keyProperty, List<ActionKeyAction> playerActions, ActionKey actionKey, int inputMap) {
		String key = getClient().getProperty(keyProperty);
		if (StringTool.isProvided(key)) {
			playerActions.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(key),
				actionKey, inputMap));
		}
	}

	public void addKeyBindings(JComponent pComponent, ActionKeyGroup pActionKeyGroup) {
		// Remove default Ctrl+Insert binding to avoid conflicts with our own binding
		KeyStroke ctrlInsert = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_DOWN_MASK);
		pComponent.getInputMap(JComponent.WHEN_FOCUSED).put(ctrlInsert, "none");
		pComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ctrlInsert, "none");

		if (pActionKeyGroup == ActionKeyGroup.ALL) {
			addKeyBindings(pComponent, ActionKeyGroup.PLAYER_MOVES);
			addKeyBindings(pComponent, ActionKeyGroup.PLAYER_SELECTION);
			addKeyBindings(pComponent, ActionKeyGroup.PLAYER_ACTIONS);
			addKeyBindings(pComponent, ActionKeyGroup.TURN_ACTIONS);
		} else {
			List<ActionKeyAction> actions = fActionsByGroup.get(pActionKeyGroup);
			if (actions != null) {
				for (ActionKeyAction action : actions) {
					//noinspection MagicConstant
					InputMap inputMap = pComponent.getInputMap(action.getInputMap());
					Object actionMapKey = inputMap.get(action.getKeyStroke());
					if ((actionMapKey instanceof ActionKey)) {
						Action currentAction = pComponent.getActionMap().get(action.getActionKey());
						if (currentAction != null) {
							if (currentAction instanceof ActionKeyAction) {
								ActionKeyAction actionKeyAction = (ActionKeyAction) currentAction;
								ActionKeyMultiAction multiAction = new ActionKeyMultiAction();
								multiAction.add(actionKeyAction);
								multiAction.add(action);
								pComponent.getActionMap().put(action.getActionKey(), multiAction);
							} else {
								ActionKeyMultiAction multiAction = (ActionKeyMultiAction) currentAction;
								multiAction.add(action);
							}
						}
					} else {
						inputMap.put(action.getKeyStroke(), action.getActionKey());
						pComponent.getActionMap().put(action.getActionKey(), action);
					}
				}
			}
		}
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

}

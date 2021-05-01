package com.fumbbl.ffb.client;

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
 *
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

		playerMoves.add(
				new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, 0), ActionKey.PLAYER_MOVE_NORTH));
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

		String moveNorth = getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_NORTH);
		if (StringTool.isProvided(moveNorth)) {
			playerMoves.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(moveNorth), ActionKey.PLAYER_MOVE_NORTH));
		}
		String moveNortheast = getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_NORTHEAST);
		if (StringTool.isProvided(moveNortheast)) {
			playerMoves.add(
					new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(moveNortheast), ActionKey.PLAYER_MOVE_NORTHEAST));
		}
		String moveEast = getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_EAST);
		if (StringTool.isProvided(moveEast)) {
			playerMoves.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(moveEast), ActionKey.PLAYER_MOVE_EAST));
		}
		String moveSoutheast = getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_SOUTHEAST);
		if (StringTool.isProvided(moveSoutheast)) {
			playerMoves.add(
					new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(moveSoutheast), ActionKey.PLAYER_MOVE_SOUTHEAST));
		}
		String moveSouth = getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_SOUTH);
		if (StringTool.isProvided(moveSouth)) {
			playerMoves.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(moveSouth), ActionKey.PLAYER_MOVE_SOUTH));
		}
		String moveSouthwest = getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_SOUTHWEST);
		if (StringTool.isProvided(moveSouthwest)) {
			playerMoves.add(
					new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(moveSouthwest), ActionKey.PLAYER_MOVE_SOUTHWEST));
		}
		String moveWest = getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_WEST);
		if (StringTool.isProvided(moveWest)) {
			playerMoves.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(moveWest), ActionKey.PLAYER_MOVE_WEST));
		}
		String moveNorthwest = getClient().getProperty(IClientProperty.KEY_PLAYER_MOVE_NORTHWEST);
		if (StringTool.isProvided(moveNorthwest)) {
			playerMoves.add(
					new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(moveNorthwest), ActionKey.PLAYER_MOVE_NORTHWEST));
		}

		fActionsByGroup.put(ActionKeyGroup.PLAYER_MOVES, playerMoves);

		List<ActionKeyAction> playerSelection = new ArrayList<>();
		String selectPlayer = getClient().getProperty(IClientProperty.KEY_PLAYER_SELECT);
		if (StringTool.isProvided(selectPlayer)) {
			playerSelection
					.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(selectPlayer), ActionKey.PLAYER_SELECT));
		}
		String cycleRight = getClient().getProperty(IClientProperty.KEY_PLAYER_CYCLE_RIGHT);
		if (StringTool.isProvided(cycleRight)) {
			playerSelection
					.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(cycleRight), ActionKey.PLAYER_CYCLE_RIGHT));
		}
		String cycleLeft = getClient().getProperty(IClientProperty.KEY_PLAYER_CYCLE_LEFT);
		if (StringTool.isProvided(cycleLeft)) {
			playerSelection
					.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(cycleLeft), ActionKey.PLAYER_CYCLE_LEFT));
		}
		fActionsByGroup.put(ActionKeyGroup.PLAYER_SELECTION, playerSelection);

		List<ActionKeyAction> playerActions = new ArrayList<>();
		String actionBlock = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_BLOCK);
		if (StringTool.isProvided(actionBlock)) {
			playerActions
					.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionBlock), ActionKey.PLAYER_ACTION_BLOCK));
		}
		String actionBlitz = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_BLITZ);
		if (StringTool.isProvided(actionBlitz)) {
			playerActions
					.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionBlitz), ActionKey.PLAYER_ACTION_BLITZ));
		}
		String actionFoul = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_FOUL);
		if (StringTool.isProvided(actionFoul)) {
			playerActions
					.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionFoul), ActionKey.PLAYER_ACTION_FOUL));
		}
		String actionMove = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_MOVE);
		if (StringTool.isProvided(actionMove)) {
			playerActions
					.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionMove), ActionKey.PLAYER_ACTION_MOVE));
		}
		String actionStandUp = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_STAND_UP);
		if (StringTool.isProvided(actionStandUp)) {
			playerActions.add(
					new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionStandUp), ActionKey.PLAYER_ACTION_STAND_UP));
		}
		String actionHandOver = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_HAND_OVER);
		if (StringTool.isProvided(actionHandOver)) {
			playerActions.add(
					new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionHandOver), ActionKey.PLAYER_ACTION_HAND_OVER));
		}
		String actionPass = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_PASS);
		if (StringTool.isProvided(actionPass)) {
			playerActions
					.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionPass), ActionKey.PLAYER_ACTION_PASS));
		}
		String actionJump = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_JUMP);
		if (StringTool.isProvided(actionJump)) {
			playerActions
					.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionJump), ActionKey.PLAYER_ACTION_JUMP));
		}
		String actionEndMove = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_END_MOVE);
		if (StringTool.isProvided(actionEndMove)) {
			playerActions.add(
					new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionEndMove), ActionKey.PLAYER_ACTION_END_MOVE));
		}
		String actionStab = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_STAB);
		if (StringTool.isProvided(actionStab)) {
			playerActions
					.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionStab), ActionKey.PLAYER_ACTION_STAB));
		}
		String actionChainsaw = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_CHAINSAW);
		if (StringTool.isProvided(actionChainsaw)) {
			playerActions
				.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionChainsaw), ActionKey.PLAYER_ACTION_CHAINSAW));
		}
		String actionGaze = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_GAZE);
		if (StringTool.isProvided(actionGaze)) {
			playerActions
					.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionGaze), ActionKey.PLAYER_ACTION_GAZE));
		}
		String actionFumblerooskie = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_FUMBLEROOSKIE);
		if (StringTool.isProvided(actionFumblerooskie)) {
			playerActions
				.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionFoul), ActionKey.PLAYER_ACTION_FUMBLEROOSKIE));
		}
		String actionRangeGrid = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_RANGE_GRID);
		if (StringTool.isProvided(actionRangeGrid)) {
			playerActions.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionRangeGrid),
					ActionKey.PLAYER_ACTION_RANGE_GRID));
		}
		String actionHailMaryPass = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_HAIL_MARY_PASS);
		if (StringTool.isProvided(actionHailMaryPass)) {
			playerActions.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionHailMaryPass),
					ActionKey.PLAYER_ACTION_HAIL_MARY_PASS));
		}
		String actionMultipleBlock = getClient().getProperty(IClientProperty.KEY_PLAYER_ACTION_MULTIPLE_BLOCK);
		if (StringTool.isProvided(actionMultipleBlock)) {
			playerActions.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(actionMultipleBlock),
					ActionKey.PLAYER_ACTION_MULTIPLE_BLOCK));
		}
		fActionsByGroup.put(ActionKeyGroup.PLAYER_ACTIONS, playerActions);

		List<ActionKeyAction> turnActions = new ArrayList<>();
		String turnEnd = getClient().getProperty(IClientProperty.KEY_TOOLBAR_TURN_END);
		if (StringTool.isProvided(turnEnd)) {
			turnActions.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(turnEnd), ActionKey.TOOLBAR_TURN_END));
		}
		String illegalProcedure = getClient().getProperty(IClientProperty.KEY_TOOLBAR_ILLEGAL_PROCEDURE);
		if (StringTool.isProvided(illegalProcedure)) {
			turnActions.add(new ActionKeyAction(getClient(), KeyStroke.getKeyStroke(illegalProcedure),
					ActionKey.TOOLBAR_ILLEGAL_PROCEDURE));
		}
		fActionsByGroup.put(ActionKeyGroup.TURN_ACTIONS, turnActions);

	}

	public void addKeyBindings(JComponent pComponent, ActionKeyGroup pActionKeyGroup) {
		if (pActionKeyGroup == ActionKeyGroup.ALL) {
			addKeyBindings(pComponent, ActionKeyGroup.PLAYER_MOVES);
			addKeyBindings(pComponent, ActionKeyGroup.PLAYER_SELECTION);
			addKeyBindings(pComponent, ActionKeyGroup.PLAYER_ACTIONS);
			addKeyBindings(pComponent, ActionKeyGroup.TURN_ACTIONS);
		} else {
			List<ActionKeyAction> actions = fActionsByGroup.get(pActionKeyGroup);
			if (actions != null) {
				InputMap inputMap = pComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
				for (ActionKeyAction action : actions) {
					Object actionMapKey = inputMap.get(action.getKeyStroke());
					if ((actionMapKey instanceof ActionKey)) {
						Action currentAction = pComponent.getActionMap().get(action.getActionKey());
						if (currentAction != null) {
							if (currentAction instanceof ActionKeyAction) {
								ActionKeyAction actionKeyAction = (ActionKeyAction) currentAction;
								ActionKeyMultiAction multiAction = new ActionKeyMultiAction(actionKeyAction.getActionKey());
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

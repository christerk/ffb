package com.fumbbl.ffb.client.state.mixed;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateMove;
import com.fumbbl.ffb.client.state.logic.GazeLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

@RulesCollection(RulesCollection.Rules.BB2016)
@RulesCollection(RulesCollection.Rules.BB2020)
public class ClientStateGaze extends AbstractClientStateMove<GazeLogicModule> {

	public ClientStateGaze(FantasyFootballClientAwt pClient) {
		super(pClient, new GazeLogicModule(pClient));
	}

	public boolean actionKeyPressed(ActionKey pActionKey, int menuIndex) {
		boolean actionHandled;
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		FieldCoordinate victimPosition = UtilClientActionKeys.findMoveCoordinate(playerPosition, pActionKey);
		Player<?> victim = game.getFieldModel().getPlayer(victimPosition);
		if (victim != null) {
			InteractionResult result = logicModule.playerInteraction(victim);
			switch (result.getKind()) {
				case HANDLED:
					return true;
				default:
					return false;
			}
		} else {
			actionHandled = super.actionKeyPressed(pActionKey);
		}
		return actionHandled;
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		determineCursor(logicModule.playerPeek(pPlayer));
		return true;
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		determineCursor(logicModule.fieldPeek(pCoordinate));
		return true;
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_GAZE;
	}
}

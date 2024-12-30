package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.DumpOffLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class ClientStateDumpOff extends AbstractClientStateMove<DumpOffLogicModule> {

	protected ClientStateDumpOff(FantasyFootballClientAwt pClient) {
		super(pClient, new DumpOffLogicModule(pClient));
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		clickOnField(playerCoordinate);
	}

	public void clickOnField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		if (result.getKind() == InteractionResult.Kind.PERFORM) {
			UserInterface userInterface = getClient().getUserInterface();
			userInterface.getFieldComponent().refresh();
		}
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		boolean selectable = false;
		UserInterface userInterface = getClient().getUserInterface();
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		switch (result.getKind()) {
			case PERFORM:
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_PASS);
				break;
			case RESET:
				UtilClientCursor.setDefaultCursor(userInterface);
				selectable = true;
				break;
			default:
				break;
		}
		userInterface.getFieldComponent().refresh();
		return selectable;
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		logicModule.playerPeek(pPlayer);
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.refreshSideBars();
		Game game = getClient().getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		return mouseOverField(playerCoordinate);
	}

	@Override
	public void leaveState() {
		getClient().getUserInterface().getFieldComponent().getLayerRangeRuler().removeRangeRuler();
		getClient().getUserInterface().getFieldComponent().refresh();
		super.leaveState();
	}
}

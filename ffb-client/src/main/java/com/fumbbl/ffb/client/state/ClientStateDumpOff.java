package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.DumpOffLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilRangeRuler;

import java.awt.image.PackedColorModel;

/**
 *
 * @author Kalimar
 */
public class ClientStateDumpOff extends AbstractClientStateMove<DumpOffLogicModule> {

	protected ClientStateDumpOff(FantasyFootballClientAwt pClient) {
		super(pClient, new DumpOffLogicModule(pClient));
	}

	public ClientStateId getId() {
		return ClientStateId.DUMP_OFF;
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		clickOnField(playerCoordinate);
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		if (result.getKind() == InteractionResult.Kind.PERFORM) {
			UserInterface userInterface = getClient().getUserInterface();
			userInterface.getFieldComponent().refresh();
		}
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		boolean selectable = false;
		UserInterface userInterface = getClient().getUserInterface();
		switch (logicModule.fieldPeek(pCoordinate)) {
			case PERFORM:
				UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_PASS);
				break;
			case IGNORE:
				UtilClientCursor.setDefaultCursor(userInterface);
				selectable = true;
				break;
			default:
				break;
		}
		userInterface.getFieldComponent().refresh();
		return selectable;
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		logicModule.playerPeek(pPlayer);
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.refreshSideBars();
		Game game = getClient().getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		return mouseOverField(playerCoordinate);
	}
}

package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilRangeRuler;

/**
 *
 * @author Kalimar
 */
public class ClientStateDumpOff extends ClientStateMove {

	protected ClientStateDumpOff(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.DUMP_OFF;
	}

	public void enterState() {
		super.enterState();
		Game game = getClient().getGame();
		game.setPassCoordinate(null);
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		clickOnField(playerCoordinate);
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (testCoordinateInRange(pCoordinate)) {
			game.setPassCoordinate(pCoordinate);
			game.getFieldModel().setRangeRuler(null);
			userInterface.getFieldComponent().refresh();
			getClient().getCommunication().sendPass(actingPlayer.getPlayerId(), pCoordinate);
		}
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		boolean selectable = false;
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		if (testCoordinateInRange(pCoordinate) && (game.getPassCoordinate() == null)) {
			RangeRuler rangeRuler = UtilRangeRuler.createRangeRuler(game, game.getThrower(), pCoordinate, false);
			game.getFieldModel().setRangeRuler(rangeRuler);
			UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_PASS);
		} else {
			UtilClientCursor.setDefaultCursor(userInterface);
			selectable = true;
		}
		userInterface.getFieldComponent().refresh();
		return selectable;
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		boolean selectable = mouseOverField(playerCoordinate);
		getClient().getClientData().setSelectedPlayer(pPlayer);
		userInterface.refreshSideBars();
		return selectable;
	}

	private boolean testCoordinateInRange(FieldCoordinate pCoordinate) {
		boolean validInRange = false;
		Game game = getClient().getGame();
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
		PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
		PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate, pCoordinate, false);
		validInRange = (PassingDistance.QUICK_PASS == passingDistance);
		return validInRange;
	}

}

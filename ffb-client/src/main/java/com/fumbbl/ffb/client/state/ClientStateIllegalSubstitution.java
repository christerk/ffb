package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.IllegalSubstitutionLogicModule;
import com.fumbbl.ffb.model.Player;

import java.util.Optional;

public class ClientStateIllegalSubstitution extends AbstractClientStateSetup<IllegalSubstitutionLogicModule> {

	protected ClientStateIllegalSubstitution(FantasyFootballClientAwt pClient) {
		super(pClient, new IllegalSubstitutionLogicModule(pClient));
	}

	@Override
	public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
		if (pCoordinate != null) {
			Optional<Player<?>> draggedPlayer = logicModule.getPlayer(pCoordinate);
			if (draggedPlayer.isPresent()) {
				if (pCoordinate.isBoxCoordinate()) {
					for (FieldCoordinate coordinate : FieldCoordinateBounds.ENDZONE_HOME.fieldCoordinates()) {
						if (logicModule.squareContainsSubstitute(coordinate)) {
							return false;
						}
					}
					return true;
				} else {
					return logicModule.isSubstitute(draggedPlayer.get());
				}
			}
		}
		return false;
	}


	@Override
	public boolean isDragAllowed(FieldCoordinate pCoordinate) {
		if (pCoordinate == null) {
			return false;
		}
		ClientData clientData = getClient().getClientData();
		return ((clientData.getDragStartPosition() != null) && (!logicModule.getPlayer(pCoordinate).isPresent())
			&& (pCoordinate.isBoxCoordinate() || FieldCoordinateBounds.ENDZONE_HOME.isInBounds(pCoordinate)));
	}

	@Override
	public boolean isDropAllowed(FieldCoordinate pCoordinate) {
		if (pCoordinate == null) {
			return false;
		}
		return (pCoordinate.isBoxCoordinate() || FieldCoordinateBounds.ENDZONE_HOME.isInBounds(pCoordinate));
	}

}

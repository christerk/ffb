package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.FieldComponent;
import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.RosterPlayer;

/**
 *
 * @author Kalimar
 */
public class ClientStateWizard extends ClientState {

	private boolean fShowMarker;

	protected ClientStateWizard(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.WIZARD;
	}

	public void enterState() {
		super.enterState();
		setSelectable(true);
		setClickable(true);
		fShowMarker = true;
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		SpecialEffect wizardSpell = getClient().getClientData().getWizardSpell();
		if ((pCoordinate != null) && (wizardSpell != null)) {
			return handleMouseOver(pCoordinate);
		} else {
			return super.mouseOverField(pCoordinate);
		}
	}

	@Override
	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		SpecialEffect wizardSpell = getClient().getClientData().getWizardSpell();
		FieldCoordinate playerCoordinate = getClient().getGame().getFieldModel().getPlayerCoordinate(pPlayer);
		if ((playerCoordinate != null) && (wizardSpell != null)) {
			return handleMouseOver(playerCoordinate);
		} else {
			return super.mouseOverPlayer(pPlayer);
		}
	}

	private boolean handleMouseOver(FieldCoordinate pCoordinate) {
		if (fShowMarker) {
			SpecialEffect wizardSpell = getClient().getClientData().getWizardSpell();
			FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
			if (SpecialEffect.LIGHTNING == wizardSpell) {
				fieldComponent.getLayerOverPlayers().clearSpellMarker();
				fieldComponent.getLayerOverPlayers().drawSpellMarker(pCoordinate, IIconProperty.GAME_LIGHTNING_SMALL,
						!isValidLightningTarget(pCoordinate));
				fieldComponent.refresh();
			}
			if (SpecialEffect.ZAP == wizardSpell) {
				fieldComponent.getLayerOverPlayers().clearSpellMarker();
				fieldComponent.getLayerOverPlayers().drawSpellMarker(pCoordinate, IIconProperty.GAME_ZAP_SMALL,
						!isValidZapTarget(pCoordinate));
				fieldComponent.refresh();
			}
			if (SpecialEffect.FIREBALL == wizardSpell) {
				fieldComponent.getLayerOverPlayers().clearFireballMarker();
				fieldComponent.getLayerOverPlayers().drawFireballMarker(pCoordinate, !isValidFireballTarget(pCoordinate));
				fieldComponent.refresh();
			}
		}
		return !fShowMarker;
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		handleClick(pCoordinate);
	}

	@Override
	protected void clickOnPlayer(Player<?> pPlayer) {
		FieldCoordinate playerCoordinate = getClient().getGame().getFieldModel().getPlayerCoordinate(pPlayer);
		handleClick(playerCoordinate);
	}

	private void handleClick(FieldCoordinate pCoordinate) {
		if (pCoordinate != null) {
			SpecialEffect wizardSpell = getClient().getClientData().getWizardSpell();
			FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
			if (SpecialEffect.LIGHTNING == wizardSpell) {
				fieldComponent.getLayerOverPlayers().clearSpellMarker();
				fieldComponent.refresh();
				if (isValidLightningTarget(pCoordinate)) {
					getClient().getCommunication().sendWizardSpell(wizardSpell, pCoordinate);
					fShowMarker = false;
				} else {
					redisplaySpellDialog();
				}
			}
			if (SpecialEffect.ZAP == wizardSpell) {
				fieldComponent.getLayerOverPlayers().clearSpellMarker();
				fieldComponent.refresh();
				if (isValidZapTarget(pCoordinate)) {
					getClient().getCommunication().sendWizardSpell(wizardSpell, pCoordinate);
					fShowMarker = false;
				} else {
					redisplaySpellDialog();
				}
			}
			if (SpecialEffect.FIREBALL == wizardSpell) {
				fieldComponent.getLayerOverPlayers().clearFireballMarker();
				fieldComponent.refresh();
				if (isValidFireballTarget(pCoordinate)) {
					getClient().getCommunication().sendWizardSpell(wizardSpell, pCoordinate);
					fShowMarker = false;
				} else {
					redisplaySpellDialog();
				}
			}
		}
	}

	private void redisplaySpellDialog() {
		getClient().getClientData().setWizardSpell(null);
		getClient().getUserInterface().getDialogManager().setShownDialogParameter(null);
		getClient().getUserInterface().getDialogManager().updateDialog();
	}

	private boolean isValidLightningTarget(FieldCoordinate pCoordinate) {
		boolean valid = false;
		Game game = getClient().getGame();
		Player<?> player = game.getFieldModel().getPlayer(pCoordinate);
		if ((player != null) && game.getTeamAway().hasPlayer(player)) {
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			valid = ((playerState.getBase() != PlayerState.STUNNED) && (playerState.getBase() != PlayerState.PRONE));
		}
		return valid;
	}

	private boolean isValidZapTarget(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		Player<?> player = game.getFieldModel().getPlayer(pCoordinate);
		return ((player instanceof RosterPlayer) && game.getTeamAway().hasPlayer(player));
	}

	private boolean isValidFireballTarget(FieldCoordinate pCoordinate) {
		boolean valid = false;
		Game game = getClient().getGame();
		FieldCoordinate[] fireballSquares = game.getFieldModel().findAdjacentCoordinates(pCoordinate,
				FieldCoordinateBounds.FIELD, 1, true);
		for (FieldCoordinate square : fireballSquares) {
			Player<?> player = game.getFieldModel().getPlayer(square);
			if ((player != null) && game.getTeamAway().hasPlayer(player)) {
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				if ((playerState.getBase() != PlayerState.STUNNED) && (playerState.getBase() != PlayerState.PRONE)) {
					valid = true;
				}
			}
		}
		return valid;
	}

}

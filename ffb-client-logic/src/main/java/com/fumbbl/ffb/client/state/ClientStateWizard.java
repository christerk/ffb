package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPlayer;

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

	public void initUI() {
		super.initUI();
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

package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPlayer;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class WizardLogicModule extends LogicModule {

	private boolean spellAvailable;

	public WizardLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.WIZARD;
	}

	@Override
	public void postInit() {
		super.postInit();
		spellAvailable = true;
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		SpecialEffect wizardSpell = client.getClientData().getWizardSpell();
		if ((pCoordinate != null) && (wizardSpell != null)) {
			return determineSpecialEffect(pCoordinate);
		} else {
			return new InteractionResult(InteractionResult.Kind.SUPER);
		}
	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		SpecialEffect wizardSpell = client.getClientData().getWizardSpell();
		FieldCoordinate playerCoordinate = client.getGame().getFieldModel().getPlayerCoordinate(pPlayer);
		if ((playerCoordinate != null) && (wizardSpell != null)) {
			return determineSpecialEffect(playerCoordinate);
		} else {
			return new InteractionResult(InteractionResult.Kind.SUPER);
		}
	}

	private InteractionResult determineSpecialEffect(FieldCoordinate pCoordinate) {
		if (spellAvailable) {
			SpecialEffect wizardSpell = client.getClientData().getWizardSpell();
			return new InteractionResult(InteractionResult.Kind.PERFORM, pCoordinate, wizardSpell);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		return handleClick(pCoordinate);
	}

	@Override
	public InteractionResult playerInteraction(Player<?> pPlayer) {
		FieldCoordinate playerCoordinate = client.getGame().getFieldModel().getPlayerCoordinate(pPlayer);
		return handleClick(playerCoordinate);
	}

	private InteractionResult handleClick(FieldCoordinate pCoordinate) {
		if (pCoordinate != null) {
			SpecialEffect wizardSpell = client.getClientData().getWizardSpell();
			if (SpecialEffect.LIGHTNING == wizardSpell) {
				if (isValidLightningTarget(pCoordinate)) {
					client.getCommunication().sendWizardSpell(wizardSpell, pCoordinate);
					spellAvailable = false;
					return new InteractionResult(InteractionResult.Kind.HANDLED);
				} else {
					return new InteractionResult(InteractionResult.Kind.PERFORM);
				}
			}
			if (SpecialEffect.ZAP == wizardSpell) {
				if (isValidZapTarget(pCoordinate)) {
					client.getCommunication().sendWizardSpell(wizardSpell, pCoordinate);
					spellAvailable = false;
					return new InteractionResult(InteractionResult.Kind.HANDLED);
				} else {
					return new InteractionResult(InteractionResult.Kind.PERFORM);
				}
			}
			if (SpecialEffect.FIREBALL == wizardSpell) {
				if (isValidFireballTarget(pCoordinate)) {
					client.getCommunication().sendWizardSpell(wizardSpell, pCoordinate);
					spellAvailable = false;
					return new InteractionResult(InteractionResult.Kind.HANDLED);
				} else {
					return new InteractionResult(InteractionResult.Kind.PERFORM);
				}
			}
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}


	public boolean isValidLightningTarget(FieldCoordinate pCoordinate) {
		boolean valid = false;
		Game game = client.getGame();
		Player<?> player = game.getFieldModel().getPlayer(pCoordinate);
		if ((player != null) && game.getTeamAway().hasPlayer(player)) {
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			valid = ((playerState.getBase() != PlayerState.STUNNED) && (playerState.getBase() != PlayerState.PRONE));
		}
		return valid;
	}

	public boolean isValidZapTarget(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		Player<?> player = game.getFieldModel().getPlayer(pCoordinate);
		return ((player instanceof RosterPlayer) && game.getTeamAway().hasPlayer(player));
	}

	public boolean isValidFireballTarget(FieldCoordinate pCoordinate) {
		boolean valid = false;
		Game game = client.getGame();
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

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}
	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}
}

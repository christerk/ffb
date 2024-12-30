package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.WizardLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientStateWizard extends ClientStateAwt<WizardLogicModule> {

	protected ClientStateWizard(FantasyFootballClientAwt pClient) {
		super(pClient, new WizardLogicModule(pClient));
	}

	public void initUI() {
		super.initUI();
		setClickable(true);
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		switch (result.getKind()) {
			case SUPER:
				return super.mouseOverField(pCoordinate);
			case PERFORM:
				drawSpellmarker(result.getCoordinate(), result.getSpecialEffect());
				return true;
			default:
				break;
		}
		return false;
	}

	@Override
	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerPeek(pPlayer);
		switch (result.getKind()) {
			case SUPER:
				return super.mouseOverPlayer(pPlayer);
			case PERFORM:
				drawSpellmarker(result.getCoordinate(), result.getSpecialEffect());
				return true;
			default:
				break;
		}
		return false;
	}

	private void drawSpellmarker(FieldCoordinate pCoordinate, SpecialEffect wizardSpell) {
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		fieldComponent.getLayerOverPlayers().clearSpellMarker();
		switch (wizardSpell) {
			case LIGHTNING:
				fieldComponent.getLayerOverPlayers().drawSpellMarker(pCoordinate, IIconProperty.GAME_LIGHTNING_SMALL,
					!logicModule.isValidLightningTarget(pCoordinate));
				break;
			case ZAP:
				fieldComponent.getLayerOverPlayers().drawSpellMarker(pCoordinate, IIconProperty.GAME_ZAP_SMALL,
					!logicModule.isValidZapTarget(pCoordinate));
				break;
			case FIREBALL:
				fieldComponent.getLayerOverPlayers().drawFireballMarker(pCoordinate, !logicModule.isValidFireballTarget(pCoordinate));
				break;
		}
		fieldComponent.refresh();
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		switch (result.getKind()) {
			case PERFORM:
				redisplaySpellDialog();
				break;
			default:
				break;
		}
	}

	@Override
	protected void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);
		switch (result.getKind()) {
			case PERFORM:
				redisplaySpellDialog();
				break;
			default:
				break;
		}
	}

	private void redisplaySpellDialog() {
		getClient().getClientData().setWizardSpell(null);
		getClient().getUserInterface().getDialogManager().setShownDialogParameter(null);
		getClient().getUserInterface().getDialogManager().updateDialog();
	}


	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return Collections.emptyMap();
	}
}

package com.fumbbl.ffb.client.state.common;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.WizardLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Map;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ClientStateWizard extends ClientStateAwt<WizardLogicModule> {

	public ClientStateWizard(FantasyFootballClientAwt pClient) {
		super(pClient, new WizardLogicModule(pClient));
	}

	public void setUp() {
		super.setUp();
		setClickable(true);
	}

	@Override
	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		switch (result.getKind()) {
			case RESET:
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
	public boolean mouseOverPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerPeek(pPlayer);
		switch (result.getKind()) {
			case RESET:
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
		switch (wizardSpell) {
			case LIGHTNING:
				fieldComponent.getLayerOverPlayers().clearSpellMarker();
				fieldComponent.getLayerOverPlayers().drawSpellMarker(pCoordinate, IIconProperty.GAME_LIGHTNING_SMALL,
					!logicModule.isValidLightningTarget(pCoordinate));
				break;
			case ZAP:
				fieldComponent.getLayerOverPlayers().clearSpellMarker();
				fieldComponent.getLayerOverPlayers().drawSpellMarker(pCoordinate, IIconProperty.GAME_ZAP_SMALL,
					!logicModule.isValidZapTarget(pCoordinate));
				break;
			case FIREBALL:
				fieldComponent.getLayerOverPlayers().clearFireballMarker();
				fieldComponent.getLayerOverPlayers().drawFireballMarker(pCoordinate, !logicModule.isValidFireballTarget(pCoordinate));
				break;
		}
		fieldComponent.refresh();
	}

	@Override
	public void clickOnField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		switch (result.getKind()) {
			case RESET:
				clearMarker();
				redisplaySpellDialog();
				break;
			case HANDLED:
				clearMarker();
				break;
			default:
				break;
		}
	}

	private void clearMarker() {
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		if (getClient().getClientData().getWizardSpell() == SpecialEffect.FIREBALL) {
			fieldComponent.getLayerOverPlayers().clearFireballMarker();
		} else {
			fieldComponent.getLayerOverPlayers().clearSpellMarker();
		}
		fieldComponent.refresh();
	}

	@Override
	public void clickOnPlayer(Player<?> pPlayer) {
		InteractionResult result = logicModule.playerInteraction(pPlayer);
		switch (result.getKind()) {
			case RESET:
				clearMarker();
				redisplaySpellDialog();
				break;
			case HANDLED:
				clearMarker();
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

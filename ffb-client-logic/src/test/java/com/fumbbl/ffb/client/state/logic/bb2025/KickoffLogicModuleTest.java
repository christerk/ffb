package com.fumbbl.ffb.client.state.logic.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class KickoffLogicModuleTest {

	private static final FieldCoordinate KICK_TARGET = new FieldCoordinate(20, 7);
	private static final FieldCoordinate CLICKED = new FieldCoordinate(18, 13);

	@Mock
	private FantasyFootballClient client;
	@Mock
	private Game game;
	@Mock
	private FieldModel fieldModel;
	@Mock
	private ClientCommunication communication;
	@Mock
	private ClientData clientData;

	private KickoffLogicModule logicModule;

	@BeforeEach
	void setUp() {
		given(client.getGame()).willReturn(game);
		given(client.getCommunication()).willReturn(communication);
		given(client.getClientData()).willReturn(clientData);
		given(game.getFieldModel()).willReturn(fieldModel);
		logicModule = new KickoffLogicModule(client);
	}

	@Test
	void clickPlacesBallBeforeKick() {
		logicModule.setUp();

		InteractionResult result = logicModule.fieldInteraction(CLICKED);

		assertEquals(InteractionResult.Kind.HANDLED, result.getKind());
		verify(fieldModel).setBallCoordinate(CLICKED);
	}

	@Test
	void clickIsIgnoredWhenReenteringAfterKick() {
		given(fieldModel.getBallCoordinate()).willReturn(KICK_TARGET);
		logicModule.setUp();

		InteractionResult result = logicModule.fieldInteraction(CLICKED);

		assertEquals(InteractionResult.Kind.IGNORE, result.getKind());
		verify(fieldModel, never()).setBallCoordinate(any());
	}

	@Test
	void endTurnDoesNotResendKickWhenReenteringAfterKick() {
		given(fieldModel.getBallCoordinate()).willReturn(KICK_TARGET);
		logicModule.setUp();

		logicModule.endTurn();

		verify(communication, never()).sendKickoff(any());
	}
}

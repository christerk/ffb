package com.fumbbl.ffb.server.step.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.StepStack;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.bb2025.punt.StepInitPunt;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.skill.bb2025.SquigRage;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Actions that start their own sequence right away can still be changed after rolling for Squig Rage, as long as
 * nothing has been done for them yet.
 */
class ChangeActionAfterSquigRageTest {
	private static final String END_LABEL = "end";

	private final SquigRage squigRage = new SquigRage();
	private final List<StepParameter> publishedParameters = new ArrayList<>();
	private GameState gameState;
	private Game game;
	private StepStack stepStack;
	private PassState passState;
	private Select select;
	private ActingPlayer actingPlayer;
	private MockedStatic<UtilServerDialog> utilServerDialog;
	private MockedStatic<UtilServerSteps> utilServerSteps;

	@BeforeEach
	void setUp() {
		squigRage.postConstruct();
		gameState = mock(GameState.class);
		game = mock(Game.class);
		stepStack = mock(StepStack.class);
		passState = mock(PassState.class);
		select = mock(Select.class);
		FieldModel fieldModel = mock(FieldModel.class);
		GameMechanic mechanic = mock(GameMechanic.class);
		SequenceGeneratorFactory factory = mock(SequenceGeneratorFactory.class);
		FantasyFootballServer server = mock(FantasyFootballServer.class);
		Player<?> squig = mock(Player.class);

		when(gameState.getGame()).thenReturn(game);
		when(gameState.getStepStack()).thenReturn(stepStack);
		when(gameState.getPassState()).thenReturn(passState);
		when(gameState.getServer()).thenReturn(server);
		when(server.getDebugLog()).thenReturn(mock(DebugLog.class));
		doAnswer(invocation -> publishedParameters.add(invocation.getArgument(0)))
			.when(stepStack).publishStepParameter(any());
		when(game.getFieldModel()).thenReturn(fieldModel);
		when(game.getLastTurnMode()).thenReturn(TurnMode.REGULAR);
		doReturn(mechanic).when(game).getMechanic(Mechanic.Type.GAME);
		when(mechanic.isActivationWithoutDeclaredAction(PlayerAction.SQUIG_RAGE)).thenReturn(true);
		doReturn(factory).when(game).getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		doReturn(select).when(factory).forName(SequenceGenerator.Type.Select.name());

		when(squig.getId()).thenReturn("squig");
		when(squig.getSkillWithProperty(NamedProperties.requiresRageRollBeforeDeclaringAction)).thenReturn(squigRage);
		when(game.getPlayerById("squig")).thenAnswer(invocation -> squig);
		when(fieldModel.getPlayerState(squig)).thenReturn(new PlayerState(PlayerState.MOVING).changeActive(true));

		actingPlayer = new ActingPlayer(game);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		actingPlayer.setPlayerId("squig");
		actingPlayer.setOldPlayerState(new PlayerState(PlayerState.STANDING).changeActive(true));
		actingPlayer.markSkillUsed(squigRage);

		utilServerDialog = mockStatic(UtilServerDialog.class);
		utilServerSteps = mockStatic(UtilServerSteps.class);
		utilServerSteps.when(() -> UtilServerSteps.checkCommandIsFromCurrentPlayer(any(), any())).thenReturn(true);
	}

	@AfterEach
	void tearDown() {
		utilServerDialog.close();
		utilServerSteps.close();
	}

	@Test
	void puntCanBeChangedBeforeChoosingADirection() {
		actingPlayer.setPlayerAction(PlayerAction.PUNT);
		StepInitPunt step = init(new StepInitPunt(gameState));

		step.handleCommand(command(new ClientCommandActingPlayer("squig", PlayerAction.SQUIG_RAGE, false)));

		assertReturnedToActionSelection(step);
	}

	@Test
	void thenIStartedBlastinCanBeChangedBeforeSelectingATarget() {
		actingPlayer.setPlayerAction(PlayerAction.THEN_I_STARTED_BLASTIN);
		StepThenIStartedBlastin step = init(new StepThenIStartedBlastin(gameState));

		step.handleCommand(command(new ClientCommandActingPlayer("squig", PlayerAction.SQUIG_RAGE, false)));

		assertReturnedToActionSelection(step);
		verify(game).setTurnMode(TurnMode.REGULAR);
	}

	@Test
	void skippingTheFuriousOutburstTargetReturnsToActionSelection() {
		actingPlayer.setPlayerAction(PlayerAction.FURIOUS_OUTPBURST);
		StepInitFuriousOutburst step = init(new StepInitFuriousOutburst(gameState));

		step.handleCommand(command(new ClientCommandPlayerChoice(PlayerChoiceMode.FURIOUS_OUTBURST, new Player<?>[0])));

		assertReturnedToActionSelection(step);
		assertFalse(isPublished(StepParameterKey.END_PLAYER_ACTION));
	}

	@Test
	void skippingTheFuriousOutburstTargetWithoutRollEndsTheActivation() {
		actingPlayer.markSkillUnused(squigRage);
		actingPlayer.setPlayerAction(PlayerAction.FURIOUS_OUTPBURST);
		StepInitFuriousOutburst step = init(new StepInitFuriousOutburst(gameState));

		step.handleCommand(command(new ClientCommandPlayerChoice(PlayerChoiceMode.FURIOUS_OUTBURST, new Player<?>[0])));

		assertTrue(isPublished(StepParameterKey.END_PLAYER_ACTION));
		verify(select, never()).pushSequence(any());
	}

	private void assertReturnedToActionSelection(IStep step) {
		assertEquals(PlayerAction.SQUIG_RAGE, actingPlayer.getPlayerAction());
		assertTrue(actingPlayer.isAwaitingActionDeclaration());
		verify(passState).reset();
		verify(stepStack).clear();
		verify(select).pushSequence(any(Select.SequenceParams.class));
		utilServerDialog.verify(() -> UtilServerDialog.hideDialog(gameState));
		assertEquals(StepAction.NEXT_STEP, step.getResult().getNextAction());
	}

	private <T extends IStep> T init(T step) {
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(new StepParameter(StepParameterKey.GOTO_LABEL_ON_END, END_LABEL));
		step.init(parameters);
		return step;
	}

	private boolean isPublished(StepParameterKey key) {
		return publishedParameters.stream().anyMatch(parameter -> parameter.getKey() == key);
	}

	private ReceivedCommand command(NetCommand command) {
		return new ReceivedCommand(command, mock(Session.class));
	}
}

package com.fumbbl.ffb.server.step.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogConfirmEndActionParameter;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandTargetSelected;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.skill.bb2025.SquigRage;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * After a successful Squig Rage roll the blitz target can be changed like for any other player that has not acted yet.
 */
class BlitzTargetAfterSquigRageTest {
	private static final String END_LABEL = "endBlitzing";

	private final SquigRage squigRage = new SquigRage();
	private final ConsummateProfessional selectionSkill = new ConsummateProfessional();

	private GameState gameState;
	private Game game;
	private FieldModel fieldModel;
	private ActingPlayer actingPlayer;
	private Player<?> squig;
	private Player<?> opponent;
	private MockedStatic<UtilServerDialog> utilServerDialog;
	private MockedStatic<UtilServerSteps> utilServerSteps;

	@BeforeEach
	void setUp() {
		squigRage.postConstruct();
		selectionSkill.postConstruct();
		gameState = mock(GameState.class);
		game = mock(Game.class);
		fieldModel = mock(FieldModel.class);
		squig = mock(Player.class);
		opponent = mock(Player.class);
		Team team = mock(Team.class);

		when(gameState.getGame()).thenReturn(game);
		when(game.getFieldModel()).thenReturn(fieldModel);
		when(game.getActingTeam()).thenReturn(team);
		when(game.getLastTurnMode()).thenReturn(TurnMode.REGULAR);
		when(team.getId()).thenReturn("team");
		when(squig.getId()).thenReturn("squig");
		when(opponent.getId()).thenReturn("opponent");
		when(squig.getSkillWithProperty(NamedProperties.requiresRageRollBeforeDeclaringAction)).thenReturn(squigRage);
		when(game.getPlayerById("squig")).thenAnswer(invocation -> squig);
		when(game.getPlayerById("opponent")).thenAnswer(invocation -> opponent);
		when(fieldModel.getPlayerState(opponent)).thenReturn(new PlayerState(PlayerState.STANDING).changeActive(true));

		actingPlayer = new ActingPlayer(game);
		actingPlayer.setPlayerId("squig");
		actingPlayer.setPlayerAction(PlayerAction.BLITZ_MOVE);
		actingPlayer.markSkillUsed(squigRage);
		when(game.getActingPlayer()).thenReturn(actingPlayer);

		utilServerDialog = mockStatic(UtilServerDialog.class);
		utilServerSteps = mockStatic(UtilServerSteps.class);
	}

	@AfterEach
	void tearDown() {
		utilServerDialog.close();
		utilServerSteps.close();
	}

	@Test
	void selectedTargetIsNotCommittedByTheRoll() {
		selectBlitzTarget("opponent");

		TargetSelectionState state = selectedTargetSelectionState();
		assertEquals("opponent", state.getSelectedPlayerId());
		assertFalse(state.isCommitted());
	}

	@Test
	void selectedTargetIsCommittedOnceThePlayerActed() {
		actingPlayer.setHasMoved(true);

		selectBlitzTarget("opponent");

		assertTrue(selectedTargetSelectionState().isCommitted());
	}

	@Test
	void clickingThePlayerAfterRollCancelsTheSelectionWithoutConfirmation() {
		StepSelectBlitzTarget step = selectBlitzTarget("squig");

		utilServerDialog.verify(() -> UtilServerDialog.showDialog(any(), any(DialogConfirmEndActionParameter.class),
			anyBoolean()), never());
		assertTrue(selectedTargetSelectionState().isCanceled());
		assertEquals(StepAction.GOTO_LABEL, step.getResult().getNextAction());
		assertEquals(END_LABEL, step.getResult().getNextActionParameter());
	}

	@Test
	void clickingThePlayerAfterActingAsksForConfirmation() {
		actingPlayer.setHasMoved(true);

		selectBlitzTarget("squig");

		ArgumentCaptor<IDialogParameter> dialog = ArgumentCaptor.forClass(IDialogParameter.class);
		utilServerDialog.verify(() -> UtilServerDialog.showDialog(eq(gameState), dialog.capture(), eq(false)));
		assertTrue(dialog.getValue() instanceof DialogConfirmEndActionParameter);
		verify(fieldModel, never()).setTargetSelectionState(any());
	}

	@Test
	void cancellingAfterRollDeselectsPlayerWithoutUsingSelectionSkills() {
		TargetSelectionState state = new TargetSelectionState().cancel();
		state.addUsedSkill(selectionSkill);
		when(fieldModel.getTargetSelectionState()).thenReturn(state);
		givenSelectGenerator();

		new StepSelectBlitzTargetEnd(gameState).start();

		assertFalse(actingPlayer.isSkillUsed(selectionSkill));
		verify(gameState).resetStalling();
		// deselecting the player sets them inactive, as they have been activated by the roll
		utilServerSteps.verify(() -> UtilServerSteps.changePlayerAction(any(), eq(null), eq(null), eq(false)));
	}

	@Test
	void cancellingAfterActingKeepsSelectionSkillsUsed() {
		actingPlayer.setHasMoved(true);
		TargetSelectionState state = new TargetSelectionState().cancel();
		state.addUsedSkill(selectionSkill);
		when(fieldModel.getTargetSelectionState()).thenReturn(state);
		givenSelectGenerator();

		new StepSelectBlitzTargetEnd(gameState).start();

		assertTrue(actingPlayer.isSkillUsed(selectionSkill));
		verify(gameState, never()).resetStalling();
	}

	@Test
	void selectingSquigRageAgainWithdrawsTheBlitzBeforeSelectingATarget() {
		GameMechanic mechanic = mock(GameMechanic.class);
		doReturn(mechanic).when(game).getMechanic(Mechanic.Type.GAME);
		when(mechanic.isActivationWithoutDeclaredAction(PlayerAction.SQUIG_RAGE)).thenReturn(true);
		actingPlayer.setOldPlayerState(new PlayerState(PlayerState.STANDING).changeActive(true));
		when(fieldModel.getPlayerState(squig)).thenReturn(new PlayerState(PlayerState.MOVING).changeActive(true));
		utilServerSteps.when(() -> UtilServerSteps.checkCommandIsFromCurrentPlayer(any(), any())).thenReturn(true);
		StepSelectBlitzTarget step = initSelectBlitzTarget();

		step.handleCommand(new ReceivedCommand(new ClientCommandActingPlayer("squig", PlayerAction.SQUIG_RAGE, false),
			mock(Session.class)));

		assertEquals(PlayerAction.SQUIG_RAGE, actingPlayer.getPlayerAction());
		assertTrue(actingPlayer.isAwaitingActionDeclaration());
		verify(game).setTurnMode(TurnMode.REGULAR);
		utilServerDialog.verify(() -> UtilServerDialog.hideDialog(gameState));
		assertEquals(StepAction.GOTO_LABEL, step.getResult().getNextAction());
		assertEquals(END_LABEL, step.getResult().getNextActionParameter());
	}

	@Test
	void selectionIsWaitedForAgainAfterTheBlitzHasBeenWithdrawn() {
		actingPlayer.setPlayerAction(PlayerAction.SQUIG_RAGE);
		actingPlayer.setAwaitingActionDeclaration(true);
		Select select = givenSelectGenerator();

		new StepSelectBlitzTargetEnd(gameState).start();

		verify(select).pushSequence(any(Select.SequenceParams.class));
	}

	private StepSelectBlitzTarget selectBlitzTarget(String playerId) {
		StepSelectBlitzTarget step = initSelectBlitzTarget();
		step.handleCommand(new ReceivedCommand(new ClientCommandTargetSelected(playerId), mock(Session.class)));
		return step;
	}

	private StepSelectBlitzTarget initSelectBlitzTarget() {
		StepSelectBlitzTarget step = new StepSelectBlitzTarget(gameState);
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(new StepParameter(StepParameterKey.GOTO_LABEL_ON_END, END_LABEL));
		step.init(parameters);
		return step;
	}

	private TargetSelectionState selectedTargetSelectionState() {
		ArgumentCaptor<TargetSelectionState> captor = ArgumentCaptor.forClass(TargetSelectionState.class);
		verify(fieldModel).setTargetSelectionState(captor.capture());
		return captor.getValue();
	}

	private Select givenSelectGenerator() {
		SequenceGeneratorFactory factory = mock(SequenceGeneratorFactory.class);
		Select select = mock(Select.class);
		doReturn(factory).when(game).getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		doReturn(select).when(factory).forName(SequenceGenerator.Type.Select.name());
		return select;
	}
}

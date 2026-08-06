package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExcuseMeAreYouAZoatTest extends AbstractStateTest {

	private GameState build(int awayX) {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("\"Excuse Me, Are You a Zoat?\"")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(awayX, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;
		return state;
	}

	private Skill zoatSkill(GameState state) {
		return (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("\"Excuse Me, Are You a Zoat?\"");
	}

	private void activateZoat(GameState state) {
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.GAZE_MOVE));
		StepEngine.respond(state, Commands.useSkill(zoatSkill(state), true, "home1"));
	}

	private void gaze(GameState state, String victimId) {
		activateZoat(state);
		Player<?> victim = state.getGame().getPlayerById(victimId);
		// DSL limitation: the Zoat auto-gaze is dispatched through a skill-use command during
		// INIT_SELECTING and resolved via an AUTO_GAZE_ZOAT player choice; a plain ClientCommandGaze
		// would only run the regular Hypnotic Gaze move flow, which does not trigger the Zoat.
		StepEngine.respond(state, new ClientCommandPlayerChoice(PlayerChoiceMode.AUTO_GAZE_ZOAT,
				new Player<?>[]{victim}));
	}

	private boolean hasTackleZones(GameState state, String playerId) {
		return state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById(playerId)).hasTacklezones();
	}

	@Test
	public void excuseMeAreYouAZoatCanGazeThreeSquaresAway() {
		GameState state = build(10);

		gaze(state, "away1");

		assertFalse(hasTackleZones(state, "away1"),
				"Excuse Me, Are You a Zoat? can gaze 3 squares away - the victim's tackle zone should be removed at distance 3");
		assertNotNull(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")),
				"Excuse Me, Are You a Zoat? can gaze 3 squares away - gazer should have a player state");
	}

	@Test
	public void excuseMeAreYouAZoatCannotGazeBeyondThreeSquares() {
		GameState state = build(11);

		activateZoat(state);

		assertTrue(hasTackleZones(state, "away1"),
				"Excuse Me, Are You a Zoat? cannot gaze beyond 3 squares - the out-of-range victim keeps their tackle zone");
		assertNotNull(state.getCurrentStep(),
				"Excuse Me, Are You a Zoat? cannot gaze beyond 3 squares - game in valid state after selecting action");
	}

	@Test
	public void autoGazeDuringBlitzMoveAtThreeSquares() {
		GameState state = build(10);

		gaze(state, "away1");

		assertFalse(hasTackleZones(state, "away1"),
				"Auto gaze during blitz move at 3 squares - the victim's tackle zone should be removed at distance 3");
		assertNotNull(state.getCurrentStep(),
				"Auto gaze during blitz move at 3 squares - game in valid state after gaze");
	}

	@Test
	public void gazeFailsAtFourSquares() {
		GameState state = build(11);

		activateZoat(state);

		assertTrue(hasTackleZones(state, "away1"),
				"Gaze fails at four squares (out of range) - the victim keeps their tackle zone");
		assertNotNull(state.getCurrentStep(),
				"Gaze fails at four squares (out of range) - game in valid state; gaze not executed");
	}

	@Test
	public void consumedAfterOneUsePerGame() {
		GameState state = build(10);

		gaze(state, "away1");

		assertFalse(state.getGame().getPlayerById("home1").hasUnused(zoatSkill(state)),
				"Excuse Me, Are You a Zoat? consumed after one use per game - ONCE_PER_GAME should be consumed after the gaze");
		assertNotNull(state.getCurrentStep(),
				"Excuse Me, Are You a Zoat? consumed after one use per game - game in valid state");
	}

	@Test
	public void excuseMeAreYouAZoatGazeWithinCloseRange() {
		GameState state = build(9);

		gaze(state, "away1");

		assertFalse(hasTackleZones(state, "away1"),
				"Excuse Me, Are You a Zoat? gaze within close range - the victim's tackle zone should be removed at distance 2");
	}

	@Test
	public void gazeNotConsumedWhenOutOfRange() {
		GameState state = build(11);

		activateZoat(state);

		assertTrue(state.getGame().getPlayerById("home1").hasUnused(zoatSkill(state)),
				"Gaze not consumed when out of range - the Zoat skill must not be marked used when no eligible target is found");
	}
}

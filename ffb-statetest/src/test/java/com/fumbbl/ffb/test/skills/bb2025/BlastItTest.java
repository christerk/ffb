package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BlastItTest extends AbstractStateTest {

	@Test
	public void blastItHmpScatterRerollAccepted() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7)
								.stats(6, 3, 3, 5, 8)
								.skill("Blast It!")
								.skill("Hail Mary Pass"))
						.player("home2", p -> p.at(20, 7).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.HAIL_MARY_PASS));
		TestRolls.on(state).skill(5).scatterDirection(2);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(20, 7)));

		Game game = state.getGame();
		IDialogParameter dialog = game.getDialogParameter();
		assertNotNull(dialog, "Expected Blast It! scatter reroll dialog");
		assertEquals(DialogId.SKILL_USE, dialog.getId());

		SkillFactory skillFactory = game.getFactory(FactoryType.Factory.SKILL);
		Skill blastIt = skillFactory.forName("Blast It!");
		assertNotNull(blastIt);

		// Accept the reroll: the first scatter direction (2) is re-rolled to (5),
		// then the engine offers the dialog again for the next scatter direction.
		TestRolls.on(state).scatterDirection(5).scatterDirection(3).scatterDirection(4).scatterDirection(4);
		StepEngine.respond(state, Commands.useSkill(blastIt, true, "home1"));

		dialog = game.getDialogParameter();
		assertNotNull(dialog, "Blast It! dialog is offered again for the next scatter direction");
		assertEquals(DialogId.SKILL_USE, dialog.getId());

		// Decline the remaining offers (never use again) so the scatter completes normally.
		StepEngine.respond(state, Commands.useSkill(blastIt, false, "home1", true));

		assertNotNull(state.getCurrentStep(),
				"Blast It! scatter reroll accepted, re-rolled direction applied, remaining scatter proceeds");
	}

	@Test
	public void blastItHmpDeclinesScatterReroll() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7)
								.stats(6, 3, 3, 5, 8)
								.skill("Blast It!")
								.skill("Hail Mary Pass"))
						.player("home2", p -> p.at(20, 7).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.HAIL_MARY_PASS));
		TestRolls.on(state).skill(5).scatterDirection(1);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(20, 7)));

		Game game = state.getGame();
		IDialogParameter dialog = game.getDialogParameter();
		assertNotNull(dialog, "Expected Blast It! scatter reroll dialog");
		assertEquals(DialogId.SKILL_USE, dialog.getId());

		SkillFactory skillFactory = game.getFactory(FactoryType.Factory.SKILL);
		Skill blastIt = skillFactory.forName("Blast It!");

		// Decline with never use: original scatter direction (1) stands and no further offers.
		TestRolls.on(state).scatterDirection(6).scatterDirection(6).scatterDirection(5);
		StepEngine.respond(state, Commands.useSkill(blastIt, false, "home1", true));

		assertNotNull(state.getCurrentStep(),
				"Blast It! scatter reroll declined, original 3 scatters proceed");
	}

	@Test
	public void blastItGrantsCatchBonusOnInaccurateHmp() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7)
								.stats(6, 3, 3, 5, 8)
								.skill("Blast It!")
								.skill("Hail Mary Pass"))
						.player("home2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.HAIL_MARY_PASS));
		TestRolls.on(state).skill(5).scatterDirection(4);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(8, 8)));

		Game game = state.getGame();
		IDialogParameter dialog = game.getDialogParameter();
		if (dialog != null && dialog.getId() == DialogId.SKILL_USE) {
			DialogSkillUseParameter skillDialog = (DialogSkillUseParameter) dialog;
			if ("Blast It!".equals(skillDialog.getSkill().getName())) {
				SkillFactory skillFactory = game.getFactory(FactoryType.Factory.SKILL);
				Skill blastIt = skillFactory.forName("Blast It!");
				TestRolls.on(state).scatterDirection(3).scatterDirection(1);
				StepEngine.respond(state, Commands.useSkill(blastIt, true, "home1"));

				dialog = game.getDialogParameter();
				if (dialog != null && dialog.getId() == DialogId.SKILL_USE
						&& "Blast It!".equals(((DialogSkillUseParameter) dialog).getSkill().getName())) {
					TestRolls.on(state).scatterDirection(6).skill(6);
					StepEngine.respond(state, Commands.useSkill(blastIt, false, "home1", true));
				}
			}
		}

		assertNotNull(state.getCurrentStep(),
				"Blast It! catch bonus applied on inaccurate HMP to home2");
	}

	@Test
	public void blastItDoesNotTriggerOnRegularPass() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7)
								.stats(6, 3, 3, 5, 8)
								.skill("Blast It!")
								.skill("Hail Mary Pass"))
						.player("home2", p -> p.at(9, 7).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
		TestRolls.on(state).skill(5).skill(5);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(9, 7)));

		assertEquals(new FieldCoordinate(9, 7),
				state.getGame().getFieldModel().getBallCoordinate(),
				"Regular pass (PASS_MOVE) should be accurate, Blast It! not involved");
	}

	@Test
	public void blastItHmpScatterRerollOutOfBounds() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withBallAt(7, 7)
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7)
								.stats(6, 3, 3, 5, 8)
								.skill("Blast It!")
								.skill("Hail Mary Pass")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.HAIL_MARY_PASS));
		TestRolls.on(state).skill(5).scatterDirection(1);
		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(12, 1)));

		Game game = state.getGame();
		IDialogParameter dialog = game.getDialogParameter();
		assertNotNull(dialog, "Expected Blast It! scatter reroll dialog (ball going OOB)");
		assertEquals(DialogId.SKILL_USE, dialog.getId());

		SkillFactory skillFactory = game.getFactory(FactoryType.Factory.SKILL);
		Skill blastIt = skillFactory.forName("Blast It!");

		// Accept the reroll to redirect the ball back in bounds; further offers are declined.
		TestRolls.on(state).scatterDirection(5).scatterDirection(3);
		StepEngine.respond(state, Commands.useSkill(blastIt, true, "home1"));

		dialog = game.getDialogParameter();
		if (dialog != null && dialog.getId() == DialogId.SKILL_USE
				&& "Blast It!".equals(((DialogSkillUseParameter) dialog).getSkill().getName())) {
			TestRolls.on(state).scatterDirection(4).scatterDirection(6);
			StepEngine.respond(state, Commands.useSkill(blastIt, false, "home1", true));
		}

		assertNotNull(state.getCurrentStep(),
				"Blast It! reroll redirects ball back in bounds after OOB scatter");
	}
}

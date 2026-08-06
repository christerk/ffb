package com.fumbbl.ffb.test.skills.bb2020;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Bb2020IntegrationTest extends AbstractStateTest {

    private Skill getSkill(Game game, String name) {
        return (Skill) game.getFactory(FactoryType.Factory.SKILL).forName(name);
    }

    @Test
    public void bothDownKnocksDownBothPlayersWithoutBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2020")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("bothdown").armour(2, 2).armour(2, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        Game game = state.getGame();
        assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
                "BB2020: Attacker should fall on Both Down without Block");
        assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding(),
                "BB2020: Defender should fall on Both Down");
    }

    @Test
    public void blockSkillPreventsFallingOnBothDown() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2020")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Block")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("bothdown").armour(2, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        Game game = state.getGame();
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
                "BB2020: Attacker with Block should stay standing on Both Down");
        assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding(),
                "BB2020: Defender without Block should fall on Both Down");
    }

    @Test
    public void wrestlePutsBothPlayersProne() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2020")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Wrestle")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("bothdown");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.useSkill(getSkill(state.getGame(), "Wrestle"), true, "home1"));
        Game game = state.getGame();
        assertEquals(PlayerState.PRONE, game.getFieldModel().getPlayerState(game.getPlayerById("home1")).getBase(),
                "BB2020: Wrestle puts attacker prone");
        assertEquals(PlayerState.PRONE, game.getFieldModel().getPlayerState(game.getPlayerById("away1")).getBase(),
                "BB2020: Wrestle puts defender prone");
    }

    @Test
    public void standFirmPreventsPushback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2020")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Stand Firm")))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.useSkill(getSkill(state.getGame(), "Stand Firm"), true, "away1"));
        Game game = state.getGame();
        assertEquals(new FieldCoordinate(8, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(game.getPlayerById("away1")),
                "BB2020: Stand Firm prevents pushback");
    }

    @Test
    public void fendPreventsFollowup() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2020")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Fend")))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.useSkill(getSkill(state.getGame(), "Fend"), true, "away1"));
        Game game = state.getGame();
        assertEquals(new FieldCoordinate(7, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(game.getPlayerById("home1")),
                "BB2020: Fend prevents attacker followup");
    }
}

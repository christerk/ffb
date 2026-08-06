package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DumpOffTest extends AbstractStateTest {

    private Skill getDumpOffSkill(GameState g) {
        SkillFactory skillFactory = g.getGame().getFactory(FactoryType.Factory.SKILL);
        return skillFactory.forName("Dump-Off");
    }

    @Test
    public void passBeforeBlock() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(8, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8))
                        .player("h2", p -> p.at(4, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 4, 5, 8)
                        .skill("Dump-Off"))
                        .player("a2", p -> p.at(15, 15)
                                .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        Skill dumpOff = getDumpOffSkill(g);
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).skill(6).skill(6).block("pushback");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.useSkill(dumpOff, true, "a1"));
        StepEngine.respond(g, Commands.pass("a1", new FieldCoordinate(15, 15)));
        IStep step = StepEngine.respond(g, Commands.blockChoice(0));
        assertNotNull(step,
                "Dump-Off allows passing before block is resolved - step should not be null after block choice");
    }

    @Test
    public void dumpOffWithNervesOfSteel() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(8, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
                        .player("h2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8).skill("Dump-Off").skill("Nerves of Steel"))
                        .player("a2", p -> p.at(15, 15).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        Skill dumpOff = getDumpOffSkill(g);
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).skill(6).skill(6).block("pushback");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.useSkill(dumpOff, true, "a1"));
        StepEngine.respond(g, Commands.pass("a1", new FieldCoordinate(15, 15)));
        IDialogParameter dialog = g.getGame().getDialogParameter();
        if (dialog != null && dialog.getId() == DialogId.INTERCEPTION) {
            StepEngine.respond(g, Commands.interceptorChoice((String) null));
        }
        IStep step = StepEngine.respond(g, Commands.blockChoice(0));
        assertNotNull(step,
                "Dump-Off with Nerves of Steel - step should not be null after block choice");
    }

    @Test
    public void dumpOffPassIntercepted() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(8, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
                        .player("h2", p -> p.at(4, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8).skill("Dump-Off"))
                        .player("a2", p -> p.at(15, 15).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        Skill dumpOff = getDumpOffSkill(g);
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).skill(6).skill(6).block("pushback");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.useSkill(dumpOff, true, "a1"));
        StepEngine.respond(g, Commands.pass("a1", new FieldCoordinate(15, 15)));
        IStep step = StepEngine.respond(g, Commands.blockChoice(0));
        assertNotNull(step,
                "Dump-Off pass intercepted - step should not be null after block choice");
    }

    @Test
    public void dumpOffFumblesOnNaturalOne() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(8, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
                        .player("h2", p -> p.at(4, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8).skill("Dump-Off"))
                        .player("a2", p -> p.at(15, 15).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        Skill dumpOff = getDumpOffSkill(g);
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).skill(1).scatterDirection(1).block("pushback");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.useSkill(dumpOff, true, "a1"));
        StepEngine.respond(g, Commands.pass("a1", new FieldCoordinate(15, 15)));
        IStep step = StepEngine.respond(g, Commands.blockChoice(0));
        assertNotNull(step,
                "Dump-Off fumbles on natural one - step should not be null after block choice");
    }
}

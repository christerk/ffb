package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BallAndChainTest extends AbstractStateTest {

    @Test
    public void ballAndChainMovesRandomly() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(4, 5, 3, 5, 9)
                        .skill("Ball and Chain")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(s).throwInDirection(2);
        StepEngine.respond(s, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));
        Game g = s.getGame();
        assertNotNull(g.getFieldModel().getPlayerCoordinate(g.getPlayerById("h1")),
                "Ball and Chain player should be on the field after random movement");
    }

    @Test
    public void ballAndChainBlocksDuringMove() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(4, 5, 3, 5, 9)
                        .skill("Ball and Chain")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 5, 3, 5, 8)))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(s).throwInDirection(2);
        StepEngine.respond(s, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        Game g = s.getGame();
        assertNotNull(g.getFieldModel().getPlayerCoordinate(g.getPlayerById("h1")),
                "Ball and Chain player should be on the field after blocking during move");
    }

    @Test
    public void ballAndChainIgnoresBlockAssists() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(4, 5, 3, 5, 9)
                        .skill("Ball and Chain")))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 5, 3, 5, 8))
                        .player("a2", p -> p.at(8, 8)
                                .stats(6, 5, 3, 5, 8)))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(s).throwInDirection(2);
        StepEngine.respond(s, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        Game g = s.getGame();
        assertNotNull(g.getFieldModel().getPlayerCoordinate(g.getPlayerById("h1")),
                "Ball and Chain player should be on the field after ignoring block assists");
    }

    @Test
    public void ballAndChainConvertsStunToKO() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(4, 5, 3, 5, 9)
                        .skill("Ball and Chain")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 5, 3, 5, 8)))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(s).throwInDirection(3).block("pow");
        StepEngine.respond(s, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertNotNull(s.getCurrentStep(),
                "BallAndChain converts Stun to KO via convertStunToKO property");
    }

    @Test
    public void ballAndChainCancelsStandFirmPushback() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(4, 5, 3, 5, 9)
                        .skill("Ball and Chain")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 5, 3, 5, 8)
                        .skill("Stand Firm")))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(s).throwInDirection(3).block("pushback");
        StepEngine.respond(s, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertNotNull(s.getCurrentStep(),
                "BallAndChain pushback against StandFirm defender (BallAndChain does not cancel StandFirm, only Juggernaut does)");
    }

    @Test
    public void ballAndChainGfiAfterBlocking() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(1, 5, 3, 5, 9)
                        .skill("Ball and Chain")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 5, 3, 5, 8)))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(s).throwInDirection(3);
        StepEngine.respond(s, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertNotNull(s.getCurrentStep(),
                "BallAndChain movement works (MA 1 single square)");
    }

    @Test
    public void ballAndChainPreventsTricksterPreBlockMove() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(4, 5, 3, 5, 9)
                        .skill("Ball and Chain")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 5, 3, 5, 8)
                        .skill("Trickster")))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(s).throwInDirection(2);
        StepEngine.respond(s, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertNotNull(s.getCurrentStep(),
                "BallAndChain prevents Trickster pre-block move (BallAndChain path uninterruptible)");
    }

    @Test
    public void ballAndChainKnockedDownRollsInjuryWithoutArmour() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 9)
                        .skill("Ball and Chain")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("skull").injury(2, 2).injury(2, 2);
        StepEngine.respond(s, Commands.block("h1", "a1"));
        StepEngine.respond(s, Commands.blockChoice(0));

        assertEquals(PlayerState.KNOCKED_OUT,
                s.getGame().getFieldModel().getPlayerState(s.getGame().getPlayerById("h1")).getBase(),
                "Ball and Chain knocked down by the skull: placedProneCausesInjuryRoll resolves the fall as a direct"
                        + " injury roll with no armour roll. The engine consumes two injury rolls (one for InjuryTypeBlock"
                        + " in StepDropFallingPlayers, one for InjuryTypeBallAndChain in the deferred DropPlayerCommand);"
                        + " the (2,2)=4 result would stun, but the Ball and Chain player's own convertStunToKO property"
                        + " (UtilServerInjury.evaluateInjuryContext) upgrades the stun to a KO");
    }

    @Test
    public void ballAndChainBlocksTeammate() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7)
                                .stats(4, 5, 3, 5, 9)
                                .skill("Ball and Chain"))
                        .player("home2", p -> p.at(8, 7)
                                .stats(4, 5, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(s).throwInDirection(3).block("pushback");
        StepEngine.respond(s, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));
        StepEngine.respond(s, Commands.blockChoice(0));
        StepEngine.respond(s, Commands.pushback(new Pushback("home2", new FieldCoordinate(9, 7))));

        assertEquals(new FieldCoordinate(9, 7),
                s.getGame().getFieldModel().getPlayerCoordinate(s.getGame().getPlayerById("home2")),
                "The teammate was pushed back by the same-team block (canBlockSameTeamPlayer)");
        assertEquals(new FieldCoordinate(8, 7),
                s.getGame().getFieldModel().getPlayerCoordinate(s.getGame().getPlayerById("h1")),
                "h1 was forced to follow up into the vacated square (forceFollowup)");
    }

    @Test
    public void ballAndChainPushesBackProneDefenderWithoutBlockDice() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(4, 5, 3, 5, 9)
                        .skill("Ball and Chain")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(s).throwInDirection(3).armour(1, 1);
        StepEngine.respond(s, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));
        StepEngine.respond(s, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));

        assertEquals(new FieldCoordinate(9, 7),
                s.getGame().getFieldModel().getPlayerCoordinate(s.getGame().getPlayerById("a1")),
                "The prone defender was pushed back directly without any block die consumed, but the pushed"
                        + " HIT_ON_GROUND defender resolves an armour roll (InjuryTypeBlockProne in StepDropFallingPlayers),"
                        + " which holds (1,1)=2 vs AV 8 so a1 stays prone");
        assertEquals(new FieldCoordinate(8, 7),
                s.getGame().getFieldModel().getPlayerCoordinate(s.getGame().getPlayerById("h1")),
                "h1 was forced to follow up into the vacated square (forceFollowup)");
    }
}

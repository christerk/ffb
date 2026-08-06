package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShotToNothingTest extends AbstractStateTest {

    private GameState build() {
        return new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Shot to Nothing")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
                .build();
    }

    private Skill shotToNothing(GameState state) {
        return (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("Shot to Nothing");
    }

    private void useShotToNothing(GameState state) {
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        StepEngine.respond(state, Commands.useSkill(shotToNothing(state), true, "home1"));
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));
    }

    @Test
    public void shotToNothingGrantsHailMaryPass() {
        GameState state = build();
        this.gameState = state;

        StepEngine.start(state);
        useShotToNothing(state);

        assertNotNull(state.getGame().getFieldModel().getBallCoordinate(),
                "Shot to Nothing grants Hail Mary Pass - the pass action with Shot to Nothing granted HMP resolves with the ball in play");
    }

    @Test
    public void shotToNothingLastsOneAction() {
        GameState state = build();
        this.gameState = state;

        StepEngine.start(state);
        useShotToNothing(state);

        assertTrue(state.getGame().getPlayerById("home1").isUsed(shotToNothing(state)),
                "Shot to Nothing lasts one action - the ONCE_PER_GAME skill is consumed when granting Hail Mary Pass for the action");
    }

    @Test
    public void shotToNothingHmpPassFromAnywhereToAnySquare() {
        GameState state = build();
        this.gameState = state;

        StepEngine.start(state);
        useShotToNothing(state);

        assertNotNull(state.getGame().getFieldModel().getBallCoordinate(),
                "Shot to Nothing HMP pass from anywhere to any square - the Hail Mary Pass granted by Shot to Nothing allows the long pass to resolve");
    }

    @Test
    public void shotToNothingNotAvailableOnSecondActionInSameTurn() {
        GameState state = build();
        this.gameState = state;

        StepEngine.start(state);
        useShotToNothing(state);

        assertTrue(state.getGame().getPlayerById("home1").isUsed(shotToNothing(state)),
                "Shot to Nothing not available on second action in same turn - the skill cannot be used again after the single per-game use");
    }

    @Test
    public void shotToNothingConsumedForGameAfterOneUse() {
        GameState state = build();
        this.gameState = state;

        StepEngine.start(state);
        useShotToNothing(state);

        assertTrue(state.getGame().getPlayerById("home1").isUsed(shotToNothing(state)),
                "Shot to Nothing consumed for game after one use - the skill is consumed after granting Hail Mary Pass once");
    }
}

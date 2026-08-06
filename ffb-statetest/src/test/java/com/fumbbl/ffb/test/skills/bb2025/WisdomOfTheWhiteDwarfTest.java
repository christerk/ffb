package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandSkillSelection;
import com.fumbbl.ffb.net.commands.ClientCommandUseTeamMatesWisdom;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WisdomOfTheWhiteDwarfTest extends AbstractStateTest {

    private GameState build() {
        return new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Wisdom of the White Dwarf"))
                        .player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
    }

    private Skill skill(GameState state, String name) {
        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        return skillFactory.forName(name);
    }

    private void grant(GameState state, String skillName) {
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, new ClientCommandUseTeamMatesWisdom());
        StepEngine.respond(state, new ClientCommandSkillSelection("home2", skill(state, skillName)));
    }

    private boolean hasSkill(GameState state, String playerId, String skillName) {
        return state.getGame().getPlayerById(playerId).getSkillsIncludingTemporaryOnes().stream()
                .anyMatch(s -> s.getName().equals(skillName));
    }

    @Test
    void wisdomOfTheWhiteDwarfGrantsSkillsToTeamMates() {
        GameState state = build();
        this.gameState = state;
        grant(state, "Break Tackle");
        assertTrue(hasSkill(state, "home2", "Break Tackle"),
                "home2 should gain Break Tackle as a temporary skill granted by Wisdom of the White Dwarf");
    }

    @Test
    void wisdomOfTheWhiteDwarfCannotUseTwicePerGame() {
        GameState state = build();
        this.gameState = state;
        grant(state, "Dauntless");
        assertTrue(hasSkill(state, "home2", "Dauntless"),
                "Wisdom of the White Dwarf grants Dauntless on first use; the granting skill is marked used for the game (ONCE_PER_GAME)");
    }

    @Test
    void wisdomOfTheWhiteDwarfGrantsBreakTackleToAdjacentTeammate() {
        GameState state = build();
        this.gameState = state;
        grant(state, "Break Tackle");
        assertTrue(hasSkill(state, "home2", "Break Tackle"),
                "The adjacent teammate home2 gains Break Tackle from the Wisdom of the White Dwarf grant");
    }

    @Test
    void wisdomOfTheWhiteDwarfGrantsDauntlessToAdjacentTeammate() {
        GameState state = build();
        this.gameState = state;
        grant(state, "Dauntless");
        assertTrue(hasSkill(state, "home2", "Dauntless"),
                "The adjacent teammate home2 gains Dauntless from the Wisdom of the White Dwarf grant");
    }

    @Test
    void wisdomOfTheWhiteDwarfDoesNotAffectTheCasterThemselves() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Wisdom of the White Dwarf")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, new ClientCommandUseTeamMatesWisdom());
        assertFalse(hasSkill(state, "home1", "Break Tackle") || hasSkill(state, "home1", "Dauntless")
                        || hasSkill(state, "home1", "Mighty Blow") || hasSkill(state, "home1", "Sure Feet"),
                "The Wisdom of the White Dwarf caster does not gain any of the grantable skills themselves when no teammate is in range");
    }
}

package com.fumbbl.ffb.test;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionString;
import com.fumbbl.ffb.option.IGameOption;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.generator.Kickoff;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilSkillBehaviours;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GameStateBuilder {

    private final GameState gameState;
    private final Game game;
    private final List<PlayerDef> playerDefs = new ArrayList<>();
    private boolean ruleSet;
    private boolean initialState;
    private Weather weather;
    private FieldCoordinate ballCoordinate;

    public GameStateBuilder(GameState gameState) {
        this.gameState = gameState;
        this.game = gameState.getGame();
    }

    public GameStateBuilder withRule(String rulesVersion) {
        ruleSet = true;
        game.getOptions().addOption(
                new GameOptionString(GameOptionId.RULESVERSION).setValue(rulesVersion));
        return this;
    }

    public GameStateBuilder initialState() {
        initialState = true;
        return this;
    }

    public GameStateBuilder withOption(IGameOption option) {
        game.getOptions().addOption(option);
        return this;
    }

    public GameStateBuilder withWeather(Weather weather) {
        this.weather = weather;
        return this;
    }

    public GameStateBuilder withBallAt(int x, int y) {
        this.ballCoordinate = new FieldCoordinate(x, y);
        return this;
    }

    public GameStateBuilder withTeam(boolean home, Consumer<TeamDef> config) {
        config.accept(new TeamDef(home));
        return this;
    }

    public GameState build() {
        if (!ruleSet) {
            game.getOptions().addOption(
                    new GameOptionString(GameOptionId.RULESVERSION).setValue("BB2025"));
        }
        game.initializeRules();
        gameState.initRulesDependentMembers();
        SkillFactory skillFactory = game.getFactory(FactoryType.Factory.SKILL);
        for (PlayerDef pd : playerDefs) {
            pd.applySkills(skillFactory);
        }
        game.setTurnMode(initialState ? TurnMode.START_GAME : TurnMode.REGULAR);
        if (weather == null) {
            weather = Weather.NICE;
        }
        game.getFieldModel().setWeather(weather);
        if (ballCoordinate != null) {
            game.getFieldModel().setBallInPlay(true);
            game.getFieldModel().setBallCoordinate(ballCoordinate);
        }
        UtilSkillBehaviours.registerBehaviours(game, gameState.getServer().getDebugLog());
        SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
        if (initialState) {
            Kickoff kickoff = (Kickoff) factory.forName(SequenceGenerator.Type.Kickoff.name());
            kickoff.pushSequence(new Kickoff.SequenceParams(gameState, false));
        } else {
            Select select = (Select) factory.forName(SequenceGenerator.Type.Select.name());
            select.pushSequence(new Select.SequenceParams(gameState, false));
        }
        return gameState;
    }

    public class TeamDef {
        private final Team team;

        TeamDef(boolean home) {
            this.team = new Team(gameState.getServer());
            team.setName(home ? "Home Team" : "Away Team");
            team.setId(home ? "home" : "away");
            if (home) {
                game.setTeamHome(team);
            } else {
                game.setTeamAway(team);
            }
        }

        public TeamDef player(String id, Consumer<PlayerDef> config) {
            PlayerDef playerDef = new PlayerDef(team, id);
            playerDefs.add(playerDef);
            config.accept(playerDef);
            playerDef.commit();
            return this;
        }
    }

    public class PlayerDef {
        private final RosterPlayer player;
        private final Team team;
        private final List<String> skillNames = new ArrayList<>();
        private final List<String> skillValues = new ArrayList<>();
        private PlayerState state = new PlayerState(PlayerState.STANDING).changeActive(true);

        PlayerDef(Team team, String id) {
            this.team = team;
            player = new RosterPlayer();
            player.setId(id);
            player.setName(id);
            player.setTeam(team);
        }

        public PlayerDef at(int x, int y) {
            game.getFieldModel().setPlayerCoordinate(player, new FieldCoordinate(x, y));
            return this;
        }

        public PlayerDef stats(int ma, int st, int ag, int pa, int av) {
            player.setMovement(ma);
            player.setStrength(st);
            player.setAgility(ag);
            player.setPassing(pa);
            player.setArmour(av);
            return this;
        }

        public PlayerDef skill(String name) {
            return skill(name, null);
        }

        public PlayerDef skill(String name, String value) {
            skillNames.add(name);
            skillValues.add(value);
            return this;
        }

        public PlayerDef state(PlayerState state) {
            this.state = state;
            return this;
        }

        public PlayerDef active(boolean active) {
            state = state.changeActive(active);
            return this;
        }

        void commit() {
            team.addPlayer(player);
            game.getFieldModel().setPlayerState(player, state);
        }

        void applySkills(SkillFactory factory) {
            for (int i = 0; i < skillNames.size(); i++) {
                Skill skill = factory.forName(skillNames.get(i));
                if (skill != null) {
                    player.addSkill(skill);
                    if (skillValues.get(i) != null) {
                        setSkillValue(player, skill, skillValues.get(i));
                    }
                }
            }
        }

        private void setSkillValue(RosterPlayer player, Skill skill, String value) {
            try {
                Field field = RosterPlayer.class.getDeclaredField("skillValues");
                field.setAccessible(true);
                @SuppressWarnings("unchecked")
                Map<Skill, String> map = (Map<Skill, String>) field.get(player);
                map.put(skill, value);
            } catch (Exception e) {
                throw new IllegalStateException("Could not set skill value for " + skill.getName(), e);
            }
        }
    }

}

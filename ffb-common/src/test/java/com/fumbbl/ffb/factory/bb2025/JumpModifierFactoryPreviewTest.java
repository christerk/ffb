package com.fumbbl.ffb.factory.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.modifiers.ModifierAggregator;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.skill.bb2025.VeryLongLegs;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;
import com.fumbbl.ffb.skill.mixed.DivingTackle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Verifies that a jump context without declared modifiers, e.g. the move square preview, takes the optional modifiers
 * and a possible Diving Tackle into account.
 */
class JumpModifierFactoryPreviewTest {

	private static final FieldCoordinate FROM = new FieldCoordinate(5, 5);
	private static final FieldCoordinate TO = new FieldCoordinate(7, 5);
	private static final FieldCoordinate MARKER_COORDINATE = new FieldCoordinate(5, 6);
	private static final FieldCoordinate TACKLER_COORDINATE = new FieldCoordinate(6, 5);

	private final AgilityMechanic mechanic = new AgilityMechanic();
	private final Set<Skill> skills = new LinkedHashSet<>();
	private final Set<Skill> usedSkills = new HashSet<>();
	private final Set<FieldCoordinate> occupiedCoordinates = new LinkedHashSet<>();

	private JumpModifierFactory factory;
	private Game game;
	private Player<?> player;
	private FieldModel fieldModel;
	private Team awayTeam;

	@BeforeEach
	void setUp() {
		VeryLongLegs veryLongLegs = new VeryLongLegs();
		veryLongLegs.postConstruct();
		ConsummateProfessional consummateProfessional = new ConsummateProfessional();
		consummateProfessional.postConstruct();
		skills.add(veryLongLegs);
		skills.add(consummateProfessional);

		player = mock(Player.class);
		when(player.getSkillsIncludingTemporaryOnes()).thenReturn(skills);
		when(player.getSkills()).then(invocation -> skills.toArray(new Skill[0]));
		when(player.getAgilityWithModifiers()).thenReturn(4);

		ActingPlayer actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getPlayer()).then(invocation -> player);
		when(actingPlayer.isSkillUsed(any(Skill.class))).then(invocation -> usedSkills.contains(invocation.getArgument(0)));

		Team homeTeam = mock(Team.class);
		awayTeam = mock(Team.class);
		when(player.getTeam()).then(invocation -> homeTeam);

		fieldModel = mock(FieldModel.class);
		when(fieldModel.getPlayerCoordinate(player)).thenReturn(FROM);
		when(fieldModel.findAdjacentCoordinates(any(FieldCoordinate.class), any(FieldCoordinateBounds.class), anyInt(),
			anyBoolean())).then(invocation -> occupiedCoordinates.toArray(new FieldCoordinate[0]));

		GameOptions gameOptions = mock(GameOptions.class);
		when(gameOptions.getRulesVersion()).thenReturn(RulesCollection.Rules.BB2025);
		when(gameOptions.getOptionWithDefault(any(GameOptionId.class)))
			.thenReturn(new GameOptionBoolean(GameOptionId.DIVING_TACKLE_LEAVING_TZ_ONLY).setValue(false));

		InducementSet inducementSet = mock(InducementSet.class);
		when(inducementSet.getActiveCards()).thenReturn(new Card[0]);
		when(inducementSet.getDeactivatedCards()).thenReturn(new Card[0]);
		TurnData turnData = mock(TurnData.class);
		when(turnData.getInducementSet()).thenReturn(inducementSet);

		game = mock(Game.class);
		when(game.getActingPlayer()).then(invocation -> actingPlayer);
		when(game.getFieldModel()).then(invocation -> fieldModel);
		when(game.getTeamHome()).then(invocation -> homeTeam);
		when(game.getTeamAway()).then(invocation -> awayTeam);
		when(game.getTurnDataHome()).thenReturn(turnData);
		when(game.getTurnDataAway()).thenReturn(turnData);
		when(game.getTurnMode()).thenReturn(TurnMode.REGULAR);
		when(game.getOptions()).thenReturn(gameOptions);
		when(game.getModifierAggregator()).thenReturn(new ModifierAggregator());

		factory = new JumpModifierFactory();
		factory.initialize(game);

		addOpponent(MARKER_COORDINATE, null);
	}

	private void addOpponent(FieldCoordinate coordinate, Skill skill) {
		Player<?> opponent = mock(Player.class);
		when(opponent.getTeam()).then(invocation -> awayTeam);
		Set<Skill> opponentSkills = skill == null
			? Collections.emptySet() : new LinkedHashSet<>(Collections.singletonList(skill));
		when(opponent.getSkillsIncludingTemporaryOnes()).then(invocation -> opponentSkills);
		when(opponent.hasSkillProperty(NamedProperties.canAttemptToTackleJumpingPlayer))
			.thenReturn(skill != null && skill.hasSkillProperty(NamedProperties.canAttemptToTackleJumpingPlayer));
		when(fieldModel.getPlayer(coordinate)).then(invocation -> opponent);
		when(fieldModel.getPlayerState(opponent)).thenReturn(new PlayerState(PlayerState.STANDING));
		when(fieldModel.getPlayerCoordinate(opponent)).thenReturn(coordinate);
		occupiedCoordinates.add(coordinate);
	}

	private void addDivingTackler() {
		DivingTackle divingTackle = new DivingTackle();
		divingTackle.postConstruct();
		addOpponent(TACKLER_COORDINATE, divingTackle);
	}

	private int minimumRollPreview() {
		Set<JumpModifier> modifiers = factory.findModifiers(new JumpContext(game, player, FROM, TO));
		return mechanic.minimumRollJump(player, modifiers);
	}

	@Test
	void previewShowsTheBestAchievableRoll() {
		// agility 4 plus one tacklezone is a 5+, Very Long Legs and Consummate Professional bring it down to a 3+
		assertEquals(3, minimumRollPreview());
	}

	@Test
	void previewIgnoresUsedSkills() {
		usedSkills.addAll(skills);
		// the used Consummate Professional is gone, the free Very Long Legs still applies
		assertEquals(4, minimumRollPreview());
	}

	@Test
	void previewIncludesDivingTackle() {
		addDivingTackler();
		// agility 4 plus two tacklezones and diving tackle is an 8+, the optional modifiers bring it down to a 6+
		assertEquals(6, minimumRollPreview());
	}

	@Test
	void declaredContextIgnoresUnselectedOptionalModifiersAndDivingTackle() {
		addDivingTackler();
		Set<JumpModifier> modifiers =
			factory.findModifiers(new JumpContext(game, player, FROM, TO, Collections.emptySet()));
		// agility 4 plus two tacklezones is a 6+, only the free Very Long Legs applies without being declared
		assertEquals(5, mechanic.minimumRollJump(player, modifiers));
	}
}

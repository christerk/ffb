package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.JumpModifierFactory;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
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
 * Verifies that only the BB2025 mechanic takes optional modifiers and Diving Tackle into account when previewing
 * jump squares.
 */
class AgilityMechanicJumpPreviewTest {

	private static final FieldCoordinate FROM = new FieldCoordinate(5, 5);
	private static final FieldCoordinate TO = new FieldCoordinate(7, 5);
	private static final FieldCoordinate TACKLER_COORDINATE = new FieldCoordinate(5, 6);
	private static final JumpModifier TACKLEZONE = new JumpModifier("Tacklezone", 1, ModifierType.TACKLEZONE);

	private Game game;
	private ActingPlayer actingPlayer;
	private Player<?> player;
	private FieldModel fieldModel;
	private Team awayTeam;
	private final Set<Skill> skills = new LinkedHashSet<>();
	private final Set<Skill> usedSkills = new HashSet<>();
	private VeryLongLegs veryLongLegs;

	@BeforeEach
	void setUp() {
		veryLongLegs = new VeryLongLegs();
		veryLongLegs.postConstruct();
		ConsummateProfessional consummateProfessional = new ConsummateProfessional();
		consummateProfessional.postConstruct();
		skills.add(veryLongLegs);
		skills.add(consummateProfessional);

		player = mock(Player.class);
		when(player.getSkillsIncludingTemporaryOnes()).thenReturn(skills);
		when(player.getAgilityWithModifiers()).thenReturn(4);

		actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getPlayer()).then(invocation -> player);
		when(actingPlayer.isSkillUsed(any(Skill.class))).then(invocation -> usedSkills.contains(invocation.getArgument(0)));

		JumpModifierFactory modifierFactory = mock(JumpModifierFactory.class);
		when(modifierFactory.findModifiers(any(JumpContext.class))).then(invocation -> {
			JumpContext context = invocation.getArgument(0);
			Set<JumpModifier> modifiers = new HashSet<>();
			modifiers.add(TACKLEZONE);
			for (Skill skill : skills) {
				skill.getJumpModifiers().stream().filter(modifier -> modifier.appliesToContext(skill, context))
					.forEach(modifiers::add);
			}
			return modifiers;
		});

		Team homeTeam = mock(Team.class);
		awayTeam = mock(Team.class);
		when(player.getTeam()).then(invocation -> homeTeam);

		fieldModel = mock(FieldModel.class);
		when(fieldModel.findAdjacentCoordinates(any(FieldCoordinate.class), any(FieldCoordinateBounds.class), anyInt(),
			anyBoolean())).thenReturn(new FieldCoordinate[]{TACKLER_COORDINATE});

		GameOptions gameOptions = mock(GameOptions.class);
		when(gameOptions.getOptionWithDefault(any(GameOptionId.class)))
			.thenReturn(new GameOptionBoolean(GameOptionId.DIVING_TACKLE_LEAVING_TZ_ONLY).setValue(false));

		game = mock(Game.class);
		when(game.<JumpModifierFactory>getFactory(Factory.JUMP_MODIFIER)).thenReturn(modifierFactory);
		when(game.getActingPlayer()).then(invocation -> actingPlayer);
		when(game.getFieldModel()).then(invocation -> fieldModel);
		when(game.getTeamHome()).then(invocation -> homeTeam);
		when(game.getTeamAway()).then(invocation -> awayTeam);
		when(game.getTurnMode()).thenReturn(TurnMode.REGULAR);
		when(game.getOptions()).thenReturn(gameOptions);
	}

	private void addDivingTackler() {
		DivingTackle divingTackle = new DivingTackle();
		divingTackle.postConstruct();
		Player<?> tackler = mock(Player.class);
		when(tackler.getTeam()).then(invocation -> awayTeam);
		when(tackler.hasSkillProperty(NamedProperties.canAttemptToTackleJumpingPlayer)).thenReturn(true);
		when(tackler.getSkillsIncludingTemporaryOnes())
			.then(invocation -> new LinkedHashSet<>(Collections.singletonList(divingTackle)));
		when(fieldModel.getPlayer(TACKLER_COORDINATE)).then(invocation -> tackler);
		when(fieldModel.getPlayerState(tackler)).thenReturn(new PlayerState(PlayerState.STANDING));
		when(fieldModel.getPlayerCoordinate(tackler)).thenReturn(TACKLER_COORDINATE);
	}

	private void assertPreviewOnlyUsesFreeModifiers(AgilityMechanic mechanic) {
		// neither the once per game modifier nor a possible Diving Tackle is taken into account
		Set<JumpModifier> freeModifiers = new HashSet<>(Collections.singletonList(TACKLEZONE));
		freeModifiers.addAll(veryLongLegs.getJumpModifiers());
		assertEquals(mechanic.minimumRollJump(player, freeModifiers),
			mechanic.minimumRollJumpPreview(game, actingPlayer, FROM, TO));
	}

	@Test
	void bb2025PreviewShowsTheBestAchievableRoll() {
		// agility 4 plus one tacklezone is a 5+, Very Long Legs and Consummate Professional bring it down to a 3+
		assertEquals(3, new com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic()
			.minimumRollJumpPreview(game, actingPlayer, FROM, TO));
	}

	@Test
	void bb2025PreviewIgnoresUsedSkills() {
		usedSkills.addAll(skills);
		assertPreviewOnlyUsesFreeModifiers(new com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic());
	}

	@Test
	void bb2025PreviewIncludesDivingTackle() {
		addDivingTackler();
		// agility 4 plus one tacklezone and diving tackle is a 7+, the two optional modifiers bring it down to a 5+
		assertEquals(5, new com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic()
			.minimumRollJumpPreview(game, actingPlayer, FROM, TO));
	}

	@Test
	void bb2020PreviewIgnoresOptionalModifiersAndDivingTackle() {
		addDivingTackler();
		assertPreviewOnlyUsesFreeModifiers(new com.fumbbl.ffb.mechanics.bb2020.AgilityMechanic());
	}

	@Test
	void bb2016PreviewIgnoresOptionalModifiersAndDivingTackle() {
		addDivingTackler();
		assertPreviewOnlyUsesFreeModifiers(new com.fumbbl.ffb.mechanics.bb2016.AgilityMechanic());
	}
}

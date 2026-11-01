package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Position;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Mocked game for chain modification tests. Armour breaks if dice + number of armour modifiers exceed
 * {@link #armour}. Injury: dice + number of injury modifiers, up to 7 stunned, 8 - 9 KO, above casualty
 * (<code>null</code>).
 */
class ChainModificationFixture {

	static final String ATTACKER_ID = "attacker";
	static final String DEFENDER_ID = "defender";

	final GameState gameState = mock(GameState.class);
	final Game game = mock(Game.class);
	final FieldModel fieldModel = mock(FieldModel.class);
	final DiceRoller diceRoller = mock(DiceRoller.class);
	final Player<?> attacker = mock(Player.class);
	final Player<?> defender = mock(Player.class);
	final Team attackerTeam = mock(Team.class);
	final Team defenderTeam = mock(Team.class);
	final Position defenderPosition = mock(Position.class);
	final InjuryEvaluationService evaluationService = mock(InjuryEvaluationService.class);
	final InjuryContextCopyService copyService = new InjuryContextCopyService();
	final List<Keyword> defenderKeywords = new ArrayList<>();
	int armour = 8;

	ChainModificationFixture() {
		ActingPlayer actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getPlayer()).thenAnswer(invocation -> attacker);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		when(game.getFieldModel()).thenReturn(fieldModel);
		when(game.getPlayerById(ATTACKER_ID)).thenAnswer(invocation -> attacker);
		when(game.getPlayerById(DEFENDER_ID)).thenAnswer(invocation -> defender);
		when(gameState.getGame()).thenReturn(game);
		when(gameState.getDiceRoller()).thenReturn(diceRoller);
		when(attacker.getId()).thenReturn(ATTACKER_ID);
		when(defender.getId()).thenReturn(DEFENDER_ID);
		when(attacker.getTeam()).thenReturn(attackerTeam);
		when(defender.getTeam()).thenReturn(defenderTeam);
		when(defender.getPosition()).thenAnswer(invocation -> defenderPosition);
		when(defenderPosition.getKeywords()).thenReturn(defenderKeywords);
		attackerHasTacklezones(true);

		when(evaluationService.isArmourBroken(any(), any())).thenAnswer(invocation -> {
			InjuryContext context = invocation.getArgument(1);
			int[] roll = context.getArmorRoll();
			return roll[0] + roll[1] + context.getArmorModifiers().length > armour;
		});
		when(evaluationService.interpretInjury(any(), any())).thenAnswer(invocation -> {
			InjuryContext context = invocation.getArgument(1);
			return injury(context.getInjuryRoll(), context.getInjuryModifiers().length);
		});
	}

	static PlayerState injury(int[] roll, int modifiers) {
		int total = roll[0] + roll[1] + modifiers;
		if (total > 9) {
			return null;
		}
		return new PlayerState(total > 7 ? PlayerState.KNOCKED_OUT : PlayerState.STUNNED);
	}

	void attackerHasTacklezones(boolean tacklezones) {
		PlayerState state = mock(PlayerState.class);
		when(state.hasTacklezones()).thenReturn(tacklezones);
		when(fieldModel.getPlayerState(attacker)).thenReturn(state);
	}

	void sameTeam() {
		when(attacker.getTeam()).thenReturn(defenderTeam);
	}

	Skill skill(String name, int armourModifiers, int injuryModifiers) {
		Skill skill = mock(Skill.class);
		when(skill.getName()).thenReturn(name);
		List<ArmorModifier> armorModifierList = new ArrayList<>();
		for (int i = 0; i < armourModifiers; i++) {
			armorModifierList.add(mock(ArmorModifier.class));
		}
		List<InjuryModifier> injuryModifierList = new ArrayList<>();
		for (int i = 0; i < injuryModifiers; i++) {
			injuryModifierList.add(mock(InjuryModifier.class));
		}
		when(skill.getArmorModifiers()).thenReturn(armorModifierList);
		when(skill.getInjuryModifiers()).thenReturn(injuryModifierList);
		return skill;
	}

	InjuryContext context(InjuryType injuryType, boolean withAttacker) {
		InjuryContext context = new InjuryContext();
		context.setInjuryType(injuryType);
		context.setDefenderId(DEFENDER_ID);
		if (withAttacker) {
			context.setAttackerId(ATTACKER_ID);
		}
		return context;
	}

	InjuryResolutionState armourState(InjuryType injuryType, int... armourRoll) {
		InjuryContext context = context(injuryType, true);
		context.setArmorRoll(armourRoll);
		context.setArmorBroken(evaluationService.isArmourBroken(gameState, context));
		return new InjuryResolutionState(context, InjuryPhase.ARMOUR);
	}

	InjuryResolutionState injuryState(InjuryType injuryType, int... injuryRoll) {
		InjuryContext context = context(injuryType, true);
		context.setArmorRoll(new int[]{6, 6});
		context.setArmorBroken(true);
		context.setInjuryRoll(injuryRoll);
		context.setInjury(evaluationService.interpretInjury(gameState, context));
		return new InjuryResolutionState(context, InjuryPhase.INJURY);
	}
}

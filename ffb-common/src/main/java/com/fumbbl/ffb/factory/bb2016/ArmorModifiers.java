package com.fumbbl.ffb.factory.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.factory.FoulAssistArmorModifier;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.SpecialEffectArmourModifier;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@RulesCollection(RulesCollection.Rules.BB2016)
public class ArmorModifiers implements com.fumbbl.ffb.factory.ArmorModifiers {

	private final Set<? extends ArmorModifier> armorModifiers = new HashSet<ArmorModifier>() {{
		add(new FoulAssistArmorModifier("1 Offensive Assist", 1, true));
		add(new FoulAssistArmorModifier("2 Offensive Assists", 2, true));
		add(new FoulAssistArmorModifier("3 Offensive Assists", 3, true));
		add(new FoulAssistArmorModifier("4 Offensive Assists", 4, true));
		add(new FoulAssistArmorModifier("5 Offensive Assists", 5, true));
		add(new FoulAssistArmorModifier("6 Offensive Assists", 6, true));
		add(new FoulAssistArmorModifier("7 Offensive Assists", 7, true));
		add(new FoulAssistArmorModifier("1 Defensive Assist", -1, true));
		add(new FoulAssistArmorModifier("2 Defensive Assists", -2, true));
		add(new FoulAssistArmorModifier("3 Defensive Assists", -3, true));
		add(new FoulAssistArmorModifier("4 Defensive Assists", -4, true));
		add(new FoulAssistArmorModifier("5 Defensive Assists", -5, true));
		add(new StaticArmourModifier("Foul", 1, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				Game game = context.getGame();
				return
					context.isFoul()
						&& (UtilGameOption.isOptionEnabled(game, GameOptionId.FOUL_BONUS)
						|| (UtilGameOption.isOptionEnabled(game, GameOptionId.FOUL_BONUS_OUTSIDE_TACKLEZONE)
						&& (UtilPlayer.findTacklezones(game, context.getAttacker()) < 1)));
			}
		});
		add(new SpecialEffectArmourModifier("Bomb", 1, false, SpecialEffect.BOMB));
		add(new SpecialEffectArmourModifier("Fireball", 1, false, SpecialEffect.FIREBALL));
		add(new SpecialEffectArmourModifier("Lightning", 1, false, SpecialEffect.LIGHTNING));
	}};

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public Stream<? extends ArmorModifier> values() {
		return armorModifiers.stream();
	}

	@Override
	public Stream<? extends ArmorModifier> allValues() {
		return values();
	}

	@Override
	public void setUseAll(boolean useAll) {

	}
}

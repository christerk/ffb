package com.fumbbl.ffb.skill.mixed.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.modifiers.StaticInjuryModifierAttacker;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPlayer;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ASneakyPair extends Skill {
	public ASneakyPair() {
		super("A Sneaky Pair", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerModifier(new StaticInjuryModifierAttacker(ASneakyPair.this.getName(), 1, false) {
			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				return super.appliesToContext(context) && (context.isFoul() || context.isStab()) && partnerMarksDefender(context.getDefender(), context.getGame());
			}

			private boolean partnerMarksDefender(Player<?> defender, Game game) {
				Player<?>[] players = UtilPlayer.findAdjacentOpposingPlayersWithSkill(game, defender,
					game.getFieldModel().getPlayerCoordinate(defender), ASneakyPair.this, false);
				return ArrayTool.isProvided(players) && players.length > 1;
			}
		});
	}
}

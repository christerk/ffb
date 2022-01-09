package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.Chainsaw;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ReRollSingleArmourDie;
import com.fumbbl.ffb.model.skill.InjuryContextModificationSkill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

import java.util.HashSet;
import java.util.Set;

/**
 * Once per game, Helmut may use his Pro skill to re-roll a single dice rolled as part of an Armour roll
 */

@RulesCollection(Rules.BB2020)
public class OldPro extends InjuryContextModificationSkill {
	private static final Set<Class<? extends InjuryType>> validInjuryTypes = new HashSet<Class<? extends InjuryType>>() {{
		add(Block.class);
		add(Chainsaw.class);
		add(Foul.class);
		add(FoulForSpp.class);
	}};

	public OldPro() {
		super("Old Pro", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME,
			new ReRollSingleArmourDie(validInjuryTypes), SkillUse.RE_ROLL_LOWER_ARMOUR_DIE);
	}
}

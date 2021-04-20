package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * Vampires must occasionally feed on the blood of the living. Immediately after
 * declaring an Action with a Vampire, roll a d6: On a 2+ the Vampire can carry
 * out the Action as normal. On a 1, however, the Vampire must feed on a Thrall
 * team-mate or a spectator. The Vampire may continue with his declared Action
 * or if he had declared a Block Action, he may take a Move Action instead.
 * Either way, at the end of the declared Action, but before actually passing,
 * handing off, or scoring, the vampire must feed. If he is standing adjacent to
 * one or more Thrall team-mates (standing, prone or stunned), then choose one
 * to bite and make an Injury roll on the Thrall treating any casualty roll as
 * Badly Hurt. The injury will not cause a turnover unless the Thrall was
 * holding the ball. Once the Vampire has bitten a Thrall he may complete his
 * Action. Failure to bite a Thrall is a turnover and requires you to feed on a
 * spectator – move the Vampire to the reserves box if he was still on the
 * pitch. If he was holding the ball, it bounces from the square he occupied
 * when he was removed and he will not score a touchdown if he was in the
 * opposing end zone.
 */
@RulesCollection(Rules.COMMON)
public class BloodLust extends Skill {

	public BloodLust() {
		super("Blood Lust", SkillCategory.EXTRAORDINARY);
	}

}

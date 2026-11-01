package com.fumbbl.ffb.test;

import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.skill.mixed.OnTheBall;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OnTheBallSkillTest {

	@Test
	public void skillHasCorrectName() {
		OnTheBall skill = new OnTheBall();
		assertEquals("On The Ball", skill.getName());
	}

	@Test
	public void skillHasPassingCategory() {
		OnTheBall skill = new OnTheBall();
		assertEquals(SkillCategory.PASSING, skill.getCategory());
	}

	@Test
	public void skillRegistersKickOffReturnProperty() {
		OnTheBall skill = new OnTheBall();
		skill.postConstruct();
		assertTrue(skill.hasSkillProperty(NamedProperties.canMoveDuringKickOffScatter));
	}

	@Test
	public void skillRegistersPassBlockProperty() {
		OnTheBall skill = new OnTheBall();
		skill.postConstruct();
		assertTrue(skill.hasSkillProperty(NamedProperties.canMoveWhenOpponentPasses));
	}

	@Test
	public void skillRegistersConflictingProperty() {
		OnTheBall skill = new OnTheBall();
		skill.postConstruct();
		assertTrue(skill.hasSkillProperty(NamedProperties.canMoveDuringKickOffScatter));
		assertTrue(skill.hasSkillProperty(NamedProperties.canMoveWhenOpponentPasses));
	}
}

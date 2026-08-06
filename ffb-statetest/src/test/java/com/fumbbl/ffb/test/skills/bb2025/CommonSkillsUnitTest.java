package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.skill.common.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonSkillsUnitTest {

    private void assertSkill(Skill skill, String name, SkillCategory category) {
        assertEquals(name, skill.getName());
        assertEquals(category, skill.getCategory());
    }

    @Test
    public void blockProperties() {
        Block s = new Block();
        s.postConstruct();
        assertSkill(s, "Block", SkillCategory.GENERAL);
        assertTrue(s.hasSkillProperty(NamedProperties.preventFallOnBothDown));
    }

    @Test
    public void catchProperties() {
        assertSkill(new Catch(), "Catch", SkillCategory.AGILITY);
    }

    @Test
    public void dauntlessProperties() {
        Dauntless s = new Dauntless();
        s.postConstruct();
        assertSkill(s, "Dauntless", SkillCategory.GENERAL);
        assertTrue(s.hasSkillProperty(NamedProperties.canRollToMatchOpponentsStrength));
    }

    @Test
    public void disturbingPresenceProperties() {
        DisturbingPresence s = new DisturbingPresence();
        s.postConstruct();
        assertSkill(s, "Disturbing Presence", SkillCategory.MUTATION);
        assertTrue(s.hasSkillProperty(NamedProperties.inflictsDisturbingPresence));
    }

    @Test
    public void divingCatchProperties() {
        DivingCatch s = new DivingCatch();
        s.postConstruct();
        assertSkill(s, "Diving Catch", SkillCategory.AGILITY);
        assertTrue(s.hasSkillProperty(NamedProperties.canAttemptCatchInAdjacentSquares));
        assertTrue(s.hasSkillProperty(NamedProperties.addBonusForAccuratePass));
    }

    @Test
    public void dumpOffProperties() {
        assertSkill(new DumpOff(), "Dump-Off", SkillCategory.PASSING);
    }

    @Test
    public void extraArmsProperties() {
        assertSkill(new ExtraArms(), "Extra Arms", SkillCategory.MUTATION);
    }

    @Test
    public void fendProperties() {
        Fend s = new Fend();
        s.postConstruct();
        assertSkill(s, "Fend", SkillCategory.GENERAL);
        assertTrue(s.hasSkillProperty(NamedProperties.preventOpponentFollowingUp));
    }

    @Test
    public void foulAppearanceProperties() {
        FoulAppearance s = new FoulAppearance();
        s.postConstruct();
        assertSkill(s, "Foul Appearance", SkillCategory.MUTATION);
        assertTrue(s.hasSkillProperty(NamedProperties.forceRollBeforeBeingBlocked));
    }

    @Test
    public void hailMaryPassProperties() {
        HailMaryPass s = new HailMaryPass();
        s.postConstruct();
        assertSkill(s, "Hail Mary Pass", SkillCategory.PASSING);
        assertTrue(s.hasSkillProperty(NamedProperties.canPassToAnySquare));
    }

    @Test
    public void hornsProperties() {
        Horns s = new Horns();
        s.postConstruct();
        assertSkill(s, "Horns", SkillCategory.MUTATION);
        assertTrue(s.hasSkillProperty(NamedProperties.addStrengthOnBlitz));
    }

    @Test
    public void jumpUpProperties() {
        JumpUp s = new JumpUp();
        s.postConstruct();
        assertSkill(s, "Jump Up", SkillCategory.AGILITY);
        assertTrue(s.hasSkillProperty(NamedProperties.canStandUpForFree));
    }

    @Test
    public void movementIncreaseProperties() {
        assertSkill(new MovementIncrease(), "+MA", SkillCategory.STAT_INCREASE);
    }

    @Test
    public void passProperties() {
        assertSkill(new Pass(), "Pass", SkillCategory.PASSING);
    }

    @Test
    public void sprintProperties() {
        Sprint s = new Sprint();
        s.postConstruct();
        assertSkill(s, "Sprint", SkillCategory.AGILITY);
        assertTrue(s.hasSkillProperty(NamedProperties.canMakeAnExtraGfi));
    }

    @Test
    public void standFirmProperties() {
        StandFirm s = new StandFirm();
        s.postConstruct();
        assertSkill(s, "Stand Firm", SkillCategory.STRENGTH);
        assertTrue(s.hasSkillProperty(NamedProperties.canRefuseToBePushed));
    }

    @Test
    public void stripBallProperties() {
        StripBall s = new StripBall();
        s.postConstruct();
        assertSkill(s, "Strip Ball", SkillCategory.GENERAL);
        assertTrue(s.hasSkillProperty(NamedProperties.forceOpponentToDropBallOnPushback));
    }

    @Test
    public void sureHandsProperties() {
        assertSkill(new SureHands(), "Sure Hands", SkillCategory.GENERAL);
    }

    @Test
    public void tackleProperties() {
        assertSkill(new Tackle(), "Tackle", SkillCategory.GENERAL);
    }

    @Test
    public void tentaclesProperties() {
        Tentacles s = new Tentacles();
        s.postConstruct();
        assertSkill(s, "Tentacles", SkillCategory.MUTATION);
        assertTrue(s.hasSkillProperty(NamedProperties.canHoldPlayersLeavingTacklezones));
    }

    @Test
    public void thickSkullProperties() {
        ThickSkull s = new ThickSkull();
        s.postConstruct();
        assertSkill(s, "Thick Skull", SkillCategory.STRENGTH);
        assertTrue(s.hasSkillProperty(NamedProperties.convertKOToStunOn8));
    }

    @Test
    public void twoHeadsProperties() {
        assertSkill(new TwoHeads(), "Two Heads", SkillCategory.MUTATION);
    }

    @Test
    public void wrestleProperties() {
        Wrestle s = new Wrestle();
        s.postConstruct();
        assertSkill(s, "Wrestle", SkillCategory.GENERAL);
        assertTrue(s.hasSkillProperty(NamedProperties.canTakeDownPlayersWithHimOnBothDown));
    }
}

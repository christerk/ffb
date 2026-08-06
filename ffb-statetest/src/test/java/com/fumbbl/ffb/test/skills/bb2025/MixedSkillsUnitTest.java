package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.skill.mixed.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MixedSkillsUnitTest {

    private void assertSkill(Skill skill, String name, SkillCategory category) {
        assertEquals(name, skill.getName());
        assertEquals(category, skill.getCategory());
    }

    @Test
    public void accurate() {
        assertSkill(new Accurate(), "Accurate", SkillCategory.PASSING);
    }

    @Test
    public void agilityIncrease() {
        assertSkill(new AgilityIncrease(), "+AG", SkillCategory.STAT_INCREASE);
    }

    @Test
    public void alwaysHungry() {
        AlwaysHungry s = new AlwaysHungry();
        s.postConstruct();
        assertSkill(s, "Always Hungry", SkillCategory.TRAIT);
        assertTrue(s.hasSkillProperty(NamedProperties.mightEatPlayerToThrow));
    }

    @Test
    public void animalSavagery() {
        assertSkill(new AnimalSavagery(), "Animal Savagery", SkillCategory.TRAIT);
    }

    @Test
    public void armBar() {
        ArmBar s = new ArmBar();
        s.postConstruct();
        assertSkill(s, "Arm Bar", SkillCategory.STRENGTH);
        assertTrue(s.hasSkillProperty(NamedProperties.affectsEitherArmourOrInjuryOnDodge));
    }

    @Test
    public void armourIncrease() {
        assertSkill(new ArmourIncrease(), "+AV", SkillCategory.STAT_INCREASE);
    }

    @Test
    public void bigHand() {
        BigHand s = new BigHand();
        s.postConstruct();
        assertSkill(s, "Big Hand", SkillCategory.MUTATION);
        assertTrue(s.hasSkillProperty(NamedProperties.ignoreTacklezonesWhenPickingUp));
    }

    @Test
    public void bloodlust() {
        assertSkill(new Bloodlust(), "Bloodlust", SkillCategory.EXTRAORDINARY);
    }

    @Test
    public void cannoneer() {
        assertSkill(new Cannoneer(), "Cannoneer", SkillCategory.PASSING);
    }

    @Test
    public void claws() {
        Claws s = new Claws();
        s.postConstruct();
        assertSkill(s, "Claws", SkillCategory.MUTATION);
        assertTrue(s.hasSkillProperty(NamedProperties.reducesArmourToFixedValue));
    }

    @Test
    public void decay() {
        assertSkill(new Decay(), "Decay", SkillCategory.TRAIT);
    }

    @Test
    public void divingTackle() {
        DivingTackle s = new DivingTackle();
        s.postConstruct();
        assertSkill(s, "Diving Tackle", SkillCategory.AGILITY);
        assertTrue(s.hasSkillProperty(NamedProperties.canAttemptToTackleDodgingPlayer));
    }

    @Test
    public void dodge() {
        Dodge s = new Dodge();
        s.postConstruct();
        assertSkill(s, "Dodge", SkillCategory.AGILITY);
        assertTrue(s.hasSkillProperty(NamedProperties.ignoreDefenderStumblesResult));
        assertTrue(s.hasSkillProperty(NamedProperties.canRerollDodge));
    }

    @Test
    public void drunkard() {
        assertSkill(new Drunkard(), "Drunkard", SkillCategory.TRAIT);
    }

    @Test
    public void frenzy() {
        Frenzy s = new Frenzy();
        s.postConstruct();
        assertSkill(s, "Frenzy", SkillCategory.GENERAL);
        assertTrue(s.hasSkillProperty(NamedProperties.forceFollowup));
        assertTrue(s.hasSkillProperty(NamedProperties.forceSecondBlock));
    }

    @Test
    public void grab() {
        Grab s = new Grab();
        s.postConstruct();
        assertSkill(s, "Grab", SkillCategory.STRENGTH);
        assertTrue(s.hasSkillProperty(NamedProperties.canPushBackToAnySquare));
    }

    @Test
    public void guard() {
        Guard s = new Guard();
        s.postConstruct();
        assertSkill(s, "Guard", SkillCategory.STRENGTH);
        assertTrue(s.hasSkillProperty(NamedProperties.assistsBlocksInTacklezones));
    }

    @Test
    public void ironHardSkin() {
        assertSkill(new IronHardSkin(), "Iron Hard Skin", SkillCategory.MUTATION);
    }

    @Test
    public void juggernaut() {
        Juggernaut s = new Juggernaut();
        s.postConstruct();
        assertSkill(s, "Juggernaut", SkillCategory.STRENGTH);
    }

    @Test
    public void kick() {
        Kick s = new Kick();
        s.postConstruct();
        assertSkill(s, "Kick", SkillCategory.GENERAL);
        assertTrue(s.hasSkillProperty(NamedProperties.canReduceKickDistance));
    }

    @Test
    public void kickTeamMate() {
        assertSkill(new KickTeamMate(), "Kick Team-Mate", SkillCategory.TRAIT);
    }

    @Test
    public void leader() {
        assertSkill(new Leader(), "Leader", SkillCategory.PASSING);
    }

    @Test
    public void loner() {
        assertSkill(new Loner(), "Loner", SkillCategory.TRAIT);
    }

    @Test
    public void multipleBlock() {
        assertSkill(new MultipleBlock(), "Multiple Block", SkillCategory.STRENGTH);
    }

    @Test
    public void myBall() {
        assertSkill(new MyBall(), "My Ball", SkillCategory.TRAIT);
    }

    @Test
    public void nervesOfSteel() {
        assertSkill(new NervesOfSteel(), "Nerves of Steel", SkillCategory.PASSING);
    }

    @Test
    public void onTheBall() {
        OnTheBall s = new OnTheBall();
        s.postConstruct();
        assertSkill(s, "On The Ball", SkillCategory.PASSING);
        assertTrue(s.hasSkillProperty(NamedProperties.canMoveDuringKickOffScatter));
        assertTrue(s.hasSkillProperty(NamedProperties.canMoveWhenOpponentPasses));
    }

    @Test
    public void pickMeUp() {
        assertSkill(new PickMeUp(), "Pick-me-up", SkillCategory.TRAIT);
    }

    @Test
    public void plagueRidden() {
        assertSkill(new PlagueRidden(), "Plague Ridden", SkillCategory.TRAIT);
    }

    @Test
    public void prehensileTail() {
        PrehensileTail s = new PrehensileTail();
        s.postConstruct();
        assertSkill(s, "Prehensile Tail", SkillCategory.MUTATION);
        assertTrue(s.hasSkillProperty(NamedProperties.makesDodgingHarder));
    }

    @Test
    public void pro() {
        Pro s = new Pro();
        s.postConstruct();
        assertSkill(s, "Pro", SkillCategory.GENERAL);
        assertTrue(s.hasSkillProperty(NamedProperties.canRerollOncePerTurn));
    }

    @Test
    public void safePairOfHands() {
        assertSkill(new SafePairOfHands(), "Safe Pair Of Hands", SkillCategory.AGILITY);
    }

    @Test
    public void safePass() {
        assertSkill(new SafePass(), "Safe Pass", SkillCategory.PASSING);
    }

    @Test
    public void secretWeapon() {
        assertSkill(new SecretWeapon(), "Secret Weapon", SkillCategory.TRAIT);
    }

    @Test
    public void strongArm() {
        assertSkill(new StrongArm(), "Strong Arm", SkillCategory.STRENGTH);
    }

    @Test
    public void stunty() {
        assertSkill(new Stunty(), "Stunty", SkillCategory.TRAIT);
    }

    @Test
    public void takeRoot() {
        assertSkill(new TakeRoot(), "Take Root", SkillCategory.TRAIT);
    }

    @Test
    public void throwTeamMate() {
        assertSkill(new ThrowTeamMate(), "Throw Team-Mate", SkillCategory.TRAIT);
    }

    @Test
    public void timmmber() {
        assertSkill(new Timmmber(), "Timmm-ber!", SkillCategory.TRAIT);
    }

    @Test
    public void titchy() {
        assertSkill(new Titchy(), "Titchy", SkillCategory.TRAIT);
    }

    @Test
    public void trickster() {
        assertSkill(new Trickster(), "Trickster", SkillCategory.TRAIT);
    }

    @Test
    public void unchannelledFury() {
        assertSkill(new UnchannelledFury(), "Unchannelled Fury", SkillCategory.TRAIT);
    }
}

package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.skill.bb2025.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Bb2025SkillsUnitTest {

    private void assertSkill(Skill skill, String name, SkillCategory category) {
        assertEquals(name, skill.getName());
        assertEquals(category, skill.getCategory());
    }

    // --- STRENGTH skills ---

    @Test
    public void brawlerProperties() {
        Brawler s = new Brawler();
        s.postConstruct();
        assertSkill(s, "Brawler", SkillCategory.STRENGTH);
        assertTrue(s.hasSkillProperty(NamedProperties.canRerollSingleBothDown));
    }

    @Test
    public void breakTackleProperties() {
        assertSkill(new BreakTackle(), "Break Tackle", SkillCategory.STRENGTH);
    }

    @Test
    public void bullseyeProperties() {
        assertSkill(new Bullseye(), "Bullseye", SkillCategory.STRENGTH);
    }

    @Test
    public void juggernautProperties() {
        Juggernaut s = new Juggernaut();
        s.postConstruct();
        assertSkill(s, "Juggernaut", SkillCategory.STRENGTH);
        assertTrue(s.hasSkillProperty(NamedProperties.canConvertBothDownToPush));
    }

    @Test
    public void mightyBlowProperties() {
        MightyBlow s = new MightyBlow();
        s.postConstruct();
        assertSkill(s, "Mighty Blow", SkillCategory.STRENGTH);
        assertTrue(s.hasSkillProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock));
    }

    // --- AGILITY skills ---

    @Test
    public void defensiveProperties() {
        assertSkill(new Defensive(), "Defensive", SkillCategory.AGILITY);
    }

    @Test
    public void dodgeProperties() {
        Dodge s = new Dodge();
        s.postConstruct();
        assertSkill(s, "Dodge", SkillCategory.AGILITY);
        assertTrue(s.hasSkillProperty(NamedProperties.ignoreDefenderStumblesResult));
        assertTrue(s.hasSkillProperty(NamedProperties.canRerollDodge));
    }

    @Test
    public void leapProperties() {
        assertSkill(new Leap(), "Leap", SkillCategory.AGILITY);
    }

    @Test
    public void sidestepProperties() {
        Sidestep s = new Sidestep();
        s.postConstruct();
        assertSkill(s, "Sidestep", SkillCategory.AGILITY);
        assertTrue(s.hasSkillProperty(NamedProperties.canChooseOwnPushedBackSquare));
    }

    // --- GENERAL skills ---

    @Test
    public void kickProperties() {
        Kick s = new Kick();
        s.postConstruct();
        assertSkill(s, "Kick", SkillCategory.GENERAL);
        assertTrue(s.hasSkillProperty(NamedProperties.canReduceKickDistance));
    }

    @Test
    public void proProperties() {
        Pro s = new Pro();
        s.postConstruct();
        assertSkill(s, "Pro", SkillCategory.GENERAL);
        assertTrue(s.hasSkillProperty(NamedProperties.canRerollOncePerTurn));
    }

    @Test
    public void tauntProperties() {
        Taunt s = new Taunt();
        s.postConstruct();
        assertSkill(s, "Taunt", SkillCategory.GENERAL);
        assertTrue(s.hasSkillProperty(NamedProperties.forceOpponentToFollowUp));
    }

    // --- PASSING skills ---

    @Test
    public void cloudBursterProperties() {
        assertSkill(new CloudBurster(), "Cloud Burster", SkillCategory.PASSING);
    }

    @Test
    public void giveAndGoProperties() {
        assertSkill(new GiveAndGo(), "Give and Go", SkillCategory.PASSING);
    }

    @Test
    public void leaderProperties() {
        assertSkill(new Leader(), "Leader", SkillCategory.PASSING);
    }

    @Test
    public void puntProperties() {
        assertSkill(new Punt(), "Punt", SkillCategory.PASSING);
    }

    // --- TRAIT skills ---

    @Test
    public void animosityProperties() {
        assertSkill(new Animosity(), "Animosity", SkillCategory.TRAIT);
    }

    @Test
    public void ballAndChainProperties() {
        assertSkill(new BallAndChain(), "Ball and Chain", SkillCategory.TRAIT);
    }

    @Test
    public void bombardierProperties() {
        assertSkill(new Bombardier(), "Bombardier", SkillCategory.TRAIT);
    }

    @Test
    public void boneHeadProperties() {
        assertSkill(new BoneHead(), "Bone Head", SkillCategory.TRAIT);
    }

    @Test
    public void breatheFireProperties() {
        assertSkill(new BreatheFire(), "Breathe Fire", SkillCategory.TRAIT);
    }

    @Test
    public void chainsawProperties() {
        assertSkill(new Chainsaw(), "Chainsaw", SkillCategory.TRAIT);
    }

    @Test
    public void hatredProperties() {
        assertSkill(new Hatred(), "Hatred", SkillCategory.TRAIT);
    }

    @Test
    public void hitAndRunProperties() {
        assertSkill(new HitAndRun(), "Hit And Run", SkillCategory.TRAIT);
    }

    @Test
    public void hypnoticGazeProperties() {
        assertSkill(new HypnoticGaze(), "Hypnotic Gaze", SkillCategory.TRAIT);
    }

    @Test
    public void insignificantProperties() {
        assertSkill(new Insignificant(), "Insignificant", SkillCategory.TRAIT);
    }

    @Test
    public void noBallProperties() {
        assertSkill(new NoBall(), "No Ball", SkillCategory.TRAIT);
    }

    @Test
    public void pogoProperties() {
        assertSkill(new Pogo(), "Pogo", SkillCategory.TRAIT);
    }

    @Test
    public void projectileVomitProperties() {
        assertSkill(new ProjectileVomit(), "Projectile Vomit", SkillCategory.TRAIT);
    }

    @Test
    public void reallyStupidProperties() {
        assertSkill(new ReallyStupid(), "Really Stupid", SkillCategory.TRAIT);
    }

    @Test
    public void regenerationProperties() {
        assertSkill(new Regeneration(), "Regeneration", SkillCategory.TRAIT);
    }

    @Test
    public void rightStuffProperties() {
        assertSkill(new RightStuff(), "Right Stuff", SkillCategory.TRAIT);
    }

    @Test
    public void stabProperties() {
        assertSkill(new Stab(), "Stab", SkillCategory.TRAIT);
    }

    @Test
    public void steadyFootingProperties() {
        assertSkill(new SteadyFooting(), "Steady Footing", SkillCategory.TRAIT);
    }

    @Test
    public void swoopProperties() {
        assertSkill(new Swoop(), "Swoop", SkillCategory.TRAIT);
    }

    @Test
    public void unsteadyProperties() {
        assertSkill(new Unsteady(), "Unsteady", SkillCategory.TRAIT);
    }

    // --- MUTATION skills ---

    @Test
    public void bigHandProperties() {
        assertSkill(new BigHand(), "Big Hand", SkillCategory.MUTATION);
    }

    @Test
    public void monstrousMouthProperties() {
        assertSkill(new MonstrousMouth(), "Monstrous Mouth", SkillCategory.MUTATION);
    }

    @Test
    public void veryLongLegsProperties() {
        assertSkill(new VeryLongLegs(), "Very Long Legs", SkillCategory.MUTATION);
    }

    // --- DEVIOUS skills ---

    @Test
    public void dirtyPlayerProperties() {
        assertSkill(new DirtyPlayer(), "Dirty Player", SkillCategory.DEVIOUS);
    }

    @Test
    public void eyeGougeProperties() {
        assertSkill(new EyeGouge(), "Eye Gouge", SkillCategory.DEVIOUS);
    }

    @Test
    public void fumblerooskiProperties() {
        assertSkill(new Fumblerooski(), "Fumblerooski", SkillCategory.DEVIOUS);
    }

    @Test
    public void lethalFlightProperties() {
        assertSkill(new LethalFlight(), "Lethal Flight", SkillCategory.DEVIOUS);
    }

    @Test
    public void loneFoulerProperties() {
        assertSkill(new LoneFouler(), "Lone Fouler", SkillCategory.DEVIOUS);
    }

    @Test
    public void pileDriverProperties() {
        assertSkill(new PileDriver(), "Pile Driver", SkillCategory.DEVIOUS);
    }

    @Test
    public void putTheBootInProperties() {
        assertSkill(new PutTheBootIn(), "Put the Boot In", SkillCategory.DEVIOUS);
    }

    @Test
    public void quickFoulProperties() {
        assertSkill(new QuickFoul(), "Quick Foul", SkillCategory.DEVIOUS);
    }

    @Test
    public void saboteurProperties() {
        Saboteur s = new Saboteur();
        s.postConstruct();
        assertSkill(s, "Saboteur", SkillCategory.DEVIOUS);
        assertTrue(s.hasSkillProperty(NamedProperties.canSabotageBlockerOnKnockdown));
    }

    @Test
    public void shadowingProperties() {
        assertSkill(new Shadowing(), "Shadowing", SkillCategory.DEVIOUS);
    }

    @Test
    public void sneakyGitProperties() {
        assertSkill(new SneakyGit(), "Sneaky Git", SkillCategory.DEVIOUS);
    }

    @Test
    public void violentInnovatorProperties() {
        assertSkill(new ViolentInnovator(), "Violent Innovator", SkillCategory.DEVIOUS);
    }

    // --- STAT_INCREASE skills ---

    @Test
    public void agilityIncreaseProperties() {
        assertSkill(new AgilityIncrease(), "+AG", SkillCategory.STAT_INCREASE);
    }

    @Test
    public void passingIncreaseProperties() {
        assertSkill(new PassingIncrease(), "+PA", SkillCategory.STAT_INCREASE);
    }

    @Test
    public void strengthIncreaseProperties() {
        assertSkill(new StrengthIncrease(), "+ST", SkillCategory.STAT_INCREASE);
    }

    @Test
    public void sureFeetProperties() {
        assertSkill(new SureFeet(), "Sure Feet", SkillCategory.AGILITY);
    }
}

package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.model.Animation;

import java.awt.*;

/**
 * @author Kalimar
 */
public class AnimationSequenceFactory {

	private static final AnimationSequenceFactory _INSTANCE = new AnimationSequenceFactory();

	public static AnimationSequenceFactory getInstance() {
		return _INSTANCE;
	}

	private AnimationSequenceFactory() {
		super();
	}

	public IAnimationSequence getAnimationSequence(FantasyFootballClient pClient, Animation pAnimation) {
		if ((pAnimation == null) || (pAnimation.getAnimationType() == null)) {
			return null;
		}
		DimensionProvider uiDimensionProvider = pClient.getUserInterface().getUiDimensionProvider();
		Dimension fieldDimension = uiDimensionProvider.dimension(Component.FIELD);
		PitchDimensionProvider dimensionProvider = pClient.getUserInterface().getPitchDimensionProvider();

		switch (pAnimation.getAnimationType()) {
			case KICKOFF_BLITZ:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_BLITZ, dimensionProvider.isPitchPortrait());
			case KICKOFF_BLIZZARD:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_BLIZZARD, dimensionProvider.isPitchPortrait());
			case KICKOFF_BRILLIANT_COACHING:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_BRILLIANT_COACHING, dimensionProvider.isPitchPortrait());
			case KICKOFF_CHEERING_FANS:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_CHEERING_FANS, dimensionProvider.isPitchPortrait());
			case KICKOFF_GET_THE_REF:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_GET_THE_REF, dimensionProvider.isPitchPortrait());
			case KICKOFF_HIGH_KICK:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_HIGH_KICK, dimensionProvider.isPitchPortrait());
			case KICKOFF_NICE:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_NICE, dimensionProvider.isPitchPortrait());
			case KICKOFF_PERFECT_DEFENSE:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_PERFECT_DEFENSE, dimensionProvider.isPitchPortrait());
			case KICKOFF_SOLID_DEFENSE:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_SOLID_DEFENCE, dimensionProvider.isPitchPortrait());
			case KICKOFF_PITCH_INVASION:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_PITCH_INVASION, dimensionProvider.isPitchPortrait());
			case KICKOFF_POURING_RAIN:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_POURING_RAIN, dimensionProvider.isPitchPortrait());
			case KICKOFF_QUICK_SNAP:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_QUICK_SNAP, dimensionProvider.isPitchPortrait());
			case KICKOFF_RIOT:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_RIOT, dimensionProvider.isPitchPortrait());
			case KICKOFF_TIMEOUT:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_TIMEOUT, dimensionProvider.isPitchPortrait());
			case KICKOFF_SWELTERING_HEAT:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_SWELTERING_HEAT, dimensionProvider.isPitchPortrait());
			case KICKOFF_THROW_A_ROCK:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_THROW_A_ROCK, dimensionProvider.isPitchPortrait());
			case KICKOFF_OFFICIOUS_REF:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_OFFICIOUS_REF, dimensionProvider.isPitchPortrait());
			case KICKOFF_VERY_SUNNY:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_VERY_SUNNY, dimensionProvider.isPitchPortrait());
			case KICKOFF_CHARGE:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_CHARGE, dimensionProvider.isPitchPortrait());
			case KICKOFF_DODGY_SNACK:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_DODGY_SNACK, dimensionProvider.isPitchPortrait());
			case SPELL_FIREBALL:
				return AnimationSequenceSpecialEffect.createAnimationSequenceFireball(pAnimation.getStartCoordinate());
			case SPELL_LIGHTNING:
				return AnimationSequenceSpecialEffect.createAnimationSequenceLightning(pAnimation.getStartCoordinate());
			case SPELL_ZAP:
				return AnimationSequenceSpecialEffect.createAnimationSequenceZap(pAnimation.getStartCoordinate());
			case BOMB_EXPLOSION:
				return AnimationSequenceSpecialEffect.createAnimationSequenceBomb(pAnimation.getStartCoordinate());
			case PASS:
				return AnimationSequenceThrowing.createAnimationSequencePass(pClient, pAnimation);
			case KICK:
				return AnimationSequenceThrowing.createAnimationSequenceKick(pClient, pAnimation);
			case HAIL_MARY_PASS:
				return AnimationSequenceThrowing.createAnimationSequenceHailMaryPass(pClient, pAnimation);
			case THROW_TEAM_MATE:
				return AnimationSequenceThrowing.createAnimationSequenceThrowTeamMate(pClient, pAnimation);
			case THROW_A_ROCK:
				return AnimationSequenceThrowing.createAnimationSequenceThrowARock(pClient, pAnimation);
			case THROW_BOMB:
				return AnimationSequenceThrowing.createAnimationSequenceThrowBomb(pClient, pAnimation);
			case HAIL_MARY_BOMB:
				return AnimationSequenceThrowing.createAnimationSequenceHailMaryBomb(pClient, pAnimation);
			case CARD:
				return AnimationSequenceCard.createAnimationSequence(pClient, pAnimation);
			case THROW_KEG:
				return AnimationSequenceThrowing.createAnimationSequenceThrowKeg(pClient, pAnimation);
			case FUMBLED_KEG:
				return AnimationSequenceSpecialEffect.createAnimationFumbledKeg(pAnimation.getStartCoordinate());
			case TRICKSTER:
				return AnimationSequenceChained.createAnimationSequenceTrickster(pClient, pAnimation);
			case THEN_I_STARTED_BLASTIN:
				return AnimationSequenceChained.createAnimationSequenceBlastin(pClient, pAnimation);
			case PRAYER_TREACHEROUS_TRAPDOOR:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_TRAPDOOR, dimensionProvider.isPitchPortrait());
			case PRAYER_BAD_HABITS:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_BAD_HABITS, dimensionProvider.isPitchPortrait());
			case PRAYER_FOULING_FRENZY:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_FOULING_FRENZY, dimensionProvider.isPitchPortrait());
			case PRAYER_FRIENDS_WITH_THE_REF:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_FRIENDS_WITH_THE_REF, dimensionProvider.isPitchPortrait());
			case PRAYER_UNDER_SCRUTINY:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_UNDER_SCRUTINY, dimensionProvider.isPitchPortrait());
			case PRAYER_FAN_INTERACTION:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_FAN_INTERACTION, dimensionProvider.isPitchPortrait());
			case PRAYER_GREASY_CLEATS:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_GREASY_CLEATS, dimensionProvider.isPitchPortrait());
			case PRAYER_IRON_MAN:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_IRON_MAN, dimensionProvider.isPitchPortrait());
			case PRAYER_INTENSIVE_TRAINING:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_INTENSIVE_TRAINING, dimensionProvider.isPitchPortrait());
			case PRAYER_KNUCKLE_DUSTERS:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_KNUCKLE_DUSTERS, dimensionProvider.isPitchPortrait());
			case PRAYER_STILETTO:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_STILETTO, dimensionProvider.isPitchPortrait());
			case PRAYER_THROW_A_ROCK:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_THROW_A_ROCK, dimensionProvider.isPitchPortrait());
			case PRAYER_NECESSARY_VIOLENCE:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_NECESSARY_VIOLENCE, dimensionProvider.isPitchPortrait());
			case PRAYER_PERFECT_PASSING:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_PERFECT_PASSING, dimensionProvider.isPitchPortrait());
			case PRAYER_MOLES_UNDER_THE_PITCH:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_MOLES_UNDER_THE_PITCH, dimensionProvider.isPitchPortrait());
			case PRAYER_BLESSED_STATUE_OF_NUFFLE:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_BLESSED_STATUE_OF_NUFFLE, dimensionProvider.isPitchPortrait());
			case BREATHE_FIRE:
				return AnimationSequenceMovingEffect.createAnimationSequenceBreatheFire(pAnimation.getStartCoordinate(), pAnimation.getEndCoordinate(), dimensionProvider);
			case PRAYER_DAZZLING_CATCHING:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_PRAYER_DAZZLING_CATCHING, dimensionProvider.isPitchPortrait());
			default:
				return null;
		}
	}

}

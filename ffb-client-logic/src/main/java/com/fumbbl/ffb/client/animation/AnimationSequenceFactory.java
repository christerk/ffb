package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Animation;

import java.awt.Dimension;

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

	public IAnimationSequence[] getAnimationSequence(FantasyFootballClient pClient, Animation pAnimation) {
		if ((pAnimation == null) || (pAnimation.getAnimationType() == null)) {
			return null;
		}
		DimensionProvider dimensionProvider = pClient.getUserInterface().getDimensionProvider();
		Dimension fieldDimension = dimensionProvider.dimension(DimensionProvider.Component.FIELD);

		switch (pAnimation.getAnimationType()) {
			case KICKOFF_BLITZ:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_BLITZ, dimensionProvider.isPitchPortrait())};
			case KICKOFF_BLIZZARD:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_BLIZZARD, dimensionProvider.isPitchPortrait())};
			case KICKOFF_BRILLIANT_COACHING:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_BRILLIANT_COACHING, dimensionProvider.isPitchPortrait())};
			case KICKOFF_CHEERING_FANS:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_CHEERING_FANS, dimensionProvider.isPitchPortrait())};
			case KICKOFF_GET_THE_REF:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_GET_THE_REF, dimensionProvider.isPitchPortrait())};
			case KICKOFF_HIGH_KICK:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_HIGH_KICK, dimensionProvider.isPitchPortrait())};
			case KICKOFF_NICE:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_NICE, dimensionProvider.isPitchPortrait())};
			case KICKOFF_PERFECT_DEFENSE:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_PERFECT_DEFENSE, dimensionProvider.isPitchPortrait())};
			case KICKOFF_SOLID_DEFENSE:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_SOLID_DEFENCE, dimensionProvider.isPitchPortrait())};
			case KICKOFF_PITCH_INVASION:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_PITCH_INVASION, dimensionProvider.isPitchPortrait())};
			case KICKOFF_POURING_RAIN:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_POURING_RAIN, dimensionProvider.isPitchPortrait())};
			case KICKOFF_QUICK_SNAP:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_QUICK_SNAP, dimensionProvider.isPitchPortrait())};
			case KICKOFF_RIOT:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_RIOT, dimensionProvider.isPitchPortrait())};
			case KICKOFF_TIMEOUT:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_TIMEOUT, dimensionProvider.isPitchPortrait())};
			case KICKOFF_SWELTERING_HEAT:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_SWELTERING_HEAT, dimensionProvider.isPitchPortrait())};
			case KICKOFF_THROW_A_ROCK:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_THROW_A_ROCK, dimensionProvider.isPitchPortrait())};
			case KICKOFF_OFFICIOUS_REF:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_OFFICIOUS_REF, dimensionProvider.isPitchPortrait())};
			case KICKOFF_VERY_SUNNY:
				return new IAnimationSequence[]{AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_VERY_SUNNY, dimensionProvider.isPitchPortrait())};
			case SPELL_FIREBALL:
				return new IAnimationSequence[]{AnimationSequenceSpecialEffect.createAnimationSequenceFireball(pAnimation.getStartCoordinate())};
			case SPELL_LIGHTNING:
				return new IAnimationSequence[]{AnimationSequenceSpecialEffect.createAnimationSequenceLightning(pAnimation.getStartCoordinate())};
			case SPELL_ZAP:
				return new IAnimationSequence[]{AnimationSequenceSpecialEffect.createAnimationSequenceZap(pAnimation.getStartCoordinate())};
			case BOMB_EXPLOSION:
				return new IAnimationSequence[]{AnimationSequenceSpecialEffect.createAnimationSequenceBomb(pAnimation.getStartCoordinate())};
			case PASS:
				return new IAnimationSequence[]{AnimationSequenceThrowing.createAnimationSequencePass(pClient, pAnimation)};
			case KICK:
				return new IAnimationSequence[]{AnimationSequenceThrowing.createAnimationSequenceKick(pClient, pAnimation)};
			case HAIL_MARY_PASS:
				return new IAnimationSequence[]{AnimationSequenceThrowing.createAnimationSequenceHailMaryPass(pClient, pAnimation)};
			case THROW_TEAM_MATE:
				return new IAnimationSequence[]{AnimationSequenceThrowing.createAnimationSequenceThrowTeamMate(pClient, pAnimation)};
			case THROW_A_ROCK:
				return new IAnimationSequence[]{AnimationSequenceThrowing.createAnimationSequenceThrowARock(pClient, pAnimation)};
			case THROW_BOMB:
				return new IAnimationSequence[]{AnimationSequenceThrowing.createAnimationSequenceThrowBomb(pClient, pAnimation)};
			case HAIL_MARY_BOMB:
				return new IAnimationSequence[]{AnimationSequenceThrowing.createAnimationSequenceHailMaryBomb(pClient, pAnimation)};
			case CARD:
				return new IAnimationSequence[]{AnimationSequenceCard.createAnimationSequence(pClient, pAnimation)};
			case THROW_KEG:
				return new IAnimationSequence[]{AnimationSequenceThrowing.createAnimationSequenceThrowKeg(pClient, pAnimation)};
			case FUMBLED_KEG:
				return new IAnimationSequence[]{AnimationSequenceSpecialEffect.createAnimationFumbledKeg(pAnimation.getStartCoordinate())};
			case TRICKSTER:
				return AnimationSequenceSimultaneous.createAnimationSequenceTrickster(pClient, pAnimation);
			default:
				return null;
		}
	}

}

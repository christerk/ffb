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

	public IAnimationSequence getAnimationSequence(FantasyFootballClient pClient, Animation pAnimation) {
		if ((pAnimation == null) || (pAnimation.getAnimationType() == null)) {
			return null;
		}
		DimensionProvider dimensionProvider = pClient.getUserInterface().getDimensionProvider();
		Dimension fieldDimension = dimensionProvider.dimension(DimensionProvider.Component.FIELD);

		switch (pAnimation.getAnimationType()) {
			case KICKOFF_BLITZ:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_BLITZ, dimensionProvider.isPortrait());
			case KICKOFF_BLIZZARD:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_BLIZZARD, dimensionProvider.isPortrait());
			case KICKOFF_BRILLIANT_COACHING:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_BRILLIANT_COACHING, dimensionProvider.isPortrait());
			case KICKOFF_CHEERING_FANS:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_CHEERING_FANS, dimensionProvider.isPortrait());
			case KICKOFF_GET_THE_REF:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_GET_THE_REF, dimensionProvider.isPortrait());
			case KICKOFF_HIGH_KICK:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_HIGH_KICK, dimensionProvider.isPortrait());
			case KICKOFF_NICE:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_NICE, dimensionProvider.isPortrait());
			case KICKOFF_PERFECT_DEFENSE:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_PERFECT_DEFENSE, dimensionProvider.isPortrait());
			case KICKOFF_SOLID_DEFENSE:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_SOLID_DEFENCE, dimensionProvider.isPortrait());
			case KICKOFF_PITCH_INVASION:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_PITCH_INVASION, dimensionProvider.isPortrait());
			case KICKOFF_POURING_RAIN:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_POURING_RAIN, dimensionProvider.isPortrait());
			case KICKOFF_QUICK_SNAP:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_QUICK_SNAP, dimensionProvider.isPortrait());
			case KICKOFF_RIOT:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_RIOT, dimensionProvider.isPortrait());
			case KICKOFF_TIMEOUT:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_TIMEOUT, dimensionProvider.isPortrait());
			case KICKOFF_SWELTERING_HEAT:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_SWELTERING_HEAT, dimensionProvider.isPortrait());
			case KICKOFF_THROW_A_ROCK:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_THROW_A_ROCK, dimensionProvider.isPortrait());
			case KICKOFF_OFFICIOUS_REF:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_OFFICIOUS_REF, dimensionProvider.isPortrait());
			case KICKOFF_VERY_SUNNY:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_VERY_SUNNY, dimensionProvider.isPortrait());
			case SPELL_FIREBALL:
				return AnimationSequenceSpecialEffect.createAnimationSequenceFireball(pAnimation.getStartCoordinate());
			case SPELL_LIGHTNING:
				return AnimationSequenceSpecialEffect.createAnimationSequenceLightning(pAnimation.getStartCoordinate());
			case SPELL_ZAP:
				return AnimationSequenceSpecialEffect.createAnimationSequenceZap(pAnimation.getStartCoordinate());
			case BOMB_EXLOSION:
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
			default:
				return null;
		}
	}

}

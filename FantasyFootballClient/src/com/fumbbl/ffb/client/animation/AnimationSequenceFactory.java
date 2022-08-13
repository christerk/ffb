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
		Dimension fieldDimension = pClient.getUserInterface().getDimensionProvider().dimension(DimensionProvider.Component.FIELD);

		switch (pAnimation.getAnimationType()) {
			case KICKOFF_BLITZ:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_BLITZ);
			case KICKOFF_BLIZZARD:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_BLIZZARD);
			case KICKOFF_BRILLIANT_COACHING:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_BRILLIANT_COACHING);
			case KICKOFF_CHEERING_FANS:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_CHEERING_FANS);
			case KICKOFF_GET_THE_REF:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_GET_THE_REF);
			case KICKOFF_HIGH_KICK:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_HIGH_KICK);
			case KICKOFF_NICE:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_NICE);
			case KICKOFF_PERFECT_DEFENSE:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_PERFECT_DEFENSE);
			case KICKOFF_SOLID_DEFENSE:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_SOLID_DEFENCE);
			case KICKOFF_PITCH_INVASION:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_PITCH_INVASION);
			case KICKOFF_POURING_RAIN:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_POURING_RAIN);
			case KICKOFF_QUICK_SNAP:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_QUICK_SNAP);
			case KICKOFF_RIOT:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_RIOT);
			case KICKOFF_TIMEOUT:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_TIMEOUT);
			case KICKOFF_SWELTERING_HEAT:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_SWELTERING_HEAT);
			case KICKOFF_THROW_A_ROCK:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_THROW_A_ROCK);
			case KICKOFF_OFFICIOUS_REF:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_OFFICIOUS_REF);
			case KICKOFF_VERY_SUNNY:
				return AnimationSequenceKickoff.createAnimationSequence(fieldDimension, IIconProperty.ANIMATION_KICKOFF_VERY_SUNNY);
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

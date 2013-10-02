package com.balancedbytes.games.ffb.client.animation;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.Animation;

/**
 * 
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
		switch (pAnimation.getAnimationType()) {
			case KICKOFF_BLITZ:
				return AnimationSequenceKickoff.KICKOFF_BLITZ;
			case KICKOFF_BLIZZARD:
				return AnimationSequenceKickoff.KICKOFF_BLIZZARD;
			case KICKOFF_BRILLIANT_COACHING:
				return AnimationSequenceKickoff.KICKOFF_BRILLIANT_COACHING;
			case KICKOFF_CHEERING_FANS:
				return AnimationSequenceKickoff.KICKOFF_CHEERING_FANS;
			case KICKOFF_GET_THE_REF:
				return AnimationSequenceKickoff.KICKOFF_GET_THE_REF;
			case KICKOFF_HIGH_KICK:
				return AnimationSequenceKickoff.KICKOFF_HIGH_KICK;
			case KICKOFF_NICE:
				return AnimationSequenceKickoff.KICKOFF_NICE;
			case KICKOFF_PERFECT_DEFENSE:
				return AnimationSequenceKickoff.KICKOFF_PERFECT_DEFENSE;
			case KICKOFF_PITCH_INVASION:
				return AnimationSequenceKickoff.KICKOFF_PITCH_INVASION;
			case KICKOFF_POURING_RAIN:
				return AnimationSequenceKickoff.KICKOFF_POURING_RAIN;
			case KICKOFF_QUICK_SNAP:
				return AnimationSequenceKickoff.KICKOFF_QUICK_SNAP;
			case KICKOFF_RIOT:
				return AnimationSequenceKickoff.KICKOFF_RIOT;
			case KICKOFF_SWELTERING_HEAT:
				return AnimationSequenceKickoff.KICKOFF_SWELTERING_HEAT;
			case KICKOFF_THROW_A_ROCK:
				return AnimationSequenceKickoff.KICKOFF_THROW_A_ROCK;
			case KICKOFF_VERY_SUNNY:
				return AnimationSequenceKickoff.KICKOFF_VERY_SUNNY;
			case SPELL_FIREBALL:
				return AnimationSequenceSpecialEffect.createAnimationSequenceFireball(pAnimation.getStartCoordinate());
			case SPELL_LIGHTNING:
				return AnimationSequenceSpecialEffect.createAnimationSequenceLightning(pAnimation.getStartCoordinate());
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
			default:
				return null;
		}
	}
	
}

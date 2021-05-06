package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.IClientProperty;
import com.fumbbl.ffb.client.IClientPropertyValue;
import com.fumbbl.ffb.client.layer.FieldLayer;
import com.fumbbl.ffb.client.sound.SoundEngine;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Kalimar
 */
public class AnimationSequenceKickoff implements IAnimationSequence, ActionListener {

	public static final AnimationSequenceKickoff KICKOFF_BLITZ = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_BLITZ);

	public static final AnimationSequenceKickoff KICKOFF_BLIZZARD = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_BLIZZARD);

	public static final AnimationSequenceKickoff KICKOFF_BRILLIANT_COACHING = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_BRILLIANT_COACHING);

	public static final AnimationSequenceKickoff KICKOFF_CHEERING_FANS = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_CHEERING_FANS);

	public static final AnimationSequenceKickoff KICKOFF_GET_THE_REF = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_GET_THE_REF);

	public static final AnimationSequenceKickoff KICKOFF_HIGH_KICK = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_HIGH_KICK);

	public static final AnimationSequenceKickoff KICKOFF_NICE = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_NICE);

	public static final AnimationSequenceKickoff KICKOFF_PERFECT_DEFENSE = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_PERFECT_DEFENSE);

	public static final AnimationSequenceKickoff KICKOFF_SOLID_DEFENSE = createAnimationSequence(
		IIconProperty.ANIMATION_KICKOFF_SOLID_DEFENCE);

	public static final AnimationSequenceKickoff KICKOFF_PITCH_INVASION = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_PITCH_INVASION);

	public static final AnimationSequenceKickoff KICKOFF_POURING_RAIN = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_POURING_RAIN);

	public static final AnimationSequenceKickoff KICKOFF_QUICK_SNAP = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_QUICK_SNAP);

	public static final AnimationSequenceKickoff KICKOFF_RIOT = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_RIOT);

	public static final AnimationSequenceKickoff KICKOFF_TIMEOUT = createAnimationSequence(
		IIconProperty.ANIMATION_KICKOFF_TIMEOUT);

	public static final AnimationSequenceKickoff KICKOFF_SWELTERING_HEAT = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_SWELTERING_HEAT);

	public static final AnimationSequenceKickoff KICKOFF_THROW_A_ROCK = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_THROW_A_ROCK);

	public static final AnimationSequenceKickoff KICKOFF_VERY_SUNNY = createAnimationSequence(
			IIconProperty.ANIMATION_KICKOFF_VERY_SUNNY);

	private static AnimationSequenceKickoff createAnimationSequence(String pIconProperty) {
		return new AnimationSequenceKickoff(new AnimationFrame[] { new AnimationFrame(pIconProperty, 0.5f, 0.3, 100),
				new AnimationFrame(pIconProperty, 0.6f, 0.4, 100), new AnimationFrame(pIconProperty, 0.7f, 0.5, 100),
				new AnimationFrame(pIconProperty, 0.8f, 0.6, 100), new AnimationFrame(pIconProperty, 0.9f, 0.7, 100),
				new AnimationFrame(pIconProperty, 1.0f, 0.8, 100), new AnimationFrame(pIconProperty, 1.0f, 0.9, 100),
				new AnimationFrame(pIconProperty, 1.0f, 1.0, 2000), new AnimationFrame(pIconProperty, 0.8f, 1.0, 100),
				new AnimationFrame(pIconProperty, 0.6f, 1.0, 100), new AnimationFrame(pIconProperty, 0.4f, 1.0, 100),
				new AnimationFrame(pIconProperty, 0.2f, 1.0, 100) });
	}

	private static final int _TIMER_DELAY = 100;

	private AnimationFrame[] fFrames;
	private int fPosition;
	private Timer fTimer;
	private FieldLayer fFieldLayer;
	private int fX;
	private int fY;
	private int fDelay;
	private IAnimationListener fListener;

	protected AnimationSequenceKickoff(AnimationFrame[] pFrames) {
		fFrames = pFrames;
		fX = FieldLayer.FIELD_IMAGE_WIDTH / 2;
		fY = FieldLayer.FIELD_IMAGE_HEIGHT / 2;
		fTimer = new Timer(_TIMER_DELAY, this);
	}

	public void play(FieldLayer pFieldLayer, IAnimationListener pListener) {
		fFieldLayer = pFieldLayer;
		fListener = pListener;
		fPosition = -1;
		fDelay = 0;
		fTimer.start();
	}

	public void actionPerformed(ActionEvent pE) {
		fDelay -= _TIMER_DELAY;
		if (fDelay > 0) {
			return;
		}
		if (fPosition >= 0) {
			fFrames[fPosition].clear();
		}
		if (fPosition < fFrames.length - 1) {
			fPosition++;
			fDelay = fFrames[fPosition].getTime();
			fFrames[fPosition].drawCenteredAndScaled(fFieldLayer, fX, fY);
			if (fFrames[fPosition].getSound() != null) {
				SoundEngine soundEngine = fFieldLayer.getClient().getUserInterface().getSoundEngine();
				String soundSetting = fFieldLayer.getClient().getProperty(IClientProperty.SETTING_SOUND_MODE);
				if (IClientPropertyValue.SETTING_SOUND_ON.equals(soundSetting)
						|| (IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting))) {
					soundEngine.playSound(fFrames[fPosition].getSound());
				}
			}
		} else {
			fTimer.stop();
			fPosition = -1;
		}
		fFieldLayer.getClient().getUserInterface().getFieldComponent().refresh();
		if ((fPosition < 0) && (fListener != null)) {
			fListener.animationFinished();
		}
	}

}

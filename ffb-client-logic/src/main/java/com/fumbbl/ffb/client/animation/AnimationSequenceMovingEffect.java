package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.layer.FieldLayer;
import com.fumbbl.ffb.client.sound.SoundEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimationSequenceMovingEffect implements IAnimationSequence, ActionListener {

	public static AnimationSequenceMovingEffect createAnimationSequenceBreatheFire(FieldCoordinate start, FieldCoordinate end, DimensionProvider dimensionProvider) {
		return new AnimationSequenceMovingEffect(start, end, null, dimensionProvider, new AnimationFrame[]{
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_1, 1.0f, 500, SoundId.FIREBALL),
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_2, 1.0f, 500),
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_3, 1.0f, 500),
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_4, 1.0f, 500),
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_5, 1.0f, 500),
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_6, 1.0f, 500),
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_7, 1.0f, 500),
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_8, 1.0f, IIconProperty.ANIMATION_FIREBALL_SMOKE_1,
				1.0f, 500),
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_8, 0.7f, IIconProperty.ANIMATION_FIREBALL_SMOKE_2,
				1.0f, 500),
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_8, 0.5f, IIconProperty.ANIMATION_FIREBALL_SMOKE_3,
				1.0f, 500),
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_SMOKE_3, 1.0f, 500),
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_SMOKE_4, 0.7f, 500),
			new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_SMOKE_4, 0.5f, 500)});
	}

	private static final int _TIMER_DELAY = 100;

	private final SoundId fSound;
	private final Timer fTimer;
	private int fDelay;
	private int fPosition;
	private final AnimationFrame[] frames;

	private final AnimationProjector animationProjector;

	private FieldLayer fFieldLayer;
	private IAnimationListener fListener;

	protected AnimationSequenceMovingEffect(FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate,
																					SoundId pSound, DimensionProvider dimensionProvider, AnimationFrame[] frames) {
		fSound = pSound;
		fTimer = new Timer((int) (20 / dimensionProvider.getScale()), this);
		this.animationProjector = new AnimationProjector(pStartCoordinate, pEndCoordinate, null, dimensionProvider);
		this.frames = frames;
	}

	public void play(FieldLayer pFieldLayer, IAnimationListener pListener) {

		fFieldLayer = pFieldLayer;
		fListener = pListener;

		String soundSetting = fFieldLayer.getClient().getProperty(CommonProperty.SETTING_SOUND_MODE);
		if ((fSound != null) && (IClientPropertyValue.SETTING_SOUND_ON.equals(soundSetting)
			|| IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting))) {
			SoundEngine soundEngine = fFieldLayer.getClient().getUserInterface().getSoundEngine();
			soundEngine.playSound(fSound);
		}

		fTimer.start();

	}

	public void actionPerformed(ActionEvent pE) {

		fDelay -= _TIMER_DELAY;

		if (fPosition >= 0) {
			frames[fPosition].clear();
		}

		boolean isAtValidFrame = fPosition < frames.length - 1;
		if (fDelay <= 0) {
			if (isAtValidFrame) {
				fPosition++;
			}
		}
		boolean stopAnimation = false;
		if (isAtValidFrame) {

			double scale = 1.0;
			stopAnimation = animationProjector.updateCurrentDimension();

			fDelay = frames[fPosition].getTime();
			frames[fPosition].draw(fFieldLayer, animationProjector.getCurrentDimension(), scale);
			fFieldLayer.getClient().getUserInterface().getFieldComponent().refresh();
		}

		if (!isAtValidFrame || stopAnimation) {
			frames[fPosition].clear();
			fTimer.stop();
			fPosition = -1;
			if (fListener != null) {
				fListener.animationFinished();
			}
		}
	}
}

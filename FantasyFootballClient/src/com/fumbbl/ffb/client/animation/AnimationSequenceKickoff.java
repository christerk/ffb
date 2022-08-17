package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.IClientProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.layer.FieldLayer;
import com.fumbbl.ffb.client.sound.SoundEngine;

import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Kalimar
 */
public class AnimationSequenceKickoff implements IAnimationSequence, ActionListener {

	private final AnimationFrame[] fFrames;

	private static final int _TIMER_DELAY = 100;
	private final Timer fTimer;
	private int fPosition;
	private final int fX;
	private FieldLayer fFieldLayer;
	private final int fY;

	protected AnimationSequenceKickoff(Dimension containerDimension, AnimationFrame[] pFrames) {
		fFrames = pFrames;
		fX = containerDimension.width / 2;
		fY = containerDimension.height / 2;
		fTimer = new Timer(_TIMER_DELAY, this);
	}

	private int fDelay;
	private IAnimationListener fListener;

	public static AnimationSequenceKickoff createAnimationSequence(Dimension containerDimension, String pIconProperty, boolean portrait) {
		double[] scaleSteps;

		if (portrait) {
			scaleSteps = new double[]{0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65};
		} else {
			scaleSteps = new double[]{0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
		}

		return new AnimationSequenceKickoff(containerDimension, new AnimationFrame[]{new AnimationFrame(pIconProperty, 0.5f, scaleSteps[0], 100),
			new AnimationFrame(pIconProperty, 0.6f, scaleSteps[1], 100), new AnimationFrame(pIconProperty, 0.7f, scaleSteps[2], 100),
			new AnimationFrame(pIconProperty, 0.8f, scaleSteps[3], 100), new AnimationFrame(pIconProperty, 0.9f, scaleSteps[4], 100),
			new AnimationFrame(pIconProperty, 1.0f, scaleSteps[5], 100), new AnimationFrame(pIconProperty, 1.0f, scaleSteps[6], 100),
			new AnimationFrame(pIconProperty, 1.0f, scaleSteps[7], 2000), new AnimationFrame(pIconProperty, 0.8f, scaleSteps[7], 100),
			new AnimationFrame(pIconProperty, 0.6f, scaleSteps[7], 100), new AnimationFrame(pIconProperty, 0.4f, scaleSteps[7], 100),
			new AnimationFrame(pIconProperty, 0.2f, scaleSteps[7], 100)});
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

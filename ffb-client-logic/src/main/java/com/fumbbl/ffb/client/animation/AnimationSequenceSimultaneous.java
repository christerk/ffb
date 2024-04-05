package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.layer.FieldLayer;
import com.fumbbl.ffb.client.sound.SoundEngine;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimationSequenceSimultaneous implements IAnimationSequence, ActionListener {

	public static AnimationSequenceSimultaneous[] createAnimationSequenceTrickster(FantasyFootballClient client, Animation animation) {
		return new AnimationSequenceSimultaneous[]{new AnimationSequenceSimultaneous(animation.getStartCoordinate(),
			new AnimationFrame[]{
				new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_GLOW_1, 0.8f, 120, SoundId.HYPNO),
				new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_GLOW_2, 0.6f, 120),
				new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_GLOW_3, 0.4f, 120),
				new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_GLOW_4, 0.2f, 0.8d, 120),
				new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_1, 1.0f, 0.35d, 150),
				new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_2, 1.0f, 0.35d, 150),
				new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_3, 0.7f, 0.35d, 200),
				new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_4, 0.5f, 0.35d, 200),
			}) {
			@Override
			public void play(FieldLayer pFieldLayer, IAnimationListener pListener) {
				super.play(pFieldLayer, pListener);
				Game game = client.getGame();
				FieldModel fieldModel = game.getFieldModel();
				Player<?> player = game.getPlayerById(animation.getThrownPlayerId());
				fieldModel.setPlayerState(player, fieldModel.getPlayerState(player).changeBase(PlayerState.IN_THE_AIR));
			}
		},
			new AnimationSequenceSimultaneous(animation.getEndCoordinate(),
				new AnimationFrame[]{
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_GLOW_1, 0.8f, 120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_GLOW_2, 0.6f, 120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_GLOW_3, 0.4f, 120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_GLOW_4, 0.2f, 0.8d, 120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_1, 1.0f, 0.35d, 150),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_2, 1.0f, 0.35d, 150),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_3, 0.7f, 0.35d, 200),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_4, 0.5f, 0.35d, 200),
				})
		};
	}


	private final AnimationFrame[] fFrames;

	private static final int _TIMER_DELAY = 100;
	private final Timer fTimer;
	private int fPosition;
	private final FieldCoordinate fCoordinate;
	private FieldLayer fFieldLayer;

	private int fDelay;
	private IAnimationListener fListener;

	protected AnimationSequenceSimultaneous(FieldCoordinate pCoordinate, AnimationFrame[] pFrames) {
		fCoordinate = pCoordinate;
		fFrames = pFrames;
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
			DimensionProvider dimensionProvider = fFieldLayer.getClient().getUserInterface().getDimensionProvider();
			Dimension dimension = dimensionProvider.mapToLocal(fCoordinate, true);
			fPosition++;
			fDelay = fFrames[fPosition].getTime();
			fFrames[fPosition].drawCenteredAndScaled(fFieldLayer, dimension.width, dimension.height);
			if (fFrames[fPosition].getSound() != null) {
				SoundEngine soundEngine = fFieldLayer.getClient().getUserInterface().getSoundEngine();
				String soundSetting = fFieldLayer.getClient().getProperty(CommonProperty.SETTING_SOUND_MODE);
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

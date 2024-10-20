package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.PlayerIconFactory;
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
import java.awt.image.BufferedImage;

public class AnimationSequenceChained implements IAnimationSequence, ActionListener {

	public static AnimationSequenceChained createAnimationSequenceTrickster(FantasyFootballClient client, Animation animation) {
		DimensionProvider dimensionProvider = client.getUserInterface().getDimensionProvider();
		PlayerIconFactory playerIconFactory = client.getUserInterface().getPlayerIconFactory();
		Player<?> player = client.getGame().getPlayerById(animation.getThrownPlayerId());
		BufferedImage icon = playerIconFactory.getIcon(client, player);

		return new AnimationSequenceChained(
			new AnimationData(dimensionProvider.mapToLocal(animation.getStartCoordinate(), true),
				new AnimationFrame[]{
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_GLOW_1, 0.8f, 1.0d, icon, 1.0f, 1.0d,120, SoundId.HYPNO),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_GLOW_2, 0.6f, 1.0d, icon, 0.9f, 0.95d,120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_GLOW_3, 0.5f, 1.0d, icon, 0.8f, 0.9d,120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_GLOW_4, 0.4f, 0.8d, icon, 0.7f, 0.85d,120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_1, 1.0f, 0.35d,icon, 0.5f, 0.6d, 120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_2, 1.0f, 0.35d, icon, 0.3f, 0.4d,120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_3, 0.7f, 0.35d, icon, 0.2f, 0.2d,120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_4, 0.5f, 0.35d, icon, 0.0f, 0.05d,120),
				}),
			new AnimationData(dimensionProvider.mapToLocal(animation.getEndCoordinate(), true),
				new AnimationFrame[]{
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_EXPLOSION_1, 0.8f, 0.6d, 120, SoundId.BLUNDER),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_EXPLOSION_2, 0.6f, 0.6d, icon, 0.2f, 0.2d,120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_1, 1.0f, 0.35d, icon, 0.4f, 0.4d,120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_2, 1.0f, 0.35d, icon, 0.8f, 0.8d,120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_3, 0.7f, 0.35d, icon, 0.9f, 0.9d,120),
					new AnimationFrame(IIconProperty.ANIMATION_TRICKSTER_SMOKE_4, 0.5f, 0.35d, icon, 1.0f, 1.0d,120),
				})
		) {
			@Override
			public void play(FieldLayer pFieldLayer, IAnimationListener pListener) {
				super.play(pFieldLayer, pListener);
				Game game = client.getGame();
				FieldModel fieldModel = game.getFieldModel();
				Player<?> player = game.getPlayerById(animation.getThrownPlayerId());
				PlayerState playerState = fieldModel.getPlayerState(player);
				animation.setOldPlayerState(playerState);
				fieldModel.setPlayerState(player, playerState.changeBase(PlayerState.IN_THE_AIR));
			}
		};
	}


	public static AnimationSequenceChained createAnimationSequenceBlastin(FantasyFootballClient client, Animation animation) {
		DimensionProvider dimensionProvider = client.getUserInterface().getDimensionProvider();

		return new AnimationSequenceChained(
			new AnimationData(dimensionProvider.mapToLocal(animation.getStartCoordinate(), true),
				new AnimationFrame[]{
					new AnimationFrame(IIconProperty.ANIMATION_BLASTIN_EXPLOSION_1, 0.8f, 0.5d, 60, SoundId.BLUNDER),
					new AnimationFrame(IIconProperty.ANIMATION_BLASTIN_SMOKE_1, 1.0f, 0.35d, 60),
					new AnimationFrame(IIconProperty.ANIMATION_BLASTIN_SMOKE_2, 1.0f, 0.15d,60),
				}),
			new AnimationData(dimensionProvider.mapToLocal(animation.getEndCoordinate(), true),
				new AnimationFrame[]{
					new AnimationFrame(IIconProperty.ANIMATION_BLASTIN_EXPLOSION_1, 0.8f, 0.6d,  120, SoundId.EXPLODE),
					new AnimationFrame(IIconProperty.ANIMATION_BLASTIN_EXPLOSION_2, 0.6f, 0.6d, 120),
					new AnimationFrame(IIconProperty.ANIMATION_BLASTIN_SMOKE_1, 1.0f, 0.35d,  120),
					new AnimationFrame(IIconProperty.ANIMATION_BLASTIN_SMOKE_2, 1.0f, 0.35d, 120),
					new AnimationFrame(IIconProperty.ANIMATION_BLASTIN_SMOKE_3, 0.7f, 0.35d, 120),
					new AnimationFrame(IIconProperty.ANIMATION_BLASTIN_SMOKE_4, 0.5f, 0.35d, 120),
				})
		);
	}


	private AnimationFrame[] frames;
	private final AnimationData[] dataFrames;

	private static final int _TIMER_DELAY = 100;
	private final Timer timer;
	private int framePosition, dataPosition;
	private FieldLayer fFieldLayer;

	private int fDelay;
	private IAnimationListener fListener;
	private Dimension position;

	protected AnimationSequenceChained(AnimationData... frames) {
		this.dataFrames = frames;
		timer = new Timer(_TIMER_DELAY, this);
	}

	public void play(FieldLayer pFieldLayer, IAnimationListener pListener) {
		fFieldLayer = pFieldLayer;
		fListener = pListener;
		framePosition = -1;
		dataPosition = 0;
		frames = dataFrames[dataPosition].frames;
		position = dataFrames[dataPosition].position;
		fDelay = 0;
		timer.start();
	}

	public void actionPerformed(ActionEvent pE) {
		fDelay -= _TIMER_DELAY;
		if (fDelay > 0) {
			return;
		}
		if (framePosition >= 0) {
			frames[framePosition].clear();
		}
		if (framePosition < frames.length - 1) {
			framePosition++;
			fDelay = frames[framePosition].getTime();
			frames[framePosition].drawCenteredAndScaled(fFieldLayer, position.width, position.height);
			if (frames[framePosition].getSound() != null) {
				SoundEngine soundEngine = fFieldLayer.getClient().getUserInterface().getSoundEngine();
				String soundSetting = fFieldLayer.getClient().getProperty(CommonProperty.SETTING_SOUND_MODE);
				if (IClientPropertyValue.SETTING_SOUND_ON.equals(soundSetting)
					|| (IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting))) {
					soundEngine.playSound(frames[framePosition].getSound());
				}
			}
		} else if (dataPosition < dataFrames.length - 1) {
			framePosition = -1;
			dataPosition++;
			frames = dataFrames[dataPosition].frames;
			position = dataFrames[dataPosition].position;
		} else {
			timer.stop();
			dataPosition = -1;
		}
		fFieldLayer.getClient().getUserInterface().getFieldComponent().refresh();
		if ((dataPosition < 0) && (fListener != null)) {
			fListener.animationFinished();
		}
	}


	private static class AnimationData {
		private final Dimension position;
		private final AnimationFrame[] frames;

		private AnimationData(Dimension position, AnimationFrame[] frames) {
			this.position = position;
			this.frames = frames;
		}
	}
}

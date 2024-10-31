package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.layer.FieldLayer;
import com.fumbbl.ffb.client.sound.SoundEngine;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class AnimationSequenceThrowing implements IAnimationSequence, ActionListener {

	protected AnimationSequenceThrowing(AnimationType pAnimationType, BufferedImage pAnimatedIcon,
																			FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate, FieldCoordinate pInterceptorCoordinate,
																			SoundId pSound, DimensionProvider dimensionProvider) {
		fAnimationType = pAnimationType;
		fAnimatedIcon = pAnimatedIcon;
		fSound = pSound;
		fTimer = new Timer((int) (20 / dimensionProvider.getLayoutSettings().getScale()), this);
		this.animationProjector = new AnimationProjector(pStartCoordinate, pEndCoordinate, pInterceptorCoordinate, dimensionProvider,
			new CoordinateBasedSteppingStrategy(pStartCoordinate, pEndCoordinate));
	}

	public static AnimationSequenceThrowing createAnimationSequencePass(FantasyFootballClient pClient,
																																			Animation pAnimation) {
		return new AnimationSequenceThrowing(AnimationType.PASS,
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_BALL),
			pAnimation.getStartCoordinate(), pAnimation.getEndCoordinate(), pAnimation.getInterceptorCoordinate(),
			SoundId.THROW, pClient.getUserInterface().getDimensionProvider());
	}

	public static AnimationSequenceThrowing createAnimationSequenceThrowBomb(FantasyFootballClient pClient,
			Animation pAnimation) {
		return new AnimationSequenceThrowing(AnimationType.THROW_BOMB,
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_BOMB),
			pAnimation.getStartCoordinate(), pAnimation.getEndCoordinate(), pAnimation.getInterceptorCoordinate(),
			SoundId.THROW, pClient.getUserInterface().getDimensionProvider());
	}

	public static AnimationSequenceThrowing createAnimationSequenceThrowARock(FantasyFootballClient pClient,
			Animation pAnimation) {
		return new AnimationSequenceThrowing(AnimationType.THROW_A_ROCK,
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_ROCK),
			pAnimation.getStartCoordinate(), pAnimation.getEndCoordinate(), pAnimation.getInterceptorCoordinate(),
			SoundId.THROW, pClient.getUserInterface().getDimensionProvider());
	}

	public static AnimationSequenceThrowing createAnimationSequenceKick(FantasyFootballClient pClient,
																																			Animation pAnimation) {
		return new AnimationSequenceThrowing(AnimationType.KICK,
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_BALL_BIG),
			pAnimation.getStartCoordinate(), pAnimation.getEndCoordinate(), pAnimation.getInterceptorCoordinate(),
			SoundId.KICK, pClient.getUserInterface().getDimensionProvider());
	}

	private final AnimationType fAnimationType;

	public static AnimationSequenceThrowing createAnimationSequenceHailMaryPass(FantasyFootballClient pClient,
																																							Animation pAnimation) {
		return new AnimationSequenceThrowing(AnimationType.HAIL_MARY_PASS,
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_BALL_BIG),
			pAnimation.getStartCoordinate(), pAnimation.getEndCoordinate(), pAnimation.getInterceptorCoordinate(),
			SoundId.THROW, pClient.getUserInterface().getDimensionProvider());
	}

	private final BufferedImage fAnimatedIcon;
	private final SoundId fSound;
	private final Timer fTimer;

	private final AnimationProjector animationProjector;

	private Rectangle fLastIconBounds;

	private FieldLayer fFieldLayer;
	private IAnimationListener fListener;

	public static AnimationSequenceThrowing createAnimationSequenceHailMaryBomb(FantasyFootballClient pClient,
																																							Animation pAnimation) {
		return new AnimationSequenceThrowing(AnimationType.HAIL_MARY_BOMB,
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_BOMB_BIG),
			pAnimation.getStartCoordinate(), pAnimation.getEndCoordinate(), pAnimation.getInterceptorCoordinate(),
			SoundId.THROW, pClient.getUserInterface().getDimensionProvider());
	}

	public static AnimationSequenceThrowing createAnimationSequenceThrowTeamMate(FantasyFootballClient pClient,
																																							 Animation pAnimation) {
		Player<?> thrownPlayer = pClient.getGame().getPlayerById(pAnimation.getThrownPlayerId());
		boolean homePlayer = pClient.getGame().getTeamHome().hasPlayer(thrownPlayer);
		PlayerIconFactory playerIconFactory = pClient.getUserInterface().getPlayerIconFactory();
		BufferedImage playerIcon = playerIconFactory.getBasicIcon(pClient, thrownPlayer, homePlayer, false,
			pAnimation.isWithBall(), false);
		return new AnimationSequenceThrowing(AnimationType.THROW_TEAM_MATE, playerIcon, pAnimation.getStartCoordinate(),
			pAnimation.getEndCoordinate(), null, SoundId.WOOOAAAH, pClient.getUserInterface().getDimensionProvider());
	}

	public static AnimationSequenceThrowing createAnimationSequenceThrowKeg(FantasyFootballClient pClient,
																																					Animation pAnimation) {
		return new AnimationSequenceThrowing(pAnimation.getAnimationType(),
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.ACTION_BEER_BARREL_BASH),
			pAnimation.getStartCoordinate(), pAnimation.getEndCoordinate(), pAnimation.getInterceptorCoordinate(),
			SoundId.THROW, pClient.getUserInterface().getDimensionProvider());
	}

	public void play(FieldLayer pFieldLayer, IAnimationListener pListener) {

		fFieldLayer = pFieldLayer;
		fListener = pListener;
		fLastIconBounds = null;

		String soundSetting = fFieldLayer.getClient().getProperty(CommonProperty.SETTING_SOUND_MODE);
		if ((fSound != null) && (IClientPropertyValue.SETTING_SOUND_ON.equals(soundSetting)
				|| IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting))) {
			SoundEngine soundEngine = fFieldLayer.getClient().getUserInterface().getSoundEngine();
			soundEngine.playSound(fSound);
		}

		fTimer.start();

	}

	public void actionPerformed(ActionEvent pE) {

		fFieldLayer.clear(fLastIconBounds, true);

		boolean stopAnimation = animationProjector.updateCurrentDimension();
		double scale = animationProjector.findScale(fAnimationType);

		fLastIconBounds = fFieldLayer.drawCenteredAndScaled(fAnimatedIcon, animationProjector.getCurrentDimension().width, animationProjector.getCurrentDimension().height, 1.0f, scale, scale);
		fFieldLayer.getClient().getUserInterface().getFieldComponent().refresh();

		if (stopAnimation) {
			fTimer.stop();
			fFieldLayer.clear(fLastIconBounds, true);
			fListener.animationFinished();
		}
	}
}

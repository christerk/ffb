package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IClientProperty;
import com.fumbbl.ffb.client.IClientPropertyValue;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.layer.FieldLayer;
import com.fumbbl.ffb.client.sound.SoundEngine;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Player;

import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class AnimationSequenceThrowing implements IAnimationSequence, ActionListener {

	private final DimensionProvider dimensionProvider;

	protected AnimationSequenceThrowing(AnimationType pAnimationType, BufferedImage pAnimatedIcon,
																			FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate, FieldCoordinate pInterceptorCoordinate,
																			SoundId pSound, DimensionProvider dimensionProvider) {
		fAnimationType = pAnimationType;
		fAnimatedIcon = pAnimatedIcon;
		fStartCoordinate = pStartCoordinate;
		fEndCoordinate = pEndCoordinate;
		fInterceptorCoordinate = pInterceptorCoordinate;
		fSound = pSound;
		fTimer = new Timer(20, this);
		this.dimensionProvider = dimensionProvider;
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
	private final FieldCoordinate fStartCoordinate;
	private final FieldCoordinate fEndCoordinate;
	private final FieldCoordinate fInterceptorCoordinate;
	private final SoundId fSound;
	private final Timer fTimer;

	private int fStartX;
	private int fStartY;

	private int fEndX;
	private int fEndY;

	private int fInterceptorX;
	private int fInterceptorY;

	private int fPositionX;
	private int fPositionY;

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

		Dimension startDimension = dimensionProvider.mapToLocal(fStartCoordinate, true);
		fStartX = startDimension.width;
		fStartY = startDimension.height;

		Dimension endDimension = dimensionProvider.mapToLocal(fEndCoordinate, true);
		fEndX = endDimension.width;
		fEndY = endDimension.height;

		fInterceptorX = fEndX;
		fInterceptorY = fEndY;

		if (fInterceptorCoordinate != null) {
			Dimension intDimension = dimensionProvider.mapToLocal(fInterceptorCoordinate, true);
			fInterceptorX = intDimension.width;
			fInterceptorY = intDimension.height;
		}

		fPositionX = fStartX;
		fPositionY = fStartY;

		String soundSetting = fFieldLayer.getClient().getProperty(IClientProperty.SETTING_SOUND_MODE);
		if ((fSound != null) && (IClientPropertyValue.SETTING_SOUND_ON.equals(soundSetting)
				|| IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting))) {
			SoundEngine soundEngine = fFieldLayer.getClient().getUserInterface().getSoundEngine();
			soundEngine.playSound(fSound);
		}

		fTimer.start();

	}

	public void actionPerformed(ActionEvent pE) {

		fFieldLayer.clear(fLastIconBounds, true);

		double scale;
		boolean stopAnimation;
		boolean xAxisAnimation = (Math.abs(fEndX - fStartX) > Math.abs(fEndY - fStartY));

		if (xAxisAnimation) {
			// y - y1 = (y2 - y1) / (x2 - x1) * (x - x1)
			fPositionY = fStartY + (int) (((double) (fEndY - fStartY) / (double) (fEndX - fStartX)) * (fPositionX - fStartX));
			scale = findScale(((double) (fPositionX - fStartX) / (double) (fEndX - fStartX)) * 2);
		} else {
			// x - x1 = (x2 - x1) / (y2 - y1) * (y - y1)
			fPositionX = fStartX + (int) (((double) (fEndX - fStartX) / (double) (fEndY - fStartY)) * (fPositionY - fStartY));
			scale = findScale(((double) (fPositionY - fStartY) / (double) (fEndY - fStartY)) * 2);
		}

//    System.out.println("scale " + scale);

		fLastIconBounds = fFieldLayer.drawCenteredAndScaled(fAnimatedIcon, fPositionX, fPositionY, 1.0f, scale, scale);
		fFieldLayer.getClient().getUserInterface().getFieldComponent().refresh();

		int stepping = findStepping();
		if (xAxisAnimation) {
			if (fStartX < fEndX) {
				fPositionX += stepping;
				stopAnimation = (fPositionX >= fInterceptorX);
			} else {
				fPositionX -= stepping;
				stopAnimation = (fPositionX <= fInterceptorX);
			}
		} else {
			if (fStartY < fEndY) {
				fPositionY += stepping;
				stopAnimation = fPositionY >= fInterceptorY;
			} else {
				fPositionY -= stepping;
				stopAnimation = fPositionY <= fInterceptorY;
			}
		}

		if (stopAnimation) {
			fTimer.stop();
			fFieldLayer.clear(fLastIconBounds, true);
			fListener.animationFinished();
		}

	}

	private int findStepping() {
		if ((fStartCoordinate == null) || (fEndCoordinate == null)) {
			return 0;
		}
		int deltaX = Math.abs(fEndCoordinate.getX() - fStartCoordinate.getX());
		int deltaY = Math.abs(fEndCoordinate.getY() - fStartCoordinate.getY());
		int deltaMax = Math.max(deltaX, deltaY);
		if (deltaMax <= 7) {
			return 2;
		} else {
			return 3;
		}
	}

	private double findScale(double pX) {
		if ((AnimationType.PASS == fAnimationType) || (AnimationType.THROW_A_ROCK == fAnimationType)
			|| (AnimationType.THROW_BOMB == fAnimationType)) {
			return 1 - ((pX - 1) * (pX - 1) * 0.5);
		} else if (AnimationType.THROW_TEAM_MATE == fAnimationType || AnimationType.THROW_KEG == fAnimationType) {
			return 1.5 - ((pX - 1) * (pX - 1) * 0.5);
		} else if ((AnimationType.KICK == fAnimationType) || (AnimationType.HAIL_MARY_PASS == fAnimationType)
			|| (AnimationType.HAIL_MARY_BOMB == fAnimationType)) {
			return 1 - ((pX - 1) * (pX - 1) * 0.75);
		} else {
			return 0.0;
		}
	}

}

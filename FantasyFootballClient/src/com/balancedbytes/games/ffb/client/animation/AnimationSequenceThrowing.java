package com.balancedbytes.games.ffb.client.animation;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IClientProperty;
import com.balancedbytes.games.ffb.client.IClientPropertyValue;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.PlayerIconFactory;
import com.balancedbytes.games.ffb.client.layer.FieldLayer;
import com.balancedbytes.games.ffb.client.sound.SoundEngine;
import com.balancedbytes.games.ffb.client.state.ClientState;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
public class AnimationSequenceThrowing implements IAnimationSequence, ActionListener {
	
	public static AnimationSequenceThrowing createAnimationSequencePass(FantasyFootballClient pClient, Animation pAnimation) {
		return new AnimationSequenceThrowing(
			AnimationType.PASS,
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_BALL),
			pAnimation.getStartCoordinate(),
			pAnimation.getEndCoordinate(),
			pAnimation.getInterceptorCoordinate(),
			SoundId.THROW
		);
	}

	public static AnimationSequenceThrowing createAnimationSequenceThrowBomb(FantasyFootballClient pClient, Animation pAnimation) {
		return new AnimationSequenceThrowing(
			AnimationType.THROW_BOMB,
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_BOMB),
			pAnimation.getStartCoordinate(),
			pAnimation.getEndCoordinate(),
			pAnimation.getInterceptorCoordinate(),
			SoundId.THROW
		);
	}

	public static AnimationSequenceThrowing createAnimationSequenceThrowARock(FantasyFootballClient pClient, Animation pAnimation) {
		return new AnimationSequenceThrowing(
			AnimationType.THROW_A_ROCK,
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_ROCK),
			pAnimation.getStartCoordinate(),
			pAnimation.getEndCoordinate(),
			pAnimation.getInterceptorCoordinate(),
			SoundId.THROW
		);
	}

	public static AnimationSequenceThrowing createAnimationSequenceKick(FantasyFootballClient pClient, Animation pAnimation) {
		return new AnimationSequenceThrowing(
			AnimationType.KICK,
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_BALL_BIG),
			pAnimation.getStartCoordinate(),
			pAnimation.getEndCoordinate(),
			pAnimation.getInterceptorCoordinate(),
			SoundId.KICK
		);
	}

	public static AnimationSequenceThrowing createAnimationSequenceHailMaryPass(FantasyFootballClient pClient, Animation pAnimation) {
		return new AnimationSequenceThrowing(
			AnimationType.HAIL_MARY_PASS,
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_BALL_BIG),
			pAnimation.getStartCoordinate(),
			pAnimation.getEndCoordinate(),
			pAnimation.getInterceptorCoordinate(),
			SoundId.THROW
		);
	}

	public static AnimationSequenceThrowing createAnimationSequenceHailMaryBomb(FantasyFootballClient pClient, Animation pAnimation) {
		return new AnimationSequenceThrowing(
			AnimationType.HAIL_MARY_BOMB,
			pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_BOMB_BIG),
			pAnimation.getStartCoordinate(),
			pAnimation.getEndCoordinate(),
			pAnimation.getInterceptorCoordinate(),
			SoundId.THROW
		);
	}
	
	public static AnimationSequenceThrowing createAnimationSequenceThrowTeamMate(FantasyFootballClient pClient, Animation pAnimation) {
	  Player thrownPlayer = pClient.getGame().getPlayerById(pAnimation.getThrownPlayerId());
	  boolean homePlayer = pClient.getGame().getTeamHome().hasPlayer(thrownPlayer);
	  PlayerIconFactory playerIconFactory = pClient.getUserInterface().getPlayerIconFactory();
	  BufferedImage playerIcon = playerIconFactory.getBasicIcon(pClient, thrownPlayer, homePlayer, false, pAnimation.isWithBall(), false);
		return new AnimationSequenceThrowing(
			AnimationType.THROW_TEAM_MATE,
			playerIcon,
			pAnimation.getStartCoordinate(),
			pAnimation.getEndCoordinate(),
			null,
			SoundId.WOOOAAAH
		);
	}

	private AnimationType fAnimationType;
  private BufferedImage fAnimatedIcon;
  private FieldCoordinate fStartCoordinate;
  private FieldCoordinate fEndCoordinate;
  private FieldCoordinate fInterceptorCoordinate;
  private SoundId fSound;
  
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
  
  private Timer fTimer;
	
	protected AnimationSequenceThrowing(AnimationType pAnimationType, BufferedImage pAnimatedIcon, FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate, FieldCoordinate pInterceptorCoordinate, SoundId pSound)  {
    fAnimationType = pAnimationType;
		fAnimatedIcon = pAnimatedIcon;
    fStartCoordinate = pStartCoordinate;
    fEndCoordinate = pEndCoordinate;
    fInterceptorCoordinate = pInterceptorCoordinate;
    fSound = pSound;
		fTimer = new Timer(20, this);
	}

  public void play(FieldLayer pFieldLayer, IAnimationListener pListener) {

  	fFieldLayer = pFieldLayer;
    fListener = pListener;
    fLastIconBounds = null;
    
    fStartX = (fStartCoordinate.getX() * ClientState.FIELD_SQUARE_SIZE) + (ClientState.FIELD_SQUARE_SIZE / 2);
    fStartY = (fStartCoordinate.getY() * ClientState.FIELD_SQUARE_SIZE) + (ClientState.FIELD_SQUARE_SIZE / 2);
    
    fEndX = (fEndCoordinate.getX() * ClientState.FIELD_SQUARE_SIZE) + (ClientState.FIELD_SQUARE_SIZE / 2);
    fEndY = (fEndCoordinate.getY() * ClientState.FIELD_SQUARE_SIZE) + (ClientState.FIELD_SQUARE_SIZE / 2);

    fInterceptorX = fEndX;
    fInterceptorY = fEndY;

    if (fInterceptorCoordinate != null) {
      fInterceptorX = (fInterceptorCoordinate.getX() * ClientState.FIELD_SQUARE_SIZE) + (ClientState.FIELD_SQUARE_SIZE / 2);
      fInterceptorY = (fInterceptorCoordinate.getY() * ClientState.FIELD_SQUARE_SIZE) + (ClientState.FIELD_SQUARE_SIZE / 2);
    }
    
    fPositionX = fStartX;
    fPositionY = fStartY;
    
    String soundSetting = fFieldLayer.getClient().getProperty(IClientProperty.SETTING_SOUND_MODE);
    if ((fSound != null) && (IClientPropertyValue.SETTING_SOUND_ON.equals(soundSetting) || IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting))) {
      SoundEngine soundEngine = fFieldLayer.getClient().getUserInterface().getSoundEngine();
      soundEngine.playSound(fSound);
    }
    
    fTimer.start();
    
  }
  
  public void actionPerformed(ActionEvent pE) {
    
    fFieldLayer.clear(fLastIconBounds, true);

    double scale = 0.0;
    boolean stopAnimation = false;
    boolean xAxisAnimation = (Math.abs(fEndX - fStartX) > Math.abs(fEndY - fStartY));
    
    if (xAxisAnimation) {
      fPositionY = fStartY + (int) (((double) (fEndY - fStartY) / (double) (fEndX - fStartX)) * (fPositionX - fStartX));  // y - y1 = (y2 - y1) / (x2 - x1) * (x - x1)
      scale = findScale(((double) (fPositionX - fStartX) / (double) (fEndX - fStartX)) * 2);
    } else {
      fPositionX = fStartX + (int) (((double) (fEndX - fStartX) / (double) (fEndY - fStartY)) * (fPositionY - fStartY));  // x - x1 = (x2 - x1) / (y2 - y1) * (y - y1)
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
    if ((AnimationType.PASS == fAnimationType) || (AnimationType.THROW_A_ROCK == fAnimationType) || (AnimationType.THROW_BOMB == fAnimationType)) {
      return 1 - ((pX - 1) * (pX - 1) * 0.5); 
    } else if (AnimationType.THROW_TEAM_MATE == fAnimationType) {
      return 1.5 - ((pX - 1) * (pX - 1) * 0.5); 
    } else if ((AnimationType.KICK == fAnimationType) || (AnimationType.HAIL_MARY_PASS == fAnimationType) || (AnimationType.HAIL_MARY_BOMB == fAnimationType)) {
    	return 1 - ((pX - 1) * (pX - 1) * 0.75);
    } else {
    	return 0.0;
    }
  }

}

package com.balancedbytes.games.ffb.client.animation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.client.IClientProperty;
import com.balancedbytes.games.ffb.client.IClientPropertyValue;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.layer.FieldLayer;
import com.balancedbytes.games.ffb.client.sound.SoundEngine;

/**
 * 
 * @author Kalimar
 */
public class AnimationSequenceSpecialEffect implements IAnimationSequence, ActionListener {

  public static final AnimationSequenceSpecialEffect createAnimationSequenceBomb(FieldCoordinate pCoordinate) {
    return new AnimationSequenceSpecialEffect(
      pCoordinate,
      new AnimationFrame[] {
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_1, 1.0f, 100, Sound.EXPLODE),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_2, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_3, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_4, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_5, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_6, 1.0f, 200),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_6, 0.7f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_6, 0.5f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_6, 0.2f, 100)
      }
    );
  }

  public static final AnimationSequenceSpecialEffect createAnimationSequenceFireball(FieldCoordinate pCoordinate) {
    return new AnimationSequenceSpecialEffect(
  		pCoordinate,
  		new AnimationFrame[] {
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_1, 1.0f, 100, Sound.FIREBALL),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_2, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_3, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_4, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_5, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_6, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_7, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_8, 1.0f, IIconProperty.ANIMATION_FIREBALL_SMOKE_1, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_8, 0.7f, IIconProperty.ANIMATION_FIREBALL_SMOKE_2, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_EXPLOSION_8, 0.5f, IIconProperty.ANIMATION_FIREBALL_SMOKE_3, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_SMOKE_3, 1.0f, 100), new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_SMOKE_3, 0.7f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_FIREBALL_SMOKE_3, 0.5f, 100)
      }
    );
  }

  public static final AnimationSequenceSpecialEffect createAnimationSequenceLightning(FieldCoordinate pCoordinate) {
    return new AnimationSequenceSpecialEffect(
    	pCoordinate,
    	new AnimationFrame[] {
    		new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_01, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_02, 1.0f, 100), new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_03, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_04, 1.0f, 100), new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_05, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_06, 1.0f, 200),
        new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_07, 1.0f, 200, Sound.LIGHTNING),
        new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_06, 1.0f, 200),
        new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_07, 1.0f, 200, Sound.LIGHTNING),
        new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_06, 1.0f, 200), new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_08, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_09, 1.0f, 100), new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_10, 1.0f, 100),
        new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_11, 1.0f, 100), new AnimationFrame(IIconProperty.ANIMATION_LIGHTNING_12, 1.0f, 100)
    	}
    );
  }

  private static final int _TIMER_DELAY = 100;

  private AnimationFrame[] fFrames;
  private int fPosition;
  private Timer fTimer;
  private FieldLayer fFieldLayer;
  private FieldCoordinate fCoordinate;
  private int fDelay;
  private IAnimationListener fListener;

  protected AnimationSequenceSpecialEffect(FieldCoordinate pCoordinate, AnimationFrame[] pFrames) {
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
      fPosition++;
      fDelay = fFrames[fPosition].getTime();
      fFrames[fPosition].draw(fFieldLayer, fCoordinate);
      if (fFrames[fPosition].getSound() != null) {
        SoundEngine soundEngine = fFieldLayer.getClient().getUserInterface().getSoundEngine();
        String soundSetting = fFieldLayer.getClient().getProperty(IClientProperty.SETTING_SOUND_MODE);
        if (IClientPropertyValue.SETTING_SOUND_ON.equals(soundSetting) || (IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting))) {
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

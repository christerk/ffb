package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.layer.FieldLayer;
import com.fumbbl.ffb.client.sound.SoundEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class AnimationSequenceMovingEffect implements IAnimationSequence, ActionListener {

  public static AnimationSequenceMovingEffect createAnimationSequenceBreatheFire(FieldCoordinate start, FieldCoordinate end, PitchDimensionProvider dimensionProvider) {
    return new AnimationSequenceMovingEffect(start, end, dimensionProvider, 1000, new AnimationFrame[]{
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

  private final int timerDelay;

  private final long duration;
  private final long totalDurationWeight;
  private final Timer fTimer;
  private long fDelay;
  private int framePos = -1;
  private final AnimationFrame[] frames;

  private final AnimationProjector animationProjector;

  private FieldLayer fFieldLayer;
  private IAnimationListener fListener;

  protected AnimationSequenceMovingEffect(FieldCoordinate pStartCoordinate, FieldCoordinate pEndCoordinate,
                                          PitchDimensionProvider dimensionProvider, long duration, AnimationFrame[] frames) {
    timerDelay = (int) (20 / (dimensionProvider.getLayoutSettings().getScale() * dimensionProvider.getLayoutSettings().getLayout().getPitchScale()));
    fTimer = new Timer(timerDelay, this);
    this.frames = frames;
    this.duration = duration;
    this.totalDurationWeight = Arrays.stream(frames).mapToLong(AnimationFrame::getTime).sum();
    this.animationProjector = new AnimationProjector(pStartCoordinate, pEndCoordinate, null, dimensionProvider,
      new TimerBasedSteppingStrategy(dimensionProvider.mapToLocal(pStartCoordinate),
        dimensionProvider.mapToLocal(pEndCoordinate), duration, timerDelay));
  }

  public void play(FieldLayer pFieldLayer, IAnimationListener pListener) {

    fFieldLayer = pFieldLayer;
    fListener = pListener;

    fTimer.start();

  }

  public void actionPerformed(ActionEvent pE) {

    fDelay -= timerDelay;

    if (framePos >= 0) {
      frames[framePos].clear();
    }

    boolean framesLeft = framePos < frames.length - 1;
    if (fDelay <= 0) {
      if (framesLeft) {
        framePos++;
        fDelay = frameDuration(frames[framePos]);
        String soundSetting = fFieldLayer.getClient().getProperty(CommonProperty.SETTING_SOUND_MODE);
        SoundId sound = frames[framePos].getSound();
        if ((sound != null) && (IClientPropertyValue.SETTING_SOUND_ON.equals(soundSetting)
          || IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting))) {
          SoundEngine soundEngine = fFieldLayer.getClient().getUserInterface().getSoundEngine();
          soundEngine.playSound(sound);
        }
      }
    }
    if (framesLeft) {

      double scale = 0.25;
      animationProjector.updateCurrentDimension();

      frames[framePos].draw(fFieldLayer, animationProjector.getCurrentDimension(), scale);
      fFieldLayer.getClient().getUserInterface().getFieldComponent().refresh();
    }

    if (!framesLeft) {
      frames[framePos].clear();
      fTimer.stop();
      framePos = -1;
      if (fListener != null) {
        fListener.animationFinished();
      }
    }
  }

  private long frameDuration(AnimationFrame frame) {
    return (frame.getTime() * duration) / totalDurationWeight;
  }
}

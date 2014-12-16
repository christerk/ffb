package com.balancedbytes.games.ffb.client.sound;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class SoundEngine {

  private static Map<SoundId, String> _SOUND_PROPERTY_KEYS = Collections.synchronizedMap(new HashMap<SoundId, String>());

  static {
    _SOUND_PROPERTY_KEYS.put(SoundId.BLOCK, ISoundProperty.BLOCK);
    _SOUND_PROPERTY_KEYS.put(SoundId.BLUNDER, ISoundProperty.BLUNDER);
    _SOUND_PROPERTY_KEYS.put(SoundId.BOUNCE, ISoundProperty.BOUNCE);
    _SOUND_PROPERTY_KEYS.put(SoundId.CATCH, ISoundProperty.CATCH);
    _SOUND_PROPERTY_KEYS.put(SoundId.CHAINSAW, ISoundProperty.CHAINSAW);
    _SOUND_PROPERTY_KEYS.put(SoundId.CLICK, ISoundProperty.CLICK);
    _SOUND_PROPERTY_KEYS.put(SoundId.DING, ISoundProperty.DING);
    _SOUND_PROPERTY_KEYS.put(SoundId.DODGE, ISoundProperty.DODGE);
    _SOUND_PROPERTY_KEYS.put(SoundId.DUH, ISoundProperty.DUH);
    _SOUND_PROPERTY_KEYS.put(SoundId.EW, ISoundProperty.EW);
    _SOUND_PROPERTY_KEYS.put(SoundId.EXPLODE, ISoundProperty.EXPLODE);
    _SOUND_PROPERTY_KEYS.put(SoundId.FALL, ISoundProperty.FALL);
    _SOUND_PROPERTY_KEYS.put(SoundId.FIREBALL, ISoundProperty.FIREBALL);
    _SOUND_PROPERTY_KEYS.put(SoundId.FOUL, ISoundProperty.FOUL);
    _SOUND_PROPERTY_KEYS.put(SoundId.HYPNO, ISoundProperty.HYPNO);
    _SOUND_PROPERTY_KEYS.put(SoundId.INJURY, ISoundProperty.INJURY);
    _SOUND_PROPERTY_KEYS.put(SoundId.KICK, ISoundProperty.KICK);
    _SOUND_PROPERTY_KEYS.put(SoundId.KO, ISoundProperty.KO);
    _SOUND_PROPERTY_KEYS.put(SoundId.LIGHTNING, ISoundProperty.LIGHTNING);
    _SOUND_PROPERTY_KEYS.put(SoundId.METAL, ISoundProperty.METAL);
    _SOUND_PROPERTY_KEYS.put(SoundId.NOMNOM, ISoundProperty.NOMNOM);
    _SOUND_PROPERTY_KEYS.put(SoundId.ORGAN, ISoundProperty.ORGAN);
    _SOUND_PROPERTY_KEYS.put(SoundId.PICKUP, ISoundProperty.PICKUP);
    _SOUND_PROPERTY_KEYS.put(SoundId.QUESTION, ISoundProperty.QUESTION);
    _SOUND_PROPERTY_KEYS.put(SoundId.RIP, ISoundProperty.RIP);
    _SOUND_PROPERTY_KEYS.put(SoundId.ROAR, ISoundProperty.ROAR);
    _SOUND_PROPERTY_KEYS.put(SoundId.ROOT, ISoundProperty.ROOT);
    _SOUND_PROPERTY_KEYS.put(SoundId.SLURP, ISoundProperty.SLURP);
    _SOUND_PROPERTY_KEYS.put(SoundId.SPEC_AAH, ISoundProperty.SPEC_AAH);
    _SOUND_PROPERTY_KEYS.put(SoundId.SPEC_BOO, ISoundProperty.SPEC_BOO);
    _SOUND_PROPERTY_KEYS.put(SoundId.SPEC_CHEER, ISoundProperty.SPEC_CHEER);
    _SOUND_PROPERTY_KEYS.put(SoundId.SPEC_CLAP, ISoundProperty.SPEC_CLAP);
    _SOUND_PROPERTY_KEYS.put(SoundId.SPEC_CRICKETS, ISoundProperty.SPEC_CRICKETS);
    _SOUND_PROPERTY_KEYS.put(SoundId.SPEC_LAUGH, ISoundProperty.SPEC_LAUGH);
    _SOUND_PROPERTY_KEYS.put(SoundId.SPEC_OOH, ISoundProperty.SPEC_OOH);
    _SOUND_PROPERTY_KEYS.put(SoundId.SPEC_SHOCK, ISoundProperty.SPEC_SHOCK);
    _SOUND_PROPERTY_KEYS.put(SoundId.SPEC_STOMP, ISoundProperty.SPEC_STOMP);
    _SOUND_PROPERTY_KEYS.put(SoundId.STEP, ISoundProperty.STEP);
    _SOUND_PROPERTY_KEYS.put(SoundId.STAB, ISoundProperty.STAB);
    _SOUND_PROPERTY_KEYS.put(SoundId.THROW, ISoundProperty.THROW);
    _SOUND_PROPERTY_KEYS.put(SoundId.TOUCHDOWN, ISoundProperty.TOUCHDOWN);
    _SOUND_PROPERTY_KEYS.put(SoundId.WHISTLE, ISoundProperty.WHISTLE);
    _SOUND_PROPERTY_KEYS.put(SoundId.WOOOAAAH, ISoundProperty.WOOOAAAH);
  }

  private FantasyFootballClient fClient;
  private Map<SoundId, Sound> fSoundById;
  private int fVolume;
  
  public SoundEngine(FantasyFootballClient pClient) {
    fClient = pClient;
    fSoundById = new HashMap<SoundId, Sound>();
  }
  
  public void init() {
    TinySound.init();
    TinySound.setGlobalVolume(0.5);
  }

  public void playSound(SoundId pSoundId) {
    Sound sound = fSoundById.get(pSoundId);
    if (sound == null) {
      String soundPropertyKey = _SOUND_PROPERTY_KEYS.get(pSoundId);
      if (StringTool.isProvided(soundPropertyKey)) {
        String fileProperty = new StringBuilder().append(soundPropertyKey).append(ISoundProperty.FILE_SUFFIX).toString();
        String soundResource = new StringBuilder().append("/sounds/").append(getClient().getProperty(fileProperty)).toString();
        sound = TinySound.loadSound(soundResource);
        fSoundById.put(pSoundId, sound);
      }
    }
    if (sound != null) {
      sound.play((double) fVolume / 100);
    }
  }
  
  public long getSoundLength(SoundId pSoundId) {
    StringBuilder lengthProperty = new StringBuilder().append(_SOUND_PROPERTY_KEYS.get(pSoundId)).append(ISoundProperty.LENGTH_SUFFIX);
    String length = getClient().getProperty(lengthProperty.toString());
    if (StringTool.isProvided(length)) {
      return Long.parseLong(length);
    } else {
      return 0;
    }
  }
  
  public FantasyFootballClient getClient() {
    return fClient;
  }
  
  // volume range 0 - 100
  public void setVolume(int pVolume) {
    fVolume = pVolume;
  }
  
  public int getVolume() {
    return fVolume;
  }

}

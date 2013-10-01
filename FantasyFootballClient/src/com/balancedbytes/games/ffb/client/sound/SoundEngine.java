package com.balancedbytes.games.ffb.client.sound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * @author Dominic Schabel
 * @author Kalimar
 */
public class SoundEngine extends Thread {

  private static Map<Sound, String> _SOUND_PROPERTY_KEYS = Collections.synchronizedMap(new HashMap<Sound, String>());

  static {
    _SOUND_PROPERTY_KEYS.put(Sound.BLOCK, ISoundProperty.BLOCK);
    _SOUND_PROPERTY_KEYS.put(Sound.BLUNDER, ISoundProperty.BLUNDER);
    // _SOUND_PROPERTY_KEYS.put(Sound.BOO, ISoundProperty.BOO);
    _SOUND_PROPERTY_KEYS.put(Sound.BOUNCE, ISoundProperty.BOUNCE);
    _SOUND_PROPERTY_KEYS.put(Sound.CATCH, ISoundProperty.CATCH);
    _SOUND_PROPERTY_KEYS.put(Sound.CHAINSAW, ISoundProperty.CHAINSAW);
    // _SOUND_PROPERTY_KEYS.put(Sound.CHEER, ISoundProperty.CHEER);
    _SOUND_PROPERTY_KEYS.put(Sound.CLICK, ISoundProperty.CLICK);
    _SOUND_PROPERTY_KEYS.put(Sound.DING, ISoundProperty.DING);
    _SOUND_PROPERTY_KEYS.put(Sound.DODGE, ISoundProperty.DODGE);
    _SOUND_PROPERTY_KEYS.put(Sound.DUH, ISoundProperty.DUH);
    _SOUND_PROPERTY_KEYS.put(Sound.EW, ISoundProperty.EW);
    _SOUND_PROPERTY_KEYS.put(Sound.EXPLODE, ISoundProperty.EXPLODE);
    _SOUND_PROPERTY_KEYS.put(Sound.FALL, ISoundProperty.FALL);
    _SOUND_PROPERTY_KEYS.put(Sound.FIREBALL, ISoundProperty.FIREBALL);
    _SOUND_PROPERTY_KEYS.put(Sound.FOUL, ISoundProperty.FOUL);
    _SOUND_PROPERTY_KEYS.put(Sound.HYPNO, ISoundProperty.HYPNO);
    _SOUND_PROPERTY_KEYS.put(Sound.INJURY, ISoundProperty.INJURY);
    _SOUND_PROPERTY_KEYS.put(Sound.KICK, ISoundProperty.KICK);
    _SOUND_PROPERTY_KEYS.put(Sound.KO, ISoundProperty.KO);
    _SOUND_PROPERTY_KEYS.put(Sound.LIGHTNING, ISoundProperty.LIGHTNING);
    _SOUND_PROPERTY_KEYS.put(Sound.METAL, ISoundProperty.METAL);
    _SOUND_PROPERTY_KEYS.put(Sound.NOMNOM, ISoundProperty.NOMNOM);
    _SOUND_PROPERTY_KEYS.put(Sound.ORGAN, ISoundProperty.ORGAN);
    _SOUND_PROPERTY_KEYS.put(Sound.PICKUP, ISoundProperty.PICKUP);
    _SOUND_PROPERTY_KEYS.put(Sound.QUESTION, ISoundProperty.QUESTION);
    _SOUND_PROPERTY_KEYS.put(Sound.RIP, ISoundProperty.RIP);
    _SOUND_PROPERTY_KEYS.put(Sound.ROAR, ISoundProperty.ROAR);
    _SOUND_PROPERTY_KEYS.put(Sound.ROOT, ISoundProperty.ROOT);
    _SOUND_PROPERTY_KEYS.put(Sound.SLURP, ISoundProperty.SLURP);
    _SOUND_PROPERTY_KEYS.put(Sound.SPEC_AAH, ISoundProperty.SPEC_AAH);
    _SOUND_PROPERTY_KEYS.put(Sound.SPEC_BOO, ISoundProperty.SPEC_BOO);
    _SOUND_PROPERTY_KEYS.put(Sound.SPEC_CHEER, ISoundProperty.SPEC_CHEER);
    _SOUND_PROPERTY_KEYS.put(Sound.SPEC_CLAP, ISoundProperty.SPEC_CLAP);
    _SOUND_PROPERTY_KEYS.put(Sound.SPEC_CRICKETS, ISoundProperty.SPEC_CRICKETS);
    _SOUND_PROPERTY_KEYS.put(Sound.SPEC_LAUGH, ISoundProperty.SPEC_LAUGH);
    _SOUND_PROPERTY_KEYS.put(Sound.SPEC_OOH, ISoundProperty.SPEC_OOH);
    _SOUND_PROPERTY_KEYS.put(Sound.SPEC_SHOCK, ISoundProperty.SPEC_SHOCK);
    _SOUND_PROPERTY_KEYS.put(Sound.SPEC_STOMP, ISoundProperty.SPEC_STOMP);
    _SOUND_PROPERTY_KEYS.put(Sound.STEP, ISoundProperty.STEP);
    _SOUND_PROPERTY_KEYS.put(Sound.STAB, ISoundProperty.STAB);
    _SOUND_PROPERTY_KEYS.put(Sound.THROW, ISoundProperty.THROW);
    _SOUND_PROPERTY_KEYS.put(Sound.TOUCHDOWN, ISoundProperty.TOUCHDOWN);
    _SOUND_PROPERTY_KEYS.put(Sound.WHISTLE, ISoundProperty.WHISTLE);
    _SOUND_PROPERTY_KEYS.put(Sound.WOOOAAAH, ISoundProperty.WOOOAAAH);
  }

  private class AudioData {
    
    private byte[] fAudio;
    private AudioFormat fFormat;
    
    public AudioData(byte[] pAudio, AudioFormat pFormat) {
      fAudio = pAudio;
      fFormat = pFormat;
    }
    
    public byte[] getAudio() {
      return fAudio;
    }
    
    public AudioFormat getFormat() {
      return fFormat;
    }
    
    public int size() {
      return (fAudio != null) ? fAudio.length : 0;
    }
    
  }
  
  private FantasyFootballClient fClient;
  private Map<Sound, AudioData> fAudioDataBySound;
  private int fVolume;
  
  public SoundEngine(FantasyFootballClient pClient) {
    fClient = pClient;
    fAudioDataBySound = new HashMap<Sound, AudioData>();
    start();
  }

  public void playSound(Sound pSound) {
    
    Clip clip = null;
    AudioData audioData = loadAudioData(pSound);
    if (audioData != null) {
      
      try {
        DataLine.Info info = new DataLine.Info(Clip.class, audioData.getFormat(), audioData.size());
        clip = (Clip) AudioSystem.getLine(info);
        clip.open(audioData.getFormat(), audioData.getAudio(), 0, audioData.size());
      } catch (LineUnavailableException lue) {
        // clip remains null
      }

      if (clip != null) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)){
          FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
          double gain = fVolume/100.0;
          float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
          gainControl.setValue(dB);
        }
        clip.start(); // Start playing
      }
      
    }
    
  }

  private AudioData loadAudioData(Sound pSound) {
    AudioData audioData = fAudioDataBySound.get(pSound);

    if (audioData == null) {
      
      try {
  
        StringBuilder fileProperty = new StringBuilder().append(_SOUND_PROPERTY_KEYS.get(pSound)).append(ISoundProperty.FILE_SUFFIX);
        StringBuilder soundPath = new StringBuilder().append("/sounds/").append(getClient().getProperty(fileProperty.toString()));
  
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(soundPath.toString())));
        AudioFormat format = audioInputStream.getFormat();
  
        int size = ((int) audioInputStream.getFrameLength() * format.getFrameSize());
        byte[] audio = new byte[size];
        audioInputStream.read(audio, 0, size);
        
        audioData = new AudioData(audio, format);
        fAudioDataBySound.put(pSound, audioData);
        
      } catch (UnsupportedAudioFileException uafe) {
        throw new FantasyFootballException(uafe);
      } catch (IOException ioe) {
        throw new FantasyFootballException(ioe);
      }
    
    }
      
    return audioData;
    
  }
  
  public long getSoundLength(Sound pSound) {
    StringBuilder lengthProperty = new StringBuilder().append(_SOUND_PROPERTY_KEYS.get(pSound)).append(ISoundProperty.LENGTH_SUFFIX);
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

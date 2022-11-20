package com.fumbbl.ffb.client.sound;

import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.util.StringTool;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public class SoundEngine {

	private static final Map<SoundId, String> _SOUND_PROPERTY_KEYS = Collections
		.synchronizedMap(new HashMap<>());

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
		_SOUND_PROPERTY_KEYS.put(SoundId.ZAP, ISoundProperty.ZAP);
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
		_SOUND_PROPERTY_KEYS.put(SoundId.SPEC_HURT, ISoundProperty.SPEC_HURT);
		_SOUND_PROPERTY_KEYS.put(SoundId.SPEC_LAUGH, ISoundProperty.SPEC_LAUGH);
		_SOUND_PROPERTY_KEYS.put(SoundId.SPEC_OOH, ISoundProperty.SPEC_OOH);
		_SOUND_PROPERTY_KEYS.put(SoundId.SPEC_SHOCK, ISoundProperty.SPEC_SHOCK);
		_SOUND_PROPERTY_KEYS.put(SoundId.SPEC_STOMP, ISoundProperty.SPEC_STOMP);
		_SOUND_PROPERTY_KEYS.put(SoundId.STEP, ISoundProperty.STEP);
		_SOUND_PROPERTY_KEYS.put(SoundId.STAB, ISoundProperty.STAB);
		_SOUND_PROPERTY_KEYS.put(SoundId.SWOOP, ISoundProperty.SWOOP);
		_SOUND_PROPERTY_KEYS.put(SoundId.THROW, ISoundProperty.THROW);
		_SOUND_PROPERTY_KEYS.put(SoundId.TOUCHDOWN, ISoundProperty.TOUCHDOWN);
		_SOUND_PROPERTY_KEYS.put(SoundId.WHISTLE, ISoundProperty.WHISTLE);
		_SOUND_PROPERTY_KEYS.put(SoundId.WOOOAAAH, ISoundProperty.WOOOAAAH);
		_SOUND_PROPERTY_KEYS.put(SoundId.PUMP_CROWD, ISoundProperty.PUMP_CROWD);
		_SOUND_PROPERTY_KEYS.put(SoundId.TRAPDOOR, ISoundProperty.TRAPDOOR);
		_SOUND_PROPERTY_KEYS.put(SoundId.VOMIT, ISoundProperty.VOMIT);
	}

	private final FantasyFootballClient fClient;
	private final Map<SoundId, Sound> fSoundById;
	private int fVolume;

	public SoundEngine(FantasyFootballClient pClient) {
		fClient = pClient;
		fSoundById = new HashMap<>();
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
				String fileProperty = soundPropertyKey + ISoundProperty.FILE_SUFFIX;
				String soundResource = "/sounds/" + getClient().getProperty(fileProperty);
				sound = TinySound.loadSound(soundResource);
				fSoundById.put(pSoundId, sound);
			}
		}
		if (sound != null) {
			sound.play((double) fVolume / 100);
		}
	}

	public long getSoundLength(SoundId pSoundId) {
		String lengthProperty = _SOUND_PROPERTY_KEYS.get(pSoundId) +
			ISoundProperty.LENGTH_SUFFIX;
		String length = getClient().getProperty(lengthProperty);
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
}

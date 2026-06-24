package com.fumbbl.ffb;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ChatCommand {
	AAH("/aah", "aaahing spectators", "crowd sighs", SoundId.SPEC_AAH),
	BOO("/boo", "booing spectators", "crowd booing", SoundId.SPEC_BOO),
	CHEER("/cheer", "cheering spectators", "crowd cheering", SoundId.SPEC_CHEER),
	CLAP("/clap", "clapping spectators", "applause", SoundId.SPEC_CLAP),
	CRICKETS("/crickets", "the sound of crickets in the grass", "crickets chirping", SoundId.SPEC_CRICKETS),
	HURT("/hurt", "announcer exclaiming", "announcer: ooh, that's gotta hurt!", SoundId.SPEC_HURT),
	LAUGH("/laugh", "laughing spectators", "crowd laughing", SoundId.SPEC_LAUGH),
	OOH("/ooh", "ooohing spectators", "crowd oohing", SoundId.SPEC_OOH),
	SHOCK("/shock", "shocked, gasping spectators", "crowd gasping in shock", SoundId.SPEC_SHOCK),
	STOMP("/stomp", "spectators stomping their feet", "crowd stomping", SoundId.SPEC_STOMP),
	SPECS("/specs", "shows all logged in spectators by name - can also be used by playing coaches", false);

	private final String command;
	private final String explanation;
	private final String caption;
	private final boolean effect;
	private final SoundId soundId;

	ChatCommand(String command, String description, String caption, SoundId soundId) {
		this(command, description, caption, true, soundId);
	}

	ChatCommand(String command, String description, boolean effect) {
		this(command, description, null, effect, null);
	}

	ChatCommand(String command, String description, String caption, boolean effect, SoundId soundId) {
		this.command = command;
		this.explanation = description;
		this.caption = caption;
		this.effect = effect;
		this.soundId = soundId;
	}

	public String getCommand() {
		return command;
	}

	public String getDescription() {
		return explanation;
	}

	public boolean isEffect() {
		return effect;
	}

	public SoundId getSoundId() {
		return soundId;
	}

	public String getCaption() {
		return "[" + caption + "]";
	}

	public static ChatCommand fromSoundId(SoundId soundId) {
		if (soundId == null) {
			return null;
		}
		for (ChatCommand chatCommand : values()) {
			if (chatCommand.getSoundId() == soundId) {
				return chatCommand;
			}
		}
		return null;
	}

	public static ChatCommand fromCommand(String command) {
		for (ChatCommand chatCommand : values()) {
			if (chatCommand.getCommand().equals(command)) {
				return chatCommand;
			}
		}
		return null;
	}

	public static Set<String> asStrings() {
		return Arrays.stream(values()).map(ChatCommand::getCommand).collect(Collectors.toSet());
	}

	public static Set<String> effectsAsStrings() {
		return Arrays.stream(values()).filter(ChatCommand::isEffect).map(ChatCommand::getCommand).collect(Collectors.toSet());
	}

	public static Set<String> controlCommandsAsStrings() {
		return Arrays.stream(values()).filter(command -> !command.isEffect()).map(ChatCommand::getCommand).collect(Collectors.toSet());
	}
}

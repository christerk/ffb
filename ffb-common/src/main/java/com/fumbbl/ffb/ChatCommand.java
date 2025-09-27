package com.fumbbl.ffb;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ChatCommand {
	AAH("/aah", "aaahing spectators"), BOO("/boo", "booing spectators"),
	CHEER("/cheer", "cheering spectators"), CLAP("/clap", "clapping spectators"),
	CRICKETS("/crickets", "the sound of crickets in the grass"), HURT("/hurt", "announcer exclaiming"),
	LAUGH("/laugh", "laughing spectators"), OOH("/ooh", "ooohing spectators"),
	SHOCK("/shock", "shocked, gasping spectators"), STOMP("/stomp", "spectators stomping their feet"),
	SPECS("/specs", "shows all logged in spectators by name - can also be used by playing coaches", false);

	private final String command;
	private final String explanation;
	private final boolean effect;

	ChatCommand(String command, String explanation) {
		this(command, explanation, true);
	}

	ChatCommand(String command, String explanation, boolean effect) {
		this.command = command;
		this.explanation = explanation;
		this.effect = effect;
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

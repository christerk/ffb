package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.ChatCommand;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandSound;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandHandlerSound extends ClientCommandHandler {

	protected ClientCommandHandlerSound(FantasyFootballClient pClient) {
		super(pClient);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_SOUND;
	}

	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
		ServerCommandSound soundCommand = (ServerCommandSound) pNetCommand;
		playSound(soundCommand.getSound(), pMode, false);
		renderSpectatorCaption(soundCommand.getSound());
		return true;
	}

	private void renderSpectatorCaption(SoundId soundId) {
		ChatCommand chatCommand = ChatCommand.fromSoundId(soundId);
		if (chatCommand != null
				&& IClientPropertyValue.SETTING_SOUND_SPECTATOR_CAPTIONS_ON.equals(
				getClient().getProperty(CommonProperty.SETTING_SOUND_SPECTATOR_CAPTIONS))) {
			getClient().getUserInterface().getChat().append(TextStyle.EXPLANATION, chatCommand.getCaption());
		}
	}
}

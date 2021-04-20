package com.fumbbl.ffb;

/**
 * 
 * @author Kalimar
 */
public enum PlayerChoiceMode implements INamedObject {

	TENTACLES("tentacles"), SHADOWING("shadowing"), DIVING_TACKLE("divingTackle"), FEED("feed"),
	DIVING_CATCH("divingCatch"), CARD("card"), BLOCK("block"), MVP("mvp");

	private final String name;

	PlayerChoiceMode(String pName) {
		name = pName;
	}

	public String getName() {
		return name;
	}

	public String getDialogHeader(int nrOfPlayers) {
		StringBuilder header = new StringBuilder();
		switch (this) {
		case DIVING_TACKLE:
			header.append("Select a player to use Diving Tackle");
			break;
		case SHADOWING:
			header.append("Select a player to use Shadowing");
			break;
		case TENTACLES:
			header.append("Select a player to use Tentacles");
			break;
		case FEED:
			header.append("Select a player to feed on");
			break;
		case DIVING_CATCH:
			header.append("Select a player to use Diving Catch");
			break;
		case CARD:
			header.append("Select a player to play this card on");
			break;
		case BLOCK:
			header.append("Select a player to block");
			break;
		case MVP:
			header.append("Nominate ").append(nrOfPlayers).append(" for the MVP");
			break;
		default:
			break;
		}
		return header.toString();
	}

	public String getStatusTitle() {
		StringBuilder title = new StringBuilder();
		switch (this) {
		case DIVING_TACKLE:
			title.append("Diving Tackle");
			break;
		case SHADOWING:
			title.append("Shadowing");
			break;
		case TENTACLES:
			title.append("Tentacles");
			break;
		case FEED:
			title.append("Feed on player");
			break;
		case DIVING_CATCH:
			title.append("Diving Catch");
			break;
		case CARD:
			title.append("Play Card");
			break;
		case BLOCK:
			title.append("Block");
			break;
		case MVP:
			title.append("MVP");
			break;
		default:
			break;
		}
		return title.toString();
	}

	public String getStatusMessage() {
		StringBuilder message = new StringBuilder();
		switch (this) {
		case DIVING_TACKLE:
			message.append("Waiting for coach to use Diving Tackle.");
			break;
		case SHADOWING:
			message.append("Waiting for coach to use Shadowing.");
			break;
		case TENTACLES:
			message.append("Waiting for coach to use Tentacles.");
			break;
		case FEED:
			message.append("Waiting for coach to choose player to feed on.");
			break;
		case DIVING_CATCH:
			message.append("Waiting for coach to use Diving Catch.");
			break;
		case CARD:
			message.append("Waiting for coach to play card on player.");
			break;
		case BLOCK:
			message.append("Waiting for coach to choose player to block.");
			break;
		case MVP:
			message.append("Waiting for coach to nominate players for the MVP.");
			break;
		default:
			break;
		}
		return message.toString();
	}

}

package com.fumbbl.ffb;

/**
 * @author Kalimar
 */
public enum PlayerChoiceMode implements INamedObject {

	TENTACLES("tentacles"), SHADOWING("shadowing"), DIVING_TACKLE("divingTackle"), FEED("feed"),
	DIVING_CATCH("divingCatch"), DECLARE_DIVING_CATCH("declareDivingCatch"), CARD("card", false), BLOCK("block"),
	MVP("mvp"), ANIMAL_SAVAGERY("animalSavagery"), IRON_MAN("ironMan", false),
	KNUCKLE_DUSTERS("knuckleDusters", false), BLESSED_STATUE_OF_NUFFLE("blessedStatueOfNuffle", false),
	ASSIGN_TOUCHDOWN("assignTouchdown", false);

	private final String name;
	private final boolean usePlayerPosition;

	PlayerChoiceMode(String pName) {
		this(pName, true);
	}

	PlayerChoiceMode(String pName, boolean usePlayerPosition) {
		name = pName;
		this.usePlayerPosition = usePlayerPosition;
	}

	public String getName() {
		return name;
	}

	public boolean isUsePlayerPosition() {
		return usePlayerPosition;
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
			case ANIMAL_SAVAGERY:
				header.append("Select a player to lash out against");
				break;
			case DECLARE_DIVING_CATCH:
				header.append("Select ALL players that should try to catch the ball");
				break;
			case IRON_MAN:
				header.append("Select a player to become Iron Man");
				break;
			case KNUCKLE_DUSTERS:
				header.append("Select a player to obtain Knuckle Dusters");
				break;
			case BLESSED_STATUE_OF_NUFFLE:
				header.append("Select a player to receive the Blessed Statue of Nuffle");
				break;
			case ASSIGN_TOUCHDOWN:
				header.append("Assign a touchdown to one of your players");
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
			case ANIMAL_SAVAGERY:
				title.append("Animal Savagery");
				break;
			case DECLARE_DIVING_CATCH:
				title.append("Declare Diving Catch");
				break;
			case IRON_MAN:
				title.append("Iron Man");
				break;
			case KNUCKLE_DUSTERS:
				title.append("Knuckle Dusters");
				break;
			case BLESSED_STATUE_OF_NUFFLE:
				title.append("Blessed Statue of Nuffle");
				break;
			case ASSIGN_TOUCHDOWN:
				title.append("Touchdown from Concession");
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
			case ANIMAL_SAVAGERY:
				message.append("Waiting for coach to choose a player to lash out against.");
				break;
			case DECLARE_DIVING_CATCH:
				message.append("Waiting for coach to choose all players to use Diving Catch.");
				break;
			case IRON_MAN:
				message.append("Waiting for coach to choose a player to become Iron Man.");
				break;
			case KNUCKLE_DUSTERS:
				message.append("Waiting for coach to choose a player to obtain Knuckle Dusters.");
				break;
			case BLESSED_STATUE_OF_NUFFLE:
				message.append("Waiting for coach to choose a player to receive the Blessed Statue of Nuffle.");
				break;
			case ASSIGN_TOUCHDOWN:
				message.append("Waiting for coach to assign touchdown");
				break;
			default:
				break;
		}
		return message.toString();
	}

}

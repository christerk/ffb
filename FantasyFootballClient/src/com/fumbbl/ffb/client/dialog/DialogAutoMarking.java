package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IClientProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.FantasyFootballClient;

import java.util.ArrayList;
import java.util.List;

public class DialogAutoMarking extends DialogInformation {

	private DialogAutoMarking(FantasyFootballClient client) {
		this(client, null, null);
	}

	private DialogAutoMarking(FantasyFootballClient client, String setting, String offValue) {
		super(client, "Automatic Marking", messages(), DialogInformation.OK_DIALOG, false, null, setting, offValue);
	}

	private static String[] messages() {
		List<String> messages = new ArrayList<>();

		messages.add("You just turned on automatic player markings.");
		messages.add("This uses the markings defined at https://fumbbl.com/p/clientoptions");
		messages.add("Details can be found at https://fumbbl.com/help:AutomaticMarking");
		messages.add("This dialog can be displayed again via the help menu");

		return messages.toArray(new String[0]);
	}

	public static DialogAutoMarking create(FantasyFootballClient client, boolean withCheckbox) {
		return withCheckbox ? new DialogAutoMarking(client, IClientProperty.SETTING_SHOW_AUTO_MARKING_DIALOG, IClientPropertyValue.SETTING_HIDE_AUTO_MARKING_DIALOG) : new DialogAutoMarking(client);
	}
}

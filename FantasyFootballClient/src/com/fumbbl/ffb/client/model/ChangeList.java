package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

	private final List<VersionChangeList> versions = new ArrayList<>();

	public ChangeList() {

		versions.add(new VersionChangeList("2.3.0")
			.addImprovement("Added Change List Dialog")
			.addBugfix("Prevent to throw/kick player that was injured to severe by Animal Savagery")
			.addBugfix("Prevent apothecary usage on zapped players")
			.addBugfix("Do not use Chainsaw modifier when player throws regular block")
			.addBugfix("Prevent overflow in 2016 petty cash dialog")
			.addBugfix("Preserve labels in replay mode when playing/moving backwards")
		);
	}

}

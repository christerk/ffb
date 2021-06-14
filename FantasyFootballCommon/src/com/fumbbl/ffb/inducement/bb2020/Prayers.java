package com.fumbbl.ffb.inducement.bb2020;

import com.fumbbl.ffb.inducement.InducementDuration;
import com.fumbbl.ffb.model.Game;

import java.util.HashMap;
import java.util.Map;

public class Prayers {
	private final Map<Integer, Prayer> exhibitionPrayers = new HashMap<Integer, Prayer>() {
		{
			put(1, new Prayer("Treacherous Trapdoor",
				"Trapdoors appear. On a roll of 1 a player stepping on them falls through them",
				InducementDuration.UNTIL_END_OF_HALF, true) {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(2, new Prayer("Friends with the Ref", "Argue the call succeeds on 5+") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(3, new Prayer("Stiletto",
				"One random player available to play during this drive without Loner gains Stab",
				InducementDuration.UNTIL_END_OF_GAME) {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(4, new Prayer("Iron Man",
				"One chosen player available to play during this drive without Loner improves AV by 1 (Max 11+)") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(5, new Prayer("Knuckle Dusters",
				"One chosen player available to play during this drive without Loner gains Mighty Blow (+1)") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(6, new Prayer("Bad Habits",
				"D3 random opponent players available to play during this drive without Loner gain Loner (2+)") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(7, new Prayer("Greasy Cleats",
				"One random opponent player available to play during this drive has his MA reduced by 1") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(8, new Prayer("Blessed Statue of Nuffle",
				"One chosen player available to play during this drive without Loner gains Pro") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});
		}
	};

	private final Map<Integer, Prayer> leagueOnlyPrayers = new HashMap<Integer, Prayer>() {{
		put(9, new Prayer("Moles under the Pitch",
			"Rushes have a -1 modifier (-2 if both coaches rolled this result)") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(10, new Prayer("Perfect Passing",
			"Completions generate 2 instead of 1 spp") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(11, new Prayer("Fan Interaction",
			"Casualties caused crowd pushes generate 2 spp") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(12, new Prayer("Necessary Violence",
			"Casualties generate 3 instead of 2 spp") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(13, new Prayer("Fouling Frenzy",
			"Casualties caused fouls generate 2 spp") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(14, new Prayer("Throw a Rock",
			"If an opposing player should stall they get hit by a rock on a 5+ and knocked down immediately") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(15, new Prayer("Under Scrutiny",
			"Fouls by opposing players are always spotted",
			InducementDuration.UNTIL_END_OF_HALF) {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(16, new Prayer("Intensive Training",
			"One random player available to play during this drive without Loner gains a chosen Primary skill",
			InducementDuration.UNTIL_END_OF_GAME) {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

	}};

	public Map<Integer, Prayer> getExhibitionPrayers() {
		return exhibitionPrayers;
	}

	public Map<Integer, Prayer> getLeagueOnlyPrayers() {
		return leagueOnlyPrayers;
	}
}

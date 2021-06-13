package com.fumbbl.ffb.inducement.bb2020;

import com.fumbbl.ffb.model.Game;

import java.util.HashMap;
import java.util.Map;

public class Prayers {
	private final Map<Integer, Prayer> exhibitionPrayers = new HashMap<Integer, Prayer>() {
		{
			put(1, new Prayer("Treacherous Trapdoor") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(2, new Prayer("Friends with the Ref") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(3, new Prayer("Stiletto") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(4, new Prayer("Iron Man") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(5, new Prayer("Knuckle Dusters") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(6, new Prayer("Bad Habits") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(7, new Prayer("Greasy Cleats") {
				@Override
				public void apply(Game game) {

				}

				@Override
				public void undo(Game game) {

				}
			});

			put(8, new Prayer("Blessed Statue of Nuffle") {
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
		put(9, new Prayer("Moles under the Pitch") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(10, new Prayer("Perfect Passing") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(11, new Prayer("Fan Interaction") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(12, new Prayer("Necessary Violence") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(13, new Prayer("Fouling Frenzy") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(14, new Prayer("Throw a Rock") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(15, new Prayer("Under Scrutiny") {
			@Override
			public void apply(Game game) {

			}

			@Override
			public void undo(Game game) {

			}
		});

		put(16, new Prayer("Intensive Training") {
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

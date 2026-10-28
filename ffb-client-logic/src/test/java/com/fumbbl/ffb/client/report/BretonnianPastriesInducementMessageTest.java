package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.StatusReport;
import com.fumbbl.ffb.client.report.bb2025.BretonnianPastriesRollMessage;
import com.fumbbl.ffb.client.report.bb2025.InducementMessage;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.bb2025.InducementCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.ReportInducement;
import com.fumbbl.ffb.report.bb2025.ReportBretonnianPastriesRoll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

class BretonnianPastriesInducementMessageTest {

	private final StringBuilder log = new StringBuilder();
	private StatusReport statusReport;
	private Game game;
	private InducementType pastries;

	@BeforeEach
	void setUp() {
		pastries = new InducementCollection().getTypes().stream()
			.filter(type -> "bretonnianPastries".equals(type.getName()))
			.findFirst()
			.orElseThrow(() -> new AssertionError("bretonnianPastries missing from BB2025 inducements"));

		statusReport = mock(StatusReport.class, withSettings().defaultAnswer(invocation -> {
			String method = invocation.getMethod().getName();
			Object[] args = invocation.getArguments();
			if (method.equals("print") || method.equals("println")) {
				Object last = args[args.length - 1];
				log.append(last instanceof Player ? ((Player<?>) last).getName() : String.valueOf(last));
				if (method.equals("println")) {
					log.append('\n');
				}
			}
			return method.equals("getIndent") ? 0 : null;
		}));

		Team home = mock(Team.class);
		doReturn("homeTeam").when(home).getId();
		doReturn("Home Team").when(home).getName();
		Team away = mock(Team.class);
		doReturn("awayTeam").when(away).getId();
		doReturn("Away Team").when(away).getName();
		Player<?> player = mock(Player.class);
		doReturn("Bertrand").when(player).getName();

		game = mock(Game.class);
		doReturn(home).when(game).getTeamHome();
		doReturn(away).when(game).getTeamAway();
		doReturn(player).when(game).getPlayerById("away1");
	}

	@Test
	void spendNoticeIsAFullLineFollowedByTheRoll() {
		render(new InducementMessage(), new ReportInducement("awayTeam", pastries, 1));
		render(new BretonnianPastriesRollMessage(), new ReportBretonnianPastriesRoll("awayTeam", "away1", 4, true));

		assertEquals("Away Team use Bretonnian Pastries on a Knocked Out player.\n"
			+ "Bretonnian Pastries Roll [ 4 ]\n"
			+ "Bertrand is revived by the pastries and is placed in the Reserves box.\n", log.toString());
	}

	@Test
	void spendNoticeNamesTheHomeTeam() {
		render(new InducementMessage(), new ReportInducement("homeTeam", pastries, 1));

		assertEquals("Home Team use Bretonnian Pastries on a Knocked Out player.\n", log.toString());
	}

	@Test
	void pastriesSpendNoticeIsRenderedOnlyByTheBb2025Message() {
		assertEquals(Collections.singletonList(Rules.BB2025), rules(InducementMessage.class));
		assertEquals(Collections.singletonList(Rules.BB2020),
			rules(com.fumbbl.ffb.client.report.mixed.InducementMessage.class));
	}

	private List<Rules> rules(Class<?> messageClass) {
		return Arrays.stream(messageClass.getAnnotationsByType(RulesCollection.class)).map(RulesCollection::value)
			.collect(Collectors.toList());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void render(ReportMessageBase message, com.fumbbl.ffb.report.IReport report) {
		message.setStatusReport(statusReport);
		message.renderMessage(game, report);
	}
}

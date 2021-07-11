package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.SkillChoiceMode;
import com.fumbbl.ffb.dialog.DialogSelectSkillParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.bb2020.ReportPlayerEvent;
import com.fumbbl.ffb.report.bb2020.ReportPrayerWasted;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerDialog;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class IntensiveTrainingHandler extends DialogPrayerHandler {

	@Override
	Prayer handledPrayer() {
		return Prayer.INTENSIVE_TRAINING;
	}

	@Override
	protected void createDialog(List<Player<?>> players, GameState gameState, Team team) {
		Collections.shuffle(players);
		Player<?> player = players.get(0);
		List<SkillCategory> categories = Arrays.asList(player.getPosition().getSkillCategories(false));
		SkillFactory skillFactory = gameState.getGame().getFactory(FactoryType.Factory.SKILL);
		List<Skill> skills = skillFactory.getSkills().stream()
				.filter(skill -> categories.contains(skill.getCategory()))
				.sorted(Comparator.comparing(Skill::getName))
				.collect(Collectors.toList());
		if (!skills.isEmpty()) {
			UtilServerDialog.showDialog(gameState, new DialogSelectSkillParameter(player.getId(), skills, SkillChoiceMode.INTENSIVE_TRAINING), false);
		} else {
			reports.add(new ReportPrayerWasted(Prayer.INTENSIVE_TRAINING.getName()));
		}
	}

	@Override
	public void applySelection(Game game, PrayerDialogSelection selection) {
		Skill skill = selection.getSkill();
		game.getFieldModel().addIntensiveTrainingSkill(selection.getPlayerId(), skill);
		reports.add(new ReportPlayerEvent(selection.getPlayerId(), " gains " + skill.getName()));
	}

	@Override
	protected boolean handled(Game game) {
		return game.getDialogParameter() == null;
	}
}

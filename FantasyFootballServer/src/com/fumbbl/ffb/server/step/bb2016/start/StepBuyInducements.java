package com.fumbbl.ffb.server.step.bb2016.start;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogBuyInducementsParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandBuyInducements;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportDoubleHiredStarPlayer;
import com.fumbbl.ffb.report.bb2016.ReportInducementsBought;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.db.DbTransaction;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.game.start.UtilInducementSequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.Inducement.SequenceParams;
import com.fumbbl.ffb.server.step.generator.common.RiotousRookies;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.skill.bb2016.Loner;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Step in start game sequence to buy inducements.
 * <p>
 * Expects stepParameter INDUCEMENT_GOLD_AWAY to be set by a preceding step.
 * Expects stepParameter INDUCEMENT_GOLD_HOME to be set by a preceding step.
 * <p>
 * Pushes inducement sequence on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public final class StepBuyInducements extends AbstractStep {

	protected static final int MINIMUM_PETTY_CASH_FOR_INDUCEMENTS = 50000;

	private int fInducementGoldHome;
	private int fInducementGoldAway;

	private boolean fInducementsSelectedHome;
	private boolean fInducementsSelectedAway;

	private int fGoldUsedHome;
	private int fGoldUsedAway;

	private boolean fReportedHome;
	private boolean fReportedAway;

	public StepBuyInducements(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BUY_INDUCEMENTS;
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case INDUCEMENT_GOLD_AWAY:
					fInducementGoldAway = (pParameter.getValue() != null) ? (Integer) pParameter.getValue() : 0;
					return true;
				case INDUCEMENT_GOLD_HOME:
					fInducementGoldHome = (pParameter.getValue() != null) ? (Integer) pParameter.getValue() : 0;
					return true;
				default:
					break;
			}
		}
		return false;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			Game game = getGameState().getGame();
			switch (pReceivedCommand.getId()) {
				case CLIENT_BUY_INDUCEMENTS:
					ClientCommandBuyInducements buyInducementsCommand = (ClientCommandBuyInducements) pReceivedCommand.getCommand();
					if (game.getTeamHome().getId().equals(buyInducementsCommand.getTeamId())) {
						game.getTurnDataHome().getInducementSet().add(buyInducementsCommand.getInducementSet());
						addStarPlayers(game.getTeamHome(), buyInducementsCommand.getStarPlayerPositionIds());
						addMercenaries(game.getTeamHome(), buyInducementsCommand.getMercenaryPositionIds(),
							buyInducementsCommand.getMercenarySkills());
						fGoldUsedHome = fInducementGoldHome - buyInducementsCommand.getAvailableGold();
						fInducementsSelectedHome = true;
					} else {
						game.getTurnDataAway().getInducementSet().add(buyInducementsCommand.getInducementSet());
						addStarPlayers(game.getTeamAway(), buyInducementsCommand.getStarPlayerPositionIds());
						addMercenaries(game.getTeamAway(), buyInducementsCommand.getMercenaryPositionIds(),
							buyInducementsCommand.getMercenarySkills());
						fGoldUsedAway = fInducementGoldAway - buyInducementsCommand.getAvailableGold();
						fInducementsSelectedAway = true;
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				default:
					break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		if (getGameState() == null) {
			return;
		}
		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		int homeTV = gameResult.getTeamResultHome().getTeamValue();
		int awayTV = gameResult.getTeamResultAway().getTeamValue();
		if (UtilGameOption.isOptionEnabled(game, GameOptionId.INDUCEMENTS)) {
			if (UtilGameOption.isOptionEnabled(game, GameOptionId.USE_PREDEFINED_INDUCEMENTS)) {
				Optional<InducementType> starType = ((InducementTypeFactory) game.getFactory(FactoryType.Factory.INDUCEMENT_TYPE))
					.allTypes().stream().filter(type -> type.getUsage() == Usage.STAR).findFirst();
				if (starType.isPresent() && game.getTeamHome().getInducementSet() != null) {
					game.getTurnDataHome().getInducementSet().add(game.getTeamHome().getInducementSet());
					String[] starPlayerPositionIds = game.getTeamHome().getInducementSet().getStarPlayerPositionIds();
					if (ArrayTool.isProvided(starPlayerPositionIds)) {
						game.getTurnDataHome().getInducementSet()
							.addInducement(new Inducement(starType.get(), starPlayerPositionIds.length));
						addStarPlayers(game.getTeamHome(), starPlayerPositionIds);
					}
					fGoldUsedHome = fInducementGoldHome;
				}
				if (starType.isPresent() && game.getTeamAway().getInducementSet() != null) {
					game.getTurnDataAway().getInducementSet().add(game.getTeamAway().getInducementSet());
					String[] starPlayerPositionIds = game.getTeamAway().getInducementSet().getStarPlayerPositionIds();
					if (ArrayTool.isProvided(starPlayerPositionIds)) {
						game.getTurnDataAway().getInducementSet()
							.addInducement(new Inducement(starType.get(), starPlayerPositionIds.length));
						addStarPlayers(game.getTeamAway(), starPlayerPositionIds);
					}
					fGoldUsedAway = fInducementGoldAway;
				}
			} else {
				if (fInducementGoldHome < MINIMUM_PETTY_CASH_FOR_INDUCEMENTS) {
					fInducementsSelectedHome = true;
				}
				if (fInducementGoldAway < MINIMUM_PETTY_CASH_FOR_INDUCEMENTS) {
					fInducementsSelectedAway = true;
				}
			}
			if (fInducementsSelectedHome && !fReportedHome) {
				fReportedHome = true;
				getResult().addReport(generateReport(game.getTeamHome()));
			}
			if (fInducementsSelectedAway && !fReportedAway) {
				fReportedAway = true;
				getResult().addReport(generateReport(game.getTeamAway()));
			}

			if (!fInducementsSelectedHome && !fInducementsSelectedAway) {
				if (homeTV > awayTV) {
					UtilServerDialog.showDialog(getGameState(),
						new DialogBuyInducementsParameter(game.getTeamHome().getId(), fInducementGoldHome), false);
				} else {
					UtilServerDialog.showDialog(getGameState(),
						new DialogBuyInducementsParameter(game.getTeamAway().getId(), fInducementGoldAway), false);
				}
			} else if (!fInducementsSelectedHome) {
				UtilServerDialog.showDialog(getGameState(),
					new DialogBuyInducementsParameter(game.getTeamHome().getId(), fInducementGoldHome), false);
			} else if (!fInducementsSelectedAway) {
				UtilServerDialog.showDialog(getGameState(),
					new DialogBuyInducementsParameter(game.getTeamAway().getId(), fInducementGoldAway), false);
			} else {
				leaveStep(homeTV, awayTV);
			}
		} else {
			leaveStep(homeTV, awayTV);
		}
	}

	private ReportInducementsBought generateReport(Team pTeam) {
		Game game = getGameState().getGame();
		InducementSet inducementSet = (game.getTeamHome() == pTeam) ? game.getTurnDataHome().getInducementSet()
			: game.getTurnDataAway().getInducementSet();
		int nrOfInducements = 0, nrOfStars = 0, nrOfMercenaries = 0;
		for (Inducement inducement : inducementSet.getInducements()) {
			switch (inducement.getType().getUsage()) {
				case STAR:
					nrOfStars = inducement.getValue();
					break;
				case LONER:
					nrOfMercenaries = inducement.getValue();
					break;
				default:
					nrOfInducements += inducement.getValue();
					break;
			}
		}
		int gold = (game.getTeamHome() == pTeam) ? fGoldUsedHome : fGoldUsedAway;
		return new ReportInducementsBought(pTeam.getId(), nrOfInducements, nrOfStars, nrOfMercenaries, gold);
	}

	private void leaveStep(int pHomeTV, int pAwayTV) {
		SequenceGeneratorFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		com.fumbbl.ffb.server.step.generator.common.Inducement generator =
			((com.fumbbl.ffb.server.step.generator.common.Inducement) factory.forName(SequenceGenerator.Type.Inducement.name()));
		if (pHomeTV > pAwayTV) {
			generator.pushSequence(new SequenceParams(getGameState(),
				InducementPhase.AFTER_INDUCEMENTS_PURCHASED, true));
			generator.pushSequence(new SequenceParams(getGameState(),
				InducementPhase.AFTER_INDUCEMENTS_PURCHASED, false));
		} else {
			generator.pushSequence(new SequenceParams(getGameState(),
				InducementPhase.AFTER_INDUCEMENTS_PURCHASED, false));
			generator.pushSequence(new SequenceParams(getGameState(),
				InducementPhase.AFTER_INDUCEMENTS_PURCHASED, true));
		}
		((RiotousRookies) factory.forName(SequenceGenerator.Type.RiotousRookies.name()))
			.pushSequence(new SequenceGenerator.SequenceParams(getGameState()));
		Game game = getGameState().getGame();
		int restGoldHome = Math.max(0, fInducementGoldHome - fGoldUsedHome);
		int maxInducementGoldHome = UtilInducementSequence.calculateInducementGold(game, true);
		game.getGameResult().getTeamResultHome().setPettyCashUsed(Math.max(0, maxInducementGoldHome - restGoldHome));
		int restGoldAway = Math.max(0, fInducementGoldAway - fGoldUsedAway);
		int maxInducementGoldAway = UtilInducementSequence.calculateInducementGold(game, false);
		game.getGameResult().getTeamResultAway().setPettyCashUsed(Math.max(0, maxInducementGoldAway - restGoldAway));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void addMercenaries(Team pTeam, String[] pPositionIds, Skill[] pSkills) {

		if (!ArrayTool.isProvided(pPositionIds) || !ArrayTool.isProvided(pSkills)) {
			return;
		}

		Roster roster = pTeam.getRoster();
		Game game = getGameState().getGame();
		List<RosterPlayer> addedPlayerList = new ArrayList<>();
		Map<RosterPosition, Integer> nrByPosition = new HashMap<>();

		SkillFactory factory = game.getFactory(FactoryType.Factory.SKILL);
		for (int i = 0; i < pPositionIds.length; i++) {
			RosterPosition position = roster.getPositionById(pPositionIds[i]);
			RosterPlayer mercenary = new RosterPlayer();
			addedPlayerList.add(mercenary);
			mercenary.setId(pTeam.getId() + "M" + addedPlayerList.size());
			mercenary.updatePosition(position, game.getRules(), game.getId());
			Integer mercNr = nrByPosition.get(position);
			if (mercNr == null) {
				mercNr = 1;
			} else {
				mercNr = mercNr + 1;
			}
			nrByPosition.put(position, mercNr);
			mercenary.setName("Merc " + position.getName() + " " + mercNr);
			mercenary.setNr(pTeam.getMaxPlayerNr() + 1);
			mercenary.setType(PlayerType.MERCENARY);
			mercenary.addSkill(factory.forClass(Loner.class));
			if (pSkills[i] != null) {
				mercenary.addSkill(pSkills[i]);
			}
			pTeam.addPlayer(mercenary);
			game.getFieldModel().setPlayerState(mercenary, new PlayerState(PlayerState.RESERVE));
			UtilBox.putPlayerIntoBox(game, mercenary);
		}

		if (addedPlayerList.size() > 0) {
			RosterPlayer[] addedPlayers = addedPlayerList.toArray(new RosterPlayer[0]);
			UtilServerSteps.sendAddedPlayers(getGameState(), pTeam, addedPlayers);
		}

	}

	private void removeStarPlayerInducements(TurnData pTurnData, int pRemoved) {
		pTurnData.getInducementSet().getInducementMapping().entrySet().stream()
			.filter(entry -> entry.getKey().getUsage() == Usage.STAR).map(Map.Entry::getValue).findFirst()
			.ifPresent(starPlayerInducement -> {
				starPlayerInducement.setValue(starPlayerInducement.getValue() - pRemoved);
				if (starPlayerInducement.getValue() <= 0) {
					pTurnData.getInducementSet().removeInducement(starPlayerInducement);
				} else {
					pTurnData.getInducementSet().addInducement(starPlayerInducement);
				}
			});
	}

	private void addStarPlayers(Team pTeam, String[] pPositionIds) {
		if (ArrayTool.isProvided(pPositionIds)) {

			Roster roster = pTeam.getRoster();
			Game game = getGameState().getGame();
			FantasyFootballServer server = getGameState().getServer();

			Map<String, Player<?>> otherTeamStarPlayerByName = new HashMap<>();
			Team otherTeam = (game.getTeamHome() == pTeam) ? game.getTeamAway() : game.getTeamHome();
			for (Player<?> otherPlayer : otherTeam.getPlayers()) {
				if (otherPlayer.getPlayerType() == PlayerType.STAR) {
					otherTeamStarPlayerByName.put(otherPlayer.getName(), otherPlayer);
				}
			}

			List<RosterPlayer> addedPlayerList = new ArrayList<>();
			List<RosterPlayer> removedPlayerList = new ArrayList<>();
			for (int i = 0; i < pPositionIds.length; i++) {
				RosterPosition position = roster.getPositionById(pPositionIds[i]);
				Player<?> otherTeamStarPlayer = otherTeamStarPlayerByName.get(position.getName());
				if (!UtilGameOption.isOptionEnabled(game, GameOptionId.ALLOW_STAR_ON_BOTH_TEAMS)
					&& (otherTeamStarPlayer != null)) {
					if (otherTeamStarPlayer instanceof RosterPlayer) {
						removedPlayerList.add((RosterPlayer) otherTeamStarPlayer);
					}
				} else {
					RosterPlayer starPlayer = new RosterPlayer();
					addedPlayerList.add(starPlayer);
					StringBuilder playerId = new StringBuilder().append(pTeam.getId()).append("S").append(addedPlayerList.size());
					starPlayer.setId(playerId.toString());
					starPlayer.updatePosition(position, game.getRules(), game.getId());
					starPlayer.setName(position.getName());
					starPlayer.setNr(pTeam.getMaxPlayerNr() + 1);
					starPlayer.setGender(position.getGender());
					pTeam.addPlayer(starPlayer);
					game.getFieldModel().setPlayerState(starPlayer, new PlayerState(PlayerState.RESERVE));
					UtilBox.putPlayerIntoBox(game, starPlayer);
				}
			}

			if (removedPlayerList.size() > 0) {
				removeStarPlayerInducements(game.getTurnDataHome(), removedPlayerList.size());
				removeStarPlayerInducements(game.getTurnDataAway(), removedPlayerList.size());
				DbTransaction transaction = new DbTransaction();
				for (Player<?> player : removedPlayerList) {
					server.getCommunication().sendRemovePlayer(getGameState(), player.getId());
					getResult().addReport(new ReportDoubleHiredStarPlayer(player.getName()));
				}
				server.getDbUpdater().add(transaction);
			}

			if (addedPlayerList.size() > 0) {
				RosterPlayer[] addedPlayers = addedPlayerList.toArray(new RosterPlayer[0]);
				UtilServerSteps.sendAddedPlayers(getGameState(), pTeam, addedPlayers);
				// TODO: update persistence?
			}

		}

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.INDUCEMENT_GOLD_AWAY.addTo(jsonObject, fInducementGoldAway);
		IServerJsonOption.INDUCEMENT_GOLD_HOME.addTo(jsonObject, fInducementGoldHome);
		IServerJsonOption.INDUCEMENTS_SELECTED_AWAY.addTo(jsonObject, fInducementsSelectedAway);
		IServerJsonOption.INDUCEMENTS_SELECTED_HOME.addTo(jsonObject, fInducementsSelectedHome);
		IServerJsonOption.GOLD_USED_AWAY.addTo(jsonObject, fGoldUsedAway);
		IServerJsonOption.GOLD_USED_HOME.addTo(jsonObject, fGoldUsedHome);
		IServerJsonOption.REPORTED_AWAY.addTo(jsonObject, fReportedAway);
		IServerJsonOption.REPORTED_HOME.addTo(jsonObject, fReportedHome);
		return jsonObject;
	}

	@Override
	public StepBuyInducements initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fInducementGoldAway = IServerJsonOption.INDUCEMENT_GOLD_AWAY.getFrom(game, jsonObject);
		fInducementGoldHome = IServerJsonOption.INDUCEMENT_GOLD_HOME.getFrom(game, jsonObject);
		fInducementsSelectedAway = IServerJsonOption.INDUCEMENTS_SELECTED_AWAY.getFrom(game, jsonObject);
		fInducementsSelectedHome = IServerJsonOption.INDUCEMENTS_SELECTED_HOME.getFrom(game, jsonObject);
		fGoldUsedAway = IServerJsonOption.GOLD_USED_AWAY.getFrom(game, jsonObject);
		fGoldUsedHome = IServerJsonOption.GOLD_USED_HOME.getFrom(game, jsonObject);
		fReportedAway = IServerJsonOption.REPORTED_AWAY.getFrom(game, jsonObject);
		fReportedHome = IServerJsonOption.REPORTED_HOME.getFrom(game, jsonObject);
		return this;
	}

}

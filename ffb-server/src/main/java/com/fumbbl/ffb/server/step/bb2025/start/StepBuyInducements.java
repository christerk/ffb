package com.fumbbl.ffb.server.step.bb2025.start;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogBuyPrayersAndInducementsParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.BriberyAndCorruptionAction;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.SpecialRule;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandBuyInducements;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportDoubleHiredStarPlayer;
import com.fumbbl.ffb.report.bb2025.ReportPrayersAndInducementsBought;
import com.fumbbl.ffb.report.mixed.ReportBriberyAndCorruptionReRoll;
import com.fumbbl.ffb.report.mixed.ReportDoubleHiredStaff;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.Kickoff;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.RiotousRookies;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.skill.mixed.Loner;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Step in start game sequence to buy cards.
 * <p>
 * Sets stepParameter INDUCEMENT_GOLD_AWAY for all steps on the stack. Sets
 * stepParameter INDUCEMENT_GOLD_HOME for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepBuyInducements extends AbstractStep {

	public static final int MAX_UNDERDOG_ALLOWANCE = 50000;
	private Integer availableInducementGoldHome;
	private Integer availableInducementGoldAway;
	private Integer usedInducementGoldHome = 0;
	private Integer usedInducementGoldAway = 0;
	private transient int pettyCash, treasury;
	private boolean parallel;
	private Phase phase = Phase.INIT;
	private int prayersBoughtHome, prayersBoughtAway;

	private final List<ClientCommandBuyInducements> buyInducementCommands = new ArrayList<>();

	public StepBuyInducements(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BUY_INDUCEMENTS;
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
			switch (pReceivedCommand.getId()) {
				case CLIENT_BUY_INDUCEMENTS:
					ClientCommandBuyInducements command = (ClientCommandBuyInducements) pReceivedCommand.getCommand();
					if (parallel) {
						buyInducementCommands.add(command);
					} else {
						Game game = getGameState().getGame();
						handleBuyInducements(game, command);

						Team team = game.getTeamById(command.getTeamId());
						if (team == game.getTeamHome()) {
							int newTvHome = getNewTv(usedInducementGoldHome, team);
							getResult().addReport(generateReport(team, usedInducementGoldHome, newTvHome));
						} else {
							int newTvAway = getNewTv(usedInducementGoldAway, team);
							getResult().addReport(generateReport(team, usedInducementGoldAway, newTvAway));
						}
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

	private void handleBuyInducements(Game game, ClientCommandBuyInducements buyInducementsCommand) {
		if (game.getTeamHome().getId().equals(buyInducementsCommand.getTeamId())) {
			usedInducementGoldHome = handleTeamInducements(game.getTurnDataHome(), game.getTeamHome(), buyInducementsCommand,
				availableInducementGoldHome);
		} else {
			usedInducementGoldAway = handleTeamInducements(game.getTurnDataAway(), game.getTeamAway(), buyInducementsCommand,
				availableInducementGoldAway);
		}
	}

	private int handleTeamInducements(TurnData turnData, Team team, ClientCommandBuyInducements buyInducementsCommand,
																		int availableInducementGold) {

		InducementSet inducementSet = buyInducementsCommand.getInducementSet();
		int inducementCost = inducementCosts(team, inducementSet);

		InducementTypeFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE);
		factory.allTypes().stream().filter(type -> type.getUsages().contains(Usage.GAME_MODIFICATION)).findFirst()
			.ifPresent(type -> {
				if (inducementSet.getInducementTypes().contains(type)) {
					Inducement prayers = inducementSet.get(type);
					inducementSet.removeInducement(prayers);
					if (team == getGameState().getGame().getTeamHome()) {
						prayersBoughtHome = prayers.getValue();
					} else {
						prayersBoughtAway = prayers.getValue();
					}
				}
			}
		);

		turnData.getInducementSet().add(inducementSet);
		int starCost = addStarPlayers(team, buyInducementsCommand.getStarPlayerPositionIds());
		int staffCost = addStaff(team, buyInducementsCommand.getStaffPositionIds());
		int mercCost =
			addMercenaries(team, buyInducementsCommand.getMercenaryPositionIds(),
				buyInducementsCommand.getMercenarySkills());
		int usedInducementGold = starCost + mercCost + inducementCost + staffCost;
		if (usedInducementGold > availableInducementGold) {
			throw new FantasyFootballException(
				"Team " + team.getName() + " with id " + team.getId() + " spent more gold than should be available, spent " +
					(usedInducementGold) + " vs available " + (availableInducementGold));
		}
		return usedInducementGold;
	}

	private void executeStep() {
		Game game = getGameState().getGame();

		switch (phase) {
			case INIT:
				init(game);
				break;
			case HOME:
			case AWAY:
				swapTeam();
				break;
			default:
				break;
		}

		if (phase == Phase.DONE) {
			leaveStep();
		}
	}

	private void init(Game game) {
		if (!UtilGameOption.isOptionEnabled(game, GameOptionId.INDUCEMENTS)) {
			phase = Phase.DONE;
		} else if (UtilGameOption.isOptionEnabled(game, GameOptionId.USE_PREDEFINED_INDUCEMENTS)) {
			Optional<InducementType> starType =
				((InducementTypeFactory) game.getFactory(FactoryType.Factory.INDUCEMENT_TYPE)).allTypes().stream()
					.filter(type -> type.hasUsage(Usage.STAR)).findFirst();
			if (starType.isPresent() && game.getTeamHome().getInducementSet() != null) {
				game.getTurnDataHome().getInducementSet().add(game.getTeamHome().getInducementSet());
				String[] starPlayerPositionIds = game.getTeamHome().getInducementSet().getStarPlayerPositionIds();
				if (ArrayTool.isProvided(starPlayerPositionIds)) {
					game.getTurnDataHome().getInducementSet()
						.addInducement(new Inducement(starType.get(), starPlayerPositionIds.length));
					addStarPlayers(game.getTeamHome(), starPlayerPositionIds);
				}
				usedInducementGoldHome = availableInducementGoldHome;
			}
			if (starType.isPresent() && game.getTeamAway().getInducementSet() != null) {
				game.getTurnDataAway().getInducementSet().add(game.getTeamAway().getInducementSet());
				String[] starPlayerPositionIds = game.getTeamAway().getInducementSet().getStarPlayerPositionIds();
				if (ArrayTool.isProvided(starPlayerPositionIds)) {
					game.getTurnDataAway().getInducementSet()
						.addInducement(new Inducement(starType.get(), starPlayerPositionIds.length));
					addStarPlayers(game.getTeamAway(), starPlayerPositionIds);
				}
				usedInducementGoldAway = availableInducementGoldAway;
			}
			phase = Phase.DONE;

		} else {

			int freeCash = UtilGameOption.getIntOption(game, GameOptionId.FREE_INDUCEMENT_CASH);

			Team overDog;

			boolean allowInducementsOnEvenCTV =
				UtilGameOption.isOptionEnabled(game, GameOptionId.INDUCEMENTS_ALLOW_SPENDING_TREASURY_ON_EQUAL_CTV) ||
					freeCash > 0;
			boolean sameTv = false;

			if (game.getGameResult().getTeamResultHome().getPettyCashFromTvDiff() > 0) {
				overDog = game.getTeamAway();
				phase = Phase.AWAY;
			} else if (game.getGameResult().getTeamResultAway().getPettyCashFromTvDiff() > 0) {
				overDog = game.getTeamHome();
				phase = Phase.HOME;
			} else if (allowInducementsOnEvenCTV) {
				overDog = game.getTeamHome();
				phase = Phase.HOME;
				parallel = true;
				sameTv = true;
			} else {
				phase = Phase.DONE;
				availableInducementGoldHome = 0;
				availableInducementGoldAway = 0;
				return;
			}

			boolean allowOverdogSpending =
				UtilGameOption.isOptionEnabled(game, GameOptionId.INDUCEMENTS_ALLOW_OVERDOG_SPENDING);

			if (!showDialog(overDog, freeCash, true, allowOverdogSpending || sameTv)) {
				swapTeam();
			}
		}
	}

	private int getAvailableGold(int freeCash, boolean useUnlimitedTreasury, boolean allowSpending) {
		Game game = getGameState().getGame();
		int availableGold;
		if (phase == Phase.HOME) {
			if (allowSpending) {
				if (useUnlimitedTreasury) {
					availableInducementGoldHome = treasury = freeCash + game.getTeamHome().getTreasury();
					pettyCash = 0;
				} else {
					pettyCash = Math.max(usedInducementGoldAway - freeCash, 0) +
						game.getGameResult().getTeamResultHome().getPettyCashFromTvDiff() + freeCash;
					treasury = Math.min(MAX_UNDERDOG_ALLOWANCE, game.getTeamHome().getTreasury());
					availableInducementGoldHome = pettyCash + treasury;
				}
			} else {
				availableInducementGoldHome = 0;
				pettyCash = 0;
				treasury = 0;
			}
			availableGold = availableInducementGoldHome;
		} else {
			if (allowSpending) {
				if (useUnlimitedTreasury) {
					availableInducementGoldAway = treasury = freeCash + game.getTeamAway().getTreasury();
					pettyCash = 0;
				} else {
					pettyCash = Math.max(usedInducementGoldHome - freeCash, 0) +
						game.getGameResult().getTeamResultAway().getPettyCashFromTvDiff() + freeCash;
					treasury = Math.min(MAX_UNDERDOG_ALLOWANCE, game.getTeamAway().getTreasury());
					availableInducementGoldAway = pettyCash + treasury;
				}
			} else {
				availableInducementGoldAway = 0;
				pettyCash = 0;
				treasury = 0;
			}
			availableGold = availableInducementGoldAway;
		}
		return availableGold;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean showDialog(Team team, int freeCash, boolean usesUnlimitedTreasury, boolean allowSpending) {
		int availableGold = getAvailableGold(freeCash, usesUnlimitedTreasury, allowSpending);

		boolean canBuyInducements = minimumInducementCost(team) <= availableGold;

		if (canBuyInducements) {
			UtilServerDialog.showDialog(getGameState(),
				new DialogBuyPrayersAndInducementsParameter(team.getId(), availableGold, usesUnlimitedTreasury, pettyCash,
					treasury), false);
			return true;
		}

		return false;
	}

	private void swapTeam() {
		Game game = getGameState().getGame();

		Team team;

		if (phase == Phase.HOME && availableInducementGoldAway == null) {
			phase = Phase.AWAY;
			team = game.getTeamAway();
		} else if (phase == Phase.AWAY && availableInducementGoldHome == null) {
			phase = Phase.HOME;
			team = game.getTeamHome();
		} else {
			phase = Phase.DONE;
			return;
		}

		int freeCash = UtilGameOption.getIntOption(game, GameOptionId.FREE_INDUCEMENT_CASH);

		if (!showDialog(team, freeCash,
			parallel || UtilGameOption.isOptionEnabled(game, GameOptionId.INDUCEMENTS_ALWAYS_USE_TREASURY), true)) {
			phase = Phase.DONE;
		}
	}

	private int minimumInducementCost(Team team) {
		Roster roster = team.getRoster();
		InducementTypeFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE);
		return Stream.concat(Stream.concat(
					Arrays.stream(roster.getPositions()).filter(pos -> pos.getType() == PlayerType.STAR).map(RosterPosition::getCost)
						.filter(i -> i > 0),
					factory.allTypes().stream().filter(type -> type.getActualCostId(team) != null && !type.getName().equals("card"))
						.map(type -> UtilGameOption.getIntOption(getGameState().getGame(), type.getActualCostId(team)))),
				Arrays.stream(roster.getPositions()).filter(pos -> pos.getType() == PlayerType.MERCENARY).map(
					pos -> pos.getCost() +
						UtilGameOption.getIntOption(getGameState().getGame(), GameOptionId.INDUCEMENT_MERCENARIES_EXTRA_COST)))
			.min(Integer::compareTo).orElse(Integer.MAX_VALUE);
	}

	private int addMercenaries(Team pTeam, String[] pPositionIds, Skill[] pSkills) {
		int sum = 0;

		if (!ArrayTool.isProvided(pPositionIds) || !ArrayTool.isProvided(pSkills)) {
			return sum;
		}

		Roster roster = pTeam.getRoster();
		Game game = getGameState().getGame();
		List<RosterPlayer> addedPlayerList = new ArrayList<>();
		Map<RosterPosition, Integer> nrByPosition = new HashMap<>();

		int extraCost = UtilGameOption.getIntOption(game, GameOptionId.INDUCEMENT_MERCENARIES_EXTRA_COST);
		int skillCost = UtilGameOption.getIntOption(game, GameOptionId.INDUCEMENT_MERCENARIES_SKILL_COST);

		SkillFactory factory = game.getFactory(FactoryType.Factory.SKILL);
		for (int i = 0; i < pPositionIds.length; i++) {
			RosterPosition position = roster.getPositionById(pPositionIds[i]);
			RosterPlayer mercenary = new RosterPlayer();
			sum += position.getCost() + extraCost;
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
				sum += skillCost;
				mercenary.addSkill(pSkills[i]);
			}
			pTeam.addPlayer(mercenary);
			game.getFieldModel().setPlayerState(mercenary, new PlayerState(PlayerState.RESERVE));
			UtilBox.putPlayerIntoBox(game, mercenary);
		}

		if (!addedPlayerList.isEmpty()) {
			RosterPlayer[] addedPlayers = addedPlayerList.toArray(new RosterPlayer[0]);
			UtilServerSteps.sendAddedPlayers(getGameState(), pTeam, addedPlayers);
		}
		return sum;
	}

	private void removeDuplicatePlayerInducements(TurnData pTurnData, int pRemoved, Usage usage) {
		pTurnData.getInducementSet().getInducementMapping().entrySet().stream()
			.filter(entry -> entry.getKey().hasUsage(usage)).map(Map.Entry::getValue).findFirst().ifPresent(inducement -> {
				inducement.setValue(inducement.getValue() - pRemoved);
				if (inducement.getValue() <= 0) {
					pTurnData.getInducementSet().removeInducement(inducement);
				} else {
					pTurnData.getInducementSet().addInducement(inducement);
				}
			});
	}

	private int addStarPlayers(Team pTeam, String[] pPositionIds) {
		int sum = 0;
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
			for (String pPositionId : pPositionIds) {
				RosterPosition position = roster.getPositionById(pPositionId);
				sum += position.getCost();
				Player<?> otherTeamStarPlayer = otherTeamStarPlayerByName.get(position.getName());
				if (!UtilGameOption.isOptionEnabled(game, GameOptionId.ALLOW_STAR_ON_BOTH_TEAMS) &&
					(otherTeamStarPlayer != null)) {
					if (otherTeamStarPlayer instanceof RosterPlayer) {
						removedPlayerList.add((RosterPlayer) otherTeamStarPlayer);
					}
				} else {
					RosterPlayer starPlayer = new RosterPlayer();
					addedPlayerList.add(starPlayer);
					starPlayer.setId(pTeam.getId() + "S" + addedPlayerList.size());
					starPlayer.updatePosition(position, game.getRules(), game.getId());
					starPlayer.setName(position.getName());
					starPlayer.setNr(pTeam.getMaxPlayerNr() + 1);
					starPlayer.setGender(position.getGender());
					pTeam.addPlayer(starPlayer);
					game.getFieldModel().setPlayerState(starPlayer, new PlayerState(PlayerState.RESERVE));
					UtilBox.putPlayerIntoBox(game, starPlayer);
				}
			}

			if (!removedPlayerList.isEmpty()) {
				removeDuplicatePlayerInducements(game.getTurnDataHome(), removedPlayerList.size(), Usage.STAR);
				removeDuplicatePlayerInducements(game.getTurnDataAway(), removedPlayerList.size(), Usage.STAR);
				for (Player<?> player : removedPlayerList) {
					server.getCommunication().sendRemovePlayer(getGameState(), player.getId());
					otherTeam.removePlayer(player);
					game.getFieldModel().remove(player);
					getResult().addReport(new ReportDoubleHiredStarPlayer(player.getName()));
				}
			}

			if (!addedPlayerList.isEmpty()) {
				RosterPlayer[] addedPlayers = addedPlayerList.toArray(new RosterPlayer[0]);
				UtilServerSteps.sendAddedPlayers(getGameState(), pTeam, addedPlayers);
			}

		}

		return sum;

	}

	private int addStaff(Team pTeam, List<String> pPositionIds) {
		int sum = 0;
		if (!pPositionIds.isEmpty()) {

			Roster roster = pTeam.getRoster();
			Game game = getGameState().getGame();
			FantasyFootballServer server = getGameState().getServer();

			Map<String, Player<?>> otherTeamStaffByName = new HashMap<>();
			Team otherTeam = (game.getTeamHome() == pTeam) ? game.getTeamAway() : game.getTeamHome();
			for (Player<?> otherPlayer : otherTeam.getPlayers()) {
				if (otherPlayer.getPlayerType() == PlayerType.INFAMOUS_STAFF) {
					otherTeamStaffByName.put(otherPlayer.getName(), otherPlayer);
				}
			}

			List<RosterPlayer> addedPlayerList = new ArrayList<>();
			List<RosterPlayer> removedPlayerList = new ArrayList<>();
			for (String pPositionId : pPositionIds) {
				RosterPosition position = roster.getPositionById(pPositionId);
				sum += position.getCost();
				Player<?> otherTeamStaff = otherTeamStaffByName.get(position.getName());
				if (!UtilGameOption.isOptionEnabled(game, GameOptionId.ALLOW_STAFF_ON_BOTH_TEAMS) && (otherTeamStaff != null)) {
					if (otherTeamStaff instanceof RosterPlayer) {
						removedPlayerList.add((RosterPlayer) otherTeamStaff);
					}
				} else {
					RosterPlayer staff = new RosterPlayer();
					addedPlayerList.add(staff);
					staff.setId(pTeam.getId() + "I" + addedPlayerList.size());
					staff.updatePosition(position, game.getRules(), game.getId());
					staff.setName(position.getName());
					staff.setNr(pTeam.getMaxPlayerNr() + 1);
					staff.setGender(position.getGender());
					pTeam.addPlayer(staff);
					game.getFieldModel().setPlayerState(staff, new PlayerState(PlayerState.RESERVE));
					UtilBox.putPlayerIntoBox(game, staff);
				}
			}

			if (!removedPlayerList.isEmpty()) {
				removeDuplicatePlayerInducements(game.getTurnDataHome(), removedPlayerList.size(), Usage.STAFF);
				removeDuplicatePlayerInducements(game.getTurnDataAway(), removedPlayerList.size(), Usage.STAFF);
				for (Player<?> player : removedPlayerList) {
					server.getCommunication().sendRemovePlayer(getGameState(), player.getId());
					otherTeam.removePlayer(player);
					game.getFieldModel().remove(player);
					getResult().addReport(new ReportDoubleHiredStaff(player.getName()));
				}
			}

			if (!addedPlayerList.isEmpty()) {
				RosterPlayer[] addedPlayers = addedPlayerList.toArray(new RosterPlayer[0]);
				UtilServerSteps.sendAddedPlayers(getGameState(), pTeam, addedPlayers);
			}

		}

		return sum;

	}

	private int inducementCosts(Team team, InducementSet inducementSet) {
		Game game = getGameState().getGame();
		return Arrays.stream(inducementSet.getInducements())
			.filter(inducement -> inducement.getType().getActualCostId(team) != null).mapToInt(
				inducement -> inducement.getValue() *
					UtilGameOption.getIntOption(game, inducement.getType().getActualCostId(team))).sum();
	}

	private void leaveStep() {
		Game game = getGameState().getGame();

		buyInducementCommands.forEach(command -> handleBuyInducements(game, command));

		Team teamHome = getGameState().getGame().getTeamHome();
		int newTvHome = getNewTv(usedInducementGoldHome, teamHome);

		Team teamAway = getGameState().getGame().getTeamAway();
		int newTvAway = getNewTv(usedInducementGoldAway, teamAway);

		if (parallel) {
			getResult().addReport(generateReport(teamHome, usedInducementGoldHome, newTvHome));
			getResult().addReport(generateReport(teamAway, usedInducementGoldAway, newTvAway));
		}

		SequenceGeneratorFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);

		((Kickoff) factory.forName(SequenceGenerator.Type.Kickoff.name())).pushSequence(
			new Kickoff.SequenceParams(getGameState(), true));

		com.fumbbl.ffb.server.step.generator.common.Inducement generator =
			((com.fumbbl.ffb.server.step.generator.common.Inducement) factory.forName(
				SequenceGenerator.Type.Inducement.name()));
		if (newTvHome > newTvAway) {
			generator.pushSequence(new com.fumbbl.ffb.server.step.generator.common.Inducement.SequenceParams(getGameState(),
				InducementPhase.AFTER_INDUCEMENTS_PURCHASED, true));
			generator.pushSequence(new com.fumbbl.ffb.server.step.generator.common.Inducement.SequenceParams(getGameState(),
				InducementPhase.AFTER_INDUCEMENTS_PURCHASED, false));
		} else {
			generator.pushSequence(new com.fumbbl.ffb.server.step.generator.common.Inducement.SequenceParams(getGameState(),
				InducementPhase.AFTER_INDUCEMENTS_PURCHASED, false));
			generator.pushSequence(new com.fumbbl.ffb.server.step.generator.common.Inducement.SequenceParams(getGameState(),
				InducementPhase.AFTER_INDUCEMENTS_PURCHASED, true));
		}
		((RiotousRookies) factory.forName(SequenceGenerator.Type.RiotousRookies.name())).pushSequence(
			new SequenceGenerator.SequenceParams(getGameState()));

		Sequence prayerSequence = new Sequence(getGameState());
		prayerSequence.add(StepId.PRAYERS);
		getGameState().getStepStack().push(prayerSequence.getSequence());
		publishParameter(StepParameter.from(StepParameterKey.TV_HOME, newTvHome));
		publishParameter(StepParameter.from(StepParameterKey.TV_AWAY, newTvAway));
		publishParameter(StepParameter.from(StepParameterKey.PRAYERS_BOUGHT_HOME, prayersBoughtHome));
		publishParameter(StepParameter.from(StepParameterKey.PRAYERS_BOUGHT_AWAY, prayersBoughtAway));

		boolean alwaysUseTreasury = UtilGameOption.isOptionEnabled(game, GameOptionId.INDUCEMENTS_ALWAYS_USE_TREASURY);

		int freeCash = UtilGameOption.getIntOption(game, GameOptionId.FREE_INDUCEMENT_CASH);

		TeamResult teamResultHome = game.getGameResult().getTeamResultHome();
		if (teamResultHome.getPettyCashFromTvDiff() == 0 || alwaysUseTreasury) {
			teamResultHome.setTreasurySpentOnInducements(Math.max(0, usedInducementGoldHome - freeCash));
		} else {
			setUnderDogCashValues(teamResultHome, usedInducementGoldHome, availableInducementGoldHome, freeCash);
		}

		TeamResult teamResultAway = game.getGameResult().getTeamResultAway();
		if (teamResultAway.getPettyCashFromTvDiff() == 0 || alwaysUseTreasury) {
			teamResultAway.setTreasurySpentOnInducements(Math.max(0, usedInducementGoldAway - freeCash));
		} else {
			setUnderDogCashValues(teamResultAway, usedInducementGoldAway, availableInducementGoldAway, freeCash);
		}

		InducementTypeFactory inducementTypeFactory = game.getFactory(FactoryType.Factory.INDUCEMENT_TYPE);

		inducementTypeFactory.allTypes().stream().filter(type -> type.hasUsage(Usage.REROLL_ARGUE)).findFirst()
			.ifPresent(inducementType -> {

				if (teamHome.getSpecialRules().contains(SpecialRule.BRIBERY_AND_CORRUPTION)) {
					game.getTurnDataHome().getInducementSet().addInducement(new Inducement(inducementType, 1));
					getResult().addReport(
						new ReportBriberyAndCorruptionReRoll(teamHome.getId(), BriberyAndCorruptionAction.ADDED));
				}

				if (teamAway.getSpecialRules().contains(SpecialRule.BRIBERY_AND_CORRUPTION)) {
					game.getTurnDataAway().getInducementSet().addInducement(new Inducement(inducementType, 1));
					getResult().addReport(
						new ReportBriberyAndCorruptionReRoll(teamAway.getId(), BriberyAndCorruptionAction.ADDED));
				}

			});

		inducementTypeFactory.allTypes().stream().filter(type -> type.hasUsage(Usage.REROLL_ONES_ON_KOS)).findFirst()
			.ifPresent(inducementType -> {

				if (Arrays.stream(game.getTeamHome().getPlayers())
					.anyMatch(player -> player.hasSkillProperty(NamedProperties.canReRollOnesOnKORecovery))) {
					game.getTurnDataHome().getInducementSet().addInducement(new Inducement(inducementType, 1));
				}

				if (Arrays.stream(game.getTeamAway().getPlayers())
					.anyMatch(player -> player.hasSkillProperty(NamedProperties.canReRollOnesOnKORecovery))) {
					game.getTurnDataAway().getInducementSet().addInducement(new Inducement(inducementType, 1));
				}

			});
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private int getNewTv(Integer usedInducementGoldHome, Team teamHome) {
		return teamHome.getTeamValue() + usedInducementGoldHome;
	}

	private void setUnderDogCashValues(TeamResult teamResult, int usedInducementGold, int availableInducementGold,
																		 int freeCash) {
		int unspent = availableInducementGold - usedInducementGold;
		int unspentAllowance = Math.min(unspent, MAX_UNDERDOG_ALLOWANCE);
		int treasurySpentOnInducements = MAX_UNDERDOG_ALLOWANCE - unspentAllowance;
		teamResult.setTreasurySpentOnInducements(treasurySpentOnInducements);

		int usedPettyCash = usedInducementGold - treasurySpentOnInducements - freeCash;

		teamResult.setPettyCashUsed(Math.min(usedPettyCash, availableInducementGold - freeCash));


	}

	private ReportPrayersAndInducementsBought generateReport(Team pTeam, int gold, int newTv) {
		Game game = getGameState().getGame();
		InducementSet inducementSet = (game.getTeamHome() == pTeam) ? game.getTurnDataHome().getInducementSet() :
			game.getTurnDataAway().getInducementSet();
		int nrOfInducements = 0, nrOfStars = 0, nrOfMercenaries = 0;
		for (Inducement inducement : inducementSet.getInducements()) {
			Set<Usage> usages = inducement.getType().getUsages();
			if (usages.contains(Usage.STAR)) {
				nrOfStars = inducement.getValue();
			} else if (usages.contains(Usage.LONER)) {
				nrOfMercenaries = inducement.getValue();
			} else {
				nrOfInducements += inducement.getValue();
			}
		}
		return new ReportPrayersAndInducementsBought(pTeam.getId(), nrOfInducements, nrOfStars, nrOfMercenaries, gold,
			newTv);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		if (availableInducementGoldAway != null) {
			IServerJsonOption.INDUCEMENT_GOLD_AWAY.addTo(jsonObject, availableInducementGoldAway);
		}
		if (availableInducementGoldHome != null) {
			IServerJsonOption.INDUCEMENT_GOLD_HOME.addTo(jsonObject, availableInducementGoldHome);
		}

		JsonArray commandArray = new JsonArray();

		buyInducementCommands.stream().map(ClientCommandBuyInducements::toJsonValue).forEach(commandArray::add);

		IServerJsonOption.INDUCEMENT_COMMANDS.addTo(jsonObject, commandArray);

		IServerJsonOption.STEP_PHASE.addTo(jsonObject, phase.name());
		IServerJsonOption.INDUCEMENTS_SELECTED_PARALLEL.addTo(jsonObject, parallel);
		return jsonObject;
	}

	@Override
	public StepBuyInducements initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		availableInducementGoldAway = IServerJsonOption.INDUCEMENT_GOLD_AWAY.getFrom(source, jsonObject);
		availableInducementGoldHome = IServerJsonOption.INDUCEMENT_GOLD_HOME.getFrom(source, jsonObject);

		JsonArray commandArray = IServerJsonOption.INDUCEMENT_COMMANDS.getFrom(source, jsonObject);

		if (commandArray != null) {
			commandArray.values().stream().map(command -> new ClientCommandBuyInducements().initFrom(source, command))
				.forEach(buyInducementCommands::add);
		}

		phase = Phase.valueOf(IServerJsonOption.STEP_PHASE.getFrom(source, jsonObject));

		parallel = toPrimitive(IServerJsonOption.INDUCEMENTS_SELECTED_PARALLEL.getFrom(source, jsonObject));

		return this;
	}

	private enum Phase {
		INIT, HOME, AWAY, DONE
	}

}

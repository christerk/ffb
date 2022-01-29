package com.fumbbl.ffb.server.step.bb2020.start;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogBuyCardsAndInducementsParameter;
import com.fumbbl.ffb.factory.CardFactory;
import com.fumbbl.ffb.factory.CardTypeFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardChoice;
import com.fumbbl.ffb.inducement.CardChoices;
import com.fumbbl.ffb.inducement.CardType;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.inducement.bb2020.BriberyAndCorruptionAction;
import com.fumbbl.ffb.json.IJsonOption;
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
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandBuyInducements;
import com.fumbbl.ffb.net.commands.ClientCommandSelectCardToBuy;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportDoubleHiredStarPlayer;
import com.fumbbl.ffb.report.bb2020.ReportBriberyAndCorruptionReRoll;
import com.fumbbl.ffb.report.bb2020.ReportCardsAndInducementsBought;
import com.fumbbl.ffb.server.CardDeck;
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
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.Kickoff;
import com.fumbbl.ffb.server.step.generator.common.RiotousRookies;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.skill.bb2020.Loner;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Step in start game sequence to buy cards.
 * <p>
 * Sets stepParameter INDUCEMENT_GOLD_AWAY for all steps on the stack. Sets
 * stepParameter INDUCEMENT_GOLD_HOME for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepBuyCardsAndInducements extends AbstractStep {

	private Integer availableInducementGoldHome;
	private Integer availableInducementGoldAway;
	private Integer usedInducementGoldHome = 0;
	private Integer usedInducementGoldAway = 0;

	private boolean fCardsSelectedHome;
	private boolean fCardsSelectedAway;

	private boolean fReportedHome;
	private boolean fReportedAway;
	private CardChoices cardChoices = new CardChoices();
	private List<Card> usedCards = new ArrayList<>();
	private ClientCommandSelectCardToBuy.Selection currentSelection;
	private Phase phase = Phase.INIT;

	private final List<ClientCommandBuyInducements> buyInducementCommands = new ArrayList<>();

	private final transient Map<CardType, CardDeck> fDeckByType;

	public StepBuyCardsAndInducements(GameState pGameState) {
		super(pGameState);
		fDeckByType = new HashMap<>();
	}

	public StepId getId() {
		return StepId.BUY_CARDS_AND_INDUCEMENTS;
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
				case CLIENT_SELECT_CARD_TO_BUY:

					ClientCommandSelectCardToBuy buyCardCommand = (ClientCommandSelectCardToBuy) pReceivedCommand.getCommand();
					currentSelection = buyCardCommand.getSelection();
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_BUY_INDUCEMENTS:
					buyInducementCommands.add((ClientCommandBuyInducements) pReceivedCommand.getCommand());
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				default:
					// Ignore other commands. This removes warnings.
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
			game.getTurnDataHome().getInducementSet().add(buyInducementsCommand.getInducementSet());
			int starCost = addStarPlayers(game.getTeamHome(), buyInducementsCommand.getStarPlayerPositionIds());
			int mercCost = addMercenaries(game.getTeamHome(), buyInducementsCommand.getMercenaryPositionIds(),
				buyInducementsCommand.getMercenarySkills());
			int inducementCost = inducementCosts(game.getTeamHome(), buyInducementsCommand.getInducementSet());
			usedInducementGoldHome = starCost + mercCost + inducementCost;
			if (usedInducementGoldHome > availableInducementGoldHome) {
				int cardCost = cardCost(game.getTurnDataHome().getInducementSet());
				throw new FantasyFootballException("Team " + game.getTeamHome().getName() + " with id "
					+ game.getTeamHome().getId() + " spent more gold than should be available, spent "
					+ (usedInducementGoldHome + cardCost) + " vs available " + (availableInducementGoldHome + cardCost));
			}
		} else {
			game.getTurnDataAway().getInducementSet().add(buyInducementsCommand.getInducementSet());
			int starCost = addStarPlayers(game.getTeamAway(), buyInducementsCommand.getStarPlayerPositionIds());
			int mercCost = addMercenaries(game.getTeamAway(), buyInducementsCommand.getMercenaryPositionIds(),
				buyInducementsCommand.getMercenarySkills());
			int inducementCost = inducementCosts(game.getTeamAway(), buyInducementsCommand.getInducementSet());
			usedInducementGoldAway = starCost + mercCost + inducementCost;
			if (usedInducementGoldAway > availableInducementGoldAway) {
				int cardCost = cardCost(game.getTurnDataAway().getInducementSet());
				throw new FantasyFootballException("Team " + game.getTeamAway().getName() + " with id "
					+ game.getTeamAway().getId() + " spent more gold than should be available, spent "
					+ (usedInducementGoldAway + cardCost) + " vs available " + (availableInducementGoldAway + cardCost));
			}
		}
	}

	private void executeStep() {
		Game game = getGameState().getGame();

		switch (phase) {
			case INIT:
				init(game);
				break;
			case HOME:
				if (currentSelection != null) {
					handleCard();
				} else {
					swapToAway();
				}
				break;
			case AWAY:
				if (currentSelection != null) {
					handleCard();
				} else {
					phase = Phase.DONE;
				}
				break;
			default:
				// Removes warning.
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

			buildDecks();
			int freeCash = UtilGameOption.getIntOption(game, GameOptionId.FREE_INDUCEMENT_CASH)
				+ UtilGameOption.getIntOption(game, GameOptionId.FREE_CARD_CASH);

			availableInducementGoldHome = freeCash + game.getTeamHome().getTreasury() + game.getGameResult().getTeamResultHome().getPettyCashFromTvDiff();
			availableInducementGoldAway = freeCash + game.getTeamAway().getTreasury() + game.getGameResult().getTeamResultAway().getPettyCashFromTvDiff();
			phase = Phase.HOME;

			int cardPrice = UtilGameOption.getIntOption(getGameState().getGame(), GameOptionId.CARDS_SPECIAL_PLAY_COST);
			int cardSlots = UtilGameOption.getIntOption(getGameState().getGame(), GameOptionId.MAX_NR_OF_CARDS);
			boolean canBuyCards = cardSlots > 0 && availableInducementGoldHome >= cardPrice && fDeckByType.entrySet().stream().anyMatch(entry -> entry.getValue().size() > 1);

			boolean canBuyInducements = minimumInducementCost(game.getTeamHome()) <= availableInducementGoldHome;

			if (canBuyCards || canBuyInducements) {
				UtilServerDialog.showDialog(getGameState(),
					createDialogParameter(game.getTeamHome().getId(), game.getTeamHome().getTreasury(), availableInducementGoldHome, canBuyCards, cardSlots, cardPrice), false);
			} else {
				swapToAway();
			}
		}
	}

	private void swapToAway() {
		Game game = getGameState().getGame();
		phase = Phase.AWAY;
		usedCards.clear();

		int cardPrice = UtilGameOption.getIntOption(getGameState().getGame(), GameOptionId.CARDS_SPECIAL_PLAY_COST);
		int cardSlots = UtilGameOption.getIntOption(getGameState().getGame(), GameOptionId.MAX_NR_OF_CARDS);
		boolean canBuyCards = cardSlots > 0 && availableInducementGoldAway >= cardPrice && fDeckByType.entrySet().stream().anyMatch(entry -> entry.getValue().size() > 1);

		boolean canBuyInducements = minimumInducementCost(game.getTeamAway()) <= availableInducementGoldAway;

		if (canBuyCards || canBuyInducements) {
			UtilServerDialog.showDialog(getGameState(),
				createDialogParameter(game.getTeamAway().getId(), game.getTeamAway().getTreasury(), availableInducementGoldAway, canBuyCards, cardSlots, cardPrice), false);
		} else {
			phase = Phase.DONE;
		}

	}

	private int minimumInducementCost(Team team) {
		Roster roster = team.getRoster();
		InducementTypeFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE);
		return Stream.concat(
			Stream.concat(
				Arrays.stream(roster.getPositions()).filter(pos -> pos.getType() == PlayerType.STAR).map(RosterPosition::getCost).filter(i -> i > 0),
				factory.allTypes().stream().filter(type -> type.getCostId() != null && !type.getName().equals("card")).map(type -> UtilGameOption.getIntOption(getGameState().getGame(), type.getActualCostId(team)))
			),
			Arrays.stream(roster.getPositions()).filter(pos -> pos.getType() == PlayerType.MERCENARY).map(pos -> pos.getCost() + UtilGameOption.getIntOption(getGameState().getGame(), GameOptionId.INDUCEMENT_MERCENARIES_EXTRA_COST))
		).min(Integer::compareTo).orElse(Integer.MAX_VALUE);
	}

	private void handleCard() {
		int cardPrice = UtilGameOption.getIntOption(getGameState().getGame(), GameOptionId.CARDS_SPECIAL_PLAY_COST);
		CardChoice choice = currentSelection.isInitialDeckChoice() ? cardChoices.getInitial() : cardChoices.getRerolled();
		if (choice.getChoiceOne() != null) {
			usedCards.add(choice.getChoiceOne());
		}
		if (choice.getChoiceTwo() != null) {
			usedCards.add(choice.getChoiceTwo());
		}
		Card chosenCard = currentSelection.isFirstCardChoice() ? choice.getChoiceOne() : choice.getChoiceTwo();
		updateChoices();
		String changeKey = phase == Phase.HOME ? ModelChange.HOME : ModelChange.AWAY;
		getGameState().getGame().notifyObservers(new ModelChange(ModelChangeId.INDUCEMENT_SET_CARD_CHOICES, changeKey, cardChoices));

		// we have to update the card choices on client side first before adding the card as that will trigger the redraw
		// otherwise the model change for card choices might arrive after the coach clicked "Buy Card" again and thus the old choices could be displayed
		if (chosenCard != null) {
			if (phase == Phase.HOME) {
				availableInducementGoldHome -= cardPrice;
				getGameState().getGame().getTurnDataHome().getInducementSet().addAvailableCard(chosenCard);
			} else {
				availableInducementGoldAway -= cardPrice;
				getGameState().getGame().getTurnDataAway().getInducementSet().addAvailableCard(chosenCard);
			}
		}
	}

	private void updateChoices() {
		currentSelection = null;
		List<CardType> types = fDeckByType.entrySet().stream().filter(entry -> entry.getValue().size() > 1).map(Map.Entry::getKey).collect(Collectors.toList());
		cardChoices = new CardChoices(createChoice(drawRandom(types)), createChoice(drawRandom(types)));
	}

	private CardChoice createChoice(CardType type) {
		List<Card> availableCards = new ArrayList<>(fDeckByType.get(type).getCards());
		availableCards.removeAll(usedCards);
		return new CardChoice()
			.withType(type)
			.withChoiceOne(drawRandom(availableCards))
			.withChoiceTwo(drawRandom(availableCards));
	}

	private <T> T drawRandom(List<T> all) {
		T drawn = all.get(getGameState().getDiceRoller().rollDice(all.size()) - 1);
		all.remove(drawn);
		return drawn;
	}

	private void buildDecks() {
		Game game = getGameState().getGame();
		fDeckByType.clear();
		((CardTypeFactory) game.getFactory(FactoryType.Factory.CARD_TYPE)).getCardTypes().forEach(type -> {
			CardDeck deck = new CardDeck(type);
			deck.build(game);
			fDeckByType.put(type, deck);
		});
	}

	private DialogBuyCardsAndInducementsParameter createDialogParameter(String pTeamId, int treasury, int availableGold, boolean canBuyCards, int cardSlots, int cardPrice) {

		if (canBuyCards) {
			updateChoices();
		}
		DialogBuyCardsAndInducementsParameter dialogParameter =
			new DialogBuyCardsAndInducementsParameter(pTeamId, canBuyCards, cardSlots, treasury, availableGold, cardChoices, cardPrice);
		for (CardType type : fDeckByType.keySet()) {
			CardDeck deck = fDeckByType.get(type);
			dialogParameter.put(type, deck.size());
		}
		return dialogParameter;
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

		if (addedPlayerList.size() > 0) {
			RosterPlayer[] addedPlayers = addedPlayerList.toArray(new RosterPlayer[0]);
			UtilServerSteps.sendAddedPlayers(getGameState(), pTeam, addedPlayers);
		}
		return sum;
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
				if (!UtilGameOption.isOptionEnabled(game, GameOptionId.ALLOW_STAR_ON_BOTH_TEAMS)
					&& (otherTeamStarPlayer != null)) {
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

			if (removedPlayerList.size() > 0) {
				removeStarPlayerInducements(game.getTurnDataHome(), removedPlayerList.size());
				removeStarPlayerInducements(game.getTurnDataAway(), removedPlayerList.size());
				DbTransaction transaction = new DbTransaction();
				for (Player<?> player : removedPlayerList) {
					server.getCommunication().sendRemovePlayer(getGameState(), player.getId());
					otherTeam.removePlayer(player);
					game.getFieldModel().remove(player);
					getResult().addReport(new ReportDoubleHiredStarPlayer(player.getName()));
				}
				server.getDbUpdater().add(transaction);
			}

			if (addedPlayerList.size() > 0) {
				RosterPlayer[] addedPlayers = addedPlayerList.toArray(new RosterPlayer[0]);
				UtilServerSteps.sendAddedPlayers(getGameState(), pTeam, addedPlayers);
			}

		}

		return sum;

	}

	private int inducementCosts(Team team, InducementSet inducementSet) {
		Game game = getGameState().getGame();
		return Arrays.stream(inducementSet.getInducements())
			.filter(inducement -> inducement.getType().getActualCostId(team) != null)
			.mapToInt(inducement -> inducement.getValue() * UtilGameOption.getIntOption(game, inducement.getType().getActualCostId(team)))
			.sum();
	}

	private int cardCost(InducementSet inducementSet) {
		return inducementSet.getAllCards().length * UtilGameOption.getIntOption(getGameState().getGame(), GameOptionId.CARDS_SPECIAL_PLAY_COST);
	}

	private void leaveStep() {
		Game game = getGameState().getGame();

		buyInducementCommands.forEach(command -> handleBuyInducements(game, command));

		Team teamHome = getGameState().getGame().getTeamHome();
		Team teamAway = getGameState().getGame().getTeamAway();

		int spentMoneyHome = usedInducementGoldHome + cardCost(getGameState().getGame().getTurnDataHome().getInducementSet());
		int spentMoneyAway = usedInducementGoldAway + cardCost(getGameState().getGame().getTurnDataAway().getInducementSet());
		int newTvHome = teamHome.getTeamValue() + spentMoneyHome;
		int newTvAway = teamAway.getTeamValue() + spentMoneyAway;

		getResult().addReport(generateReport(teamHome, usedInducementGoldHome, newTvHome));
		getResult().addReport(generateReport(teamAway, usedInducementGoldAway, newTvAway));

		SequenceGeneratorFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);

		((Kickoff) factory.forName(SequenceGenerator.Type.Kickoff.name()))
			.pushSequence(new Kickoff.SequenceParams(getGameState(), true));

		com.fumbbl.ffb.server.step.generator.common.Inducement generator =
			((com.fumbbl.ffb.server.step.generator.common.Inducement) factory.forName(SequenceGenerator.Type.Inducement.name()));
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
		((RiotousRookies) factory.forName(SequenceGenerator.Type.RiotousRookies.name()))
			.pushSequence(new SequenceGenerator.SequenceParams(getGameState()));

		boolean usePrayers = ((GameOptionBoolean) getGameState().getGame().getOptions().getOptionWithDefault(GameOptionId.INDUCEMENT_PRAYERS_AVAILABLE_FOR_UNDERDOG)).isEnabled();

		if (usePrayers) {
			Sequence prayerSequence = new Sequence(getGameState());
			prayerSequence.add(StepId.PRAYERS);
			getGameState().getStepStack().push(prayerSequence.getSequence());
			publishParameter(StepParameter.from(StepParameterKey.TV_HOME, newTvHome));
			publishParameter(StepParameter.from(StepParameterKey.TV_AWAY, newTvAway));
		}

		int unspentMoneyHome = availableInducementGoldHome - usedInducementGoldHome;
		int spentTreasuryHome = Math.max(0, game.getTeamHome().getTreasury() - unspentMoneyHome);
		TeamResult teamResultHome = game.getGameResult().getTeamResultHome();
		teamResultHome.setTreasurySpentOnInducements(spentTreasuryHome);
		teamResultHome.setPettyCashUsed(Math.min(usedInducementGoldHome, teamResultHome.getPettyCashFromTvDiff()));

		int unspentMoneyAway = availableInducementGoldAway - usedInducementGoldAway;
		int spentTreasuryAway = Math.max(0, game.getTeamAway().getTreasury() - unspentMoneyAway);
		TeamResult teamResultAway = game.getGameResult().getTeamResultAway();
		teamResultAway.setTreasurySpentOnInducements(spentTreasuryAway);
		teamResultAway.setPettyCashUsed(Math.min(usedInducementGoldAway, teamResultAway.getPettyCashFromTvDiff()));

		InducementTypeFactory inducementTypeFactory = game.getFactory(FactoryType.Factory.INDUCEMENT_TYPE);

		inducementTypeFactory.allTypes().stream().filter(type -> type.getUsage() == Usage.REROLL_ARGUE).findFirst()
			.ifPresent(inducementType -> {

				if (teamHome.getSpecialRules().contains(SpecialRule.BRIBERY_AND_CORRUPTION)) {
					game.getTurnDataHome().getInducementSet().addInducement(new Inducement(inducementType, 1));
					getResult().addReport(new ReportBriberyAndCorruptionReRoll(teamHome.getId(), BriberyAndCorruptionAction.ADDED));
				}

				if (teamAway.getSpecialRules().contains(SpecialRule.BRIBERY_AND_CORRUPTION)) {
					game.getTurnDataAway().getInducementSet().addInducement(new Inducement(inducementType, 1));
					getResult().addReport(new ReportBriberyAndCorruptionReRoll(teamAway.getId(), BriberyAndCorruptionAction.ADDED));
				}

			});
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private ReportCardsAndInducementsBought generateReport(Team pTeam, int gold, int newTv) {
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
		return new ReportCardsAndInducementsBought(pTeam.getId(), inducementSet.getAllCards().length, nrOfInducements, nrOfStars, nrOfMercenaries, gold, newTv);
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

		IServerJsonOption.CARD_CHOICES.addTo(jsonObject, cardChoices.toJsonValue());

		IServerJsonOption.CARDS_USED.addTo(jsonObject, usedCards.stream().map(Card::getName).collect(Collectors.toList()));


		IServerJsonOption.CARDS_SELECTED_AWAY.addTo(jsonObject, fCardsSelectedAway);
		IServerJsonOption.CARDS_SELECTED_HOME.addTo(jsonObject, fCardsSelectedHome);
		IServerJsonOption.REPORTED_AWAY.addTo(jsonObject, fReportedAway);
		IServerJsonOption.REPORTED_HOME.addTo(jsonObject, fReportedHome);

		if (currentSelection != null) {
			IServerJsonOption.CARD_SELECTION.addTo(jsonObject, currentSelection.name());
		}

		JsonArray commandArray = new JsonArray();

		buyInducementCommands.stream().map(ClientCommandBuyInducements::toJsonValue).forEach(commandArray::add);

		IServerJsonOption.INDUCEMENT_COMMANDS.addTo(jsonObject, commandArray);

		IServerJsonOption.STEP_PHASE.addTo(jsonObject, phase.name());
		return jsonObject;
	}

	@Override
	public StepBuyCardsAndInducements initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		availableInducementGoldAway = IServerJsonOption.INDUCEMENT_GOLD_AWAY.getFrom(game, jsonObject);
		availableInducementGoldHome = IServerJsonOption.INDUCEMENT_GOLD_HOME.getFrom(game, jsonObject);
		fCardsSelectedAway = IServerJsonOption.CARDS_SELECTED_AWAY.getFrom(game, jsonObject);
		fCardsSelectedHome = IServerJsonOption.CARDS_SELECTED_HOME.getFrom(game, jsonObject);
		fReportedAway = IServerJsonOption.REPORTED_AWAY.getFrom(game, jsonObject);
		fReportedHome = IServerJsonOption.REPORTED_HOME.getFrom(game, jsonObject);

		JsonObject choiceObject = IServerJsonOption.CARD_CHOICES.getFrom(game, jsonObject);
		if (choiceObject != null) {
			cardChoices = new CardChoices().initFrom(game, jsonObject);
		}

		CardFactory cardFactory = game.getFactory(FactoryType.Factory.CARD);

		String[] selectedCardNames = IJsonOption.CARDS_USED.getFrom(game, jsonObject);
		if (selectedCardNames != null) {
			usedCards = Arrays.stream(selectedCardNames).map(cardFactory::forName).collect(Collectors.toList());
		}

		String selectionName = IServerJsonOption.CARD_SELECTION.getFrom(game, jsonObject);
		if (selectionName != null) {
			currentSelection = ClientCommandSelectCardToBuy.Selection.valueOf(selectionName);
		}

		JsonArray commandArray = IServerJsonOption.INDUCEMENT_COMMANDS.getFrom(game, jsonObject);

		if (commandArray != null) {
			commandArray.values().stream().map(command -> new ClientCommandBuyInducements().initFrom(game, command)).forEach(buyInducementCommands::add);
		}

		phase = Phase.valueOf(IServerJsonOption.STEP_PHASE.getFrom(game, jsonObject));

		return this;
	}

	private enum Phase {
		INIT, HOME, AWAY, DONE
	}

}

package com.balancedbytes.games.ffb.server.step.bb2020.start;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.dialog.DialogBuyCardsAndInducementsParameter;
import com.balancedbytes.games.ffb.factory.CardFactory;
import com.balancedbytes.games.ffb.factory.CardTypeFactory;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.CardChoice;
import com.balancedbytes.games.ffb.inducement.CardType;
import com.balancedbytes.games.ffb.inducement.Usage;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.model.RosterPlayer;
import com.balancedbytes.games.ffb.model.RosterPosition;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBuyCard;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBuyInducements;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.GameOptionInt;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportDoubleHiredStarPlayer;
import com.balancedbytes.games.ffb.server.CardDeck;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.db.DbTransaction;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.skill.Loner;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Step in start game sequence to buy cards.
 *
 * Sets stepParameter INDUCEMENT_GOLD_AWAY for all steps on the stack. Sets
 * stepParameter INDUCEMENT_GOLD_HOME for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepBuyCards extends AbstractStep {

	private Integer fInducementGoldHome;
	private Integer fInducementGoldAway;
	private Integer fGoldUsedHome = 0;
	private Integer fGoldUsedAway = 0;

	private boolean fCardsSelectedHome;
	private boolean fCardsSelectedAway;
	private boolean fInducementsSelectedHome;
	private boolean fInducementsSelectedAway;

	private boolean fReportedHome;
	private boolean fReportedAway;
	private CardChoice initialChoice, rerolledChoice;
	private List<Card> selectedCards = new ArrayList<>();
	private List<Card> discardedCards = new ArrayList<>();

	private final transient Map<CardType, CardDeck> fDeckByType;
	private transient CardType fBuyCardHome;
	private transient CardType fBuyCardAway;
	private transient Map<CardType, Integer> cardPrices;

	public StepBuyCards(GameState pGameState) {
		super(pGameState);
		fDeckByType = new HashMap<>();
		fBuyCardHome = null;
		fBuyCardAway = null;
	}

	public StepId getId() {
		return StepId.BUY_CARDS;
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
				case CLIENT_BUY_CARD:
					ClientCommandBuyCard buyCardCommand = (ClientCommandBuyCard) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
						fBuyCardHome = buyCardCommand.getCardType();
						if (fBuyCardHome == null) {
							fCardsSelectedHome = true;
						}
					} else {
						fBuyCardAway = buyCardCommand.getCardType();
						if (fBuyCardAway == null) {
							fCardsSelectedAway = true;
						}
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
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
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		int freeCash = UtilGameOption.getIntOption(game, GameOptionId.FREE_INDUCEMENT_CASH)
			+ UtilGameOption.getIntOption(game, GameOptionId.FREE_CARD_CASH);

		if (cardPrices == null) {
			buildDecks();

			cardPrices = new HashMap<>();
			((CardTypeFactory) game.getFactory(FactoryType.Factory.CARD_TYPE)).getCardTypes().forEach(cardType -> {
				int price = ((GameOptionInt) game.getOptions().getOptionWithDefault(cardType.getCostId())).getValue();
				cardPrices.put(cardType, price);
			});
		}

		if (fInducementGoldHome == null) {
			fInducementGoldHome = freeCash + game.getTeamHome().getTreasury() + game.getGameResult().getTeamResultHome().getPettyCashAvailable();
			UtilServerDialog.showDialog(getGameState(),
				createDialogParameter(game.getTeamHome().getId(), game.getTeamHome().getTreasury(), fInducementGoldHome), false);
		}

		if (fInducementGoldAway == null) {
			fInducementGoldAway = game.getTeamAway().getTreasury() + game.getGameResult().getTeamResultAway().getPettyCashAvailable();
		}
		
/*
		if (fBuyCardHome != null) {
			fInducementGoldHome -= cardPrices.getOrDefault(fBuyCardHome, 0);
			CardDeck deck = fDeckByType.get(fBuyCardHome);
			Card card = getGameState().getDiceRoller().drawCard(deck);
			game.getTurnDataHome().getInducementSet().addAvailableCard(card);
			fBuyCardHome = null;
		} else if (fBuyCardAway != null) {
			fInducementGoldAway -= cardPrices.getOrDefault(fBuyCardAway, 0);
			CardDeck deck = fDeckByType.get(fBuyCardAway);
			Card card = getGameState().getDiceRoller().drawCard(deck);
			game.getTurnDataAway().getInducementSet().addAvailableCard(card);
			fBuyCardAway = null;
		} else {
			if (!fCardsSelectedHome && !fCardsSelectedAway) {

				fInducementGoldHome = UtilInducementSequence.calculateInducementGold(game, true) + freeCash;
				fInducementGoldAway = UtilInducementSequence.calculateInducementGold(game, false) + freeCash;
			}
			if (fInducementGoldHome < minimumCardPrice) {
				fCardsSelectedHome = true;
			}
			if (fInducementGoldAway < minimumCardPrice) {
				fCardsSelectedAway = true;
			}
			if (fCardsSelectedHome && !fReportedHome) {
				fReportedHome = true;
				Card[] cardsHome = game.getTurnDataHome().getInducementSet().getAllCards();
				int totalCostHome = calculateTotalCost(cardsHome);
				getResult().addReport(new ReportCardsBought(game.getTeamHome().getId(), cardsHome.length, totalCostHome));
			}
			if (fCardsSelectedAway && !fReportedAway) {
				fReportedAway = true;
				Card[] cardsAway = game.getTurnDataAway().getInducementSet().getAllCards();
				int totalCostAway = calculateTotalCost(cardsAway);
				getResult().addReport(new ReportCardsBought(game.getTeamAway().getId(), cardsAway.length, totalCostAway));
			}
			if (!fCardsSelectedHome && !fCardsSelectedAway) {
				int homeTV = gameResult.getTeamResultHome().getTeamValue();
				int awayTV = gameResult.getTeamResultAway().getTeamValue();
				if (homeTV > awayTV) {
					UtilServerDialog.showDialog(getGameState(),
							createDialogParameter(game.getTeamHome().getId(), fInducementGoldHome), false);
				} else {
					UtilServerDialog.showDialog(getGameState(),
							createDialogParameter(game.getTeamAway().getId(), fInducementGoldAway), false);
				}
			} else if (!fCardsSelectedHome) {
				UtilServerDialog.showDialog(getGameState(),
						createDialogParameter(game.getTeamHome().getId(), fInducementGoldHome), false);
			} else if (!fCardsSelectedAway) {
				UtilServerDialog.showDialog(getGameState(),
						createDialogParameter(game.getTeamAway().getId(), fInducementGoldAway), false);
			} else {
				int maxInducementGoldHome = UtilInducementSequence.calculateInducementGold(game, true) + freeCash;
				publishParameter(new StepParameter(StepParameterKey.INDUCEMENT_GOLD_HOME,
						Math.min(fInducementGoldHome, maxInducementGoldHome)));
				int maxInducementGoldAway = UtilInducementSequence.calculateInducementGold(game, false) + freeCash;
				publishParameter(new StepParameter(StepParameterKey.INDUCEMENT_GOLD_AWAY,
						Math.min(fInducementGoldAway, maxInducementGoldAway)));
				getResult().setNextAction(StepAction.NEXT_STEP);
			}

		}*/
	}

	private void updateChoices() {
		List<CardType> types = fDeckByType.entrySet().stream().filter(entry -> entry.getValue().size() > 1).map(Map.Entry::getKey).collect(Collectors.toList());
		initialChoice = createChoice(drawRandom(types));
		rerolledChoice = createChoice(drawRandom(types));
	}

	private CardChoice createChoice(CardType type) {
		Set<Card> drawnCards = Stream.concat(selectedCards.stream(), discardedCards.stream()).collect(Collectors.toSet());
		List<Card> availableCards = new ArrayList<>(fDeckByType.get(type).getCards());
		availableCards.removeAll(drawnCards);
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

	private int calculateTotalCost(Card[] pCards) {
		int totalCost = 0;
		if (ArrayTool.isProvided(pCards)) {
			for (Card card : pCards) {
				totalCost += cardPrices.getOrDefault(card.getType(), 0);
			}
		}
		return totalCost;
	}

	private void buildDecks() {
		Game game = getGameState().getGame();
		fDeckByType.clear();
		((CardTypeFactory)game.getFactory(FactoryType.Factory.CARD_TYPE)).getCardTypes().forEach(type -> {
			CardDeck deck = new CardDeck(type);
			deck.build(game);
			fDeckByType.put(type, deck);
		});
	}

	private DialogBuyCardsAndInducementsParameter createDialogParameter(String pTeamId, int treasury, int availableGold) {
		boolean noCards = UtilGameOption.isOptionEnabled(getGameState().getGame(), GameOptionId.USE_PREDEFINED_INDUCEMENTS);
		int availableCards = noCards ? 0 : UtilGameOption.getIntOption(getGameState().getGame(), GameOptionId.MAX_NR_OF_CARDS);
		updateChoices();
		DialogBuyCardsAndInducementsParameter dialogParameter = new DialogBuyCardsAndInducementsParameter(pTeamId, availableCards, treasury, availableGold, initialChoice, rerolledChoice);
		for (CardType type : fDeckByType.keySet()) {
			CardDeck deck = fDeckByType.get(type);
			dialogParameter.put(type, deck.size());
		}
		return dialogParameter;
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
			mercenary.updatePosition(position, game.getRules());
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
			for (String pPositionId : pPositionIds) {
				RosterPosition position = roster.getPositionById(pPositionId);
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
					starPlayer.updatePosition(position, game.getRules());
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
		if (fInducementGoldAway != null) {
			IServerJsonOption.INDUCEMENT_GOLD_AWAY.addTo(jsonObject, fInducementGoldAway);
		}
		if (fInducementGoldHome != null) {
			IServerJsonOption.INDUCEMENT_GOLD_HOME.addTo(jsonObject, fInducementGoldHome);
		}

		if (initialChoice != null) {
			IServerJsonOption.CARD_CHOICE_INITIAL.addTo(jsonObject, initialChoice.toJsonValue());
		}
		if (rerolledChoice != null) {
			IServerJsonOption.CARD_CHOICE_REROLLED.addTo(jsonObject, rerolledChoice.toJsonValue());
		}

		IServerJsonOption.CARDS_SELECTED.addTo(jsonObject, selectedCards.stream().map(Card::getName).collect(Collectors.toList()));

		IServerJsonOption.CARDS_DISCARDED.addTo(jsonObject, discardedCards.stream().map(Card::getName).collect(Collectors.toList()));


		IServerJsonOption.CARDS_SELECTED_AWAY.addTo(jsonObject, fCardsSelectedAway);
		IServerJsonOption.CARDS_SELECTED_HOME.addTo(jsonObject, fCardsSelectedHome);
		IServerJsonOption.REPORTED_AWAY.addTo(jsonObject, fReportedAway);
		IServerJsonOption.REPORTED_HOME.addTo(jsonObject, fReportedHome);
		return jsonObject;
	}

	@Override
	public StepBuyCards initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fInducementGoldAway = IServerJsonOption.INDUCEMENT_GOLD_AWAY.getFrom(game, jsonObject);
		fInducementGoldHome = IServerJsonOption.INDUCEMENT_GOLD_HOME.getFrom(game, jsonObject);
		fCardsSelectedAway = IServerJsonOption.CARDS_SELECTED_AWAY.getFrom(game, jsonObject);
		fCardsSelectedHome = IServerJsonOption.CARDS_SELECTED_HOME.getFrom(game, jsonObject);
		fReportedAway = IServerJsonOption.REPORTED_AWAY.getFrom(game, jsonObject);
		fReportedHome = IServerJsonOption.REPORTED_HOME.getFrom(game, jsonObject);
		JsonObject choiceObject = IServerJsonOption.CARD_CHOICE_INITIAL.getFrom(game, jsonObject);
		if (choiceObject != null) {
			initialChoice = new CardChoice().initFrom(game, choiceObject);
		}

		choiceObject = IServerJsonOption.CARD_CHOICE_REROLLED.getFrom(game, jsonObject);
		if (choiceObject != null) {
			rerolledChoice = new CardChoice().initFrom(game, choiceObject);
		}

		CardFactory cardFactory = game.getFactory(FactoryType.Factory.CARD);

		String[] selectedCardNames = IJsonOption.CARDS_SELECTED.getFrom(game, jsonObject);
		if (selectedCardNames != null) {
			selectedCards = Arrays.stream(selectedCardNames).map(cardFactory::forName).collect(Collectors.toList());
		}

		String[] discardedCardNames = IJsonOption.CARDS_DISCARDED.getFrom(game, jsonObject);
		if (discardedCardNames != null) {
			discardedCards = Arrays.stream(discardedCardNames).map(cardFactory::forName).collect(Collectors.toList());
		}

		return this;
	}

}

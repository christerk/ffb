package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollBlockForTargetsPropertiesParameter;
import com.fumbbl.ffb.model.BlockRollProperties;
import com.fumbbl.ffb.model.Game;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ObjIntConsumer;

public class DialogReRollBlockForTargetsProperties extends AbstractDialogMultiBlockProperties implements MultiReRollMnemonics {

	private final DialogReRollBlockForTargetsPropertiesParameter dialogParameter;
	private final Map<String, Map<ReRolledAction, ReRollSource>> actionToSourceMaps;
	private ReRollSource reRollSource;
	private final List<Integer> anyDiceIndexes = new ArrayList<>();
	private final Map<String, List<ReRollSource>> teamReRollSources = new HashMap<>();

	private int proIndex;

	public DialogReRollBlockForTargetsProperties(FantasyFootballClient pClient,
		DialogReRollBlockForTargetsPropertiesParameter parameter,
		Map<String, Map<ReRolledAction, ReRollSource>> actionToSourceMaps) {

		super(pClient, "Block Roll", false);

		dialogParameter = parameter;
		this.actionToSourceMaps = actionToSourceMaps;

		Game game = getClient().getGame();

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		List<Mnemonics> mnemonics = mnemonics();

		for (BlockRollProperties blockRoll : parameter.getBlockRolls()) {
			Map<ReRolledAction, ReRollSource> actionReRollSourceMap = actionToSourceMaps.get(blockRoll.getTargetId());
			String target = blockRoll.getTargetId();
			boolean ownChoice = blockRoll.isOwnChoice();
			Color background = ownChoice ? colorOwnChoice : colorOpponentChoice;
			JPanel targetPanel = new BackgroundPanel(background);
			targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
			targetPanel.setAlignmentX(CENTER_ALIGNMENT);
			ReRollSource anyBlockDiceReRollSource = actionReRollSourceMap.get(ReRolledActions.MULTI_BLOCK_DICE);
			JPanel dicePanel = dicePanel(blockRoll, ownChoice && blockRoll.needsSelection(), blockDieMnemonics.remove(0),
				anyBlockDiceReRollSource != null && blockRoll.getNrOfDice() > 1);

			targetPanel.add(dicePanel);
			if (blockRoll.hasReRollsLeft()) {
				Mnemonics currentMnemonics = mnemonics.remove(0);

				ReRollSource singleDiePerActicationReRollSource =
					actionReRollSourceMap.get(ReRolledActions.SINGLE_DIE_PER_ACTIVATION);
				ReRollSource singleDieReRollSource = actionReRollSourceMap.get(ReRolledActions.SINGLE_DIE);
				ReRollSource bothDownReRollSource = actionReRollSourceMap.get(ReRolledActions.SINGLE_BOTH_DOWN);
				ReRollSource skullReRollSource = actionReRollSourceMap.get(ReRolledActions.SINGLE_SKULL);
				ReRollSource anyDiceReRollSource = actionReRollSourceMap.get(ReRolledActions.MULTI_BLOCK_DICE);
				ReRollSource singleBlockDieReRollSource = actionReRollSourceMap.get(ReRolledActions.SINGLE_BLOCK_DIE);

				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
				buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
				buttonPanel.add(Box.createHorizontalGlue());
				buttonPanel.setOpaque(false);

				List<ReRollSource> targetTeamReRollSources = mascotExtension.teamReRollSources(blockRoll, true);
				teamReRollSources.put(target, targetTeamReRollSources);
				for (int i = 0; i < targetTeamReRollSources.size() && i < currentMnemonics.getTeam().size(); i++) {
					ReRollSource teamReRollSource = targetTeamReRollSources.get(i);
					JButton teamReRollButton = createReRollButton(target, mascotExtension.label(teamReRollSource, game),
						teamReRollSource, currentMnemonics.getTeam().get(i));
					buttonPanel.add(mascotExtension.wrapperPanel(teamReRollButton));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (blockRoll.getNrOfDice() == 1) {
					if (singleDiePerActicationReRollSource != null) {
						JButton proButton = createReRollButton(target, singleDiePerActicationReRollSource.getName(game),
							singleDiePerActicationReRollSource,
							currentMnemonics.getPro().get(0));
						buttonPanel.add(mascotExtension.wrapperPanel(proButton));
						buttonPanel.add(Box.createHorizontalGlue());
					}
					if (singleDieReRollSource != null) {
						buttonPanel.add(mascotExtension.wrapperPanel(
							createReRollButton(target, singleDieReRollSource.getName(pClient.getGame()), singleDieReRollSource,
								currentMnemonics.getAnyDie().get(0))));
						buttonPanel.add(Box.createHorizontalGlue());
					}
					if (singleBlockDieReRollSource != null) {
						buttonPanel.add(mascotExtension.wrapperPanel(
							createReRollButton(target, singleBlockDieReRollSource.getName(game), singleBlockDieReRollSource, 
								currentMnemonics.getSingleBlockDie().get(0))));
						buttonPanel.add(Box.createHorizontalGlue());
					}
				}
				if (bothDownReRollSource != null) {
					buttonPanel.add(mascotExtension.wrapperPanel(
						createReRollButton(target, "Brawler Re-Roll", bothDownReRollSource, currentMnemonics.getBrawler())));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (skullReRollSource != null) {
					buttonPanel.add(mascotExtension.wrapperPanel(
						createReRollButton(target, "Hatred Re-Roll", skullReRollSource, currentMnemonics.getHatred())));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (anyDiceReRollSource != null) {
					JButton anyDiceButton = createReRollButton(target, "Savage Blow", anyDiceReRollSource,
						currentMnemonics.getAnyBlockDice());
					anyDiceButton.setEnabled(blockRoll.getNrOfDice() == 1);
					anyDiceButtons.put(target, anyDiceButton);
					buttonPanel.add(mascotExtension.wrapperPanel(anyDiceButton));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (!ownChoice) {
					buttonPanel.add(mascotExtension.wrapperPanel(createReRollButton(target, "No Re-Roll", null,
						currentMnemonics.getNone())));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				targetPanel.add(Box.createVerticalStrut(3));
				targetPanel.add(buttonPanel);

				if (Math.abs(blockRoll.getNrOfDice()) > 1) {
					if (singleDiePerActicationReRollSource != null) {
						targetPanel.add(createSingleDieReRollPanel(proTextPanel(),
							blockRoll.getTargetId(), Math.abs(blockRoll.getNrOfDice()), currentMnemonics.getPro(), this::proAction));
					}
					if (singleDieReRollSource != null) {
						targetPanel.add(createSingleDieReRollPanel(textPanel(singleDieReRollSource.getName(getClient().getGame())),
							blockRoll.getTargetId(), Math.abs(blockRoll.getNrOfDice()), currentMnemonics.getAnyDie(),
							this::anyDieAction));
					}
					if (singleBlockDieReRollSource != null) {
						targetPanel.add(createSingleDieReRollPanel(textPanel(singleBlockDieReRollSource.getName(getClient().getGame())),
							blockRoll.getTargetId(), Math.abs(blockRoll.getNrOfDice()),	currentMnemonics.getSingleBlockDie(),
							this::singleBlockDieAction));
					}
				}

				targetPanel.add(Box.createVerticalStrut(3));
			}

			if (!ownChoice) {
				targetPanel.add(opponentChoicePanel());
			}

			targetPanel.add(namePanel(target));
			mainPanel.add(targetPanel);
			mainPanel.add(Box.createVerticalStrut(5));
		}

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(mainPanel);

		pack();
		setLocationToCenter();

	}

	private JPanel createSingleDieReRollPanel(JPanel titlePanel, String target, int diceCount, List<Character> mnemonics, ObjIntConsumer<String> consumer) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(titlePanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(CENTER_ALIGNMENT);

		for (int die = 1; die <= diceCount; die++) {
			int finalDie = die;
			JButton proButton = new JButton(dimensionProvider(), "Die " + die, mnemonics.get(0));
			proButton.addActionListener(e -> consumer.accept(target, finalDie - 1));
			proButton.setMnemonic(mnemonics.get(0));
			this.addKeyListener(new PressedKeyListener(mnemonics.get(0)) {
				@Override
				protected void handleKey() {
					consumer.accept(target, finalDie - 1);
				}
			});
			buttonPanel.add(proButton);
			mnemonics.remove(0);
		}

		panel.add(buttonPanel);
		return panel;
	}

	private void proAction(String target, int proIndex) {
		reRollSource = ReRollSources.PRO;
		this.proIndex = proIndex;
		this.selectedTarget = target;
		close();
	}

	private void anyDieAction(String target, int index) {
		reRollSource = actionToSourceMaps.get(target).get(ReRolledActions.SINGLE_DIE);
		this.proIndex = index;
		this.selectedTarget = target;
		close();
	}

	private void singleBlockDieAction(String target, int index) {
		reRollSource = actionToSourceMaps.get(target).get(ReRolledActions.SINGLE_BLOCK_DIE);
		this.proIndex = index;
		this.selectedTarget = target;
		anyDiceIndexes.add(index);
		close();
	}

	private JButton createReRollButton(String target, String buttonName, ReRollSource reRollSource, char mnemonic) {
		JButton button = new JButton(dimensionProvider(), buttonName, mnemonic);
		button.setOpaque(false);
		button.addActionListener(e -> handleReRollUse(target, reRollSource));
		this.addKeyListener(new PressedKeyListener(mnemonic) {
			@Override
			protected void handleKey() {
				handleReRollUse(target, reRollSource);
			}
		});
		button.setMnemonic((int) mnemonic);
		return button;
	}

	private void handleReRollUse(String target, ReRollSource reRollSource) {
		if (reRollSource == ReRollSources.PRO) {
			proAction(target, 0);
			return;
		}

		this.reRollSource = reRollSource;
		selectedTarget = target;
		if (reRollSource == ReRollSources.SAVAGE_BLOW) {
			List<JCheckBox> boxes = anyDiceCheckBoxes.get(target);
			if (boxes == null) {
				anyDiceIndexes.add(0);
			} else {
				for (int i = 0; i < boxes.size(); i++) {
					if (boxes.get(i).isSelected()) {
						anyDiceIndexes.add(i);
					}
				}
			}
		}
		if (reRollSource == ReRollSources.LORD_OF_CHAOS) {
			anyDiceIndexes.add(0);
		}
		close();
	}

	@Override
	protected void close() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(DialogReRollBlockForTargetsProperties.this);
		}
	}

	public DialogId getId() {
		return DialogId.RE_ROLL_BLOCK_FOR_TARGETS_PROPERTIES;
	}

	public String getSelectedTarget() {
		return selectedTarget;
	}

	public ReRollSource getReRollSource() {
		return reRollSource;
	}

	public Integer getSelectedIndex() {
		return selectedIndex;
	}

	public int getProIndex() {
		return proIndex;
	}

	public List<Integer> getAnyDiceIndexes() {
		return anyDiceIndexes;
	}

	public DialogReRollBlockForTargetsPropertiesParameter getDialogParameter() {
		return dialogParameter;
	}

	/**
	 * @return whether the selected re-roll source is Pro or one of the team re-roll options of the selected target
	 */
	public boolean isTeamReRollSelected() {
		return reRollSource == ReRollSources.PRO || (selectedTarget != null
			&& teamReRollSources.getOrDefault(selectedTarget, Collections.emptyList()).contains(reRollSource));
	}
}

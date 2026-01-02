package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollProperty;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ObjIntConsumer;

public class DialogReRollBlockForTargetsProperties extends AbstractDialogMultiBlockProperties {

	private final DialogReRollBlockForTargetsPropertiesParameter dialogParameter;
	private final Map<String, Map<ReRolledAction, ReRollSource>> actionToSourceMaps;
	private ReRollSource reRollSource;
	private final List<Integer> anyDiceIndexes = new ArrayList<>();
	private final Set<String> blockWillUseMascot = new HashSet<>();
	private final Map<String, FallbackCheckBoxes> fallbackCheckBoxes = new HashMap<>();

	@SuppressWarnings("FieldCanBeLocal")
	private final List<Mnemonics> mnemonics = new ArrayList<Mnemonics>() {{
		add(new Mnemonics('T', 'N', 'B', 'H',
			new ArrayList<Character>() {{
				add('P');
				add('o');
				add('x');
			}},
			new ArrayList<Character>() {{
				add('C');
				add('u');
				add('m');
			}}, 'S', 'f', 'p', 'n'));
		add(new Mnemonics('e', 'l', 'r', 'h',
			new ArrayList<Character>() {{
				add('r');
				add('y');
				add('z');
			}},
			new ArrayList<Character>() {{
				add('a');
				add('f');
				add('v');
			}}, 'b', 'q', 's','u' ));
	}};
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

				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
				buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
				buttonPanel.add(Box.createHorizontalGlue());
				buttonPanel.setOpaque(false);

				ReRollSource trrSource = mascotExtension.teamReRollText(blockRoll);

				FallbackCheckBoxes checkBoxes = new FallbackCheckBoxes();
				boolean willUseMascot = trrSource == ReRollSources.MASCOT;
				fallbackCheckBoxes.put(target, checkBoxes);

				if (willUseMascot) {
					blockWillUseMascot.add(target);
				}

				if (blockRoll.hasProperty(ReRollProperty.TRR) || willUseMascot) {
					JButton trrButton =
						createReRollButton(target, trrSource.getName(getClient().getGame()), ReRollSources.TEAM_RE_ROLL,
							currentMnemonics.team);
					if (willUseMascot) {
						JPanel mascotPanel = new JPanel();
						mascotPanel.setBackground(null);
						mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
						mascotPanel.setAlignmentX(Box.CENTER_ALIGNMENT);
						mascotPanel.setAlignmentY(Box.TOP_ALIGNMENT);
						trrButton.setAlignmentX(Box.CENTER_ALIGNMENT);
						mascotPanel.add(trrButton);
						mascotPanel.setOpaque(false);
						if (blockRoll.hasProperty(ReRollProperty.TRR)) {
							checkBoxes.trr = mascotExtension.checkBox("TRR fallback", currentMnemonics.trrFallback, Color.WHITE,
								dimensionProvider(),
								null, null);
							mascotPanel.add(checkBoxes.trr);
						}
						buttonPanel.add(mascotPanel);
					} else {
						buttonPanel.add(mascotExtension.wrapperPanel(trrButton));
					}

					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (blockRoll.getNrOfDice() == 1) {
					if (singleDiePerActicationReRollSource != null) {
						JButton proButton = createReRollButton(target, singleDiePerActicationReRollSource.getName(game),
							singleDiePerActicationReRollSource,
							currentMnemonics.pro.get(0));
						if (willUseMascot || blockRoll.hasProperty(ReRollProperty.TRR)) {
							buttonPanel.add(proMascotPanelSingle(blockRoll, proButton, currentMnemonics));
						} else {
							buttonPanel.add(mascotExtension.wrapperPanel(proButton));
						}
						buttonPanel.add(Box.createHorizontalGlue());
					}
					if (singleDieReRollSource != null) {
						buttonPanel.add(mascotExtension.wrapperPanel(
							createReRollButton(target, singleDieReRollSource.getName(pClient.getGame()), singleDieReRollSource,
								currentMnemonics.anyDie.get(0))));
						buttonPanel.add(Box.createHorizontalGlue());
					}
				}
				if (bothDownReRollSource != null) {
					buttonPanel.add(mascotExtension.wrapperPanel(
						createReRollButton(target, "Brawler Re-Roll", bothDownReRollSource, currentMnemonics.brawler)));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (skullReRollSource != null) {
					buttonPanel.add(mascotExtension.wrapperPanel(
						createReRollButton(target, "Hatred Re-Roll", skullReRollSource, currentMnemonics.hatred)));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (anyDiceReRollSource != null) {
					JButton anyDiceButton = createReRollButton(target, "Savage Blow", anyDiceReRollSource,
						currentMnemonics.anyBlockDice);
					anyDiceButton.setEnabled(blockRoll.getNrOfDice() == 1);
					anyDiceButtons.put(target, anyDiceButton);
					buttonPanel.add(mascotExtension.wrapperPanel(anyDiceButton));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				if (!ownChoice) {
					buttonPanel.add(mascotExtension.wrapperPanel(createReRollButton(target, "No Re-Roll", null,
						currentMnemonics.none)));
					buttonPanel.add(Box.createHorizontalGlue());
				}
				targetPanel.add(Box.createVerticalStrut(3));
				targetPanel.add(buttonPanel);

				if (Math.abs(blockRoll.getNrOfDice()) > 1) {
					if (singleDiePerActicationReRollSource != null) {
						targetPanel.add(createSingleDieReRollPanel(proTextPanel(),
							blockRoll.getTargetId(), Math.abs(blockRoll.getNrOfDice()), currentMnemonics.pro, this::proAction));

						targetPanel.add(proMascotPanelMultiple(blockRoll, currentMnemonics));
					}
					if (singleDieReRollSource != null) {
						targetPanel.add(createSingleDieReRollPanel(textPanel(singleDieReRollSource.getName(getClient().getGame())),
							blockRoll.getTargetId(), Math.abs(blockRoll.getNrOfDice()), currentMnemonics.anyDie, this::anyDieAction));
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

	private JPanel proMascotPanelSingle(BlockRollProperties blockRoll, JButton proButton, Mnemonics mnemonics) {
		JPanel proPanel = new JPanel();
		Color checkboxColor =
			!blockRoll.hasProperty(ReRollProperty.TRR) || rerollButtons(blockRoll) > 2 ||
				blockRoll.getNrOfDice() < 0 ?
				Color.WHITE : Color.BLACK;

		proPanel.setLayout(new BoxLayout(proPanel, BoxLayout.Y_AXIS));
		proPanel.setAlignmentX(Box.CENTER_ALIGNMENT);
		proPanel.setAlignmentY(Box.TOP_ALIGNMENT);
		proPanel.setOpaque(false);
		proButton.setAlignmentX(Box.CENTER_ALIGNMENT);
		proPanel.add(proButton);
		FallbackCheckBoxes checkBoxes = fallbackCheckBoxes.get(blockRoll.getTargetId());
		boolean willUseMascot = blockWillUseMascot.contains(blockRoll.getTargetId());
		if (willUseMascot) {
			checkBoxes.pro =
				mascotExtension.checkBox("Mascot", mnemonics.proFallback, checkboxColor, dimensionProvider(), null, null);
			proPanel.add(checkBoxes.pro);
		}
		if (blockRoll.hasProperty(ReRollProperty.TRR)) {
			checkBoxes.proTrr = mascotExtension.checkBox(willUseMascot ? "TRR fallback" : "ReRoll", mnemonics.proTrrFallback,
				checkboxColor, dimensionProvider(), null, null);
			checkBoxes.proTrr.setEnabled(!willUseMascot);
			proPanel.add(checkBoxes.proTrr);
		}
		return proPanel;
	}

	private long rerollButtons(BlockRollProperties blockRoll) {
		return blockRoll.getReRollProperties().stream()
			.filter(prop -> prop.isActualReRoll() && prop != ReRollProperty.MASCOT).count() +
			blockRoll.getRrActionToSource().size();
	}

	private JPanel proMascotPanelMultiple(BlockRollProperties blockRoll, Mnemonics mnemonics) {
		JPanel mascotPanel = new JPanel();
		List<Color> checkboxColor = new ArrayList<Color>() {{
			add(Color.WHITE);
			add(Color.BLACK);
		}};

		mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.X_AXIS));
		mascotPanel.setAlignmentX(Box.CENTER_ALIGNMENT);
		mascotPanel.setAlignmentY(Box.TOP_ALIGNMENT);
		mascotPanel.setOpaque(false);
		FallbackCheckBoxes checkBoxes = fallbackCheckBoxes.get(blockRoll.getTargetId());
		boolean willUseMascot = blockWillUseMascot.contains(blockRoll.getTargetId());
		if (willUseMascot) {
			checkBoxes.pro = mascotExtension.checkBox("Mascot", mnemonics.proFallback, checkboxColor.remove(0),
				dimensionProvider(), e -> syncProCheckBoxes(checkBoxes), new PressedKeyListener(0) {
					@Override
					protected void handleKey() {
						syncProCheckBoxes(checkBoxes);
					}
				});
			mascotPanel.add(checkBoxes.pro);
		}
		if (blockRoll.hasProperty(ReRollProperty.TRR)) {
			checkBoxes.proTrr = mascotExtension.checkBox(willUseMascot ? "TRR fallback" : "ReRoll", mnemonics.proTrrFallback,
				checkboxColor.remove(0), dimensionProvider(), null, null);
			checkBoxes.proTrr.setEnabled(!willUseMascot);
			mascotPanel.add(checkBoxes.proTrr);
		}
		return mascotPanel;
	}

	private void syncProCheckBoxes(FallbackCheckBoxes checkBoxes) {
		if (checkBoxes.proTrr != null) {
			if (!checkBoxes.pro.isSelected()) {
				checkBoxes.proTrr.setSelected(false);
			}
			checkBoxes.proTrr.setEnabled(checkBoxes.pro.isSelected());
		}
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
		reRollSource = determineProReRollSource(target);
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

	private ReRollSource determineProReRollSource(String target) {
		FallbackCheckBoxes checkBoxes = fallbackCheckBoxes.get(target);
		boolean mascot = checkBoxes.pro != null && checkBoxes.pro.isSelected();
		boolean reRoll = checkBoxes.proTrr != null && checkBoxes.proTrr.isSelected();

		if (mascot && reRoll) {
			return ReRollSources.PRO_MASCOT_TRR;
		} else if (mascot) {
			return ReRollSources.PRO_MASCOT;
		} else if (reRoll) {
			return ReRollSources.PRO_TRR;
		} else {
			return ReRollSources.PRO;
		}
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

		this.reRollSource = determineReRollSource(target, reRollSource);
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
		close();
	}

	private ReRollSource determineReRollSource(String target, ReRollSource reRollSource) {
		if (reRollSource != ReRollSources.TEAM_RE_ROLL) {
			return reRollSource;
		}
		FallbackCheckBoxes checkBoxes = fallbackCheckBoxes.get(target);
		if (blockWillUseMascot.contains(target)) {
			if (checkBoxes.trr.isSelected()) {
				return ReRollSources.MASCOT_TRR;
			} else {
				return ReRollSources.MASCOT;
			}
		} else {
			return ReRollSources.TEAM_RE_ROLL;
		}
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

	private static class Mnemonics {
		private final char team, brawler, hatred, none, anyBlockDice, trrFallback, proFallback, proTrrFallback;
		private final List<Character> pro, anyDie;

		public Mnemonics(char team, char none, char brawler, char hatred, List<Character> pro, List<Character> anyDie, char anyBlockDice, char trrFallback, char proFallback, char proTrrFallback) {
			this.team = team;
			this.none = none;
			this.brawler = brawler;
			this.hatred = hatred;
			this.pro = pro;
			this.anyDie = anyDie;
			this.anyBlockDice = anyBlockDice;
			this.trrFallback = trrFallback;
			this.proFallback = proFallback;
			this.proTrrFallback = proTrrFallback;
		}
	}

	private static class FallbackCheckBoxes {
		private JCheckBox trr, pro, proTrr;
	}
}

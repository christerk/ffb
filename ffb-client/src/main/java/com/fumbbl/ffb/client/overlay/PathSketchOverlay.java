package com.fumbbl.ffb.client.overlay;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.CoordinateConverter;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.client.ui.ColorIcon;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.ui.swing.JTextField;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.model.sketch.Sketch;
import com.fumbbl.ffb.model.sketch.SketchState;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PathSketchOverlay implements Overlay, ActionListener {

	private final FantasyFootballClient client;
	private final UserInterface userInterface;
	private final CoordinateConverter coordinateConverter;
	private final ClientSketchManager sketchManager;
	private final FieldComponent fieldComponent;
	private final PitchDimensionProvider pitchDimensionProvider;
	private final List<String> actionTargets = new ArrayList<>();
	private final JMenuItem deleteAll;
	private final JMenuItem deleteMine;
	private final JMenuItem deleteSingle;
	private final JMenuItem deleteMultiple;
	private final JMenuItem editLabel;
	private final JMenuItem editLabels;
	private final JMenuItem editColor;
	private final JMenuItem editColors;
	private JPopupMenu popupMenu;
	private int popupX;
	private int popupY;
	private FieldCoordinate previewCoordinate;
	private Color sketchColor = new Color(0, 200, 0);
	private final boolean isOnline;

	public PathSketchOverlay(FantasyFootballClient client) {
		this.client = client;
		this.userInterface = client.getUserInterface();
		this.coordinateConverter = userInterface.getCoordinateConverter();
		this.sketchManager = userInterface.getSketchManager();
		this.fieldComponent = userInterface.getFieldComponent();
		this.pitchDimensionProvider = userInterface.getPitchDimensionProvider();
		IconCache iconCache = userInterface.getIconCache();
		isOnline = this.client.getMode() != ClientMode.REPLAY || this.client.getReplayer().isOnline();

		ImageIcon deleteAllIcon = iconCache.getImageIconByProperty(IIconProperty.SKETCH_DELETE_ALL, pitchDimensionProvider);
		ImageIcon deleteIcon = iconCache.getImageIconByProperty(IIconProperty.SKETCH_DELETE, pitchDimensionProvider);
		ImageIcon editIcon = iconCache.getImageIconByProperty(IIconProperty.SKETCH_EDIT_LABEL, pitchDimensionProvider);

		this.deleteAll = new JMenuItem(pitchDimensionProvider, "Clear sketches", deleteAllIcon);
		this.deleteMine = new JMenuItem(pitchDimensionProvider, "Delete my sketches", deleteAllIcon);
		this.deleteSingle = new JMenuItem(pitchDimensionProvider, "Delete sketch", deleteIcon);
		this.deleteMultiple = new JMenuItem(pitchDimensionProvider, "Delete sketches", deleteIcon);
		this.editLabel = new JMenuItem(pitchDimensionProvider, "Edit label", editIcon);
		this.editLabels = new JMenuItem(pitchDimensionProvider, "Edit labels", editIcon);
		this.editColor = new JMenuItem(pitchDimensionProvider, "Set color");
		this.editColors = new JMenuItem(pitchDimensionProvider, "Set colors");

		this.deleteAll.addActionListener(this);
		this.deleteMine.addActionListener(this);
		this.deleteSingle.addActionListener(this);
		this.deleteMultiple.addActionListener(this);
		this.editLabel.addActionListener(this);
		this.editLabels.addActionListener(this);
		this.editColor.addActionListener(this);
		this.editColors.addActionListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_SKETCH, false);
		if (popupMenu != null && popupMenu.isVisible()) {
			return;
		}

		Set<String> newTargets = sketchManager.getSketches(e.getX(), e.getY()).stream().map(Sketch::getId).collect(Collectors.toSet());
		Set<String> oldTargets = new HashSet<>(actionTargets);
		FieldCoordinate coordinate = coordinateConverter.getFieldCoordinate(e);

		if (oldTargets.containsAll(newTargets) && newTargets.containsAll(oldTargets) && coordinate == previewCoordinate) {
			return;
		}
		actionTargets.clear();
		actionTargets.addAll(newTargets);
		previewCoordinate = coordinate;
		if (sketchManager.activeSketch().isPresent()) {
			drawSketches();
		} else {
			drawSketches(actionTargets);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (popupMenu != null && popupMenu.isVisible()) {
			return;
		}
		if (e.getButton() != MouseEvent.BUTTON1) {
			showContextMenu(e);
			return;
		}
		FieldCoordinate coordinate = coordinateConverter.getFieldCoordinate(e);
		if (coordinate == null) {
			return;
		}
		Optional<Sketch> activeSketch = sketchManager.activeSketch();
		if (e.getClickCount() > 1 && activeSketch.isPresent()) {
			sketchManager.finishSketch(coordinate);
			drawSketches();
			if (isOnline) {
				client.getCommunication().sendSketchAddCoordinate(activeSketch.get().getId(), coordinate);
			}
		} else {
			if (activeSketch.isPresent()) {
				sketchManager.add(coordinate);
				if (isOnline) {
					client.getCommunication().sendSketchAddCoordinate(activeSketch.get().getId(), coordinate);
				}
			} else {
				sketchManager.create(coordinate, sketchColor.getRGB());
				if (isOnline) {
					sketchManager.activeSketch().ifPresent(sketch ->
						client.getCommunication().sendAddSketch(sketch));
				}
			}
			drawSketches();
		}
	}

	private void drawSketches() {
		drawSketches(Collections.emptyList());
	}

	private void drawSketches(List<String> highlights) {
		Optional<Sketch> activeSketch = sketchManager.activeSketch();
		String activeSketchId = activeSketch.map(Sketch::getId).orElse(null);
		SketchState sketchState = new SketchState(sketchManager.getAllSketches(), highlights, previewCoordinate, activeSketchId);
		ModelChange modelChange = new ModelChange(ModelChangeId.SKETCH_UPDATE, null, sketchState);
		client.getGame().notifyObservers(modelChange);
	}

	private void showContextMenu(MouseEvent e) {
		List<JMenuItem> menuItems = collectActions();
		if (menuItems.isEmpty()) {
			return;
		}
		createPopupMenu(menuItems.toArray(new JMenuItem[0]), e);
	}

	private List<JMenuItem> collectActions() {
		List<JMenuItem> menuItems = new ArrayList<>();
		if (isOnline && sketchManager.hasAnySketches()) {
			menuItems.add(deleteAll);
		}
		if (sketchManager.hasOwnSketches()) {
			menuItems.add(deleteMine);
			if (sketchManager.activeSketch().isPresent()) {
				menuItems.add(deleteSingle);
				menuItems.add(editLabel);
				menuItems.add(editColor);
				editColor.setIcon(createColorIcon(sketchManager.activeSketch().get().getRgb()));
			} else if (actionTargets.size() == 1) {
				menuItems.add(deleteSingle);
				menuItems.add(editLabel);
				menuItems.add(editColor);
				sketchManager.getSketch(actionTargets.get(0))
					.ifPresent(sketch -> editColor.setIcon(createColorIcon(sketch.getRgb())));

			} else if (actionTargets.size() > 1) {
				menuItems.add(deleteMultiple);
				menuItems.add(editLabels);
				menuItems.add(editColors);
				editColors.setIcon(createColorIcon(sketchColor));
			} else {
				menuItems.add(editColor);
				editColor.setIcon(createColorIcon(sketchColor));
			}
		} else {
			menuItems.add(editColor);
			editColor.setIcon(createColorIcon(sketchColor));
		}
		return menuItems;
	}

	private ColorIcon createColorIcon(int rgb) {
		return createColorIcon(new Color(rgb));
	}

	private ColorIcon createColorIcon(Color color) {
		Dimension dimension = pitchDimensionProvider.dimension(Component.SKETCH_COLOR_ICON);
		return new ColorIcon(dimension.width, dimension.height, color);
	}

	private void createPopupMenu(JMenuItem[] pMenuItems, MouseEvent e) {
		popupMenu = new JPopupMenu();
		for (JMenuItem menuItem : pMenuItems) {
			popupMenu.add(menuItem);
		}
		popupMenu.show(fieldComponent, e.getX(), e.getY());
		popupX = e.getX();
		popupY = e.getY();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Sketch singleSketch = sketchManager.activeSketch().orElseGet(() -> {
			if (actionTargets.isEmpty()) {
				return null;
			} else {
				return sketchManager.getSketch(actionTargets.get(0)).orElse(null);
			}
		});


		if (e.getSource() == deleteAll) {
			sketchManager.clearAll();
			drawSketches();
			removeMenu();
			if (isOnline) {
				client.getCommunication().sendClearSketches();
			}
		} else if (e.getSource() == deleteMine) {
			sketchManager.clearOwn();
			drawSketches(actionTargets);
			removeMenu();
			if (isOnline) {
				client.getCommunication().sendRemoveSketches(null);
			}
		} else if (e.getSource() == deleteSingle) {
			if (singleSketch != null) {
				sketchManager.remove(singleSketch);
				drawSketches(actionTargets);
				removeMenu();
				if (isOnline) {
					client.getCommunication().sendRemoveSketches(Collections.singletonList(singleSketch.getId()));
				}
			}
		} else if (e.getSource() == deleteMultiple) {
			for (String sketchId : actionTargets) {
				sketchManager.remove(sketchId);
			}
			drawSketches(actionTargets);
			removeMenu();
			if (isOnline) {
				client.getCommunication().sendRemoveSketches(actionTargets);
			}
		} else if (e.getSource() == editLabel) {
			if (singleSketch == null) {
				return; // nothing to edit
			}
			String label = singleSketch.getLabel();
			createLabelPopup(label, popupX, popupY);
		} else if (e.getSource() == editLabels) {
			createLabelPopup(null, popupX, popupY);
		} else if (e.getSource() == editColor) {
			int rgb = singleSketch != null ? singleSketch.getRgb() : sketchColor.getRGB();
			Color color = new Color(rgb);
			Color newColor = JColorChooser.showDialog(fieldComponent, "Select color", color);
			if (newColor != null && newColor != color) {
				if (singleSketch == null) {
					sketchColor = newColor;
				} else {
					singleSketch.setRgb(newColor.getRGB());
					drawSketches();
					if (isOnline) {
						client.getCommunication().sendSketchSetColor(Collections.singletonList(singleSketch.getId()), newColor.getRGB());
					}
				}
			}
		} else if (e.getSource() == editColors) {
			Color newColor = JColorChooser.showDialog(fieldComponent, "Select color", sketchColor);
			if (newColor != null) {
				actionTargets.stream().map(sketchManager::getSketch).filter(Optional::isPresent).map(Optional::get).forEach(target -> target.setRgb(newColor.getRGB()));
				drawSketches();
				if (isOnline) {
					client.getCommunication().sendSketchSetColor(actionTargets, newColor.getRGB());
				}
			}
		}
	}

	private void removeMenu() {
		popupMenu.setVisible(false);
		popupMenu = null;
	}


	private void createLabelPopup(String existingLabel, int pX, int pY) {
		popupMenu.setVisible(false);
		popupMenu = new JPopupMenu();
		popupMenu.add(new JLabel(pitchDimensionProvider, "Label"));

		popupMenu.setLayout(new BoxLayout(popupMenu, BoxLayout.X_AXIS));
		popupMenu.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		JTextField labelField = new JTextField(pitchDimensionProvider, 7);
		if (StringTool.isProvided(existingLabel)) {
			labelField.setText(existingLabel);
		}
		popupMenu.add(Box.createHorizontalStrut(5));
		popupMenu.add(labelField);
		popupMenu.show(fieldComponent, pX, pY);
		labelField.selectAll();
		labelField.requestFocus();
		labelField.addActionListener(e -> {
			Optional<Sketch> activeSketch = sketchManager.activeSketch();
			if (activeSketch.isPresent()) {
				activeSketch.get().setLabel(labelField.getText());
				if (isOnline) {
					client.getCommunication().sendSketchSetLabel(Collections.singletonList(activeSketch.get().getId()), labelField.getText());
				}
			} else {
				actionTargets.stream().map(sketchManager::getSketch)
					.filter(Optional::isPresent).map(Optional::get)
					.forEach(target -> target.setLabel(labelField.getText()));
				if (isOnline) {
					client.getCommunication().sendSketchSetLabel(actionTargets, labelField.getText());
				}
			}
			drawSketches();
			removeMenu();
		});
	}
}

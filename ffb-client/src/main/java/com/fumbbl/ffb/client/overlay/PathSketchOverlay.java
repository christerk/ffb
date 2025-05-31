package com.fumbbl.ffb.client.overlay;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.CoordinateConverter;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.ui.swing.JTextField;
import com.fumbbl.ffb.model.sketch.Sketch;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PathSketchOverlay implements Overlay, ActionListener {

	private final CoordinateConverter coordinateConverter;
	private final ClientSketchManager sketchManager;
	private final FieldComponent fieldComponent;
	private final PitchDimensionProvider pitchDimensionProvider;
	private final List<Sketch> actionTargets = new ArrayList<>();
	private final JMenuItem deleteAll;
	private final JMenuItem deleteSingle;
	private final JMenuItem deleteMultiple;
	private final JMenuItem editLabel;
	private final JMenuItem editLabels;
	private JPopupMenu popupMenu;
	private int popupX;
	private int popupY;
	private FieldCoordinate previewCoordinate;

	public PathSketchOverlay(CoordinateConverter coordinateConverter, FieldComponent fieldComponent, ClientSketchManager sketchManager, PitchDimensionProvider pitchDimensionProvider) {
		this.coordinateConverter = coordinateConverter;
		this.sketchManager = sketchManager;
		this.fieldComponent = fieldComponent;
		this.pitchDimensionProvider = pitchDimensionProvider;
		this.deleteAll = new JMenuItem(pitchDimensionProvider, "Clear all sketches");
		this.deleteSingle = new JMenuItem(pitchDimensionProvider, "Clear sketch");
		this.deleteMultiple = new JMenuItem(pitchDimensionProvider, "Clear sketches");
		this.editLabel = new JMenuItem(pitchDimensionProvider, "Edit label");
		this.editLabels = new JMenuItem(pitchDimensionProvider, "Edit labels");
		this.deleteAll.addActionListener(this);
		this.deleteSingle.addActionListener(this);
		this.deleteMultiple.addActionListener(this);
		this.editLabel.addActionListener(this);
		this.editLabels.addActionListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

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
		if (popupMenu != null && popupMenu.isVisible()) {
			return;
		}

		Set<Sketch> newTargets = sketchManager.getSketches(e.getX(), e.getY());
		Set<Sketch> oldTargets = new HashSet<>(actionTargets);
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
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1) {
			showContextMenu(e);
			return;
		}
		FieldCoordinate coordinate = coordinateConverter.getFieldCoordinate(e);
		if (coordinate == null) {
			return;
		}
		Optional<Sketch> activeSketch = sketchManager.activeSketch();
		if (e.getClickCount() > 1) {
			if (activeSketch.isPresent()) {
				sketchManager.finishSketch(coordinate);
			} else {
				sketchManager.create(coordinate, new Color(0, 200, 0).getRGB());
			}
			drawSketches();
		} else if (activeSketch.isPresent()) {
			sketchManager.add(coordinate);
			drawSketches();
		}
	}

	private void drawSketches() {
		drawSketches(Collections.emptyList());
	}

	private void drawSketches(List<Sketch> highlights) {
		fieldComponent.getLayerSketches().draw(highlights, previewCoordinate);
		fieldComponent.refresh();
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
		if (sketchManager.hasSketches()) {
			menuItems.add(deleteAll);
			if (sketchManager.activeSketch().isPresent()) {
				menuItems.add(deleteSingle);
				menuItems.add(editLabel);
			} else if (actionTargets.size() == 1) {
				menuItems.add(deleteSingle);
				menuItems.add(editLabel);
			} else if (actionTargets.size() > 1) {
				menuItems.add(deleteMultiple);
				menuItems.add(editLabels);
			}
		}
		return menuItems;
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
		if (e.getSource() == deleteAll) {
			sketchManager.clear();
			drawSketches(actionTargets);
			removeMenu();
		} else if (e.getSource() == deleteSingle) {
			if (sketchManager.activeSketch().isPresent()) {
				sketchManager.remove(sketchManager.activeSketch().get());
			} else {
				sketchManager.remove(actionTargets.get(0));
			}
			drawSketches(actionTargets);
			removeMenu();
		} else if (e.getSource() == deleteMultiple) {
			for (Sketch sketch : actionTargets) {
				sketchManager.remove(sketch);
			}
			drawSketches(actionTargets);
			removeMenu();
		} else if (e.getSource() == editLabel) {
			String label = sketchManager.activeSketch().orElseGet(() -> actionTargets.get(0)).getLabel();
			createLabelPopup(label, popupX, popupY);
		} else if (e.getSource() == editLabels) {
			createLabelPopup(null, popupX, popupY);
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
			if (sketchManager.activeSketch().isPresent()) {
				sketchManager.activeSketch().get().setLabel(labelField.getText());
			} else {
				for (Sketch actionTarget : actionTargets) {
					actionTarget.setLabel(labelField.getText());
				}
			}
			drawSketches();
			removeMenu();
		});
	}
}

package com.fumbbl.ffb.client.overlay;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.CoordinateConverter;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.ui.swing.JTextField;
import com.fumbbl.ffb.model.sketch.ClientSketchManager;
import com.fumbbl.ffb.model.sketch.Sketch;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PathSketchOverlay implements Overlay, ActionListener, PopupMenuListener {

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
	private final JMenuItem setActive;
	private JPopupMenu popupMenu;

	public PathSketchOverlay(CoordinateConverter coordinateConverter, FieldComponent fieldComponent, ClientSketchManager sketchManager, PitchDimensionProvider pitchDimensionProvider) {
		this.coordinateConverter = coordinateConverter;
		this.sketchManager = sketchManager;
		this.fieldComponent = fieldComponent;
		this.pitchDimensionProvider = pitchDimensionProvider;
		this.deleteAll = new JMenuItem(pitchDimensionProvider, "Clear all sketches");
		this.deleteSingle = new JMenuItem(pitchDimensionProvider, "Clear sketch");
		this.deleteMultiple = new JMenuItem(pitchDimensionProvider, "Clear sketch(es)");
		this.editLabel = new JMenuItem(pitchDimensionProvider, "Edit label");
		this.editLabels = new JMenuItem(pitchDimensionProvider, "Edit labels");
		this.setActive = new JMenuItem(pitchDimensionProvider, "Modify sketch");
		this.deleteAll.addActionListener(this);
		this.deleteSingle.addActionListener(this);
		this.deleteMultiple.addActionListener(this);
		this.editLabel.addActionListener(this);
		this.editLabels.addActionListener(this);
		this.setActive.addActionListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

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
		fieldComponent.getLayerSketches().draw();
		fieldComponent.refresh();
	}

	private void showContextMenu(MouseEvent e) {
		List<JMenuItem> menuItems = collectActions(e);
		if (menuItems.isEmpty()) {
			return;
		}
		createPopupMenu(menuItems.toArray(new JMenuItem[0]), e);
	}

	private List<JMenuItem> collectActions(MouseEvent e) {
		FieldCoordinate coordinate = coordinateConverter.getFieldCoordinate(e);

		List<JMenuItem> menuItems = new ArrayList<>();
		actionTargets.addAll(sketchManager.getSketches(coordinate));
		if (sketchManager.hasSketches()) {
			menuItems.add(deleteAll);
			if (actionTargets.size() == 1) {
				menuItems.add(deleteSingle);
				menuItems.add(editLabel);
				if (actionTargets.get(0) == sketchManager.activeSketch().orElse(null)) {
					menuItems.add(setActive);
				}
			} else if (actionTargets.size() > 1) {
				menuItems.add(deleteMultiple);
				menuItems.add(editLabels);
			}
		}
		return menuItems;
	}

	private void createPopupMenu(JMenuItem[] pMenuItems, MouseEvent e) {
		popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(this);
		for (JMenuItem menuItem : pMenuItems) {
			popupMenu.add(menuItem);
		}
		popupMenu.show(fieldComponent, e.getX(), e.getY());
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == deleteAll) {
			sketchManager.clear();
			drawSketches();
			removeMenu();
		} else if (e.getSource() == deleteSingle) {
			sketchManager.remove(actionTargets.get(0));
			drawSketches();
			removeMenu();
		} else if (e.getSource() == deleteMultiple) {
			for (Sketch sketch : actionTargets) {
				sketchManager.remove(sketch);
			}
			drawSketches();
			removeMenu();
		} else if (e.getSource() == editLabel) {
			Point location = popupMenu.getLocation();
			createLabelPopup(actionTargets.get(0).getLabel(), location.x, location.y);
		} else if (e.getSource() == editLabels) {
			Point location = popupMenu.getLocation();
			createLabelPopup(null, location.x, location.y);
		} else if (e.getSource() == setActive) {
			sketchManager.setActive(actionTargets.get(0));
			removeMenu();
		}
	}

	private void removeMenu() {
		popupMenu.setVisible(false);
		popupMenu = null;
		actionTargets.clear();
	}


	private void createLabelPopup(String existingLabel, int pX, int pY) {
		popupMenu.setVisible(false);
		popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(this);
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
			for (Sketch actionTarget : actionTargets) {
				actionTarget.setLabel(labelField.getText());
			}
			drawSketches();
			removeMenu();
		});
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
		actionTargets.clear();
	}
}

package com.balancedbytes.games.ffb.client.dialog.inducements;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.balancedbytes.games.ffb.inducement.InducementType;

public class DropDownPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -1914526447650181630L;
	private final int fMax;
	private final int fCost;
	private final JComboBox<String> fBox;
	private final ActionListener fActionListener;
	private int fAmountSelected = 0;
	private boolean fHandleEvents = true;
	private final InducementType fInducementType;

	public DropDownPanel(InducementType pInducementType, int pMax, String pText, int pCost, ActionListener pListener,
			int pAvailableGold) {
		super();
		fInducementType = pInducementType;
		fMax = pMax;
		fCost = pCost;
		boolean fAvailable = pMax != 0;
		fActionListener = pListener;

		List<String> anzahl = new ArrayList<>();
		for (int i = 0; i <= fMax && i * fCost <= pAvailableGold; i++) {
			anzahl.add(Integer.toString(i));
		}
		fBox = new JComboBox<>(anzahl.toArray(new String[0]));
		fBox.setSelectedIndex(0);
		fBox.setMaximumSize(fBox.getMinimumSize());
		fBox.addActionListener(pListener);
		fBox.setEnabled(fAvailable);
		fBox.addActionListener(this);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(fBox);
		add(Box.createHorizontalStrut(10));
		JLabel label = new JLabel(
				pText + " (Max: " + fMax + "  " + formatGold(fCost) + " Gold" + (pMax > 1 ? " each)" : ")"));
		add(label);
		add(Box.createHorizontalGlue());

		setMaximumSize(new Dimension(getMaximumSize().width, getMinimumSize().height));

	}

	public InducementType getInducementType() {
		return fInducementType;
	}

	public int getSelectedAmount() {
		return fAmountSelected;
	}

	public void reset(int pAvailableGold) {
		fAmountSelected = 0;
		fBox.setSelectedIndex(0);
		availableGoldChanged(pAvailableGold);
	}

	private String formatGold(int pAmount) {
		StringBuilder buf = new StringBuilder(Integer.toString(pAmount)).reverse();
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < buf.length(); i++) {
			result.insert(0, buf.charAt(i));
			if ((i + 1) % 3 == 0 && i + 1 < buf.length()) {
				result.insert(0, ",");
			}
		}
		return result.toString();
	}

	public void availableGoldChanged(int pAvailableGold) {
		fHandleEvents = false;
		List<String> anzahl = new ArrayList<>();
		if ((fMax - fAmountSelected) * fCost > pAvailableGold || fBox.getItemCount() < fMax + 1) {
			if (fBox.isEnabled()) {
				for (int i = 0; i <= fAmountSelected; i++) {
					anzahl.add(Integer.toString(i));
				}
				for (int i = fAmountSelected + 1; i <= fMax && (i - fAmountSelected) * fCost <= pAvailableGold; i++) {
					anzahl.add(Integer.toString(i));
				}
				if (fBox.getItemCount() > anzahl.size()) {
					for (int i = fBox.getItemCount(); i > anzahl.size(); i--) {
						fBox.removeItemAt(i - 1);
					}
				} else {
					for (int i = fBox.getItemCount() + 1; i <= anzahl.size(); i++) {
						fBox.addItem(anzahl.get(i - 1));
					}
				}
			}
		}
		fHandleEvents = true;
	}

	public int getCurrentCost() {
		return fAmountSelected * fCost;

	}

	public void actionPerformed(ActionEvent e) {
		if (fHandleEvents) {
			fAmountSelected = Integer.parseInt(fBox.getItemAt(fBox.getSelectedIndex()));
			fActionListener.actionPerformed(e);
		}
	}
}

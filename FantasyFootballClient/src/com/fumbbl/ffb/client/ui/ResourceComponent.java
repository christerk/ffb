package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.factory.bb2020.PrayerFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Kalimar
 */
public class ResourceComponent extends JPanel {

	public static final int WIDTH = 145;
	public static final int HEIGHT = 168; // 256

	private final SideBarComponent fSideBar;
	private final BufferedImage fImage;
	private boolean fRefreshNecessary;
	private int fNrOfSlots, fCurrentReRolls, fCurrentApothecaries, fCurrentCards, currentPrayers;
	private final ResourceSlot[] fSlots;

	private final Map<InducementType, Integer> inducementValues = new HashMap<>();

	private static final int _SLOT_HEIGHT = 40;
	private static final int _SLOT_WIDTH = 46;

	private static final Font _NUMBER_FONT = new Font("Sans Serif", Font.BOLD, 16);

	public ResourceComponent(SideBarComponent pSideBar) {
		fSideBar = pSideBar;
		fImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		fSlots = createResourceSlots();
		fRefreshNecessary = true;
		setLayout(null);
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	private ResourceSlot[] createResourceSlots() {
		ResourceSlot[] resourceSlots;
		if (getSideBar().isHomeSide()) {
			resourceSlots = new ResourceSlot[]{
				new ResourceSlot(new Rectangle(0, 3 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, 3 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(2 * (_SLOT_WIDTH + 2), 3 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(0, 2 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, 2 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(2 * (_SLOT_WIDTH + 2), 2 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(0, (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(2 * (_SLOT_WIDTH + 2), (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(0, 0, _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, 0, _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(2 * (_SLOT_WIDTH + 2), 0, _SLOT_WIDTH, _SLOT_HEIGHT))};
		} else {
			resourceSlots = new ResourceSlot[]{
				new ResourceSlot(new Rectangle(2 * (_SLOT_WIDTH + 2), 3 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, 3 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(0, 3 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(2 * (_SLOT_WIDTH + 2), 2 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, 2 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(0, 2 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(2 * (_SLOT_WIDTH + 2), (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(0, (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(2 * (_SLOT_WIDTH + 2), 0, _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, 0, _SLOT_WIDTH, _SLOT_HEIGHT)),
				new ResourceSlot(new Rectangle(0, 0, _SLOT_WIDTH, _SLOT_HEIGHT))};
		}
		return resourceSlots;
	}

	public SideBarComponent getSideBar() {
		return fSideBar;
	}

	private void drawBackground() {
		Graphics2D g2d = fImage.createGraphics();
		IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
		BufferedImage background;
		if (getSideBar().isHomeSide()) {
			background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_RESOURCE_RED);
		} else {
			background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_RESOURCE_BLUE);
		}
		g2d.drawImage(background, 0, 0, null);
		g2d.dispose();
	}

	private void drawSlot(ResourceSlot pSlot) {
		if ((pSlot != null) && (pSlot.getIconProperty() != null)) {
			IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
			Graphics2D g2d = fImage.createGraphics();
			int x = pSlot.getLocation().x;
			int y = pSlot.getLocation().y;
			BufferedImage resourceIcon = iconCache.getIconByProperty(pSlot.getIconProperty());
			if (getSideBar().isHomeSide()) {
				x += pSlot.getLocation().width - resourceIcon.getWidth() - 1;
			} else {
				x += 1;
			}
			y += (pSlot.getLocation().height - resourceIcon.getHeight() + 1) / 2;
			g2d.drawImage(resourceIcon, x, y, null);
			if (!pSlot.isEnabled()) {
				BufferedImage disabledIcon = iconCache.getIconByProperty(IIconProperty.DECORATION_STUNNED);
				x += (resourceIcon.getWidth() - disabledIcon.getWidth()) / 2;
				y += (resourceIcon.getHeight() - disabledIcon.getHeight()) / 2;
				g2d.drawImage(disabledIcon, x, y, null);
			}
			g2d.setFont(_NUMBER_FONT);
			String resourceValue = Integer.toString(pSlot.getValue());
			FontMetrics metrics = g2d.getFontMetrics();
			Rectangle2D bounds = metrics.getStringBounds(resourceValue, g2d);
			y = pSlot.getLocation().y + ((pSlot.getLocation().height + metrics.getHeight()) / 2) - metrics.getDescent();
			if (getSideBar().isHomeSide()) {
				x = pSlot.getLocation().x + 3;
			} else {
				x = pSlot.getLocation().x + pSlot.getLocation().width - (int) bounds.getWidth() - 3;
			}
			g2d.setColor(Color.BLACK);
			g2d.drawString(resourceValue, x + 1, y + 1);
			g2d.setColor(Color.WHITE);
			g2d.drawString(resourceValue, x, y);
			g2d.dispose();
		}
	}

	private void updateSlots() {

		AtomicInteger slotIndex = new AtomicInteger(0);
		Game game = getSideBar().getClient().getGame();
		TurnData turnData = getSideBar().isHomeSide() ? game.getTurnDataHome() : game.getTurnDataAway();
		Team team = getSideBar().isHomeSide() ? game.getTeamHome() : game.getTeamAway();

		Arrays.stream(fSlots).forEach(ResourceSlot::clearDetails);

		fRefreshNecessary |= (turnData.getReRolls() != fCurrentReRolls);
		fCurrentReRolls = turnData.getReRolls();
		if ((team.getReRolls() > 0) || (turnData.getReRolls() > 0)) {
			ResourceSlot reRollSlot = fSlots[slotIndex.getAndIncrement()];
			reRollSlot.setSingular("Re-Roll");
			reRollSlot.setPlural("Re-Rolls");
			fRefreshNecessary |= (turnData.isReRollUsed() == reRollSlot.isEnabled());
			reRollSlot.setEnabled(!turnData.isReRollUsed());
			reRollSlot.setValue(fCurrentReRolls);
			reRollSlot.setIconProperty(IIconProperty.RESOURCE_RE_ROLL);
		}

		fRefreshNecessary |= (turnData.getApothecaries() != fCurrentApothecaries);
		fCurrentApothecaries = turnData.getApothecaries();
		if ((team.getApothecaries() > 0) || (turnData.getApothecaries() > 0)) {
			ResourceSlot apothecarySlot = fSlots[slotIndex.getAndIncrement()];
			apothecarySlot.setSingular("Apothecary");
			apothecarySlot.setPlural("Apothecaries");
			apothecarySlot.setValue(fCurrentApothecaries);
			apothecarySlot.setIconProperty(IIconProperty.RESOURCE_APOTHECARY);
		}


		turnData.getInducementSet().getInducementMapping().entrySet().stream()
			.filter(entry -> entry.getValue() != null && entry.getKey().isUsingGenericSlot())
			.sorted(Comparator.comparing(o -> o.getKey().getName()))
			.forEach(entry -> {
				InducementType type = entry.getKey();
				Inducement inducement = entry.getValue();

				int newValue = inducement.getValue() - inducement.getUses();
				fRefreshNecessary |= (inducementValues.get(type) != null ? (newValue != inducementValues.get(type)) : newValue > 0);
				inducementValues.put(type, newValue);
				if (newValue > 0) {
					ResourceSlot slot = fSlots[slotIndex.getAndIncrement()];
					slot.setPlural(type.getPlural());
					slot.setSingular(type.getSingular());
					slot.setValue(newValue);
					slot.setIconProperty(type.getSlotIconProperty());
				}
			});

		Card[] availableCards = turnData.getInducementSet().getAvailableCards();
		fRefreshNecessary |= (availableCards.length != fCurrentCards);
		fCurrentCards = availableCards.length;
		if (fCurrentCards > 0) {
			ResourceSlot cardsSlot = fSlots[slotIndex.getAndIncrement()];
			cardsSlot.setPlural("Cards");
			cardsSlot.setSingular("Card");
			cardsSlot.setValue(fCurrentCards);
			cardsSlot.setIconProperty(IIconProperty.RESOURCE_CARD);
		}

		Set<Prayer> prayers = turnData.getInducementSet().getPrayers();
		fRefreshNecessary |= (prayers.size() != currentPrayers);
		currentPrayers = prayers.size();
		if (currentPrayers > 0) {
			ResourceSlot prayerSlot = fSlots[slotIndex.getAndIncrement()];
			prayerSlot.setPlural("Prayers");
			prayerSlot.setSingular("Prayer");
			prayerSlot.setValue(currentPrayers);
			prayerSlot.setIconProperty(IIconProperty.RESOURCE_PRAYER);
			PrayerFactory prayerFactory = game.getFactory(FactoryType.Factory.PRAYER);
			prayerFactory.sort(prayers).stream().map(Prayer::getName).forEach(prayerSlot::addDetail);
		}

		fNrOfSlots = slotIndex.get();

	}

	public void init() {
		fRefreshNecessary = true;
		refresh();
	}

	public void refresh() {
		Game game = getSideBar().getClient().getGame();
		if (game.getHalf() > 0) {
			updateSlots();
			if (fRefreshNecessary) {
				drawBackground();
				for (int i = 0; i < fNrOfSlots; i++) {
					drawSlot(fSlots[i]);
				}
				repaint();
				fRefreshNecessary = false;
			}
		} else {
			drawBackground();
			repaint();
		}
	}

	protected void paintComponent(Graphics pGraphics) {
		pGraphics.drawImage(fImage, 0, 0, null);
	}

	public String getToolTipText(MouseEvent pMouseEvent) {
		String toolTip = null;
		for (int i = 0; (toolTip == null) && (i < fSlots.length); i++) {
			if (fSlots[i].getLocation().contains(pMouseEvent.getPoint())) {
				toolTip = fSlots[i].getToolTip();
			}
		}
		return toolTip;
	}

}

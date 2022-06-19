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
import com.fumbbl.ffb.model.TurnData;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Kalimar
 */
public class ResourceComponent extends JPanel {

	public static final int WIDTH = 145;
	public static final int HEIGHT = 168; // 256
	private static final int _SLOT_HEIGHT = 40;
	private static final int _SLOT_WIDTH = 46;
	private static final int COUNTER_SIZE = 15;

	private final SideBarComponent fSideBar;
	private final BufferedImage fImage;
	private boolean fRefreshNecessary;
	private int fNrOfSlots, fCurrentReRolls, fCurrentApothecaries, fCurrentCards, currentPrayers, currentSingleUseReRolls, currentPlagueDoctors;
	private final ResourceSlot[] fSlots;

	private final Map<InducementType, Integer> inducementValues = new HashMap<>();

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
				g2d.drawImage(disabledIcon, x + (resourceIcon.getWidth() - disabledIcon.getWidth()) / 2,
					y + (resourceIcon.getHeight() - disabledIcon.getHeight()) / 2, null);
			}

			List<ResourceValue> values = pSlot.getValues();
			for (int i = 0; i < Math.min(4, values.size()); i++) {
				ResourceValue resourceValue = values.get(i);
				if (resourceValue.getValue() > 1 || (values.size() > 1 && resourceValue.getValue() == 1)) {
					drawCounter(iconCache, g2d, x, y, resourceValue, offset(pSlot.getLocation(), i));
				}
			}
			g2d.dispose();
		}
	}

	private void drawCounter(IconCache iconCache, Graphics2D g2d, int x, int y, ResourceValue resourceValue, Dimension offset) {
			Rectangle counterCrop = counterCrop(Math.min(resourceValue.getValue() - 1, 15));
			BufferedImage counter = iconCache.getIconByProperty(IIconProperty.RESOURCE_COUNTER_SPRITE)
				.getSubimage(counterCrop.x, counterCrop.y, counterCrop.width, counterCrop.height);

			g2d.drawImage(counter, x + offset.width, y + offset.height, null);
	}

	private Dimension offset(Rectangle location, int index) {
		int width = index % 2 == 0 ? location.width - COUNTER_SIZE - 5 : 0;
		int height = index < 2 ? location.height - COUNTER_SIZE : 0;
		return new Dimension(width, height);
	}

	private Rectangle counterCrop(int elementIndex) {
		int row = elementIndex / 4;
		int column = elementIndex % 4;

		return new Rectangle(column * COUNTER_SIZE, row * COUNTER_SIZE, COUNTER_SIZE, COUNTER_SIZE);
	}

	private void updateSlots() {

		AtomicInteger slotIndex = new AtomicInteger(0);
		Game game = getSideBar().getClient().getGame();
		TurnData turnData = getSideBar().isHomeSide() ? game.getTurnDataHome() : game.getTurnDataAway();

		Arrays.stream(fSlots).forEach(ResourceSlot::clear);

		fRefreshNecessary |= (turnData.getReRolls() != fCurrentReRolls);
		fRefreshNecessary |= turnData.getSingleUseReRolls() != currentSingleUseReRolls;
		fCurrentReRolls = turnData.getReRolls();
		currentSingleUseReRolls = turnData.getSingleUseReRolls();
		if (fCurrentReRolls + currentSingleUseReRolls > 0) {
			ResourceSlot reRollSlot = fSlots[slotIndex.getAndIncrement()];
			fRefreshNecessary |= (turnData.isReRollUsed() == reRollSlot.isEnabled());
			reRollSlot.setEnabled(!turnData.isReRollUsed());
			reRollSlot.add(new ResourceValue(fCurrentReRolls, "Re-Roll", "Re-Rolls"));
			if (currentSingleUseReRolls > 0) {
				reRollSlot.add(new ResourceValue(currentSingleUseReRolls, "Lord of Chaos", "Lords of Chaos"));
			}
			reRollSlot.setIconProperty(IIconProperty.RESOURCE_RE_ROLL);
		}

		fRefreshNecessary |= (turnData.getApothecaries() != fCurrentApothecaries);
		fRefreshNecessary |= turnData.getPlagueDoctors() != currentPlagueDoctors;
		fCurrentApothecaries = turnData.getApothecaries();
		currentPlagueDoctors = turnData.getPlagueDoctors();
		if (fCurrentApothecaries + currentPlagueDoctors > 0) {
			ResourceSlot apothecarySlot = fSlots[slotIndex.getAndIncrement()];
			if (fCurrentApothecaries > 0) {
				apothecarySlot.add(new ResourceValue(fCurrentApothecaries, "Apothecary", "Apothecaries"));
			}
			if (currentPlagueDoctors > 0) {
				apothecarySlot.add(new ResourceValue(currentPlagueDoctors, "Plague Doctor", "Plague Doctors"));
			}
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
					ResourceSlot slot = Arrays.stream(fSlots).limit(slotIndex.get())
						.filter(existingSlot -> existingSlot.getIconProperty().equalsIgnoreCase(type.getSlotIconProperty()))
						.findFirst()
						.orElse(fSlots[slotIndex.getAndIncrement()]);
					slot.add(new ResourceValue(newValue, type.getSingular(), type.getPlural()));
					slot.setIconProperty(type.getSlotIconProperty());
				}
			});

		Card[] availableCards = turnData.getInducementSet().getAvailableCards();
		fRefreshNecessary |= (availableCards.length != fCurrentCards);
		fCurrentCards = availableCards.length;
		if (fCurrentCards > 0) {
			ResourceSlot cardsSlot = fSlots[slotIndex.getAndIncrement()];
			cardsSlot.add(new ResourceValue(fCurrentCards, "Card", "Cards"));
			cardsSlot.setIconProperty(IIconProperty.RESOURCE_CARD);
		}

		Set<Prayer> prayers = turnData.getInducementSet().getPrayers();
		fRefreshNecessary |= (prayers.size() != currentPrayers);
		currentPrayers = prayers.size();
		if (currentPrayers > 0) {
			ResourceSlot prayerSlot = fSlots[slotIndex.getAndIncrement()];
			prayerSlot.add(new ResourceValue(currentPrayers, "Prayer", "Prayers"));
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

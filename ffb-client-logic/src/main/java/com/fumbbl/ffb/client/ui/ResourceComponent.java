package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.factory.bb2020.PrayerFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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

    private final SideBarComponent fSideBar;
    private BufferedImage fImage;
    private boolean fRefreshNecessary;
    private int fNrOfSlots, fCurrentReRolls, fCurrentApothecaries, fCurrentCards, currentPrayers, currentSingleUseReRolls, currentPlagueDoctors;
    private final ResourceSlot[] fSlots;

    private final Map<InducementType, Integer> inducementValues = new HashMap<>();

    private final DimensionProvider dimensionProvider;
    private final StyleProvider styleProvider;
    private Dimension size;

    public ResourceComponent(SideBarComponent pSideBar, DimensionProvider dimensionProvider, StyleProvider styleProvider) {
        this.dimensionProvider = dimensionProvider;
        fSideBar = pSideBar;
        fSlots = createResourceSlots();
        fRefreshNecessary = true;
        this.styleProvider = styleProvider;
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    public void initLayout() {
        size = dimensionProvider.dimension(Component.RESOURCE, RenderContext.UI);
        fImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        setLayout(null);
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

    private ResourceSlot[] createResourceSlots() {
        ResourceSlot[] resourceSlots;
        Dimension dimension = dimensionProvider.unscaledDimension(Component.RESOURCE_SLOT);
        if (getSideBar().isHomeSide()) {
            resourceSlots = new ResourceSlot[]{
                    new ResourceSlot(new Rectangle(0, 3 * (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(dimension.width + 2, 3 * (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(2 * (dimension.width + 2), 3 * (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(0, 2 * (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(dimension.width + 2, 2 * (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(2 * (dimension.width + 2), 2 * (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(0, (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(dimension.width + 2, (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(2 * (dimension.width + 2), (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(0, 0, dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(dimension.width + 2, 0, dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(2 * (dimension.width + 2), 0, dimension.width, dimension.height))};
        } else {
            resourceSlots = new ResourceSlot[]{
                    new ResourceSlot(new Rectangle(2 * (dimension.width + 2), 3 * (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(dimension.width + 2, 3 * (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(0, 3 * (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(2 * (dimension.width + 2), 2 * (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(dimension.width + 2, 2 * (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(0, 2 * (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(2 * (dimension.width + 2), (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(dimension.width + 2, (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(0, (dimension.height + 2), dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(2 * (dimension.width + 2), 0, dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(dimension.width + 2, 0, dimension.width, dimension.height)),
                    new ResourceSlot(new Rectangle(0, 0, dimension.width, dimension.height))};
        }
        return resourceSlots;
    }

    public SideBarComponent getSideBar() {
        return fSideBar;
    }

    private void drawBackground() {
        Graphics2D g2d = fImage.createGraphics();
        if (styleProvider.getFrameBackground() == null) {
            IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
            BufferedImage background;
            String swapSetting = fSideBar.getClient().getProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS);
            boolean swapColors = IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_ON.equals(swapSetting);

            boolean homeSide = getSideBar().isHomeSide();
            if (swapColors) {
                homeSide = !homeSide;
            }
            if (homeSide) {
                background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_RESOURCE_RED, RenderContext.UI);
            } else {
                background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_RESOURCE_BLUE, RenderContext.UI);
            }
            g2d.drawImage(background, 0, 0, size.width, size.height, null);
        } else {
            g2d.setColor(styleProvider.getFrameBackground());
            g2d.fillRect(0, 0, size.width, size.height);
        }
        g2d.dispose();
    }

    private void drawSlot(ResourceSlot pSlot) {
        if ((pSlot != null) && (pSlot.getIconProperty() != null)) {
            IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
            ScaledSlot scaledSlot = scaledSlot(pSlot, iconCache);
            Graphics2D g2d = fImage.createGraphics();
            g2d.drawImage(scaledSlot.resourceIcon, scaledSlot.area.x, scaledSlot.area.y, scaledSlot.area.width, scaledSlot.area.height, null);
            if (!pSlot.isEnabled()) {
                BufferedImage disabledIcon = iconCache.getIconByProperty(IIconProperty.DECORATION_STUNNED, RenderContext.UI);
                g2d.drawImage(disabledIcon, scaledSlot.area.x + (scaledSlot.area.width - disabledIcon.getWidth()) / 2,
                        scaledSlot.area.y + (scaledSlot.area.height - disabledIcon.getHeight()) / 2, null);
            }

            List<ResourceValue> values = pSlot.getValues();
            for (int i = 0; i < Math.min(4, values.size()); i++) {
                ResourceValue resourceValue = values.get(i);
                if (resourceValue.getValue() > 1 || (values.size() > 1 && resourceValue.getValue() == 1)) {
                    drawCounter(iconCache, g2d, scaledSlot.origin.x, scaledSlot.origin.y, resourceValue, offset(pSlot.getLocation(), i));
                }
            }
            g2d.dispose();
        }
    }


    private void drawCounter(IconCache iconCache, Graphics2D g2d, int x, int y, ResourceValue resourceValue, Dimension offset) {
        Rectangle counterCrop = counterCrop(Math.min(resourceValue.getValue() - 1, 15));
        BufferedImage counter = iconCache.getUnscaledIconByProperty(IIconProperty.RESOURCE_COUNTER_SPRITE)
                .getSubimage(counterCrop.x, counterCrop.y, counterCrop.width, counterCrop.height);

        Dimension counterSize = dimensionProvider.dimension(Component.INDUCEMENT_COUNTER_SIZE, RenderContext.UI);
        g2d.drawImage(counter, dimensionProvider.scale(x + offset.width, RenderContext.UI), dimensionProvider.scale(y + offset.height, RenderContext.UI), counterSize.width, counterSize.height, null);
    }

    private Dimension offset(Rectangle location, int index) {
        Dimension counterSize = dimensionProvider.unscaledDimension(Component.INDUCEMENT_COUNTER_SIZE);
        int width = index % 2 == 0 ? location.width - counterSize.width - 5 : 0;
        int height = index < 2 ? location.height - counterSize.height : 0;
        return new Dimension(width, height);
    }

    private Rectangle counterCrop(int elementIndex) {
        int row = elementIndex / 4;
        int column = elementIndex % 4;
        Dimension counterSize = dimensionProvider.unscaledDimension(Component.INDUCEMENT_COUNTER_SIZE);
        return new Rectangle(column * counterSize.width, row * counterSize.height, counterSize.width, counterSize.height);
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
                                .orElseGet(() -> fSlots[slotIndex.getAndIncrement()]);
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

    public synchronized void refresh() {
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
        IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
        String toolTip = null;
        for (int i = 0; (toolTip == null) && (i < fSlots.length); i++) {
            if (StringTool.isProvided(fSlots[i].getIconProperty())) {
                ScaledSlot scaledSlot = scaledSlot(fSlots[i], iconCache);
                if (scaledSlot.area.contains(pMouseEvent.getPoint())) {
                    toolTip = fSlots[i].getToolTip();
                }
            }
        }
        return toolTip;
    }

    private ScaledSlot scaledSlot(ResourceSlot pSlot, IconCache iconCache) {
        int x = pSlot.getLocation().x;
        int y = pSlot.getLocation().y;
        BufferedImage resourceIcon = iconCache.getUnscaledIconByProperty(pSlot.getIconProperty());
        if (getSideBar().isHomeSide()) {
            x += pSlot.getLocation().width - resourceIcon.getWidth() - 1;
        } else {
            x += 1;
        }
        y += (pSlot.getLocation().height - resourceIcon.getHeight() + 1) / 2;
        int scaledX = dimensionProvider.scale(x, RenderContext.UI);
        int scaledY = dimensionProvider.scale(y, RenderContext.UI);
        Dimension resourceDimension = dimensionProvider.scale(new Dimension(resourceIcon.getWidth(), resourceIcon.getHeight()), RenderContext.UI);
        return new ScaledSlot(new Rectangle(scaledX, scaledY, resourceDimension.width, resourceDimension.height), resourceIcon, new Point(x, y));
    }

    private static class ScaledSlot {
        Rectangle area;
        BufferedImage resourceIcon;
        Point origin;

        public ScaledSlot(Rectangle area, BufferedImage resourceIcon, Point origin) {
            this.area = area;
            this.resourceIcon = resourceIcon;
            this.origin = origin;
        }
    }

}

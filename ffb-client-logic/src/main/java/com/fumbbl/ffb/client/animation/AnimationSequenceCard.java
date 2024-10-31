package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.layer.FieldLayer;
import com.fumbbl.ffb.client.sound.SoundEngine;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.model.Animation;

import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class AnimationSequenceCard implements IAnimationSequence, ActionListener {

	private final AnimationFrame[] fFrames;
	private final Timer fTimer;

	private static final int _TIMER_DELAY = 100;
	private final int fX;
	private int fPosition;
	private final int fY;
	private FieldLayer fFieldLayer;

	protected AnimationSequenceCard(Dimension containerDimension, AnimationFrame[] pFrames) {
		fFrames = pFrames;
		fX = containerDimension.width / 2;
		fY = containerDimension.height / 2;
		fTimer = new Timer(_TIMER_DELAY, this);
	}

	public static AnimationSequenceCard createAnimationSequence(FantasyFootballClient pClient, Animation pAnimation) {
		String cardBackProperty = pAnimation.getCard().getType().getCardBack();
		BufferedImage cardFront = createCardFront(pClient, pAnimation.getCard());
		return new AnimationSequenceCard(pClient.getUserInterface().getDimensionProvider().dimension(Component.FIELD),
			new AnimationFrame[]{new AnimationFrame(cardBackProperty, 0.5f, 0.3, 100),
				new AnimationFrame(cardBackProperty, 0.6f, 0.4, 100), new AnimationFrame(cardBackProperty, 0.7f, 0.5, 100),
				new AnimationFrame(cardBackProperty, 0.8f, 0.6, 100), new AnimationFrame(cardBackProperty, 0.9f, 0.7, 100),
				new AnimationFrame(cardBackProperty, 1.0f, 0.8, 100), new AnimationFrame(cardBackProperty, 1.0f, 0.9, 100),
				new AnimationFrame(cardBackProperty, 1.0f, 1.0, 1.0, 500),
				// new AnimationFrame(cardBackProperty, 1.0f, 0.9, 1.0, 100),
				new AnimationFrame(cardBackProperty, 1.0f, 0.8, 1.0, 100),
				// new AnimationFrame(cardBackProperty, 1.0f, 0.7, 1.0, 100),
				new AnimationFrame(cardBackProperty, 1.0f, 0.6, 1.0, 100),
				// new AnimationFrame(cardBackProperty, 1.0f, 0.5, 1.0, 100),
				new AnimationFrame(cardBackProperty, 1.0f, 0.4, 1.0, 100),
				// new AnimationFrame(cardBackProperty, 1.0f, 0.3, 1.0, 100),
				new AnimationFrame(cardBackProperty, 1.0f, 0.2, 1.0, 100),
				// new AnimationFrame(cardBackProperty, 1.0f, 0.1, 1.0, 100),
				new AnimationFrame(cardBackProperty, 1.0f, 0.0, 1.0, 100),
				// new AnimationFrame(cardFront, 1.0f, 0.1, 1.0, 100),
				new AnimationFrame(cardFront, 1.0f, 0.2, 1.0, 100),
				// new AnimationFrame(cardFront, 1.0f, 0.3, 1.0, 100),
				new AnimationFrame(cardFront, 1.0f, 0.4, 1.0, 100),
				// new AnimationFrame(cardFront, 1.0f, 0.5, 1.0, 100),
				new AnimationFrame(cardFront, 1.0f, 0.6, 1.0, 100),
				// new AnimationFrame(cardFront, 1.0f, 0.7, 1.0, 100),
				new AnimationFrame(cardFront, 1.0f, 0.8, 1.0, 100),
				// new AnimationFrame(cardFront, 1.0f, 0.9, 1.0, 100),
				new AnimationFrame(cardFront, 1.0f, 1.0, 1.0, 2000)});
	}

	private int fDelay;
	private IAnimationListener fListener;

	private static BufferedImage createCardFront(FantasyFootballClient pClient, Card pCard) {
		IconCache iconCache = pClient.getUserInterface().getIconCache();
		BufferedImage frontIcon = iconCache.getIconByProperty(pCard.getType().getCardFront());
		if (frontIcon == null) {
			return null;
		}
		String[] lines = pCard.getShortName().split(" ");
		BufferedImage cardIcon = new BufferedImage(frontIcon.getWidth(), frontIcon.getHeight(),
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = cardIcon.createGraphics();
		g2d.drawImage(frontIcon, 0, 0, null);
		g2d.setColor(Color.BLACK);
		FontCache fontCache = pClient.getUserInterface().getFontCache();
		g2d.setFont(fontCache.font(Font.BOLD, 22));
		FontMetrics metrics = g2d.getFontMetrics();
		for (int i = 0; i < lines.length; i++) {
			Rectangle2D textBounds = metrics.getStringBounds(lines[i], g2d);
			int x = (frontIcon.getWidth() / 2) - (int) (textBounds.getWidth() / 2) + 1;
			int y = (frontIcon.getHeight() / 2) - (lines.length * 28) / 2 + (i * 28) + (int) (textBounds.getHeight() / 2) - 2;
			g2d.drawString(lines[i], x, y);
		}
		g2d.dispose();
		return cardIcon;
	}

	public void play(FieldLayer pFieldLayer, IAnimationListener pListener) {
		fFieldLayer = pFieldLayer;
		fListener = pListener;
		fPosition = -1;
		fDelay = 0;
		fTimer.start();
	}

	public void actionPerformed(ActionEvent pE) {
		fDelay -= _TIMER_DELAY;
		if (fDelay > 0) {
			return;
		}
		if (fPosition >= 0) {
			fFrames[fPosition].clear();
		}
		if (fPosition < fFrames.length - 1) {
			fPosition++;
			fDelay = fFrames[fPosition].getTime();
			fFrames[fPosition].drawCenteredAndScaled(fFieldLayer, fX, fY);
			if (fFrames[fPosition].getSound() != null) {
				SoundEngine soundEngine = fFieldLayer.getClient().getUserInterface().getSoundEngine();
				String soundSetting = fFieldLayer.getClient().getProperty(CommonProperty.SETTING_SOUND_MODE);
				if (IClientPropertyValue.SETTING_SOUND_ON.equals(soundSetting)
					|| (IClientPropertyValue.SETTING_SOUND_MUTE_SPECTATORS.equals(soundSetting))) {
					soundEngine.playSound(fFrames[fPosition].getSound());
				}
			}
		} else {
			fTimer.stop();
			fPosition = -1;
		}
		fFieldLayer.getClient().getUserInterface().getFieldComponent().refresh();
		if ((fPosition < 0) && (fListener != null)) {
			fListener.animationFinished();
		}
	}

}

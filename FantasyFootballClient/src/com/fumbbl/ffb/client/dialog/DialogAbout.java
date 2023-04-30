package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.FantasyFootballConstants;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.ImageIcon;
import javax.swing.event.InternalFrameEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class DialogAbout extends Dialog {

	private static final String[] _PLAYTESTERS = {
		"Ale1972, Asharak, Avantar, Avien, Ballcrusher, Balle2000, Barre, Benjysmyth, BiggieB, Brainsaw, Calthor, Carnis, CircularLogic, Chavo,",
		"Clarkin, Cmelchior, Cyrus-Havoc, Don Tomaso, DanTitan76, DukeTyrion, Dynamo380, Ebenezer, Ehlers, Flix, Floppeditbackwards,",
		"Freak_in_a_Frock, Freppa, Gandresch, Garaygos, Gjopie, Hangar18, Happygrue, Hitonagashi, Howlett, Janekt, JanMattys, Janzki,",
		"Jarvis_Pants, Java, JoeMalik, Koigokoro, LeBlanc, Lerysh, Lewdgrip, Loraxwolfsbane, Louky, LoxleyAndy, Magistern, Malitrius, Mickael, Mtknight,",
		"MxFr, Nazgob, Neilwat, Nelphine, Nighteye, On1, PhrollikK, Purplegoo, RamonSalazar, Ravenmore, Razin, RedDevilCG, Reisender, Relezite, Shadow46x2,",
		"Sl8, Stej, Steve, Stimme, Svemole, SvenS, Tarabaralla, Teluriel, Tensai, Thul, Tortured-Robot, Treborius, Tussock, Ulrik, Ultwe, Uomotigre3, Uuni,",
		"Vesikannu, Woodstock, XZCion, Zakatan"};

	public DialogAbout(FantasyFootballClient pClient) {

		super(pClient, "About Fantasy Football", true);

		JLabel aboutLabel = new JLabel(dimensionProvider(), createAboutImageIcon(pClient));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(aboutLabel, BorderLayout.CENTER);

		pack();

		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.ABOUT;
	}

	public void internalFrameClosing(InternalFrameEvent pE) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	private ImageIcon createAboutImageIcon(FantasyFootballClient pClient) {
		DimensionProvider dimensionProvider = pClient.getUserInterface().getDimensionProvider();
		Dimension dimension = dimensionProvider.dimension(DimensionProvider.Component.ABOUT_DIALOG);

		BufferedImage aboutImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = aboutImage.createGraphics();
		g2d.drawImage(pClient.getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_SPLASH_SCREEN), 0, 0,
			aboutImage.getWidth(), aboutImage.getHeight(), null);

		g2d.setColor(Color.WHITE);

		g2d.setFont(fontCache().font(Font.BOLD, 17));

		String versionInfo = getClient().getParameters().getBuild();
		if (versionInfo == null) {
			versionInfo = "Version " + FantasyFootballConstants.CLIENT_VERSION;
		} else {
			versionInfo = "Build " + FantasyFootballConstants.CLIENT_VERSION + "-" + versionInfo;
		}
		Rectangle2D versionBounds = g2d.getFontMetrics().getStringBounds(versionInfo, g2d);
		g2d.drawString(versionInfo, dimension.width - dimensionProvider.scale(25) - (int) versionBounds.getWidth(), dimensionProvider.scale(155));

		int y = 130;

		drawBold(g2d, dimensionProvider.scale(10), dimensionProvider.scale(y += 0), "Headcoach: BattleLore");
		drawText(g2d, dimensionProvider.scale(20), dimensionProvider.scale(y += 20), "thank you for providing ideas, encouragement and the occasional kick in the butt.");

		drawBold(g2d, dimensionProvider.scale(10), dimensionProvider.scale(y += 25), "Assistant Coaches: WhatBall, Garion and Lakrillo");
		drawText(g2d, dimensionProvider.scale(20), dimensionProvider.scale(y += 20), "thank you for helping to to pull the cart along.");

		drawBold(g2d, dimensionProvider.scale(10), dimensionProvider.scale(y += 25), "Sports Director: Christer");
		drawText(g2d, dimensionProvider.scale(20), dimensionProvider.scale(y += 20), "thank you for the patience and energy to tackle the long road with me.");

		drawBold(g2d, dimensionProvider.scale(10), dimensionProvider.scale(y += 25), "Lifetime Luxury Suite Owner: SkiJunkie");
		drawText(g2d, dimensionProvider.scale(20), dimensionProvider.scale(y += 20), "thank you doing it first and giving a vision to follow.");

		drawBold(g2d, dimensionProvider.scale(10), dimensionProvider.scale(y += 25),
			"Light Show by: Cowhead, F_alk, FreeRange, Harvestmouse, Knut_Rockie, MisterFurious and Ryanfitz");
		drawBold(g2d, dimensionProvider.scale(10), dimensionProvider.scale(y += 20), "Playing the Stadium Organ: VocalVoodoo and Minenbonnie");
		drawBold(g2d, dimensionProvider.scale(10), dimensionProvider.scale(y += 20), "Official supplier of game balls: Qaz");
		drawBold(g2d, dimensionProvider.scale(10), dimensionProvider.scale(y += 20), "Thanks for the hats: ArrestedDevelopment");
		drawText(g2d, dimensionProvider.scale(20), dimensionProvider.scale(y += 20), "thank you all for making FFB look and sound great.");

		drawBold(g2d, dimensionProvider.scale(10), dimensionProvider.scale(y += 25), "Cheerleaders & Pest Control:");

		y += 5;
		for (String playtesters : _PLAYTESTERS) {
			drawText(g2d, dimensionProvider.scale(20), dimensionProvider.scale(y += 15), playtesters);
		}

		g2d.dispose();

		return new ImageIcon(aboutImage);

	}

	private void drawText(Graphics2D pG2d, int pX, int pY, String pText) {
		pG2d.setFont(fontCache().font(Font.PLAIN, 12));
		pG2d.drawString(pText, pX, pY);
	}

	private void drawBold(Graphics2D pG2d, int pX, int pY, String pText) {
		pG2d.setFont(fontCache().font(Font.BOLD, 12));
		pG2d.drawString(pText, pX, pY);
	}

	protected void setLocationToCenter() {
		Dimension dialogSize = getSize();
		Dimension frameSize = getClient().getUserInterface().getSize();
		Dimension menuBarSize = getClient().getUserInterface().getGameMenuBar().getSize();
		setLocation((frameSize.width - dialogSize.width) / 2,
				((frameSize.height - dialogSize.height) / 2) - menuBarSize.height);
	}

}

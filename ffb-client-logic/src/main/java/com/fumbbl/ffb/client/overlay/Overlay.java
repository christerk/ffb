package com.fumbbl.ffb.client.overlay;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface Overlay extends MouseListener, MouseMotionListener {
	void setOnline(boolean online);
}

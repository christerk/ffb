package net.codejava.swing;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.codejava.model.Country;

/**
 * Custom renderer to display a country's flag alongside its name
 *
 * @author wwww.codejava.net
 */
public class CountryRenderer extends JLabel implements ListCellRenderer<Country> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7896925225003735613L;

	public CountryRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Country> list, Country country, int index,
            boolean isSelected, boolean cellHasFocus) {

        String code = country.getCode();
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/" + code + ".png"));

        setIcon(imageIcon);
        setText(country.getName());

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}

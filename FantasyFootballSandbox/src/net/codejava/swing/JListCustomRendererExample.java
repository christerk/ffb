package net.codejava.swing;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.codejava.model.Country;

/**
 * JList Custom Renderer Example
 *
 * @author wwww.codejava.net
 */
public class JListCustomRendererExample extends JFrame {

    public JListCustomRendererExample() {
        Country us = new Country("USA", "us");
        Country in = new Country("India", "in");
        Country vn = new Country("Vietnam", "vn");
        Country ca = new Country("Canada", "ca");
        Country de = new Country("Denmark", "de");
        Country fr = new Country("France", "fr");
        Country gb = new Country("Great Britain", "gb");
        Country jp = new Country("Japan", "jp");

        //create the model and add elements
        DefaultListModel<Country> listModel = new DefaultListModel<Country>();
        listModel.addElement(us);
        listModel.addElement(in);
        listModel.addElement(vn);
        listModel.addElement(ca);
        listModel.addElement(de);
        listModel.addElement(fr);
        listModel.addElement(gb);
        listModel.addElement(jp);

        //create the list
        JList<Country> countryList = new JList<Country>(listModel);
        add(new JScrollPane(countryList));
        countryList.setCellRenderer(new CountryRenderer());

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JList Renderer Example");
        this.setSize(200, 200);
        this.setLocationRelativeTo(null);        
        this.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JListCustomRendererExample();
            }
        });
    }
}

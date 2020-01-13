package com.kor.admiralty.ui.renderers;

import com.kor.admiralty.beans.Admiral;
import com.kor.admiralty.beans.Ship;
import com.kor.admiralty.ui.resources.Swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MaintenanceShipCellRenderer extends ShipCellRenderer {

    private static final long serialVersionUID = 1413030761141709940L;
    private static Map<Admiral, MaintenanceShipCellRenderer> SINGLETONS = new HashMap<>();

    private Admiral admiral;

    protected JLabel lblReadyTime;

    public static MaintenanceShipCellRenderer cellRenderer(Admiral admiral) {
        MaintenanceShipCellRenderer singleton = SINGLETONS.get(admiral);
        if (singleton == null) {
            singleton = new MaintenanceShipCellRenderer(admiral);
            SINGLETONS.put(admiral, singleton);
        }
        return singleton;
    }

    public MaintenanceShipCellRenderer(Admiral admiral) {
        super();
        lblReadyTime = new JLabel("", SwingConstants.RIGHT);
        lblReadyTime.setFont(new Font("Tahoma", Font.BOLD, 12));
        GridBagConstraints gbc_lblReadyTime = new GridBagConstraints();
        gbc_lblReadyTime.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblReadyTime.weightx = 10.0;
        gbc_lblReadyTime.insets = new Insets(5, 0, 5, 5);
        gbc_lblReadyTime.gridx = 1;
        gbc_lblReadyTime.gridy = 0;
        add(lblReadyTime, gbc_lblReadyTime);

        this.admiral = admiral;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Ship> list, Ship ship, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, ship, index, isSelected, cellHasFocus);
        if (lblReadyTime != null) {
            if (ship == null || admiral == null || !admiral.getMaintenanceShips().contains(ship)) {
                lblReadyTime.setText("");
                lblReadyTime.setForeground(Swing.ColorCommon);
            } else {
                Long readyTime = admiral.getMaintenance().get(ship.getName());
                lblReadyTime.setText(readyTime != null ? readyDuration(readyTime) : "---");
                lblReadyTime.setForeground(Swing.ColorCommon);
            }
        }
        return this;
    }

    private String readyDuration(long readyTime) {
        long duration = readyTime - System.currentTimeMillis();
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hours));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));
        String result;
        if (hours > 0) {
            result = String.format("%dh %02dm %02ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            result = String.format("%02dm %02ds", minutes, seconds);
        } else {
            result = String.format("%02ds", seconds);
        }
        return result;
    }
}

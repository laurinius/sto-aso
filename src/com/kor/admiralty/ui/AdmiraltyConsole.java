/*******************************************************************************
 * Copyright (C) 2015, 2019 Dave Kor
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.kor.admiralty.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.*;

import com.kor.admiralty.beans.Admiral;
import com.kor.admiralty.beans.Admirals;
import com.kor.admiralty.beans.Maintenance;
import com.kor.admiralty.beans.Ship;
import com.kor.admiralty.io.Datastore;
import com.kor.admiralty.ui.components.ExceptionDialog;
import com.kor.admiralty.ui.resources.Images;
import com.kor.admiralty.ui.resources.Strings;
import com.kor.admiralty.ui.resources.Swing;
import static com.kor.admiralty.ui.resources.Strings.Empty;
import static com.kor.admiralty.ui.resources.Strings.AdmiraltyConsole.*;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdmiraltyConsole extends JFrame implements Runnable, PropertyChangeListener, UncaughtExceptionHandler {

	private static final long serialVersionUID = 5802106751292695623L;

	public static final AdmiraltyConsole CONSOLE = new AdmiraltyConsole();
	public static final ShipUsageFrame STATS_FRAME = new ShipUsageFrame();

	protected Admirals admirals;
	protected Map<Admiral, AdmiralPanel> admiralMap;
	protected SortedMap<String, Ship> ships;

	protected JPanel contentPane;
	protected Action actionAddAdmiral;
	private JTabbedPane tabAdmirals;
	private JToggleButton tglbtnStayOnTop;
	private Action actionCenter;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(CONSOLE);
		EventQueue.invokeLater(CONSOLE);
		Swing.overrideComboBoxMouseWheel();
	}

	/**
	 * Create the frame.
	 */
	public AdmiraltyConsole() {
		Datastore.updateDataFiles();
		this.actionAddAdmiral = new AddAdmiralAction();
		this.actionCenter = new CenterAction();
		Swing.setLookAndFeel();
		setTitle(Title);
		setIconImage(Images.IMG_ASO);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Datastore.setAdmirals(admirals);
				Datastore.preserveIconCache();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640, 480);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		contentPane.add(toolBar, BorderLayout.NORTH);

		Action actionInfo = new InfoAction();
		JButton btnInfo = new JButton(actionInfo);
		toolBar.add(btnInfo);

		JButton btnAddAdmiral = new JButton(actionAddAdmiral);
		toolBar.add(btnAddAdmiral);

		Action actionDeleteAdmiral = new DeleteAdmiralAction();
		JButton btnDeleteAdmiral = new JButton(actionDeleteAdmiral);
		toolBar.add(btnDeleteAdmiral);

		Action actionUsage = new ShipStatsAction();
		JButton btnUsage = new JButton(actionUsage);
		toolBar.add(btnUsage);

		toolBar.add(Box.createHorizontalGlue());

		Action actionUpdate = new DataUpdateAction();
		JButton btnUpdate = new JButton(actionUpdate);
		toolBar.add(btnUpdate);

		JLabel lblWindow = new JLabel(LabelWindowPosition);
		toolBar.add(lblWindow);

		Action actionLeft = new LeftAction();
		JToggleButton tglbtnLeft = new JToggleButton(actionLeft);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(tglbtnLeft);
		toolBar.add(tglbtnLeft);

		JToggleButton tglbtnCenter = new JToggleButton(actionCenter);
		buttonGroup.add(tglbtnCenter);
		toolBar.add(tglbtnCenter);

		Action actionRight = new RightAction();
		JToggleButton tglbtnRight = new JToggleButton(actionRight);
		buttonGroup.add(tglbtnRight);
		toolBar.add(tglbtnRight);

		Action actionStayOnTop = new StayOnTopAction();
		tglbtnStayOnTop = new JToggleButton(actionStayOnTop);
		toolBar.add(tglbtnStayOnTop);

		tabAdmirals = new JTabbedPane(JTabbedPane.LEFT);
		contentPane.add(tabAdmirals, BorderLayout.CENTER);

		if (Beans.isDesignTime()) {
			initDesignTime();
		} else {
			initRunTime();
		}
	}

	protected void initDesignTime() {
	}

	protected void initRunTime() {
		actionCenter.actionPerformed(null);
		ships = Datastore.getAllShips();
		admirals = Datastore.getAdmirals();
		admiralMap = new HashMap<Admiral, AdmiralPanel>();
		for (Admiral admiral : admirals.getAdmirals()) {
			AdmiralPanel panel = new AdmiralPanel(admiral);
			tabAdmirals.addTab(admiral.getName(), panel);
			admiralMap.put(admiral, panel);
			admiral.addPropertyChangeListener(this);
		}

		Timer timer = new Timer(5000, e -> {
			long now = System.currentTimeMillis();
			for (Admiral admiral : admiralMap.keySet()) {
				Set<String> ready = new HashSet<>();
				for (Maintenance maintenance : admiral.getMaintenance()) {
					if (maintenance.getReadyTime() != null && now > maintenance.getReadyTime()) {
						ready.add(maintenance.getName());
					}
				}
				for (String ship : ready) {
					admiral.removeMaintenance(ship);
					admiral.addActive(ship);
				}
				if (!admiral.getMaintenance().isEmpty()) {
					admiralMap.get(admiral).lstMaintenance.invalidate();
					admiralMap.get(admiral).lstMaintenance.repaint();
				}
			}
		});
		timer.setInitialDelay(1);
		timer.start();
	}

	public SortedMap<String, Ship> getShipDatabase() {
		return ships;
	}

	public Admirals getAdmirals() {
		return admirals;
	}

	@Override
	public void run() {
		CONSOLE.setVisible(true);
		CONSOLE.toFront();
		CONSOLE.repaint();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		Admiral admiral = (Admiral) e.getSource();
		String property = e.getPropertyName();
		if (Objects.equals(property, Admiral.PROP_NAME)) {
			String newName = e.getNewValue().toString();
			AdmiralPanel panel = admiralMap.get(admiral);
			int index = tabAdmirals.indexOfComponent(panel);
			if (index < 0)
				return;

			tabAdmirals.setTitleAt(index, newName);
		}
	}

	private class AddAdmiralAction extends AbstractAction {
		private static final long serialVersionUID = 7270481036042791758L;
		public AddAdmiralAction() {
			super(LabelAdmiral, Images.ICON_ADD);
			putValue(SHORT_DESCRIPTION, LabelCreateAdmiral);
		}
		public void actionPerformed(ActionEvent e) {
			Admiral admiral = new Admiral();
			admirals.addAdmiral(admiral);
			AdmiralPanel panel = new AdmiralPanel(admiral);
			tabAdmirals.addTab(admiral.getName(), panel);
			admiralMap.put(admiral, panel);
			admiral.addPropertyChangeListener(CONSOLE);
		}
	}

	private class DeleteAdmiralAction extends AbstractAction {
		private static final long serialVersionUID = -5415290655392916478L;
		public DeleteAdmiralAction() {
			super(LabelAdmiral, Images.ICON_REMOVE);
			putValue(SHORT_DESCRIPTION, LabelDeleteAdmiral);
		}
		public void actionPerformed(ActionEvent e) {
			Component component = tabAdmirals.getSelectedComponent();
			if (component == null)
				return;

			AdmiralPanel panel = (AdmiralPanel) component;
			Admiral admiral = panel.getAdmiral();
			String question = String.format(MsgConfirmDeleteQuestion, admiral.getName());
			int result = JOptionPane.showConfirmDialog(CONSOLE, question, TitleConfirmDelete, JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				admirals.removeAdmiral(admiral);
				tabAdmirals.remove(panel);
			}
		}
	}

	private class LeftAction extends AbstractAction {
		private static final long serialVersionUID = -3473188421024690575L;
		public LeftAction() {
			super(Empty, Images.ICON_LHS);
			putValue(SHORT_DESCRIPTION, DescLHS);
			putValue(MNEMONIC_KEY, KeyEvent.VK_L);
		}
		public void actionPerformed(ActionEvent e) {
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			Dimension screen = getSize();
			setExtendedState(JFrame.NORMAL);
			int w = (int) (screen.getWidth() * 0.5);
			int h = (int) (screen.getHeight() * 1.0);
			setSize(w, h);
			setLocation(0, 0);
		}
	}
	
	private class CenterAction extends AbstractAction {
		private static final long serialVersionUID = 9150512791467638511L;
		public CenterAction() {
			super(Empty, Images.ICON_CTR);
			putValue(SHORT_DESCRIPTION, DescCTR);
			putValue(MNEMONIC_KEY, KeyEvent.VK_C);
		}
		public void actionPerformed(ActionEvent e) {
			setExtendedState(JFrame.NORMAL);
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			int w = (int) (screen.getWidth() * 0.8);
			int h = (int) (screen.getHeight() * 0.8);
			int x = (int) ((screen.getWidth() - w) / 2);
			int y = (int) ((screen.getHeight() - h) / 2);
			setSize(w, h);
			setLocation(x, y);
		}
	}
	
	private class RightAction extends AbstractAction {
		private static final long serialVersionUID = 5495029836130381804L;
		public RightAction() {
			super(Empty, Images.ICON_RHS);
			putValue(SHORT_DESCRIPTION, DescRHS);
			putValue(MNEMONIC_KEY, KeyEvent.VK_R);
		}
		public void actionPerformed(ActionEvent e) {
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			Dimension screen = getSize();
			setExtendedState(JFrame.NORMAL);
			int w = (int) (screen.getWidth() * 0.5);
			int h = (int) (screen.getHeight() * 1.0);
			setSize(w, h);
			setLocation(w, 0);
		}
	}
	
	private class StayOnTopAction extends AbstractAction {
		private static final long serialVersionUID = -4586643308522585265L;
		public StayOnTopAction() {
			super(Empty, Images.ICON_PIN);
			putValue(SHORT_DESCRIPTION, DescStayOnTop);
			putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		}
		public void actionPerformed(ActionEvent e) {
			CONSOLE.setAlwaysOnTop(tglbtnStayOnTop.isSelected());
		}
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace();
		ExceptionDialog dialog = new ExceptionDialog(CONSOLE, TitleError, e.getMessage(), e);
		dialog.setLocationRelativeTo(CONSOLE);
		dialog.setVisible(true);
	}

	private class InfoAction extends AbstractAction {
		private static final long serialVersionUID = 8645438505116441090L;
		public InfoAction() {
			super(LabelAbout, Images.ICON_INFO);
			putValue(SHORT_DESCRIPTION, DescInfo);
		}
		public void actionPerformed(ActionEvent e) {
			String version = Strings.Version();
			String message = String.format(MsgVersionInfo, version);
			JOptionPane.showMessageDialog(CONSOLE, message, DescInfo, JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private class ShipStatsAction extends AbstractAction {
		private static final long serialVersionUID = 2346493909669345790L;
		public ShipStatsAction() {
			super(LabelShipStats, Images.ICON_CHART);
			putValue(SHORT_DESCRIPTION, DescShipStats);
		}
		public void actionPerformed(ActionEvent e) {
			EventQueue.invokeLater(STATS_FRAME);
		}
	}

	private class DataUpdateAction extends AbstractAction {
		public DataUpdateAction() {
			super(LabelDataUpdate);
			putValue(SHORT_DESCRIPTION, DescDataUpdate);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Datastore.updateDataFiles(true);
			Datastore.clearCachedIcons();
			Datastore.setAdmirals(admirals);
			dispose();
		}
	}
}

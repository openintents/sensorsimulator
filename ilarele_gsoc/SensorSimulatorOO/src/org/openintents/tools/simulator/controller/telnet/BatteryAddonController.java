package org.openintents.tools.simulator.controller.telnet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openintents.tools.simulator.model.telnet.addons.BatteryAddonModel;
import org.openintents.tools.simulator.view.telnet.addons.BatteryAddonView;

public class BatteryAddonController {

	public BatteryAddonController(final BatteryAddonModel model,
			final BatteryAddonView view) {
		JSlider batterySlider = view.getBatterySlider();

		batterySlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				model.changePower(source.getValue());
			}
		});

		JCheckBox batteryPresence = view.getBatteryPresence();
		batteryPresence.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				switch (e.getStateChange()) {
				case ItemEvent.SELECTED:
					model.changePresence(true);
					break;
				case ItemEvent.DESELECTED:
					model.changePresence(false);
					break;
				}
			}
		});

		JCheckBox batteryAC = view.getBatteryAC();
		batteryAC.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				switch (e.getStateChange()) {
				case ItemEvent.SELECTED:
					model.changeAC(true);
					break;
				case ItemEvent.DESELECTED:
					model.changeAC(false);
					break;
				}
			}
		});

		final JComboBox batteryStatusList = view.getBatteryStatusList();
		batteryStatusList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ("comboBoxChanged".equals(e.getActionCommand())) {
					model.changeStatus((String) batteryStatusList
							.getSelectedItem());
				}
			}
		});

		final JComboBox batteryHealthList = view.getBatteryHealthList();
		batteryHealthList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ("comboBoxChanged".equals(e.getActionCommand())) {
					model.changeHealth((String) batteryHealthList
							.getSelectedItem());
				}
			}
		});

		JButton batteryNext = view.getBatteryNext();
		batteryNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.nextTimeEvent();
			}
		});

		final JFileChooser fileChooser = view.getFileChooser();
		JButton openButton = view.getOpenFileButton();
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(view);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					model.openFile(file);
				}
			}
		});

		JButton batteryEmulation = view.getBatteryEmulation();
		batteryEmulation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.slowEmulation(view);
			}
		});
	}
}

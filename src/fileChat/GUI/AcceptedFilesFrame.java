package fileChat.GUI;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Vector;

import javax.swing.*;

public class AcceptedFilesFrame extends JFrame implements WindowListener {
	private JList<String> list;
	private Vector<String> acceptedFiles;

	public AcceptedFilesFrame(String name) {
		super(name);
		JPanel panel = new JPanel();
		acceptedFiles = new Vector<String>();
		panel.setPreferredSize(new Dimension(300, 400));
		list = new JList<String>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(list);
		panel.setLayout(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		setLayout(new BorderLayout());
		getContentPane().add(panel, BorderLayout.CENTER);
		JPanel panelButton = new JPanel();
		JButton open = new JButton("Open");
		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!list.isSelectionEmpty()) {
					String selectedFile = list.getSelectedValue();
					try {
						Desktop.getDesktop().open(new File("AcceptedFiles/" + selectedFile));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

			}
		});
		JButton showFolder = new JButton("Show Folder");
		showFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!list.isSelectionEmpty()) {
					try {
						Desktop.getDesktop().open(new File("AcceptedFiles/"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

			}
		});
		panelButton.setLayout(new FlowLayout());
		panelButton.add(open);
		panelButton.add(showFolder);
		getContentPane().add(panelButton, BorderLayout.SOUTH);
		setVisible(false);
		addWindowListener(this);
		pack();
	}

	public void addToList(String fileName) {
		acceptedFiles.add(fileName);
		list.setListData(acceptedFiles);
	}
	

	public JList<String> getFileList() {
		return list;
	}
	@Override
	public void windowClosed(WindowEvent e) {
		setVisible(false);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}

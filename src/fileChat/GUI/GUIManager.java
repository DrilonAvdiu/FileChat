package fileChat.GUI;
/******************************************************************************
 * JavaGroupsVC version 0.5 beta 
 * 
 * GUIManager main handler in the GUI layer
 * All communication from the control/data layer to the GUI layer goes
 * thru this class. The same goes to the communication from the opposite direction.
 * 
 ******************************************************************************/

import javax.swing.*;
import JavaGroups.*;
import fileChat.ControlManager.*;
import fileChat.DataManager.*;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class GUIManager implements WindowListener, ActionListener, Runnable {
	boolean debug;
	int iconNum = 0;
	JPanel mainPanel;
	JFrame mainFrame;
	ChatPanel chatPanel;
	MembershipPanel membershipPanel = null;
	GroupsPanel groupsPanel = null;
	JPanel groupsAndMembersPanel = null;
	JPanel textPanel = null;
	Container mainContentPane;
	GridBagLayout gridBag;
	GridBagConstraints c;
	TextManager textManager = null;
	FileManager fileManager = null;
	MembershipManager membershipManager = null;
	UserInfoManager userInfoManager = null;
	GroupsManager groupsManager = null;
	UserInfo myUserInfo = null;
	JMenuBar menuBar;
	JMenu menuAction;
	JMenu menuFile;
	JMenu menuHelp;
	JMenuItem menuItemQuit;
	public JMenuItem menuItemLeaveGroup;
	public JMenu menuJoinGroup;
	public JMenuItem newGroupMenuItem;
	public JMenuItem menuItemAbout;
	JSeparator separator;
	Thread t;
	String groupName;
	String username;
	File videoFile = null;
	File audioFile = null;
	File file = null;
	boolean newGroup = false;
	AcceptedFilesFrame aFrame;
	JPanel controllButtonsPanel;

	public GUIManager(boolean debug) {
		this.debug = debug;
		mainFrame = new JFrame();
		gridBag = new GridBagLayout();
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainFrame.addWindowListener(this);
		mainContentPane = mainFrame.getContentPane();
		mainContentPane.setLayout(new BorderLayout());
		groupsAndMembersPanel = new JPanel(new GridLayout(1, 2));
		textPanel = new JPanel(new BorderLayout());
		controllButtonsPanel = new JPanel();
		menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
		menuFile = new JMenu("File");
		menuItemQuit = new JMenuItem("Quit");
		menuItemQuit.setActionCommand("@Quit");
		menuItemQuit.addActionListener(this);
		menuFile.add(menuItemQuit);
		menuBar.add(menuFile);
		menuHelp = new JMenu("Help");
		menuItemAbout = new JMenuItem("About");
		menuItemAbout.setActionCommand("@About");
		menuItemAbout.addActionListener(this);
		menuHelp.add(menuItemAbout);
		/*
		 * Add menuItem Action
		 */
		menuAction = new JMenu("Action");
		menuItemLeaveGroup = new JMenuItem("Leave Group");
		menuItemLeaveGroup.setEnabled(false);
		menuAction.add(menuItemLeaveGroup);
		menuItemLeaveGroup.setActionCommand("@Leave@Group");
		menuItemLeaveGroup.addActionListener(this);
		menuJoinGroup = new JMenu("Join Group");
		menuAction.add(menuJoinGroup);
		menuJoinGroup.setActionCommand("JoinGroup");
		newGroupMenuItem = new JMenuItem("Create a New Group");
		newGroupMenuItem.setActionCommand("@Create@New@Group");
		newGroupMenuItem.addActionListener(this);
		separator = new JSeparator();
		menuJoinGroup.add(newGroupMenuItem);
		menuBar.add(menuAction);
		menuBar.add(menuHelp);

	}

	public void run() {
		UserInfo oldUserInfo = null;
		if (userInfoManager != null) {
			oldUserInfo = userInfoManager.getMyUserInfo();
		}
		if (oldUserInfo == null) {
			String username = GUIManager.promptUsernameInput("Please input username");
			if (username == null) {
				userInfoManager.setMyUserInfo(null);
				myUserInfo = null;
				oldUserInfo = null;
				return;
			}
			if (newGroup) {
				groupName = GUIManager.promptUsernameInput("Please choose a group name");
			}
			if (groupName == null) {
				userInfoManager.setMyUserInfo(null);
				myUserInfo = null;
				oldUserInfo = null;

				return;

			}
			myUserInfo = new UserInfo(username);

		} else {
			if (newGroup) {
				groupName = GUIManager.promptUsernameInput("Please choose a new group name");
			}
			if (groupName == null) {
				userInfoManager.setMyUserInfo(null);
				myUserInfo = null;
				oldUserInfo = null;
				return;
			}
			newGroup = false;
			myUserInfo = new UserInfo(oldUserInfo.getUserName());
			myUserInfo.setUniqueNum(userInfoManager.getMyUserInfo().getUniqueNum());
		}
		userInfoManager = new UserInfoManager(debug, myUserInfo);
		ChannelFactory channelFactory = groupsManager.getChannelFactory();
		MembershipManager membershipManager = new MembershipManager(debug, userInfoManager, channelFactory, this,
				groupsManager);
		setControlManager(membershipManager);
		groupsManager.setMembershipManager(membershipManager);
		membershipPanel.setMembershipManger(membershipManager);
		membershipManager.connectToGroup(groupName, "UDP:PING:FD:GMS");
		membershipPanel.setSelfAddress(membershipManager.getSelfAddress());
		membershipPanel.updateMembershipPanel(membershipManager.getCurrentMembersVector(),
				membershipManager.getUserInfoManager());
		TextManager textManager = new TextManager(debug, userInfoManager, channelFactory, this);
		setTextManager(textManager);
		groupsManager.setTextManager(textManager);
		membershipPanel.setTextManager(textManager);
		chatPanel.setTextManager(textManager);
		chatPanel.clearAllMessage();
		textManager.connectToGroup(groupName, "UDP:PING:FD:GMS");
		fileManager = new FileManager(debug, userInfoManager, channelFactory, this,textManager );
		fileManager.connectToGroup(groupName, "UDP:PING:FD:GMS");
		menuItemLeaveGroup.setEnabled(true);
		menuJoinGroup.setEnabled(false);
	}

	public void updateMenuItemJoinGroup() {
		menuJoinGroup.removeAll();
		Hashtable groupListTable = groupsManager.getGroupListManager().getGroupListTable();
		for (Enumeration e = groupListTable.keys(); e.hasMoreElements();) {
			String groupName = (String) e.nextElement();
			JMenuItem groupItem = new JMenuItem("\"" + groupName + "\"");
			groupItem.setActionCommand(groupName);
			groupItem.addActionListener(this);
			menuJoinGroup.add(groupItem);
		}

		menuJoinGroup.add(separator);
		menuJoinGroup.add(newGroupMenuItem);

	}

	public void setTextManager(TextManager textManager) {
		if (textManager == null) {
			System.out.println("Trying to pass a null TextManager to guiManager");
			return;
		}
		this.textManager = textManager;
	}

	public void setControlManager(MembershipManager membershipManager) {
		this.membershipManager = membershipManager;
		userInfoManager = membershipManager.getUserInfoManager();
		myUserInfo = userInfoManager.getMyUserInfo();
		setTitle();

	}

	public void setGroupsManger(GroupsManager groupsManager) {
		if (groupsManager == null) {
			System.out.println("Trying to pass a null groupsManager to guiManager");
			return;
		}
		this.groupsManager = groupsManager;
	}

	public void setTitle(String title) {
		mainFrame.setTitle(title);
	}

	public void setTitle() {
		if (myUserInfo != null)
			mainFrame.setTitle(myUserInfo.getUserName());
	}

	public void setLeaveGroupMenuEnable(boolean enable) {
		menuItemLeaveGroup.setEnabled(enable);
	}

	public void setJoinGroupEnable(boolean enable) {
		menuJoinGroup.setEnabled(enable);
	}

	public void updateMembershipPanel(Vector newViewVictor, UserInfoManager userInfoManager) {
		if (membershipPanel != null)
			membershipPanel.updateMembershipPanel(newViewVictor, userInfoManager);

	}

	public void updateMembershipPanel(UserInfo userInfo, Object address) {
		if (membershipPanel != null)
			membershipPanel.updateMembershipPanel(userInfo, address);

	}

	public void updateGroupsPanel(GroupListManager groupListManager) {
		if (groupsPanel != null) {
			groupsPanel.updateGroupsPanel(groupListManager);
		}
	}

	public void setVisible(boolean visible) {
		mainFrame.setSize(400, 400);
		mainFrame.pack();
		mainFrame.setVisible(visible);
	}

	public void createChatPanel() {
		if (textManager == null) {
			System.out.println("Trying to pass a null TextManager to chatPanel");
			// return;
		}
		chatPanel = new ChatPanel(textManager);
		gridBag.setConstraints(chatPanel, c);
		textPanel.add(chatPanel, BorderLayout.NORTH);
		mainContentPane.add(textPanel,BorderLayout.NORTH);

	}

	public void createMembershipPanel() {
		if (membershipManager == null) {
			System.out.println("Trying to pass a null MembershipManager to membershipPanel");
			// return;
		}
		membershipPanel = new MembershipPanel(membershipManager, textManager);
		gridBag.setConstraints(membershipPanel, c);
		groupsAndMembersPanel.add(membershipPanel);
		if (groupsPanel != null) {
			mainContentPane.add(groupsAndMembersPanel,BorderLayout.SOUTH);
		}
		if (membershipManager != null) {
			membershipPanel.updateMembershipPanel(membershipManager.getCurrentMembersVector(),
					membershipManager.getUserInfoManager());
		}
	}

	public void createGroupsPanel() {

		if (groupsManager == null) {
			System.out.println("Trying to pass a null GroupsManager to groupsPanel");
			return;
		}
		groupsPanel = new GroupsPanel(debug, groupsManager);
		groupsAndMembersPanel.add(groupsPanel);
		if (groupsPanel != null) {
			mainContentPane.add(groupsAndMembersPanel,BorderLayout.SOUTH);
		}
		groupsPanel.updateGroupsPanel(groupsManager.getGroupListManager());

	}

	public MembershipPanel getMembershipPanel() {
		return membershipPanel;
	}

	public ChatPanel getChatPanel() {
		return chatPanel;
	}

	public MembershipManager getMembershipManager() {
		return membershipManager;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public TextManager getTextManager() {
		return textManager;
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public File getVideoFile() {
		return videoFile;
	}

	public File getAudioFile() {
		return audioFile;
	}

	public File getFile() {
		return file;
	}

	public void addTextPanel() {
		mainContentPane.add(textPanel,BorderLayout.NORTH);
	}

	public void createFileSendButton() {
		JButton button = new JButton("Browse");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
					membershipManager.sendStartFileBcastRequestMsg();
				}

			}
		});
		controllButtonsPanel.add(button);
		textPanel.add(controllButtonsPanel, BorderLayout.CENTER);

	}

	public void createFrameOfAcceptedFiles() {
		aFrame = new AcceptedFilesFrame("Accepted Files");
		aFrame.setLocationRelativeTo(mainFrame);
	}

	public AcceptedFilesFrame getFrameOfAcceptedFiles() {
		return aFrame;
	}

	public void createFrameOfAcceptedFilesButton() {
		JButton button = new JButton("Files");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				aFrame.setVisible(true);
			}
		});

		controllButtonsPanel.add(button);
	}

	public void showErrorDialog(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}

	static public String promptUsernameInput(String promptInfo) {
		String username = JOptionPane.showInputDialog(promptInfo);
		return username;
	}

	static public String promptUserGroupnameInput(String promptInfo) {
		String groupName = JOptionPane.showInputDialog(promptInfo);
		return groupName;
	}

	public int promptUserIcon(Vector availableIcons, String title) {
		JPanel iconsPanel = new JPanel(new GridLayout(5, 6));
		int size = availableIcons.size();

		for (int i = 0; i < size; i++) {
			Integer iconNum = (Integer) availableIcons.elementAt(i);
			ImageIcon imageIcon = new ImageIcon(getClass().getResource("/" + iconNum + ".GIF"));
			UserButton userButton = new UserButton(null, null, iconNum.intValue(), imageIcon);
			userButton.setActionCommand(iconNum.toString());
			userButton.addActionListener(this);
			iconsPanel.add(userButton);
		}

		String[] iconsOptionNames = { "OK", "Cancel" };
		String iconsDialogTitle = "Please choose an icon to represent you";
		if (title != null) {
			iconsDialogTitle = title;
		}

		int n = JOptionPane.showOptionDialog(null, iconsPanel, iconsDialogTitle, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, iconsOptionNames, iconsOptionNames[0]);
		if (n == 1) {
			iconNum = -1;
		}
		return iconNum;
	}

	public void windowClosing(WindowEvent e) {
		int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Quit",
				JOptionPane.YES_NO_OPTION);
		if (n == 0) {
			System.exit(0);
		}
		System.out.println("windowClosed");
	}

	public void actionPerformed(ActionEvent evt) {
		String commandString = evt.getActionCommand();
		if (commandString.equals("@Leave@Group")) {
			if (debug)
				System.out.println("Menu item LeaveGroup is selected");
			groupsManager.disconnectFromGroup();
			membershipPanel.resetPanel();
			menuItemLeaveGroup.setEnabled(false);
			menuJoinGroup.setEnabled(true);
		} else if (commandString.equals("JoinGroup")) {
			t = new Thread(this, "JoinGroup");
			t.start();
		} else if (commandString.equals("@Create@New@Group")) {
			newGroup = true;
			t = new Thread(this, "JoinGroup");
			t.start();
		} else if (commandString.equals("@Quit")) {
			int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Quit",
					JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				System.exit(0);
			}
		} else if (commandString.equals("@About")) {

			JOptionPane.showMessageDialog(null,
					"JavaGroupsVC  version 0.5 beta\nAuthor: Ng, Hooi Ming\nEmail:hooiming@cs.cornell.edu\nMay 20, 1999",
					"About", JOptionPane.INFORMATION_MESSAGE, null);

		} else if (commandString.startsWith("@")) {
			// start VC with another member
			String userName = commandString.substring(1);
			UserMenuItem userMenuItem = (UserMenuItem) evt.getSource();
			if (debug)
				System.out.println(
						"Wants to VC with member " + userMenuItem.getUserName() + ":" + userMenuItem.getAddress());
			showErrorDialog("Feature not implemented", "You chose to start File Chat with "
					+ userMenuItem.getUserName() + "\n This feature is not implemented in version 0.5");

		} else if (Character.isLetter(commandString.charAt(0))) {
			newGroup = false;
			this.groupName = commandString;
			t = new Thread(this, "JoinGroup");
			t.start();
		} else {
			iconNum = Integer.parseInt(commandString);
			System.out.println("button " + commandString + " is pressed");
		}
	}
	public JList<String> getFileList() {
		return aFrame.getFileList();
	}

	public void updateMenuItemStartFileChat() {
		Hashtable userInfoTable = userInfoManager.getEntireTable();
		UserInfo myUserInfo = userInfoManager.getMyUserInfo();
		Object myAddress = myUserInfo.getAddress();
		for (Enumeration e = userInfoTable.keys(); e.hasMoreElements();) {
			Object address = e.nextElement();
			if (myAddress.equals(address)) {
				continue;
			}
			UserInfo userInfo = (UserInfo) userInfoTable.get(address);
			String userName = userInfo.getUserName();
			UserMenuItem userMenuItem = new UserMenuItem(userName, address);
			userMenuItem.setActionCommand("@" + userName);
			userMenuItem.addActionListener(this);
		}
	}

	public void windowActivated(WindowEvent e) {
		System.out.println("windowActivated");
	}

	public void windowClosed(WindowEvent e) {
		System.out.println("windowClosed");
	}

	public void windowDeactivated(WindowEvent e) {
		System.out.println("windowDeactivated");
	}

	public void windowDeiconified(WindowEvent e) {
		System.out.println("windowDeiconified");
	}

	public void windowIconified(WindowEvent e) {
		System.out.println("windowIconified");
	}

	public void windowOpened(WindowEvent e) {
		System.out.println("windowOpened");
	}


}

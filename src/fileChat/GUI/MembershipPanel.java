package fileChat.GUI;

import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import fileChat.ControlManager.*;
import fileChat.DataManager.*;

public class MembershipPanel extends JPanel implements ActionListener {
	private MembershipManager membershipManager;
	private TextManager textManager;
	JPanel innerPanel;
	JScrollPane scrollPane;
	Object selfAddress;
	JLabel membershipPanelLabel;

	public MembershipPanel(MembershipManager membershipManager, TextManager textManager) {
		this.membershipManager = membershipManager;
		this.textManager = textManager;
		innerPanel = new JPanel();
		if (membershipManager != null) {
			selfAddress = membershipManager.getSelfAddress();
			membershipPanelLabel = new JLabel("Members in Group \"" + membershipManager.getGroupName() + "\"");
		} else {
			selfAddress = null;
			membershipPanelLabel = new JLabel("You Are Not In Any Group");
		}
		Dimension minDim = new Dimension(1, 1);
		setMinimumSize(minDim);
		setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		scrollPane = new JScrollPane(innerPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		innerPanel.setAutoscrolls(true);
		//// *********************
		GridBagLayout gridBag = new GridBagLayout();
		setLayout(gridBag);
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		membershipPanelLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		c.fill = GridBagConstraints.HORIZONTAL;
		gridBag.setConstraints(membershipPanelLabel, c);
		add(membershipPanelLabel);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		gridBag.setConstraints(scrollPane, c);

		/// *********************
		innerPanel.setLayout(new GridLayout(10, 1));

		// add(membershipPanelLabel);
		add(scrollPane);
		innerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		Dimension dim = new Dimension(200, 300);
		scrollPane.setPreferredSize(dim);
		validate();
	}

	public void setMembershipManger(MembershipManager membershipManager) {
		this.membershipManager = membershipManager;
		selfAddress = this.membershipManager.getSelfAddress();
	}

	public void setSelfAddress(Object selfAddress) {
		this.selfAddress = selfAddress;
	}

	public void setTextManager(TextManager textManager) {
		this.textManager = textManager;
	}

	public void resetPanel() {
		innerPanel.removeAll();
		UserInfo myUserInfo = membershipManager.getUserInfoManager().getMyUserInfo();
		String myUsername = myUserInfo.getUserName();
		ImageIcon myIcon = new ImageIcon("/" + myUserInfo.getUniqueNum() + ".GIF");
		JLabel myLabel = new JLabel(myUsername, myIcon, SwingConstants.CENTER);
		innerPanel.add(myLabel);
		setLabel(null);
		validate();
	}

	public void setLabel(String group) {
		if (group != null) {
			membershipPanelLabel.setText("Members in Group \"" + group + "\"");
		} else {
			membershipPanelLabel.setText("You Are Not In Any Group");

		}
	}

	public void updateMembershipPanel(Vector membersVector, UserInfoManager userInfoManager) {
		try {
			if (userInfoManager.getMyUserInfo() == null) {
				setLabel(null);
				return;
			}
			setLabel(membershipManager.getGroupName());
			innerPanel.removeAll();
			innerPanel.validate();
			System.out.println("SelfAddress is " + selfAddress);
			UserInfo myUserInfo = userInfoManager.getMyUserInfo();
			String myUsername = myUserInfo.getUserName();
			ImageIcon myIcon = new ImageIcon(getClass().getResource("/" + myUserInfo.getUniqueNum() + ".GIF"));
			JLabel myLabel = new JLabel(myUsername, myIcon, SwingConstants.CENTER);
			innerPanel.add(myLabel);
			if (selfAddress != null) {
				int size = membersVector.size();
				for (int i = 0; i < size; i++) {
					Object address = membersVector.elementAt(i);
					if (!address.equals(selfAddress)) {
						UserInfo userInfo = userInfoManager.getUserInfo(address);
						if (userInfo != null) {
							Object chatAddress = userInfo.getChatAddress();
							ImageIcon imageIcon = new ImageIcon(
									getClass().getResource("/" + userInfo.getUniqueNum() + ".GIF"));
							UserButton userButton = new UserButton(userInfo.getUserName(), chatAddress,
									userInfo.getUniqueNum(), imageIcon);
							userButton.addActionListener(this);
							innerPanel.add(userButton);

						}
					}
				}
			}
			validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateMembershipPanel(UserInfo userInfo, Object address) {

		if (userInfo != null) {
			ImageIcon imageIcon = new ImageIcon(getClass().getResource("/" + userInfo.getUniqueNum() + ".GIF"));
			UserButton userButton = new UserButton(userInfo.getUserName(), address, userInfo.getUniqueNum(), imageIcon);
			userButton.addActionListener(this);
			innerPanel.add(userButton);
		}

		validate();
	}

	public void actionPerformed(ActionEvent evt) {
		UserButton userButton = (UserButton) evt.getSource();
		textManager.showOwnTextMsg(userButton.getUserName());
		textManager.sendTextMsg(userButton.getAddress());

	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

}

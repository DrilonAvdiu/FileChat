/******************************************************************************
 *JavaGroupsVC version 0.5 beta 
 * 
 * MembershipManager the channel manager that runs 
 * the intra-group membership management protocol
 * It communicates through GUIManager for user events.
 * 
 ******************************************************************************/
package fileChat.ControlManager;

import JavaGroups.*;
import fileChat.Manager;
import fileChat.ControlManager.ControlMessage.*;
import fileChat.ControlManager.ControlMessage.MediaControlMessage.*;
import fileChat.ControlManager.ControlMessage.MediaControlMessage.FileBcastMessage.*;
import fileChat.DataManager.*;
import fileChat.GUI.GUIManager;

import java.util.*;

public class MembershipManager extends Manager implements MembershipListener, MessageListener {
	private Vector currentMembersVector;
	private Vector newMembersVector;
	private View currentView;
	private UserInfo trialUserInfo;
	public boolean isCoordinator;
	private boolean isSecondCoordinator;
	private boolean isAlreadyInGroup;
	private boolean debug;
	private String iconTitle = null;
	private GroupsManager groupsManager;
	boolean isBroadcastingFile = false;
	boolean isFileChatting = false;
	UserInfo fileChatUser = null;
	UserInfo fileBroadcastor = null;

	public MembershipManager(boolean debug, UserInfoManager userInfoManager, ChannelFactory channelFactory,
			GUIManager guiManager, GroupsManager groupsManager) {

		currentMembersVector = new Vector();
		newMembersVector = new Vector();
		channelName = new String();
		this.channelFactory = channelFactory;
		this.userInfoManager = userInfoManager;
		this.guiManager = guiManager;
		this.groupsManager = groupsManager;
		groupName = new String();
		isCoordinator = false;
		isSecondCoordinator = false;
		isAlreadyInGroup = false;
		this.debug = debug;
	}

	/*
	 * This method handles the initial connection of control channel only
	 */

	public boolean connectToGroup(String groupName, String prop) {
		this.groupName = groupName;
		trialUserInfo = userInfoManager.getMyUserInfo();
		channelName = nameChannel(groupName);
		try {
			channel = channelFactory.CreateChannel(prop);
			channel.Connect(channelName);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		adapter = new PullPushAdapter(channel, this, this);
		selfAddress = channel.GetLocalAddress();
		trialUserInfo.setAddress(selfAddress);
		trialUserInfo.setDefaultAddress(groupsManager.getSelfAddress());

		if (debug) {
			System.out.println("Connected successfully to group <" + groupName + ">");
		}

		while (!isAlreadyInGroup) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		guiManager.setLeaveGroupMenuEnable(true);
		guiManager.setJoinGroupEnable(false);
		return true;
	}

	public boolean disconnectFromGroup() {
		groupsManager.sendMemberLeaveGroupMsg(groupName, userInfoManager.getMyUserInfo());
		channel.Disconnect();
		return true;
	}

	private void requestToJoinGroup(UserInfo trialuserInfo) {

		if (debug) {
			System.out.println("Sent JoinRequestMsg to: " + newMembersVector.firstElement());
		}
		try {
			JoinRequestMsg msg = new JoinRequestMsg(newMembersVector.firstElement(), selfAddress,
					Util.ObjectToByteBuffer(trialUserInfo));
			channel.Send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method checks for username uniqueness through control channel
	 */
	String grantMembership(UserInfo userInfo) {
		if (userInfoManager.isInTable(userInfo)) {
			return "name duplication";
		} else {
			Integer tempUIN = new Integer(userInfo.getUniqueNum());
			if (userInfoManager.getAvailableNumVector().contains(tempUIN)) {
				return null;
			} else {
				return "icon duplication";
			}
		}
	}

	public String nameChannel(String groupName) {
		channelName = new String(groupName + "@control");
		return channelName;
	}

	public void Receive(Message msg) {

		if (!isAlreadyInGroup) {
			/*
			 * If it is not already been accepted to a group, can't listen to
			 * other messages
			 */
			if (!((msg instanceof JoinRequestGrantedMsg) || (msg instanceof JoinRequestDeniedMsg)
					|| (msg instanceof InitialMembersReplyMsg) || (msg instanceof IconsInquiryMsg)
					|| (msg instanceof IconsInquiryReplyMsg)))
				return;
		}
		// InitialMembersRequestMsg
		if (msg instanceof InitialMembersRequestMsg) {
			if (debug) {
				System.out.println("Receive InitialMembersRequestMsg from " + msg.GetSrc());
			}
			if (isCoordinator) {
				sendInitialMembers(msg);
			}
			// InitialMembersReplyMsg
		} else if (msg instanceof InitialMembersReplyMsg) {
			if (debug) {
				System.out.println("Receive InitialMembersReplyMsg from " + msg.GetSrc());
			}
			try {
				addInitialMembers((Hashtable) Util.ObjectFromByteBuffer(msg.GetBuffer()));
				userInfoManager.addUser(userInfoManager.getMyUserInfo(), selfAddress);
				isAlreadyInGroup = true;
				guiManager.updateMembershipPanel(currentMembersVector, userInfoManager);
				guiManager.updateMenuItemStartFileChat();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// JoinRequestMsg
		} else if (msg instanceof JoinRequestMsg) {
			if (debug) {
				System.out.println("Receive JoinRequestMsg from " + msg.GetSrc());
			}
			try {
				UserInfo tempUserInfo = (UserInfo) Util.ObjectFromByteBuffer(msg.GetBuffer());
				String grantString = grantMembership(tempUserInfo);
				if (grantString == null) {
					channel.Send(new JoinRequestGrantedMsg(msg.GetSrc(), selfAddress,
							Util.ObjectToByteBuffer(tempUserInfo)));
				} else {
					channel.Send(
							new JoinRequestDeniedMsg(msg.GetSrc(), selfAddress, Util.ObjectToByteBuffer(grantString)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// JoinRequestGrantedMsg
		} else if (msg instanceof JoinRequestGrantedMsg) {
			if (debug) {
				System.out.println("Receive JoinRequestGrantedMsg from " + msg.GetSrc());
			}
			try {
				trialUserInfo = ((UserInfo) Util.ObjectFromByteBuffer(msg.GetBuffer()));

			} catch (Exception e) {
				e.printStackTrace();
			}
			userInfoManager.setMyUserInfo(trialUserInfo);
			guiManager.setTitle();
			iconTitle = null;
			bcastUserInfo(trialUserInfo);
			groupsManager.setUseOldIcon(true);
			if (debug) {
				System.out.println("UseOldIcon is set to false");
			}

			// JoinRequestDeniedMsg
		} else if (msg instanceof JoinRequestDeniedMsg) {
			try {
				if (debug) {
					System.out.println("Receive JoinRequestDeniedMsg from " + msg.GetSrc());
				}
				String grantString = (String) Util.ObjectFromByteBuffer(msg.GetBuffer());
				if (debug) {
					System.out.println("grantString is " + grantString);
				}
				if (grantString.equals("name duplication")) {
					if (debug) {
						System.out.println("grantString is name duplication");
					}

					String trialUsername = GUIManager.promptUsernameInput("Username \"" + trialUserInfo.getUserName()
							+ " \" belongs to someone, please choose a different name");
					if (trialUsername == null) {
						disconnectFromGroup();
						return;
					}
					trialUserInfo.setUserName(trialUsername);
					requestToJoinGroup(trialUserInfo);
				} else if (grantString.equals("icon duplication")) {
					if (debug) {
						System.out.println("grantString is icon duplication");
					}
					groupsManager.setUseOldIcon(false);
					if (debug) {
						System.out.println("UseOldIcon is set to false");
					}
					iconTitle = "The icon you chose belongs to someone, please choose another icon from below";
					channel.Send(new IconsInquiryMsg(msg.GetSrc(), selfAddress, null));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// BcastUserInfoMsg
		} else if (msg instanceof BcastUserInfoMsg) {
			if (msg.GetSrc().equals(selfAddress)) {
				if (debug) {
					System.out.println("Receive own BcastUserInfoMsg");
				}
				return;
			}
			if (debug) {
				System.out.println("Receive BcastUserInfoMsg from " + msg.GetSrc());
			}
			try {
				UserInfo tempUserInfo = (UserInfo) Util.ObjectFromByteBuffer(msg.GetBuffer());
				Object addr = msg.GetSrc();
				userInfoManager.addUser(tempUserInfo, addr);
				guiManager.updateMembershipPanel(tempUserInfo, addr);
				guiManager.updateMenuItemStartFileChat();
				if (isCoordinator) {
					groupsManager.sendMemberJoinGroupMsg(groupName, tempUserInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// IconsInquiryMsg
		} else if (msg instanceof IconsInquiryMsg) {
			if (debug) {
				System.out.println("Receive IconsInquiryMsg from " + msg.GetSrc());
			}

			try {
				channel.Send(new IconsInquiryReplyMsg(msg.GetSrc(), selfAddress,
						Util.ObjectToByteBuffer(userInfoManager.getAvailableNumVector())));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// IconsInquiryReplyMsg
		} else if (msg instanceof IconsInquiryReplyMsg) {
			if (debug) {
				System.out.println("Receive IconsInquiryReplyMsg from " + msg.GetSrc());
			}
			try {
				Vector availableIcons = (Vector) Util.ObjectFromByteBuffer(msg.GetBuffer());
				int num = guiManager.promptUserIcon(availableIcons, iconTitle);
				if (num == -1) { // cancel button is pressed
					disconnectFromGroup();
					// guiManager.getAudioManager().disconnectFromGroup();
					// guiManager.getVideoManager().disconnectFromGroup();
					// guiManager.getTextManager().disconnectFromGroup();
					guiManager.setTitle("");
					userInfoManager.setMyUserInfo(null);
					guiManager.menuItemLeaveGroup.setEnabled(false);
					guiManager.menuJoinGroup.setEnabled(true);
					return;
				}
				trialUserInfo.setUniqueNum(num);
				requestToJoinGroup(trialUserInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// TextChannelBcastMsg
		} else if (msg instanceof TextChannelBcastMsg) {
			if (debug) {
				System.out.println("Receive TextChannelBcastMsg from " + msg.GetSrc());
			}
			try {
				UserInfo newUserInfo = (UserInfo) Util.ObjectFromByteBuffer(msg.GetBuffer());
				UserInfo userInfo = userInfoManager.getUserInfo(msg.GetSrc());
				userInfo.setChatAddress(newUserInfo.getChatAddress());
				if (guiManager.getMembershipPanel() != null) {
					// guiManager.updateMembershipPanel(newMembersVector,userInfoManager);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} 
		// FileChannelBCastMsg
		else if (msg instanceof FileChannelBCastMsg) {
			if (debug) {
				System.out.println("Receive FileChannelBcastMsg from " + msg.GetSrc());
			}
			try {
				UserInfo newUserInfo = (UserInfo) Util.ObjectFromByteBuffer(msg.GetBuffer());
				UserInfo userInfo = userInfoManager.getUserInfo(msg.GetSrc());
				userInfo.setFileAddress(newUserInfo.getFileAddress());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		 else if (msg instanceof StartFileBcastRequestMsg) {
			if (debug) {
				System.out.println("Receive StartFileBcastRequestMsg from " + msg.GetSrc());
			}

			if (isBroadcastingFile) {
				sendStartFileBcastRequestDeniedMsg(msg.GetSrc());
			} else {
				isBroadcastingFile = true;
				fileBroadcastor = userInfoManager.getUserInfo(msg.GetSrc());
				sendStartFileBcastRequestGrantedMsg(msg.GetSrc());
				bcastStartFileBcastMsg(fileBroadcastor);

			}
		}

		else if (msg instanceof StartFileBcastRequestGrantedMsg) {
			if (debug) {
				System.out.println("Receive StartFileBcastRequestGrantedMsg from " + msg.GetSrc());
			}
			FileManager fileManager = guiManager.getFileManager();
			fileManager.setupBroadcastChannel(guiManager.getFile().getPath(), guiManager.getFile().getName(),guiManager.getFileList());
			guiManager.getFileManager().startBroadcastMedia();
		} else if (msg instanceof StartFileBcastRequestDeniedMsg) {
			if (debug) {
				System.out.println("Receive StartFileBcastRequestDeniedMsg from " + msg.GetSrc());
			}

			UserInfo userInfo = null;
			try {
				userInfo = (UserInfo) Util.ObjectFromByteBuffer(msg.GetBuffer());
			} catch (Exception e) {
				e.printStackTrace();
			}
			guiManager.showErrorDialog("Broadcast Channel Busy", userInfo.getUserName()
					+ " is currently using the file broadcast channel \nPlease wait till the channel is idle");
			// guiManager.menuItemOpenAudioFile.setEnabled(false);

		} else if (msg instanceof StartFileBcastMsg) {
			if (debug) {
				System.out.println("Receive StartFileBcastMsg from " + msg.GetSrc());
			}
			isBroadcastingFile = true;
			try {
				fileBroadcastor = (UserInfo) Util.ObjectFromByteBuffer(msg.GetBuffer());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (msg instanceof EndFileBcastMsg) {
			if (debug) {
				System.out.println("Receive EndFileBcastMsg from " + msg.GetSrc());
			}
			clearFileBcastChannel();

		}
		// StartFileChatRequestMsg
		else if (msg instanceof StartFileChatRequestMsg) {

			// StartFileChatRequestGrantedMsg
		} else if (msg instanceof StartFileChatRequestGrantedMsg) {

			// StartFileChatRequestDeniedMsg
		} else if (msg instanceof StartFileChatRequestDeniedMsg) {

		}
	}

	public void sendStartFileBcastRequestGrantedMsg(Object address) {
		if (debug) {
			System.out.println("sent StartFileBcastRequestGrantedMsg to " + address);
			StartFileBcastRequestGrantedMsg msg = new StartFileBcastRequestGrantedMsg(address, selfAddress, null);
			try {
				channel.Send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void sendStartFileBcastRequestDeniedMsg(Object address) {
		if (debug)
			System.out.println("sent StartFileBcastRequestDeniedMsg to " + address);
		StartFileBcastRequestDeniedMsg msg = new StartFileBcastRequestDeniedMsg(address, selfAddress, fileBroadcastor);
		try {
			channel.Send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void Block() {
		if (debug)
			System.out.println("Block() is called");
	}

	public void Suspect(Object address) {
	}

	public void ViewAccepted(View newView) {
		try {
			newMembersVector = newView.GetMembers();
			boolean wasCoordinator = isCoordinator;
			if (debug) {
				System.out.println("previous view is " + currentMembersVector.toString());
				System.out.println("new view is " + newMembersVector.toString());
			}
			int currentSize = currentMembersVector.size();
			int newSize = newMembersVector.size();
			isCoordinator = ifIsCoordinator(newMembersVector);

			if (currentSize == 0 && newSize == 1) {
				if (debug) {
					System.out.println("I am the ONLY one, therefore I am the coordinator!");
				}
				int num = -1;
				if (!groupsManager.getUseOldIcon()) {
					num = guiManager.promptUserIcon(userInfoManager.getAvailableNumVector(), iconTitle);
					if (num == -1) { // cancel button is pressed
						userInfoManager.setMyUserInfo(null);
						guiManager.setTitle("");
						disconnectFromGroup();
						return;

					}
				} else {
					num = userInfoManager.getMyUserInfo().getUniqueNum();
				}
				groupsManager.setUseOldIcon(true);
				trialUserInfo.setUniqueNum(num);
				userInfoManager.addUser(trialUserInfo, selfAddress);
				userInfoManager.setMyUserInfo(trialUserInfo);
				isAlreadyInGroup = true;
				GroupList groupList = new GroupList(groupName, userInfoManager);
				groupsManager.bcastNewGroup(groupList);

			} else if (!isAlreadyInGroup && viewChanged()) {
				requestToJoinGroup(trialUserInfo);
			}
			if (wasCoordinator == true && isCoordinator == false) {
				if (debug) {
					System.out.println("Leadership changed");
				}
				isAlreadyInGroup = false;
				requestToJoinGroup(trialUserInfo);
			}
			if (newSize < currentSize) {
				Vector membersLeft = getMembersLeft(newMembersVector);
				if (isCoordinator) {
					for (int i = 0; i < membersLeft.size(); i++) {
						UserInfo tempUserInfo = userInfoManager.getUserInfo(membersLeft.elementAt(i));
						groupsManager.sendMemberLeaveGroupMsg(groupName, tempUserInfo);
						if (isBroadcastingFile) {
							if (tempUserInfo.getUserName().equals(fileBroadcastor.getUserName())) {
								bcastEndFileBcastMsg();
							}
						}
					}
				}
				userInfoManager.removeUsers(membersLeft);
			}

			if (isAlreadyInGroup && viewChanged()) {
				if (guiManager.getMembershipPanel() != null) {
					guiManager.updateMembershipPanel(newMembersVector, userInfoManager);
					guiManager.updateMenuItemStartFileChat();
				}
			}
			currentView = newView.Copy();
			currentMembersVector = currentView.GetMembers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean ifIsCoordinator(Vector newMembersVector) {
		if (debug) {
			System.out.println("First element is " + newMembersVector.firstElement());
			System.out.println("Myself is " + selfAddress);
		}
		if (newMembersVector.firstElement().equals(selfAddress)) {
			return true;
		} else {
			return false;
		}
	}

	boolean viewChanged() {
		return !(newMembersVector.containsAll(currentMembersVector)
				&& newMembersVector.size() == currentMembersVector.size());
	}

	public String getGroupName() {
		return groupName;
	}

	/*
	 * Return the Vector of addr that are missing from the new view i.e. the
	 * member who just left the group
	 */
	private Vector getMembersLeft(Vector newMembersVector) {
		Vector membersLeft = new Vector();
		int size = currentMembersVector.size();
		for (int i = 0; i < size; i++) {
			if (!newMembersVector.contains(currentMembersVector.elementAt(i))) {
				membersLeft.addElement(currentMembersVector.elementAt(i));
			}
		}
		if (debug) {
			System.out.println("Members who left are: " + membersLeft);
		}
		return membersLeft;

	}

	/*
	 * Return the addr that is new in the new view i.e. the member who just
	 * joined the group
	 */
	private Vector getMembersJoined(Vector newMembersVector) {
		Vector membersJoined = new Vector();
		int size = newMembersVector.size();
		for (int i = 0; i < size; i++) {
			if (!currentMembersVector.contains(newMembersVector.elementAt(i))) {
				membersJoined.addElement(newMembersVector.elementAt(i));
			}
		}
		if (debug) {
			System.out.println("Members who join are: " + membersJoined);
		}
		return membersJoined;
	}

	public Object getSelfAddress() {
		return selfAddress;
	}

	public UserInfoManager getUserInfoManager() {
		return userInfoManager;

	}

	public Vector getCurrentMembersVector() {
		return currentMembersVector;

	}

	public Object getCoordinator() {
		return newMembersVector.firstElement();
	}

	private void addInitialMembers(Hashtable newMembersTable) {
		userInfoManager.setEntireTable(newMembersTable);
	}

	private void sendInitialMembers(Message incomingMsg) {
		if (debug) {
			System.out.println("Sent InitialMembers msg to : " + incomingMsg.GetSrc());
		}

		InitialMembersReplyMsg msg = new InitialMembersReplyMsg(incomingMsg.GetSrc(), selfAddress,
				userInfoManager.getEntireTable());
		try {
			channel.Send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void requestInitialMembers(Object coordinatorAddr) {
		if (debug) {
			System.out.println("Sent requestInitialMembers msg to coordinator: " + coordinatorAddr);
		}
		InitialMembersRequestMsg msg = new InitialMembersRequestMsg(coordinatorAddr, selfAddress, currentMembersVector);
		try {
			channel.Send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void bcastUserInfo(UserInfo userInfo) {
		if (debug) {
			System.out.println("Bcast userInfo: " + userInfo);
		}
		BcastUserInfoMsg bcastMsg = new BcastUserInfoMsg(null, selfAddress, userInfo);
		requestInitialMembers(currentMembersVector.firstElement());
		try {
			channel.Send(bcastMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void bcastTextChannelAddress(Object address) {
		if (debug) {
			System.out.println("Bcast textChannel address: " + address);
		}
		UserInfo userInfo = new UserInfo(null);
		userInfo.setAddress(selfAddress);
		userInfo.setChatAddress(address);
		userInfo.setDefaultAddress(groupsManager.getSelfAddress());
		TextChannelBcastMsg bcastMsg = new TextChannelBcastMsg(null, selfAddress, userInfo);
		try {
			channel.Send(bcastMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void bcastFileChannelAddress(Object address) {
		if (debug) {
			System.out.println("Bcast fileChannel address: " + address);
			UserInfo userInfo = new UserInfo(null);
			userInfo.setAddress(selfAddress);
			userInfo.setChatAddress(address);
			userInfo.setDefaultAddress(groupsManager.getSelfAddress());
			FileChannelBCastMsg bcastMsg = new FileChannelBCastMsg(null, selfAddress, userInfo);
			try {
				channel.Send(bcastMsg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void bcastEndFileBcastMsg() {
		EndFileBcastMsg bcastMsg = new EndFileBcastMsg(null, selfAddress, null);
		try {
			channel.Send(bcastMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void bcastStartFileBcastMsg(UserInfo fileBcastor) {
		StartFileBcastMsg bcastMsg = new StartFileBcastMsg(null, selfAddress, fileBcastor);
		try {
			channel.Send(bcastMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void clearFileBcastChannel() {
		isBroadcastingFile = false;
		fileBroadcastor = null;
	}

	public UserInfo getFileBroadcastor() {
		return fileBroadcastor;
	}

	public void sendStartFileBcastRequestMsg() {
		if (debug)
			System.out.println("sent StartFileBcastRequestMsg to " + getCoordinator());
		StartFileBcastRequestMsg msg = new StartFileBcastRequestMsg(getCoordinator(), selfAddress, null);
		try {
			channel.Send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

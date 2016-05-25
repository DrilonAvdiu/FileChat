package fileChat.ControlManager;/******************************************************************************
																			
																			* JavaGroupsVC version 0.5 beta 
																			* 
																			* GroupsManager is the channel manager that runs 
																			* the inter-group membership management protocol
																			* It communicates through GUIManager for user events.
																			* 
																			*/

import JavaGroups.*;
import fileChat.Manager;
import fileChat.ControlManager.ControlMessage.*;
import fileChat.ControlManager.ControlMessage.GroupListMessage.*;
import fileChat.DataManager.*;
import fileChat.GUI.GUIManager;

import java.util.*;
import javax.swing.*;

public class GroupsManager extends Manager implements MembershipListener, MessageListener {

	GroupListManager groupListManager;
	Vector currentMembersVector;
	Vector newMembersVector;
	boolean debug;
	UserInfo trialUserInfo;
	MembershipManager membershipManager;
	TextManager textManager;
	FileManager fileManager;
	boolean useOldIcon = false;

	public GroupsManager(boolean debug, UserInfoManager userInfoManager, ChannelFactory channelFactory,
			GUIManager guiManager) {
		this.debug = debug;
		groupListManager = new GroupListManager(debug);
		channelName = new String();
		currentMembersVector = new Vector();
		newMembersVector = new Vector();
		this.channelFactory = channelFactory;
		this.userInfoManager = userInfoManager;
		this.guiManager = guiManager;
		groupName = new String();
		this.debug = debug;
	}

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
		if (trialUserInfo != null) {
			trialUserInfo.setAddress(selfAddress);
		}
		if (debug) {
			System.out.println("Connected successfully to group <" + groupName + ">");
		}
		return true;
	}

	public boolean disconnectFromGroup() {
		textManager.disconnectFromGroup();
		// audioManager.disconnectFromGroup();
		// videoManager.disconnectFromGroup();
		membershipManager.disconnectFromGroup();
		return true;
	}

	public Object getSelfAddress() {
		return selfAddress;
	}

	public String nameChannel(String groupName) {
		channelName = new String(groupName + "@server");
		return channelName;
	}

	public void setMembershipManager(MembershipManager membershipManager) {
		this.membershipManager = membershipManager;
	}

	public void setTextManager(TextManager textManager) {
		this.textManager = textManager;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setUseOldIcon(boolean useOldIcon) {
		this.useOldIcon = useOldIcon;
	}

	public boolean getUseOldIcon() {
		return useOldIcon;
	}

	public GroupListManager getGroupListManager() {
		return groupListManager;
	}

	public ChannelFactory getChannelFactory() {
		return channelFactory;
	}

	public void Receive(Message msg) {
		// if(msg.GetSrc().equals(selfAddress)) return;
		if (msg instanceof MemberLeaveGroupMsg) {
			if (debug)
				System.out.println("Received MemberLeaveGroupMsg from " + msg.GetSrc());
			MemberLeaveGroupMsg memberLeaveGroupMsg = (MemberLeaveGroupMsg) msg;
			String groupName = memberLeaveGroupMsg.groupName;
			UserInfo memberLeft = memberLeaveGroupMsg.memberLeft;
			groupListManager.removeMemberFromGroup(groupName, memberLeft);
			if (groupListManager.isGroupEmpty(groupName)) {
				groupListManager.removeGroup(groupName);
			}
			guiManager.updateGroupsPanel(groupListManager);
			guiManager.updateMenuItemJoinGroup();
		} else if (msg instanceof MemberJoinGroupMsg) {
			if (debug)
				System.out.println("Received MemberJoinGroupMsg from " + msg.GetSrc());
			try {
				MemberJoinGroupMsg memberJoinGroupMsg = (MemberJoinGroupMsg) msg;
				String groupName = memberJoinGroupMsg.groupName;
				UserInfo memberJoined = memberJoinGroupMsg.memberJoined;
				groupListManager.addMemberToGroup(groupName, memberJoined);
				guiManager.updateGroupsPanel(groupListManager);
				guiManager.updateMenuItemJoinGroup();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (msg instanceof GroupListRequestMsg) {
			if (debug)
				System.out.println("Received GroupListRequestMsg from " + msg.GetSrc());
			sendGroupListReply(msg.GetSrc());
		} else if (msg instanceof GroupListReplyMsg) {
			if (debug)
				System.out.println("Received GroupListReplyMsg from " + msg.GetSrc());
			try {
				Hashtable groupListTable = (Hashtable) Util.ObjectFromByteBuffer(msg.GetBuffer());
				groupListManager.setGroupListTable(groupListTable);
				guiManager.updateGroupsPanel(groupListManager);
				guiManager.updateMenuItemJoinGroup();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (msg instanceof BcastNewGroupMsg) {
			try {
				if (debug)
					System.out.println("Received BcastNewGroupMsg from " + msg.GetSrc());
				BcastNewGroupMsg bcastNewGroupMsg = (BcastNewGroupMsg) msg;
				groupListManager.addGroup(bcastNewGroupMsg.groupList);
				guiManager.updateGroupsPanel(groupListManager);
				guiManager.updateMenuItemJoinGroup();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void ViewAccepted(View newView) {
		try {
			newMembersVector = newView.GetMembers();
			if (debug)
				System.out.println("<<<<View accepted in GroupManager>>>>");

			if (debug)
				System.out.println("The old view is " + currentMembersVector);
			if (debug)
				System.out.println("The new view is " + newMembersVector);

			int oldSize = currentMembersVector.size();
			int newSize = newMembersVector.size();

			Object coordinator = newMembersVector.elementAt(0);
			if (!selfAddress.equals(coordinator)) {
				sendGroupListRequest(coordinator);
			}

			if (newSize < oldSize) {

				Vector membersLeft = getMembersLeft();
				if (debug)
					System.out.println("Member left is " + membersLeft);

				int size = membersLeft.size();
				for (int i = 0; i < size; i++) {
					String groupName = (String) groupListManager.addrToGroupNameTable.get(membersLeft.elementAt(i));
					if (debug)
						System.out.println("This member belonged to group " + groupName);
					if (groupName != null) {
						GroupList groupList = (GroupList) groupListManager.nameToGroupListTable.get(groupName);
						if (groupList.userInfoManager.getEntireTable().size() == 1) {
							groupListManager.removeGroup(groupName);
						}
					}
				}
				guiManager.updateGroupsPanel(groupListManager);
				guiManager.updateMenuItemJoinGroup();
			}

			View currentView = newView.Copy();
			currentMembersVector = currentView.GetMembers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Vector getMembersLeft() {
		Vector membersLeft = new Vector();
		int size = currentMembersVector.size();
		for (int i = 0; i < size; i++) {
			if (!newMembersVector.contains(currentMembersVector.elementAt(i))) {
				membersLeft.addElement(currentMembersVector.elementAt(i));
			}
		}
		if (debug) {
			System.out.println("Members who left groups are: " + membersLeft);
		}
		return membersLeft;

	}

	private void sendGroupListRequest(Object address) {
		try {
			GroupListRequestMsg groupListRequestMsg = new GroupListRequestMsg(address, selfAddress, null);
			channel.Send(groupListRequestMsg);
			if (debug)
				System.out.println("Sent GroupListRequestMsg to " + address);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sendGroupListReply(Object address) {
		try {
			Hashtable groupListTable = groupListManager.getGroupListTable();
			GroupListReplyMsg groupListReplyMsg = new GroupListReplyMsg(address, selfAddress, groupListTable);
			channel.Send(groupListReplyMsg);
			if (debug)
				System.out.println("Sent GroupListReplyMsg to " + address);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void bcastNewGroup(GroupList groupList) {
		try {
			BcastNewGroupMsg bcastNewGroupMsg = new BcastNewGroupMsg(null, selfAddress, null, groupList);
			channel.Send(bcastNewGroupMsg);
			if (debug)
				System.out.println("Sent BcastNewGroupMsg to all for groupName " + groupList.groupName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMemberLeaveGroupMsg(String groupName, UserInfo memberLeft) {
		try {
			MemberLeaveGroupMsg memberLeaveGroupMsg = new MemberLeaveGroupMsg(null, selfAddress, null, groupName,
					memberLeft);
			channel.Send(memberLeaveGroupMsg);
			if (debug)
				System.out.println("Sent MemberLeaveGroupMsg to all:" + memberLeft + " left group " + groupName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMemberJoinGroupMsg(String groupName, UserInfo memberJoined) {
		try {
			MemberJoinGroupMsg memberJoinGroupMsg = new MemberJoinGroupMsg(null, selfAddress, null, groupName,
					memberJoined);
			channel.Send(memberJoinGroupMsg);
			if (debug)
				System.out.println("Sent MemberJoinGroupMsg to all:" + memberJoined + " joined group " + groupName);

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
}


/*******************************************************************************
 *  JavaGroupsVC Version 0.5 beta 
 *  It is a open source project 
 *  It is an extension package for reliable groups communication toolkit 
 *  JavaGroups developed at Cornell University
 *  
 *  Author: Ng, Hooi Ming (hooiming@cs.cornell.edu)
 *  May 20, 1999 
 * *****************************************************************************/

import JavaGroups.*;
import fileChat.ControlManager.*;
import fileChat.GUI.*;

public class JavaGroupsVC {
	public static void main(String[] args) {
		boolean debug = true;
		String defaultGroup = "DefaultGroup";
		GUIManager guiManager = new GUIManager(debug);
		UserInfoManager userInfoManager = new UserInfoManager(debug, null);
		ChannelFactory channelFactory = new JChannelFactory();
		GroupsManager groupsManager = new GroupsManager(debug, userInfoManager, channelFactory, guiManager);
		groupsManager.connectToGroup(defaultGroup, "UDP:PING:FD:GMS");
		guiManager.setGroupsManger(groupsManager);
		guiManager.createChatPanel();
		guiManager.createMembershipPanel();
		guiManager.addTextPanel();
		guiManager.createGroupsPanel();
		guiManager.createFileSendButton();
		guiManager.createFrameOfAcceptedFiles();
		guiManager.createFrameOfAcceptedFilesButton();
		guiManager.setVisible(true);

	}

}

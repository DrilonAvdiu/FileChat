/*****************************************************************************
 * JavaGroupsVC version 0.5 beta
 * 
 * Manager is an abstract class that defines basic variables and methods for
 * a channel Manager.
 * Extended by MembershipManager, GroupsManager and MediaManager 
 * 
 ****************************************************************************/
package fileChat;

import JavaGroups.*;
import fileChat.ControlManager.*;
import fileChat.GUI.GUIManager;

public abstract class Manager implements MessageListener,MembershipListener
{
	protected Channel channel;
	protected String channelName;
	protected String groupName;
	protected Object selfAddress;
	protected PullPushAdapter adapter;
	protected UserInfoManager userInfoManager;
	protected GUIManager guiManager;
	protected ChannelFactory channelFactory;
	public abstract boolean connectToGroup(String groupName,String prop);
	public abstract boolean disconnectFromGroup();
	public abstract String nameChannel(String groupName);

}
																					 
	

		
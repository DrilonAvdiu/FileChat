package fileChat.ControlManager;

/******************************************************************************
 *JavaGroupsVC version 0.5 beta 
 * 
 *GroupList is the data structure for storing information about a group
 *Used by GroupListManager and GroupsManager
 ******************************************************************************/
import java.util.*;

public class GroupList implements java.io.Serializable {
	public String groupName;
	public UserInfoManager userInfoManager;

	public GroupList(String groupName, UserInfoManager userInfoManager) {
		this.groupName = groupName;
		this.userInfoManager = userInfoManager;
	}

	public String toString() {
		return "Group: " + groupName + "\nTable now has " + userInfoManager.getEntireTable();

	}
}

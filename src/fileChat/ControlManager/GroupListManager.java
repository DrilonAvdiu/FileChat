package fileChat.ControlManager;
/******************************************************************************
 *JavaGroupsVC version 0.5 beta 
 * 
 * GroupListManager is the data structure manager for GroupList
 * It handles operation of adding and deleting groups as well as adding
 * and deleting members in a GroupList
 ******************************************************************************/
import java.util.*;
public class GroupListManager implements java.io.Serializable
{
	Hashtable nameToGroupListTable;
	Hashtable addrToGroupNameTable;
	GroupList myGroupList;
	boolean debug;
	
	public GroupListManager(boolean debug){
		this.debug=debug;
		nameToGroupListTable=new Hashtable();
		addrToGroupNameTable=new Hashtable();
	}
	
	public Hashtable getGroupListTable(){
		if(debug){System.out.println("Now the table has:" +nameToGroupListTable);}		
		if(debug){System.out.println("And the table has:" +addrToGroupNameTable);}		
		return nameToGroupListTable;
	}
	
	public GroupList getGroupList(String groupName){
		if(debug){System.out.println("Now the table has:" +nameToGroupListTable);}		
		if(debug){System.out.println("And the table has:" +addrToGroupNameTable);}		
		return (GroupList)nameToGroupListTable.get(groupName);
	}
	public boolean isInTable(String groupName){
		if(debug){System.out.println("Now the table has:" +nameToGroupListTable);}		
		if(debug){System.out.println("And the table has:" +addrToGroupNameTable);}		
		return nameToGroupListTable.containsKey(groupName);
	}
	
	public void addGroup(GroupList groupList){
		try{
			String groupName=groupList.groupName;
			if(!isInTable(groupName)){
				nameToGroupListTable.put(groupName,groupList);
				UserInfo firstMember=groupList.userInfoManager.getMyUserInfo();
				addrToGroupNameTable.put(firstMember.getDefaultAddress(),groupName);

				if(debug){System.out.println("Added group:"+groupList);}				
			}else{
				GroupList oldGroupList=(GroupList)getGroupList(groupName);
				if(debug)System.out.println("Old list is "+ oldGroupList);
				if(debug)System.out.println("New list is "+ groupList);		
			}
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public void removeGroup(String groupName){
		
		GroupList groupList=(GroupList)nameToGroupListTable.remove(groupName);
		Hashtable addrToUserInfoTable=groupList.userInfoManager.getEntireTable();
		for (Enumeration e=addrToUserInfoTable.keys();e.hasMoreElements();){
			Object address=e.nextElement();
			UserInfo userInfo=(UserInfo)addrToUserInfoTable.get(address);
			addrToGroupNameTable.remove(userInfo.getDefaultAddress());
		}
	}
	
	public void setGroupListTable(Hashtable nameToGroupListTable){
		this.nameToGroupListTable=nameToGroupListTable;
		
		for (Enumeration e=nameToGroupListTable.keys();e.hasMoreElements();){
			String groupName=(String)e.nextElement();
			GroupList groupList=(GroupList)nameToGroupListTable.get(groupName);
			Hashtable addrToUserInfoTable=groupList.userInfoManager.getEntireTable();
			for(Enumeration ee=addrToUserInfoTable.keys();ee.hasMoreElements();){
				Object address=ee.nextElement();
				UserInfo userInfo=(UserInfo)addrToUserInfoTable.get(address);
				addrToGroupNameTable.put(userInfo.getDefaultAddress(),groupName);
			}

		}
		
		if(debug){System.out.println("Now the table has:" +nameToGroupListTable);}		
		if(debug){System.out.println("And the table has:" +addrToGroupNameTable);}		

	}
	
	public void setMyGroupList(GroupList groupList){
		this.myGroupList=groupList;
	}
	
	public void addMemberToGroup(String groupName,UserInfo memberJoined){
		GroupList groupList=(GroupList)nameToGroupListTable.get(groupName);
		groupList.userInfoManager.addUser(memberJoined, memberJoined.getAddress());
		addrToGroupNameTable.put(memberJoined.getDefaultAddress(),groupName);
		if(debug)System.out.println("After added member "+memberJoined+" "+groupList);
		if(debug){System.out.println("Now the table has:" +nameToGroupListTable);}		
		if(debug){System.out.println("And the table has:" +addrToGroupNameTable);}		
	}
	
	//if groupSize become zero, return true
	public void removeMemberFromGroup(String groupName,UserInfo memberLeft){
		GroupList groupList=(GroupList)nameToGroupListTable.get(groupName);
		groupList.userInfoManager.removeUser(memberLeft.getAddress());
		addrToGroupNameTable.remove(memberLeft.getDefaultAddress());
		if(debug)System.out.println("After removed member "+memberLeft+" "+groupList);	
		if(debug){System.out.println("Now the table has:" +nameToGroupListTable);}		
		if(debug){System.out.println("And the table has:" +addrToGroupNameTable);}
		
	}
	public boolean isGroupEmpty(String groupName){
		GroupList groupList=(GroupList)nameToGroupListTable.get(groupName);
		return (groupList.userInfoManager.getEntireTable().size()==0);
	}
	
	
}


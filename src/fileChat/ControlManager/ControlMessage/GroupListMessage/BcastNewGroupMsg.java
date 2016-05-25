package fileChat.ControlManager.ControlMessage.GroupListMessage;

import fileChat.ControlManager.GroupList;

public class BcastNewGroupMsg extends JavaGroups.Message
{
	public GroupList groupList;
	
	public BcastNewGroupMsg(Object dest, Object src,byte[] buffer,GroupList groupList){
	  super(dest,src,buffer);
		this.groupList=groupList;
	}
	public BcastNewGroupMsg(Object dest, Object src,Object obj,GroupList groupList){
		super(dest,src,obj);
		this.groupList=groupList;
	}
}

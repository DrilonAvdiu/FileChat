package fileChat.ControlManager.ControlMessage.GroupListMessage;

import fileChat.ControlManager.*;

public class MemberLeaveGroupMsg extends JavaGroups.Message
{
	public String groupName;
	public UserInfo memberLeft;
	
		public MemberLeaveGroupMsg(Object dest, Object src,byte[] buffer,
															 String groupName,UserInfo memberLeft){
		super(dest,src,buffer);
		this.groupName=groupName;
		this.memberLeft=memberLeft;
	}
	public MemberLeaveGroupMsg(Object dest, Object src,Object obj,
														 String groupName,UserInfo memberLeft){
		super(dest,src,obj);
		this.groupName=groupName;
		this.memberLeft=memberLeft;
	}
}

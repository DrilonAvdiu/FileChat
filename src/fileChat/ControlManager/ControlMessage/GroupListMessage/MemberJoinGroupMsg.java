package fileChat.ControlManager.ControlMessage.GroupListMessage;
 
import fileChat.ControlManager.*;

public class MemberJoinGroupMsg extends JavaGroups.Message
{
	public String groupName;
	public UserInfo memberJoined;
	
	public MemberJoinGroupMsg(Object dest, Object src,byte[] buffer,
														String groupName,UserInfo memberJoined){
		super(dest,src,buffer);
		this.groupName=groupName;
		this.memberJoined=memberJoined;
	}
	public MemberJoinGroupMsg(Object dest, Object src,Object obj,
														String groupName,UserInfo memberJoined){
		super(dest,src,obj);
		this.groupName=groupName;
		this.memberJoined=memberJoined;
	}
}

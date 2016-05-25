package fileChat.ControlManager.ControlMessage;

public class InitialMembersReplyMsg extends JavaGroups.Message{
	
	public InitialMembersReplyMsg(Object dest, Object src,byte[] buffer){
		super(dest,src,buffer);
	}
	public InitialMembersReplyMsg(Object dest, Object src,Object obj){
		super(dest,src,obj);
	}
}

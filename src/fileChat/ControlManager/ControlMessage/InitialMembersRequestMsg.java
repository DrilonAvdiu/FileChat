package fileChat.ControlManager.ControlMessage;

public class InitialMembersRequestMsg extends JavaGroups.Message{
	public InitialMembersRequestMsg(Object dest, Object src,byte[] buffer){
	super(dest,src,buffer);
	}
	public InitialMembersRequestMsg(Object dest, Object src,Object obj){
		super(dest,src,obj);
	}
}

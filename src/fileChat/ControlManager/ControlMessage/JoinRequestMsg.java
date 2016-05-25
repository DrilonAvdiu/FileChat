package fileChat.ControlManager.ControlMessage;

public class JoinRequestMsg extends JavaGroups.Message{
	public JoinRequestMsg(Object dest, Object src,byte[] buffer){
	super(dest,src,buffer);
	}
	public JoinRequestMsg(Object dest, Object src,Object obj){
		super(dest,src,obj);
	}
	
}

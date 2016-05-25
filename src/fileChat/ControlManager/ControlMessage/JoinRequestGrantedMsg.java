package fileChat.ControlManager.ControlMessage;

public class JoinRequestGrantedMsg extends JavaGroups.Message{
	public JoinRequestGrantedMsg(Object dest, Object src,byte[] buffer){
	super(dest,src,buffer);
	}
	public JoinRequestGrantedMsg(Object dest, Object src,Object obj){
		super(dest,src,obj);
	}
	
}

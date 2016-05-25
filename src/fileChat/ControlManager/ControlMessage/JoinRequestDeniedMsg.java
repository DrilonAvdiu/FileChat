package fileChat.ControlManager.ControlMessage;

public class JoinRequestDeniedMsg extends JavaGroups.Message{
	public JoinRequestDeniedMsg(Object dest, Object src,byte[] buffer){
	super(dest,src,buffer);
	}
	public JoinRequestDeniedMsg(Object dest, Object src,Object obj){
		super(dest,src,obj);
	}
}

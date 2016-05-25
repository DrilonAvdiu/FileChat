package fileChat.ControlManager.ControlMessage;

public class BcastUserInfoMsg extends JavaGroups.Message
{
	
	public BcastUserInfoMsg(Object dest, Object src,byte[] buffer){
	super(dest,src,buffer);
	}
	public BcastUserInfoMsg(Object dest, Object src,Object obj){
		super(dest,src,obj);
	}
}

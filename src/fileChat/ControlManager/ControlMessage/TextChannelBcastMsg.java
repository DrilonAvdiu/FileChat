package fileChat.ControlManager.ControlMessage;

public class TextChannelBcastMsg extends JavaGroups.Message
{
	public TextChannelBcastMsg(Object dest, Object src,byte[] buffer){
	super(dest,src,buffer);
	}
	public TextChannelBcastMsg(Object dest, Object src,Object obj){
		super(dest,src,obj);
	}
}

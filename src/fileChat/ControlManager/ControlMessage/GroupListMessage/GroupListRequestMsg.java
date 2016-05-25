package fileChat.ControlManager.ControlMessage.GroupListMessage;

public class GroupListRequestMsg extends JavaGroups.Message
{
		public GroupListRequestMsg(Object dest, Object src,byte[] buffer){
	super(dest,src,buffer);
	}
	public GroupListRequestMsg(Object dest, Object src,Object obj){
		super(dest,src,obj);
	}
}

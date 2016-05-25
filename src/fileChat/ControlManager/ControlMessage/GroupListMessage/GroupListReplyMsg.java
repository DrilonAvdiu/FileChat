package fileChat.ControlManager.ControlMessage.GroupListMessage;



public class GroupListReplyMsg extends JavaGroups.Message
{
	public GroupListReplyMsg(Object dest, Object src,byte[] buffer){
		super(dest,src,buffer);
	}
	public GroupListReplyMsg(Object dest, Object src,Object obj){
		super(dest,src,obj);
	}
}

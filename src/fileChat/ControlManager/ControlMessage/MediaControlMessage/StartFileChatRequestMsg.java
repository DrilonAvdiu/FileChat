package fileChat.ControlManager.ControlMessage.MediaControlMessage;

public class StartFileChatRequestMsg extends JavaGroups.Message
{	
	public StartFileChatRequestMsg(Object dest, Object src,byte[] buffer){
			super(dest,src,buffer);
		}
		public StartFileChatRequestMsg(Object dest, Object src,Object obj){
			super(dest,src,obj);
		}
}

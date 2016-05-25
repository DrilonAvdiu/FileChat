package fileChat.ControlManager.ControlMessage.MediaControlMessage;

public class StartFileChatRequestDeniedMsg extends JavaGroups.Message
{
	
	public StartFileChatRequestDeniedMsg(Object dest, Object src,byte[] buffer){
			super(dest,src,buffer);
		}
		public StartFileChatRequestDeniedMsg(Object dest, Object src,Object obj){
			super(dest,src,obj);
		}
}


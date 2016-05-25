package fileChat.ControlManager.ControlMessage.MediaControlMessage;

public class StartFileChatRequestGrantedMsg extends JavaGroups.Message
{	
	public StartFileChatRequestGrantedMsg(Object dest, Object src,byte[] buffer){
			super(dest,src,buffer);
		}
		public StartFileChatRequestGrantedMsg(Object dest, Object src,Object obj){
			super(dest,src,obj);
		}
}


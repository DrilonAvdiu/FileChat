package fileChat.ControlManager.ControlMessage.MediaControlMessage.FileBcastMessage;

public class StartFileBcastRequestGrantedMsg extends JavaGroups.Message {

	public StartFileBcastRequestGrantedMsg(Object src, Object dest, byte[] buffer) {
		super(src, dest, buffer);
	}

	public StartFileBcastRequestGrantedMsg(Object src, Object dest, Object obj) {
		super(src, dest, obj);
	}
}

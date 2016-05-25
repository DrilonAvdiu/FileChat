package fileChat.ControlManager.ControlMessage.MediaControlMessage.FileBcastMessage;

public class StartFileBcastRequestMsg extends JavaGroups.Message {

	public StartFileBcastRequestMsg(Object src, Object dest, byte[] buffer) {
		super(src, dest, buffer);
	}

	public StartFileBcastRequestMsg(Object src, Object dest, Object obj) {
		super(src, dest, obj);
	}
}

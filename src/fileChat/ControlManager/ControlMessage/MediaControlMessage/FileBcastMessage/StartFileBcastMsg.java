package fileChat.ControlManager.ControlMessage.MediaControlMessage.FileBcastMessage;

public class StartFileBcastMsg extends JavaGroups.Message {

	public StartFileBcastMsg(Object src, Object dest, byte[] buffer) {
		super(src, dest, buffer);
	}

	public StartFileBcastMsg(Object src, Object dest, Object obj) {
		super(src, dest, obj);
	}
}

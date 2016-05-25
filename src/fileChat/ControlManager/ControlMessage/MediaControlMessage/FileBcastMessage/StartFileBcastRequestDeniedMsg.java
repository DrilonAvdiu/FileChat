package fileChat.ControlManager.ControlMessage.MediaControlMessage.FileBcastMessage;

public class StartFileBcastRequestDeniedMsg extends JavaGroups.Message {

	public StartFileBcastRequestDeniedMsg(Object src, Object dest, byte[] buffer) {
		super(src, dest, buffer);
	}

	public StartFileBcastRequestDeniedMsg(Object src, Object dest, Object obj) {
		super(src, dest, obj);
	}
}

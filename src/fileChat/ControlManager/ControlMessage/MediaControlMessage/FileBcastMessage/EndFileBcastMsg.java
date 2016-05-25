package fileChat.ControlManager.ControlMessage.MediaControlMessage.FileBcastMessage;

public class EndFileBcastMsg extends JavaGroups.Message {

	public EndFileBcastMsg(Object src, Object dest, byte[] buffer) {
		super(src, dest, buffer);
	}

	public EndFileBcastMsg(Object src, Object dest, Object obj) {
		super(src, dest, obj);
	}
}

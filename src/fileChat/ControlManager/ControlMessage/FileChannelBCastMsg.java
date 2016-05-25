package fileChat.ControlManager.ControlMessage;

public class FileChannelBCastMsg extends JavaGroups.Message {

	public FileChannelBCastMsg(Object src, Object dest, byte[] buffer) {
		super(src, dest, buffer);
	}

	public FileChannelBCastMsg(Object src, Object dest, Object obj) {
		super(src, dest, obj);
	}
}

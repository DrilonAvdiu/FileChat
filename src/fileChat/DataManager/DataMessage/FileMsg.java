package fileChat.DataManager.DataMessage;
import JavaGroups.*;

public class FileMsg extends Message
{
	
	public int length;
	public int seqNum;

	
	public FileMsg(Object dest,Object src,byte[] data,int length,int seqNum){
		super(dest,src,data);	
		this.length=length;
		this.seqNum=seqNum;

	}
}

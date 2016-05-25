package fileChat.DataManager.DataMessage;

public class StartFileMsg extends JavaGroups.Message
{
	public int length;
	public int seqNum;
	
	public StartFileMsg(Object dest,Object src,byte[] data,int length,int seqNum){
		super(dest,src,data);
		this.length=length;
		this.seqNum=seqNum;
	}

}

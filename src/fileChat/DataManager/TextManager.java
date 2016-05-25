package fileChat.DataManager;
/******************************************************************************
 * JavaGroupsVC version 0.5 beta 
 * 
 * TextManager is the channel manager for the text channel 
 * It communicates through GUIManager for user events.
 * 
 ******************************************************************************/
import JavaGroups.*;
import fileChat.ControlManager.*;
import fileChat.DataManager.DataMessage.*;
import fileChat.GUI.GUIManager;


public class TextManager extends MediaManager
{
	public TextManager(boolean debug,UserInfoManager userInfoManager,ChannelFactory channeFactory,GUIManager guiManager){		
		super(debug, userInfoManager, channeFactory, guiManager);			
	}
	
	public String nameChannel(String groupName){
		channelName=new String(groupName+"@chat");
		return channelName;
	}

	public boolean connectToGroup(String groupName,String prop){
		this.groupName=groupName;
		channelName=nameChannel(groupName);
		try{
			channel=channelFactory.CreateChannel(prop);
			channel.Connect(channelName);		}catch (Exception e)		{
			e.printStackTrace();
			return false;		}		
		adapter=new PullPushAdapter(channel,this,this);
		selfAddress=userInfoManager.getMyUserInfo().getAddress();
		channelAddress=channel.GetLocalAddress();
		userInfoManager.getMyUserInfo().setChatAddress(channelAddress);
		guiManager.getMembershipManager().bcastTextChannelAddress(channelAddress);
		if(debug)
		{
			System.out.println("Connected successfully to channel <"+channelName+"> of group <"+groupName+">");
		}				
		return true;
	}
	public boolean disconnectFromGroup(){
		channel.Disconnect();
		return true;
	}
	
	public UserInfoManager getUserInfoManager(){
		return userInfoManager;
	}
	public void startCaptureMedia(){
		
	}

	public void Receive(Message msg){
		try{
			if(msg instanceof TextMsg){
				if(debug){System.out.println("Received TextMsg from "+ msg.GetSrc());}
				Object src=msg.GetSrc();
				Object dest=msg.GetDest();
				String strMsg =(String)Util.ObjectFromByteBuffer(msg.GetBuffer());
				String user=userInfoManager.getUserName(src);
				if(dest.equals(channelAddress)){
					guiManager.getChatPanel().showMsg("*"+user+":"+strMsg+"*");	
				}else{
					guiManager.getChatPanel().showMsg(user+":"+strMsg);
				}
			}
		}catch (Exception e){e.printStackTrace();}
	}	
	
	public void sendTextMsg(Object dest){
		String strMsg=guiManager.getChatPanel().getMsg();
		guiManager.getChatPanel().clearTextMsg();
		TextMsg textMsg=new TextMsg(dest,selfAddress,strMsg);
		try{
			if(debug){System.out.println("Sent TextMsg to "+dest);}
			channel.Send(textMsg);
		}catch (Exception e){e.printStackTrace();}
	}
	public void showOwnTextMsg(String user){
		String msgStr=guiManager.getChatPanel().getMsg();
		guiManager.getChatPanel().showMsg(userInfoManager.getMyUserInfo().getUserName()
																			+"*"+user+":"+msgStr+"*");
	}
	public void bcastTextMsg(String strMsg){
		TextMsg textMsg=new TextMsg(null,selfAddress,strMsg);
		try{
			if(debug){System.out.println("Bcast TextMsg");}
			channel.Send(textMsg);
		}catch (Exception e){e.printStackTrace();}
	}
	
	public void Suspect(Object address){}
	public void ViewAccepted(View newView){	}
}
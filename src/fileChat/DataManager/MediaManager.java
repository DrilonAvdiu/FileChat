package fileChat.DataManager;

/******************************************************************************
 * JavaGroupsVC version 0.5 beta 
 * 
 * MediaManager is the super class for all data channel Manager
 * It defines the common channel operaions such as connect to group
 * Extended by FileManager
 * 
 ******************************************************************************/
import JavaGroups.*;
import fileChat.Manager;
import fileChat.ControlManager.*;
import fileChat.GUI.GUIManager;
import java.io.*;

import javax.swing.JList;


public class MediaManager extends Manager {
	boolean debug;
	Object channelAddress = null;
	FileInputStream fileInputStream;
	boolean receivingData = false;
	boolean ignoreCurrentSession = false;
	final int filePacketSize = 24576*2;
	final int filePacketDelay = 500;
	String fileName="";
	public MediaManager(boolean debug, UserInfoManager userInfoManager, ChannelFactory channelFactory,
			GUIManager guiManager) {

		this.debug = debug;
		this.userInfoManager = userInfoManager;
		this.userInfoManager = userInfoManager;
		this.channelFactory = channelFactory;
		this.guiManager = guiManager;

	}

	public boolean connectToGroup(String groupName, String prop) {
		this.groupName = groupName;
		channelName = nameChannel(groupName);
		try {
			channel = channelFactory.CreateChannel(prop);
			channel.Connect(channelName);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		adapter = new PullPushAdapter(channel, this, this);
		selfAddress = userInfoManager.getMyUserInfo().getAddress();
		if (debug) {
			System.out.println("Connected successfully to channel <" + channelName + "> of group <" + groupName + ">");
		}
		return true;
	}

	public boolean disconnectFromGroup() {
		channel.Disconnect();
		return true;
	}

	public String nameChannel(String groupName) {
		return groupName + "genericChannel";
	}

	public void setupCaptureChannel(String fileName) {

		/*
		 * Not implemented in version 0.5 Need JMF2.0 for the capture
		 * 
		 */
	}

	public void ignoreCurrentSession() {
		ignoreCurrentSession = true;
	}

	public void setupBroadcastChannel(String filePath, String fileName, JList lista) {
		try {
			this.fileName = fileName;
			fileInputStream = new FileInputStream(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void Receive(Message msg) {
	}

	public void Block() {
	}

	public void Suspect(Object address) {
	}

	public void ViewAccepted(View newView) {
	}

}

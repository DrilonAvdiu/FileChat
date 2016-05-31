package fileChat.DataManager;

import JavaGroups.*;
import fileChat.ControlManager.*;
import fileChat.DataManager.DataMessage.*;
import fileChat.GUI.GUIManager;
import fileChat.Media.Capture.*;

import java.io.*;
import javax.swing.*;

public class FileManager extends MediaManager {
	FileReaderThread fileReaderThread;
	FileOutputStream fileOutputStream;
	String name = "";
	JList<String> lista;
	TextManager textManager;
	public FileManager(boolean debug, UserInfoManager userInfoManager, ChannelFactory channelFactory,
			GUIManager guiManager, TextManager textManager) {
		super(debug, userInfoManager, channelFactory, guiManager);
		lista = guiManager.getFileList();
		this.textManager=textManager;
	}

	public String nameChannel(String groupName) {
		channelName = new String(groupName + "@file");
		return channelName;
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
		channelAddress = channel.GetLocalAddress();
		userInfoManager.getMyUserInfo().setFileAddress(channelAddress);
		guiManager.getMembershipManager().bcastFileChannelAddress(channelAddress);
		if (debug) {
			System.out.println("Connected successfully to channel <" + channelName + "> of group <" + groupName + ">");
		}
		return true;
	}

	public void stopBroadcastMedia() {
		fileReaderThread.stopFile();

	}

	public void startBroadcastMedia() {
		fileReaderThread = new FileReaderThread(debug, selfAddress, channel, fileInputStream, fileName,
				filePacketDelay, filePacketSize, textManager);
	}

	public void Receive(Message msg) {

		// If you join the group in the middle of a _broadCast_
		// you won't recieve

		if ((!receivingData) && (!(msg instanceof StartFileMsg)) && (!(msg instanceof FileNameMsg))) {
			return;
		}
		if (ignoreCurrentSession && msg instanceof FileMsg) {
			return;
		}
		try {
			if (msg instanceof FileNameMsg) {
				name = new String(msg.GetBuffer(), "UTF-8");
				new File("AcceptedFiles").mkdir();
				fileOutputStream = new FileOutputStream("AcceptedFiles/" + name);

			}
			if (msg instanceof FileMsg) {
				fileOutputStream.write(msg.GetBuffer());
			} else if (msg instanceof StartFileMsg) {
				fileOutputStream.write(msg.GetBuffer());
				receivingData = true;
			} else if (msg instanceof EndFileMsg) {
				fileOutputStream.write(msg.GetBuffer());
				fileOutputStream.close();
				int len = lista.getModel().getSize();
				boolean exists = false;
				for (int i = 0; i < len; i++) {
					if (lista.getModel().getElementAt(i).equals(name)) {
						exists = true;
						break;
					}
				}
				if (!exists)
					guiManager.getFrameOfAcceptedFiles().addToList(name);
				receivingData = false;
				if (guiManager.getMembershipManager().isCoordinator) {
					guiManager.getMembershipManager().clearFileBcastChannel();
					guiManager.getMembershipManager().bcastEndFileBcastMsg();
				}

			}
			ignoreCurrentSession = false;

		} catch (Exception e)

		{
			e.printStackTrace();
		}
	}

}

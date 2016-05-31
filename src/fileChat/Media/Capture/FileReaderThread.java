package fileChat.Media.Capture;

import java.io.*;

import javax.swing.JList;

import JavaGroups.*;
import fileChat.DataManager.TextManager;
import fileChat.DataManager.DataMessage.*;
import fileChat.GUI.GUIManager;

public class FileReaderThread implements Runnable {

	int FilePacketDelay;
	int FilePacketSize;

	FileInputStream fileInputStream;
	String fileName = "";
	Channel channel;
	Object selfAddress;
	Thread t;
	boolean debug;
	boolean stop;
	boolean fileNameSent = false;
	TextManager textManager;
	public FileReaderThread(boolean debug, Object selfAddress, Channel channel, FileInputStream fileInputStream,
			String fileName, int FilePacketDelay, int FilePacketSize,TextManager textManager) {
		this.debug = debug;
		this.fileName = fileName;
		this.selfAddress = selfAddress;
		this.channel = channel;
		this.fileInputStream = fileInputStream;
		this.FilePacketDelay = FilePacketDelay;
		this.FilePacketSize = FilePacketSize;
		this.textManager=textManager;
		t = new Thread(this, "FileReaderThread");
		t.start();
	}

	public void stopFile() {
		stop = true;
	}

	public void run() {
		byte[] buff = new byte[FilePacketSize];
		int len = 0;
		int num = 0;

		try {
			if (!fileNameSent) {
				textManager.bcastTextMsg("I'm sending "+fileName+". Please wait...");
				FileNameMsg fileNameMsg = new FileNameMsg(null, selfAddress, fileName.getBytes("UTF-8"));
				channel.Send(fileNameMsg);
				fileNameSent = true;
			}
			len = fileInputStream.read(buff);
			if (debug) {
				System.out.println("reading packet " + num + " from file with lenght " + len);
			}
			StartFileMsg startFileMsg = new StartFileMsg(null, selfAddress, buff, len, num++);
			channel.Send(startFileMsg);
			if (debug) {
				System.out.println("reading packet " + num + " from file with lenght " + len);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		while (!stop) {
			try {
				Thread.sleep(FilePacketDelay);
				len = fileInputStream.read(buff);
				if (len < FilePacketSize) {
					EndFileMsg endFileMsg = new EndFileMsg(null, selfAddress, buff, len, num++);
					fileInputStream.close();
					channel.Send(endFileMsg);
					textManager.bcastTextMsg("I finished sending "+fileName+".");
					return;
				}
				FileMsg fileMsg = new FileMsg(null, selfAddress, buff, len, num++);
				channel.Send(fileMsg);
				if (debug) {
					System.out.println("reading packet " + num + " from file with lenght " + len);
				}

			} catch (Exception e1) {
				if (debug) {
					System.out.println("End of file. Stop reading file");
				}
				break;
			}
		}
		if (stop) {
			try {
				if (debug) {
					System.out.println("File broadcast stopped by initiator");
				}
				EndFileMsg endFileMsg = new EndFileMsg(null, selfAddress, buff, len, num++);
				fileInputStream.close();
				channel.Send(endFileMsg);
				textManager.bcastTextMsg("I stopped broadcasting "+fileName+".");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}

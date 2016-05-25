package fileChat.ControlManager;

/******************************************************************************
 * JavaGroupsVC version 0.5 beta 
 * 
 * UserInfo is the data structure that stores information about individual member
 * Used by UserInfoManager and MembershipManager
 * 
 ******************************************************************************/
import JavaGroups.*;

public class UserInfo implements java.io.Serializable {
	private String userName;
	private Object address = null;
	private Object chatAddress = null;
	private Object fileAddress = null;
	private Object defaultAddress = null;
	private int uniqueNum = -1;

	public UserInfo(String userName) {
		this.userName = userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setAddress(Object address) {
		this.address = address;
	}

	public void setChatAddress(Object chatAddress) {
		this.chatAddress = chatAddress;
	}

	public void setDefaultAddress(Object defaultAddress) {
		this.defaultAddress = defaultAddress;
	}

	public Object getDefaultAddress() {
		return defaultAddress;
	}

	public Object getAddress() {
		return address;
	}

	public Object getChatAddress() {
		return chatAddress;
	}

	public Object getFileAddress() {
		return fileAddress;
	}

	public void setFileAddress(Object fileAddress) {
		this.fileAddress = fileAddress;
	}

	public int getUniqueNum() {

		return uniqueNum;
	}

	public void setUniqueNum(int uniqueNum) {

		this.uniqueNum = uniqueNum;
	}

	public String toString() {
		return userName + "(" + Integer.toString(uniqueNum) + ")" + " Chat:" + chatAddress;
	}
}

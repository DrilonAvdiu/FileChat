package fileChat.ControlManager;

import JavaGroups.*;
import java.util.*;

/******************************************************************************
 * JavaGroupsVC version 0.5 beta
 * 
 * UserInfoManager is the data structure manager for UserInfo This class deals
 * with all the issues regarding associating readable username with the endpoint
 * address. UsernameManager must be instantiated for every client
 ******************************************************************************/
public class UserInfoManager implements java.io.Serializable {
	private Hashtable addrToNameTable;
	private Hashtable addrToUserInfoTable;
	private boolean debug;
	private UserInfo myUserInfo;
	private Vector uniqNumberAvailableVector;
	final int maxUser = 30;

	public UserInfoManager(boolean debug, UserInfo userInfo) {
		addrToNameTable = new Hashtable();
		addrToUserInfoTable = new Hashtable();
		uniqNumberAvailableVector = new Vector();
		setupUniqNumVector();
		myUserInfo = userInfo;
		this.debug = debug;
	}

	public Vector getAvailableNumVector() {

		return uniqNumberAvailableVector;

	}

	private void setupUniqNumVector() {
		for (int i = 0; i < maxUser; i++) {
			uniqNumberAvailableVector.addElement(new Integer(i));
		}
	}

	private void removeUniqNum(int usedUniqNum) {
		if (uniqNumberAvailableVector.removeElement(new Integer(usedUniqNum))) {
			if (debug) {
				System.out.println("Removed UniqNum =" + usedUniqNum);
			}
		} else {
			if (debug) {
				System.out.println("UniqNum =" + usedUniqNum + " is not found!");
			}
		}
	}

	private void replaceUniqNum(int usedUniqNum) {
		Integer tempUniqNum = new Integer(usedUniqNum);
		if (uniqNumberAvailableVector.contains(tempUniqNum)) {
			if (debug) {
				System.out.println("Trying to add second UniqNum =" + usedUniqNum);
			}
		} else {
			uniqNumberAvailableVector.add(tempUniqNum);
			if (debug) {
				System.out.println("UniqNum =" + usedUniqNum + " is restored as available!");
			}
		}
	}

	public String getUserName(Object address) {

		if (isInTable(address))
			return (String) addrToNameTable.get(address);
		else
			return null;

	}

	public void setMyUserInfo(UserInfo userInfo) {
		myUserInfo = userInfo;
		if (debug) {
			System.out.println("myUserInfo is set as " + myUserInfo);
		}
	}

	public UserInfo getMyUserInfo() {
		if (debug) {
			System.out.println("myUserInfo is " + myUserInfo);
		}
		return myUserInfo;
	}

	public UserInfo getUserInfo(Object address) {

		if (isInTable(address))
			return (UserInfo) this.addrToUserInfoTable.get(address);
		else
			return null;

	}

	public Hashtable getEntireTable() {
		if (debug) {
			System.out.println("return addrToUserInfoTable");
		}
		return addrToUserInfoTable;
	}

	public void setEntireTable(Hashtable newTable) {
		if (debug) {
			System.out.println("Reset entire table");
		}
		addrToNameTable = new Hashtable();
		addrToUserInfoTable = newTable;
		for (Enumeration e = addrToUserInfoTable.keys(); e.hasMoreElements();) {
			Object addr = e.nextElement();
			UserInfo tempUserInfo = (UserInfo) addrToUserInfoTable.get(addr);
			String tempUserName = tempUserInfo.getUserName();
			int tempUniqNum = tempUserInfo.getUniqueNum();
			addrToNameTable.put(addr, tempUserName);
			removeUniqNum(tempUniqNum);
		}

		if (debug) {
			System.out.println("Now the table has:" + addrToUserInfoTable);
		}
	}

	void addUser(UserInfo userInfo, Object addr) {

		if (isInTable(addr) || isInTable(userInfo)) {
			return;
		}
		if (debug) {
			System.out.println("Added user: " + userInfo + " with address: " + addr);
		}
		addrToUserInfoTable.put(addr, userInfo);
		addrToNameTable.put(addr, userInfo.getUserName());
		removeUniqNum(userInfo.getUniqueNum());
		if (debug) {
			System.out.println("Now the table has:" + addrToUserInfoTable);
		}
	}

	void removeUsers(Vector addrVector) {
		int size = addrVector.size();
		for (int i = 0; i < size; i++) {
			removeUser(addrVector.elementAt(i));
		}
		if (debug) {
			System.out.println("Now the table has:" + addrToUserInfoTable);
		}
	}

	void removeUser(Object addr) {
		if (isInTable(addr)) {
			addrToNameTable.remove(addr);
			UserInfo tempUserInfo = (UserInfo) addrToUserInfoTable.get(addr);
			int tempUniqNum = tempUserInfo.getUniqueNum();
			replaceUniqNum(tempUniqNum);
			addrToUserInfoTable.remove(addr);
			if (debug) {
				System.out.println("Removed user: " + addrToNameTable.get(addr) + " with address: " + addr
						+ " and uniqNum " + tempUniqNum);
			}
		}
	}

	public int assignUniqNum() {
		if (uniqNumberAvailableVector.size() == 0) {
			if (debug) {
				System.out.println("Maximum user count reached, unable to assign UniqNum");
			}
			return -1;
		} else {
			Integer tempUniqNum = (Integer) uniqNumberAvailableVector.remove(0);
			return tempUniqNum.intValue();
		}
	}

	boolean isInTable(Object addrOrUserInfo) {
		if (addrOrUserInfo instanceof UserInfo) {
			String tempUserName = ((UserInfo) addrOrUserInfo).getUserName();
			if (addrToNameTable.containsValue(tempUserName)) {
				if (debug) {
					System.out.println(addrOrUserInfo + " is in the table");
				}
				return true;
			} else {
				if (debug) {
					System.out.println(addrOrUserInfo + " is NOT in the table");
				}
				return false;
			}
		} else {
			if (addrToNameTable.containsKey(addrOrUserInfo)) {
				if (debug) {
					System.out.println(addrOrUserInfo + " is in the table");
				}
				return true;
			} else {
				if (debug) {
					System.out.println(addrOrUserInfo + " is NOT in the table");
				}
				return false;
			}
		}
	}
}

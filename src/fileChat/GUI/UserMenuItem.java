package fileChat.GUI;
/******************************************************************************
 * JavaGroupsVC version 0.5 beta 
 * 
 * UserMenuItem is the menuItem that stores neccessary user information so that
 * when the item is selected in the GUIManager menu,a video conference session 
 * will be launched with the particular user.
 * Note: Video Conference feature not implemented in version 0.5
 * 
 ******************************************************************************/

import javax.swing.*;

public class UserMenuItem extends JMenuItem
{
	String name;
	Object address;
	public int num;
	public UserMenuItem(String name,Object address){
		super(name);
		this.name=name;
		this.address=address;
	}
	
	public Object getAddress(){	
	return address;
	}
	
	public String getUserName(){
		return name;
	}
}

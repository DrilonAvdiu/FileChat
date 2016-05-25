package fileChat.GUI;
import javax.swing.*;
/******************************************************************************
 * JavaGroupsVC version 0.5 beta 
 * 
 * UserButton is the button that stores neccessary user information so that
 * when the button is clicked in the MembershipPanel, a private message can
 * be sent to that particular user 
 * 
 ******************************************************************************/
public class UserButton extends JButton
{
	String name;
	Object address;
	public int num;
	public UserButton(String name,Object address, int num,Icon icon){
		super(name, icon);
		this.name=name;
		this.num=num;
		this.address=address;
	}
	
	public Object getAddress(){	
	return address;
	}
	
	public String getUserName(){
		return name;
	}
}

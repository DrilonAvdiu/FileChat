package fileChat.GUI;
/******************************************************************************
 * JavaGroupsVC version 0.5 beta 
 * 
 * GroupsPanel is the GUI component that displays activities in the default channel
 * It communicates through GUIManager with GroupsManager for user events.
 * 
 ******************************************************************************/
import JavaGroups.*;
import fileChat.ControlManager.*;
import fileChat.DataManager.*;

import java.util.*;import java.awt.*;
import java.awt.event.*;
import javax.swing.*;	
import javax.swing.border.*;
import javax.swing.tree.*;

public class GroupsPanel extends JPanel implements ActionListener
{
	private GroupsManager groupsManager;
	JPanel innerPanel;
	JScrollPane scrollPane;
	DefaultMutableTreeNode top;
	JTree groupsTree;
  DefaultTreeCellRenderer treeCellRenderer; 
	boolean debug;
	
	public GroupsPanel(boolean debug,GroupsManager groupsManager){
	  this.debug=debug;
		this.groupsManager=groupsManager;		innerPanel=new JPanel();		innerPanel.setLayout(new GridLayout(10,1));
		top=new DefaultMutableTreeNode("All Groups");		treeCellRenderer=new DefaultTreeCellRenderer();		treeCellRenderer.setBackgroundNonSelectionColor(innerPanel.getBackground());
		ImageIcon userIcon=new ImageIcon("JavaGroups\\VideoCOnference\\GUI\\icons\\user.gif");		treeCellRenderer.setLeafIcon(userIcon);
		groupsTree=new JTree(top);
		groupsTree.setCellRenderer(treeCellRenderer);
		groupsTree.putClientProperty("JTree.lineStyle","Angled");
		
		Dimension minDim=new Dimension(1,1);
		setMinimumSize(minDim);
		setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		scrollPane=new JScrollPane(innerPanel,
								   ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
								   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		innerPanel.setAutoscrolls(true);		JLabel groupsPanelLabel=new JLabel("All the groups available");
		add(groupsPanelLabel);				///***********
		GridBagLayout gridBag = new GridBagLayout();
		setLayout(gridBag);
		GridBagConstraints c = new GridBagConstraints();
		c.weightx=1.0;
		c.weighty=0;
		c.gridwidth = GridBagConstraints.REMAINDER;  		groupsPanelLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		c.fill = GridBagConstraints.HORIZONTAL;  
		gridBag.setConstraints(groupsPanelLabel, c);
		add(groupsPanelLabel);
		c.gridwidth = GridBagConstraints.REMAINDER;  
		c.weightx=1.0;
		c.weighty=1.0;
		c.fill = GridBagConstraints.BOTH;    
		gridBag.setConstraints(scrollPane, c);		////***********
		add(scrollPane);
		innerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		Dimension dim = new Dimension (200,300);
		scrollPane.setPreferredSize(dim);
		validate();
	}
	
	public void updateGroupsPanel(GroupListManager groupListManager){
		System.out.println("updateGroupsPanel is called");
		try{
			innerPanel.removeAll();
			top=new DefaultMutableTreeNode("All Groups");	
			groupsTree=new JTree(top);
			groupsTree.setCellRenderer(treeCellRenderer);
			groupsTree.putClientProperty("JTree.lineStyle","Angled");

			groupsTree.setBackground(innerPanel.getBackground());
			Hashtable groupListTable=groupListManager.getGroupListTable();
			int size=groupListTable.size();
			for(Enumeration e=groupListTable.keys();e.hasMoreElements();){
				String groupName=(String)e.nextElement();
				DefaultMutableTreeNode groupNode =new DefaultMutableTreeNode(groupName);	
				top.add(groupNode);
				GroupList groupList=(GroupList)groupListTable.get(groupName);
				Hashtable userInfoTable=groupList.userInfoManager.getEntireTable();
				for(Enumeration ee=userInfoTable.keys();ee.hasMoreElements();){
					Object address=ee.nextElement();
					UserInfo userInfo=(UserInfo)userInfoTable.get(address);
					String username=userInfo.getUserName();
					DefaultMutableTreeNode memberNode=new DefaultMutableTreeNode(username);
					groupNode.add(memberNode);
				}
			}
			
			innerPanel.add(groupsTree);
			validate();	
		}catch (Exception e){e.printStackTrace();}
	}
		public void actionPerformed(ActionEvent evt) {		/*UUserButton userButton=(UserButton)evt.getSource();		textManager.sendTextMsg(userButton.getAddress());	
	*/	}
	public JScrollPane getScrollPane() {
		return scrollPane;
	}

}
	
	



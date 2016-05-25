package fileChat.GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import fileChat.DataManager.TextManager;
public class ChatPanel extends JPanel implements ActionListener
{
	
	String username;
	JTextField textField;
	JTextArea textArea;
	JPanel textFieldPanel=new JPanel();
	JPanel textAreaPanel=new JPanel();
	String newline;
	
	int member_size=1;
	
	
	public ChatPanel(TextManager textManager) {
		this.textManager=textManager;
		textField = new JTextField(40);
		textArea = new JTextArea(10, 40);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textField.addActionListener(this);
		newline = System.getProperty("line.separator");
		
		//put the text field and text Area in separate JPanel
		//So that there will be gaps between each component
		
		
		textFieldPanel.add(textField);
		JScrollPane scrollPane = new JScrollPane(textArea);    
		scrollPane.setAutoscrolls(true);
		textAreaPanel.add(scrollPane);
		textAreaPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		GridBagLayout gridBag = new GridBagLayout();
		setLayout(gridBag);
		GridBagConstraints c = new GridBagConstraints();
		c.weightx=1.0;
		c.weighty=0;
		c.gridwidth = GridBagConstraints.REMAINDER;   
		textFieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		c.fill = GridBagConstraints.HORIZONTAL;  
		gridBag.setConstraints(textFieldPanel, c);
		textFieldPanel.setLayout(gridBag);
		gridBag.setConstraints(textField, c);
		add(textFieldPanel); 
		c.gridwidth = GridBagConstraints.REMAINDER;  
		c.weightx=1.0;
		c.weighty=1.0;
		c.fill = GridBagConstraints.BOTH;    
		textAreaPanel.setLayout(gridBag);
		gridBag.setConstraints(scrollPane, c);
		gridBag.setConstraints(textAreaPanel, c);
		add(textAreaPanel);
	}
	
	public String getMsg(){
		return msgStr;
	}
	public void setTextManager(TextManager textManager){
		this.textManager=textManager;
	}
	public void clearAllMessage(){
		textField.setText("");
	}
	public void clearTextMsg(){
		textField.setText("");
	}
		textArea.append(msg + newline);
	}
		if(textManager==null){return;}
			textManager.bcastTextMsg(textField.getText());
		}
	}
}


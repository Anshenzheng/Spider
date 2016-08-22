package com.an.model;

import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.an.startup.Spider;
import com.an.util.CommonUtil;

public class MainFrame extends JFrame implements ActionListener {
        
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFrame frame = new JFrame("保健食品库Spider"); 
    JTabbedPane tabPane = new JTabbedPane(); 
    Container con = new Container();
    
    JLabel outputPathLabel = new JLabel("File Path:");  
    JLabel fromDateLabel = new JLabel("From Date:");  
    JLabel toDateLabel = new JLabel("To Date:");  
    JTextField outputPathText = new JTextField(); 
    JTextField fromDateText = new JTextField(); 
    JTextField toDateText = new JTextField(); 
    
    public static  JTextArea queryInfo = new JTextArea();  
    JScrollPane scroll = new JScrollPane(queryInfo); 
    
    JLabel authorLabel = new JLabel("Copyright © 2016-2018, Annan.An, All Rights Reserved");  
    
    JButton selectFileBtn = new JButton("Select File"); 
    JFileChooser jfc = new JFileChooser(); 
    JButton OKBtn = new JButton("Start Spider"); 
    
      
    MainFrame() {  
    	//Set init Directory as C://
        jfc.setCurrentDirectory(new File("c://"));
          
        //Display the panel in center
        double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();  
        double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();     
        frame.setLocation(new Point((int) (lx / 2) - 150, (int) (ly / 2) - 150));
        
        frame.setSize(550, 450);
        frame.setResizable(false);
        frame.setContentPane(tabPane);
        outputPathLabel.setBounds(10, 10, 70, 30);  
        outputPathText.setBounds(75, 10, 300, 30);  
        outputPathText.setEditable(false);
        selectFileBtn.setBounds(380, 10, 80, 30);  
        fromDateLabel.setBounds(10, 45, 70, 30);  
        toDateLabel.setBounds(280, 45, 70, 30);  
        fromDateText.setBounds(75, 45, 120, 30);  
        fromDateText.setToolTipText("Please input YYYY-MM-DD format Date");
        toDateText.setBounds(340,45,120,30);
        toDateText.setToolTipText("Please input YYYY-MM-DD format Date");
        OKBtn.setBounds(75, 85, 120, 30);  
        scroll.setBounds(10, 130, 510, 200);  
        authorLabel.setBounds(10, 350, 500, 30);
        authorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        queryInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        queryInfo.setEditable(false);
        queryInfo.setAutoscrolls(true);
        
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        
        selectFileBtn.addActionListener(this); 
        OKBtn.addActionListener(this); 
        con.add(outputPathLabel);  
        con.add(outputPathText);  
        con.add(selectFileBtn);  
        con.add(fromDateLabel);  
        con.add(toDateLabel); 
        con.add(fromDateText);  
        con.add(toDateText);  
        con.add(OKBtn);  
        con.add(scroll);  
        con.add(authorLabel);  
        frame.setVisible(true); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        tabPane.add("Spider by date", con); 
    }  

    public void actionPerformed(ActionEvent e) {  
        if (e.getSource().equals(selectFileBtn)) { 
            jfc.setFileSelectionMode(1); 
            int state = jfc.showOpenDialog(null); 
            if (state == 1) {  
                return;  
            } else {  
                File f = jfc.getSelectedFile(); 
                outputPathText.setText(f.getAbsolutePath());  
            }  
        }  
 
        if (e.getSource().equals(OKBtn)) {  
           
        	String outputPath = outputPathText.getText();
        	String fromDate = fromDateText.getText();
        	String toDate = toDateText.getText();
        	if(CommonUtil.isNotNullOrEmpty(outputPath) && CommonUtil.isNotNullOrEmpty(fromDate) && CommonUtil.isNotNullOrEmpty(toDate)){
        		try {
        			queryInfo.setText("Start loading...\r\n");
        			String fileName = outputPath + "\\"+fromDate+"_"+toDate+"_"+new Date().getTime()+".xls";
        			MainFrame.appendInfo("Start loading data to file " + fileName + " ...");
        			Spider.fetchDataByDate(fromDate, toDate, fileName);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "error", 2);  
				}
        	}else{
                JOptionPane.showMessageDialog(null, "Please input specified fields", "warning", 2);  
        	}
        }  
    }  
    
    public static void appendInfo(String info){
    	if(queryInfo.getText().length() > 100000){
    		queryInfo.setText("");
    	}
    	queryInfo.setText(queryInfo.getText() + "\r\n" + info);
    	queryInfo.paintImmediately(queryInfo.getBounds());
    }
    
    public static void main(String[] args) {  
        new MainFrame();  
    }  
    
}

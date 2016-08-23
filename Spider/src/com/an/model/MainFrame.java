package com.an.model;

import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import com.an.util.DateUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

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
   
    UtilDateModel model = new UtilDateModel();
    JDatePanelImpl datePanel = new JDatePanelImpl(model);
    JDatePickerImpl fromDateText = new JDatePickerImpl(datePanel);
    
    UtilDateModel model2 = new UtilDateModel();
    JDatePanelImpl datePanel2 = new JDatePanelImpl(model2);
    JDatePickerImpl toDateText = new JDatePickerImpl(datePanel2);

    		
    public static  JTextArea queryInfo = new JTextArea();  
    JScrollPane scroll = new JScrollPane(queryInfo); 
    
    JLabel authorLabel = new JLabel("Copyright © 2016-2018, Annan.An, All Rights Reserved");  
    
    JButton selectFileBtn = new JButton("Select File"); 
    JFileChooser jfc = new JFileChooser(); 
    JButton OKBtn = new JButton("Start Spider"); 
    JButton closeBtn = new JButton("Quit"); 
    
    Thread thread;
    MainFrame() {  
    	fromDateText.setLocale(Locale.US);
    	toDateText.setLocale(Locale.US);
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
        fromDateText.setBounds(75, 45, 120, 26);  
        toDateText.setBounds(340,45,120,26);
        OKBtn.setBounds(75, 85, 120, 30);  
        closeBtn.setBounds(200,85,120,30);
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
        closeBtn.addActionListener(this); 
        con.add(outputPathLabel);  
        con.add(outputPathText);  
        con.add(selectFileBtn);  
        con.add(fromDateLabel);  
        con.add(toDateLabel); 
        con.add(fromDateText);  
        con.add(toDateText);  
        con.add(OKBtn);  
        con.add(closeBtn);  
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
        	
        	Date dateFrom = (Date)fromDateText.getModel().getValue();
        	Date dateto =   (Date)toDateText.getModel().getValue();
        	
        	String fromDate = DateUtil.dateToString(dateFrom);
        	String toDate = DateUtil.dateToString(dateto);
        	if(CommonUtil.isNotNullOrEmpty(outputPath) && CommonUtil.isNotNullOrEmpty(fromDate) && CommonUtil.isNotNullOrEmpty(toDate)){
        		try {
        			queryInfo.setText("");
        	    	queryInfo.paintImmediately(queryInfo.getBounds());
        			MainFrame.appendInfo("Start crawling data from website...\r\n");
        			MainFrame.appendInfo("Start generating date list....");
        			// Step 1 - Generate date list
        			List<String> dates = DateUtil.generateDataList(fromDate, toDate);
        			String fileName = outputPath + "\\"+fromDate+"_"+toDate+"_"+new Date().getTime()+".xls";
        			MainFrame.appendInfo("Data will be export to file " + fileName);
        			thread = new Thread(()->{
        				try {
							Spider.fetchDataByDate(dates, fileName);
						} catch (Exception exception) {
							exception.printStackTrace();
						}
						OKBtn.setEnabled(true);

        			});
        			
        			thread.start();
        			OKBtn.setEnabled(false);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "error", 2);  
				}
        	}else{
                JOptionPane.showMessageDialog(null, "Please input specified fields", "warning", 2);  
        	}
        }  
        if(e.getSource().equals(closeBtn)){
        	int close = JOptionPane.showConfirmDialog(null, "Are you sure to quit?", "Confirm to Quit", 0);
        	if(close == 0){
            	System.exit(ABORT);
        	}
        }
    }  
    
    public static void appendInfo(String info){
    	if(queryInfo.getText().length() > 10000){
    		queryInfo.setText("");
    	}
    	queryInfo.setText(queryInfo.getText() + "\r\n" + info);
    	queryInfo.paintImmediately(queryInfo.getBounds());
    }
    
    public static void main(String[] args) {  
        new MainFrame();  
    }  
    
}

package mkdd_ghost;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class GUI extends JFrame implements ActionListener {
	private JButton btnLoad;
	private JLabel filepath;
	private JButton btnConvert;
	
	private static String filepathName;
	
	public GUI() {
		JFrame frame = new JFrame ("MKDD Ghost Converter");
	    frame.setSize(700, 135);    
	    frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    
	    //first row
	    btnLoad = new JButton("Load File");    
	    btnLoad.setBounds(10,10,95,30);  
	    frame.add(btnLoad);
	    btnLoad.addActionListener(this);      
	    
	    filepath = new JLabel("Nothing Loaded");
	    filepath.setBounds(120,10,750,30);
	    frame.add(filepath);
	    
	    //frame.add(courseSelect);
	    
	    btnConvert = new JButton("Make .dtm");
	    btnConvert.setEnabled(false);
	    btnConvert.setBounds(580, 10, 95, 30);
	    frame.add(btnConvert);
	    btnConvert.addActionListener(this);
	    
	    frame.setLayout(null);  
	    frame.setVisible(true); 
	    
	    
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnLoad) {
			buttonLoadClicked();
			btnConvert.setEnabled(true);
		} else if(e.getSource() == btnConvert) {
			ArrayList<Byte> inputs = FileWriter.readInputs(filepathName);
			try {
				FileWriter.makefile(inputs);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public void buttonLoadClicked() {
		JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	    int result = fileChooser.showOpenDialog(new JFrame());
	    
	    if (result == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        
	        filepath.setText("Selected file: " + selectedFile.getAbsolutePath());
	        filepathName = selectedFile.getAbsolutePath();
	    }
	    
		
	}
	
	
}

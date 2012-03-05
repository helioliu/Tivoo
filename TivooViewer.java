import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.event.*;
import javax.swing.*;

import org.joda.time.DateTime;

@SuppressWarnings("serial")
public class TivooViewer extends JPanel{
	
	// constants
    public static final Dimension SIZE = new Dimension(800, 600);
    public static final String BLANK = " ";

    // web page
    private JEditorPane myPage;
    // information area
    private JLabel myStatus;
    // the real worker
    protected TivooModel myModel;
    
    private JButton myLoadButton;
    private JButton myClearButton;
    private JButton myGenerateButton;
    private JTextField myURLDisplay;
    
    private JCheckBox myKeywordFilterBox;
    private JCheckBox myLocationFilterBox;
    private JCheckBox myTimeFilterBox;
    private JCheckBox myTVActorFilterBox;

    /**
     * Create a view of the given model of a web browser.
     */
    public TivooViewer (TivooModel model){
        myModel = model;
        // add components to frame
        setLayout(new BorderLayout());
        // must be first since other panels may refer to page
        add(makePageDisplay(), BorderLayout.CENTER);
        add(makeInputPanel(), BorderLayout.NORTH);
        add(makeInformationPanel(), BorderLayout.SOUTH);
    }
    
    private void addEvents(){
    	JFileChooser fc = new JFileChooser();
        
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            
            myModel.parseFile(file);
        } 
    }
    
    private void clearEvents(){
    	myModel.startOver();
    }
    
    private void generateOutput(){
    	applyFilters();
    	myModel.generate();
    	update(myModel.getURL());
    }
    
    private void applyFilters(){
    	if(myKeywordFilterBox.isSelected())
    		myModel.applyKeywordFilter();
    	if(myLocationFilterBox.isSelected())
    		myModel.applyLocationFilter();
    	if(myTimeFilterBox.isSelected())
    		myModel.applyTimeFilter();
    	if(myTVActorFilterBox.isSelected())
    		myModel.applyTVActorFilter();
    }
    
    private void setKeyword(){
    	String key = JOptionPane.showInputDialog(this,
		        "Enter keyword",
		  		"Set Keyword",
		        JOptionPane.QUESTION_MESSAGE);
    	if (key != null){
    		myModel.setKeyword(key);
    	}
    }
    
    private void setLocation(){
    	String loc = JOptionPane.showInputDialog(this,
		        "Enter Location",
		  		"Set Location",
		        JOptionPane.QUESTION_MESSAGE);
    	if (loc != null){
    		myModel.setLocation(loc);
    	}
    }
    
    private void setStartEndTime(){
    	String start = JOptionPane.showInputDialog(this,
		        "Enter Start (MM dd YYYY)",
		  		"Set Start",
		        JOptionPane.QUESTION_MESSAGE);
    	
    	String end = JOptionPane.showInputDialog(this,
		        "Enter End (MM dd YYYY)",
		  		"Set End",
		        JOptionPane.QUESTION_MESSAGE);
    	if (start != null && end != null){
    		myModel.setStartEndTime(start, end);
    	}
    }
    
    private void setActor(){
    	String actor = JOptionPane.showInputDialog(this,
		        "Enter Actor",
		  		"Set Actor",
		        JOptionPane.QUESTION_MESSAGE);
    	if (actor != null){
    		myModel.setActor(actor);
    	}
    }

    /**
     * Display given message as an error in the GUI.
     */
    public void showError (String message){
        JOptionPane.showMessageDialog(this,
        		                      message, 
        		                      "Browser Error",
        		                      JOptionPane.ERROR_MESSAGE);
    }


    /**
     * Display given message as information in the GUI.
     */
    public void showStatus (String message){
        myStatus.setText(message);
    }

    private void update (String url){
        try{
        	URL dummy = new File("").toURL();
        	myPage.setPage(dummy);
        	URL url2 = new File(url).toURL();
            myPage.setPage(url2);
            myURLDisplay.setText(url);
        }
        catch (IOException e){
            showError("Could not load " + url);
        }
    }

    // convenience method to create HTML page display
	private JComponent makePageDisplay (){
        myPage = new JEditorPane();
        myPage.setPreferredSize(SIZE);

        myPage.setEditable(false);
        myPage.addHyperlinkListener(new LinkFollower());
		return new JScrollPane(myPage);
	}

    // organize user's options for controlling/giving input to model
    private JComponent makeInputPanel (){
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(makeNavigationPanel(), BorderLayout.NORTH);
        panel.add(makePreferencesPanel(), BorderLayout.SOUTH);
        return panel;
    }

    // make the panel where "would-be" clicked URL is displayed
    private JComponent makeInformationPanel (){
        // BLANK must be non-empty or status label will not be displayed in GUI
        myStatus = new JLabel(BLANK);
        return myStatus;
    }

    // make user-entered URL/text field and back/next buttons
    private JComponent makeNavigationPanel (){
        JPanel panel = new JPanel();
        myLoadButton = new JButton("Load File");
        myLoadButton.addActionListener(new loadFileAction());
        panel.add(myLoadButton);

        myClearButton = new JButton("Clear");
        myClearButton.addActionListener(new clearEventsAction());
        panel.add(myClearButton);

        myGenerateButton = new JButton("Generate");
        myGenerateButton.addActionListener(new generateOutputAction());
        panel.add(myGenerateButton);
        
        myURLDisplay = new JTextField(35);
        myURLDisplay.addActionListener(new ShowPageAction());
        panel.add(myURLDisplay);

        return panel;
    }

    // make buttons for setting favorites/home URLs
    private JComponent makePreferencesPanel (){
        JPanel panel = new JPanel();

        myKeywordFilterBox = new JCheckBox("Filter by Keyword");
        myKeywordFilterBox.addActionListener(new keywordFilterAction());
        panel.add(myKeywordFilterBox);

        myLocationFilterBox = new JCheckBox("Filter by Location");
        myLocationFilterBox.addActionListener(new locationFilterAction());
        panel.add(myLocationFilterBox);
        
        myTimeFilterBox = new JCheckBox("Filter by Time Range");
        myTimeFilterBox.addActionListener(new timeFilterAction());
        panel.add(myTimeFilterBox);
        
        myTVActorFilterBox = new JCheckBox("Filter by Actor");
        myTVActorFilterBox.addActionListener(new tvActorFilterAction());
        panel.add(myTVActorFilterBox);

        return panel;
    }

	/**
     * Inner class to deal with link-clicks and mouse-overs
     */
    private class LinkFollower implements HyperlinkListener{
        public void hyperlinkUpdate (HyperlinkEvent evt)
        {
            // user clicked a link, load it and show it
            if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
            	update(evt.getURL().toString());
            }
            // user moused-into a link, show what would load
            else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED)
            {
            	showStatus(evt.getURL().toString());
            }
            // user moused-out of a link, erase what was shown
            else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED)
            {
                showStatus(BLANK);
            }
        }
    }
    
    private class loadFileAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			addEvents();
		}
    }
    
    private class clearEventsAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			clearEvents();
		}
    }
    
    private class generateOutputAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			generateOutput();
		}
    }
    
    private class ShowPageAction implements ActionListener{
		public void actionPerformed (ActionEvent e){
		    update(myURLDisplay.getText());
		}
	}
    
    private class keywordFilterAction implements ActionListener{
    	public void actionPerformed(ActionEvent arg0){
    		if(myKeywordFilterBox.isSelected())
    			setKeyword();
    	}
    }
    
    private class locationFilterAction implements ActionListener{
    	public void actionPerformed(ActionEvent arg0){
    		if(myLocationFilterBox.isSelected())
    			setLocation();
    	}
    }
    
    private class timeFilterAction implements ActionListener{
    	public void actionPerformed(ActionEvent arg0){
    		if(myTimeFilterBox.isSelected())
    			setStartEndTime();
    	}
    }
    
    private class tvActorFilterAction implements ActionListener{
    	public void actionPerformed(ActionEvent arg0){
    		if(myTVActorFilterBox.isSelected())
    				setActor();
    	}
    }

}

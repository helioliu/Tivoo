import java.util.*;
import java.io.*;

import org.joda.time.DateTime;

import parsing.*;
import output.*;
import filtering.*;
import model.Event;

public class TivooModel {
	
	private List<Event> myEvents;
	private List<Event> myFilteredEvents;
	private String myURL;
	private List<AbstractXMLParser> myParsers;
	private String myKeyword;
	private String myLocation;
	private DateTime myStartTime;
    private DateTime myEndTime;
    private String myActor;



    /**
     * Creates an empty model.
     */
    public TivooModel ()
    {
    	myEvents = new ArrayList<Event>();
    	myFilteredEvents = new ArrayList<Event>();
    	myURL = "";
    	myParsers = new ArrayList<AbstractXMLParser>();
    	initializeParsers();
    	myKeyword = "";
    	myLocation = "";
    	myStartTime = new DateTime();
    	myEndTime = new DateTime();
    	myActor = "";
    }
    
    /**
     * Adds available parsers to the list to check
     */
    public void initializeParsers(){
    	myParsers.add(new DukeBasketBallXMLParser());
    	myParsers.add(new DukeXMLParser());
    	myParsers.add(new GoogleXMLParser());
    	myParsers.add(new NFLXMLParser());
    	myParsers.add(new TVXMLParser());
    }
    
    /**
     * Parses a file for events to add
     * @param file The file to parse
     */
    public void parseFile(File file){
    	List<Event> events = null;
    	for(AbstractXMLParser parser : myParsers){
			try{
				events = parser.processEvents(file.getPath());
			} catch (ParserException e){
				System.err.println(e.getMessage());
				continue;
			}
			if(events != null && events.size() != 0)
				break;
		}
    	
    	myEvents.addAll(events);
    	myFilteredEvents.addAll(events);
    }
    
    /**
     * Clears the internal list of events
     */
    public void startOver(){
    	myEvents.clear();
    	myFilteredEvents.clear();
    }
    
    /**
     * Applies the keyword filter to the internal list
     * @param keyword The keyword for which to filter
     */
    public void applyKeywordFilter(){
    	AbstractFilter filter = new KeywordFilter();
    	myFilteredEvents = filter.filter(myFilteredEvents, myKeyword);
    }
    
    /**
     * Applies the location filter to the internal list
     * @param location The location for which to filter
     */
    public void applyLocationFilter(){
    	AbstractFilter filter = new LocationFilter();
    	myFilteredEvents = filter.filter(myFilteredEvents, myLocation);
    }
    
    /**
     * Applies the time filter to the internal list
     * @param dt1 Beginning time range
     * @param dt2 Ending time range
     */
    public void applyTimeFilter(){
    	AbstractFilter filter = new TimeFilter();
    	myFilteredEvents = filter.filter(myFilteredEvents, myStartTime, myEndTime);
    }
    
    /**
     * Applies the tv actor filter to the internal list
     * @param actor Name of the actor for whom to filter
     */
    public void applyTVActorFilter(){
    	AbstractFilter filter = new TVActorFilter();
    	myFilteredEvents = filter.filter(myFilteredEvents, myActor);
    }
    
    /**
     * Creates the html page and sets the url to that page
     */
    public void generate(){
    	AbstractHtmlOutputter out = new SortedListOutputter();
    	myURL = out.writeEvents(myFilteredEvents);
    	myFilteredEvents.clear();
    	myFilteredEvents.addAll(myEvents);
    }
    
    /**
     * @return The stored URL
     */
    public String getURL(){
    	return myURL;
    }
    
    /**
     * Set the keyword for which to filter
     * @param s The new keyword
     */
    public void setKeyword(String s){
    	myKeyword = s;
    }
    
    /**
     * Set the location for which to filter
     * @param s The new location
     */
    public void setLocation(String s){
    	myLocation = s;
    }
    
    /**
     * Set the start and end times for which to filter
     * @param s1 Start of time range
     * @param s2 End of time range
     */
    public void setStartEndTime(String s1, String s2){
    	Scanner sc1 = new Scanner(s1);
    	int s1MM = sc1.nextInt();
    	int s1dd = sc1.nextInt();
    	int s1YYYY = sc1.nextInt();
    	Scanner sc2 = new Scanner(s2);
    	int s2MM = sc2.nextInt();
    	int s2dd = sc2.nextInt();
    	int s2YYYY = sc2.nextInt();
    	
    	myStartTime = new DateTime(s1YYYY, s1MM, s1dd, 0, 0);
    	myEndTime = new DateTime(s2YYYY, s2MM, s2dd, 0, 0);
    }
    
    /**
     * Set the actor for whom to filter
     * @param s Name of the actor
     */
    public void setActor(String s){
    	myActor = s;
    }
    
}

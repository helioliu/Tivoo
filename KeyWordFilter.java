
import java.util.ArrayList;

public class KeyWordFilter {
	ArrayList<Event> myEventList;
	
	public KeyWordFilter(ArrayList<Event> eventList) {
		myEventList = eventList;
	}
	
	public ArrayList<Event> filterByKeyword(String keyword) {
		ArrayList<Event> filteredList = new ArrayList<Event>();
		for (Event event : myEventList) {
			if (event.getTitle().contains(keyword)) {
				filteredList.add(event);
			}
		}
		return filteredList;
	}
}

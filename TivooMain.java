import javax.swing.JFrame;

public class TivooMain {
	// convenience constants
    public static final String TITLE = "Tivoo GUI";
    public static final String DEFAULT_START_PAGE = "";


    public static void main (String[] args)
    {
    	// create program specific components
        TivooModel model = new TivooModel();
        TivooViewer display = new TivooViewer(model);
        // create container that will work with Window manager
        JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // add our user interface components to Frame and show it
        frame.getContentPane().add(display);
        frame.pack();
        frame.setVisible(true);
        // start somewhere, less typing for debugging
        //display.showPage(DEFAULT_START_PAGE);
    }
}

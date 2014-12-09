package twitchlsgui;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author Niklas 28.06.2014
 * 
 */
public class Functions {

    public static ArrayList<TwitchStream> streamList;
    public static ArrayList<String> streamServicesList;
    public static int checkTimer = 10;

    /**
     * 
     * @param name
     * @param quality
     */
    public static void OpenStream(String name, String quality) {
	String cmd = "livestreamer " + Main_GUI.currentStreamService + "/"
		+ name + " " + quality;
	try {
	    Process prc = Runtime.getRuntime().exec(cmd);
	    Thread reader = new Thread(new PromptReader(prc.getInputStream()));
	    reader.start();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}

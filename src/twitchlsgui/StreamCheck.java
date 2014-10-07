package twitchlsgui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import twitchAPI.Twitch_API;

/**
 * 
 * @author Niklas 28.06.2014
 * 
 */
public class StreamCheck implements Runnable {

    ArrayList<Thread> threads = new ArrayList<Thread>();
    int count = 0;
    public String upTimeString;
    public long upTimeLong;
    public int upTimeHour;
    public int upTimeMinute;

    @Override
    public void run() {
	while (true) {
	    if (Functions.streamList.size() > 0) {
		Main_GUI.onlineStatus.setText("Updating");
		for (int i = 0; i < Functions.streamList.size(); i++) {
		    threads.add(new Thread(new CheckThread(i)));
		}
		for (int i = 0; i < Functions.streamList.size(); i++) {
		    threads.get(i).start();
		    count++;
		}
		for (int i = 0; i < Functions.streamList.size(); i++) {
		    try {
			threads.get(i).join();
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
		}
		for (int i = 0; i < Main_GUI.streamListModel.getSize(); i++) {
		    Main_GUI.streamListModel.setElementAt(
			    Main_GUI.streamListModel.getElementAt(i), i);
		}
	    }
	    // System.out.println(count);
	    if (Main_GUI.currentStreamName == "") {
		Main_GUI.onlineStatus.setText("Finished updating");
	    } else {
		if (Twitch_API.getStream(Main_GUI.currentStreamName).isOnline()) {
		    upTimeString = Twitch_API.getStream(
			    Main_GUI.currentStreamName).getUp_time();
		    if (upTimeString != null) {
			upTimeLong = convertDate(upTimeString);
			GregorianCalendar c = new GregorianCalendar();
			c.setTimeInMillis(System.currentTimeMillis()
				- upTimeLong);
			upTimeHour = c.get(Calendar.HOUR_OF_DAY);
			upTimeMinute = c.get(Calendar.MINUTE);
		    }
		    Main_GUI.onlineStatus.setText("<html>Playing "
			    + Twitch_API.getStream(Main_GUI.currentStreamName)
				    .getMeta_game()
			    + " (Online for "
			    + getUpTimeHours()
			    + ":"
			    + getUpTimeMinutes()
			    + " hours)"
			    + "<br>"
			    + Twitch_API.getStream(Main_GUI.currentStreamName)
				    .getTitle() + "</html>");

		} else {
		    Main_GUI.onlineStatus.setText("Stream is Offline");
		}
	    }
	    threads = new ArrayList<Thread>();

	    try {
		Thread.sleep(Functions.checkTimer * 1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    public int getUpTimeHours() {
	return upTimeHour;
    }

    public String getUpTimeMinutes() {
	if (upTimeMinute < 10) {
	    return 0 + "" + upTimeMinute;
	} else {
	    return "" + upTimeMinute;
	}
    }

    /**
     * Converts Twitch date to long
     * 
     * @param date
     * @return long value of date
     */
    private long convertDate(String date) {
	DateFormat fm = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
	String a = date;
	String[] b = a.split("T");
	a = "";
	for (String s : b) {
	    if (a != "") {
		a = a + " " + s;
	    } else {
		a = a + s;
	    }
	}
	b = a.split("Z");
	a = "";
	for (String s : b) {
	    a = a + s;
	}
	Date d = null;
	try {
	    d = fm.parse(a);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	return d.getTime();
    }

    private class CheckThread implements Runnable {
	private int index = -1;

	public CheckThread(int i) {
	    index = i;
	}

	@Override
	public void run() {
	    Functions.streamList.get(index).refresh();

	}
    }
}

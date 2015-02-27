#Livestreamer GUI
GUI for the livestreamer application (http://livestreamer.tanuki.se/)<br>
Its intended use is for twitch.tv.

#Features
<b>General features:</b><br>
	- supports stream lists for multiple stream services<br>
	- save streams in a list and start them via a double click or a button<br>
	- switch between all livestreamer available quality settings(worst, low, medium, high, best)<br>
	- Import/Export streams from all saved services to a simple .txt file<br>
	- New version check and a link to the releases section if one is available<br>
<b>Twitch.tv exclusive:</b><br>
	- get Stream Information like online status, current game and channel title<br>
	- download and display a preview image from the stream<br>
	- simple chat client for Twitch.tv chat(needs an OAuth Token instead of your password)<br>
	
#Planned Features:
	- Error output window
	
If you have an idea for a new feature or you found a bug, i'm happy to assist if possible.

#Download
You can find the current Version under the <a href src="https://github.com/westerwave/livestreamer_twitch_gui/releases">Release</a> section.
There is an .jar version and a .exe. The .exe file still needs Java!

#Requirements
Java SE, i recommend you use the most current version but Java 7 and 8 should both work.
The .exe file is packed with Launch4j (http://launch4j.sourceforge.net/) but you still need Java!
Currently only works under Windows

If you need language support for languages like russian or grecian and you want to use the .jar file
you will probably need to append the command line parameter -Dfile.encoding="UTF-8" e.g. "java -Dfile.encoding="UTF-8" -jar livestreamergui.jar"

#Preview
<br>
<img width="auto" height="auto" src="https://github.com/westerwave/livestreamer_twitch_gui/blob/master/preview.png"></img>
<br>


#Misc
Uses Gson from https://code.google.com/p/google-gson/ (licensed under Apache License 2.0) and
PIrcBot from http://www.jibble.org/pircbot.php

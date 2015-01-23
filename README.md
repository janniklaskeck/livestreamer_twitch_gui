livestreamer_twitch_gui
=======================
GUI for the livestreamer application (http://livestreamer.tanuki.se/)
Uses Gson from https://code.google.com/p/google-gson/

Currently only works under Windows

I wrote this, because all of the GUI for livestreamer did not do what i wanted, at least back then.

Many parts of this are poorly written and probably not commented.
I originally got the contents of Twitch_API.java from some forum i can't remember, 
but i had to rewrite it, after justin.tv was closed.

If you find any errors or have (reasonable) suggestions, please let me know

!!IMPORTANT!!
To add another streaming service other than twitch.tv, press shift while pressing the plus button.
This should open a popup window where you can type in the data. If it does not, try clicking into the current list and then press shift and the plus button. Removing works the same way.

Note however, the resolution options might not work with other streaming sites other than twitch. Worst and Best Quality should work on most. Also when adding another site, take a close look how the final stream urls look like.
E.g. on twitch.tv it is twitch.tv/channelname (only add "channelname"), on livestream.com however it is new.livestream.com/channelname/live (here you have to add new.livestream.com as a site and "channelname/live" as a channel).

Things like online status and preview image are currently twitch.tv only, as i'm using their API to get the information.

This feature isn't fully fleshed out, so please report bugs if you find them.

For the update interval a 30 seconds is recommended depeding on number of streams in your list and how many of them are online at the same time). 20 Seconds is the minimum.

The Export feature should work fine, it is only tested on windows.
You may have to restart the application after importing to see your streams. I didn't test this feature a lot, there are probably some bugs.

!!IMPORTANT!!

Preview Image:<br>
<img width="auto" height="auto" src="https://github.com/westerwave/livestreamer_twitch_gui/blob/master/preview.png"></img>
<br>
Planned Features:<br>
- Window resizing
- Error output window
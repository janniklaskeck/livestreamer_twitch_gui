#Livestreamer GUI v3
GUI for the livestreamer application (http://livestreamer.tanuki.se/)<br>
Its intended use is for twitch.tv but there is rudimentary support for other services.

#Features
 - List of channels for multiple Streaming Services
 - Choose from available qualities
 - Start Livestreamer stream or record it
 - Open Stream in Browser

#Twitch.tv exclusive Features
 - Update Channels with information like current game, viewer count, uptime and preview Image
 - Simple IRC Chat client
 - Simple Twitch.tv Browser, shows configurable amount of Games and Channels, favourite List for Games

##IMPORTANT
Twitch recently made Authentication to their API <b>mandatory</b>.<br>
If you want to use these features, you have to generate an OAuth Token for your account and save it in the settings,
either in the form "oauth:xyzabc" or just "xyzabc" (without the " of course).<br>
To generate such a Token you can use a Site like https://twitchapps.com/tmi/.<br>
To use the Chat client, you have to provide your Twitch.tv Username as well.
This application only saves your Token locally on your Computer, in doubt look at the Code and build yourself.

#Requirements
 - Current Java 8 JRE

#Download
You can find the download links over at the http://github.com/westerwave/livestreamer_twitch_gui/releases section.


#Preview
![lightscreen](https://cloud.githubusercontent.com/assets/1731203/15981648/b4d03d80-2f78-11e6-929f-038a112af26e.png)
![darkscreen](https://cloud.githubusercontent.com/assets/1731203/15981649/b64ee31e-2f78-11e6-879d-efde71877bc8.png)
![notification](https://cloud.githubusercontent.com/assets/1731203/15981650/b7a30dc6-2f78-11e6-951b-289c408c6c6a.png)

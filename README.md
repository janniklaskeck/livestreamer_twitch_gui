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
![lightscreen](https://cloud.githubusercontent.com/assets/1731203/18693068/3939c61a-7f9f-11e6-83f7-147ae6542bd7.PNG)
![darkscreen](https://cloud.githubusercontent.com/assets/1731203/18693067/391edb98-7f9f-11e6-891a-b06d527bc33b.PNG)
![settings](https://cloud.githubusercontent.com/assets/1731203/18693070/394089e6-7f9f-11e6-8698-d7b5533fc70c.PNG)
![browserGames](https://cloud.githubusercontent.com/assets/1731203/18693071/3958201a-7f9f-11e6-8fcd-a23ee82fe6df.PNG)
![browserChannels](https://cloud.githubusercontent.com/assets/1731203/18693069/393d4934-7f9f-11e6-8e49-459debc91c3d.PNG)
![notification](https://cloud.githubusercontent.com/assets/1731203/15981650/b7a30dc6-2f78-11e6-951b-289c408c6c6a.png)

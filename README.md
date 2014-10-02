JavaScreenCapture
=================
Allows to record a video of your desktop screen. And is completely platform independent, if your platform can run Java, this will run on it, as its only dependency is AWT, which is supplied with the JRE version of SE platform since version 1.3 !!! Altough it does use a modern scheduler construct from JRE version 1.7 so you will need atleast that to run this :P.

Screen capture written in pure java, no dependencies, runs at ANY FPS !!! (Limited by machine's performance)
Runs 30FPS+ on my machine no problem whatsover without even loading my machine.
Recording is smooth although with some settings weird things can happen.

###Implementation###

Written using AWT's Robot class, but as many of you will know, with that thing you can almost never get above 15FPS but you want 30 or atleast 25.

But no matter how much you optimize you just can't do it. That is because the native calls that they use are quite slow. Fast enough for screenshots but too slow for video.

If one Robot can capture at about 12FPS, what about 2 Robots ? Can they both capture at 12FPS ? THE ANSWER IS YES !!!
It only becomes a matter of scheduling. And that is what I do here. 
I have a couple of threads with Robot's that are scheduled using ScheduledThreadPoolExecutor construct from Java 7.
Each are scheduled to start capturing one after another in such manner that they become interleaved. Constucting a pipeline of Screencapturing robots I am able to achieve higher FPS by combining few low FPS robots.

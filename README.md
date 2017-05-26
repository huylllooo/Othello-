Othello Game
===============================
Server/Client programs allows multiple users to play with server’s AI.
Further description can be view ['here'](https://docs.google.com/document/d/1kUhQb_2QC6ZydOxRn0TH-FTgd-9x0WgA_Im5RN-hA0w/edit?usp=sharing)

## Run the code

### Unix/Linux

Prompt your terminal to 'Othello_Unix' folder.

#### 1.Build file

Use command

    ant jar

to compile and make executable file c7.jar

#### 2.Execute

Server

	java -jar c7.jar server [PORT]

RemoteClient

    java -jar c7.jar client [HOST] [PORT]

RandomClient

    java -jar c7.jar rclient [HOST] [PORT]

HumanClient

    java -jar c7.jar hclient [HOST] [PORT]

### Windows (Eclipse IDE)

Create a new package named 'jp.ac.tohoku.ecei.sf' and import all the files.

Run 'ServerTest.java' first to set up the server.
Run 'ClientTest.java' to create a client, connect to server and start a new game.

*Note:* PORT_number is hard-coded in 'ServerTest.java' file.
import java.io.*;//JavaScript source code
import java.net.*;
import java.nio.charset.StandardCharsets;

public class client {
    public static void main(String[] args) //Declaring Variables
	{ 
        try {
			    Socket s = new Socket("localhost", 50000);          
			    String[] LargestServer = {""};
			    boolean LargestFound = false;
			    String CurrentMsg = "";
			    handshake(s);

            while (!CurrentMsg.contains("NONE"))// If there are more jobs, continue while loop
			{
                sendMsg(s, "REDY\n");
                CurrentMsg = readMsg(s);
                if (CurrentMsg.contains("JOBN")) 
				{
                    String[] JOBNSplit = CurrentMsg.split(" ");
                    sendMsg(s, "GETS Avail " + JOBNSplit[4] + " " + JOBNSplit[5] + " " + JOBNSplit[6] + "\n");
                    CurrentMsg = readMsg(s);
                    sendMsg(s, "OK\n"); //If statement reads message and replies with "OK"
                    CurrentMsg = readMsg(s);
                    sendMsg(s, "OK\n");
                    if(LargestFound == false)//Checks for largest server
					{ 
                        LargestServer = findLargestServer(CurrentMsg);
                        LargestFound = true;
                    }
                    CurrentMsg = readMsg(s); //Reads message from the server           
                    sendMsg(s, "SCHD " + JOBNSplit[2] + " " + LargestServer[0] + " " + LargestServer[1] + "\n");//Schedules the current job to largest server available.
                    CurrentMsg = readMsg(s);
                    System.out.println("SCHD: " + CurrentMsg); //Next Job
                }
                else if (CurrentMsg.contains("DATA")) 
				{
                    sendMsg(s, "OK\n");
                }
            }
            sendMsg(s, "QUIT\n"); //Quits to end current session
            s.close();
        } 
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public static synchronized String readMsg(Socket s)//This function reads message
	{ 
        String CurrentMsg = "FAIL";
        try
		{
            DataInputStream dis = new DataInputStream(s.getInputStream());
            Byte[] ByteArray = new Byte[dis.available()];
            ByteArray = new Byte[0]; //Prepares Array for message
            while (ByteArray.length == 0)
			{
                ByteArray = new Byte[dis.available()];
                dis.read(ByteArray);
                CurrentMsg = new String(ByteArray, StandardCharsets.UTF_8); //Creates String
            }
        } 
		catch (IOException e) 
		{
            e.printStackTrace(); //Prints String
        }
        return CurrentMsg; //Returns current message
    }

    public static synchronized void sendMsg(Socket s, String CurrentMsg) 
	{    //This function sends messages
        try 
		{
				DataOutputStream Dout = new DataOutputStream(s.getOutputStream());
			    Byte[] ByteArray = CurrentMsg.getBytes(); //Converts string for sending to server
			    Dout.write(ByteArray);
                Dout.flush();
        } 
		catch (IOException e)
		{
            e.printStackTrace();
        }
    }

    public static void handshake(Socket s)//Handshaking with Server
	{ 
        String CurrentMsg = "";
        sendMsg(s, "HELO\n");
        CurrentMsg = readMsg(s);
        System.out.println("RCVD: " + CurrentMsg);
        sendMsg(s, "AUTH " + System.getProperty("user.name") + "\n");//Authentication
        CurrentMsg = readMsg(s);
        System.out.println("RCVD: " + CurrentMsg);
    }

    public static String[] findLargestServer(String CurrentMsg)//Searches for largest available server
	{
		String[] serversAndInfo = CurrentMsg.split("\n");
        int MostCores = 0;
        String[] CurrentServer = {""};
        for (int i = 0; i < serversAndInfo.length; i++) 
		{
            CurrentServer = serversAndInfo[i].split(" ");
            int CurrentCores = Integer.valueOf(CurrentServer[4]);
            if (CurrentCores > MostCores) 
			{
                MostCores = CurrentCores;
            }
        }
        for (int i = 0; i < serversAndInfo.length; i++)//Returns largest server with most cores
		{ 
            CurrentServer = serversAndInfo[i].split(" ");
            int CurrentCores = Integer.valueOf(CurrentServer[4]);
            if (CurrentCores == MostCores) 
			{
                return CurrentServer;
            }
        }
        return CurrentServer;
    }
}
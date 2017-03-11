package raspPiSide;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    ServerSocket socketServer;
    Socket clientSocket;
    DataInputStream DIS;
    DataOutputStream DOS;
    LoginsCSV logins;
    
    String pathToFolder;
    String currentActiveAttendancePeriodClassName;
    attendancePeriodCSV activePeriod;
    /*
    public static void main(String[] args) throws IOException {
        ServerSocket socketServer = new ServerSocket(1420);
        Socket clientSocket = socketServer.accept();       //This is blocking. It will wait.
        DataInputStream DIS = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream DOS = new DataOutputStream(clientSocket.getOutputStream());
        while(true){
            clientSocket = socketServer.accept();
            String input = DIS.readUTF();
            serveRequest(input);
        }
    }
    */

    public Server() throws IOException {
        socketServer = new ServerSocket(1420);
        logins = new LoginsCSV("un.txt");
    }

    public void GetNextRequest() throws IOException {
        Socket clientSocket = socketServer.accept(); //This is blocking. It will wait.
        DIS = new DataInputStream(clientSocket.getInputStream());
        DOS = new DataOutputStream(clientSocket.getOutputStream());

        serveRequest(DIS.readUTF());
    }

    private void serveRequest(String in) throws IOException {
        String[] split = in.split("&");
        int code;
        try {
            code = Integer.parseInt(split[0]);
        } catch(Exception e) {
            DOS.writeUTF("102&Bad request");
            return;
        }
        switch(code) {
            case 0: //Logins
                if (split.length != 3) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                
                String username = split[1];
                String password = split[2];
                
                User user = logins.getUser(username);
                
                if(user == null) {
                	DOS.writeUTF("103&Username and password did not match");
                }
                
                if(username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                	if(user.isTeacher()) DOS.writeUTF("1&Teacher");
                	else DOS.writeUTF("0&Is not teacher");
                } else {
                	DOS.writeUTF("103&Username and password did not match");
                }
                break;
            case 1: //Register
                if (split.length != 5) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                username = split[1];
                password = split[2];
                String name = split[3];
                String isTeacher = split[4];
                try {
                    Integer.parseInt(isTeacher);
                } catch(Exception e) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                logins.addUser(username, password, name, isTeacher);
                logins.write();
                
                DOS.writeUTF("0&Success!");
                break;
            case 2: //Student submit attendance
                if (split.length != 2) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                username = split[1];
                
                user = logins.getUser(username);
                if(user == null) {
                	DOS.writeUTF("104&Error retrieving account");
                	return;
                }
                
                if(currentActiveAttendancePeriodClassName == null) {
                	DOS.writeUTF("105& No active attendance period");
                	return;
                } else {
                	boolean b = activePeriod.submitAttendance(user.getName());
                	if(b) DOS.writeUTF("0&Attendance recieved!");
                	else DOS.writeUTF("106&Student not found in class");
                }
                
                break;
            case 3: //Begin attendance -------------NOT DONE YET, FIX IT FIX IT FIX IT
                if (split.length != 4) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                username = split[1];
                String timeAsStr = split[2];
                String className = split[3];
                int realTimeInSeconds;

                try {
                    realTimeInSeconds = Integer.parseInt(timeAsStr);
                } catch(Exception e) {
                    DOS.writeUTF("102&Bad request");
                }

                    break;
            case 4: //Cancel attendance
                if (split.length != 2) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                
                if(currentActiveAttendancePeriodClassName == null) {
                	DOS.writeUTF("105& No active attendance period");
                	return;
                }
                
                username = split[1];
                user = logins.getUser(username);
                
                if(user == null) {
                	DOS.writeUTF("104&Error retrieving account");
                	return;
                }
                
                String[] classList = user.getClassList();
                for(String cl : classList) {
                	if(cl.equals(currentActiveAttendancePeriodClassName)) {
                		activePeriod.cancelAttendancePeriod();
                		DOS.writeUTF("0&Success!");
                	}
                }
                DOS.writeUTF("107&You don't have access to this class");
                
                break;
            case 5: //Create new class AHAHHHHHHHHHHHHHHHHHHHHHHHHH
                DOS.writeUTF("200&Create new class bad");
                break;
            case 6: //Delete class FIX IT FIX IT FIX ITIIITITITTITITIT
                if (split.length != 3) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                username = split[1];
                className = split[2];
                DOS.writeUTF("201&Delete class bad");
                
                break;
            case 7: //Get record
                if (split.length != 3) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                username = split[1];
                className = split[2];
                
                String reponse = activePeriod.toString();
                DOS.writeUTF("202&Get record bad");
                break;

        }
    }
}
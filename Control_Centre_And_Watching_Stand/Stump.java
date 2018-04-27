import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.lang.ClassNotFoundException;

public class Stump{
    public static void main(String[] args){
        GeneralRepositoryOfInformation groi = null;
        try{
            //Creates input and output streams
            InetAddress sourceAddress = InetAddress.getByName("localhost");
            int sourcePort = 23040;
            Socket echoSocket = new Socket(InetAddress.getByName(args[0]), Integer.valueOf(args[1]), sourceAddress, sourcePort + 1);
            groi = new GeneralRepositoryOfInformation(echoSocket);
            
            //Calls method setMonitorAddress for monitor #3 (Control Centre and Watching Stand)
            groi.setMonitorAddress(sourceAddress, sourcePort, 3);

            //Gets variables necessary for ControlCentre
            int raceLength = groi.getRaceLength();
            int numSpectators = groi.getNumberOfSpectators();
            int numHorses = groi.getNumberOfHorses();

            ControlCentreAndWatchingStand ccws = new ControlCentreAndWatchingStand(groi);

        
            //Monitor is now open to requests from clients
            ServerSocket serverSocket = new ServerSocket(sourcePort);
            while(true){
                new ClientThread(serverSocket.accept(), ControlCentreAndWatchingStand.class, ccws).run();
            }

        } catch(IOException e){
            e.printStackTrace();
        } finally{
            groi.close();
        }
    }
}
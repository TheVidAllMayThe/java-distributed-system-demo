import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static int closed = 0;
    public static ReentrantLock r1 = new ReentrantLock(false);
    public static void main(String[] args){
        GeneralRepositoryOfInformation groi = null;
        try{
            //Creates input and output streams
            int sourcePort = Integer.valueOf(args[0]);
            Socket echoSocket = null;
            while(echoSocket == null){
                try{
                    echoSocket = new Socket(InetAddress.getByName(args[1]), Integer.valueOf(args[2]));
                }catch(Exception e){
                }
            }

            groi = new GeneralRepositoryOfInformation(echoSocket);
            
            //Calls method setMonitorAddress for monitor #4 (Race_Track)
            groi.setMonitorAddress(InetAddress.getLocalHost(), sourcePort, 4);
            int numRaces = groi.getNumberOfRaces();

            RaceTrack rt = new RaceTrack(groi);

        
            //Monitor is now open to requests from clients
            ServerSocket serverSocket = new ServerSocket(sourcePort);
            serverSocket.setSoTimeout(1000);
            while(closed < 1 + rt.getNumberOfHorses() * numRaces){
                try{
                    new ClientThread(serverSocket.accept(), rt).start();
                }catch (SocketTimeoutException e){
                }
            }

        } catch(IOException e){
            e.printStackTrace();
        } finally{
            groi.close();
        }
    }

    public static void closed(){
        r1.lock();
        try{
            closed++;
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            r1.unlock();
        }
    }
}

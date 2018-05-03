import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;


/**
 * The {@link RaceTrack} class is a monitor that contains
 * necessary methods to be used in mutual exclusive access by multiple {@link Horse}s and by the {@link Broker}.
 * <p>
 * This is where the {@link Horse}s compete with each other to reach the end of the race.
 *
 * @author  David Almeida, Manuel Xarez
 * @version 1.0
 * @since   2018-03-21
 * @see Main.HorseRace

 */

@SuppressWarnings("JavadocReference")
public class RaceTrack extends MonitorProxy{

    RaceTrack(InetSocketAddress address){
        super(address);
    }

    public void startTheRace(){
        try {
            

            LinkedList<Object> list = new LinkedList<>();
            list.add("startTheRace");

            out.writeObject(list);
            out.flush();

            if(!in.readObject().equals("ok"))
                System.out.println("Something wrong in openingTheEvents of Broker");

            
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }


    public Integer[] reportResults(){
        Integer[] result = null;
        try {
            

            LinkedList<Object> list = new LinkedList<>();
            list.add("reportResults");

            out.writeObject(list);
            out.flush();
            
            result = (Integer[])in.readObject();

            
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return result;
    }
}
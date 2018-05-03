import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;

public class ControlCentreAndWatchingStand extends MonitorProxy{

    ControlCentreAndWatchingStand(InetSocketAddress address){
        super(address);
    }

    public void openingTheEvents() {
        try {
            

            LinkedList<Object> list = new LinkedList<>();
            list.add("openingTheEvents");

            out.writeObject(list);
            out.flush();

            if(!in.readObject().equals("ok"))
                System.out.println("Something wrong in openingTheEvents of Broker");

            
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void summonHorsesToPaddock(int numRace) {
        try {
            

            LinkedList<Object> list = new LinkedList<>();
            list.add("summonHorsesToPaddock");
            list.add(numRace);

            out.writeObject(list);
            out.flush();

            if(!in.readObject().equals("ok"))
                System.out.println("Something wrong in openingTheEvents of Broker");

            
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void startTheRace() {
        try {
            

            LinkedList<Object> list = new LinkedList<>();
            list.add("startTheRace");

            out.writeObject(list);
            out.flush();

            if(!in.readObject().equals("ok"))
                System.out.println("Something wrong in startTheRace of Broker");

            
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void reportResults(Integer[] results) {
        try {
            

            LinkedList<Object> list = new LinkedList<>();
            list.add("reportResults");
            list.add(results);

            out.writeObject(list);
            out.flush();

            if(!in.readObject().equals("ok"))
                System.out.println("Something wrong in reportResults of Broker");

            
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void entertainTheGuests() {
        try {
            LinkedList<Object> list = new LinkedList<>();
            list.add("entertainTheGuests");

            out.writeObject(list);
            out.flush();

            if(!in.readObject().equals("ok"))
                System.out.println("Something wrong in entertainTheGuests of Broker");

            
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
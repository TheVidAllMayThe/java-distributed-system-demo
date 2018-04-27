import java.util.LinkedList;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.net.InetAddress;

public class GeneralRepositoryOfInformation{
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public GeneralRepositoryOfInformation(Socket socket){
        try{
            this.clientSocket = socket;
            this.out = new ObjectOutputStream(clientSocket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(clientSocket.getInputStream());
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void close(){
        try{
            //Closes sockets both ways
            this.out.writeObject("close");
            this.out.flush();
            this.out.close();
            this.in.close();
            this.clientSocket.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setMonitorAddress(InetAddress sourceAddress, int sourcePort, int monitor){
        LinkedList<Object> list = new LinkedList<>();
        list.add("setMonitorAddress");
        list.add(sourceAddress);
        list.add(sourcePort);
        list.add(monitor);

        try{
            out.writeObject(list);
            out.flush();
            if(!((String)in.readObject()).equals("ok"))
                System.out.println("Something wrong in setMonitorAddress of GRI");
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        } 
    }

    public void setRaceNumber(int numRace){
        LinkedList<Object> list = new LinkedList<>();
        list.add("setRaceNumber");
        list.add(numRace);

        try{
            out.writeObject(list);
            out.flush();
            if(!((String)in.readObject()).equals("ok"))
                System.out.println("Something wrong in setRaceDistance of GRI");
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        } 
    }

    public void setRaceDistance(int raceLength){
        LinkedList<Object> list = new LinkedList<>();
        list.add("setRaceDistance");
        list.add(raceLength);

        try{
            out.writeObject(list);
            out.flush();
            if(!((String)in.readObject()).equals("ok"))
                System.out.println("Something wrong in setRaceDistance of GRI");
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        } 
    }

    public int getNumberOfSpectators(){
        LinkedList<Object> list = new LinkedList<>();
        list.add("getNumberOfSpectators");
        
        int result = -1;
        try{
            out.writeObject(list);
            out.flush();
            
            result = (int)in.readObject();
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        } 
        return result;
    }

    public int getNumberOfHorses(){
        LinkedList<Object> list = new LinkedList<>();
        list.add("getNumberOfHorses");
        
        int result = -1;
        try{
            out.writeObject(list);
            out.flush();
            
            result = (int)in.readObject();
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        } 
        return result;
    }

    public int getRaceLength(){
        LinkedList<Object> list = new LinkedList<>();
        list.add("getRaceLength");
        
        int result = -1;
        try{
            out.writeObject(list);
            out.flush();
            
            result = (int)in.readObject();
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        } 
        return result;
    }

    public void setBrokerState(String state){
        LinkedList<Object> list = new LinkedList<>();
        list.add("setBrokerState");
        list.add(state);

        try{
            out.writeObject(list);
            out.flush();
            if(!((String)in.readObject()).equals("ok"))
                System.out.println("Something wrong in setBrokerState of GRI");
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        }
    }

    public void setHorsesState(String state, int horseID){
        LinkedList<Object> list = new LinkedList<>();
        list.add("setHorsesState");
        list.add(state);
        list.add(horseID);

        try{
            out.writeObject(list);
            out.flush();
            if(!((String)in.readObject()).equals("ok"))
                System.out.println("Something wrong in setHorsesState of GRI");
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        }
    }

    public void setHorseProbability(double prob, int horseID){
        LinkedList<Object> list = new LinkedList<>();
        list.add("setHorseProbability");
        list.add(prob);
        list.add(horseID);

        try{
            out.writeObject(list);
            out.flush();
            if(!((String)in.readObject()).equals("ok"))
                System.out.println("Something wrong in setHorseProbability of GRI");
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        }
    }

    public void setSpectatorsState(String state, int spectatorID){
        LinkedList<Object> list = new LinkedList<>();
        list.add("setSpectatorsState");
        list.add(state);
        list.add(spectatorID);

        try{
            out.writeObject(list);
            out.flush();
            if(!((String)in.readObject()).equals("ok"))
                System.out.println("Something wrong in setBrokerState of GRI");
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        }
        
    }

    public void setSpectatorsSelection(int horseID, int spectatorID){
        LinkedList<Object> list = new LinkedList<>();
        list.add("setSpectatorsSelection");
        list.add(horseID);
        list.add(spectatorID);

        try{
            out.writeObject(list);
            out.flush();
            if(!((String)in.readObject()).equals("ok"))
                System.out.println("Something wrong in setSpectatorsSelection of GRI");
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        }
        
    }

    public void setSpectatorsBudget(double budget, int spectatorID){
        LinkedList<Object> list = new LinkedList<>();
        list.add("setSpectatorsBudget");
        list.add(budget);
        list.add(spectatorID);

        try{ 
            out.writeObject(list);
            out.flush();
       
            if(!((String)in.readObject()).equals("ok"))
                System.out.println("Something wrong in setSpectatorsBudget of GRI");
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        }
    }

    public void setSpectatorsBet(double value, int spectatorID){
        LinkedList<Object> list = new LinkedList<>();
        list.add("setSpectatorsBet");
        list.add(value);
        list.add(spectatorID);

        
        try{
            out.writeObject(list);
            out.flush();
            if(!((String)in.readObject()).equals("ok"))
                System.out.println("Something wrong in setSpectatorsBet of GRI");
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();    
        }
    }
}
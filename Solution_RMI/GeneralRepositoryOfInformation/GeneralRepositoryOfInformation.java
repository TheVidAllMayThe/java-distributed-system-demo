import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.InetAddress;


/**
 * This class stores the state of all the components of the program. Every time that there's a change the program logs the new state to a file (log.txt).
 */

public class GeneralRepositoryOfInformation implements GeneralRepositoryOfInformation_Interface{
    private ReentrantLock r1 = new ReentrantLock(false);
    private Condition[] conditions = new Condition[5];
    private PrintWriter writer;
    private Registry registry;

    private InetAddress[] monitorAddresses = new InetAddress[5];
    private int[] monitorPorts = new int[5];
    private int numberOfRaces, numberOfHorses, numberOfSpectators, raceLength;

    private String brokerState;
    private String[] spectatorsState;
    private String[] horsesState;
    private String[] spectatorsBudget;
    private int raceNumber ;
    private String[] horsesPnk;
    private String raceDistance;
    private String[] spectatorsSelection;
    private String[] spectatorsBet;
    private String[] horseProbability; 
    private String[] horseIteration;
    private String[] horseTrackPosition;
    private String[] horsesStanding;
    private boolean end = false;



    public GeneralRepositoryOfInformation(int numberOfRaces, int numberOfHorses, int numberOfSpectators, int raceLength, Registry registry){
        this.numberOfRaces = numberOfRaces;
        this.numberOfHorses = numberOfHorses;
        this.numberOfSpectators = numberOfSpectators;
        this.raceLength = raceLength;
        this.registry = registry;
        for(int i=0; i < conditions.length; i++) conditions[i] = r1.newCondition();

        try{
            writer = new PrintWriter("log.txt");

        }catch(FileNotFoundException fe){
            fe.printStackTrace();
        }

        brokerState = "----";
        spectatorsState = new String[numberOfSpectators];
        for(int i=0; i < spectatorsState.length; i++) spectatorsState[i] = "---";
        horsesState = new String[numberOfHorses];
        for(int i=0; i < horsesState.length; i++) horsesState[i] = "---";
        spectatorsBudget = new String[numberOfSpectators];
        for(int i=0; i < spectatorsBudget.length; i++) spectatorsBudget[i] = "----";
        raceNumber = 0; 
        horsesPnk = new String[numberOfHorses];
        for(int i=0; i < horsesPnk.length; i++) horsesPnk[i] = "--";
        raceDistance = "--";
        spectatorsSelection = new String[numberOfSpectators];
        for(int i=0; i < spectatorsSelection.length; i++) spectatorsSelection[i] = "-";
        spectatorsBet = new String[numberOfSpectators];
        for(int i=0; i < spectatorsBet.length; i++) spectatorsBet[i] = "----";
        horseProbability = new String[numberOfHorses];
        for(int i=0; i < horseProbability.length; i++) horseProbability[i] = "----";
        horseIteration = new String[numberOfHorses];
        for(int i=0; i < horseIteration.length; i++) horseIteration[i] = "--";
        horseTrackPosition = new String[numberOfHorses];
        for(int i=0; i < horseTrackPosition.length; i++) horseTrackPosition[i] = "--";
        horsesStanding = new String[numberOfHorses];
        for(int i=0; i < horsesStanding.length; i++) horsesStanding[i] = "-";



        writer.println("MAN/BRK           SPECTATOR/BETTER              HORSE/JOCKEY PAIR at Race RN");
        writer.println("  Stat  St0  Am0 St1  Am1 St2  Am2 St3  Am3 RN St0 Len0 St1 Len1 St2 Len2 St3 Len3");
        writer.println("                                        Race RN Status");
        writer.println("RN Dist BS0  BA0 BS1  BA1 BS2  BA2 BS3  BA3  Od0  N0 Ps0 SD0 Od1  N1 Ps1 Sd1 Od2  N2 Ps2 Sd2 Od3  N3 Ps3 St3");
        writer.flush();

    }

    public void close(){
        writer.close();
    }
 
    private void log(){
        try{

            StringBuilder line1 = new StringBuilder(String.format("  %4s ", brokerState));
            StringBuilder line2 = new StringBuilder(String.format(" %1d  %2s  ", raceNumber, raceDistance));


            for(int i=0; i<numberOfSpectators; i++){
                line1.append(String.format("%4s %3s", spectatorsState[i], spectatorsBudget[i]));
                line2.append(String.format(" %1s  %4s ", spectatorsSelection[i], spectatorsBet[i]));
            }
            
            line1.append(String.format("  %1d ", raceNumber));

            for(int i=0; i<numberOfHorses; i++){
                line1.append(String.format("%3s  %2s  ", horsesState[i], horsesPnk[i]));
                line2.append(String.format("%4s  %2s  %2s  %1s ", horseProbability[i], horseIteration[i], horseTrackPosition[i], horsesStanding[i]));
            }

            writer.println(line1);
            writer.println(line2);
        }catch(Exception fe){
            fe.printStackTrace();
        }
        writer.flush();
    }    
    
    
    public void setMonitorAddress(InetAddress address, Integer port, Integer monitor){
        r1.lock();
        try{
            monitorAddresses[monitor] = address;
            monitorPorts[monitor] = port;
            System.out.println("Monitor " + monitor + ": " + address + "/" + port);
            conditions[monitor].signalAll();
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            r1.unlock();
        }
    }

    
    public InetAddress getMonitorAddress(Integer monitor){
        r1.lock();
        try{
            while(monitorAddresses[monitor] == null) conditions[monitor].await();
        } catch(InterruptedException ie){
            ie.printStackTrace();
        } finally{
            r1.unlock();
        }
        return monitorAddresses[monitor];
    }

    
    public int getMonitorPort(Integer monitor){
        r1.lock();
        try{
            while(monitorPorts[monitor] == 0) conditions[monitor].await();
        } catch(InterruptedException ie){
            ie.printStackTrace();
        } finally{
            r1.unlock();
        }
        return monitorPorts[monitor];
    }

    
    public void setBrokerState(String state){
        r1.lock();
        try{
            brokerState = state;
            log();

            if(state.equals("PHAB"))
                checkIfOver();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        } 
    }

    
    public void setSpectatorsState(String state, Integer i){
        r1.lock();
        try{
            spectatorsState[i] = state;
            log();
            if(state.equals("CEL")) checkIfOver();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }
    
    
    public void setHorsesState(String state, Integer i){
        r1.lock();
        try{
            horsesState[i] = state;
            log();
            if(state.equals("ATS")) checkIfOver();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    
    public void setSpectatorsBudget(Double budget, Integer i){
        r1.lock();
        try{
            String temp = "" + budget.intValue();
            for(int j=temp.length(); j<4; j++) temp = "0" + temp;
            spectatorsBudget[i] = temp;
            log();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    
    public void setRaceNumber(Integer raceNu){
        r1.lock();
        try{
            raceNumber = raceNu;
            log();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    
    public void setHorsesPnk(Integer pnk, Integer i){
        r1.lock();
        try{
            String temp = "" + pnk;
            for(int j=temp.length(); j<2; j++) temp = "0" + temp;
            horsesPnk[i] = temp;
            log();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    
    public void setRaceDistance(Integer distance){
        r1.lock();
        try{
            if(distance < 10) raceDistance = "0"+distance;
            else raceDistance = "" + distance;
            log();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    
    public void setSpectatorsSelection(Integer horse, Integer i){
        r1.lock();
        try{
            spectatorsSelection[i] = "" + horse;
            log();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    
    public void setSpectatorsBet(Double bet, Integer i){
        r1.lock();
        try{
            String temp = "" + bet.intValue();
            for(int j=temp.length(); j<4; j++) temp = "0" + temp;
            spectatorsBet[i] = temp;
            log();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    
    public void setHorseProbability(Double prob, Integer i){
        r1.lock();
        try{

            String temp;
            if(prob < 0) temp = "----";
            else{
                temp = "" + prob.intValue();
                for(int j=temp.length(); j<4; j++) temp = "0" + temp;
            }
            horseProbability[i] = temp;
            log();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    
    public void setHorseIteration(Integer iteration, Integer i){
        r1.lock();
        try{
            if(iteration < 0) horseIteration[i] = "--";
            else{
                if(iteration < 10) horseIteration[i] = "0" + iteration;
                horseIteration[i] = "" + iteration;
            }
            log();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    
    public void setHorseTrackPosition(Integer position, Integer i){
        r1.lock();
        try{
            if(position < 0) horseTrackPosition[i] = "--";
            else{
                if(position < 10) horseTrackPosition[i] = "0" + position;
                horseTrackPosition[i] = "" + position;
            }
            log();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    
    public void setHorsesStanding(Character standing, Integer i){
        r1.lock();
        try{
            horsesStanding[i] = "" + standing;
            log();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    
    public int getNumberOfSpectators(){
        return this.numberOfSpectators;
    }

    
    public int getNumberOfHorses(){
        return this.numberOfHorses;
    }

    
    public int getRaceLength(){
        return this.raceLength;
    }
    
    public int getNumberOfRaces(){
        return this.numberOfRaces;
    }

    public void bindObject(String name, Remote obj) {
        try{
            registry.rebind(name, obj);
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }

    private void checkIfOver(){

        if(end) return;

        if(!brokerState.equals("PHAB")) return;

        for(String i: spectatorsState)
            if(!i.equals("CEL")) return;

        for(String i: horsesState)
            if(!i.equals("ATS")) return;

        end = true;

        Register reg = null;
        try {
            reg = (Register) registry.lookup("RegisterHandler");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }


        System.out.println("\nClosing BettingCentre...");

        try {
            ((BettingCentre_Interface)registry.lookup("BettingCentre")).close();
            reg.unbind("BettingCentre");
        }catch (UnmarshalException ignore){}
         catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        System.out.println("Closed BettingCentre\n");

        System.out.println("Closing ControlCentre...");
        try {
            ((ControlCentreAndWatchingStand_Interface) registry.lookup("ControlCentreAndWatchingStand")).close();
            reg.unbind("ControlCentreAndWatchingStand");
        }catch (UnmarshalException ignore){}
        catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        System.out.println("Closed ControlCentre\n");
        System.out.println("Closing Paddock...");

        try {
            ((Paddock_Interface)registry.lookup("Paddock")).close();
            reg.unbind("Paddock");
        }catch (UnmarshalException ignore){}
         catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        System.out.println("Closed Paddock\n");
        System.out.println("Closing RaceTrack..");


        try {
            ((RaceTrack_Interface)registry.lookup("RaceTrack")).close();
            reg.unbind("RaceTrack");
        }catch (UnmarshalException ignore){}
         catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        System.out.println("Closed RaceTrack");
        System.out.println("Closing Stable...");

        try {
            ((Stable_Interface)registry.lookup("Stable")).close();
            reg.unbind("Stable");
        }catch (UnmarshalException ignore){}
         catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        System.out.println("Closed Stable\n");
        System.out.println("Closing myself...");

        try {
            UnicastRemoteObject.unexportObject(this, true);
            reg.unbind("GeneralRepositoryOfInformation");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}

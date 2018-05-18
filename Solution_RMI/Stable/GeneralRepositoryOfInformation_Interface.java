import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

public interface GeneralRepositoryOfInformation_Interface extends Remote{
    void setMonitorAddress(InetAddress address, Integer port, Integer monitor) throws RemoteException;
    void setHorsesState(String state, Integer i) throws RemoteException;
    void setHorsesPnk(Integer pnk, Integer i) throws RemoteException;
    void setHorseProbability(Double prob, Integer i) throws RemoteException;
    void setHorseIteration(Integer iteration, Integer i) throws RemoteException;
    void setHorseTrackPosition(Integer position, Integer i) throws RemoteException;
    void setHorsesStanding(Character standing, Integer i) throws RemoteException;
    int getNumberOfHorses() throws RemoteException;
    int getRaceLength() throws RemoteException;
    int getNumberOfRaces() throws RemoteException;
    void bindObject(String name, Remote obj) throws RemoteException;
}

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * The {@link Stable} class is a monitor that contains all the
 * necessary methods to be used in mutual exclusive access by the Broker and Horses to itself.
 * <p>
* This is where the Horses are initially and after a race.
*
* @author  David Almeida, Manuel Xarez
* @version 1.0
* @since   2018-03-21

*/

public class Stable {
    private ReentrantLock r1;
    private Condition horsesToPaddock;
    private boolean canHorsesMoveToPaddock;
    private Condition newRace;
    private int numHorses;
    private int raceNumber;
    private GeneralRepositoryOfInformation groi;
    private int numberOfHorses, numberOfRaces, raceLength;

    Stable(GeneralRepositoryOfInformation groi){
        r1 = new ReentrantLock();
        horsesToPaddock = r1.newCondition();
        canHorsesMoveToPaddock = false;
        newRace = r1.newCondition();
        numHorses = 0;
        raceNumber = -1;
        numberOfHorses = groi.getNumberOfHorses();
        numberOfRaces = groi.getNumberOfRaces();
        raceLength = groi.getRaceLength();
        this.groi = groi;
        System.out.println("numberOfHorses: " + numberOfHorses);
        System.out.println("numberOfRaces: " + numberOfRaces);
        System.out.println("raceLength: " + raceLength);
    }

    /**
     * Broker awakes the Horses who are waiting to enter the Paddock.
     */
    public void summonHorsesToPaddock(){
        r1.lock();
        try{
            raceNumber++;
            canHorsesMoveToPaddock = true;
            newRace.signalAll();
            horsesToPaddock.signal();
        }catch (IllegalMonitorStateException e){
            e.printStackTrace();
        }finally {
            r1.unlock();
        }
    }

    /**
     * Last function of Broker lifecycle, awakes Horses waiting to enter Paddock.
     */
    public void entertainTheGuests(){
        r1.lock();
        try{
            raceNumber++;
            canHorsesMoveToPaddock = true;
            newRace.signalAll();
            horsesToPaddock.signal();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    /**
     * Horses wait to move to Paddock.
     * @param horseID Id of the horse calling the method.
     * @param pnk Chance to win of the horse.
     * @param raceNum Number of the race in which the calling Horse will participate.
     */


    public void proceedToStable(Integer raceNum, Integer horseID, Integer pnk){
        r1.lock();
        try{ 
            while(raceNum != raceNumber){
                newRace.await();
            }

            groi.setHorsesState("ATS",horseID);
            groi.setHorsesPnk(pnk, horseID); 
            groi.setHorseProbability(-1, horseID);
            groi.setHorseIteration(-1, horseID);
            groi.setHorseTrackPosition(-1, horseID);
            groi.setHorsesStanding('-', horseID); 

            while(!canHorsesMoveToPaddock)
                horsesToPaddock.await();

            if(++numHorses == numberOfHorses){
                numHorses = 0;
                canHorsesMoveToPaddock = false;
                if(raceNumber == numberOfRaces){
                    raceNumber = 0;
                }
            }
            horsesToPaddock.signal();

        }catch (IllegalMonitorStateException | InterruptedException e){
            e.printStackTrace();
        } finally {
            r1.unlock();
        }
    }

    public int getNumberOfHorses(){
        return this.numberOfHorses;
    }
}

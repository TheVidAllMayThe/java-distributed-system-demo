import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The {@link Paddock} class is a monitor that contains
 * necessary methods to be used in mutual exclusive access by {@link Horse}s and {@link Spectator}s.
 * <p>
 * This is where the {@link Horse}s are paraded for the {@link Spectator}s.
 *
 * @author  David Almeida, Manuel Xarez
 * @version 1.0
 * @since   2018-03-21
 * @see Main.HorseRace
 * @see Broker
 * @see Horse
 * @see Spectator
 */

public class Paddock{
    private final ReentrantLock r1;

    private final Condition horsesCond;
    private Boolean allowHorses;

    private final Condition spectatorsCond;
    private Boolean allowSpectators;
    private final HorseInPaddock horses[];
    private int horsesInPaddock;
    private int spectatorsInPaddock;
    private int numberOfSpectators;
    private int numberOfHorses;
    private GeneralRepositoryOfInformation groi;

    public Paddock(GeneralRepositoryOfInformation groi){
        this.numberOfSpectators = groi.getNumberOfSpectators();
        this.numberOfHorses = groi.getNumberOfHorses();
        r1 = new ReentrantLock(false);
        horsesCond = r1.newCondition();
        allowHorses = false;
        spectatorsCond = r1.newCondition();
        allowSpectators = false;
        horses = new HorseInPaddock[numberOfHorses];
        horsesInPaddock = 0;
        spectatorsInPaddock = 0;
        this.groi = groi;
    }

    //Horses methods

    /**
     * The {@link Horse}s enter the paddock and add their information to the {@link #horsesInPaddock} array, then they wait until all the {@link Spectator}s have reached the {@link Paddock}, at the end the last {@link Horse} awakes the {@link Spectator}s.
     *
     * @param horseID ID of the calling thread.
     * @param pnk Max step size.
     */

    public void proceedToPaddock(int horseID, int pnk){
        r1.lock();
        try {
            groi.setHorsesState("ATP", horseID);

            horses[horsesInPaddock++] = new HorseInPaddock(horseID, pnk);
            if (horsesInPaddock == numberOfHorses) {
                int total_pnk = 0;
                for (HorseInPaddock horse : horses) total_pnk += horse.getPnk();

                for (HorseInPaddock horse : horses){
                    if(numberOfHorses > 1){
                        horse.setOdds((double) horse.getPnk() / total_pnk / (1 - (horse.getPnk() / total_pnk)));
                    }
                    else horse.setOdds(1.0);
                    groi.setHorseProbability(horse.getOdds() * 100, horse.getHorseID());
                }
                allowSpectators = true;
                spectatorsCond.signal();
            }
        }catch(Exception ie){
            ie.printStackTrace();
        } finally{
            r1.unlock();
        }
    }

    /**
     * Called by the {@link Horse} to exit the {@link Paddock}.
     */

    public void proceedToStartLine(){
        r1.lock();
        try{
            
            while (!allowHorses) {
                horsesCond.await();
            }

            if(--horsesInPaddock==0){
                allowHorses = false;
                spectatorsCond.signal();
            }

            horsesCond.signal();
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    /**
     * Function in which the {@link Spectator} enters the {@link Paddock}. The last {@link Spectator} to enter wakes up the {@link Horse}s. In this function the {@link Spectator} determines in which {@link Horse} they will bet.
     * @return Returns the {@link Horse} in which the {@link Spectator} will bet.
     */
    public HorseInPaddock goCheckHorses(int spectatorID){
        HorseInPaddock result = null;
        r1.lock();

        try{
            groi.setSpectatorsState("ATH", spectatorID);
            while(!allowSpectators){
                spectatorsCond.await();
            }

            result = horses[ThreadLocalRandom.current().nextInt(horses.length)];
            groi.setSpectatorsSelection(result.getHorseID(), spectatorID);

            if(++spectatorsInPaddock == numberOfSpectators){
                allowHorses = true;
                allowSpectators = false;
                spectatorsInPaddock = 0;
                horsesCond.signal();
            }

            spectatorsCond.signal();
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }finally{
            r1.unlock();
        }
        return result;
    }
}
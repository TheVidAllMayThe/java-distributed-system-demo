package Monitors;

import Monitors.AuxiliaryClasses.Parameters;
import Monitors.AuxiliaryClasses.Bet;
import Threads.Broker;
import Threads.Horse;
import Threads.Spectator;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
* The {@link BettingCentre} class is a monitor that contains all the
* necessary methods to be used in mutual exclusive access by the {@link Broker} and {@link Spectator}.
* <p>
* This is where the {@link Bet}s are handled.
*
* @author  David Almeida, Manuel Xarez
* @version 1.0
* @since   2018-03-21
* @see Broker
* @see Spectator
*/

public class BettingCentre{
    
    private static final Bet[] bets  = new Bet[Parameters.getNumberOfSpectators()];
    private static final Lock r1 = new ReentrantLock();
    private static final Condition brokerCond = r1.newCondition();
    private static final Condition spectatorCond = r1.newCondition();
    private static boolean waitingOnBroker = false;
    private static boolean resolvedSpectator = false;
    private static int numWinners = 0;
    private static int currentNumberOfSpectators = 0;
    private static int numHorsesFirst = 0;


    /**
     * {@link Broker} accepts a {@link Bet}, wakes next {@link Spectator} in line and waits for the next one until all {@link Bet}s are taken.
     */

    public static void acceptTheBets() throws IllegalMonitorStateException{
        r1.lock();
        try {
            ((Broker)Thread.currentThread()).setState("WAITING_FOR_BETS");      
            GeneralRepositoryOfInformation.setBrokerState("WFBE"); 
            while(currentNumberOfSpectators != Parameters.getNumberOfSpectators()){
                resolvedSpectator = true;
                waitingOnBroker = false;
                spectatorCond.signal();

                while (!waitingOnBroker){
                    brokerCond.await();
                }

            }

            currentNumberOfSpectators = 0;
        }catch (IllegalMonitorStateException | InterruptedException e){
            e.printStackTrace();
        } finally {
            r1.unlock();
        }
    }
    
    /**
     * {@link Broker} verifies if any {@link Spectator} won a {@link Bet}.
     *
     * @param   winnerList  Integer array containing the ID of all the {@link Horse}s who won.
     * @return  boolean     Returns true if any {@link Spectator} won a {@link Bet}.
     */

    static public boolean areThereAnyWinners(int[] winnerList) {
        boolean returnValue = false;

        r1.lock();
        try {
            numHorsesFirst = winnerList.length; 
            for (Bet bet: bets){
                for (int winner : winnerList){
                    if (bet.getHorseID() == winner) {
                        returnValue = true;
                        numWinners++;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            r1.unlock();
        }

        return returnValue;
    }

    /**
     * {@link Broker} honors a {@link Bet}, wakes next {@link Spectator} in line and waits for the next {@link Spectator} claiming a reward until all rewards are given.
     */
    public static void honorBets(){
        r1.lock();
        try{
            ((Broker)Thread.currentThread()).setState("SETTLING_ACCOUNTS");
            while(currentNumberOfSpectators != numWinners){
                waitingOnBroker = false;
                resolvedSpectator = true;
                spectatorCond.signal();
                while(!waitingOnBroker){
                    brokerCond.await();
                }
                
            }
            currentNumberOfSpectators = 0;
            numWinners = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            r1.unlock();
        }
    }


    /**
     * {@link Spectator} waits in line, places a {@link Bet} and then wakes the {@link Broker}.
     *
     * @param pid ID of the thread calling the method.
     * @param value Amount to bet.
     * @param horseID ID of the {@link Horse} in which to bet.
     * @param odds Odds of the {@link Horse} in which to bet.
     */

    public static void placeABet(int pid, double value, int horseID, double odds){
        r1.lock();
        try{  
            Spectator sInst = (Spectator)Thread.currentThread();
            sInst.setState("PLACING_A_BET");
            GeneralRepositoryOfInformation.setSpectatorsState("PAB", sInst.getID());
            while(!resolvedSpectator){
                spectatorCond.await();
            }

            waitingOnBroker = true;
            sInst.setBudget(sInst.getBudget() - value);
            GeneralRepositoryOfInformation.setSpectatorsBudget(sInst.getBudget(), sInst.getID());
            bets[currentNumberOfSpectators++] = new Bet(pid, value, horseID, odds);
            GeneralRepositoryOfInformation.setSpectatorsBet(value, sInst.getID());
            resolvedSpectator = false;
            brokerCond.signal();

        }catch(InterruptedException ie){
            ie.printStackTrace();
        }finally{
            r1.unlock();
        }
    }

    /**
     * Called by a {@link Spectator} to collect the gain of after having won a {@link Bet}.
     *
     * @param spectatorID ID of the thread calling the method.
     */
    public static void goCollectTheGains(int spectatorID){
        double result = 0;
        r1.lock();
        try{
            Spectator sInst = (Spectator)Thread.currentThread();
            sInst.setState("COLLECTING_THE_GAINS");
            GeneralRepositoryOfInformation.setSpectatorsState("CTG", spectatorID);
            while(!resolvedSpectator){
                spectatorCond.await();
            }

            currentNumberOfSpectators++;
            waitingOnBroker = true;
            resolvedSpectator = false;
            brokerCond.signal();
            
            Bet bet;
            for (Bet bet1 : bets) {
                if (bet1.getSpectatorID() == spectatorID) {
                    bet = bet1;
                    result = bet.getBetAmount() * bet.getOdds() / numHorsesFirst;
                    break;
                }

            }
            sInst.setBudget(sInst.getBudget() + result);
            GeneralRepositoryOfInformation.setSpectatorsBudget(sInst.getBudget(), spectatorID);
        
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }finally{
            r1.unlock();
        }
    }
}

package Monitors.AuxiliaryClasses;

public class Parameters {
    private static int numberOfRaces, numberOfHorses, numberOfSpectators, raceLength;
    private static boolean set = false;

    public static void initialize(int numberOfRaces, int numberOfHorses, int numberOfSpectators, int raceLength){
        if(set) return;
        Parameters.numberOfHorses = numberOfHorses;
        Parameters.numberOfRaces = numberOfRaces;
        Parameters.numberOfSpectators = numberOfSpectators;
        Parameters.raceLength = raceLength;
        set = true;
    }

    public static int getNumberOfRaces() {
        return numberOfRaces;
    }

    public static int getNumberOfHorses() {
        return numberOfHorses;
    }

    public static int getNumberOfSpectators() {
        return numberOfSpectators;
    }
}

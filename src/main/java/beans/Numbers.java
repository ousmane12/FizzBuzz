package beans;

import java.util.ArrayList;

public class Numbers {
    /**
     * Classe gerant les nombres. Il prend en parametre 2 entiers
     * representant le debut de l'intervalle et la fin de l'intervalle
     */
    private int start;
    private int last;

    /***
     *
     * @param start
     * @param last
     * @return list of strings including the Fizz and Buzz strings
     */
    public ArrayList<String> play(int start, int last){
        //Initialize a list that holds all values according to whether it is a simple value,
        //Fizz value, FizzBuzz value or Buzz value
        ArrayList<String> liste = new ArrayList<>();
            for (int i = start; i <= last; i++) {
                if (i % 3 == 0 && i % 5 == 0) {
                    liste.add("FizzBuzz");
                } else if (i % 3 == 0) {
                    liste.add("Fizz");
                } else if (i % 5 == 0) {
                    liste.add("Buzz");
                } else {
                    liste.add(Integer.toString(i));
                }
            }
        return liste;
    }
    public ArrayList<String> playStage2(int start, int last){
        //Initialize a list that holds all values according to whether it is a simple value,
        //Fizz value, FizzBuzz value or Buzz value
        ArrayList<String> liste = new ArrayList<>();
        for (int i = start; i <= last; i++) {
            if (i % 3 == 0 || Integer.toString(i).startsWith("3") || Integer.toString(i).endsWith("3")) {
                liste.add("Fizz");
            } else if (i % 5 == 0 || Integer.toString(i).startsWith("5") || Integer.toString(i).endsWith("5")) {
                liste.add("Buzz");
            } else {
                liste.add(Integer.toString(i));
            }
        }
        return liste;
    }
}

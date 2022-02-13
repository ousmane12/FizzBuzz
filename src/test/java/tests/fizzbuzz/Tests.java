package tests.fizzbuzz;

import beans.Numbers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class Tests {

    @Test
    @DisplayName("Play FizzBuzz")
    void playFizzBuzz(){
        int start = 1;
        int last = 100;
        //Arrange
        Numbers numbers = new Numbers();
        //Action on methods
        //Method 1
        ArrayList<String> myGame = numbers.play(start,last);
        //Method 2
        ArrayList<String> myGame2 = numbers.playStage2(start,last);

        //Assert
        Assertions.assertEquals(1, start);
        Assertions.assertEquals(100, last);
        /**Note that values can be checked according to user, he has to provide
        *Positional argument and expected value for this example it is 'Fizz' that
         * we are expecting at the position 2
         */
        Assertions.assertEquals("Fizz", myGame.get(2));

        System.out.println("***************************");
        System.out.println("Result game: "+myGame);
        System.out.println("***************************");

        System.out.println("***************************");
        System.out.println("Result game stage 2: "+myGame2);
        System.out.println("***************************");
    }
}

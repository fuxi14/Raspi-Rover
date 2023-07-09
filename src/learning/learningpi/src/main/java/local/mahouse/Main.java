/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package local.mahouse;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.platform.Platforms;
import com.pi4j.util.Console;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author luca
 */
public class Main {

    private static final int PIN_LED = 22; // PIN 15 = BCM 22
    private static final Console console = new Console();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        console.title("Test program", "For learning Pi4J");
        Context pi4j = null;
        /*float tim;
        //Comprovem si s'ha donat algun argument
        if(args.length == 0 || "0".equals(args[0])){
            console.println("Nothing or 0 introduced, defaulting to x1");
            tim = 1;
        }else{
            console.println("Value introduced, timer augmented by a factor of " + args[0]);
            tim = Float.parseFloat(args[0]);
        }*/
        
        try {
            pi4j = Pi4J.newAutoContext();
            new Main().run(pi4j); //We don't need tim now
        } catch (InvocationTargetException e) {
            console.println("Error: " + e.getTargetException().getMessage());
        } catch (Exception e) {
            console.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (pi4j != null) {
                pi4j.shutdown();
            }
        }
    }
    //Afegim un argument per controlar la duració del parpalleig
    private void run(Context pi4j) throws Exception { //float timing is no longer needed
        Platforms platforms = pi4j.platforms();

        console.box("Pi4J PLATFORMS");
        console.println();
        platforms.describe().print(System.out);
        console.println();

        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                        .id("led")
                        .name("LED Flasher")
                        .address(PIN_LED)
                        .shutdown(DigitalState.LOW)
                        .initial(DigitalState.LOW)
                        .provider("pigpio-digital-output");

        var led = pi4j.create(ledConfig);
        //int counter = 0;
        
        
        Input reader = new Input("Reader");
        reader.start();

        boolean working = true;
        //while(counter <= 50 {
        while (working) {
            
            /*if (led.equals(DigitalState.HIGH)) {
                led.low();
                System.out.println("low");
            } else {
                led.high();
                System.out.println("high");
            }*/
            //Thread.sleep((long) (500.0 * timing)); //Modifquem freqüencia
            //counter++;
            while(Resource.word.equals("")) {}
            switch(Resource.word.toLowerCase()) {
                case "down":
                    led.low();
                    console.println("Led OFF");
                    break;
                case "up":
                    led.high();
                    console.println("Led ON");
                    break;
                case "quit":
                    led.low();
                    console.println("Exiting program now");
                    working = false;
                    break;
                default:
                    console.println("Not reconized, don't do anything");
                    break;
            }
            Resource.word = "";
        }
    }

}

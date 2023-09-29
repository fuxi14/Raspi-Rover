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
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.platform.Platforms;
import com.pi4j.util.Console;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import server.DiscoveryServer.DiscoveryServer;
/**
 *
 * @author luca (base), Marcel
 */
public class Main {

    //Motors de l'esquerre
    private static final int m1 = 20; // PIN 38 = BCM 20
    private static final int m2 = 21; // PIN 40 = BCM 21
    private static final int PWMA = 26; // PIN 37 = BCM 26
    //Motors de la dreta
    private static final int m3 = 6; // PIN 31 = BCM 6
    private static final int m4 = 13; // PIN 33 = BCM 13
    private static final int PWMB = 12; // PIN 32 = BCM 12
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    Executor exe = new Executor() {
        @Override
        public void execute(Runnable runnable) {
            executorService.execute(runnable);
        }
    };
    
    
    private static final Console console = new Console();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        console.title("Raspberry Pi Rover", "By fuxi14");
        console.box("Made with Pi4J");
        Context pi4j = null;
        
        //Per fer execució multifil
        
        
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
    
    /*
     * El codi per controlar la placa controladora ha sigut adaptat del codi
     * d'exemple que ofereix l'empresa que l'ha creat.
     *
     * https://www.waveshare.com/w/upload/9/96/RPi-Motor-Driver-Board-Demo-Code.tar.gz
     */
    
    //Afegim un argument per controlar la duració del parpalleig
    private void run(Context pi4j) throws Exception { //float timing is no longer needed
        Platforms platforms = pi4j.platforms();

        console.box("Pi4J PLATFORMS");
        console.println();
        platforms.describe().print(System.out);
        console.println();
        
        //Configuració pins
        var m1Config = DigitalOutput.newConfigBuilder(pi4j)
                        .id("m1")
                        .name("Motor 1 state 1")
                        .address(m1)
                        .shutdown(DigitalState.LOW)
                        .initial(DigitalState.LOW)
                        .provider("pigpio-digital-output");
        var m2Config = DigitalOutput.newConfigBuilder(pi4j)
                        .id("m2")
                        .name("Motor 1 state 2")
                        .address(m2)
                        .shutdown(DigitalState.LOW)
                        .initial(DigitalState.LOW)
                        .provider("pigpio-digital-output");
        var m3Config = DigitalOutput.newConfigBuilder(pi4j)
                        .id("m3")
                        .name("Motor 2 state 1")
                        .address(m3)
                        .shutdown(DigitalState.LOW)
                        .initial(DigitalState.LOW)
                        .provider("pigpio-digital-output");
        var m4Config = DigitalOutput.newConfigBuilder(pi4j)
                        .id("m4")
                        .name("Motor 2 state 2")
                        .address(m4)
                        .shutdown(DigitalState.LOW)
                        .initial(DigitalState.LOW)
                        .provider("pigpio-digital-output");
        
        //Els pins amb "Pulse Width Modulation tenen una configuració diferent
        var PWMAConfig = Pwm.newConfigBuilder(pi4j)
                        .id("PWMA")
                        .name("Speed Motor 1")
                        .address(PWMA)
                        .pwmType(PwmType.SOFTWARE)
                        .shutdown(0)
                        .initial(0)
                        .provider("pigpio-pwm");
        var PWMBConfig = Pwm.newConfigBuilder(pi4j)
                        .id("PWMB")
                        .name("Speed Motor 2")
                        .address(PWMB)
                        .pwmType(PwmType.HARDWARE)
                        .shutdown(0)
                        .initial(0)
                        .provider("pigpio-pwm");
        

        //Creem els objectes dels pins
        var them1 = pi4j.create(m1Config);
        var them2 = pi4j.create(m2Config);
        var them3 = pi4j.create(m3Config);
        var them4 = pi4j.create(m4Config);
        var thePWMA = pi4j.create(PWMAConfig);
        var thePWMB = pi4j.create(PWMBConfig);
        
        //La freqüència per defecte és de 500Hz 
        thePWMA.on(Resource.speedLeft, 500);
        thePWMB.on(Resource.speedRight, 500);
        
        //LA duració del cicle per defecte és de 100%
        
        //Funcions
        class Movement {
            void reverse() {
                them1.low();
                them2.high();
                them3.low();
                them4.high();
                
                this.speed(Resource.speedLeft, Resource.speedRight);
                console.println("Reverse", this);
                console.println("Speed is (left | right): " + String.valueOf(Resource.speedLeft)
                        + "% | " + String.valueOf(Resource.speedRight) + "%");
            }
            void forward() {
                them1.high();
                them2.low();
                them3.high();
                them4.low();
                this.speed(Resource.speedLeft, Resource.speedRight);
                console.println("Forward", this);
                console.println("Speed is (left | right): " + String.valueOf(Resource.speedLeft) 
                        + "% | " + String.valueOf(Resource.speedRight) + "%");
            }
            void left() {
                them1.high();
                them2.low();
                them3.low();
                them4.high();
                this.speed(Resource.speedLeft, Resource.speedRight);
                console.println("Left", this);
                console.println("Speed is (left | right): " + String.valueOf(Resource.speedLeft) 
                        + "% | " + String.valueOf(Resource.speedRight) + "%");
            }
            void right() {
                them1.low();
                them2.high();
                them3.high();
                them4.low();
                this.speed(Resource.speedLeft, Resource.speedRight);
                console.println("Right", this);
                console.println("Speed is (left | right): " + String.valueOf(Resource.speedLeft) 
                        + "% | " + String.valueOf(Resource.speedRight) + "%");
            }
            void stop() {
                them1.low();
                them2.low();
                them3.low();
                them4.low();
                this.speed(0, 0);
                console.println("Stop", this);
            }
            void speed(Number speedLeft, Number speedRight) {
              thePWMA.on(speedLeft, 500);
              thePWMB.on(speedRight, 500);
            }
        }
        
        
        
        Movement move = new Movement();
        
        DiscoveryServer discover = new DiscoveryServer();
        console.println("Opening server...");
        //Obrim server
        Server server = new Server();
        exe.execute(server.getRunnable());
        executorService.submit(discover);
            
        /*
        //DEPRECATED    
        Input reader = new Input("Reader");
        reader.start();*/

        int imSpeed;
        boolean working = true;
        while (working) {
            
            
            while(Resource.word.equals("")) {} //Esperem a que s'introdueixi alguna cosa
            switch(Resource.word.toLowerCase()) {
                case "reverse":
                    move.reverse();
                    break;
                case "forward":
                    move.forward();
                    break;
                case "left":
                    move.left();
                    break;
                case "right":
                    move.right();
                    break;
                case "stop":
                    move.stop();
                    break;
                case "speed":
                    console.println("SPEED CASE IS DEPRECADED", this);
                    break;
                case "quit":
                    move.stop();
                    console.println("Exiting program now", this);
                    working = false;
                    break;
                default:
                    console.println("Not reconized, don't do anything", this);
                    break;
            }
            Resource.word = "";
            Resource.canExit = true;
            
        }
        //Since DiscoveryServer doesn't want to shutdown on it's own, we obliterate it
        discover.shutdown();
        executorService.shutdownNow();
    }
    
    
    
                
}

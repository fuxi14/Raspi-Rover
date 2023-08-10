/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.mahouse;

/**
 *
 * @author Marcel
 */

//Aquesta classe conté recursos compartits entre diferents fils
public class Resource {
    
    public static volatile String word = "";
    public static volatile boolean canExit = true;
    public static volatile int speedLeft = 100;
    public static volatile int speedRight = 100;
}

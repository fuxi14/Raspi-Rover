/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package local.mahouse.multithreadtest;

/**
 *
 * @author nuria
 */
public class MultithreadTest {

    public static void main(String[] args) {
        //Let's rock!
        
        //Iniciem la classe amb la paraula de buffer
        Resource buffer = new Resource();
        //Iniciem lector
        Reader reader = new Reader("Reader");
        reader.start();
        
        boolean work = true;
        
        while(work) {
            while(Resource.word.equals("")) {}
            if(Resource.word.equalsIgnoreCase("quit")) {
                    System.out.println("Surt del programa");
                    work = false;
            }else{        
                System.out.println("S'ha introduiut la paraula: " + Resource.word);
                Resource.word = "";
                    
            }
        }
        
    }
}

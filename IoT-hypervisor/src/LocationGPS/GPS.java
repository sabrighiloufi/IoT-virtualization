
package LocationGPS;

import clientMQTT.GuiVS;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

        
public class GPS { 
   
     private String name;
     private double longitude;
     private double latitude;   

    public GPS() {
    }
    // create and initialize a point with given name and
    // (latitude, longitude) specified in degrees
    public GPS(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude  = latitude;
        this.longitude = longitude;
    }

    
    public String toString() {
        return name + " (" + latitude + ", " + longitude + ")";
    }

  public void aff() {
        System.out.println(name + " (" + latitude + ", " + longitude + ")");
    }
    
    
    // test client
   
    public void changeLocation() throws ClassNotFoundException, SQLException, InterruptedException{
        Location l1 = null,l2;
         ProcessBuilder processBuilder = new ProcessBuilder("./gps.sh").directory(new File("/home/contiki/Desktop"));

        try {

            Process process = processBuilder.start();

			// blocked :(
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                String s[]=line.split(",");
                latitude  = Double.parseDouble(s[0]);
                longitude = Double.parseDouble(s[1]);
                name= "("+s[2]+","+s[3]+")";
                l1= new  Location(name, latitude, longitude);
              //loc1 = new GPS(name, latitude, longitude);
             // System.out.println("last location:"+line);
               
            }


        } catch (IOException e) {
            e.printStackTrace();
        
    }
        
        while(true){
         processBuilder = new ProcessBuilder("./gps.sh").directory(new File("/home/contiki/Desktop"));
        Thread.sleep(10000);
        try {

            Process process = processBuilder.start();

			// blocked :(
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line2;
            while ((line2 = reader.readLine()) != null) {
                //System.out.println(line);
                String s2[]=line2.split(",");
                latitude  = Double.parseDouble(s2[0]);
                longitude = Double.parseDouble(s2[1]);
                name= s2[2]+","+s2[3];
                Location l= new  Location(name, latitude, longitude);
              // GPS loc2 = new GPS(name, latitude, longitude);
               double distance = l1.distanceTo(l);
               //System.out.println("current location:"+line2);
               System.out.printf("%6.3f miles\n from",distance);
               System.out.println(l1.toString() + " to " + l.toString()+"\n");
               //if(distance>0.5){
                   //migration to plus proche node parametre(pos of app)
                   GuiVS.migrationMobility(l);
              // }
            }


        } catch (IOException e) {
            e.printStackTrace();
        
    }
        
        }
    }
}
package clientMQTT;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecuteBroker {
     public static void main(String args[])throws IOException, InterruptedException {

     ProcessBuilder processBuilder = new ProcessBuilder("sudo","./broker_mqtts","config.mqtt").directory(new File("/home/contiki/contiki/mqtt-sn-contiki/tools/mosquitto.rsmb/rsmb/src"));

        try {

            Process process = processBuilder.start();

			// blocked :(
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }


        } catch (IOException e) {
            e.printStackTrace();
        
    }       
     
     
     }
    
}

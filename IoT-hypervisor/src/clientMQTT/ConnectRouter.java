/*pour connecter au routeur du WSN et connaitre les capteurs physiques deployés*/
package clientMQTT;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConnectRouter {

     public static void main(String args[]) throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder("sudo","make","connect-router-cooja").directory(new File("/home/contiki/contiki/examples/ipv6/rpl-border-router"));

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

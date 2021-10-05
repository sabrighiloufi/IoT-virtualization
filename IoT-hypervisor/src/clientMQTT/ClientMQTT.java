/*ce code represente MQTT subscriber
 * MQTT Publisher: sont les VSs
 */

package clientMQTT;
import DB_API_Client.Client_DB;
import java.util.Arrays;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
        import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
        import org.eclipse.paho.client.mqttv3.MqttException;
        import org.eclipse.paho.client.mqttv3.MqttMessage;
        import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

        public class ClientMQTT {

        public static void main(String[] args) {

            String topic        = "MQTT Examples";
            String content      = "Message from MqttPublishSample";
            int qos             = 2;
            String broker       = "tcp://localhost:1883";
            String clientId     = "JavaSample";
            MemoryPersistence persistence = new MemoryPersistence();

            try {
                MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                sampleClient.setCallback(new MqttCallback() {

    @Override
    public void connectionLost(Throwable cause) { //Called when the client lost the connection to the broker 
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Client_DB c=new Client_DB();
        //c.addData("fff", 10);
         String[] result = new String(message.getPayload()).split(":");
        //System.out.println(result[1].substring(2));
         int vsid= Integer.parseInt(result[1].substring(2));
          System.out.println(vsid+"**"+result[2]);
        c.addData(result[2],vsid);
       // System.out.println(String.format("[%s] %s\n", topic, new String(message.getPayload())));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {//Called when a outgoing publish is complete 
    }
});
                System.out.println("Connecting to broker: "+broker);
                sampleClient.connect(connOpts);
                sampleClient.subscribe("#", 1);
                
            } catch(MqttException me) {
                System.out.println("reason "+me.getReasonCode());
                System.out.println("msg "+me.getMessage());
                System.out.println("loc "+me.getLocalizedMessage());
                System.out.println("cause "+me.getCause());
                System.out.println("excep "+me);
                me.printStackTrace();
            }
        }
    }
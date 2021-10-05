
package LocationGPS;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import DB_API_Server.Interrogation;
import LocationGPS.Location;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import java.io.*;
import java.lang.String;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Mobility {
    
   static private String name;
    static private double longitude;
    static private double latitude;
    static String texts;
    static String urlGateway = "http://[aaaa::c30c:0:0:1]/";
    static int nbNodes=0;
   static int nbServices=0;
   static String[] urlNodes=new String[50];
  
    public String toString() {
        return name + " (" + latitude + ", " + longitude + ")";
    }

  public void aff() {
        System.out.println(name + " (" + latitude + ", " + longitude + ")");
    }
    
    
    // test client
   
    public static void main(String args[]) throws ClassNotFoundException, SQLException, InterruptedException{
        Location l1 = null,l2;
        urlNodes=getAllNodes(urlGateway);
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
                  migrationMobility(l);
              //}
            }


        } catch (IOException e) {
            e.printStackTrace();
        
    }
        
        }
    }
    
    
    static class CustomerSortingComparator implements Comparator<Location> { 
  
        @Override
        public int compare(Location l1, Location l2) { 
  
           
  
            
            if (l1.getDistanceToApp()> l2.getDistanceToApp()) { 
                return 1; 
            } else { 
                return -1; 
            } 
        } 
    } 
    
    public static void migrationMobility(Location l) throws ClassNotFoundException, SQLException{
            //migration to plus proche
             Interrogation in=new Interrogation();
             List<Location> myNodes=in.getGlobalContextInf4ALL();
             TreeSet<Location> myNodesSorted=new TreeSet<Location>(new CustomerSortingComparator());
             Location plusProche,curr;
             //double minDistance = 0 ;
             if (myNodes.isEmpty()) {
               plusProche =null;
             } else {
                   final ListIterator<Location> itr = myNodes.listIterator();
                   
                    while (itr.hasNext()) {
                         curr = itr.next();
                         curr.setDistanceToApp(curr.distanceTo(l));
                        myNodesSorted.add(curr);
                    }
                    
            }
          
     
        TreeSet  tsetreverse = new TreeSet();
        
        tsetreverse=(TreeSet)myNodesSorted.descendingSet();
        Iterator iterate,it2; 
        iterate = tsetreverse.iterator();
        it2=myNodesSorted.iterator();
        Location plusProcheNode=(Location) myNodesSorted.first();
        plusProcheNode.setVisited(true);
        String idPPN=plusProcheNode.getName().split(" ")[1];
            String urlNP=chercherUrlNodes(plusProcheNode.getName().split(" ")[1]);
        
        int i=getNumVs(urlNP);//number of vs in the node
        //System.out.println("nombre de vs existant="+i);
        // Iterating through the headSet 
        while (iterate.hasNext()) { 
            //migration to plusProcheNode
            Location current=(Location) iterate.next();
            String id=current.getName().split(" ")[1];
            
            if((!id.equals(plusProcheNode.getName().split(" ")[1]))&&(current.isVisited()==false)){ 
                String urlNode=chercherUrlNodes(id);
                String VSs[]=getAllVs(urlNode);
                if(nbServices>0){
                for(int j=0;j<nbServices;j++){
                    String urlNodeProche=chercherUrlNodes(plusProcheNode.getName().split(" ")[1]);
                    //get details vs
                    String detailsVS=getDetailsVS(urlNode,Integer.parseInt(VSs[j]) );
                    // add new vs in plusProche node
                    String addVS=addVS(urlNodeProche, detailsVS);
                    //delete vs from current
                    String del=DeleteVS(urlNode,Integer.parseInt(VSs[j]));
                    i++;
                    if((i % 43 ==0)&&(it2.hasNext())){
                      plusProcheNode=(Location) it2.next();
                      plusProcheNode.setVisited(true);
                      i=getNumVs(chercherUrlNodes(plusProcheNode.getName().split(" ")[1]));
                    }
                   System.out.println(detailsVS); 
                }     
                }
            }
            
            
            
            
            
        } 
         

         }//end migrationMobility
    
    static public String chercherUrlNodes(String id){
    
    for(int i=0;i<nbNodes;i++){
                  if(id.equals( urlNodes[i].substring(urlNodes[i].lastIndexOf(':') + 1)))  
                   return urlNodes[i];
                  }
                   return "";
    }
    
    	static public String HttpClientGet(String myurl){
        String adresse = myurl;
		String toreturn = null;
		  try { 
//		creation d'un objet URL 
		    URL url = new URL(adresse); 
//		on etablie une connection a cette url 
		   URLConnection uc = url.openConnection(); 
//		on y cree un flux de lecture 
		   InputStream in = uc.getInputStream(); 
//		on lit le premier bit 
		   int c = in.read(); 
//		on cree un StringBuilder pour par la suite y ajouter tout les bit lus 
		   StringBuilder build = new StringBuilder(); 
//		tant que c n'est pas egale au bit indiquant la fin d'un flux... 
		   while (c != -1) { 
		    build.append((char) c); 
//		...on l'ajoute dasn le StringBuilder... 
		    c = in.read(); 
//		...on lit le suivant 
		   } 
//		on retourne le code de la page 
		   toreturn = build.toString(); 
		   
		  } catch (MalformedURLException e) { 
		  
		   e.printStackTrace(); 
		  } catch (IOException e) { 
		  
		   e.printStackTrace(); 
		  }
		   return toreturn;
        }
        
      static  public String[] getAllNodes(String urlGateway) {
            String texts=HttpClientGet(urlGateway);
            String [] my_urlNodes=new String[50];
            nbNodes=0;
         if(texts!=null)
          { 
              
        Document document = Jsoup.parse(texts);
        Element link= document.select("pre").first(); 
        Element link1 = document.select("pre").last(); 
        System.out.println("Neighbors :\n");
        System.out.println(link.text());
        System.out.println("Routes :\n");
        System.out.println( link1.text());
        System.out.println("Splitting to lines");
                     System.out.println("URLs");
                        String[] lines = link1.text().split("\\r?\\n");
                        
                             for (String l : lines) {                              
                                    //System.out.println(l);
                                    nbNodes++;
                                    String my_url = l.split("/")[0];
                                    System.out.println("URL"+nbNodes+": "+my_url);
                                    my_urlNodes[nbNodes-1]=my_url;
                                    
                            }
              }

            
            return my_urlNodes;    
            }
    
      static  public String[] getAllVs(String urlNode) {
            texts=HttpClientGet("http://["+urlNode+"]:8080/getListVS");
            String [] listOfvss=new String[50];
         if(texts!=null && texts.length()>0)
          {                     
            System.out.println("Splitting to lines");
                     System.out.println("VSs");
                     System.out.println(texts);
                        String[] lines = texts.split("[+]");
                            nbServices =0;
                            for(String l : lines){   
                                if(l.length()>2){
                                    System.out.println("---->"+l);            
                                    nbServices++;
                                    listOfvss[nbServices-1]=l;  }                                                                 /*String my_service = l.substring(l.indexOf(">")+1,l.indexOf("</"));
                                    String my_ressource = l.substring(l.indexOf("<")+1,l.indexOf(">"));
                                    System.out.println(my_service+" : "+my_ressource);
                                    listOfServices[nbServices-1]=my_service;
                                    ressourcesServices[nbServices-1]=my_ressource;*/
                                  
                            }
              }

            
            return listOfvss;    
            }
      
      //number of vs
      static  public int getNumVs(String urlNode) {
            texts=HttpClientGet("http://["+urlNode+"]:8080/getListVS");
            //String [] listOfvss=new String[50];
        int nbVS =0;
            if(texts!=null && texts.length()>0)
          {                     
            
                        String[] lines = texts.split("[+]");
                           
                            for(String l : lines){   
                                if(l.length()>2){
                                            
                                    nbVS++;
                                 
                                }    
                            }
              }

            
            return nbVS;    
            }
      
      
      
        // return get_service as a string
        public String getService(String urlService,String ressourceService)
        {
            String sr=HttpClientGet("http://["+urlService+"]:8080/"+ressourceService);
            
            return sr;
        }
        
        // delete vs
         static public String DeleteVS(String urlNode,int vsid)
        {
           String sr=HttpClientGet("http://["+urlNode+"]:8080/deleteVS?id="+(vsid));
            
            return sr;
        }
    
        static public String getDetailsVS(String urlNode,int vsid)
        {
           String sr=HttpClientGet("http://["+urlNode+"]:8080/getDetailsVS?id="+(vsid));
            
            return sr;
        }
        static public String addVS(String urlNode, String param)
        {
           String sr=HttpClientGet("http://["+urlNode+"]:8080/addvs?"+(param));
            
            return sr;
        }
         
}

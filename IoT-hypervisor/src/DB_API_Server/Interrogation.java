/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DB_API_Server;
import LocationGPS.Location;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;



public class Interrogation {
    ConneXion cnx=null;
  
     Statement stmt;
     
     
    public Interrogation() throws ClassNotFoundException, SQLException{
         cnx=new ConneXion();
    }
    
    public boolean  newSensor(int id, double latitude, double longitude) throws ClassNotFoundException, SQLException{
       
       
       stmt = cnx.getCnx().createStatement();
        String req="insert into sensorDescription(id,latitude,longitude) values  ("+id+","+latitude+","+longitude+")"; 
       /*appel preference*/
  
 int n= stmt.executeUpdate(req);
   if(n==1) {
       return true;
   }   
   else{return false;}  
   
    }
   
    
   
    public Location  getGlobalContextInf(int id) throws ClassNotFoundException, SQLException{
       
      
       stmt = cnx.getCnx().createStatement();
       ResultSet rs = stmt.executeQuery("select latitude,longitude from sensorDescription where id="+id); 
       /*appel preference*/
       if(rs.next()){
       Location l=new Location("node "+id, rs.getDouble("latitude"), rs.getDouble("longitude"));
       return l;}
       else 
           return null;
    }     
      
     public boolean  existID(int id) throws ClassNotFoundException, SQLException{
       
      
       stmt = cnx.getCnx().createStatement();
       ResultSet rs = stmt.executeQuery("select * from sensorDescription where id="+id); 
       /*appel preference*/
       if(rs.next()){
         return true;}
       else 
           return false;
      
    }  
     public List  getGlobalContextInf4ALL() throws ClassNotFoundException, SQLException{
             List<Location> list = new ArrayList<Location>();

      
       stmt = cnx.getCnx().createStatement();
       ResultSet rs = stmt.executeQuery("select * from sensorDescription"); 
       /*appel preference*/
      while ( rs.next() ) {
          Location l=new Location("node "+rs.getInt("id"), rs.getDouble("latitude"), rs.getDouble("longitude"));
               list.add(l);
            }
       
       return list;
       
    } 
    
}


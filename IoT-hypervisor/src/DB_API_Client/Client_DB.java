/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DB_API_Client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;



public class Client_DB {
    Connexion cnx=null;
  
     Statement stmt;
     
     
    public Client_DB() throws ClassNotFoundException, SQLException{
         cnx=new Connexion();
    }
    
    public boolean  addData(String Value, int vsid) throws ClassNotFoundException, SQLException{
       
       
       stmt = cnx.getCnx().createStatement();
        String req="insert into Data(VSID,Value,Timer) values  ("+vsid+",'"+Value+"',NOW())"; 
       /*appel preference*/
  
 int n= stmt.executeUpdate(req);
   if(n==1) {
       return true;
   }   
   else{return false;}  
   
    }
   
    
   

}

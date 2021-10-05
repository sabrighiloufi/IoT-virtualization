/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DB_API_Server;


import java.sql.*;
public class ConneXion {
private Connection conn = null;

public  ConneXion() throws ClassNotFoundException, SQLException{

    
     try {
 
  Class.forName("com.mysql.jdbc.Driver");
  conn=  (Connection)    DriverManager.getConnection("jdbc:mysql://localhost:3306/repositry","root","root");
 //System.out.println("connected ") ;
      }
catch (SQLException ex) {
System.out.println("Probleme de connexion : "+ex) ;
}
     
}

public Connection getCnx() {
return conn;}



}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DB_API_Client;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


import java.sql.*;
public class Connexion {
private Connection conn = null;

public  Connexion() throws ClassNotFoundException, SQLException{

    
     try {
 
  Class.forName("com.mysql.jdbc.Driver");
  conn=  (Connection)    DriverManager.getConnection("jdbc:mysql://localhost:3306/vWSN_BD","root","root");
 //System.out.println("connected ") ;
      }
catch (SQLException ex) {
System.out.println("Probleme de connexion : "+ex) ;
}
     
}

public Connection getCnx() {
return conn;}



}

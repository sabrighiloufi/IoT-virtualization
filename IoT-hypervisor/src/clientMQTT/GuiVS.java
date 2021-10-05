/*IoT-hypervisor: une application java(avec un GUI) qui 
 * permet de creer, supprimer, migrer un capteur virtuel d'un capteur physique vers un autre
 * il permet également de consulter la liste des VSs crées+ ses informations(frequence, id, capacité d'observation).    
 */
package clientMQTT;

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
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class GuiVS extends JFrame {
	
   String urlGateway = "http://[aaaa::c30c:0:0:1]/";
    int nbNodes=0;
   static int nbServices=0;
    String[] urlNodes=new String[50];
    String[] ressourcesServices=new String[50];
    final JButton btnRunVsP;
    static JProgressBar progressb;
    static JFrame f; 
    Interrogation in;  
    double [] latitudes={43.578225,43.578325,43.578450,43.578550,43.579450,43.577450,43.588225,43.588455,43.598225};
    double [] longitudes={1.462912,1.462950,1.462920,1.462812,1.462912,1.463012,1.462842,1.462772,1.462892};
    /**
	 * Launch the application.
	 */
    
    public static void fill() 
    { 
        int i = 0; 
        try { 
            while (i <= 100) { 
                // fill the menu bar 
                progressb.setValue(i + 1); 
  
                // delay the thread 
                Thread.sleep(40); 
                i += 1; 
            } 
        } 
        catch (Exception e) { 
        } 
    } 
    
	public static void main(String[] args) {
             f = new JFrame("starting"); 
  
        // create a panel 
        JPanel p = new JPanel(); 
  
        // create a progressbar 
        progressb = new JProgressBar(); 
  
        // set initial value 
        progressb.setValue(0); 
  
        progressb.setStringPainted(true); 
  
        // add progressbar 
        p.add(progressb); 
  
        // add panel 
        f.add(p); 
  
        // set the size of the frame 
        f.pack(); 
        f.setBounds(500, 180,150, 70);
        f.setVisible(true); 
  
        fill(); 
        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
            if(progressb.getValue()==100)
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiVS frame = new GuiVS();
					frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
	
	/**
	 * Create the frame.(in constructor)
	 */
	
	public GuiVS()
	{
		setTitle("VWSN");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 80,400, 300);
                setMinimumSize(new Dimension(400,550));
		BorderLayout bl=new BorderLayout(5,5);
		JPanel pGlobal=new JPanel (bl);
		
		//pGlobal.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Nodes", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(1, 120, 190)));
		setContentPane(pGlobal);
		
		//*************************************************************** 
		
		JPanel panelNodes = new JPanel();
		panelNodes.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Nodes", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(1, 120, 190)));
		panelNodes.setLayout(new BoxLayout(panelNodes, BoxLayout.Y_AXIS));
		pGlobal.add(panelNodes, BorderLayout.NORTH);
		
		JButton btnRefresh = new JButton("Refresh list");
		btnRefresh.setToolTipText("Refresh list of nodes");
		btnRefresh.setHorizontalAlignment(JButton.RIGHT); 
	
		btnRefresh.setBounds(0, 0, 53, 23);
		panelNodes.add(btnRefresh);
		
		panelNodes.add(Box.createVerticalStrut(5));
	
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(300, 170));
		//scrollPane.setMinimumSize(new Dimension(300,170));
		panelNodes.add(scrollPane);
		
		final DefaultListModel dlmNodes=new DefaultListModel();
		final JList listNodes = new JList(dlmNodes);
		scrollPane.setViewportView(listNodes);
		
		final JLabel lblListNodes = new JLabel("Nodes list ("+nbNodes+")");
		scrollPane.setColumnHeaderView(lblListNodes);
                
                listNodes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                
		
		panelNodes.add(Box.createVerticalStrut(5));
	
		JPanel panelDiscovering =new JPanel();
		//panelDiscovering.setBorder(new Border(5,5,5,5));
		panelDiscovering.setLayout(new BoxLayout(panelDiscovering, BoxLayout.X_AXIS));
		panelNodes.add(panelDiscovering);
                
                
		
		final JLabel lblSelectNode = new JLabel("Select node from list");
		lblSelectNode.setToolTipText("Select node to discover its vs");
		panelDiscovering.add(lblSelectNode);
                     		
		panelDiscovering.add(Box.createHorizontalStrut(10));
		
		final JButton btnGetListVS = new JButton("List VSs");
		btnGetListVS.setToolTipText("Discover VSs of selected node");
		
		btnGetListVS.setBounds(171, 236, 53, 23);
		btnGetListVS.setEnabled(false);
		panelDiscovering.add(btnGetListVS);
                
                
		
		panelNodes.add(Box.createVerticalStrut(5));
		
		//***********************************************************
		
		pGlobal.add(Box.createVerticalStrut(5));
		JPanel panelVs = new JPanel();
		panelVs.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Virtuals Sensors", TitledBorder.LEADING, TitledBorder.TOP, null, Color.MAGENTA));
		panelVs.setLayout(new BoxLayout(panelVs, BoxLayout.Y_AXIS));
		pGlobal.add(panelVs, BorderLayout.SOUTH);
		
		panelVs.add(Box.createVerticalStrut(5));

		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setPreferredSize(new Dimension(300, 170));
		//scrollPane2.setMinimumSize(new Dimension(300,170));
		panelVs.add(scrollPane2);
		
		final DefaultListModel dlmServices=new DefaultListModel();
		final JList listVs = new JList(dlmServices);
                listVs.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		scrollPane2.setViewportView(listVs);     
		
		final JLabel lblListVs = new JLabel("VSs list ("+nbServices+")");
		scrollPane2.setColumnHeaderView(lblListVs);
		
		panelVs.add(Box.createVerticalStrut(5));
		
		JPanel panelButtonVs =new JPanel();
		//panelRunningService.setBorder(new Border(5,5,5,5));
		panelButtonVs.setLayout(new BoxLayout(panelButtonVs, BoxLayout.X_AXIS));
		panelVs.add(panelButtonVs);
		
		final JLabel lblSelectVs = new JLabel("Select VSs ");
		lblSelectVs.setToolTipText("Select VSs from list");
		panelButtonVs.add(lblSelectVs);
		
		panelButtonVs.add(Box.createHorizontalStrut(10));
                
                final JButton btnAddVs = new JButton("Add Vs");
                btnAddVs.setToolTipText("Add  VSs");
                btnAddVs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Add vs   
                            int si=listNodes.getSelectedIndex();
                                if(si>=0)
				{ 
                                    
                                AddVSFrame addframe = new AddVSFrame(GuiVS.this,urlNodes[si],0);
                                addframe.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                                addframe.setSize(300, 250);
                                addframe.setLocation(550, 200);
				addframe.setVisible(true);
                                //btnRunVsP.setEnabled(true);
                                   //Afficher(HttpClientGet("http://["+urlNodes[si]+"]:8080/addvs?services=13&freq=30"));
                                    /*String[] listOfVs=getAllVs(urlNodes[si]);
                                    lblListVs.setText("list Vs of node "+(si+1)+" ("+nbServices+")");
                                    if(listOfVs!=null)
                                    {
                                        dlmServices.removeAllElements();                               
                                            for( int i=0;i<nbServices;i++)
                                            {
                                                dlmServices.addElement(listOfVs[i]);                                         
                                            }
                                    }*/  
                                }else
                                    Afficher("Select a node from list!!");                                                   
                                    
                        }
			
		});
                
		btnAddVs.setBounds(171, 236, 53, 23);
		panelButtonVs.add(btnAddVs);
                
                
		final JButton btnDeleteVs = new JButton("Delete Vs");
                btnDeleteVs.setToolTipText("Delete selected VSs");
                btnDeleteVs.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
				// delete vs
                            int si=listVs.getSelectedIndex();
                                if(si>=0)
				{ 
                                    Afficher("Delete "+listVs.getSelectedValue());
                                    int sin=listNodes.getSelectedIndex();
                                        if(si>=0)
                                            { //Afficher("delete "+si+" from node "+sin);  
                                                  int id=Integer.parseInt((String)listVs.getSelectedValue());
                                               Afficher(DeleteVS(urlNodes[sin],id));
                                               nbServices--;
                                               lblListVs.setText("List Vs of node "+(si+1)+" ("+nbServices+")");
                                               dlmServices.remove(si);
                                            }
                                    
                                                                       
                                }else
				Afficher("Select VS");
                        }
			
		});
                
		btnDeleteVs.setBounds(171, 236, 53, 23);
		btnDeleteVs.setEnabled(false);
		panelButtonVs.add(btnDeleteVs);
                
               final JButton btnMigrateVs = new JButton("Migrate");
                btnMigrateVs.setToolTipText("Migrate selected VSs");
                btnMigrateVs.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
				// delete vs
                            int si=listVs.getSelectedIndex();
                                if(si>=0)
				{ 
                                    //Afficher("Delete "+listVs.getSelectedValue());
                                    int sin=listNodes.getSelectedIndex();
                                        if(sin>=0)
                                            
                                            { String x=(String) listVs.getSelectedValue();
                                             int idvs=Integer.parseInt(x);
                                             //details of vs
                                             String detailsVs=HttpClientGet("http://["+urlNodes[sin]+"]:8080/getDetailsVS?id="+(idvs));   
                                            System.out.println(detailsVs);
                                             // migrate
                                                Migration_v2 ms= new Migration_v2(GuiVS.this,sin,idvs,urlNodes,detailsVs,nbNodes);
                                                  ms.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                                                  ms.setSize(300, 350);
                                                  ms.setLocation(400, 200);
			                          ms.setVisible(true);
                                                //Afficher("delete "+si+" from node "+sin);                                     
                                               
                                             //delete   
                                               
                                              
                                            }
                                               
                                                                       
                                }else
				Afficher("Select VS!!");
                        }
			
		});
                
		btnMigrateVs.setBounds(171, 236, 53, 23);
		btnMigrateVs.setEnabled(false);
		panelButtonVs.add(btnMigrateVs);
                
                /*
		final JButton btnRunVs = new JButton("Run Vs(1)");
		btnRunVs.setToolTipText("Running selected VSs une seule foie");
		btnRunVs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// run services
                            int si=listVs.getSelectedIndex();
                                if(si>=0)
				{ 
                                   // Afficher("Run "+listVs.getSelectedValue());
                                    int sin=listNodes.getSelectedIndex();
                                        if(si>=0)
                                            { 
                                              Afficher(HttpClientGet("http://["+urlNodes[sin]+"]:8080/ExecVS?id="+(si+1)));  
                                            }
                                    
                                                                       
                                }else
				Afficher("Select VS");
                        }
			
		});
                
                  
		btnRunVs.setBounds(171, 236, 53, 23);
		btnRunVs.setEnabled(false);
		panelButtonVs.add(btnRunVs);*/
                
                btnRunVsP = new JButton("Run Vs(*)");
		btnRunVsP.setToolTipText("make sur that exist vs");
		btnRunVsP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// run services
                          /*  int[] si=listVs.getSelectedIndices();
                           String ch="",ch1; 
                            for(int j=0;j<si.length;j++){
                               ch1= Integer.toString(si[j]+1)+"+";
                               ch+=ch1;
                            }*/
                               // if(ch.length()>0)
				//{ //System.out.println("** "+ch+" **");
                                   // Afficher("Run "+listVs.getSelectedValue());
                                    int sin=listNodes.getSelectedIndex();
                                    
                                        if(sin>=0)
                                         
                                                //execute publish
                                                 Afficher(HttpClientGet("http://["+urlNodes[sin]+"]:8080/RunVS?")); 
                                               
                                                                                                                                                  
                                }//else
				//Afficher("Select VS");
                       
			
		});
                
		btnRunVsP.setBounds(171, 236, 53, 23);
		btnRunVsP.setEnabled(false);
		panelButtonVs.add(btnRunVsP);
                
                btnGetListVS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// dicovering node	
				//Afficher("Discovering selected node");
                                int si=listNodes.getSelectedIndex();
                                if(si>=0)
				{ 
                                    //Afficher(si+"=>"+urlNodes[si]);
                                    String[] listOfVs=getAllVs(urlNodes[si]);
                                    lblListVs.setText("List Vs of node "+(si+1)+" ("+nbServices+")");
                                    if(listOfVs!=null)
                                    {
                                        dlmServices.removeAllElements();                               
                                            for( int i=0;i<nbServices;i++)
                                            {   
                                                if(listOfVs[i].length()>2)
                                                dlmServices.addElement(listOfVs[i]);                                         
                                            }
                                            if(!dlmServices.isEmpty()){
                                                btnRunVsP.setEnabled(true);
                                            btnRunVsP.setToolTipText("you can run now");}
                                    }
                                }else
                                    Afficher("Select a node from list!!");                                                     
                                
			}
		});
                
                listVs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
                            String textServ="";
				if(e.getClickCount()==1)
                                    if(listVs.getSelectedIndex()>=0)
                                    {
                                    textServ=listVs.getSelectedValue().toString();
					//Afficher(textServ);
                                        //btnRunVs.setEnabled(true);
                                        //btnRunVsP.setEnabled(true);
                                        btnMigrateVs.setEnabled(true);
                                        btnDeleteVs.setEnabled(true);
                                        lblSelectVs.setText(textServ+" selected");
                                    }
				}
		});
                
                btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// refreshing list	
				//Afficher("Refreshing list of nodes");
                                //vider(urlNodes)
                                urlNodes=getAllNodes(urlGateway);
                                if(urlNodes!=null)
                                {
                                    lblListNodes.setText("Nodes list ("+nbNodes+")");
                                    lblListVs.setText("Vs list (0)");
                                    dlmNodes.removeAllElements();
                                    dlmServices.removeAllElements();
                                   // btnRunVs.setEnabled(false);
                                    lblSelectNode.setText("Select node");
                                    btnGetListVS.setEnabled(false);
                                    lblSelectVs.setText("Select Vs");
                                    for( int i=0;i<nbNodes;i++)
                                        {
                                        dlmNodes.addElement("Nodes"+(i+1)+" ("+urlNodes[i]+")");                                           
                                        }
                    try {
                        //add description to repositry
                        in=new Interrogation();
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(GuiVS.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(GuiVS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                                    for( int i=0;i<nbNodes;i++)
                                        {String node=urlNodes[i];
                                          
                                           
                                          String last = node.substring(node.lastIndexOf(':') + 1);
                        try {
                            if(in.existID(Integer.parseInt(last))==false){
                                //get random location
                                int rnd1 = new Random().nextInt(latitudes.length);
                                int rnd2 = new Random().nextInt(longitudes.length);
                                in.newSensor(Integer.parseInt(last), latitudes[rnd1], longitudes[rnd2]);
                            }
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(GuiVS.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            Logger.getLogger(GuiVS.class.getName()).log(Level.SEVERE, null, ex);
                        }
                                               
                                        }
                                }
                                
			}

            
		});
                
                listNodes.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
                            String textNode="";
                                    lblListVs.setText("Vs list (0)");
                                    dlmServices.removeAllElements();
                                    if(e.getClickCount()==1)                                    
                                    if(listNodes.getSelectedIndex()>=0 && urlNodes[listNodes.getSelectedIndex()]!=null)
                                    {
                                        textNode=listNodes.getSelectedValue().toString();
					//Afficher(textNode);
                                        btnGetListVS.setEnabled(true);
                                        lblSelectNode.setText(textNode.substring(0,textNode.indexOf(" "))+" is selected");
                                    }
				}
		});
		
		
		pGlobal.add(Box.createVerticalStrut(20));
		
	}
	
	/**
	 * Show as a MessageDialog
	 */
	
	public void Afficher(String msg)
	{
		JOptionPane.showMessageDialog(this,msg);
	}
      
	
	/**
	 * HttpClientGet Function
	 */
	public String HttpClientGet(String myurl){
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
        
        public String[] getAllNodes(String urlGateway) {
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
    
        public String[] getAllVs(String urlNode) {
            String texts=HttpClientGet("http://["+urlNode+"]:8080/getListVS");
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
        
        // return get_service as a string
        public String getService(String urlService,String ressourceService)
        {
            String sr=HttpClientGet("http://["+urlService+"]:8080/"+ressourceService);
            
            return sr;
        }
        
        // delete vs
         public String DeleteVS(String urlNode,int vsid)
        {
           String sr=HttpClientGet("http://["+urlNode+"]:8080/deleteVS?id="+(vsid));
            
            return sr;
        }
        
         public static void migrationMobility(Location l) throws ClassNotFoundException, SQLException{
            //migration to plus proche
             Interrogation in=new Interrogation();
             List<Location> myNodes=in.getGlobalContextInf4ALL();
             Location plusProche,curr;
             double minDistance = 0 ;
             if (myNodes.isEmpty()) {
               plusProche =null;
             } else {
                   final ListIterator<Location> itr = myNodes.listIterator();
                   plusProche = itr.next(); // first element as the current minimum
                   minDistance=plusProche.distanceTo(l);
                    System.out.println(plusProche.getName()+"("+plusProche.getLatitude()+","+plusProche.getLongitude()+") with distance="+minDistance);

                    while (itr.hasNext()) {
                         curr = itr.next();
                      if (curr.distanceTo(l)<minDistance) {

                         minDistance = curr.distanceTo(l);
                         //System.out.println(curr.getName()+"("+curr.getLatitude()+","+curr.getLongitude()+") with distance="+minDistance);
             
                         plusProche=curr;
                       }
                    }
            }
         //migration all vs from other nodes to plus proche    
             if(plusProche!=null){
             // System.out.println(plusProche.getName()+"("+plusProche.getLatitude()+","+plusProche.getLongitude()+") with distance="+minDistance);
              //int idpp=Integer.parseInt(plusProche.getName().split(" ")[1]);
                // System.out.println(plusProche.getName().split(" ")[1]);
                 
             }

         }//end migrationMobility
            
}

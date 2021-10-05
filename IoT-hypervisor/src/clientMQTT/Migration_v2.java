/*assure la migration(deplacement) d'un vs d'un capteur physique vers un autre*/
package clientMQTT;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class Migration_v2 extends JDialog{
    String[] urlNodes=new String[50];
    String detailsVS;
    int nbN;
    int p,i;
    public Migration_v2(JFrame frame,int posSource,int idVS, String[] listOfNode, String detailsVs, int nbNodes)
    {
        super(frame,"Migrate VS",true);
                
                detailsVS=detailsVs;
	        nbN=nbNodes;
                p=posSource;
                i=idVS;
                for(int x=0;x<listOfNode.length;x++)
                   urlNodes[x]=listOfNode[x];
        setLayout(new BorderLayout());
        //JLabel label=new JLabel("Add your vs informations:");
        //add(label,BorderLayout.NORTH);        
               JPanel pGlobal=new JPanel (new BorderLayout(5,5));
		
		//pGlobal.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Nodes", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(1, 120, 190)));
		setContentPane(pGlobal);
		
		//*************************************************************** 
		
		JPanel panelNodes = new JPanel();
		panelNodes.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Nodes", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(1, 120, 190)));
		panelNodes.setLayout(new BoxLayout(panelNodes, BoxLayout.Y_AXIS));
		pGlobal.add(panelNodes, BorderLayout.NORTH);
		
		
		
		panelNodes.add(Box.createVerticalStrut(5));
	
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(300, 170));
		//scrollPane.setMinimumSize(new Dimension(300,170));
		panelNodes.add(scrollPane);
		
		final DefaultListModel dlmNodes=new DefaultListModel();
		final JList listNodes = new JList(dlmNodes);
		scrollPane.setViewportView(listNodes);
		
		final JLabel lblListNodes = new JLabel("Nodes list ("+(nbNodes-1)+")");
		scrollPane.setColumnHeaderView(lblListNodes);
                
                listNodes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                 for( int i=0;i<=nbNodes;i++)
                  {  if((i!=posSource)&&(listOfNode[i]!=null))
                     dlmNodes.addElement(listOfNode[i]); 
                   }
                
		
		panelNodes.add(Box.createVerticalStrut(5));
	
		JPanel panelDiscovering =new JPanel();
		//panelDiscovering.setBorder(new Border(5,5,5,5));
		panelDiscovering.setLayout(new BoxLayout(panelDiscovering, BoxLayout.X_AXIS));
		panelNodes.add(panelDiscovering);
                
                
		
		final JLabel lblSelectNode = new JLabel("Select node from list");
		lblSelectNode.setToolTipText("Select node to discover its vs");
		panelDiscovering.add(lblSelectNode);
                     		
		panelDiscovering.add(Box.createHorizontalStrut(10));
		
		final JButton btnGetListVS = new JButton("Migrate");
		btnGetListVS.setToolTipText("Migrate to this sensor");
		
		btnGetListVS.setBounds(171, 236, 53, 23);
		btnGetListVS.setEnabled(false);
		panelDiscovering.add(btnGetListVS);
                
                btnGetListVS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// dicovering node	
				//Afficher("Discovering selected node");
                                int si=listNodes.getSelectedIndex();
                                if(si>=0)
				{ 
                              
                                 int pos= cherche((String)listNodes.getSelectedValue());
                           if(pos>0){ 
                        try {
                            String ok=AddVSFrame.AddVS("http://["+urlNodes[pos]+"]:8080/addvs?"+detailsVS);
                            Afficher("migration terminate with succes");
                        } catch (IOException ex) {
                            Logger.getLogger(Migration_v2.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                       
                         //delete vs from source
                          String del=DeleteVS(urlNodes[p],i);

                        }else
                                    Afficher("Select a node from list!!");                                                     
                                
			}
		});
		
                
                
		panelNodes.add(Box.createVerticalStrut(5));
		
                
                
		//***********************************************************
		
                
                listNodes.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
                            
                                    //lblListVs.setText("Vs list (0)");
                                    //dlmServices.removeAllElements();
                                    if(e.getClickCount()==1)                                    
                                    if(listNodes.getSelectedIndex()>=0 && urlNodes[listNodes.getSelectedIndex()]!=null)
                                    {
                                        
					//Afficher(textNode);
                                        btnGetListVS.setEnabled(true);
                                       // lblSelectNode.setText(textNode.substring(0,textNode.indexOf(" "))+" is selected");
                                    }
				}
		});
		
		
		pGlobal.add(Box.createVerticalStrut(20));
                
		
	}
        
        public int cherche(String node){
        int i=0;
        while((i<=nbN)&&(!urlNodes[i].equals(node))){
        i++;
        }
        if(i<=nbN)
            return i;
        else
            return -1;
        }
        
        public void Afficher(String msg)
	{
		JOptionPane.showMessageDialog(this,msg);
	}
   
        public String DeleteVS(String urlNode,int vsid)
        {
           String sr=HttpClientGet("http://["+urlNode+"]:8080/deleteVS?id="+(vsid));
            
            return sr;
        }
        
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
}

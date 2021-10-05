/*pour creer un nouveau vs */
package clientMQTT;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class AddVSFrame extends JDialog{
    String urlNode;
    int m,nbvs;
    public AddVSFrame(JFrame frame,String urlN,int migration)
    {
        super(frame,"Create VS",true);
        urlNode=urlN;
        m=migration;
        setLayout(new BorderLayout());
        JLabel label=new JLabel("Add your vs informations:");
        //add(label,BorderLayout.NORTH);        
                JPanel panel_0 = new JPanel(); 
                panel_0.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "VS informations", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(100, 100, 100)));
		panel_0.setToolTipText("Add your vs informations");
                panel_0.setLayout(new FlowLayout(FlowLayout.LEFT));
                add(panel_0, BorderLayout.CENTER);
                
                
                JPanel panel_2 = new JPanel();
		panel_0.add(panel_2);
		panel_2.setLayout(new GridLayout(4, 1, 0, 10));
		
		
		
		JLabel lblNewLabel_2 = new JLabel("Frequence");
		panel_2.add(lblNewLabel_2);
                
                final JTextField textField_2 = new JTextField();
		panel_2.add(textField_2);
		textField_2.setColumns(10);
		
	
                
                
                JPanel panel_cb = new JPanel();              
                panel_cb.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Services", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(100, 100, 100)));
		panel_0.add(panel_cb);
		panel_cb.setLayout(new GridLayout(3, 1, 0, 10));
                
                final JCheckBox cb_temp = new JCheckBox("Temperature");
                final JCheckBox cb_hum = new JCheckBox("Humidity");
                final JCheckBox cb_light = new JCheckBox("Light");
                panel_cb.add(cb_temp);
                panel_cb.add(cb_hum);
                panel_cb.add(cb_light);
                
                /*panel num vs*/
                JPanel panelNumbVS = new JPanel();
		panel_0.add(panelNumbVS, BorderLayout.SOUTH);
		panelNumbVS.setLayout(new GridLayout(4, 1, 0, 10));
		
		
		
		JLabel lblNewLabel = new JLabel("Number of VS");
		panelNumbVS.add(lblNewLabel);
                
                final JTextField textField_3 = new JTextField("1");
                textField_3.setToolTipText("par defaut creer 1 vs");
		panelNumbVS.add(textField_3);
		textField_3.setColumns(5);
                /******************/
                
                JPanel panel_1 = new JPanel();
                panel_1.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(panel_1, BorderLayout.SOUTH);
                
		JButton btnCancel = new JButton("Cancel");
                btnCancel.setSize(new Dimension(50,50));
		btnCancel.setToolTipText("Cancel operation");
		panel_1.add(btnCancel);
                
                btnCancel.addActionListener(new ActionListener() {
                        @Override
			public void actionPerformed(ActionEvent arg0) {
				// cancel
                            int i=JOptionPane.showConfirmDialog(AddVSFrame.this, "Cancel operation !!", "Cancel", JOptionPane.CANCEL_OPTION,JOptionPane.OK_CANCEL_OPTION);                          
                            if(i==0)
                                dispose();
                           
                        }
		});
                
                
                JButton btnAdd = new JButton("Create");
                btnAdd.setSize(new Dimension(50,50));
		btnAdd.setToolTipText("Create virtual sensor");
		panel_1.add(btnAdd);
                
                btnAdd.addActionListener(new ActionListener() {
                        @Override
			public void actionPerformed(ActionEvent arg0) {
				// add
                            String ok = null;
                            if((textField_2.getText().equals(""))||(textField_3.getText().equals("")))
				{
                                    JOptionPane.showMessageDialog(AddVSFrame.this, "All fields are required!","Alert", JOptionPane.ERROR_MESSAGE);
                                }
                            else
				try { nbvs= Integer.parseInt(textField_3.getText());
                                        String freq=textField_2.getText();
					String serv="";
					if(cb_temp.isSelected())
					{serv+="1";}
                                        if(cb_hum.isSelected())
					{serv+="2";}
                                        if(cb_light.isSelected())
					{serv+="3";}
                                        
                                       // int st=comboBox.getSelectedIndex();
                                        if(nbvs>0){
                                         
                                            for(int compteur=1;compteur<=nbvs;compteur++)
                                             ok=AddVS("http://["+urlNode+"]:8080/addvs?services="+serv+"&freq="+freq+"&id="+m);
                                        
                                        }JOptionPane.showMessageDialog(AddVSFrame.this,"created");
					 dispose();
				} catch (Exception e) {
                                    System.out.println("There are an exception:");
                                    e.printStackTrace();
                                     }
                        }
		});
		
    
    }
    
   static public String AddVS(String myurl) throws IOException{
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

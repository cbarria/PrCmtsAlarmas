import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

@SuppressWarnings("serial")
public class listaServidores extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {				
					listaServidores frame = new listaServidores();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
              
               
                
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	public listaServidores() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 510, 461);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);
		
		JButton btnNuevo = new JButton("Nuevo");
		btnNuevo.setBounds(10, 11, 89, 23);
		contentPane.add(btnNuevo);
		
		JButton btnBorrar = new JButton("Borrar");
		btnBorrar.setBounds(109, 11, 89, 23);
		contentPane.add(btnBorrar);
		
		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.setBounds(208, 11, 89, 23);
		contentPane.add(btnGuardar);	
		
		JLabel lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(341, 42, 53, 14);
		contentPane.add(lblNombre);
		
		textField = new JTextField();
		textField.setBounds(341, 58, 116, 23);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblDireccion = new JLabel("Direcci\u00F3n:");
		lblDireccion.setBounds(341, 92, 57, 14);
		contentPane.add(lblDireccion);
		
		textField_1 = new JTextField();
		textField_1.setBounds(341, 158, 116, 23);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Puerto:");
		lblNewLabel.setBounds(341, 140, 46, 14);
		contentPane.add(lblNewLabel);
		
		textField_2 = new JTextField();
		textField_2.setBounds(341, 212, 116, 23);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		
		JLabel lblTipo = new JLabel("Tipo:");
		lblTipo.setBounds(341, 192, 33, 14);
		contentPane.add(lblTipo);
		
		textField_3 = new JTextField();
		textField_3.setBounds(341, 106, 116, 23);
		contentPane.add(textField_3);
		textField_3.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 40, 314, 372);
		contentPane.add(scrollPane);
		
		JTree tree = new JTree();
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
							
				DefaultMutableTreeNode selectedElement 
				   =(DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
				
				//System.out.println(selectedElement.getUserObject()); 
				
			    if (node == null)
			    //Nothing is selected.  
			    return;

			    Object nodeInfo = node.getUserObject();

			    if (node.isLeaf()) {
			    // terminar!
			    } else {
			        
			    }
			}
		});
		tree.setModel(new CustomTreeModel(new DefaultMutableTreeNode("Sectores")));
		scrollPane.setViewportView(tree);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);		
	}	
} 


class CustomTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;
	BufferedReader br;
	
	public CustomTreeModel(TreeNode root) {
		super(root);
		// TODO Auto-generated constructor stub
		
		File archivo = new File("servidores.lst");

		try {
			br = new BufferedReader(new FileReader(archivo));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    String s = null;
	    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) root;
	    List<String> items = new ArrayList<>();
	
	    DefaultMutableTreeNode sector = new DefaultMutableTreeNode() ;
	    DefaultMutableTreeNode nombreServidor;
	    DefaultMutableTreeNode urlServidor;
	    DefaultMutableTreeNode puertoServidor;
	    DefaultMutableTreeNode tipoServidor;
	
	    try {
			while ((s = br.readLine()) != null)
			{
			    items.addAll(Arrays.asList(s.split("\\s")));
			    items.removeAll(Arrays.asList(""));

			    if (items.size() == 1){
			    	sector = new DefaultMutableTreeNode(items.get(0));
			    	//sector.setAllowsChildren(true);
			    	rootNode.add(sector);    				    	
			    }
			    
			    if (items.size() == 4)
			    {
			    	//System.out.print("lala");
			       	nombreServidor = new DefaultMutableTreeNode(items.get(0));
			    	nombreServidor.setAllowsChildren(true);
			    	
			    	urlServidor = new DefaultMutableTreeNode(items.get(1));
			    	urlServidor.setAllowsChildren(false);
			    	
			    	puertoServidor = new DefaultMutableTreeNode(items.get(2));
			    	puertoServidor.setAllowsChildren(false);
			    	
			    	tipoServidor = new DefaultMutableTreeNode(items.get(3));
			    	tipoServidor.setAllowsChildren(false);
			    	
			    	nombreServidor.add(urlServidor);
			    	nombreServidor.add(puertoServidor);
			    	nombreServidor.add(tipoServidor);
			    	
			    	sector.add(nombreServidor);   
			    }
			    items.clear();			    

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    setRoot(rootNode);
	}
}



import javax.swing.JFrame;
import javax.swing.JLabel;


public class ProgramFrame extends JFrame {

	JLabel label;
	public ProgramFrame()
	{
		super("Klient");
		label=new JLabel("Etykieta");
		add(label);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300,100);
		setVisible(true);
	}
	
	public JLabel getLabel()
	{
		return label;
	}
	
}

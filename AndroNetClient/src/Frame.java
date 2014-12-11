import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Lukasz on 2014-11-17.
 */
public class Frame extends JFrame {
    private JTextField textField1;
    private JButton button1;
    private JPanel panel;

    private Client client;

    public Frame(final Client client)
    {
        super("Test Client App");
        this.client=client;

        this.setContentPane(panel);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.send("test", textField1.getText());
            }
        });

        this.setVisible(true);
    }
}
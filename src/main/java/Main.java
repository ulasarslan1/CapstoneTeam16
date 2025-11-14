import java.io.IOException;
import javax.swing.SwingUtilities;

import HMI.HMI;

public class Main {
    

	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
			try {
				new HMI().setVisible(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
    }
    
    
}

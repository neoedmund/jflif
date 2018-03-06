package neoe.flif;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class FLIFViewerDemo {

	public static void main(String[] args) throws Exception {
		new FLIFViewerDemo().show(args[0]);

	}

	private void show(String fn) throws Exception {
		BufferedImage img = readImg(fn);
		JFrame f = new JFrame("flif viewer");
		f.getContentPane().add(new JLabel(new ImageIcon(img)));
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}

	private BufferedImage readImg(String fn) throws Exception {
		byte[] bs = FileUtil.read(new FileInputStream(fn));
		return JFLIF.decode(bs);
	}

}

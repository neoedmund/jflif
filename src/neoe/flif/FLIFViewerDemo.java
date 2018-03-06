package neoe.flif;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
		byte[] bs = read(new FileInputStream(fn));
		return JFLIF.dec(bs);
	}

	public static void copy(InputStream in, OutputStream outstream) throws IOException {
		BufferedOutputStream out = new BufferedOutputStream(outstream);
		byte[] buf = new byte[1024 * 16];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copy(in, baos);
		return baos.toByteArray();
	}
}

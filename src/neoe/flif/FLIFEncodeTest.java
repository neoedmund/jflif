package neoe.flif;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

public class FLIFEncodeTest {

	public static void main(String[] args) throws Exception {
		new FLIFEncodeTest().encode(args[0], args[1], Integer.parseInt(args[2]));

	}

	/**
	 * 
	 * @param fn
	 * @param fn2
	 * @param loss
	 *            0 for no loss, 100 for maximum loss
	 * @throws Exception
	 */
	private void encode(String fn, String fn2, int loss) throws Exception {
		BufferedImage img = ImageIO.read(new File(fn));

		byte[] bs = JFLIF.encode(img, loss);

		FileOutputStream out = new FileOutputStream(fn2);
		out.write(bs);
		out.close();
		System.out.println("write  to "+fn2);

	}

	

}

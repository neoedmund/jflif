package neoe.flif;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class FLIFEncodeAnimateTest {

	public static void main(String[] args) throws Exception {
		new FLIFEncodeAnimateTest().run(args[0], Integer.parseInt(args[1]), args[2]);

	}

	private void run(String dir, int cnt, String outfn) throws IOException {

		CFLIF lib = CFLIF.INSTANCE;
		Pointer encoder = lib.flif_create_encoder();
		lib.flif_encoder_set_lossy(encoder, 0);
		FileOutputStream out = new FileOutputStream(outfn);

		for (int i = 0; i <= cnt; i++) {
			File f = new File(dir, i + ".png");
			System.out.println("add " + i + "/" + cnt);
			BufferedImage img0 = ImageIO.read(f);
			BufferedImage img = resize(img0, 64, 64);
			int[] rgbArray = getRGBArr(img);
			int w = img.getWidth();
			int h = img.getHeight();
			Pointer image = lib.flif_import_image_RGBA(w, h, rgbArray, w * 4);
			lib.flif_image_set_frame_delay(image, 80);
			lib.flif_encoder_add_image_move(encoder, image);
		}
		PointerByReference buffer = new PointerByReference();
		int[] len = new int[1];
		lib.flif_encoder_encode_memory(encoder, buffer, len);
		byte[] bs = buffer.getValue().getByteArray(0, len[0]);
		System.out.println(bs.length + " -> " + outfn);

		out.write(bs);
		out.close();
		lib.flif_destroy_encoder(encoder);

	}

	private BufferedImage resize(BufferedImage img, int w, int h) {
		BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = b.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return b;
	}

	private int[] getRGBArr(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		int[] rgbArray = new int[w * h];
		img.getRGB(0, 0, w, h, rgbArray, 0, w);
		// ARGB->ABGR
		for (int i = 0; i < rgbArray.length; i++) {
			int c = rgbArray[i];
			rgbArray[i] = (c >> 16) & 0xff | (c & 0xff) << 16 | c & 0xff00ff00;
		}
		return rgbArray;
	}

	 

}

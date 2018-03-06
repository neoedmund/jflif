package neoe.flif;

import java.awt.image.BufferedImage;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class JFLIF {
	public static BufferedImage dec(byte[] buf) {
		CFLIF lib = CFLIF.INSTANCE;
		Pointer decoder = lib.flif_create_decoder();
		int ret = lib.flif_decoder_decode_memory(decoder, buf, buf.length);
		System.out.println("decode ret=" + ret);
		int num = lib.flif_decoder_num_images(decoder);
		System.out.println("decode num=" + num);
		if (num <= 0)
			return null;
		Pointer image = lib.flif_decoder_get_image(decoder, 0);
		int w = lib.flif_image_get_width(image);
		int h = lib.flif_image_get_height(image);
		// caution: performance
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int[] out = new int[w * h];
		lib.flif_image_write_RGBA8(image, out, 4 * w * h);
		// ARGB->ABGR
		for (int i = 0; i < out.length; i++) {
			int c = out[i];
			out[i] = (c >> 16) & 0xff | (c & 0xff) << 16 | c & 0xff00ff00;
		}
		bi.setRGB(0, 0, w, h, out, 0, w);
		//
		lib.flif_destroy_decoder(decoder);
		// lib.flif_destroy_image(image);
		return bi;
	}

	public static byte[] encode(BufferedImage img, int loss) {
		int w = img.getWidth();
		int h = img.getHeight();
		int[] rgbArray = new int[w * h];
		img.getRGB(0, 0, w, h, rgbArray, 0, w);
		// ARGB->ABGR
		for (int i = 0; i < rgbArray.length; i++) {
			int c = rgbArray[i];
			rgbArray[i] = (c >> 16) & 0xff | (c & 0xff) << 16 | c & 0xff00ff00;
		}
		CFLIF lib = CFLIF.INSTANCE;
		Pointer encoder = lib.flif_create_encoder();
		Pointer image = lib.flif_import_image_RGBA(w, h, rgbArray, w * 4);
		lib.flif_encoder_set_lossy(encoder, loss);
		lib.flif_encoder_add_image_move(encoder, image);
		PointerByReference buffer = new PointerByReference();
		int[] len = new int[1];
		lib.flif_encoder_encode_memory(encoder, buffer, len);
		// System.out.println(len[0]);
		byte[] bs = buffer.getValue().getByteArray(0, len[0]);
		System.out.println(bs.length);
		lib.flif_destroy_encoder(encoder);
		return bs;
	}
}

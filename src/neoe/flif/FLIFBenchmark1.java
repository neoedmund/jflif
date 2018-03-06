package neoe.flif;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import javax.imageio.ImageIO;

public class FLIFBenchmark1 {

	public static void main(String[] args) throws Exception {
		new FLIFBenchmark1().run(args[0]);

	}

	private void run(String fn) throws IOException {
		BufferedImage img = ImageIO.read(new File(fn));
		long flen = new File(fn).length();
		ExecutorService tp = java.util.concurrent.Executors.newFixedThreadPool(4);
		for (int loss0 = 0; loss0 <= 100; loss0 += 10) {
			final int loss = loss0;
			tp.submit(() -> {
				byte[] bs = JFLIF.encode(img, loss);
				BufferedImage img2 = JFLIF.decode(bs);
				float loss1 = getLoss1(img, img2);
				System.out.printf("loss:%d\tsize:%,d\tsize ratio:%.1f%%\tLOSS1*:%f\n", loss, bs.length,
						ratio(bs.length, flen), loss1);
				try {
					FileUtil.save(bs, fn + "." + loss + ".flif");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
		tp.shutdown();
	}

	private static float ratio(int length, long flen) {
		return 100f * length / flen;
	}

	private float getLoss1(BufferedImage img, BufferedImage img2) {
		int w = img.getWidth();
		int h = img.getHeight();
		float d = 0;
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				int c1 = img.getRGB(i, j);
				int c2 = img2.getRGB(i, j);
				d += diff1(c1, c2);
			}
		}
		return d / w / h;
	}

	private float diff1(int c1, int c2) {
		int[] rgb1 = getRGB(c1);
		int[] rgb2 = getRGB(c2);
		float d = 0;
		for (int i = 0; i < 3; i++) {
			d += pow2(rgb1[i] - rgb2[i]) / 256f;
		}
		return d;
	}

	private static float pow2(int i) {
		return i * i;
	}

	private static int[] getRGB(int c) {
		int[] bs = new int[3];
		bs[0] = (c >> 16) & 0xff;
		bs[1] = (c >> 8) & 0xff;
		bs[2] = c & 0xff;
		return bs;
	}

}

package neoe.flif;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface CFLIF extends Library {
	public static final String JNA_LIBRARY_NAME = "libflif";
	public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(CFLIF.JNA_LIBRARY_NAME);
	public static final CFLIF INSTANCE = (CFLIF) Native.loadLibrary(CFLIF.JNA_LIBRARY_NAME, CFLIF.class);

	Pointer /* FLIF_IMAGE* */ flif_import_image_RGBA(int width, int height, int[] rgba, int rgba_stride);

	Pointer /* FLIF_IMAGE* */ flif_import_image_RGB(int width, int height, byte[] rgb, int rgb_stride);

	Pointer /* FLIF_IMAGE* */ flif_import_image_GRAY(int width, int height, byte[] gray, int gray_stride);

	Pointer /* FLIF_IMAGE* */ flif_import_image_GRAY16(int width, int height, byte[] gray, int gray_stride);

	Pointer /* FLIF_IMAGE* */ flif_import_image_PALETTE(int width, int height, byte[] gray, int gray_stride);

	void flif_destroy_image(Pointer /* FLIF_IMAGE* */ image);

	int flif_image_get_width(Pointer /* FLIF_IMAGE* */ image);

	int flif_image_get_height(Pointer /* FLIF_IMAGE* */ image);

	byte flif_image_get_nb_channels(Pointer /* FLIF_IMAGE* */ image);

	byte flif_image_get_depth(Pointer /* FLIF_IMAGE* */ image);

	int flif_image_get_palette_size(Pointer /* FLIF_IMAGE* */ image); // 0 = no palette, 1-256 = nb of colors in palette

	void flif_image_get_palette(Pointer /* FLIF_IMAGE* */ image, byte[] buffer); // puts RGBA colors in buffer
																					// (4*palette_size bytes)

	void flif_image_set_palette(Pointer /* FLIF_IMAGE* */ image, byte[] buffer, int palette_size); // puts RGBA colors
																									// in buffer
																									// (4*palette_size
																									// bytes)

	int flif_image_get_frame_delay(Pointer /* FLIF_IMAGE* */ image);

	void flif_image_set_frame_delay(Pointer /* FLIF_IMAGE* */ image, int delay);

	// --------------------------------
	// initialize a FLIF decoder
	Pointer /* FLIF_DECODER* */ flif_create_decoder();

	// decode a given FLIF file
	// int flif_decoder_decode_file(Pointer /*FLIF_DECODER* */ decoder, const char*
	// filename);
	// decode a FLIF blob in memory: buffer should point to the blob and
	// buffer_size_bytes should be its size
	int flif_decoder_decode_memory(Pointer /* FLIF_DECODER* */ decoder, byte[] buffer, int buffer_size_bytes);

	/*
	 * Decode a given FLIF from a file pointer The filename here is used for error
	 * messages. It would be helpful to pass an actual filename here, but a non-NULL
	 * dummy one can be used instead.
	 */
	// int flif_decoder_decode_filepointer(Pointer /*FLIF_DECODER* */ decoder, FILE
	// *filepointer, const char *filename);

	// returns the number of frames (1 if it is not an animation)
	int flif_decoder_num_images(Pointer /* FLIF_DECODER* */ decoder);

	// only relevant for animations: returns the loop count (0 = loop forever)
	int flif_decoder_num_loops(Pointer /* FLIF_DECODER* */ decoder);

	// returns a pointer to a given frame, counting from 0 (use index=0 for still
	// images)
	Pointer /* FLIF_IMAGE* */ flif_decoder_get_image(Pointer /* FLIF_DECODER* */ decoder, int index);

	// void flif_decoder_generate_preview(void *context);

	// release an decoder (has to be called after decoding is done, to avoid memory
	// leaks)
	void flif_destroy_decoder(Pointer /* FLIF_DECODER* */ decoder);

	// abort a decoder (can be used before decoding is completed)
	int flif_abort_decoder(Pointer /* FLIF_DECODER* */ decoder);

	// decode options, all optional, can be set after decoder initialization and
	// before actual decoding
	void flif_decoder_set_crc_check(Pointer /* FLIF_DECODER* */ decoder, int crc_check); // default: no (0)

	void flif_decoder_set_quality(Pointer /* FLIF_DECODER* */ decoder, int quality); // valid quality: 0-100

	void flif_decoder_set_scale(Pointer /* FLIF_DECODER* */ decoder, int scale); // valid scales: 1,2,4,8,16,...

	void flif_decoder_set_resize(Pointer /* FLIF_DECODER* */ decoder, int width, int height);

	void flif_decoder_set_fit(Pointer /* FLIF_DECODER* */ decoder, int width, int height);

	// Progressive decoding: set a callback function. The callback will be called
	// after a certain quality is reached,
	// and it should return the desired next quality that should be reached before
	// it will be called again.
	// The qualities are expressed on a scale from 0 to 10000 (not 0 to 100!) for
	// fine-grained control.
	// `user_data` can be NULL or a pointer to any user-defined context. The decoder
	// doesn't care about its contents;
	// it just passes the pointer value back to the callback.
	// void flif_decoder_set_callback(Pointer /*FLIF_DECODER* */ decoder, callback_t
	// callback, void *user_data);
	void flif_decoder_set_first_callback_quality(Pointer /* FLIF_DECODER* */ decoder, int quality); // valid quality:
																									// 0-10000

	// Reads the header of a FLIF file and packages it as a FLIF_INFO struct.
	// May return a null pointer if the file is not in the right format.
	// The caller takes ownership of the return value and must call
	// flif_destroy_info().
	Pointer /* FLIF_INFO* */ flif_read_info_from_memory(byte[] buffer, int buffer_size_bytes);

	// deallocator function for FLIF_INFO
	void flif_destroy_info(Pointer /* FLIF_INFO* */ info);

	// get the image width
	int flif_info_get_width(Pointer /* FLIF_INFO* */ info);

	// get the image height
	int flif_info_get_height(Pointer /* FLIF_INFO* */ info);

	// get the number of color channels
	byte flif_info_get_nb_channels(Pointer /* FLIF_INFO* */ info);

	// get the number of bits per channel
	byte flif_info_get_depth(Pointer /* FLIF_INFO* */ info);

	// get the number of animation frames
	int flif_info_num_images(Pointer /* FLIF_INFO* */ info);

	// ----------------------------
	Pointer /* FLIF_ENCODER* */ flif_create_encoder();

	// give it an image to encode; add more than one image to encode an animation;
	// it will CLONE the image
	// (so the input image is not touched and you have to call flif_destroy_image on
	// it yourself to free that memory)
	void flif_encoder_add_image(Pointer /* FLIF_ENCODER* */ encoder, Pointer /* FLIF_IMAGE* */ image);

	// give it an image to encode; add more than one image to encode an animation;
	// it will MOVE the input image
	// (input image becomes invalid during encode and flif_destroy_encoder will free
	// it)
	void flif_encoder_add_image_move(Pointer /* FLIF_ENCODER* */ encoder, Pointer /* FLIF_IMAGE* */ image);

	// encode to a file
	// int flif_encoder_encode_file(Pointer /* FLIF_ENCODER* */ encoder, const char*
	// filename);

	// encode to memory (afterwards, buffer will point to the blob and
	// buffer_size_bytes contains its size)
	int flif_encoder_encode_memory(Pointer /* FLIF_ENCODER* */ encoder, PointerByReference buffer,
			int[] buffer_size_bytes);

	// release an encoder (has to be called to avoid memory leaks)
	void flif_destroy_encoder(Pointer /* FLIF_ENCODER* */ encoder);

	// encoder options (these are all optional, the defaults should be fine)
	void flif_encoder_set_interlaced(Pointer /* FLIF_ENCODER* */ encoder, int interlaced); // 0 = -N, 1 = -I (default:
																							// -I)

	void flif_encoder_set_learn_repeat(Pointer /* FLIF_ENCODER* */ encoder, int learn_repeats); // default: 2 (-R)

	void flif_encoder_set_auto_color_buckets(Pointer /* FLIF_ENCODER* */ encoder, int acb); // 0 = -B, 1 = default

	void flif_encoder_set_palette_size(Pointer /* FLIF_ENCODER* */ encoder, int palette_size); // default: 512 (max
																								// palette size)

	void flif_encoder_set_lookback(Pointer /* FLIF_ENCODER* */ encoder, int lookback); // default: 1 (-L)

	void flif_encoder_set_divisor(Pointer /* FLIF_ENCODER* */ encoder, int divisor); // default: 30 (-D)

	void flif_encoder_set_min_size(Pointer /* FLIF_ENCODER* */ encoder, int min_size); // default: 50 (-M)

	void flif_encoder_set_split_threshold(Pointer /* FLIF_ENCODER* */ encoder, int threshold); // default: 64 (-T)
	// The default is to not store RGB values of fully transparent pixels. If you
	// want to avoid that, you have to change it with this function!

	void flif_encoder_set_alpha_zero(Pointer /* FLIF_ENCODER* */ encoder, int lossless); // 0 = default (RGB undefined
																							// when A=0), 1 = keep RGB
																							// at A=0 (-K)

	void flif_encoder_set_alpha_zero_lossless(Pointer /* FLIF_ENCODER* */ encoder); // {
																					// flif_encoder_set_alpha_zero(encoder,1);
																					// };

	void flif_encoder_set_chance_cutoff(Pointer /* FLIF_ENCODER* */ encoder, int cutoff); // default: 2 (-X)

	void flif_encoder_set_chance_alpha(Pointer /* FLIF_ENCODER* */ encoder, int alpha); // default: 19 (-Z)

	void flif_encoder_set_crc_check(Pointer /* FLIF_ENCODER* */ encoder, int crc_check); // 0 = no CRC, 1 = add CRC

	void flif_encoder_set_channel_compact(Pointer /* FLIF_ENCODER* */ encoder, int plc); // 0 = -C, 1 = default

	void flif_encoder_set_ycocg(Pointer /* FLIF_ENCODER* */ encoder, int ycocg); // 0 = -Y, 1 = default

	void flif_encoder_set_frame_shape(Pointer /* FLIF_ENCODER* */ encoder, int frs); // 0 = -S, 1 = default

	/**
	 * set amount of quality loss, 0 for no loss, 100 for maximum loss, negative
	 * values indicate adaptive lossy (second image should be the saliency map)
	 */
	void flif_encoder_set_lossy(Pointer /* FLIF_ENCODER* */ encoder, int loss); // default: 0 (lossless)

	// ---------- added by neoe --------
	void flif_image_write_RGBA8(Pointer /* FLIF_IMAGE* */ image, int[] buffer, int buffer_size_bytes);
}

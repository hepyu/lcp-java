package com.open.common.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImageUtil {

	private static final Log logger = LogFactory.getLog(ImageUtil.class);

	public static byte[] getImage(String urlString) {
		try {
			URL u = new URL(urlString);
			BufferedImage image = ImageIO.read(u);

			// convert BufferedImage to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);
			baos.flush();
			return baos.toByteArray();
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		return null;
	}

	public static byte[] getImage(InputStream imgInputStream, String imgType) {
		try {
			BufferedImage image = ImageIO.read(imgInputStream);

			// convert BufferedImage to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, imgType, baos);
			baos.flush();
			return baos.toByteArray();
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		return null;
	}
}

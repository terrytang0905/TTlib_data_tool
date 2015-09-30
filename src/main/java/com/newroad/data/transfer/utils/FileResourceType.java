package com.newroad.data.transfer.utils;

/**
 * @author tangzj1
 * @version 2.0
 * @since Apr 11, 2014
 */
public enum FileResourceType {
	general(128, "other", "application/octet-stream"), 
	image(256), 
	jpg(257,".jpg", "image/jpeg"), 
	bmp(258, ".bmp", "image/x-ms-bmp"), 
	png(259, ".png", "image/png"), 
	gif(260, ".gif", "image/gif"),
	jpeg(261,".jpeg", "image/jpeg"), 
	document(384), 
	doc(385, ".doc","application/msword"), 
	docx(386, ".docx", "application/msword"), ppt(
			387), xls(388), txt(389, ".txt", "text/plain"), pdf(390, ".pdf",
			"application/pdf"), pptx(391), xlsx(392), xml(393, ".xml",
			"text/xml"), xhtml(394, ".xhtml", "application/xhtml+xml"), audio(
			512, ".au", "audio/basic"), kk(513, ".kk", "text/x-koka"), mp3(514,
			".mp3", "audio/mpeg"), wav(515, ".wav", "audio/wav"), amr(516), aac(
			517), ogg(518), midi(519, ".midi", "audio/midi"), mid(520, ".mid",
			"audio/midi"), ape(521), file(640), rar(641), zip(642, ".zip",
			"application/zip"), apk(643), text(768), handwrite(896), doodle(
			1024), todo(1152), video(1280), mp4(1281), avi(1282), mov(1283), rm(
			1284), rmvb(1285), flv(1286), mpeg(1287), v3gp(1288), templateDate(
			1408), templateContacts(1536), templateLocation(1664), templateWeather(
			1792), templateTime(1920), templateMood(2048),

	/**
	 * 附件类型判定子(16进制表示)：TYPE_DECIDE = 0xff80 使用附件类型判定子可以判断附件的大类，如：TYPE_IMAGE_JGP
	 * & TYPE_DECIDE = TYPE_IMAGE
	 */
	DECIDE(65408), mix(99);

	private int value;
	private String fileExtension;
	private String contentType;

	private FileResourceType(int value) {
		this.value = value;
	}

	private FileResourceType(int value, String fileExtension, String contentType) {
		this.value = value;
		this.fileExtension = fileExtension;
		this.contentType = contentType;
	}

	public static String getContentType(String fileExtension) {
		for (FileResourceType dict : FileResourceType.values()) {
			if (fileExtension.equals(dict.getFileExtension())) {
				return dict.getContentType();
			}
		}
		return FileResourceType.getContentType(128);
	}

	public static String getContentType(int value) {
		for (FileResourceType dict : FileResourceType.values()) {
			if (value == dict.getValue()) {
				return dict.getContentType();
			}
		}
		return FileResourceType.getContentType("other");
	}
	
//	public static int getValue(String fileExtension) {
//		for (FileResourceType dict : FileResourceType.values()) {
//			if (fileExtension.equals(dict.getFileExtension())) {
//				return dict.getValue();
//			}
//		}
//		return 128;
//	}
	
	public static int getValue(String contentType) {
		for (FileResourceType dict : FileResourceType.values()) {
			if (contentType.equals(dict.getContentType())) {
				return dict.getValue();
			}
		}
		return 128;
	}

	public int getValue() {
		return this.value;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getContentType() {
		return contentType;
	}

}
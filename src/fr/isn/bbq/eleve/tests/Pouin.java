package fr.isn.bbq.eleve.tests;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

public class Pouin {
	
	public static void main(String[] args) throws AWTException, IOException{
		Robot robot = new Robot();
        String format = "jpg";
        String fileName = "E:/FullScreenshot." + format;
       
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
        ImageIO.write(screenFullImage, format, new File(fileName));

try {
	/*
	 * Reading a Image file from file system
	 */

	
	
	/*
	 * Converting Image byte array into Base64 String 
	 */
	String imageDataString = encodeImage(Files.readAllBytes(new File(fileName).toPath()));
	
	System.out.println(imageDataString);
	
	/*
	 * Converting a Base64 String into Image byte array 
	 */
	byte[] imageByteArray = decodeImage(imageDataString);
	
	System.out.println(imageByteArray);
	
	/*
	 * Write a image byte array into file system  
	 */
	FileOutputStream imageOutFile = new FileOutputStream("E:/FullScreenshotV2.jpg");
	imageOutFile.write(imageByteArray);
	
	imageOutFile.close();
	
	System.out.println("Image Successfully Manipulated!");
} catch (FileNotFoundException e) {
	System.out.println("Image not found" + e);
} catch (IOException ioe) {
	System.out.println("Exception while reading the Image " + ioe);
}

}

/**
* Encodes the byte array into base64 string
* @param imageByteArray - byte array
* @return String a {@link java.lang.String}
*/
public static String encodeImage(byte[] imageByteArray){		
return DatatypeConverter.printBase64Binary(imageByteArray);		
}

/**
* Decodes the base64 string into byte array
* @param imageDataString - a {@link java.lang.String} 
* @return byte array
*/
public static byte[] decodeImage(String imageDataString) {		
return DatatypeConverter.parseBase64Binary(imageDataString);
}
}
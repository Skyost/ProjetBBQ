package fr.isn.bbq.eleve.tests;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

public class ServerTest {
	
	/**
	* Premi�re m�thode ex�cut�e par le programme.
	* 
	* @param args Arguments � passer.
	*/
	
	public static final void main(final String[] args) {
		new Thread() {
			int w;
			int h;
			
			@Override
			public final void run() {
				try {
					final ServerSocket serverSocket = new ServerSocket(4444, 50, InetAddress.getByName("192.168.0.138")); // Cr�ation d'un nouveau serveur sur le port 4444.
					serverSocket.setSoTimeout(10000);
					while(true) {
						try {
							String imageDataString = null;
							System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
							Socket server = serverSocket.accept(); // Le client se connecte.
							System.out.println("Just connected to " + server.getRemoteSocketAddress());
							DataInputStream in = new DataInputStream(server.getInputStream());
							final String message = in.readUTF();
							System.out.println(message); // Message du client.
							final int index = Integer.valueOf(message.split(" ")[0]);
							
							Robot robot = new Robot();
					        String format = "jpg";
					        String fileName = "E:/FullScreenshot." + format;
					        File file = new File(fileName);
					       
					        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
					        BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
					        ImageIO.write(screenFullImage, format, new File(fileName));
					        
					        if (index==0){
					        	w = 200;
					    		h = 200;
					    		
					    		BufferedImage image = ImageIO.read(file);
					    		int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
					    		BufferedImage resizedImage = new BufferedImage(w, h, type);
					    		Graphics2D g = resizedImage.createGraphics();
					    		g.drawImage(image, 0, 0, w, h, null);
					    		g.dispose();
					    		g.setComposite(AlphaComposite.Src);
					    		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					    		g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
					    		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
					    		ImageIO.write(resizedImage, "jpg", new File("E:/FullScreenshot.jpg"));
					        	
					        }
					        if (index==1){
					        	
					        }

					try {
						/*
						 * Reading a Image file from file system
						 */

						
						
						/*
						 * Converting Image byte array into Base64 String 
						 */
						imageDataString = Pouin.encodeImage(Files.readAllBytes(new File(fileName).toPath()));
						
						System.out.println(imageDataString);
						
						/*
						 * Converting a Base64 String into Image byte array 
						 */

						

						
						System.out.println("Image Successfully Manipulated!");
					} catch (FileNotFoundException e) {
						System.out.println("Image not found" + e);
					} catch (IOException ioe) {
						System.out.println("Exception while reading the Image " + ioe);
					}

					/**
					* Encodes the byte array into base64 string
					* @param imageByteArray - byte array
					* @return String a {@link java.lang.String}
					*/
					

					/**
					* Decodes the base64 string into byte array
					* @param imageDataString - a {@link java.lang.String} 
					* @return byte array
					*/
							
							DataOutputStream out = new DataOutputStream(server.getOutputStream());
							out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!"); // Envoi d'un message au client.
							out.writeUTF(imageDataString + server.getLocalSocketAddress() + "\nGoodbye!");
							server.close();
						}
						catch(final SocketTimeoutException timedOut) {
							continue;
						}
						catch(final Exception ex) {
							ex.printStackTrace();
							break;
						}
					}
					serverSocket.close();
				}
				catch(final Exception ex) {
					ex.printStackTrace();
				}
			}
			
		}.start();
	}
	

}
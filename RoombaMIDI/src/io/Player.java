package io;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Player implements Runnable {
	private static final int EXTERNAL_BUFFER_SIZE = 128000;
	private static SourceDataLine line;
	private static String song;

	public static void play(String newSong){
		song = newSong;
		(new Thread(new Player())).start();
		try {Thread.sleep(1500);} catch (InterruptedException e1) {}
	}
	
	public static void kill(){
		line.stop();
		line.drain();
		line.close();
	}

	@SuppressWarnings("unused")
	@Override
	public void run() {

		if(song.split("\\.")[song.split("\\.").length-1].contains("midi"))
			song = song.substring(0, song.length()-1);

		System.out.println("Song: " + song);
					
		File soundFile = new File(song);
		AudioInputStream	audioInputStream = null;
		
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		}catch (Exception e) {
			System.out.println("\n*** SONG NOT FOUND ***");
			return;
		}
		System.out.println("\n*** NOW PLAYING " + song + " ***");

		AudioFormat	audioFormat = audioInputStream.getFormat();
		line = null;
		DataLine.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat);
		
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(audioFormat);
		}catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	
		line.start();
		
		int	nBytesRead = 0;
		byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
		while (nBytesRead != -1)
		{
			try {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (nBytesRead >= 0) {
				int	nBytesWritten = line.write(abData, 0, nBytesRead);
			}
		}
		line.drain();
		line.close();
	}
	
}

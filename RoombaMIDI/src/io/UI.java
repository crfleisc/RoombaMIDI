package io;

import java.util.Scanner;
import java.util.regex.Pattern;
import conversion.CSV_MIDI;
import conversion.CSV_iROBOT;
import conversion.AbstractConverter;
import conversion.MIDI_CSV;
import conversion.MIDI_iROBOT;

public class UI extends AbstractConverter{
	String selection = "0";
	Scanner reader;
	Pattern digit;
	
	public UI(){
		reader = new Scanner(System.in);
		getInput();
	}

	@SuppressWarnings({ "static-access", "unused" })
	private void getInput(){
		printOptions();
		selection = reader.nextLine().trim();
		if(!digit.matches("\\d", selection)){
			System.out.println("INVALID SELECTION\n");
			getInput();
			return;
		}
		
		switch(Integer.parseInt(selection)){
		
			case 1: MIDI_CSV m2c = new MIDI_CSV();
					break;
					
			case 2: CSV_MIDI c2m = new CSV_MIDI();
					break;
					
			case 3: MIDI_iROBOT m2i = new MIDI_iROBOT();
					break;
					
			case 4: CSV_iROBOT i2m = new CSV_iROBOT(null, null);
					break;
			
			case 5:	Browser.openBrowser(Browser.csv);
					getInput();
					break;
					
			case 6: Browser.openBrowser(Browser.midi);
					getInput();
					break;
					
			case 7: Browser.openBrowser(Browser.iRobot);
					getInput();
					break;
					
			case 8: listFiles(0); // 0 = only list midi files
					String song = getMidi();
					Player.play(song);
					getInput();
					return;
					
			case 9: Player.kill();
					getInput();
					return;
					
			case 0:
					System.out.println("exiting...");
					System.exit(0);
					break;
			default:
					System.out.println("INVALID SELECTION\n");
					getInput();
					return;
		}
		getInput();
	}

	private void printOptions() {
		System.out.println("\n==============================\n"
				+ "  1. MIDI -> CSV\n"
				+ "  2. CSV  -> MIDI\n"
				+ "  3. MIDI -> iROBOT SCRIPT\n"
				+ "  4. CSV  -> iROBOT SCRIPT\n"
				+ "\n  5. LEARN ABOUT CSV\n"
				+ "  6. LEARN ABOUT MIDI\n"
				+ "  7. LEARN ABOUT iROBOT\n"
				+ "\n  8. PLAY MIDI FILE\n"
				+ "  9. STOP MIDI PLAYBACK\n"
				+ "\n  0. EXIT\n"
				+ "==============================\n");
		System.out.print(" > ");
	}
}

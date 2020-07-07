package conversion;

import io.UI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

// TODO handle tempo?

public class CSV_iROBOT extends AbstractConverter{
	private BufferedReader bReader;
	private BufferedWriter bWriter;
	
	private String line;
	private String[] score;
	private int noteOnTime = 0;    // measured in 1/1536th seconds from 0, when a note starts
	private int noteOffTime = 0;   // measured in 1/1536th seconds from 0, when a note ends
	private int noteLength = 0;    // measured in 1/64th seconds, duration of a note
	private int pitch = 0;		   // [31 – 127], all others are considered rests. iRobot and MIDI use the same values to denote pitch :)
	private int numNotes = 0; 	   // tracks the # of notes in an iRobot song
	private int songLength = 0;    // measured in 1/64th seconds, duration of an iRobot song
	private int trackNum = -1;	   // used to try and predict if user has input a song with simultaneously playing notes
	private boolean multiTrackWarn = false;
	private int numSongs = 0;	   // total number of iRobot songs created
	private int totalBytes = 0;    // total size of all songs in bytes
	private double totalLength = 0;// measured in 1/64th seconds, duration of all songs
	private int totalNotes = 0;	   // total number of notes
	
	private String[] songs = new String[500]; // accounting for lengths up to Iron Butterfly - In-A-Gadda-Da-Vida
	
	public CSV_iROBOT(String inputPath, String outputPath) {
		if(inputPath == null || outputPath == null){
			do{
				listFiles(1);
				inputPath = getCSV();
				inputPath.concat(".txt");
			}while(!testInput(inputPath, "txt"));
			
			outputPath = getOutput();
			outputPath = outputPath.concat(".lisp");
		}

		testOutput(outputPath);

	    try{
	        bReader = new BufferedReader(new FileReader(inputPath));
			bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath, true)));
	        while((line = bReader.readLine()) != null) {
	        	try{ 
	        		translate(line); 
	        	} 
	        	catch(Throwable e){
	        		System.err.println("The CSV file " + inputPath + " is invalid.\n"
	        				+ " Are you certain it contains only one note playing at a time?");
	        	    bReader.close();
	        	    bWriter.close();
	        		new File(outputPath).delete();

	        		System.out.println("exiting...");
	        		e.printStackTrace();
	        		System.exit(-1); 
	        	}
	        }    
		    bReader.close();
		    bWriter.close();
		    
		    if(new File("tempCSV.txt").exists()){
		    	while(new File("tempCSV.txt").exists()){
		    		new File("tempCSV.txt").delete();
		    	}
		    }   
	    }
	    catch(FileNotFoundException e) {
	    	System.err.println( "Unable to open file '" + inputPath + "\n"
	    			+ "Please ensure you haven't entered illegal characters as path names.");
	    	try {
				bReader.close();
				bWriter.close();
			} catch (IOException e1) {}
    		new File(outputPath).delete();
	    	e.printStackTrace();
	    }
	    catch(IOException e) { 
	    	System.err.println("Error reading file "+ inputPath);
	    	e.printStackTrace();
	    }
	    
	    printStats();
	    @SuppressWarnings("unused")
		UI ui = new UI(); // quick&dirty go back to main prompt, negligible memory leak
	}
	
	
	
	
/*
  	------ SCRIPTS ------------------------------------------------------------------------------------------------
     [152] [Script Length] [Opcode 1] [Opcode 2] [Opcode 3] etc.
     A one note song is specified by four data bytes. For each additional note within a song, add two data bytes.

   	------ SONGS --------------------------------------------------------------------------------------------------
    sendCommand("(iRobot.execute-raw \"140 1 6 76 8 79 8 76 8 72 8 74 8 79 8\")");
	sendCommand("(iRobot.execute-raw \"141 1\")");	
    [140] [Song Number] [Song Length] [Note Number 1] [Note Duration 1] [Note Number 2] [Note Duration 2], etc.	
        
   - Song Number (0 – 15)
   - Song Length (1 – 16)
   - Note Number (31 – 127)
   - Note Duration (0 – 255)   increments of 1/64th of a second
   
     0     1       2          3       4      5
   Track, Time, Note_on_c, Channel, Note, Velocity
 */

	private void translate(String aLine) throws IOException {//throws IOException {
		score = aLine.split("\\,");
		if(! (score[2].toLowerCase().trim().equals("note_on_c") || score[2].toLowerCase().trim().equals("note_off_c")) ){
			try {
				bWriter.write(";".concat(aLine)+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			if((score[2].toLowerCase().trim().equals("note_on_c")) && Integer.parseInt(score[5].trim()) != 0){ // check if we're pressing a note at this time
				numNotes++;																				// A Note_on_c event with Velocity = 0 is equivalent to a Note_off_c 					
				if(numNotes > 15){ // add the lisp notation to the notes and start on a new song
					if(trackNum == -1) trackNum = Integer.parseInt(score[0]); // only check once per 16 notes
					else if(trackNum != Integer.parseInt(score[0])) multiTrackWarn = true; 
					
					songs[numSongs] = getStart().concat(songs[numSongs]).concat(getEnd());
					bWriter.write(songs[numSongs]+"\n");
					bWriter.write(getPlay());
					bWriter.write(getWait());
					
					totalNotes += 16;
					numSongs ++;
					songLength = 0;
					numNotes = 0;
				}
				noteOnTime = Integer.parseInt(score[1].trim()); 				// we wait for "note_off_c" and compare the difference to get duration
				pitch = Integer.parseInt(score[4].trim()); 						// the note to be played 
			}
			else if ((score[2].toLowerCase().trim().equals("note_off_c")) || 	// check if this is when a note is released
					((score[2].toLowerCase().trim().equals("note_on_c") && Integer.parseInt(score[5].trim()) == 0))){ 	// 
				noteOffTime = Integer.parseInt(score[1].trim());				// compare this to noteOnTime to get duration
				noteLength = (noteOffTime-noteOnTime)/24;			 			// iRobot's units of time are 24 times larger than CSV
				totalLength += noteLength;  									// 1/64th seconds
				songLength += noteLength;										// 1/64th seconds

				if(songs[numSongs]==null) 										// is first note in the song add notes to songs array
					songs[numSongs] = " " + pitch + " " + noteLength;
				else 															// otherwise concatenate to existing notes in the song
					songs[numSongs] = songs[numSongs].concat(" " + pitch + " " + noteLength);
			}
			else { System.err.println("Should never happen!"); System.exit(-1); }
		}
	}
	
	// byte code to load a song into song # <numSong> which is <numNotes> long
	private String getStart() {	
		String start = "(iRobot.execute-raw \"140 "+ numSongs + " " + numNotes;
		return start;
	}
	
	// easier than remembering how to type all that
	private String getEnd() {
		String end = "\")";
		return end;
	}
	
	// byte code to wait <songLength> "deci-seconds" (10ths of seconds)
	private String getWait(){ 
		String wait = "(iRobot.execute-raw \"155 "+ (int) Math.ceil((double)((double)songLength/64)*10) + getEnd() +"\n";
		return wait;
	}
	
	 // byte code to play song # <numSongs>
	private String getPlay() {
		String start = "(iRobot.execute-raw \"141 "+ numSongs + getEnd() + "\n";
		return start;
	}

	private void printStats() {
		String warning = "\n\n ~~~~~~~~~~~~~~~~ WARNING ~~~~~~~~~~~~~~~~\n"
				       + "  THIS TRACK LIKELY CONTAINS SIMULTANEOUSLY \n"
				       + " PLAYING NOTES AND WILL NOT PLAY ON THE iROBOT\n";
	    String success = "\n~~~~~~~~~~~~~~~~ SUCCESS ~~~~~~~~~~~~~~~~";
	   
		totalNotes += numNotes;
	    totalBytes = numSongs*4 + totalNotes - numSongs;
	    
	    if(multiTrackWarn){
	    	System.out.println(warning);
		    try { Thread.sleep(1000);} catch (InterruptedException e) {}
	    }
	    else
	    	System.out.println(success);
	    System.out.println("         " + numSongs + "        iRobot 16-note songs\n"
	    		+ "         " + totalNotes + "       individual notes\n"
	    		+ "         " + totalBytes + "       bytes\n"
	    		+ "         " + totalLength/(64) + "   seconds\n"
	    		+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}
	
}

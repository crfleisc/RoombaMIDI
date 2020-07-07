package conversion;

import io.UI;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public abstract class AbstractConverter {
	protected final String CSV2MIDI = "exe/csvmidi.exe";
	protected final String MIDI2CSV = "exe/midicsv.exe";
	
	protected Scanner reader;
	protected String inputPath;
	protected String outputPath;
	
	public AbstractConverter(){
		reader = new Scanner(System.in);
	}
	
	protected String getMidi(){
		System.out.println("\nENTER MIDI FILE NAME - or - '0' TO EXIT\n"
				+ "EG: <mySong>");
		System.out.print(" > ");
		inputPath = reader.nextLine();
		if(inputPath.equals("0")){
			System.out.println("exiting...");
			System.exit(0);
		}
		return inputPath;
	}

	protected String getCSV(){
		System.out.println("\nENTER CSV FILE PATH - or - '0' TO EXIT\n"
				+ "EG: <myCSV>");
		System.out.print(" > ");
		inputPath = reader.nextLine();
		if(inputPath.equals("0")){
			System.out.println("exiting...");
			System.exit(0);
		}
		return inputPath;
	}
	
	protected String getOutput(){
		System.out.println("\nENTER OUTPUT FILE NAME - or - '0' to EXIT\n"
				+ "EG: <mySong> or <myCSV>");
		System.out.print(" > ");
		outputPath = reader.nextLine();
		if(outputPath.equals("0")){
			System.out.println("exiting...");
			System.exit(0);
		}
		return outputPath;
	}
	
	@SuppressWarnings("unused")
	protected boolean testInput(String inputPath, String type) {
		if(!inputPath.split("\\.")[inputPath.split("\\.").length-1].contains(type))
			return false;
		try{
			File test = new File(inputPath);
		} catch (Throwable e){
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unused")
	protected void testOutput(String outputPath) {
		File test = null;
		try{
			test = new File(outputPath);
		} catch (Throwable e){
			System.err.println("Error creating file " + outputPath);
			e.printStackTrace();
		}
		if(test.exists()){
			String answer;
			do{
				System.out.println("*** WARNING ***\n"
						+ "FILE " + outputPath + " ALREADY EXISTS.\n"
						+ "OVERWRITE?   Y/N\n");
				System.out.print(" > ");
				answer = reader.nextLine();
			}while(!(answer.equals("Y") || answer.equals("y") || answer.equals("N") || answer.equals("n")));
			if(answer.equals("n") || answer.equals("N")){
				UI ui = new UI(); // quick&dirty go back to main prompt, negligible memory leak each time
			}
			test.delete();
		}
	}
	
	protected void run(String program, String input, String output){
		try {
			Process process = new ProcessBuilder(program, input, output).start();
			while(process.isAlive())
			{
				//just wait
			}
			if(process.exitValue() != 0){
				System.out.println("WARNING: Conversion process may have failed");
			}
		} catch (IOException e) {
			System.err.println("Unable to start process: " + program);
			e.printStackTrace();
		}
	}
	
	// 0 = midi, 1 = txt, 2 = both
	@SuppressWarnings("unused")
	protected void listFiles(int whichFiles){
		System.out.println("\n ***** AVAILABLE FILES *****");
		File folder = new File("./");
		File[] listOfFiles = folder.listFiles();
		if(listOfFiles != null && listOfFiles.length > 0){
			File[] temp = new File[listOfFiles.length];
			int i = 0;
			for(File file : listOfFiles){
				if(whichFiles == 2){
					if(testInput(listOfFiles[i].toString(), "mid") ||
							testInput(listOfFiles[i].toPath().toString(), "txt")){
						temp[i] = listOfFiles[i];
					}					
				}
				else if(whichFiles == 1){
					if(testInput(listOfFiles[i].toPath().toString(), "txt")){
						temp[i] = listOfFiles[i];
					}	
				}
				else if(whichFiles == 0){
					if(testInput(listOfFiles[i].toPath().toString(), "mid")){
						temp[i] = listOfFiles[i];
					}	
				}
				i++;
			}
			listOfFiles = temp;
		}
		else if(listOfFiles == null || listOfFiles.length == 0){
			System.out.println("No .txt or .mid files found in program directory.\n"
					+ "Please ensure your files are in the same directory as MOZART 9000.");
			System.out.println("exiting...");
			System.exit(0);
		}

		for(File file : listOfFiles){
			if(file != null){
				System.out.println(" - " + file.toString().substring(2, file.toString().length()));
			}
		}
	}

}

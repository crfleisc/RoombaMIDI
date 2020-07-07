package io;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Browser {
	public static String csv = "http://www.fourmilab.ch/webtools/midicsv/#midicsv.5";
	public static  String midi = "http://en.wikipedia.org/wiki/MIDI";
	public static  String iRobot = "http://en.wikipedia.org/wiki/IRobot_Create";
	
	public Browser(){
		 // TODO try to open from saved html if necessary
		 // 	File htmlFile = new File("MIDICSV  Convert MIDI File to and from CSV.html");
		 // 	Desktop.getDesktop().browse(htmlFile.toURI());
		 //     MIDICSV  Convert MIDI File to and from CSV.html
	}

	public static void openBrowser(String url){
		if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                System.out.println(url);
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                System.out.println(url);
            }
        }
	}
}
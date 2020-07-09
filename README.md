# RoombaMIDI
Converts songs between MIDI, CSV, and LISP to be played on an iRobot Create Roomba.
Also included is a simple MIDI player and links to more information about the file
types and iRobot Create.

A sample song, CSV, and LISP file is included.

Songs can be converted from {MIDI, CSV} to {MIDI, CSV, LISP}.

iRobot Create can only load a single 16 note song at a time. To extend this behaviour, 
the RoombaMIDI will:
- break the input song into 16 note segments
- calculate the length of each segment
- load all segments into memory
- start playing a song segment
- wait the length of that segment
- start playing the following segment

## Usage
Uses a simple command line text interface:
> ==============================
>  1. MIDI -> CSV
>  2. CSV  -> MIDI
>  3. MIDI -> iROBOT SCRIPT
>  4. CSV  -> iROBOT SCRIPT
>  5. LEARN ABOUT CSV
>  6. LEARN ABOUT MIDI
>  7. LEARN ABOUT iROBOT
>  8. PLAY MIDI FILE
>  9. STOP MIDI PLAYBACK
>
> ============================== 

- Songs must be single channel (one note played at a time) because the iRobot Create 
is only capable of playing one note at a time.

- Input files must be in the program's root directory to be recognized. Output files also appear
in the root directory.

## References
Uses MIDI File CSV Editing Tools by John Walker http://www.fourmilab.ch/webtools/midicsv/ to handle
conversion between MIDI and CSV. This is included in the "exe" directory.

### TODO
- [ ] Verify that the duration of song segments is being calculated correctly  
- [ ] Convert text-based menu to GUI
- [ ] Continuity as to whether file extensions need to be typed in, or allow for either case
- [ ] Investigate unused File in AbstractConverter - functionality seems fine?
- [x] Move input and output to dedicated folders
- [x] Rename "exe" directory to "utilities"
- [x] Output prompts should specify the file type being outputted. Eg. instead of "OUTPUT FILENAME", "OUTPUT CSV FILENAME".

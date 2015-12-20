# MSGIP – Mean Shift Genomic Island Preditor

MSGIP is an automated tool for the prediction of genomic islands based on mean shift algorithm. MSGIP was developed in Java and is compatible with **any operation system** with **Java Runtime Environment** installed. The usage requires only a FASTA “.fna” file, containing the complete genomic sequence of the investigated bacteria.

**Figure 1** - Initial screen for the user-friendly version of MSGIP tool. The user define the parameters described on the manuscript from Brito et al. and upload the genomic sequence in FASTA format (.fna) in order to predict all potential genomic islands based on Mean Shift algorithm methodology:

![alt tag](http://integrativebioinformatics.me/wp-content/uploads/2015/11/initial-screen.png)

**Figure 2** - Results screen for the user-friendly version of MSGIP tool. The predicted genomic islands are listed together with its genomic position on the sequence. The result can be saved and exported to a .txt file by clicking on the Save button:

![alt tag](http://integrativebioinformatics.me/wp-content/uploads/2015/11/result-screen.png)

## Download

The latest executable .jar and also a command-line version of the program can be downloaded through the link: http://integrativebioinformatics.me/msgip/ 

## Usage:

* Run the file MSGIP.jar
* Select the sequence in ".fna" format
* Select the parameters values.
* Click "Start" and wait to the finish.

## Notes: 

* If you get the error: "java.lang.OutOfMemoryError: Java heap space", please run the GUI by command line and use the parameter -Xmx<size>
(where the <size> is the desired memory size) to increase the heap size while running the MSGIP. 
Sample usage is: "java -jar -Xmx1024m MSGIP.jar" if you want to set the maximum heap size to 1024 Mb. 
* Make sure you have all the environment variables set!
* It may be necessary to open the MSGIP.jar through the command line under Linux OS or MAC OS, in order to the program runs properly. This can be done
using the following command: "java -jar MSGIP.jar".
* Due to the use of non overlapping sliding windows by MSGIP, it may be useful to fill manually the remaining of the last window, in order to allow that 
it can be considered by the method. This can be doneusing a text editor, adding a "piece" of the genome in the end of the .fna file

## Contact:

Please, feel free to contact us in case of comments, criticisms and suggestions at: msgiptool@gmail.com

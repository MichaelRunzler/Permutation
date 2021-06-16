package com.michaelRunzler.ark.Permutators;

import com.michaelRunzler.ark.Permutator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class AudioPermutator extends Permutator
{
    /**
     * Default constructor.
     */
    public AudioPermutator()
    {
        super();
        this.description = "Audio";
        this.prompt = "Input two or more paths to audio files. Each file will be permuted with other files and written " +
                "as a new file.";
    }

    @Override
    public void permuteAll(String[] inputs, File targetFolder) throws IOException
    {
        int counter = 1;
        boolean running = true;

        // Verify that all target files are valid and readable
        boolean allValid = true;
        for(String s : inputs){
            File f = new File(s);
            if(!f.exists() || !f.canRead()) {
                System.out.println("ERROR: Could not read from file " + f.getAbsolutePath() + ". Please check the path and try again.");
                allValid = false;
            }
        }

        // If not all files are readable, throw an exception to signal to the main loop that something went wrong
        if(!allValid) throw new IOException("Could not read from one or more files.");

        // If the executable hasn't already been extracted:
        String mergeExecExtPath = System.getProperty("java.io.tmpdir") + "sox" + File.separator + "sox.exe";
        if(!new File(mergeExecExtPath).exists())
        {
            // Get the local file inventory as a list
            InputStream inv = this.getClass().getClassLoader().getResourceAsStream("file-list.txt");
            if (inv == null) throw new IOException("Could not get path to resource index file.");
            File invExtPath = new File(System.getProperty("java.io.tmpdir") + File.separator + inv);
            FileUtils.copyInputStreamToFile(inv, invExtPath);
            List<String> fileList = Files.readAllLines(Paths.get(invExtPath.toURI()));

            // For each file in the inventory:
            for (String file : fileList) {
                // Get a stream that refers to the file within the JAR or classpath
                InputStream fs = this.getClass().getClassLoader().getResourceAsStream(file);
                if (fs == null) throw new IOException("Could not get path to internal resource file: " + file);

                // Extract to the temporary folder
                File fileExtPath = new File(System.getProperty("java.io.tmpdir") + File.separator + "sox" + File.separator + file);
                FileUtils.copyInputStreamToFile(fs, fileExtPath);
            }
        }

        // Sort the incoming array before permuting
        Arrays.sort(inputs);

        // Produce all permutations of the input set
        while(running)
        {
            // Construct the destination path object and ensure that it doesn't already exist
            String dstFileNameNoExt = String.format("permutations-Audio-%d", counter);
            File dst = new File(targetFolder, dstFileNameNoExt + ".mp3");
            if(dst.exists() && !dst.delete()) throw new IOException("Destination file " + dst.getAbsolutePath()
                    + " already exists and could not be deleted.");

            // Construct argument string for the executable
            String args = String.format("\"%s\" %s \"%s\"", mergeExecExtPath, concat(inputs), dst.getAbsolutePath());

            System.out.printf("Processing permutation %d...", counter);

            // Run the executable with the arguments and wait for it to terminate
            Process extProcess = Runtime.getRuntime().exec(args);
            try {
                int result = extProcess.waitFor();
                if(result != 0) throw new IOException("MP3 manipulation sub-process terminated with nonzero exit code " + result);
            } catch (InterruptedException ignored) {}
            System.out.println("done.");

            // Iterate to the next permutation
            running = nextPermutation(inputs);
            counter ++;
        }
    }

    // Concatenates an array of strings into one string, separated by spaces
    private String concat(String[] arr)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < arr.length; i++) {
            sb.append("\"");
            sb.append(arr[i]);
            sb.append("\"");
            if(i != arr.length - 1) sb.append(" ");
        }

        return sb.toString();
    }
}

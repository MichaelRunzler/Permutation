package com.michaelRunzler.ark.Permutators;

import com.michaelRunzler.ark.Permutator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

        // Produce all permutations of the input set
        while(running)
        {
            // Get a stream that refers to the merge executable within the JAR or classpath
            InputStream fs = this.getClass().getClassLoader().getResourceAsStream("mp3wrap.exe");
            if(fs == null) throw new IOException("Could not get path to internal MP3 manipulation executable.");
            String dstFileNameNoExt = String.format("permutations-Audio-%d", counter);

            // Extract merge executable to the temporary folder and get new path
            File mergeExecExtPath = new File(System.getProperty("java.io.tmpdir") + File.separator + "mp3wrap.exe");
            FileUtils.copyInputStreamToFile(fs, mergeExecExtPath);

            // Construct the destination path object and ensure that it doesn't already exist
            File dst = new File(targetFolder, dstFileNameNoExt + ".mp3");
            if(dst.exists() && !dst.delete()) throw new IOException("Destination file " + dst.getAbsolutePath()
                    + " already exists and could not be deleted.");

            // Construct argument string for the executable
            String args = String.format("%s %s %s", mergeExecExtPath, dst.getAbsolutePath(), concat(inputs));

            System.out.printf("Processing permutation %d...", counter);

            // TODO debug
            System.out.println("DEBUG/ARGS: \"" + args + "\"");

            // Run the executable with the arguments and wait for it to terminate
            Process extProcess = Runtime.getRuntime().exec(args);
            try {
                int result = extProcess.waitFor();
                if(result != 0) throw new IOException("MP3 manipulation sub-process terminated with nonzero exit code " + result);
            } catch (InterruptedException ignored) {}

            // Change the output filename to remove the auto-added "_MP3WRAP" extension
            File dst_tmp = new File(targetFolder, dstFileNameNoExt + "_MP3WRAP.mp3");
            if(!dst_tmp.renameTo(dst))
                throw new IOException("Could not complete file rename operation on file " + dst_tmp.getAbsolutePath());

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

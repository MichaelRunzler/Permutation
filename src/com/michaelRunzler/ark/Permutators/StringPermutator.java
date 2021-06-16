package com.michaelRunzler.ark.Permutators;

import com.michaelRunzler.ark.Permutator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class StringPermutator extends Permutator
{
    /**
     * Default constructor.
     */
    public StringPermutator()
    {
        super();
        this.description = "String";
        this.prompt = "Input two or more strings. Each will be permuted with other strings, but will itself be left" +
                " unchanged. Use the Character Permutator to permute the characters within the string.";
    }

    @Override
    public void permuteAll(String[] inputs, File targetFolder) throws IOException
    {
        // Write output file and ensure overwrite
        File output = new File(targetFolder, "permutations-String.txt");
        if(output.exists())
            output.delete();

        BufferedOutputStream fos;

        output.createNewFile();
        fos = new BufferedOutputStream(new FileOutputStream(output));

        // Sort the incoming array before permuting
        Arrays.sort(inputs);

        // Write each permutation to the destination file, terminated by a newline
        boolean running = true;
        while(running) {
            fos.write((concat(inputs) + "\n").getBytes());
            running = nextPermutation(inputs);
        }

        // Close out the file
        fos.flush();
        fos.close();
    }

    // Concatenates an array of strings into one string, separated by spaces
    private String concat(String[] arr)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if(i != arr.length - 1) sb.append(" ");
        }

        return sb.toString();
    }
}

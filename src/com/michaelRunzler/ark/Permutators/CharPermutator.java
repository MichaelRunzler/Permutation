package com.michaelRunzler.ark.Permutators;

import com.michaelRunzler.ark.Permutator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class CharPermutator extends Permutator
{
    /**
     * Default constructor.
     */
    public CharPermutator()
    {
        super();
        this.description = "Character";
        this.prompt = "Input one or more strings. Each will have its characters permuted, but will not be permuted " +
                "with other strings. Use the String Permutator to permute the strings with each other.";
    }

    @Override
    public void permuteAll(String[] inputs, File targetFolder) throws IOException
    {
        int counter = 1;
        // Repeat the process for each string in the input array, writing each to its own numbered file
        for(String s : inputs)
        {
            // Write output file and ensure overwrite
            File output = new File(targetFolder, String.format("permutations-Character-%d.txt", counter));
            if(output.exists())
                output.delete();

            BufferedOutputStream fos;

            output.createNewFile();
            fos = new BufferedOutputStream(new FileOutputStream(output));

            String[] str = toStringArray(s.toCharArray());

            // Sort the incoming array before permuting
            Arrays.sort(str);

            // Write each permutation to the destination file, terminated by a newline
            boolean running = true;
            while(running) {
                fos.write((concat(str) + "\n").getBytes());
                running = nextPermutation(str);
            }

            // Close out the file
            fos.flush();
            fos.close();
            counter ++;
        }
    }

    // Concatenates an array of strings into one string
    private String concat(String[] arr)
    {
        StringBuilder sb = new StringBuilder();
        for (String s : arr) sb.append(s);

        return sb.toString();
    }

    // Convert a character array into a string array for use by the nextPermutation method
    private String[] toStringArray(char[] c)
    {
        String[] strs = new String[c.length];
        for(int i = 0; i < c.length; i++)
            strs[i] = "" + c[i];

        return strs;
    }
}

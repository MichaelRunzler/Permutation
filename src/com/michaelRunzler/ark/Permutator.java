package com.michaelRunzler.ark;

import java.io.File;
import java.io.IOException;

/**
 * The superclass for all Permutator objects. Allows permutation of every possible reorganization of a given sequence
 * of objects.
 */
public abstract class Permutator
{
    // Internal strings meant to describe this object type. These should be set by all subclasses.
    public String description;
    public String prompt;

    /**
     * Default constructor.
     */
    public Permutator() {
        description = "Invalid";
        prompt = "This isn't a valid Permutator type";
    }

    /**
     * Gets all possible permutations of the provided input array's contents. The exact result of each permutation varies
     * depending on the type of Permutator object that this is being called on, but in all cases, results are written to
     * a file in binary mode. Entries in the input array are either treated as character arrays or file paths, depending
     * on Permutator type.
     * @param inputs an array of either arbitrary strings or file paths in UNIX-type format
     * @param targetFolder the destination folder to which the results should be written
     */
    public abstract void permuteAll(String[] inputs, File targetFolder) throws IOException;

    /**
     * Gets the next permutation of the provided array.
     * WARNING: If the input array is NOT in lexicographical order, the permutation algorithm will likely miss some
     * permutations due to the fact that it stops when the array is in reverse order. If the array is provided in
     * reverse order to start with, only ONE permutation will be produced. Arrays should be prepared with
     * {@link java.util.Arrays#sort(Object[])})} before starting permutation.
     * This method assumes that {@param s} is mutable; that is, it does not create a shallow copy before mutating it.
     * Repeatedly calling this method on the same array will only produce successive permutations if {@param s} is fully
     * mutable (that is, not a shallow copy or redirectable symbolic reference).
     * @param s the array to permute
     * @return {@code true} if a new permutation has been generated; {@code false} if the array is not permutable
     * (i.e zero-length or {@code null}) or all possible permutations have already been produced.
     */
    protected static boolean nextPermutation(String[] s)
    {
        boolean found = false;
        int n1 = -1;

        // for each entry, starting 1 from the end:
        for(int i = s.length - 2; i >= 0; i--) {
            // if the value of the entry in the current index is less than that of the one after (i.e the array is
            // not in reverse lexicographical order), store the index and break the loop
            if(s[i].compareTo(s[i+1]) < 0){
                n1 = i;
                found = true;
                break;
            }
        }

        // if the array is in the proper order, break, since we know we're done
        if(!found) return false;

        int n2 = -1;
        // for each character, starting at the end:
        for(int i = s.length - 1; i > n1; i--) {
            // if the value of the current entry is more than the previously found out-of-order indexed entry:
            if(s[i].compareTo(s[n1]) > 0){
                n2 = i;
                break;
            }
        }

        // swap the two entries
        String temp = s[n2];
        s[n2] = s[n1];
        s[n1] = temp;

        // reverse the sequence of entries between the first marker and the end of the string
        for(int i = 0; i < (s.length - n1 - 1)/2; i++){
            temp = s[n1 + i + 1];
            s[n1 + i + 1] = s[s.length - i - 1];
            s[s.length - i - 1] = temp;
        }

        return true;
    }
}

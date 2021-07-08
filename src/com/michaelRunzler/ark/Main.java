package com.michaelRunzler.ark;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main
{
    public static final String version = "Permutator version 2.3.3-J by Ethan Scott." +
            "\nLast updated 2021-06-07 at 22:54 PST, local revision 3d (da6d2f94)." +
            "\nPrivate use permitted under license, or under the terms of the GNU General Public License (GPL)." +
            "\nCopyright (c) 2021-2022 ARK Software. All rights reserved.\n";

    public static void main(String[] args)
    {
        System.out.println(version);
        System.out.println("Populating registry from classpath, please wait...");

        // Populate the registry
        PermutatorRegister.populateRegistry();

        // Run the main loop
        boolean isStillRunning = true;
        while(isStillRunning)
            isStillRunning = run();
    }

    private static boolean run()
    {
        System.out.println("Please select an input type from the following list.");

        // List all possible permutators and map them to numeric selection numbers
        HashMap<String, Permutator> reg = PermutatorRegister.registry;
        HashMap<Integer, Permutator> selections = new HashMap<>();
        int selNum = 1;

        for(String s : reg.keySet()) {
            System.out.printf("%d. %s\n", selNum, s);
            selections.put(selNum, reg.get(s));
            selNum ++;
        }

        // Also list an exit option
        System.out.printf("%d. %s\n", selNum, "Exit");

        // Await user input
        Scanner in = new Scanner(System.in);
        System.out.print("Input selection: ");
        boolean valid = false;
        int selection = -1;
        String input;

        // Continue trying for a valid input until we get one
        while(!valid)
        {
            input = in.nextLine();

            // Try to format as an int
            try{
                selection = Integer.parseInt(input);
            }catch (NumberFormatException e){
                System.out.print("Not a valid number; try again: ");
            }

            // Ensure bounds
            if(selection < 1 || selection > selections.size() + 1)
                System.out.print("Selection out of bounds, try again: ");
            else
                valid = true;
        }

        // Exit if we got the exit option
        if(selection == selNum) {
            System.out.println("Exiting.");
            return false;
        }

        // Now that we have a valid selection, get the target folder path
        System.out.print("Provide a path for the output permutations: ");
        valid = false;
        File dest = null;

        // Try to get a valid file path that is a directory and exists
        while(!valid)
        {
            input = in.nextLine();
            dest = new File(input);
            if(dest.exists() && dest.isDirectory()) valid = true;
            else System.out.print("Not a valid directory path; please try again: ");
        }

        // Now, grab the permutator for the selection number and get input
        String done = "q";
        String quit = "qq";

        Permutator perm = selections.get(selection);
        System.out.println("\n" + perm.prompt);
        System.out.printf("Enter %s when done or %s to quit:\n", done, quit);

        // Cyclically grab input and add it to the cache until the user quits or indicates that they are done.
        boolean finished = false;
        ArrayList<String> cache = new ArrayList<>();

        while(!finished)
        {
            input = in.nextLine();
            if(input.equals(done)) finished = true;
            else if(input.equals(quit)){
                System.out.println("Quit command received, exiting.");
                return false;
            }else cache.add(input);
        }

        // Pass the inputs to the permutator
        try {
            perm.permuteAll(cache.toArray(new String[0]), dest);
            // Confirm completion
            System.out.printf("Permutations written to \"%s\".\n\n", dest.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Encountered system I/O error while permuting:");
            e.printStackTrace();
            System.out.println();
        }

        // Wait a second for the error message (if any) to be read, then return to the main menu
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Returning to main menu...\n");
        return true;
    }
}

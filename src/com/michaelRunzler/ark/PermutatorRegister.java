package com.michaelRunzler.ark;

import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

/**
 * Stores a publicly accessible registry of all active Permutator objects for use by other classes.
 */
public class PermutatorRegister
{
    public static final HashMap<String, Permutator> registry = new HashMap<>();

    /**
     * Populates the registry from all loaded subclasses of the Permutator abstract class.
     */
    public static void populateRegistry()
    {
        // Index all subclasses of the Permutator abstract class
        Reflections rf = new Reflections();
        Set<Class<? extends Permutator>> classes = rf.getSubTypesOf(Permutator.class);

        // Iterate through each class and add it to the registry
        for(Class<? extends Permutator> c : classes)
        {
            try {
                // Call the constructor and add the resulting object (cast appropriately) to the registry
                Permutator o = c.getDeclaredConstructor().newInstance();
                registerPermutator(o);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                System.out.println("Failure indexing permutators:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a permutator object to the registry.
     * @param obj the permutator to add to the registry
     */
    public static void registerPermutator(Permutator obj) {
        registry.put(obj.description, obj);
    }
}

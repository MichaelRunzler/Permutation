package com.michaelRunzler.ark;

import core.CoreUtil.JFXUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class PermutatorUIBase extends Application
{
    @Override
    public void start(Stage primaryStage)
    {
        PermutatorUI ui = new PermutatorUI("Permutator UI Window", (int)(450 * JFXUtil.SCALE), (int)(500 * JFXUtil.SCALE), -1, -1);
        ui.display();
    }
}

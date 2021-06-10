package com.michaelRunzler.ark;

import core.CoreUtil.JFXUtil;
import core.UI.ARKManagerBase;
import core.UI.InterfaceDialogs.ARKInterfaceAlert;
import core.UI.InterfaceDialogs.ARKInterfaceDialogYN;
import core.UI.ModeLocal.ModeLocal;
import core.UI.ModeLocal.ModeSwitchController;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PermutatorUI extends ARKManagerBase
{
    public static final String version = "Permutator version 2.2.2-GUI by Ethan Scott." +
            "\nLast updated 2021-06-10 at 16:08 PST, local revision 4f (ba643a80)." +
            "\nPrivate use permitted under license, or under the terms of the GNU General Public License (GPL)." +
            "\nCopyright (c) 2021-2022 ARK Software. All rights reserved.\n";

    private static final int MODE_FILE = 2;
    private static final int MODE_STRING = 1;
    private static final int MODE_CHARACTER = 0;

    // Main node group
    private TabPane modeSelect;
    private ListView<String> items;
    private HBox mainButtons;
    private Button process;
    private Button clear;
    private ProgressIndicator loading;
    private Button info;

    // Output selection
    private HBox outputSelectContainer;
    private Button selectOutputPath;
    private TextField outputPath;

    // Input selection
    @ModeLocal(MODE_FILE)
    private HBox fileSelectContainer;
    private Button selectFile;
    private TextField filePathDisplay;
    private Button addFile;

    // String inputs
    @ModeLocal({MODE_STRING, MODE_CHARACTER})
    private HBox stringSelectContainer;
    private TextField strInput;
    private Button addStr;

    private ModeSwitchController modeController;

    PermutatorUI(String title, int width, int height, double x, double y)
    {
        super(title, width, height, x, y);

        // Set up window dimensions and location
        window.setMinHeight(height);
        window.setMinWidth(width);
        window.setResizable(true);

        if(x < 0) window.setX((Screen.getPrimary().getBounds().getWidth() / 2) - (width / 2));
        if(y < 0) window.setY((Screen.getPrimary().getBounds().getHeight() / 2) - (height / 2));

        // Set up file choice dialogs for later
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files (.mp3)", "*.mp3"));
        fc.setTitle("Select a file");

        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        dc.setTitle("Select Target Folder");

        // Initialize all mode-agnostic nodes
        items = new ListView<>();
        process = new Button("Process");
        clear = new Button("Clear List");
        loading = new ProgressIndicator();
        mainButtons = new HBox(clear, process, loading);
        info = new Button("Program Info...");

        // Grab permutators and generate tabs for each
        PermutatorRegister.populateRegistry();
        HashMap<String, Permutator> registry = PermutatorRegister.registry;

        ArrayList<Tab> tabs = new ArrayList<>();
        for(String s : registry.keySet()) {
            Tab t = new Tab(s);
            t.setTooltip(new Tooltip(registry.get(s).prompt));
            tabs.add(t);
        }

        modeSelect = new TabPane(tabs.toArray(new Tab[0]));

        // Set up mode-specific nodes
        selectOutputPath = new Button("Output Folder...");
        outputPath = new TextField();
        outputSelectContainer = new HBox(selectOutputPath, outputPath);

        selectFile = new Button("Select File...");
        filePathDisplay = new TextField();
        addFile = new Button("Add to List");
        fileSelectContainer = new HBox(selectFile, filePathDisplay, addFile);

        strInput = new TextField();
        addStr = new Button("Add to List");
        stringSelectContainer = new HBox(strInput, addStr);

        // Set up mode controller object
        try {
            modeController = new ModeSwitchController(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(4041);
        }

        final boolean[] validSelection = new boolean[1];
        validSelection[0] = true;

        // Set up listener for tab changes. The validSelection flag is necessary because of the verification dialog -
        // otherwise, the visually selected tab wouldn't match the actual mode
        modeSelect.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
        {
            if(!validSelection[0]){
                validSelection[0] = true;
                return;
            }

            if(items.getItems().size() > 0){
                boolean proceed = new ARKInterfaceDialogYN("Warning", "List items will be cleared if you switch tabs now. Continue?", "Yes", "No").display();
                if(!proceed) {
                    validSelection[0] = false;
                    modeSelect.getSelectionModel().select(oldValue.intValue());
                    return;
                }
            }

            items.getItems().clear();
            if(modeController.getCurrentMode() != newValue.intValue()) modeController.switchMode(newValue.intValue());
            validSelection[0] = true;
        });

        // Set up properties for all nodes
        modeSelect.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        modeSelect.setSide(Side.TOP);

        filePathDisplay.setEditable(true);
        filePathDisplay.setPromptText("File path...");
        strInput.setEditable(true);
        strInput.setPromptText("Enter text...");
        outputPath.setEditable(true);
        outputPath.setPromptText("Folder path...");
        loading.setProgress(0.0);

        fileSelectContainer.setSpacing(5 * JFXUtil.SCALE);
        stringSelectContainer.setSpacing(5 * JFXUtil.SCALE);
        outputSelectContainer.setSpacing(5 * JFXUtil.SCALE);
        mainButtons.setSpacing(5 * JFXUtil.SCALE);

        // Set node positions
        JFXUtil.setElementPositionInGrid(layout, fileSelectContainer, 0, 0, 1, -1);
        JFXUtil.setElementPositionInGrid(layout, stringSelectContainer, 0, 0, 1, -1);
        JFXUtil.setElementPositionInGrid(layout, items, 0, 0, 3, 2);
        JFXUtil.setElementPositionInGrid(layout, mainButtons, -1, 0, -1, 0);
        JFXUtil.setElementPositionInGrid(layout, modeSelect, 0, 0, 0, -1);
        JFXUtil.setElementPositionInGrid(layout, outputSelectContainer, 0, -1, 2, -1);
        JFXUtil.setElementPositionInGrid(layout, info, 0, -1, -1, 0);

        // Set tooltips for non-tab elements
        process.setTooltip(new Tooltip("Permute all items currently in the list"));
        clear.setTooltip(new Tooltip("Clear all items in the list. This cannot be undone!"));
        info.setTooltip(new Tooltip("Show program version and copyright information"));
        selectOutputPath.setTooltip(new Tooltip("Set where the permuted output files will go"));
        selectFile.setTooltip(new Tooltip("Choose an input file"));
        addFile.setTooltip(new Tooltip("Add the displayed file path to the list"));
        addStr.setTooltip(new Tooltip("Add the displayed string to the list"));

        // Switch mode to 1st tab - otherwise, all nodes will be visible
        modeController.switchMode(0);

        //
        // Set up action listeners for nodes
        //

        selectFile.setOnAction(e ->
        {
            File f = fc.showOpenDialog(window);
            // Ensure that a file was chosen, and remember the last directory for ease of use
            if(f != null) {
                filePathDisplay.setText(f.getAbsolutePath());
                fc.setInitialDirectory(f.getParentFile());
            }
        });

        addFile.setOnAction(e ->
        {
            // Ensure that there is text in the box before adding
            if(filePathDisplay.getText().length() > 0)
            {
                // Validate file path (this is done again later, but that doesn't show any visual warning)
                File f = new File(filePathDisplay.getText());
                if(f.exists() && f.canRead()) {
                    items.getItems().add(f.toString());
                    filePathDisplay.clear();
                } else
                    new ARKInterfaceAlert("Warning", "Selected file does not exist or could not be read. Try again.");
            }
        });

        addStr.setOnAction(e ->
        {
            // Ensure that there is text in the box before adding
            if(strInput.getText().length() > 0){
                items.getItems().add(strInput.getText());
                strInput.clear();
            }
        });

        selectOutputPath.setOnAction(e -> {
            File f = dc.showDialog(window);
            // Ensure that a file was chosen, and remember the last directory for ease of use
            if(f != null) {
                outputPath.setText(f.getAbsolutePath());
                dc.setInitialDirectory(f.getParentFile() == null ? f : f.getParentFile());
            }
        });

        clear.setOnAction(e ->{
            // Confirm first
            if(new ARKInterfaceDialogYN("Warning", "This will clear all list items. Are you sure?", "Yes", "No").display())
                items.getItems().clear();
        });

        process.setOnAction(e ->
        {
            // Validate output path (again, done later, but we need visual warnings)
            File output = new File(outputPath.getText());
            if(!output.exists() || !output.canWrite()){
                new ARKInterfaceAlert("Error", "Invalid output file path. Please specify a valid path.").display();
                return;
            }

            // Get permutator from the registry and format the item list
            String id = modeSelect.getSelectionModel().getSelectedItem().getText();
            Permutator perm = registry.get(id);
            String[] inputs = items.getItems().toArray(new String[0]);
            // Set the loading indicator so that it's obvious that the program hasn't just frozen
            loading.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            // Calculate how many permutations there will be
            int numPermutations = fac(items.getItems().size());

            // Confirm so that the user knows roughly how long to expect to wait
            if(!new ARKInterfaceDialogYN("Confirm", "Processing " + numPermutations + " permutations.", "Yes", "No").display()) {
                loading.setProgress(0.0);
                return;
            }

            // Permute
            try {
                perm.permuteAll(inputs, output);
                new ARKInterfaceAlert("Info", "Permutations succeeded.").display();
            } catch (IOException ioException) {
                new ARKInterfaceAlert("Error", "Encountered I/O error while permuting: " +
                        (ioException.getCause() == null ? ioException.getMessage() : ioException.getCause().getMessage())).display();
                ioException.printStackTrace();
            }

            // Set indicator back to 0%
            loading.setProgress(0.0);
        });

        info.setOnAction(e -> new ARKInterfaceAlert("Program Info", version).display());
    }

    private static int fac(int num)
    {
        if(num == 1) return 1;
        else if(num == 0) return 0;
        else return num * fac(num -1);
    }
}

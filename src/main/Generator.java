package main;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Generator extends DialogWrapper {

    private static String[] dataTypes = {
            "TEXT",
            "INTEGER",
            "BOOLEAN",
            "REAL",
            "NUMERIC"};

    private JPanel panel1;
    private JButton addNewRowButton;
    private JPanel columnsPanel;
    public JTextField modelPathText;
    private JButton modelBrowseButton;
    private JButton contractBrowseButton;
    public JTextField contractPathText;
    private JPanel modelPanel;
    public JTextField featureNameText;
    public JTextField crudHelpersPathText;
    private JPanel contractPanel;
    public JButton crudHelpersBrowseButton;
    public JPanel crudHelpersPanel;
    private JCheckBox readAllCheckBox;
    private JCheckBox readByCheckBox;
    private JCheckBox insertCheckBox;
    private JPanel daoPanel;
    public JTextField daoPathText;
    private JButton daoBrowseButton;
    private JPanel repositoryPanel;
    private JPanel repositoryImplPanel;
    public JTextField repositoryImplPathText;
    private JButton repositoryImplBrowseButton;
    public JTextField repositoryPathText;
    private JButton repositoryBrowseButton;
    private JPanel usecasePanel;
    public JTextField usecasePathText;
    private JButton usecaseBrowseButton;
    private JFileChooser directoryFileChooser;
    private JFileChooser directoryOrFileFileChooser;

    public List<ColumnUI> columnUIList = new ArrayList<>();

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel1;
    }

    public Generator(final Project project) {
        super(project);

        directoryFileChooser = new JFileChooser();
        directoryFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryFileChooser.setCurrentDirectory(new File(project.getBasePath()));

        directoryOrFileFileChooser = new JFileChooser();
        directoryOrFileFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        directoryOrFileFileChooser.setCurrentDirectory(new File(project.getBasePath()));
        columnsPanel.setLayout(new BoxLayout(columnsPanel, BoxLayout.Y_AXIS));
        addNewRow();
        addNewRowButton.addActionListener(e -> {
            addNewRow();
        });

        addBrowseListeners();
    }

    private void addBrowseListeners() {
        addBrowseListener(modelPanel, modelBrowseButton, modelPathText, directoryFileChooser);
        addBrowseListener(contractPanel, contractBrowseButton, contractPathText, directoryFileChooser);
        addBrowseListener(crudHelpersPanel, crudHelpersBrowseButton, crudHelpersPathText, directoryFileChooser);
        addBrowseListener(daoPanel, daoBrowseButton, daoPathText, directoryOrFileFileChooser);
        addBrowseListener(repositoryPanel, repositoryBrowseButton, repositoryPathText, directoryOrFileFileChooser);
        addBrowseListener(repositoryImplPanel, repositoryImplBrowseButton, repositoryImplPathText, directoryOrFileFileChooser);
        addBrowseListener(usecasePanel, usecaseBrowseButton, usecasePathText, directoryFileChooser);
    }

    private void addBrowseListener(JPanel panel, JButton button, JTextField textField,
                                   JFileChooser fileChooser) {
        button.addActionListener(e -> {
            int choice = fileChooser.showDialog(panel1, "Browse directory");
            if (choice != JFileChooser.APPROVE_OPTION) return;
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            panel.revalidate();
            panel.repaint();
            pack();
        });
    }

    private void addNewRow() {
        columnsPanel.add(getNewRowForDatabaseColumn());
        columnsPanel.revalidate();
        columnsPanel.repaint();
        pack();
    }

    private JPanel getNewRowForDatabaseColumn() {
        final JPanel jPanel = new JPanel(new FlowLayout());
        jPanel.add(new Label("Column name"));
        final JTextField textField = new JTextField();
        jPanel.add(textField);
        jPanel.add(new Label("Type"));
        final ComboBox<String> comboBox = new ComboBox(dataTypes);
        comboBox.setSelectedIndex(0);
        jPanel.add(comboBox);
        final JCheckBox primaryKeyCheckBox = new JCheckBox("Primary key");
        jPanel.add(primaryKeyCheckBox);
        columnUIList.add(new ColumnUI(textField, comboBox, primaryKeyCheckBox));

        return jPanel;
    }

    public MethodTypes getMethodTypes() {
        return new MethodTypes(
                readAllCheckBox.isSelected(),
                readByCheckBox.isSelected(),
                insertCheckBox.isSelected());
    }

    public class ColumnUI {
        public final JTextField nameTextField;
        public final ComboBox<String> typeComboBox;
        public final JCheckBox primaryKeyCheckBox;

        public ColumnUI(final JTextField nameTextField,
                        final ComboBox<String> typeComboBox,
                        final JCheckBox primaryKeyCheckBox) {
            this.nameTextField = nameTextField;
            this.typeComboBox = typeComboBox;
            this.primaryKeyCheckBox = primaryKeyCheckBox;
        }
    }
}

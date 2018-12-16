package main;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GenerateClassesAction extends DialogWrapper {

    public final JTextField txtReleaseNote;
    public final JPanel panelWrapper;
    public final JPanel panelWrapper2;
    public final JPanel panelWrapper3;
    public final JButton jButton;
    public final JCheckBox jCheckBox;
    public final JFileChooser jFileChooser;
    public final JTextField featureNameText;

    protected GenerateClassesAction(Project project) {
        super(project);
        txtReleaseNote = new JTextField();

        panelWrapper = new JPanel();
        panelWrapper2 = new JPanel(new FlowLayout());
        panelWrapper3 = new JPanel(new VerticalFlowLayout());
        jButton = new JButton();
        jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.setCurrentDirectory(new File(project.getBasePath()));
        jButton.setText("browse");
        jButton.addActionListener(e -> {
            panelWrapper.add(new JTextField("bok"));
            panelWrapper3.revalidate();
            panelWrapper3.repaint();
            pack();
            /*
            int choice = jFileChooser.showDialog(panelWrapper3, "Browse file");
            if (choice != JFileChooser.APPROVE_OPTION) return;
            txtReleaseNote.setText(jFileChooser.getSelectedFile().getAbsolutePath());*/
        });
        txtReleaseNote.setPreferredSize(new Dimension(200, 30));
        txtReleaseNote.setEnabled(false);
        jCheckBox = new JCheckBox();
        jCheckBox.setText("bok2");
        jCheckBox.setToolTipText("bok2");
        jCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelWrapper3.setPreferredSize(new Dimension(640, 480));
        featureNameText = new JTextField();
        featureNameText.setPreferredSize(new Dimension(200, 30));

    }

    protected JComponent createCenterPanel() {
        //panelWrapper.setLayout(new BoxLayout(panelWrapper, BoxLayout.Y_AXIS));

        panelWrapper.add(featureNameText);
        panelWrapper2.add(txtReleaseNote);
        // panelWrapper.add(jButton);
        panelWrapper2.add(jButton);
        // panelWrapper3.add(panelWrapper);
        panelWrapper3.add(panelWrapper);
        panelWrapper3.add(panelWrapper2);
        return panelWrapper3;
    }
}

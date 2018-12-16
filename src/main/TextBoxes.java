package main;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;

import static main.CreateContractFileKt.createContractFile;
import static main.CreateDaoFileKt.createOrMergeDaoFile;
import static main.CreateModelFileKt.createModelFile;
import static main.CreateModelFileKt.getDatabaseColumnList;
import static main.CreateReadAllFileKt.createReadAllFile;
import static main.CreateReadByPrimaryKeysFileKt.createReadByPrimaryKeysFile;
import static main.CreateRepositoryFileKt.createOrMergeRepositoryFile;
import static main.CreateRepositoryImplFileKt.createOrMergeRepositoryImplFile;
import static main.CreateUpdateFileKt.createUpdateFile;
import static main.CreateUseCaseClassesKt.createUseCaseFiles;

public class TextBoxes extends AnAction {
    // If you register the action from Java code, this constructor is used to set the menu item name
    // (optionally, you can specify the menu description and an icon to display next to the menu item).
    // You can omit this constructor when registering the action in the plugin.xml file.
    public TextBoxes() {
        // Set the menu item name.
        super("Text _Boxes");
        // Set the menu item name, description and icon.
        // super("Text _Boxes","Item description",IconLoader.getIcon("/Mypackage/icon.png"));
    }

    public void actionPerformed(AnActionEvent anActionEvent) {

        GenerateClassesAction generateClassesAction =
                new GenerateClassesAction(anActionEvent.getProject());
        final Generator generator = new Generator(anActionEvent.getProject());
        final JComponent component = generator.createCenterPanel();

        final DialogBuilder dialogBuilder = new DialogBuilder()
                .centerPanel(
                        //new Bok().getPanel()
                        /*generateClassesAction.createCenterPanel()*/
                        component)
                .title("neki title");
        dialogBuilder.addOkAction();

        boolean isOk = dialogBuilder.show() == DialogWrapper.OK_EXIT_CODE;

        if (isOk) {
            final String modelPath = generator.modelPathText.getText();
            final String contractPath = generator.contractPathText.getText();
            final String crudPath = generator.crudHelpersPathText.getText();
            final String daoPath = generator.daoPathText.getText();
            final String repositoryPath = generator.repositoryPathText.getText();
            final String repositoryImplPath = generator.repositoryImplPathText.getText();
            final String usecasePath = generator.usecasePathText.getText();
            final String featureName = generator.featureNameText.getText();


            Messages.showMessageDialog(anActionEvent.getProject(),
                    modelPath, "title", null);
           /* if (modelPath.endsWith(".java")) {
                mergeFile(featureName, modelPath);
            } else {*/
            createModelFile(modelPath, featureName, getDatabaseColumnList(generator.columnUIList));
            createContractFile(contractPath, featureName, getDatabaseColumnList(generator.columnUIList));
            final MethodTypes methodTypes = generator.getMethodTypes();
            if (methodTypes.getShouldReadAll()) {
                createReadAllFile(crudPath, featureName, getDatabaseColumnList(generator.columnUIList));
            }
            if (methodTypes.getShouldReadByPrimaryKeys()) {
                createReadByPrimaryKeysFile(crudPath, featureName, getDatabaseColumnList(generator.columnUIList));
            }
            if (methodTypes.getShouldUpdate()) {
                createUpdateFile(crudPath, featureName, getDatabaseColumnList(generator.columnUIList));
            }

            createOrMergeDaoFile(daoPath, featureName, getDatabaseColumnList(generator.columnUIList));
            createOrMergeRepositoryFile(repositoryPath, featureName, getDatabaseColumnList(generator.columnUIList));
            createOrMergeRepositoryImplFile(repositoryImplPath, featureName, getDatabaseColumnList(generator.columnUIList));
            createUseCaseFiles(usecasePath, featureName, getDatabaseColumnList(generator.columnUIList), methodTypes);

                /*createFile("C:\\Users\\Tino\\IdeaProjects\\TemplateGenerator\\src\\main\\Meho.txt",
                        path,
                        featureName);*/
            // }
        }

       /* Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        final String virtualFile = new DirectoryChooser(project).getSelectedDirectory().getName();
       /* final String virtualFile =
                FileChooser.chooseFile(new FileChooserDescriptor(true,false,
                false,false,false,false),project,
                null).getUrl();*/

        // Messages.showMessageDialog(project,virtualFile, "",null);

      /*  new TextFieldWithBrowseButton().addBrowseFolderListener(null,null, project,
                FileChooserDescriptorFactory.createSingleFileDescriptor());

        Messages.showInputDialog(project, )*/
        /*TreeClassChooserFactory.getInstance(project).createFileChooser("choose",
                null, null,null).showDialog();
        /*String txt = Messages.showMultilineInputDialog(project, "What is your name?",
                "Generate database layer","bok",null,null);*/
    }
}
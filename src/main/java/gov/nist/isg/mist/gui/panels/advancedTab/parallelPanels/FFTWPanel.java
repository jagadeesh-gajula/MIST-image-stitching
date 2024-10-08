// NIST-developed software is provided by NIST as a public service. You may use, copy and distribute copies of the software in any medium, provided that you keep intact this entire notice. You may improve, modify and create derivative works of the software or any portion of the software, and you may copy and distribute such modifications or works. Modified works should carry a notice stating that you changed the software and should note the date and nature of any such change. Please explicitly acknowledge the National Institute of Standards and Technology as the source of the software.

// NIST-developed software is expressly provided "AS IS." NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED, IN FACT OR ARISING BY OPERATION OF LAW, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT AND DATA ACCURACY. NIST NEITHER REPRESENTS NOR WARRANTS THAT THE OPERATION OF THE SOFTWARE WILL BE UNINTERRUPTED OR ERROR-FREE, OR THAT ANY DEFECTS WILL BE CORRECTED. NIST DOES NOT WARRANT OR MAKE ANY REPRESENTATIONS REGARDING THE USE OF THE SOFTWARE OR THE RESULTS THEREOF, INCLUDING BUT NOT LIMITED TO THE CORRECTNESS, ACCURACY, RELIABILITY, OR USEFULNESS OF THE SOFTWARE.

// You are solely responsible for determining the appropriateness of using and distributing the software and you assume all risks associated with its use, including but not limited to the risks and costs of program errors, compliance with applicable laws, damage to or loss of data, programs or equipment, and the unavailability or interruption of operation. This software is not intended to be used in any situation where a failure could cause risk of injury or damage to property. The software developed by NIST employees is not subject to copyright protection within the United States.



// ================================================================
//
// Author: tjb3
// Date: Apr 18, 2014 12:49:26 PM EST
//
// Time-stamp: <Apr 18, 2014 12:49:26 PM tjb3>
//
//
// ================================================================

package gov.nist.isg.mist.gui.panels.advancedTab.parallelPanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import gov.nist.isg.mist.gui.components.buttongroup.ButtonGroupPanel;
import gov.nist.isg.mist.gui.components.filechooser.FileChooserPanel;
import gov.nist.isg.mist.gui.components.textfield.TextFieldInputPanel;
import gov.nist.isg.mist.gui.components.textfield.textFieldModel.IntModel;
import gov.nist.isg.mist.gui.params.StitchingAppParams;
import gov.nist.isg.mist.gui.params.interfaces.GUIParamFunctions;
import gov.nist.isg.mist.lib.imagetile.fftw.FftwPlanType;
import jcuda.LibUtils;
import jcuda.LibUtils.OSType;

/**
 * Cretes a panel to select FFTW options
 *
 * @author Tim Blattner
 * @version 1.0
 */
public class FFTWPanel extends JPanel implements GUIParamFunctions {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private TextFieldInputPanel<Integer> numThreadsCPU;
  private ButtonGroupPanel fftwPlanGroup;
  private FileChooserPanel fftwLibraryPath;

  private FileChooserPanel savedPlan;
  private JCheckBox savePlanToFile = new JCheckBox("Save Plan?");
  private JCheckBox loadPlanFromFile = new JCheckBox("Load Plan?");

  /**
   * Initializes the FFTW panel
   */
  public FFTWPanel() {
    initControls();
  }

  private void initControls() {
    this.fftwPlanGroup = new ButtonGroupPanel(FftwPlanType.values(), "FFTW Plan Type");

    this.fftwLibraryPath =
        new FileChooserPanel("FFTW Library File", null, System.getProperty("user.dir") + File.separator
            + "lib" + File.separator + "fftw" + File.separator + "libfftw3.dll");

    this.savedPlan =
        new FileChooserPanel("Plan Location (or file)", null, System.getProperty("user.dir")
            + File.separator + "lib" + File.separator + "fftw" + File.separator + "fftPlans");

    int numProc = Runtime.getRuntime().availableProcessors();
    this.numThreadsCPU =
        new TextFieldInputPanel<Integer>("CPU worker threads", Integer.toString(numProc),
            new IntModel(1, numProc));

    this.savePlanToFile.setSelected(true);
    this.loadPlanFromFile.setSelected(true);
    this.fftwPlanGroup.setValue(FftwPlanType.MEASURE.toString());

    JPanel subPanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.gridy = 0;
    c.anchor = GridBagConstraints.FIRST_LINE_START;
    subPanel.add(this.numThreadsCPU, c);

    JPanel saveLoadPanel = new JPanel();
    saveLoadPanel.add(this.savePlanToFile);
    saveLoadPanel.add(this.loadPlanFromFile);

    c.gridy = 1;
    subPanel.add(this.fftwPlanGroup, c);

    c.gridy = 2;
    subPanel.add(saveLoadPanel, c);

    c.gridy = 3;
    subPanel.add(this.fftwLibraryPath, c);

    c.gridy = 4;
    subPanel.add(this.savedPlan, c);

    add(subPanel, c);
  }

  /**
   * Gets the number of CPU threads the user specified
   *
   * @return the number of CPU threads
   */
  public int getNumCPUThreads() {
    return this.numThreadsCPU.getValue();

  }

  /**
   * Gets the user specified FFTW plan type
   *
   * @return the FFTW plan type
   */
  public FftwPlanType getPlanType() {
    return FftwPlanType.valueOf(this.fftwPlanGroup.getValue().toUpperCase());
  }

  /**
   * Gets whether or not the user wishes the save the plan to file
   *
   * @return true if the user wants the plan to be saved
   */
  public boolean getSavePlanToFile() {
    return this.savePlanToFile.isSelected();
  }

  /**
   * Gets whether or not the user wishes the load the plan from file
   *
   * @return true if the user wants the plan to be loaded
   */
  public boolean getLoadPlanFromFile() {
    return this.loadPlanFromFile.isSelected();
  }

  /**
   * Gets the path for the plan that the user specified
   *
   * @return the plan path
   */
  public String getSavedPlanPath() {
    return this.savedPlan.getValue();
  }

  /**
   * Gets the FFTW library path that the user specified
   *
   * @return the path to the library
   */
  public String getFftwLibraryPath() {
    File f = this.fftwLibraryPath.getFile();

    File fParent = f.getParentFile();

    if (fParent == null)
      return ".";

    return fParent.getAbsolutePath();
  }

  /**
   * Gets the FFTW library name that the user specified
   *
   * @return the name of the library
   */
  public String getFftwLibraryName() {
    File f = this.fftwLibraryPath.getFile();
    String libraryName = f.getName();

    if (libraryName.indexOf(".") > 0)
      libraryName = libraryName.substring(0, libraryName.lastIndexOf("."));

    // If the o/s is linux
    OSType os = LibUtils.calculateOS();
    switch (os) {
      case APPLE:
      case LINUX:
        // remove lib prefix
        if (libraryName.startsWith("lib"))
          libraryName = libraryName.substring(3, libraryName.length());

        break;
      case SUN:
      case UNKNOWN:
      case WINDOWS:
        break;
      default:
        break;

    }

    return libraryName;

  }

  /**
   * Gets the FFTW library filename that the user specified
   *
   * @return the FFTW library filename
   */
  public String getFftwLibraryFileName() {
    return this.fftwLibraryPath.getFile().getName();
  }

  @Override
  public void loadParamsIntoGUI(StitchingAppParams params) {
    this.numThreadsCPU.setValue(params.getAdvancedParams().getNumCPUThreads());
    this.savedPlan.setValue(params.getAdvancedParams().getPlanPath());
    this.loadPlanFromFile.setSelected(params.getAdvancedParams().isLoadFFTWPlan());
    this.savePlanToFile.setSelected(params.getAdvancedParams().isSaveFFTWPlan());
    this.fftwPlanGroup.setValue(params.getAdvancedParams().getFftwPlanType().toString());
    this.fftwLibraryPath.setValue(params.getAdvancedParams().getFftwLibraryPath() + File.separator
        + params.getAdvancedParams().getFftwLibraryFileName());
  }

  @Override
  public boolean checkAndParseGUI(StitchingAppParams params) {
    if (checkGUIArgs()) {
      saveParamsFromGUI(params, false);
      return true;
    }
    return false;
  }

  @Override
  public boolean checkGUIArgs() {
    int val = getNumCPUThreads();

    if (val < 1)
      return false;

    FftwPlanType plantype = getPlanType();
    String planPath = getSavedPlanPath();
    String fftwLibraryPath = getFftwLibraryPath();
    String fftwLibraryName = getFftwLibraryName();

    if (plantype == null || planPath == null || fftwLibraryPath == null || fftwLibraryName == null)
      return false;

    return true;
  }

  private boolean loadingParams = false;

  @Override
  public void enableLoadingParams() {
    this.loadingParams = true;
    this.numThreadsCPU.enableIgnoreErrors();
  }

  @Override
  public void disableLoadingParams() {
    this.loadingParams = false;
    this.numThreadsCPU.disableIgnoreErrors();
  }

  @Override
  public boolean isLoadingParams() {
    return this.loadingParams;
  }

  @Override
  public void saveParamsFromGUI(StitchingAppParams params, boolean isClosing) {
    int val = getNumCPUThreads();
    FftwPlanType plantype = getPlanType();
    boolean savePlanToFile = getSavePlanToFile();
    String planPath = getSavedPlanPath();
    String fftwLibraryPath = getFftwLibraryPath();
    String fftwLibraryName = getFftwLibraryName();
    String fftwLibraryFileName = getFftwLibraryFileName();
    boolean loadPlanFromFile = getLoadPlanFromFile();

    params.getAdvancedParams().setNumCPUThreads(val);
    params.getAdvancedParams().setLoadFFTWPlan(loadPlanFromFile);
    params.getAdvancedParams().setSaveFFTWPlan(savePlanToFile);
    params.getAdvancedParams().setFftwPlanType(plantype);
    params.getAdvancedParams().setPlanPath(planPath);
    params.getAdvancedParams().setFftwLibraryPath(fftwLibraryPath);
    params.getAdvancedParams().setFftwLibraryName(fftwLibraryName);
    params.getAdvancedParams().setFftwLibraryFileName(fftwLibraryFileName);
  }

}

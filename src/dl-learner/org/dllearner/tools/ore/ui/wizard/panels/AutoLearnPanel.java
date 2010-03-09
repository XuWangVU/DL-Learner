package org.dllearner.tools.ore.ui.wizard.panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.dllearner.core.owl.NamedClass;
import org.dllearner.learningproblems.EvaluatedDescriptionClass;
import org.dllearner.tools.ore.LearningManager;
import org.dllearner.tools.ore.OREManager;
import org.dllearner.tools.ore.ui.GraphicalCoveragePanel;
import org.dllearner.tools.ore.ui.MarkableClassesTable;
import org.dllearner.tools.ore.ui.SelectableClassExpressionsTable;
import org.jdesktop.swingx.JXTitledPanel;

public class AutoLearnPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5204979906041331328L;
	
//	private ClassesTable classesTable;
	private MarkableClassesTable classesTable;
	
	private JXTitledPanel equivalentPanel;
	private JXTitledPanel superPanel;
	
	private SelectableClassExpressionsTable equivalentClassResultsTable;
	private SelectableClassExpressionsTable superClassResultsTable;
	
	private GraphicalCoveragePanel equivalentClassCoveragePanel;
	private GraphicalCoveragePanel superClassCoveragePanel;
	
	private JLabel equivalentInconsistencyLabel;
	private JLabel superInconsistencyLabel;
	
	private JButton skipButton;
	
	private final static String SUPERCLASS_PANEL_TITLE = "Superclass expressions for ";
	private final static String EQUIVALENTCLASS_PANEL_TITLE = "Equivalent class expressions for ";
	
	
	private final static String INCONSISTENCY_WARNING = "<html><font color=red>" +
														"Warning. Selected class expressions leads to an inconsistent ontology!" +
														"</font></html>";
	public AutoLearnPanel(){
		createUI();
	}
	
	private void createUI(){
		setLayout(new BorderLayout());
		
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setDividerLocation(0.2);
		
		mainSplitPane.setLeftComponent(createClassesPanel());
		mainSplitPane.setRightComponent(createResultPanel());
		
		add(mainSplitPane);
	}
	
	private JComponent createClassesPanel(){
		JXTitledPanel classesPanel = new JXTitledPanel("Classes");
		classesPanel.getContentContainer().setLayout(new BorderLayout());
		classesTable = new MarkableClassesTable();
		classesTable.setBorder(null);
		JScrollPane classesScroll = new JScrollPane(classesTable);
		classesScroll.setBorder(new MatteBorder(null));
		classesPanel.getContentContainer().add(classesScroll);
		return classesPanel;
	}
	
	private JComponent createResultPanel(){
		JPanel resultPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JSplitPane equivSubSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		equivSubSplitPane.setOneTouchExpandable(true);
		equivSubSplitPane.setDividerLocation(0.5);
		equivSubSplitPane.setResizeWeight(0.5);
		
		equivSubSplitPane.setTopComponent(createEquivalentPanel());
		equivSubSplitPane.setBottomComponent(createSuperPanel());
	
		addTableSelectionListeners();
		
		skipButton = new JButton("Next class");
		skipButton.setActionCommand("skip");
		
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 1.0;
		resultPanel.add(equivSubSplitPane,c);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		resultPanel.add(skipButton, c);
		
		return resultPanel;
	}
	
	private JComponent createEquivalentPanel(){
		GridBagConstraints c = new GridBagConstraints();
		equivalentPanel = new JXTitledPanel(EQUIVALENTCLASS_PANEL_TITLE);
		equivalentPanel.getContentContainer().setLayout(new GridBagLayout());
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		equivalentClassResultsTable = new SelectableClassExpressionsTable();
		equivalentClassResultsTable.setName("equivalent");
		equivalentPanel.getContentContainer().add(new JScrollPane(equivalentClassResultsTable), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 0;
		equivalentClassCoveragePanel = new GraphicalCoveragePanel("");
		equivalentPanel.getContentContainer().add(new JScrollPane(equivalentClassCoveragePanel), c);
		
		c.gridx = 0;
		c.gridy = 1;
		equivalentInconsistencyLabel = new JLabel(" ");
		equivalentPanel.getContentContainer().add(equivalentInconsistencyLabel, c);
		
		return equivalentPanel;
	}
	
	private JComponent createSuperPanel(){
		GridBagConstraints c = new GridBagConstraints();
		superPanel = new JXTitledPanel(SUPERCLASS_PANEL_TITLE);
		superPanel.getContentContainer().setLayout(new GridBagLayout());
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		superClassResultsTable = new SelectableClassExpressionsTable();
		superClassResultsTable.setName("super");
		superPanel.getContentContainer().add(new JScrollPane(superClassResultsTable), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 0;
		superClassCoveragePanel = new GraphicalCoveragePanel("");
		superPanel.getContentContainer().add(new JScrollPane(superClassCoveragePanel), c);
		
		c.gridx = 0;
		c.gridy = 1;
		superInconsistencyLabel = new JLabel(" ");
		superPanel.getContentContainer().add(superInconsistencyLabel, c);
		
		return superPanel;
	}
	
	public void fillClassesTable(Set<NamedClass> classes){
		classesTable.addClasses(classes);
	}
	
	public void fillSuperClassExpressionsTable(List<EvaluatedDescriptionClass> resultList){
		superClassResultsTable.addResults(resultList);
	}
	
	public void fillEquivalentClassExpressionsTable(List<EvaluatedDescriptionClass> resultList){
		equivalentClassResultsTable.addResults(resultList);
	}
	
	public void addActionListener(ActionListener aL){
		skipButton.addActionListener(aL);
	}
	
	public void resetPanel(){
		equivalentClassResultsTable.clear();
		superClassResultsTable.clear();
		equivalentClassCoveragePanel.clear();
		superClassCoveragePanel.clear();
		validate();
		repaint();
	}
	
	public void clearClassesTable(){
		classesTable.clear();
	}
	
	public void setNextButtonEnabled(boolean enabled){
		skipButton.setEnabled(enabled);
	}
	
	public List<List<EvaluatedDescriptionClass>> getSelectedDescriptions(){
		List<List<EvaluatedDescriptionClass>> selected = new ArrayList<List<EvaluatedDescriptionClass>>();
		selected.add(equivalentClassResultsTable.getSelectedDescriptions());
		selected.add(superClassResultsTable.getSelectedDescriptions());
		
		return selected;
	}
	
	private void addTableSelectionListeners(){
		equivalentClassResultsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {		
				@Override
				public void valueChanged(ListSelectionEvent e) {
					
					if (!e.getValueIsAdjusting() && equivalentClassResultsTable.getSelectedRow() >= 0){
						
						EvaluatedDescriptionClass selectedClassExpression = equivalentClassResultsTable.getSelectedValue();
						OREManager.getInstance().setNewClassDescription(selectedClassExpression);
						equivalentClassCoveragePanel.setNewClassDescription(selectedClassExpression);
						if(!selectedClassExpression.isConsistent()){
							equivalentInconsistencyLabel.setText(INCONSISTENCY_WARNING);
						} else {
							equivalentInconsistencyLabel.setText(" ");
						}
					}				
				}			
			
		});
		
		superClassResultsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				if (!e.getValueIsAdjusting() && superClassResultsTable.getSelectedRow() >= 0){
					
					EvaluatedDescriptionClass selectedClassExpression = superClassResultsTable.getSelectedValue();
					OREManager.getInstance().setNewClassDescription(selectedClassExpression);
					superClassCoveragePanel.setNewClassDescription(selectedClassExpression);
					if(!selectedClassExpression.isConsistent()){
						superInconsistencyLabel.setText(INCONSISTENCY_WARNING);
					} else {
						superInconsistencyLabel.setText(" ");
					}
				}				
			}	
		});
	}
	
	public void updateEquivalentGraphicalCoveragePanel(EvaluatedDescriptionClass desc){
		equivalentClassCoveragePanel.setNewClassDescription(desc);
	}
	
	public void updateSuperGraphicalCoveragePanel(EvaluatedDescriptionClass desc){
		superClassCoveragePanel.setNewClassDescription(desc);
	}
	
	public void setSelectedClass(int rowIndex){
		classesTable.setSelectedClass(rowIndex);
		String renderedClassName = OREManager.getInstance().getManchesterSyntaxRendering(classesTable.getSelectedClass(rowIndex));
		superPanel.setTitle(SUPERCLASS_PANEL_TITLE + renderedClassName);
		equivalentPanel.setTitle(EQUIVALENTCLASS_PANEL_TITLE + renderedClassName);
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		
		
		frame.add(new AutoLearnPanel());
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	
}

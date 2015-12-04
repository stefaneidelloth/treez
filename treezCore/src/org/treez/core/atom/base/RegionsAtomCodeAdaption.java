package org.treez.core.atom.base;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.CodeContainer;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.scripting.ScriptType;

/**
 * Extends AtomCodeAdaption to include a region (//#region XYZ ... //#end
 * region) for each child
 *
 */
public class RegionsAtomCodeAdaption extends AdjustableAtomCodeAdaption {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(RegionsAtomCodeAdaption.class);

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 * 
	 * @param atom
	 */
	public RegionsAtomCodeAdaption(AbstractAtom atom) {
		super(atom);
	}

	//#end region

	//#region METHODS

	/**
	 * Creates a code container that contains the code for all children of the
	 * atom that corresponds to this code adaption. Inserts a region for each
	 * child.
	 * 
	 * @return
	 */
	@Override
	protected CodeContainer createCodeContainerForChildAtoms() {

		//get child node adaptions
		List<TreeNodeAdaption> childNodes = atom.createTreeNodeAdaption().getChildren();

		//loop through the child nodes and create code container
		CodeContainer allChildrenCodeContainer = new CodeContainer(scriptType);
		for (TreeNodeAdaption childNode : childNodes) {

			String childName = childNode.getName();
			sysLog.debug("creating code container for child " + childName);
			Adaptable childAdaptable = childNode.getAdaptable();
			CodeAdaption childCodeAdaption = childAdaptable.createCodeAdaption(ScriptType.JAVA);

			//add region start
			allChildrenCodeContainer.extendBulkWithEmptyLine();
			String regionStartLine = "\t\t//#region " + childName.toUpperCase();
			allChildrenCodeContainer.extendBulk(regionStartLine);
			allChildrenCodeContainer.extendBulkWithEmptyLine();

			//extend with child code container			
			allChildrenCodeContainer = childCodeAdaption.buildCodeContainer(allChildrenCodeContainer,
					Optional.ofNullable(null));

			//add region end
			String regionEndLine = "\t\t//#end region";
			allChildrenCodeContainer.extendBulk(regionEndLine);
		}

		//post process the container
		allChildrenCodeContainer = postProcessAllChildrenCodeContainer(allChildrenCodeContainer);

		return allChildrenCodeContainer;
	}

	//#end region

}

package org.treez.core.atom.adjustable;

/**
 * This package contains classes for a distinct implementation of the
 * AbstractAtom: the "AdjustableAtom". 
 * 
 * The behavior of an AdjustableAtom is not hard coded but defined by 
 * an underlying model tree. That model tree itself is build with 
 * AttrinuteAtoms, see package org.treez.core.atom.attribute. 
 * So the idea is to define the element of a tree with another tree.
 * 
 * The ControlAdaption that is shown for the AdjustableAtom will be
 * build "automatically" from the AttributeAtoms of the underlying model.     
 * Therefore, atoms inheriting from the AdjustableAtom 
 * will only have to define that model tree to define the ControlAdaption. 
 * The model tree can for example be defined with a java script.
 * 
 * The values of the AttributeAtoms in the model tree can be accessed 
 * with model attribute paths.
 */

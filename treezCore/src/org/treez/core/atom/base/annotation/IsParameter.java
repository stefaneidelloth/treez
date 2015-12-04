package org.treez.core.atom.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines an annotation that is used for AbstractAtoms to define default
 * values and combo box items for their attributes. 
 * * The optional default Value of the Parameter has to be specified as String, 
 * e.g. @IsParamter(defaultValue = "hello"). 
 * * The optional combo items have to be specified as String array, 
 * e.g @IsParameter(comboItems = {"first_item", "second_item"}, defaultValue = "second_item")
 * 
 * This annotation has several impacts: 
 *
 * * If the type of the attribute is String, Integer, Float or Double and no comboItems 
 *   are specified, the attribute will be represented by a text field. 
 * * If the type of the attribute is String, Integer, Float or Double and combo box items 
 *   are specified, the attribute will be represented by a combo box.
 * * If the type of the attribute is an Enum, the attribute will be represented by a combo box.
 *   In this case the combo box items will be directly derived from the Enum and 
 *   an eventually specified string array "comboItems" will not be used/has no meaning.  *     
 * * If the type of the attribute is boolean, the attribute will be represented by a check box.
 *   The default value has to be specified as one of the case-insensitive Strings: "true", "false"
 *   (a check is performed with the upper case version of the strings).
 * * Default values and comboItems have to be specified as String values, e.g defaultValue = "VERTICAL". 
 *   This only works correctly with Enums if the toString() method of the Enum returns exactly that 
 *   String for the expected Enum constant. The transformation from text to numbers and vice versa should
 *   also be handled with care.  
 *   
 * * If this annotation is specified, the parameter will be included in the Code that is generated
 *   by the AtomCodeAdaption. In order to keep the amount of required code small, this inclusion 
 *   will only be done if the current value deviates from the default value.   
 * * The main purpose of this is annotation is to allow a comfortable generation of the ControlAdaption.
 *    
 * See the method "createControlsForAnnotatedAttributes" in AtomControlAdaption for an 
 * example how the annotation is evaluated/applied.
 * 
 * The class "IsParameters" provides some static methods that make it easier
 * to evaluate this annotation and the annotated field/class attribute. 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IsParameter {
	/**
	 * The default value for the parameter
	 */
	String defaultValue() default "";	

	/**
	 * If the type of the field is String and if these items are specified, 
	 * a combo box will be created. Example: comboItems = {"VERTICAL", "HORIZONTZAL"}
	 * @return
	 */
	String[] comboItems() default {};
	
	
}

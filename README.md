# ![alt tag](https://github.com/stefaneidelloth/treez/blob/master/treez.png) Treez

**Eclipse plugins** for the creation of **graphical user interfaces** that are **based on trees**.

Some tags that are related to Treez:

GUI, tree based, tree structure, hierarchical structure, scientific plotting, d3.js, SVG export, batch, sweep, parallel, iteration, evaluation, execution, simulation, generic, java, plugin

##Installation

Use following [**Eclipse Update Side**](http://www.vogella.com/tutorials/Eclipse/article.html#plugin_installation) and disable the option "Group items by category" to see the treez feature:

https://github.com/stefaneidelloth/treez/raw/master/treezUpdate/

If you have trouble getting Treez up and running, please create an issue ticket. 

##Getting started#

* Switch to the **Treez Perspective**.
* Click the ![alt tag](https://github.com/stefaneidelloth/treez/blob/master/treezCore/icons/root.png) **root** symbol in the tool bar of the **Treez View**. This will create a new root atom in the Treez View.
* Use the context menu of the root atom to add further tree elements. 
* Read the **Eclipse Help** for Treez: Help => Help Contents => Treez 


![alt tag](https://github.com/stefaneidelloth/treez/blob/master/Treez_Screenshot.png)

## Purpose

Treez is an open source project that provides a set of **Eclipse plugins**. The concept of Treez allows you to create custom tree based **Graphical User Interfaces** (GUIs) for many kinds of (scientific) applications.  

## Views & Editors

The heart of Treez is the so called **Treez View** which displays a tree model of the application. The **context menus** of the tree nodes provide mouse actions for editing the tree model: if you right-click on a tree node, you will see a context menu that is specific to that tree node. The context menu shows a list of tree node operations you can choose from, e.g. to add a new child node. If you left-click on a node of the tree, the properties of that tree node are shown in the so called **Treez Properties View**. 

The tree model can be **exported** to a corresponding Java code file which is shown in the Eclipse **Java Editor**. Java code can be **imported** to a corresponding tree model in the Treez View, too. 

The purpose of the so called **Treez Graphics View** is to show a graphical representation of the tree nodes. The content of the Graphics View might be a scientific chart or a block diagram or what ever you draw there. Treez provides a wrapper for the well known JavaScript library d3.js. As a result you are able to export scalable vector graphics (SVG) and to utilize the full power of d3.js for scientific plotting.  
	
## User interaction

In order to edit complex tree models, the Treez concept focuses on the **context menus of the tree nodes** in the Treez View and on **operations in the Treez Properties View**. Treez does not build up a complex main menu on the top of the application window. As already explained, a distinct context menu in the Treez View will show exactly those actions which are relevant to the corresponding tree node. This is not a new idea. Nevertheless, the decision to have small main menus/tool bars and to concentrate on the tree nodes actually has a deep impact on the work flows and the way you think about your application in Treez. 

The Treez concept allows you to edit a tree model with 

* **tree node operations** in the Treez View and controls in the Treez Properties View or to use
* the Java Editor of Eclipse to **edit Java code**, utilizing Eclipse features like code highlighting and completion.

The graphical work flow with mouse clicks in the Treez View is easy to learn for new users and the source code work flow provides an efficient option for advanced users. 

## Managing complexity

The Treez concept could be applied to create GUIs for applications from very different domains, e.g. for a simulation tool for solar cells or for a vector drawing tool. Tree structures are very well suited to **manage complexity** and therefore it does not surprise that trees are a common element in **complex applications**. Tree structures are not the one and only way to organize the underlying elements of an application. Lets nevertheless assume that trees could be applied to model the majority of applications. The file system on your computer has a tree structure. And you might already use the tree in the Package Explorer of Eclipse to manage the source files of huge software projects. 

## Atoms

The idea that an application is build from **underlying reusable building blocks** is very useful for creating and maintaining applications. In Treez those underlying building blocks are called **atoms** and atoms have several representations or **adaptations**:

* The **TreeNodeAdaption** of an atom creates the tree node that is shown in the Treez View.
* The **ControlAdaption** of the atom creates the atom control that is shown in the Treez Properties View. 
* The **CodeAdaption** of an atom creates the corresponding piece of code that is shown in the Text Editor. 
* The **GraphicsAdaption** of an atom creates the composite that is shown in the Treez Graphics View. 

The **exemplary atoms** that come with Treez are thought to model a common simulation work flow: you can use the atoms to control an external executable and perform **parameter variations**. If you want to apply the Treez concept for a completely different application, you are welcome to do so. Please **feel free to create your custom atoms**. An aim of Treez is to make the creation of new atoms as easy as possible. Treez provides a set of so called **attribute atoms** that can be used as building blocks for the creation of new atoms. 

If users of Treez create new atoms and feed them back to the Treez project, a large open source **atom library** will arise with time. The **extensible plugin structure** of Eclipse is perfectly suited for this purpose. 


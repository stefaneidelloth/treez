package org.treez.model.atom.tableImport;

import org.eclipse.swt.graphics.Image;
import org.junit.Before;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.GraphicsAdaption;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;
import org.treez.model.atom.Models;
import org.treez.model.atom.executable.Executable;

public class TableImportTest extends AbstractAbstractAtomTest {

	@Override
	@Before
	public void createTestAtom() {

		Models models = new Models("root");

		//#region EXECUTABLE

		Executable executable = models.createExecutable("executable");

		String treezExamplePath = "D:/EclipseJava/workspaceTreez/TreezExamples";
		String sqLitePath = treezExamplePath + "/resources/example.sqlite";

		TableImport tableImport = executable.createTableImport("tableImport");
		tableImport.sourceType.set("sqlite");
		tableImport.inheritSourceFilePath.set(false);
		tableImport.sourceFilePath.set(sqLitePath);
		tableImport.tableName.set("example");
		tableImport.rowLimit.set(1000);
		tableImport.resultTableModelPath.set("root.results.data.table");

		atom = tableImport;
	}

	@Override
	protected Boolean isShowingPreviewWindow() {
		return true;
	}

	@Override
	protected void checkOptainedObjects(
			String name,
			Image atomImage,
			AbstractControlAdaption controlAdaption,
			GraphicsAdaption graphicsAdaption,
			CodeAdaption codeAdaption,
			TreeNodeAdaption treeNodeAdaption) {

	}

}

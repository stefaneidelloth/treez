package org.treez.study.atom;

import java.util.List;

import org.treez.data.database.mysql.MySqlDatabase;
import org.treez.data.database.sqlite.SqLiteDatabase;
import org.treez.model.input.ModelInput;

public interface ModelInputGenerator {

	List<ModelInput> createModelInputs();

	int getNumberOfSimulations();

	void exportStudyInfoToTextFile(String filePath);

	void fillStudyInfo(SqLiteDatabase database, String tableName, String studyId);

	void fillStudyInfo(MySqlDatabase database, String schemaName, String tableName, String studyId);

}

function v = DimensionedVariable(dimensionNames,dimensionToCreate)
%This is the help for the dimensioned variables
v.names = dimensionNames;
v.exponents = zeros(size(v.names));
dimensionIndex = find(strcmp(v.names,dimensionToCreate));
v.exponents(dimensionIndex) = 1;
v.exponentsZeroTolerance = 1e-6;
v.value = 1;
v = class(v,'DimensionedVariable');
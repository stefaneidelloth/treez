function v1 = subsasgn(v1,S,v2)

% --- ONLY  v1 is a dimensioned variable ------
if(isa(v1,'DimensionedVariable') && ~isa(v2,'DimensionedVariable'))
    vOut = NaN;
    error('Unit inconsistency in assignment');
end

% --- ONLY  v2 is a dimensioned variable ------
if(~isa(v1,'DimensionedVariable') && isa(v2,'DimensionedVariable'))
    vOut = NaN;
    error('Unit inconsistency in assignment');
end

%---- BOTH v1 and v2 are dimensioned variables -----
if(max(abs(v1.exponents - v2.exponents))>v1.exponentsZeroTolerance)
    vOut = NaN;
    error('Unit inconsistency in assignment');
end


NIndexes = length(S.subs);
leftCommandString = 'v1.value(';
for(ni = 1:NIndexes)
    leftCommandString = sprintf('%sS.subs{%d},',leftCommandString,ni);
end
leftCommandString = sprintf('%s)',leftCommandString(1:end-1));
rightCommandString = 'v2.value;';
commandString = sprintf('%s = %s',leftCommandString,rightCommandString);
eval(commandString)




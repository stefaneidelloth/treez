function outIndex = gt(v1,v2)

% --- ONLY  v1 is a dimensioned variable ------
if(isa(v1,'DimensionedVariable') && ~isa(v2,'DimensionedVariable'))
    outIndex = NaN;
    error('Unit inconsistency in relational operator');
end

% --- ONLY  v2 is a dimensioned variable ------
if(~isa(v1,'DimensionedVariable') && isa(v2,'DimensionedVariable'))
    outIndex = NaN;
    error('Unit inconsistency in relational operator');
end

%---- BOTH v1 and v2 are dimensioned variables -----
if(isa(v1,'DimensionedVariable') && isa(v2,'DimensionedVariable'))
    if(max(abs(v1.exponents - v2.exponents))>v1.exponentsZeroTolerance)
        outIndex = NaN;
        error('Unit inconsistency in relational operator');
    end
    outIndex = v1.value>v2.value;
end

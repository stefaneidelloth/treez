function vOut = rdivide(v1,v2)

% --- ONLY  v1 is a dimensioned variable ------
if(isa(v1,'DimensionedVariable') && ~isa(v2,'DimensionedVariable'))
    vOut = v1;
    vOut.value = v1.value./v2;
    return
end

% --- ONLY  v2 is a dimensioned variable ------
if(~isa(v1,'DimensionedVariable') && isa(v2,'DimensionedVariable'))
    vOut = v2;
    vOut.value = v1./v2.value;
    vOut.exponents = - v2.exponents;
    return
end

%---- BOTH v1 and v2 are dimensioned variables -----
if(isa(v1,'DimensionedVariable') && isa(v2,'DimensionedVariable'))
    vOut = v1;
    vOut.value = v1.value./v2.value;
    vOut.exponents = v1.exponents - v2.exponents;
    if(max(abs(vOut.exponents))<vOut.exponentsZeroTolerance)  %  if all units cancelled, return plain numbers
        vOut = vOut.value;
    end
end

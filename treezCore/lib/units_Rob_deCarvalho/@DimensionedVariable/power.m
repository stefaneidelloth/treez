function vOut = power(v1,v2)

% --- ONLY  v1 is a dimensioned variable ------
if(isa(v1,'DimensionedVariable') && ~isa(v2,'DimensionedVariable'))
    vOut = v1;
    vOut.value = v1.value.^v2;
    if(length(v2)>1)
        error('For X.^b, b must be scalar when X is dimensioned variable')
    end
    vOut.exponents = v2*v1.exponents;
else
    vOut = NaN;
    error('Unit inconsistency in power function');
end


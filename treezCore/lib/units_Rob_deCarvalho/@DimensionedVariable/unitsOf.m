function varargout = unitsOf(v)
NDimensions = length(v.names);
nameString = '';
for(nd = 1:NDimensions)
    if(v.exponents(nd)~=0)
        [n,d] = rat(v.exponents(nd));
        if(d==1)
            nameString = sprintf('%s[%s^%g]',nameString,v.names{nd},v.exponents(nd));
        else
            nameString = sprintf('%s[%s^(%g/%g)]',nameString,v.names{nd},n,d);
        end
    end
end

value = v.value;
unitsVar = v;
unitsVar.value = 1;

if(nargout == 0)
    disp(nameString);
end

if(nargout == 1)
    varargout = {unitsVar};
end
if(nargout == 2)
    varargout = {unitsVar,nameString};
end
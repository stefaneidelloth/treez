function vOut = subsref(vIn, S)

NIndexes = length(S.subs);
commandString = 'vOut.value = vIn.value(';
for(ni = 1:NIndexes)
    commandString = sprintf('%sS.subs{%d},',commandString,ni);
end
commandString = sprintf('%s);',commandString(1:end-1));

vOut = vIn;
eval(commandString)




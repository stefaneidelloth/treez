function vOut = cumsum(v1)

vOut = v1;
vOut.value = cumsum(v1.value);

function vOut = circshift(vIn,shiftIndicator)
vOut = vIn;
vOut.value = circshift(vOut.value,shiftIndicator);
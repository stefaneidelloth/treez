REM @echo off
REM read command line arguments and assign them to variables
set inputFile=%1
set outputFile=%2

REM read input file and add numbers
set sum=0
for /f "delims=" %%x in (%inputFile%) do set /a "sum=sum+%%x"

REM write sum to output file
>%outputFile%  echo z
>>%outputFile% echo %sum%
function varargout = units
%
%--------------------------------------------------------------------------
%This function returns a struct.  Each field of the struct contains
%a new type of variable called a dimensioned variable.  A dimensioned
%variable contains both a value (any valid matlab numeric type) and 
%dimensions (e.g. mass, length, time, etc.)  Arithmatic performed on
%dimensioned variables will automatically perform dimensional analysis to
%ensure that units and dimensions are treated consistently throughout your
%code. A script with example code is included at the end of this help text.
%
%  **** IMPORTANT ***
%  Both units.m and the directory ..\@DimensionedVariable  must be placed
%  in a single directory.  This directory must be contained in the current
%  path.  A simple, but cumbersom way of accomplishing this would be to 
%  place units.m and \@DimensionedVariable in your current working
%  directory.  
%  *******************
%
%
% Creating the units struct:
%---------------------------------------------------------
%
%    u = units;        %  creates the units struct, u
%    units;            %  displays all available units
%
%
% Using the units struct:
%--------------------------------------------------------
%        %To enter a number in a given unit, MULTIPLY by the unit:
%              L = 5*u.in   % matlab automatically displays L in SI units
%
%        % To display in a desired unit, simply divide by that unit
%              L/u.ft       % displays L in ft.
%
%        % To convert between units, MULTIPLY by the starting unit, and
%        % DIVIDE by the ending unit:
%              u.mi^2/u.ft^2  %displays the number of feet^2 in one mile^2
%
%        %More complicated units can be obtained through arithmatic
%              mach1 = 340.29*u.m/u.s;  %speed of sound 
%
%           %Note... to make the speed of sound available wherever your
%           %units struct is defined, simply write:
%              u.mach1 = 340.29*u.m/u.s;   %mach1 now part of units struct
%
%
% Dimensioned Variables
%---------------------------------------------------------
%  The fields of the units struct, and hence all the variables with units 
%  in your code will be of a new data type called dimensioned variables.
%  The behavior of this new data type is controlled by the
%  DimensionedVariable class defined in the @DimensionedVariable
%  directory, but can be treated as a "black box" and should not need to be 
%  modified.
%
%  Arithmatic on dimensioned variables is exactly like that of normal
%  matlab numeric variables, but with additional constraints to enforce
%  consistent dimensional analysis throughout your work.  For example,
%  adding two variables having different dimesions will result in an error.
%
%      3*u.s + 2*u.kg   %BAD!!  This addition is not allowed.  
%  
%  Note 1:  There is no such thing as a dimensioned variable with no units.
%           If you perform operations in which the units cancel, a normal 
%           variable will be returned.  For example
%
%                  pi*u.m/u.m  % returns matlab's normal definition of pi
%
%
%  Note 2:  Many matlab function will not take dimensioned variables.  This
%           is because most non-arithmatic mathematical functions are only
%           defined for dimensionless arguments.  For Example:
%
%    t = (0:.1:1)*u.s;
%    w = (2*pi)*10*u.Hz;
%    %y = (3*u.in)*sin(t);     %BAD!!  sin not mathematically defined with dimensioned argument
%    y = (3*u.in)*sin(w.*t)    %Good.  sin function now defined.
%    
%
%
%  Two useful functions on dimensioned variables are
%    uv = unitsOf(x)                %Returns a value = 1 dimenioned variable with
%                                   % same units as x 
%
%    [uv,us] = unitsOf(x)           %Returns the the same uv as with single
%                                   %output argument, but in addition returns
%                                   %a string describing the units of x.
% 
%
%    u2num(x)                       %Converts a dimensioned variable to a normal variable.
%                                   %This is essentially a typecast to a matlab numeric 
%                                   %variable and is very useful for forcing a dimensioned
%                                   %variable to work with arbitrary matlab functions. 
%
%    for example, take t,y above:
%        %plot(t,y);                    %BAD!!    plot function not defined for 
%                                                %dimensioned variables
%                                       
%        plot(u2num(t),u2num(y));      %Good.
%
%        plot(t/u.s,y/u.cm);           %Better.  Explicitly states desired
%                                                %units in which to plot
%    
%
%        tV = u2num(t)                %example using the unitsOf function 
%        tV = tV(~isnan(tV))          %to call general matlab functions on 
%        t = tV*unitsOf(t)            %dimensioned variables. (Note that
%                                     %this is a only a demonstration. The
%                                     %isnan() function is defined for
%                                     %dimensioned variables.)
%
%
% %------  BEGIN EXAMPLE CODE --------------------------------
% %This is an example calculation that uses the units struct to calculate the
% %pressure at the bottom of a long vertically oriented pipe that is capped 
% %at the bottom and filled with oil.
% 
% u = units;
% pipeInnerDiameter = 4*u.in;     %4 inch inner diameter
% pipeHeight = 30*u.ft;           %pipe sticks 30 feet up into the air
% densityOfOil = 0.926*u.gm/u.cc; %density of oil as found on some random web site = .926 gm/cc
% pipeCrossSectionArea = pi*(pipeInnerDiameter/2)^2;  %cross sectional area of pipe bore
% volumeOfOil = pipeCrossSectionArea * pipeHeight;    %volume of oil that the pipe can hold
% pressurePipeBottom = densityOfOil * u.g * pipeHeight;  %pressure formula from physics: P = rho*g*h.
% forceOnPipeBottom = pressurePipeBottom * pipeCrossSectionArea;  %force exterted on bottom cap of the pipe.
% 
% %Note that each variable holds its value as expressed in SI units.  To
% %express these values in different units, simply divide by the desired
% %unit as shown below.
% line1 = sprintf('A %2.3g inch diameter pipe sticking %3.3g meters into the air and filled',pipeInnerDiameter/u.in, pipeHeight/u.m);
% line2 = sprintf('with %3.3g fluid ounces of oil will have a pressure at the bottom of %4.4g psi.',volumeOfOil/u.floz, pressurePipeBottom/u.psi);
% line3 = sprintf('This will cause a total force of %5.5g lbs to press on the bottom cap of the pipe.',forceOnPipeBottom/u.lbf);
% 
% textVal = sprintf('\n\n%s\n%s\n%s\n',line1,line2,line3);
% disp(textVal);
%%------  END EXAMPLE CODE --------------------------------













%------ Set up the fundamental dimensions over which to assign units -----
dimensionNames = {'m','kg','s','coul','K'};  %  Add any dimension you want, (e.g. 'dollar')
                                         %  but never delete 'm','kg','s',
                                         %  'coul', or K

NDimensions = length(dimensionNames);
clear class
for(nd = 1:NDimensions)
    command = sprintf('u.%s = DimensionedVariable(dimensionNames,''%s'');',dimensionNames{nd},dimensionNames{nd});
    eval(command);
end


%-------- Define useful units over your dimensions ------------------------------
%------- length ----
u.km = 1e3*u.m;
u.cm = 1e-2*u.m;
u.mm = 1e-3*u.m;
u.um = 1e-6*u.m;
u.nm = 1e-9*u.m;
u.ang = 1e-10*u.m;
u.in = 2.54*u.cm;
u.mil = 1e-3*u.in;
u.ft = 12*u.in;
u.yd = 3*u.ft;
u.mi = 5280*u.ft;
u.a0 = .529e-10*u.m;

%------- Volume -------
u.cc = (u.cm)^3;
u.L = 1000*u.cc;
u.mL = u.cc;
u.floz = 29.5735297*u.cc;
u.pint = 473.176475*u.cc;
u.quart = 946.35295*u.cc;
u.gal = 3.78541197*u.L;

%----- mass ---------
u.gm = 1e-3*u.kg;
u.mg = 1e-3*u.gm;
u.lb = 0.45359237*u.kg;
u.oz = (1/16)*u.lb;
u.amu = 1.66e-27*u.kg;

%---- time -------
u.ms = 1e-3*u.s;
u.us = 1e-6*u.s;
u.ns = 1e-9*u.s;
u.ps = 1e-12*u.s;
u.min = 60*u.s;
u.hr = 60*u.min;
u.day = 24*u.hr;
u.yr = 365.242199*u.day; 

%---- frequency ---- 
u.Hz = 1/u.s;
u.kHz = 1e3 *u.Hz;
u.MHz = 1e6 *u.Hz;
u.GHz = 1e9 *u.Hz;

%---- force -------
u.N = 1*u.kg*u.m/u.s^2;
u.dyne = 1e-5*u.N;
u.lbf = 4.44822*u.N;


%----- energy -----
u.J = u.N*u.m;
u.MJ = 1e6*u.J;
u.kJ = 1e3*u.J;
u.mJ = 1e-3*u.J;
u.uJ = 1e-6*u.J;
u.nJ = 1e-9*u.J;
u.eV = 1.6022e-19*u.J;
u.BTU = 1.0550559e3*u.J;
u.kWh = 3.6e6*u.J;
u.cal = 4.1868*u.J;
u.kCal = 1e3*u.cal;

%---- temperature ---
u.mK = 1e-3*u.K;
u.uK = 1e-6*u.K;
u.nK = 1e-9*u.K;

%---- pressure -----
u.Pa = u.N/u.m^2;
u.torr = 133.322*u.Pa;
u.mtorr = 1e-3*u.torr;
u.bar = 1e5*u.Pa;
u.mbar = 1e-3*u.bar;
u.atm = 1.013e5*u.Pa;
u.psi = 6.895e3*u.Pa;



%----- power --- ---
u.W = u.J/u.s;
u.MW = 1e6*u.W;
u.kW = 1e3*u.W;
u.mW = 1e-3*u.W;
u.uW = 1e-6*u.W;
u.nW = 1e-9*u.W;
u.pW = 1e-12*u.W;
u.hp = 745.69987*u.W;

%------ charge ------
u.e = 1.6022e-19*u.coul;


%------ Voltage -----
u.V = 1*u.J/u.coul;
u.kV = 1e3*u.V;
u.mV = 1e-3*u.V;
u.uV = 1e-6*u.V;

%----- Current ------
u.A = 1*u.coul/u.s;
u.mA = 1e-3*u.A;
u.uA = 1e-6*u.A;
u.nA = 1e-9*u.A;

%----magnetic field -----
u.T = 1*u.N/(u.A*u.m);
u.gauss = 1e-4*u.T;



%----fundamental constants ----
u.g = 9.80665*u.m/u.s^2;
u.kB = 1.38e-23*u.J/u.K;
u.sigma_SB = 5.670e-8 * u.W/(u.m^2 * u.K^4);
u.h = 6.626e-34 * u.J*u.s;
u.hbar = u.h/(2*pi);
u.mu_B = 9.274e-24 * u.J/u.T;
u.mu_N = 5.0507866e-27 * u.J/u.T;
u.c = 2.99792458e8*u.m/u.s;
u.eps0 = 8.8541878176204e-12* u.coul/(u.V*u.m);
u.mu0 = 1.2566370614359e-6 * u.J/(u.m*u.A^2);

% if(nargin>0)
%     displayUnits(u)
% end

if(nargout>0)
    varargout = {u};
else
    DisplayUnits(u)
end
    


%==================================================================
function DisplayUnits(u)
names = fieldnames(u);
nFields = length(names);
lastUnits = 'sldkjflskdjf';
nl = 1;
for(nf = 1:nFields)
    [crap,currentUnits] = unitsOf(u.(names{nf}));
    if(~strcmp(currentUnits,lastUnits))
        lines2Print{nl} = sprintf('\t------------------------');
        lastUnits = currentUnits;
        nl = nl + 1;
    end
    [vu,unitString] = unitsOf(u.(names{nf}));
    lines2Print{nl} = sprintf('\t%s = %g \t%s',names{nf}, u2num(u.(names{nf})), unitString   );
    nl = nl+1;
end    
    
    
display(char(lines2Print'))   
    





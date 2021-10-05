# Agent based digital twin using Java and JADE
## Overview: 
This is a Java program built using the JADE multi-agent platform. The program is meant to request data from an external program (which was provided by the lecturer) over a TCP/IP socket. The requested data is fuel sensor reading and a position reading for a tractor. The program makes use of multiple agents to transfer and display the data.

## Entry point
Launch.java

## Modules			
* The FuelReader agents and PositionReader agents requested the fuel and position readings			
* The assembler agents assembles data from the fuel and position agents related to a specific tractor.
* The dashboard and GUI agents work together to display the data

## Notes
* The JADE platform modules are required
			
			
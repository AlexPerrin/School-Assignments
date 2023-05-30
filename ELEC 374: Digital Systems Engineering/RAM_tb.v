`timescale 1ns/10ps
module RAM_tb;	
   reg clk;
   reg clr;
   reg R0out;
   reg R1out;
   reg R2out;
   reg R3out;
   reg R4out;
   reg R5out;
   reg R6out;
   reg R7out;
   reg R8out;
   reg R9out;
   reg R10out;
   reg R11out;
   reg R12out;
   reg R13out;
   reg R14out;
   reg R15out;
   reg HIout;
   reg LOout;
   reg Zhighout;
   reg Zlowout;
   reg PCout;
   reg MDRout;
   reg InPortout;
   reg Cout;
   reg R0in;
   reg R1in;
   reg R2in;
   reg R3in;
   reg R4in;
   reg R5in;
   reg R6in;
   reg R7in;
   reg R8in;
   reg R9in;
   reg R10in;
   reg R11in;
   reg R12in;
   reg R13in;
   reg R14in;
   reg R15in;
   reg HIin;
   reg LOin;
   reg Zhighin;
   reg Zlowin;
   reg PCin;
   reg MDRin;
   reg InPortin;
   reg Cin;
	reg read;
	reg MARin;
	reg wren;
	reg Yin;
	reg AND, OR, ADD, SUB, MUL, DIV, SHR, SHL, ROR, ROL, NEG, NOT;
	reg OutPortin;
	reg[31:0] in_port;
	wire[31:0] out_port;
	
	datapath test(
   clk,
   clr,
   R0out,
   R1out,
   R2out,
   R3out,
   R4out,
   R5out,
   R6out,
   R7out,
   R8out,
   R9out,
   R10out,
   R11out,
   R12out,
   R13out,
   R14out,
   R15out,
   HIout,
   LOout,
   Zhighout,
   Zlowout,
   PCout,
   MDRout,
   InPortout,
   Cout,
   R0in,
   R1in,
   R2in,
   R3in,
   R4in,
   R5in,
   R6in,
   R7in,
   R8in,
   R9in,
   R10in,
   R11in,
   R12in,
   R13in,
   R14in,
   R15in,
   HIin,
   LOin,
   Zhighin,
   Zlowin,
   PCin,
   MDRin,
   InPortin,
   Cin,
   read,
   MARin,
	wren,
	Yin,
	AND, OR, ADD, SUB, MUL, DIV, SHR, SHL, ROR, ROL, NEG, NOT,
	OutPortin,
	in_port,
	out_port
	);
	
	initial begin
		clk <= 0;
		
		#20;
		InPortin <= 1;
		in_port <= 32'b00001000;
		
		#20;
		InPortin <= 0;
		
		#20;
		InPortout <= 1;
		MARin <= 1;
		
		#20;
		InPortout <= 0;
		MARin <= 0;	
	
		#20;
		read <= 1;
		MDRin <= 1;
		
		#20;
		read <= 0;
		MDRin <= 0;
		
		#20;
		MDRout <= 1;
		R2in <= 1;
		
		#20;
		MDRout <= 0;
		R2in <= 0;
		
		#40;
	end
	
	always begin
		#10 clk = !clk;
	end
	
endmodule

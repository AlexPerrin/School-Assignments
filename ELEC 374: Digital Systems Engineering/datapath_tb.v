`timescale 1ns/10ps
module datapath_tb;
   reg clk;
	reg clr;
   reg HIout;
   reg LOout;
   reg Zhighout;
   reg Zlowout;
   reg PCout;
   reg MDRout;
   reg InPortout;
   reg Cout;
   reg HIin;
   reg LOin;
   reg Zhighin;
   reg Zlowin;
   reg PCin;
   reg MDRin;
   reg InPortin;
   reg Cin;
	reg IRin;
	reg MARin;
	reg read;
	reg wren;
	reg CONin;
	reg Yin;
	reg AND, OR, ADD, SUB, MUL, DIV, SHR, SHL, ROR, ROL, NEG, NOT, IncPC;
	reg Gra, Grb, Grc, Rin, Rout, BAout;
	reg OutPortin;
	reg[31:0] in_port;
	wire[31:0] out_port;
   
	datapath DUT(
      clk,
	   clr,
      HIout,
      LOout,
      Zhighout,
      Zlowout,
      PCout,
      MDRout,
      InPortout,
      Cout,
      HIin,
      LOin,
      Zhighin,
      Zlowin,
      PCin,
      MDRin,
      InPortin,
      Cin,
	   IRin,
	   MARin,
	   read,
	   wren,
	   CONin,
	   Yin,
	   AND, OR, ADD, SUB, MUL, DIV, SHR, SHL, ROR, ROL, NEG, NOT, IncPC,
	   Gra, Grb, Grc, Rin, Rout, BAout,
	   OutPortin,
	   in_port,
	   out_port
   );


	initial begin
		clk <= 0;
		Gra <= 0;
		Grb <= 0;
		Grc <= 0;
		Rin <= 0;
		Rout <= 0;
		BAout <= 0;
		
		#20;	//T0
		PCout <= 1;
		MARin <= 1;
		IncPC <= 1;
		Zhighin <= 1;
		
		#20; //T1
		PCout <= 0;
		MARin <= 0;
		IncPC <= 0;
		Zhighin <= 0;
		
		Zhighout <= 1;
		PCin <= 1;
		read <= 1;
		MDRin <= 1;
		
		#20; //T2
		Zhighout <= 0;
		PCin <= 0;
		read <= 0;
		MDRin <= 0;
		
		MDRout <= 1;
		IRin <= 1;
		
		#20; //T3
		MDRout <= 0;
		IRin <= 0;
		
		Gra <= 1;
		Rout <= 1;
		Cin <= 1; //ADDED needed to write C reg
		CONin <= 1;
		
		#20; //T4
		Gra <= 0;
		Rout <= 0;
		Cin <= 0;
		CONin <= 0;

		PCout <= 1;
		Yin <= 1;
		
		#20; //T5
		PCout <= 0;
		Yin <= 0;	
	
		Cout <= 1;
		//ADD <= 1; REMOVED handled by CON signal 
		Zhighin <= 1;
		
		#20; //T6
		Cout <= 0;
		//ADD <= 0;
		Zhighin <= 0;
		
		Zhighout <= 1;
		PCin <= 1;
		
		#20;
		Zhighout <= 0;
		PCin <= 1;
		
		#80;
	end

	always begin
		#10 clk = !clk;
	end
			
endmodule
	
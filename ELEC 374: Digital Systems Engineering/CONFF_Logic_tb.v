`timescale 1ns/10ps
module CONFF_Logic_tb;
	reg clk; 
	reg clr;
	reg IRin;
	reg [31:0] BusMuxOut;
	wire [31:0] IR_q;
	register IR(clk, clr, IRin, BusMuxOut, IR_q);
	
	reg CONin;
	wire CON;
	CONFF_Logic CONFF_Logic_test(IR_q[20:19], BusMuxOut, CONin, CON);
	
	initial begin
		clk <= 0;
		
		#20;
		BusMuxOut <= {5'b10010, 4'b0000, 4'b0010, 19'b0000000000000000000};
		IRin <= 1;
		
		#20;
		IRin <= 0;
		CONin <= 1;
		
		#20;
		CONin <= 0;
		BusMuxOut <= 32'b11111111;
		
		#40;
	end	
	
	always begin
		#10 clk = !clk;
	end	
	
endmodule

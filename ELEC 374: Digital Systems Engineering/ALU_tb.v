`timescale 1ns/10ps
module ALU_tb;
	reg clk; 
	reg clr;
	reg Yin;
	reg Zin;
	
	reg AND, OR, ADD, SUB, MUL, DIV, SHR, SHL, ROR, ROL, NEG, NOT;
	reg[31:0] BusMuxOut;
	wire[31:0] BusMuxOut_output;
	wire[31:0] A, C;
	
	register Y(clk, clr, Yin, BusMuxOut, A);
	register Z(clk, clr, Zin, C, BusMuxOut_output);
	ALU alu_test(AND, OR, ADD, SUB, MUL, SHR, SHL, ROR, ROL, NEG, NOT, A, BusMuxOut, C);
	
	initial begin
		clk = 1'b0;
		clr = 1'b0;
		Yin = 1'b0;
		Zin = 1'b0;
		
		#40;
		BusMuxOut <= 32'd64;
		Yin <= 1;
		
		#40;
		Yin <= 0;
		BusMuxOut <= 32'd32;
		Zin <= 1'b1;
		SUB <= 1'b1;
		
		#40;
		Zin <= 1'b0;
		SUB <= 1'b0;
		
		#80;
	
	end
	
	always begin
		#10 clk = !clk;
	end
		
endmodule

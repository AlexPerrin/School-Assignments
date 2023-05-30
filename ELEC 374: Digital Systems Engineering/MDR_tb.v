`timescale 1ns/10ps
module MDR_tb;
	reg [31:0] BusMuxOut;
	reg [31:0] Mdatain;
	reg read;
	wire [31:0] MDMux_out;
	MDMux MDMux_t(BusMuxOut, Mdatain, read, MDMux_out);
	
	reg clk;
	reg clr;
	reg enable;
	wire [31:0] BusMuxIn_MDR;
	register MDR(clk, clr, enable, MDMux_out, BusMuxIn_MDR);
	
	initial begin
		clk <= 0;
		BusMuxOut <= 32'd0;
		Mdatain <= ~32'd0;
		
		#20;
		read = 0;
		enable <= 1;
		
		#20;
		read = 1;
		
		#20;
		clr <= 1;
		enable <= 0;
		
		#100;
	end
	
	always begin
		#10 clk = !clk;
	end	
	
endmodule 
`timescale 1ns/10ps
module mux_32to1_tb;
	reg [31:0] BusMuxIn_R0;
	reg [31:0] BusMuxIn_R1;
	reg [31:0] BusMuxIn_R2;
	reg [31:0] BusMuxIn_R3;
	reg [31:0] BusMuxIn_R4;
	reg [31:0] BusMuxIn_R5;
	reg [31:0] BusMuxIn_R6;
	reg [31:0] BusMuxIn_R7;
	reg [31:0] BusMuxIn_R8;
	reg [31:0] BusMuxIn_R9;
	reg [31:0] BusMuxIn_R10;
	reg [31:0] BusMuxIn_R11;
	reg [31:0] BusMuxIn_R12;
	reg [31:0] BusMuxIn_R13;
	reg [31:0] BusMuxIn_R14;
	reg [31:0] BusMuxIn_R15;
	reg [31:0] BusMuxIn_HI;
	reg [31:0] BusMuxIn_LO;
	reg [31:0] BusMuxIn_Zhigh;
	reg [31:0] BusMuxIn_Zlow;
	reg [31:0] BusMuxIn_PC;
	reg [31:0] BusMuxIn_MDR;	
	reg [31:0] BusMuxIn_InPort;	
	reg [31:0] C_sign_extended;
	reg [4:0] mux_select;
	wire [31:0] BusMuxOut;

    mux_32to1 mux(
		BusMuxIn_R0,
		BusMuxIn_R1,
		BusMuxIn_R2,
		BusMuxIn_R3,
		BusMuxIn_R4,
		BusMuxIn_R5,
		BusMuxIn_R6,
		BusMuxIn_R7,
		BusMuxIn_R8,
		BusMuxIn_R9,
		BusMuxIn_R10,
		BusMuxIn_R11,
		BusMuxIn_R12,
		BusMuxIn_R13,
		BusMuxIn_R14,
		BusMuxIn_R15,
		BusMuxIn_HI,
		BusMuxIn_LO,
		BusMuxIn_Zhigh,
		BusMuxIn_Zlow,
		BusMuxIn_PC,
		BusMuxIn_MDR,
		BusMuxIn_InPort,
		C_sign_extended,
		mux_select,
		BusMuxOut
		);
		
	initial begin
		#20;
		BusMuxIn_R0 <= ~32'd0;
		mux_select <= 0;
		
		#20;
		mux_select <= 1;
		
		#20;
		BusMuxIn_R1 <= ~32'd0;
		
		#100;
	end
	
endmodule

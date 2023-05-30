module datapath(
   input wire clk,
	input wire clr,
   input wire HIout,
   input wire LOout,
   input wire Zhighout,
   input wire Zlowout,
   input wire PCout,
   input wire MDRout,
   input wire InPortout,
   input wire Cout,
   input wire HIin,
   input wire LOin,
   input wire Zhighin,
   input wire Zlowin,
   input wire PCin,
   input wire MDRin,
   input wire InPortin,
   input wire Cin,
	input wire IRin,
	input wire MARin,
	input wire read,
	input wire wren,
	input wire CONin,
	input wire Yin,
	input wire AND, OR, ADD, SUB, MUL, DIV, SHR, SHL, ROR, ROL, NEG, NOT, IncPC,
	input wire Gra, Grb, Grc, Rin, Rout, BAout,
	input wire OutPortin,
	input wire[31:0] in_port,
	output wire[31:0] out_port
   );

	//Bus Enconder
	wire R0out;
   wire R1out;
   wire R2out;
   wire R3out;
   wire R4out;
   wire R5out;
   wire R6out;
   wire R7out;
   wire R8out;
   wire R9out;
   wire R10out;
   wire R11out;
   wire R12out;
   wire R13out;
   wire R14out;
   wire R15out;
	wire [4:0] encoder_out;
	encoder_32to5 encoder(
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
      encoder_out
      );
	
	//Bus Mux	
	wire [31:0] BusMuxIn_R0;
	wire [31:0] BusMuxIn_R1;
	wire [31:0] BusMuxIn_R2;
	wire [31:0] BusMuxIn_R3;
	wire [31:0] BusMuxIn_R4;
	wire [31:0] BusMuxIn_R5;
	wire [31:0] BusMuxIn_R6;
	wire [31:0] BusMuxIn_R7;
	wire [31:0] BusMuxIn_R8;
	wire [31:0] BusMuxIn_R9;
	wire [31:0] BusMuxIn_R10;
	wire [31:0] BusMuxIn_R11;
	wire [31:0] BusMuxIn_R12;
	wire [31:0] BusMuxIn_R13;
	wire [31:0] BusMuxIn_R14;
	wire [31:0] BusMuxIn_R15;
	wire [31:0] BusMuxIn_HI;
	wire [31:0] BusMuxIn_LO;
	wire [31:0] BusMuxIn_Zhigh;
	wire [31:0] BusMuxIn_Zlow;
	wire [31:0] BusMuxIn_PC;
	wire [31:0] BusMuxIn_MDR;	
	wire [31:0] BusMuxIn_InPort;	
	wire [31:0] C_sign_extended;
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
		encoder_out,
		BusMuxOut
		);

	//MDR	
	wire [31:0] Mdatain;
	wire [31:0] MDMux_out;
	MDMux MDMux_t(BusMuxOut, Mdatain, read, MDMux_out);
	register MDR(clk, clr, MDRin, MDMux_out, BusMuxIn_MDR);
	
	//MAR
	wire[8:0] Address;
	mar MAR(clk, clr, MARin, BusMuxOut, Address);
	
	//Select and Encode Logic
	wire[31:0] instruction;
	wire[31:0] C_sign_extended_in;
	wire R0in;
	wire R1in;
   wire R2in;
   wire R3in;
   wire R4in;
   wire R5in;
   wire R6in;
   wire R7in;
   wire R8in;
   wire R9in;
   wire R10in;
   wire R11in;
   wire R12in;
   wire R13in;
   wire R14in;
   wire R15in;
	select_encode_logic Select_and_Decode_Logic(
      instruction,
	   Gra, Grb, Grc, Rin, Rout, BAout,
	   C_sign_extended_in,
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
      R15in
   );
	
	//R0 Register
	wire[31:0] R0_q;
	register R0(clk, clr, R0in, BusMuxOut, R0_q);
	assign BusMuxIn_R0 = {32{!BAout}} & R0_q;
	
	//General Registers
	register R1(clk, clr, R1in, BusMuxOut, BusMuxIn_R1);
	register R2(clk, clr, R2in, BusMuxOut, BusMuxIn_R2);
	register R3(clk, clr, R3in, BusMuxOut, BusMuxIn_R3);
	register R4(clk, clr, R4in, BusMuxOut, BusMuxIn_R4);
	register R5(clk, clr, R5in, BusMuxOut, BusMuxIn_R5);
	register R6(clk, clr, R6in, BusMuxOut, BusMuxIn_R6);
	register R7(clk, clr, R7in, BusMuxOut, BusMuxIn_R7);
	register R8(clk, clr, R8in, BusMuxOut, BusMuxIn_R8);
	register R9(clk, clr, R9in, BusMuxOut, BusMuxIn_R9);
	register R10(clk, clr, R10in, BusMuxOut, BusMuxIn_R10);
	register R11(clk, clr, R11in, BusMuxOut, BusMuxIn_R11);
	register R12(clk, clr, R12in, BusMuxOut, BusMuxIn_R12);
	register R13(clk, clr, R13in, BusMuxOut, BusMuxIn_R13);
	register R14(clk, clr, R14in, BusMuxOut, BusMuxIn_R14);
	register R15(clk, clr, R15in, BusMuxOut, BusMuxIn_R15);
	
	//Instruction Registers
	register PC(clk, clr, PCin, BusMuxOut, BusMuxIn_PC);
	register IR(clk, clr, IRin, BusMuxOut, instruction);
	
	//I/O Registers
	register InPort(clk, clr, InPortin, in_port, BusMuxIn_InPort);
	register OutPort(clk, clr, OutPortin, BusMuxOut, out_port);
	
	//CONFF Logic
	wire CON;
	CONFF_Logic CONFFLogic(clk, instruction[20:19], BusMuxOut, CONin, CON);
	
	//ALU
	wire[31:0] A, C;
	register Y(clk, clr, Yin, BusMuxOut, A);
	ALU alu(AND, OR, ADD, SUB, MUL, DIV, SHR, SHL, ROR, ROL, NEG, NOT, IncPC, CON, A, BusMuxOut, C);
	
	//ALU Registers
	register C_extended(clk, clr, Cin, C_sign_extended_in, C_sign_extended);
	register HI(clk, clr, HIin, BusMuxOut, BusMuxIn_HI);
	register_with_init_value LO(clk, clr, LOin, BusMuxOut, BusMuxIn_LO);
	register Zhigh(clk, clr, Zhighin, C, BusMuxIn_Zhigh);
	register Zlow(clk, clr, Zlowin, BusMuxOut, BusMuxIn_Zlow);

	
	//RAM
	ram	ram_inst (
	.address ( Address ),
	.clock ( clk ),
	.data ( BusMuxIn_MDR ),
	.wren ( wren ),
	.q ( Mdatain )
	);
	
endmodule

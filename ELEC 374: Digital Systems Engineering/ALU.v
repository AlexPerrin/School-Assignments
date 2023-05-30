module ALU(
	input wire AND, OR, ADD, SUB, MUL, DIV, SHR, SHL, ROR, ROL, NEG, NOT, IncPC, CON,
	input wire [31:0] A, BusMuxOut,
	output reg[31:0] C
	);
	
	reg[31:0] B_twoscomp;
	wire[31:0] B_mux_out;
	mux_2to1 B_mux(BusMuxOut, B_twoscomp, SUB, B_mux_out);
	
	wire [31:0] adder_out;
	RCAdd adder_32bit(A, B_mux_out, adder_out);

	always@(*) begin
		
		if(AND) begin
			C <= A & B_mux_out;
		end
		
		if(OR) begin
			C <= A | B_mux_out;
		end		
		
		if(ADD) begin
			C <= adder_out;
		end
		
		if(SUB) begin
			B_twoscomp <= ~BusMuxOut + 1'b1;
			C <= adder_out;
		end		
		
		if(MUL) begin
			C <= A * B_mux_out;
		end
		
		if(DIV) begin
			C <= A / B_mux_out;
		end
		
		if(SHR) begin
			C <= A >> B_mux_out;
		end
		
		if(SHL) begin
			C <= A << B_mux_out;
		end
		
		if(NEG) begin
			C <= ~B_mux_out + 1'b1;
		end
	
		if(NOT) begin
			C <= ~B_mux_out;
		end	
	
		if(IncPC) begin
			C <= B_mux_out + 1'b1;
		end
		
		if(CON) begin
			C <= adder_out + 1'b1;
		end
		
	end
	
endmodule

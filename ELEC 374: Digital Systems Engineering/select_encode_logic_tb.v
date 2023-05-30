`timescale 1ns/10ps
module select_encode_logic_tb;
	reg[31:0] instruction;
	reg Gra, Grb, Grc, Rin, Rout, BAout;
	wire[31:0] C_sign_extended;
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

   select_encode_logic tb(
      instruction,
	   Gra, Grb, Grc, Rin, Rout, BAout,
	   C_sign_extended,
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

	integer i;
	
	initial begin
		Gra <= 1;
		Grb <= 0;
		Grc <= 0;
		Rin <= 0;
		Rout <= 1;
		BAout <= 0;

		
		for(i = 4'b0000; i <= 4'b1111; i = i + 4'b0001) begin
			instruction <= {5'b00000, i, 4'b0000, 4'b0000, 15'b000000000000000};
			#10;
		end
		
		Rout <= 0;
		Rin <= 1;
		
		for(i = 4'b0000; i <= 4'b1111; i = i + 4'b0001) begin
			instruction <= {5'b00000, i, 4'b0000, 4'b0000, 15'b000000000000000};
			#10;
		end
		
	end
		
endmodule

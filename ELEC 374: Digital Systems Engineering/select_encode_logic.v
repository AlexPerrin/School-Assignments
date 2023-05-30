module select_encode_logic (
	input wire[31:0] instruction,
	input wire Gra, Grb, Grc, Rin, Rout, BAout,
	output reg[31:0] C_sign_extended,
   output reg R0out, 
   output reg R1out,
   output reg R2out,
   output reg R3out,
   output reg R4out,
   output reg R5out,
   output reg R6out,
   output reg R7out,
   output reg R8out,
   output reg R9out,
   output reg R10out,
   output reg R11out,
   output reg R12out,
   output reg R13out,
   output reg R14out,
   output reg R15out,
   output reg R0in,
   output reg R1in,
   output reg R2in,
   output reg R3in,
   output reg R4in,
   output reg R5in,
   output reg R6in,
   output reg R7in,
   output reg R8in,
   output reg R9in,
   output reg R10in,
   output reg R11in,
   output reg R12in,
   output reg R13in,
   output reg R14in,
   output reg R15in
	);
	
	reg[3:0] decoder_in;
	wire[15:0] decoder_out;
	decoder_4_to_16 decoded(decoder_in, decoder_out);
	
	always@(*) begin
		 decoder_in <= (instruction[26:23]&{4{Gra}}) | (instruction[22:19]&{4{Grb}}) | (instruction[18:15]&{4{Grc}});
	    C_sign_extended <= {{13{instruction[18]}},instruction[18:0]};
	    R0in <= decoder_out[0] & Rin;
	    R1in <= decoder_out[1] & Rin;
	    R2in <= decoder_out[2] & Rin;
	    R3in <= decoder_out[3] & Rin;
	    R4in <= decoder_out[4] & Rin;
	    R5in <= decoder_out[5] & Rin;
	    R6in <= decoder_out[6] & Rin;
	    R7in <= decoder_out[7] & Rin;
	    R8in <= decoder_out[8] & Rin;
	    R9in <= decoder_out[9] & Rin;
	    R10in <= decoder_out[10] & Rin;
	    R11in <= decoder_out[11] & Rin;
	    R12in <= decoder_out[12] & Rin;
	    R13in <= decoder_out[13] & Rin;
	    R14in <= decoder_out[14] & Rin;
	    R15in <= decoder_out[15] & Rin;
	    R0out <= decoder_out[0] & (Rout | BAout);
	    R1out <= decoder_out[1] & (Rout | BAout);
	    R2out <= decoder_out[2] & (Rout | BAout);
	    R3out <= decoder_out[3] & (Rout | BAout);
	    R4out <= decoder_out[4] & (Rout | BAout);
	    R5out <= decoder_out[5] & (Rout | BAout);
	    R6out <= decoder_out[6] & (Rout | BAout);
	    R7out <= decoder_out[7] & (Rout | BAout);
	    R8out <= decoder_out[8] & (Rout | BAout);
	    R9out <= decoder_out[9] & (Rout | BAout);
	    R10out <= decoder_out[10] & (Rout | BAout);
	    R11out <= decoder_out[11] & (Rout | BAout);
	    R12out <= decoder_out[12] & (Rout | BAout);
	    R13out <= decoder_out[13] & (Rout | BAout);
	    R14out <= decoder_out[14] & (Rout | BAout);
	    R15out <= decoder_out[15] & (Rout | BAout);	
	end
	
endmodule

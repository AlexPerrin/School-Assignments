module CONFF_Logic (
	input wire clk,
	input wire[1:0] c2,
	input wire[31:0] BusMuxOut,
	input wire CONin,
	output reg CON
	);
	
	wire[3:0] decoder_out;
	decoder_2_to_4 CONFF_Decoder(c2, decoder_out);
	
	reg CON_flag;
	wire CON_out;
	register_CON reg_CON(clk, CONin, CON_flag, CON_out);
	
	reg brzr;	//zero
	reg brnz;	//not zero
	reg brpl;	//positive
	reg brmi;	//negative
	
	always@(*) begin
		brzr <= (BusMuxOut == 32'd0) ? 1'b1 : 1'b0;
		brnz <= (BusMuxOut != 32'd0) ? 1'b1 : 1'b0;
		brpl <= (BusMuxOut[31] == 0) ? 1'b1 : 1'b0;
		brmi <= (BusMuxOut[31] != 0) ? 1'b1 : 1'b0;
		CON_flag <= (decoder_out[0] & brzr) | (decoder_out[1] & brnz) | (decoder_out[2] & brpl) | (decoder_out[3] & brmi);
		CON <= CON_out;
	end

endmodule

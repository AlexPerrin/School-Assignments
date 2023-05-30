// File Name:   RCAdd_tb.v
`timescale 1ns/10ps
module RCAdd_tb;
	reg [31:0] in_a, in_b;
	wire [31:0] out_res_rc;

initial
	begin
		in_a <= 32'd0;
		in_b <= 32'd0;
		#300	in_a <= 32'd1024;
				in_b <= 32'd21;
	end
	
RCAdd rc_adder(in_a, in_b, out_res_rc);

endmodule 
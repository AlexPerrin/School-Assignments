`timescale 1ns/10ps
module register_tb;
	reg clk; 
	reg clr;
	reg enable;
	reg [31:0] d;
	wire [31:0] q;
	register r0(clk, clr, enable, d, q);
	
	initial begin
		clk = 1'b0;
		clr = 1'b0;
		enable = 1'b0;
		d = 32'd0;
		
		#20;
		d <= ~32'd0;
		enable <= 1'b1;
					
		#20;
		clr <= 1'b1;
		d <= 32'd0;
			
		#20;
		clr <= 1'b0;
		
		#20;
	end
	
	always begin
		#10 clk = !clk;
	end
		
endmodule

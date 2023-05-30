module register_with_init_value(
	input wire clk,
	input wire clr, 
	input wire enable,
	input wire [31:0] d,
	output reg [31:0] q
	);

	initial q = 32'b11111111;
	
	always@(clk) 
	begin
		if(enable) begin
			q[31:0] <= d[31:0];
			if (clr) begin
				q[31:0] <= 32'b0;
			end
		end
	end
	
endmodule

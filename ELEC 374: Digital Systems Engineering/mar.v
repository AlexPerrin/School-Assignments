module mar(
	input wire clk,
	input wire clr, 
	input wire enable,
	input wire [31:0] d,
	output reg [8:0] q
	);

	always@(clk) 
	begin
		if(enable) begin
			q[8:0] <= d[8:0];
			if (clr) begin
				q[8:0] <= 9'b0;
			end
		end
	end
	
endmodule

module register_CON(
	input wire clk,
	input wire CONin,
	input wire d,
	output reg q
	);
	
	initial q = 0;

	always@(clk) 
	begin
		if(CONin) begin
			q <= d;
		end
	end
	
endmodule

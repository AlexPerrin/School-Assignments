module mux_2to1(
	input wire [31:0] a, b,
	input wire mux_select,
	output reg [31:0] c
	);
		
	always@(*) begin
		if(mux_select) begin
        	c <= b;
		end else begin
        	c <= a;
		end
   end
endmodule

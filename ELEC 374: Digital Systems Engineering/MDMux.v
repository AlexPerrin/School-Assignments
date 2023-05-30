module MDMux (
	input wire [31:0] BusMuxOut,
	input wire [31:0] Mdatain,
	input wire read,
	output reg [31:0] MDMux_out
	);

	always@(*)begin
		if (read) begin
			MDMux_out[31:0] <= Mdatain[31:0];
		end
		else begin
			MDMux_out[31:0] <= BusMuxOut[31:0];
		end
	end
endmodule

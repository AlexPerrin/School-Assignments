module decoder_2_to_4(
	input wire [1:0] decoder_in,
	output reg [3:0] decoder_out
	);
	
	always@(*) begin
		case(decoder_in)
         		4'b00 : decoder_out <= 4'b0001;    
       		 	4'b01 : decoder_out <= 4'b0010;    
         		4'b10 : decoder_out <= 4'b0100;    
         		4'b11 : decoder_out <= 4'b1000;    
      		endcase
   	end
		
endmodule

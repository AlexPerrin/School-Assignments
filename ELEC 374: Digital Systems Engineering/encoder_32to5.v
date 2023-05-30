module encoder_32to5(
	input wire R0out,
   input wire R1out,
   input wire R2out,
   input wire R3out,
   input wire R4out,
   input wire R5out,
   input wire R6out,
   input wire R7out,
   input wire R8out,
   input wire R9out,
   input wire R10out,
   input wire R11out,
   input wire R12out,
   input wire R13out,
   input wire R14out,
   input wire R15out,
   input wire HIout,
   input wire LOout,
   input wire Zhighout,
   input wire Zlowout,
   input wire PCout,
   input wire MDRout,
   input wire InPortout,
   input wire Cout,
	output reg [4:0] encoder_out
	);
	
	always@(*) begin
         if(R0out) begin encoder_out <= 5'd0; end
         if(R1out) begin encoder_out <= 5'd1; end
			if(R2out) begin encoder_out <= 5'd2; end
         if(R3out) begin encoder_out <= 5'd3; end
         if(R4out) begin encoder_out <= 5'd4; end
         if(R5out) begin encoder_out <= 5'd5; end
         if(R6out) begin encoder_out <= 5'd6; end
         if(R7out) begin encoder_out <= 5'd7; end
         if(R8out) begin encoder_out <= 5'd8; end
         if(R9out) begin encoder_out <= 5'd9; end
         if(R10out) begin encoder_out <= 5'd10; end
         if(R11out) begin encoder_out <= 5'd11; end
         if(R12out) begin encoder_out <= 5'd12; end
         if(R13out) begin encoder_out <= 5'd13; end
         if(R14out) begin encoder_out <= 5'd14; end
         if(R15out) begin encoder_out <= 5'd15; end
			if(HIout) begin encoder_out <= 5'd16; end
         if(LOout) begin encoder_out <= 5'd17; end
         if(Zhighout) begin encoder_out <= 5'd18; end      
			if(Zlowout) begin encoder_out <= 5'd19; end
         if(PCout) begin encoder_out <= 5'd20; end
         if(MDRout) begin encoder_out <= 5'd21; end      
			if(InPortout) begin encoder_out <= 5'd22; end
         if(Cout) begin encoder_out <= 5'd23; end
   end
	
endmodule

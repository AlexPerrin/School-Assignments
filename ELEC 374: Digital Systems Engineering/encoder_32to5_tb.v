`timescale 1ns/10ps
module encoder_32to5_tb;
	reg R0out;
   reg R1out;
   reg R2out;
   reg R3out;
   reg R4out;
   reg R5out;
   reg R6out;
   reg R7out;
   reg R8out;
   reg R9out;
   reg R10out;
   reg R11out;
   reg R12out;
   reg R13out;
   reg R14out;
   reg R15out;
   reg HIout;
   reg LOout;
   reg Zhighout;
   reg Zlowout;
   reg PCout;
   reg MDRout;
   reg InPortout;
   reg Cout;
	wire [4:0] encoder_out;
	
   encoder_32to5 encoder(
      R0out,
      R1out,
      R2out,
      R3out,
      R4out,
      R5out,
      R6out,
      R7out,
      R8out,
      R9out,
      R10out,
      R11out,
      R12out,
      R13out,
      R14out,
      R15out,
      HIout,
      LOout,
      Zhighout,
      Zlowout,
      PCout,
      MDRout,
      InPortout,
      Cout,
      encoder_out
      );
	
	initial begin
		#10 R0out <= 1; #10 R0out <= 0;  
      #10 R1out <= 1; #10 R1out <= 0;  
      #10 R2out <= 1; #10 R2out <= 0;  
      #10 R3out <= 1; #10 R3out <= 0;  
      #10 R4out <= 1; #10 R4out <= 0;  
      #10 R5out <= 1; #10 R5out <= 0;  
      #10 R6out <= 1; #10 R6out <= 0;  
      #10 R7out <= 1; #10 R7out <= 0;  
      #10 R8out <= 1; #10 R8out <= 0;  
      #10 R9out <= 1; #10 R9out <= 0;  
      #10 R10out <= 1; #10 R10out <= 0;  
      #10 R11out <= 1; #10 R11out <= 0;  
      #10 R12out <= 1; #10 R12out <= 0;  
      #10 R13out <= 1; #10 R13out <= 0;  
      #10 R14out <= 1; #10 R14out <= 0;  
      #10 R15out <= 1; #10 R15out <= 0;  
      #10 HIout <= 1; #10 HIout <= 0;  
      #10 LOout <= 1; #10 LOout <= 0;  
      #10 Zhighout <= 1; #10 Zhighout <= 0;  
      #10 Zlowout <= 1; #10 Zlowout <= 0;  
      #10 PCout <= 1; #10 PCout <= 0;  
      #10 MDRout <= 1; #10 MDRout <= 0;  
      #10 InPortout <= 1; #10 InPortout <= 0;  
      #10 Cout <= 1; #10 Cout <= 0;  
		#100;
	end
endmodule

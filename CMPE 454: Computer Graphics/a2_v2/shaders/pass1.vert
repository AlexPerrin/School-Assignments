// Pass 1 vertex shader
//
// Stores colour, normal, depth

#version 300 es

uniform mat4 M;
uniform mat4 MV;
uniform mat4 MVP;

layout (location = 0) in mediump vec3 vertPosition;
layout (location = 1) in mediump vec3 vertNormal;
layout (location = 2) in mediump vec3 vertTexCoord;

// Your shader should compute the colour, normal (in the VCS), and
// depth (in the range [0,1] with 0=near and 1=far) and store these
// values in the corresponding variables.

out mediump vec3 colour;
out mediump vec3 normal;
out mediump float depth;

void main()

{
  // calc vertex position in CCS (always required)

  gl_Position = MVP * vec4( vertPosition, 1.0 );

  // Provide a colour 

  // YOUR CODE HERE

  colour = vec3(0.66, 0.84, 0.36);

  // calculate normal in VCS

  // YOUR CODE HERE

  normal = (MV * vec4(vertNormal, 0.0)).xyz;

  // Calculate the depth in [0,1]

  // YOUR CODE HERE

  depth = (gl_Position.z / gl_Position.w + 1.0) / 2.0;
}

#version 110

uniform mat4 matrix_projection;
uniform mat4 matrix_modelview;

attribute vec3 position;
attribute vec3 normal;
attribute vec2 uv;

varying vec3 vertex_position;
varying vec3 vertex_normal;
varying vec2 vertex_uv;

void
main (void)
{
  vec4 p          = matrix_projection * matrix_modelview * vec4(position, 1.0);
  vec4 n          = matrix_modelview * vec4(normal, 0.0);
  gl_Position     = p;
  vertex_position = p.xyz;
  vertex_normal   = n.xyz;
  vertex_uv       = uv;
}
